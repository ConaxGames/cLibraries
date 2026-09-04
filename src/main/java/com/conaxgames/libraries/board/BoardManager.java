package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

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
        var plugin = LibraryPlugin.getInstance().getPlugin();
        var server = plugin.getServer();
        var logger = plugin.getLogger();

        boards.entrySet().removeIf(entry -> {
            var player = server.getPlayer(entry.getKey());
            if (player == null) {
                return true;
            }
            try {
                updateBoard(player, entry.getValue());
            } catch (Exception ex) {
                logger.severe("Scoreboard error for " + player.getName() + ": " + ex.getMessage());
            }
            return false;
        });
    }

    private void updateBoard(Player player, Board board) {
        var lines = this.lines.apply(player);
        if (lines.size() > Board.MAX_LINES) {
            lines = lines.subList(0, Board.MAX_LINES);
        }
        board.updateTitle(title.apply(player));

        var entries = board.entries;
        while (entries.size() > lines.size()) {
            entries.removeLast().remove();
        }

        int i = 0;
        for (var line : lines.reversed()) {
            BoardEntry entry;
            if (i < entries.size()) {
                entry = entries.get(i);
                entry.text = line;
            } else {
                entry = new BoardEntry(board, i, line);
                entries.add(entry);
            }
            entry.send(i + 1);
            i++;
        }

        if (!player.getScoreboard().equals(board.scoreboard)) {
            player.setScoreboard(board.scoreboard);
        }
    }

    public void createBoard(Player player) {
        if (player.hasMetadata(SKIP_BOARD_METADATA) || boards.containsKey(player.getUniqueId())) {
            return;
        }
        boards.put(player.getUniqueId(), new Board(player));
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
