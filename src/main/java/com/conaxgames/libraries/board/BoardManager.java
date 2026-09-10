package com.conaxgames.libraries.board;

import com.conaxgames.libraries.message.CC;
import com.cryptomorin.xseries.reflection.XReflection;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public final class BoardManager implements Runnable {

    public static final String SKIP_BOARD_METADATA = "cElement";

    // Criteria, NumberFormat and Adventure only exist above their gate, so their uses live in Modern below.
    private static final boolean MODERN = XReflection.supports(1, 20, 4);
    private static final boolean TEXT_SHADOW = XReflection.supports(1, 21, 4);
    private static final int SEGMENT_MAX = XReflection.supports(1, 13) ? 64 : 16;
    private static final int TITLE_MAX = XReflection.supports(1, 13) ? 128 : 32;
    // A legacy entry has to render as nothing, so a unique colour pair per line is what caps the height.
    private static final String CODES = "0123456789abcdefklmor";
    private static final String[] KEYS = new String[CODES.length()];

    static {
        for (int i = 0; i < KEYS.length; i++) {
            KEYS[i] = MODERN
                    ? Integer.toString(i)
                    : String.valueOf(ChatColor.COLOR_CHAR) + CODES.charAt(i) + ChatColor.COLOR_CHAR + 'f';
        }
    }

    private final Map<UUID, Board> boards = new HashMap<>();
    private final Function<Player, String> title;
    private final Function<Player, List<String>> lines;

    public BoardManager(Function<Player, String> title, Function<Player, List<String>> lines) {
        this.title = title;
        this.lines = lines;
    }

    @Override
    public void run() {
        boards.entrySet().removeIf(entry -> {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                return true;
            }
            update(player, entry.getValue());
            return false;
        });
    }

    private void update(Player player, Board board) {
        List<String> lines = this.lines.apply(player);
        int count = Math.min(lines.size(), KEYS.length);

        String title = CC.translate(this.title.apply(player));
        title = title.substring(0, Math.min(title.length(), TITLE_MAX));
        if (!title.equals(board.title)) {
            board.title = title;
            if (MODERN) {
                Modern.title(board.objective, title);
            } else {
                board.objective.setDisplayName(title);
            }
        }

        // A dropped line keeps its team, which is picked back up below if the board grows again.
        while (board.entries.size() > count) {
            board.entries.remove(board.entries.size() - 1);
            board.scoreboard.resetScores(KEYS[board.entries.size()]);
        }

        // The sidebar puts the highest score on top, so the board is filled from the last line up.
        for (int i = 0; i < count; i++) {
            if (i == board.entries.size()) {
                board.entries.add(new Entry());
                // The score is the line's slot, which holds for as long as the entry does.
                board.objective.getScore(KEYS[i]).setScore(i + 1);
            }

            Entry entry = board.entries.get(i);
            if (!MODERN && entry.team == null) {
                Team team = board.scoreboard.getTeam("board_" + i);
                entry.team = team != null ? team : board.scoreboard.registerNewTeam("board_" + i);
                entry.team.addEntry(KEYS[i]);
            }

            String line = lines.get(count - 1 - i);
            if (line.equals(entry.text)) {
                continue;
            }
            entry.text = line;

            String text = CC.translate(line);
            if (MODERN) {
                Modern.line(board.objective.getScore(KEYS[i]), text);
            } else {
                String prefix = text;
                String suffix = "";
                if (text.length() > SEGMENT_MAX) {
                    // Cut before the colour code rather than through it.
                    int cut = text.charAt(SEGMENT_MAX - 1) == ChatColor.COLOR_CHAR ? SEGMENT_MAX - 1 : SEGMENT_MAX;
                    prefix = text.substring(0, cut);
                    suffix = CC.getLastColors(prefix) + text.substring(cut);
                    suffix = suffix.substring(0, Math.min(suffix.length(), SEGMENT_MAX));
                }
                if (!prefix.equals(entry.team.getPrefix())) {
                    entry.team.setPrefix(prefix);
                }
                if (!suffix.equals(entry.team.getSuffix())) {
                    entry.team.setSuffix(suffix);
                }
            }
        }

        if (!player.getScoreboard().equals(board.scoreboard)) {
            player.setScoreboard(board.scoreboard);
        }
    }

    public void createBoard(Player player) {
        if (player.hasMetadata(SKIP_BOARD_METADATA) || boards.containsKey(player.getUniqueId())) {
            return;
        }

        Board board = new Board();
        // Keep whatever another plugin already put on the player, only the main scoreboard is shared.
        board.scoreboard = player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())
                ? Bukkit.getScoreboardManager().getNewScoreboard()
                : player.getScoreboard();

        Objective existing = board.scoreboard.getObjective("sb");
        if (existing != null) {
            existing.unregister();
        }
        board.objective = MODERN
                ? Modern.objective(board.scoreboard)
                : board.scoreboard.registerNewObjective("sb", "dummy");
        board.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        boards.put(player.getUniqueId(), board);
        // The board is empty and unassigned until it is filled, so do it here instead of waiting for the next update.
        update(player, board);
    }

    public void removeBoard(Player player) {
        if (boards.remove(player.getUniqueId()) != null && player.isOnline()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    private static final class Board {
        final List<Entry> entries = new ArrayList<>();
        Scoreboard scoreboard;
        Objective objective;
        String title;
    }

    private static final class Entry {
        Team team;
        String text;
    }

    // Kept apart so the verifier never has to resolve Adventure or Criteria on servers below the gate.
    private static final class Modern {

        static Objective objective(Scoreboard scoreboard) {
            Objective objective = scoreboard.registerNewObjective("sb", Criteria.DUMMY, Component.empty());
            objective.numberFormat(NumberFormat.blank());
            return objective;
        }

        static void title(Objective objective, String legacy) {
            objective.displayName(component(legacy));
        }

        static void line(Score score, String legacy) {
            score.customName(component(legacy));
        }

        private static Component component(String legacy) {
            Component name = CC.legacy().deserialize(legacy);
            return TEXT_SHADOW ? name.shadowColor(ShadowColor.shadowColor(0xFF000000)) : name;
        }
    }
}
