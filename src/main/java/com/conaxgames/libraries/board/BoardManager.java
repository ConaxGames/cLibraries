package com.conaxgames.libraries.board;

import com.conaxgames.libraries.message.CC;
import com.conaxgames.libraries.util.VersioningChecker;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public final class BoardManager implements Runnable {

    public static final String SKIP_BOARD_METADATA = "cElement";

    private static final boolean MODERN = VersioningChecker.supports("1.20.4");
    private static final boolean TEXT_SHADOW = VersioningChecker.supports("1.21.4");
    private static final int SEGMENT_MAX = VersioningChecker.supports("1.13") ? 64 : 16;
    private static final int TITLE_MAX = VersioningChecker.supports("1.13") ? 128 : 32;
    // Legacy entries have to render as nothing; a unique colour pair per line is the height cap.
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
        if (!MODERN && title.length() > TITLE_MAX) {
            title = title.substring(0, TITLE_MAX);
        }
        if (!title.equals(board.title)) {
            board.title = title;
            if (MODERN) {
                Modern.title(board.objective, title);
            } else {
                board.objective.setDisplayName(title);
            }
        }

        while (board.size > count) {
            board.scoreboard.resetScores(KEYS[--board.size]);
        }

        // Highest score sits at the top, so the list is written from the bottom up.
        for (int i = 0; i < count; i++) {
            if (i == board.size) {
                if (!MODERN && board.teams[i] == null) {
                    String name = "board_" + i;
                    Team team = board.scoreboard.getTeam(name);
                    if (team == null) {
                        team = board.scoreboard.registerNewTeam(name);
                    }
                    team.addEntry(KEYS[i]);
                    board.teams[i] = team;
                }
                board.objective.getScore(KEYS[i]).setScore(i + 1);
                board.size++;
            }

            String line = lines.get(count - 1 - i);
            if (line.equals(board.texts[i])) {
                continue;
            }
            board.texts[i] = line;

            String text = CC.translate(line);
            if (MODERN) {
                Modern.line(board.objective, KEYS[i], text);
                continue;
            }

            String prefix = text;
            String suffix = "";
            if (text.length() > SEGMENT_MAX) {
                int cut = text.charAt(SEGMENT_MAX - 1) == ChatColor.COLOR_CHAR ? SEGMENT_MAX - 1 : SEGMENT_MAX;
                prefix = text.substring(0, cut);
                suffix = ChatColor.getLastColors(prefix) + text.substring(cut);
                if (suffix.length() > SEGMENT_MAX) {
                    suffix = suffix.substring(0, SEGMENT_MAX);
                }
            }
            board.teams[i].setPrefix(prefix);
            board.teams[i].setSuffix(suffix);
        }

        if (!player.getScoreboard().equals(board.scoreboard)) {
            player.setScoreboard(board.scoreboard);
        }
    }

    public void createBoard(Player player) {
        if (player.hasMetadata(SKIP_BOARD_METADATA) || boards.containsKey(player.getUniqueId())) {
            return;
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard.equals(manager.getMainScoreboard())) {
            scoreboard = manager.getNewScoreboard();
        }

        Board board = new Board(scoreboard);
        boards.put(player.getUniqueId(), board);
        update(player, board);
    }

    public void removeBoard(Player player) {
        Board board = boards.remove(player.getUniqueId());
        if (board == null) {
            return;
        }
        // Sidebar only. setScoreboard(main) would flash leftover nametag teams from other plugins.
        Objective objective = board.scoreboard.getObjective("sb");
        if (objective != null) {
            objective.unregister();
        }
    }

    private static final class Board {
        final Team[] teams = new Team[KEYS.length];
        final String[] texts = new String[KEYS.length];
        final Scoreboard scoreboard;
        final Objective objective;
        String title;
        int size;

        Board(Scoreboard scoreboard) {
            this.scoreboard = scoreboard;
            Objective existing = scoreboard.getObjective("sb");
            if (existing != null) {
                existing.unregister();
            }
            objective = MODERN
                    ? Modern.objective(scoreboard)
                    : scoreboard.registerNewObjective("sb", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
    }

    // Criteria, NumberFormat, Adventure and Score#customName must not be mentioned on BoardManager
    // or the verifier loads them on servers below the 1.20.4 gate.
    private static final class Modern {

        static Objective objective(Scoreboard scoreboard) {
            Objective objective = scoreboard.registerNewObjective("sb", Criteria.DUMMY, Component.empty());
            objective.numberFormat(NumberFormat.blank());
            return objective;
        }

        static void title(Objective objective, String legacy) {
            objective.displayName(component(legacy));
        }

        static void line(Objective objective, String key, String legacy) {
            objective.getScore(key).customName(component(legacy));
        }

        private static Component component(String legacy) {
            Component name = CC.legacy().deserialize(legacy);
            return TEXT_SHADOW ? name.shadowColor(ShadowColor.shadowColor(0xFF000000)) : name;
        }
    }
}
