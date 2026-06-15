package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public final class BoardManager implements Runnable {

    public static final String SKIP_BOARD_METADATA = "cElement";

    private final Map<UUID, Board> boards = new HashMap<>();

    private final Function<Player, String> title;
    private final Function<Player, List<String>> lines;
    private final long interval;
    private final String skipMetadata;

    private BoardManager(Builder builder) {
        this.title = builder.title;
        this.lines = builder.lines;
        this.interval = builder.interval;
        this.skipMetadata = builder.skipMetadata;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getInterval() {
        return interval;
    }

    @Override
    public void run() {
        var server = LibraryPlugin.getInstance().getPlugin().getServer();
        var logger = LibraryPlugin.getInstance().getPlugin().getLogger();

        boards.entrySet().removeIf(entry -> {
            var player = server.getPlayer(entry.getKey());
            if (player == null || !player.isOnline()) {
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
        var lines = Objects.requireNonNullElse(this.lines.apply(player), List.<String>of());
        board.updateTitle(title.apply(player));

        var entries = board.entries();
        while (entries.size() > lines.size()) {
            entries.removeLast().remove();
        }

        int i = 0;
        for (var raw : lines.reversed()) {
            var line = Objects.requireNonNullElse(raw, "");
            BoardEntry entry;
            if (i < entries.size()) {
                entry = entries.get(i);
                entry.text(line);
            } else {
                entry = new BoardEntry(board, i, line);
                entries.add(entry);
            }
            entry.send(i + 1);
            i++;
        }

        var sb = board.scoreboard();
        if (!player.getScoreboard().equals(sb)) {
            player.setScoreboard(sb);
        }
    }

    public void createBoard(Player player) {
        if (player.hasMetadata(skipMetadata) || boards.containsKey(player.getUniqueId())) {
            return;
        }
        boards.put(player.getUniqueId(), new Board(player));
    }

    public void removeBoard(Player player) {
        if (player.hasMetadata(skipMetadata)) {
            return;
        }
        if (boards.remove(player.getUniqueId()) != null && player.isOnline()) {
            player.setScoreboard(player.getServer().getScoreboardManager().getMainScoreboard());
        }
    }

    public static final class Builder {

        private Function<Player, String> title = player -> "";
        private Function<Player, List<String>> lines = player -> List.of();
        private long interval = 20L;
        private String skipMetadata = SKIP_BOARD_METADATA;

        private Builder() {
        }

        public Builder title(Function<Player, String> title) {
            this.title = Objects.requireNonNull(title, "title");
            return this;
        }

        public Builder lines(Function<Player, List<String>> lines) {
            this.lines = Objects.requireNonNull(lines, "lines");
            return this;
        }

        public Builder interval(long interval) {
            this.interval = interval;
            return this;
        }

        public Builder skipMetadata(String skipMetadata) {
            this.skipMetadata = Objects.requireNonNull(skipMetadata, "skipMetadata");
            return this;
        }

        public BoardManager build() {
            return new BoardManager(this);
        }
    }
}
