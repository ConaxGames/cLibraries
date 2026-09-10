package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.message.CC;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.ShadowColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@SuppressWarnings("deprecation")
public final class BoardManager implements Runnable {

    public static final String SKIP_BOARD_METADATA = "cElement";

    private final Map<UUID, Board> boards = new HashMap<>();
    private final Function<Player, String> title;
    private final Function<Player, List<String>> lines;

    private BoardManager(Builder builder) {
        this.title = builder.title;
        this.lines = builder.lines;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void run() {
        boards.entrySet().removeIf(entry -> {
            var player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(entry.getKey());
            if (player == null) {
                return true;
            }
            update(player, entry.getValue());
            return false;
        });
    }

    private void update(Player player, Board board) {
        try {
            var lines = this.lines.apply(player);
            if (lines.size() > Board.MAX_LINES) {
                lines = lines.subList(0, Board.MAX_LINES);
            }

            var translatedTitle = CC.translate(title.apply(player));
            if (translatedTitle.length() > Board.TITLE_MAX) {
                translatedTitle = translatedTitle.substring(0, Board.TITLE_MAX);
            }
            if (!translatedTitle.equals(board.lastTitle)) {
                board.lastTitle = translatedTitle;
                if (Board.MODERN) {
                    var component = CC.LEGACY.deserialize(translatedTitle);
                    board.objective.displayName(Board.TEXT_SHADOW
                            ? component.shadowColor(ShadowColor.shadowColor(0xFF000000))
                            : component);
                } else {
                    board.objective.setDisplayName(translatedTitle);
                }
            }

            var entries = board.entries;
            while (entries.size() > lines.size()) {
                var removed = entries.removeLast();
                board.scoreboard.resetScores(removed.key);
                if (removed.team != null) {
                    removed.team.unregister();
                }
            }

            int i = 0;
            for (var line : lines.reversed()) {
                BoardEntry boardEntry;
                if (i < entries.size()) {
                    boardEntry = entries.get(i);
                } else {
                    boardEntry = new BoardEntry();
                    boardEntry.key = Board.MODERN ? Integer.toString(i) : Board.ENTRY_KEYS[i];
                    if (!Board.MODERN) {
                        boardEntry.team = board.scoreboard.registerNewTeam("board_" + i);
                        boardEntry.team.addEntry(boardEntry.key);
                    }
                    entries.add(boardEntry);
                }

                var score = board.objective.getScore(boardEntry.key);
                if (score.getScore() != i + 1) {
                    score.setScore(i + 1);
                }
                if (!line.equals(boardEntry.lastSent)) {
                    boardEntry.lastSent = line;
                    var translated = CC.translate(line);
                    if (Board.MODERN) {
                        var component = CC.LEGACY.deserialize(translated);
                        score.customName(Board.TEXT_SHADOW
                                ? component.shadowColor(ShadowColor.shadowColor(0xFF000000))
                                : component);
                    } else {
                        int max = Board.SEGMENT_MAX;
                        String prefix;
                        String suffix;
                        if (translated.length() <= max) {
                            prefix = translated;
                            suffix = "";
                        } else {
                            prefix = translated.substring(0, max);
                            int lastColor = prefix.lastIndexOf('\u00a7');
                            if (lastColor >= max - 2) {
                                suffix = CC.getLastColors(translated.substring(0, Math.min(translated.length(), max + 1)))
                                        + translated.substring(lastColor + 2);
                                prefix = prefix.substring(0, lastColor);
                            } else {
                                suffix = CC.getLastColors(prefix) + translated.substring(max);
                            }
                            if (suffix.length() > max) {
                                suffix = suffix.substring(0, max);
                            }
                        }
                        if (!prefix.equals(boardEntry.team.getPrefix())) {
                            boardEntry.team.setPrefix(prefix);
                        }
                        if (!suffix.equals(boardEntry.team.getSuffix())) {
                            boardEntry.team.setSuffix(suffix);
                        }
                    }
                }
                i++;
            }

            if (!player.getScoreboard().equals(board.scoreboard)) {
                player.setScoreboard(board.scoreboard);
            }
        } catch (Exception ex) {
            LibraryPlugin.getInstance().getPlugin().getLogger()
                    .severe("Scoreboard error for " + player.getName() + ": " + ex.getMessage());
        }
    }

    public void createBoard(Player player) {
        if (player.hasMetadata(SKIP_BOARD_METADATA) || boards.containsKey(player.getUniqueId())) {
            return;
        }

        var board = new Board();
        var scoreboardManager = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager();
        board.scoreboard = player.getScoreboard().equals(scoreboardManager.getMainScoreboard())
                ? scoreboardManager.getNewScoreboard()
                : player.getScoreboard();
        var existing = board.scoreboard.getObjective("sb");
        if (existing != null) {
            existing.unregister();
        }
        if (Board.MODERN) {
            board.objective = board.scoreboard.registerNewObjective("sb", Criteria.DUMMY, Component.empty());
            board.objective.numberFormat(NumberFormat.blank());
        } else {
            board.objective = board.scoreboard.registerNewObjective("sb", "dummy");
        }
        board.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        boards.put(player.getUniqueId(), board);
        // The board is empty and unassigned until it is filled, so do it here instead of waiting for the next update.
        update(player, board);
    }

    public void removeBoard(Player player) {
        if (boards.remove(player.getUniqueId()) != null && player.isOnline()) {
            player.setScoreboard(player.getServer().getScoreboardManager().getMainScoreboard());
        }
    }

    public static final class Builder {

        private Function<Player, String> title = player -> "";
        private Function<Player, List<String>> lines = player -> List.of();

        private Builder() {
        }

        public Builder title(Function<Player, String> title) {
            this.title = title;
            return this;
        }

        public Builder lines(Function<Player, List<String>> lines) {
            this.lines = lines;
            return this;
        }

        public BoardManager build() {
            return new BoardManager(this);
        }
    }
}
