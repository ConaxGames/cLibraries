package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Runs the sidebar scoreboard update loop for all players with a board. Uses a {@link BoardAdapter}
 * for title and lines; entries are synced via {@link Board} and {@link BoardEntry}. Offline players
 * are removed from the map and their entries cleared.
 * <p>
 * <b>Usage:</b> Construct with your {@link BoardAdapter}, then call
 * {@link com.conaxgames.libraries.LibraryPlugin#setBoardManager}. The library schedules
 * {@link #run()} at the adapter's {@link BoardAdapter#getInterval()}. Call {@link #createBoard} when
 * a player should see the board (e.g. on join) and {@link #removeBoard} when they should not.
 */
public class BoardManager implements Runnable {

    private static final String C_ELEMENT_METADATA_KEY = "cElement";
    private final Map<UUID, Board> playerBoards = new HashMap<>();
    @Getter
    private final BoardAdapter adapter;

    /**
     * Creates a manager that will use the given adapter for all title and line content.
     */
    public BoardManager(BoardAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run() {
        adapter.preLoop();
        Iterator<Map.Entry<UUID, Board>> it = playerBoards.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Board> e = it.next();
            Board board = e.getValue();
            Player player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(e.getKey());
            if (player == null || !player.isOnline()) {
                it.remove();
                if (!board.getEntries().isEmpty()) {
                    board.getEntries().forEach(BoardEntry::remove);
                    board.getEntries().clear();
                }
                continue;
            }
            try {
                updateBoard(player, board);
            } catch (Exception ex) {
                LibraryPlugin.getInstance().getPlugin().getLogger()
                    .severe("Scoreboard error for " + player.getName() + ": " + ex.getMessage());
            }
        }
    }

    private void updateBoard(Player player, Board board) {
        List<String> lines = adapter.getScoreboard(player, board);
        if (lines == null || lines.isEmpty()) {
            if (!board.getEntries().isEmpty()) board.clearAllEntries();
            return;
        }
        Collections.reverse(lines);
        String newTitle = adapter.getTitle(player);
        if (newTitle != null) {
            String current = LegacyComponentSerializer.legacySection().serialize(board.getObjective().displayName());
            if (!current.equals(newTitle)) {
                board.getObjective().displayName(LegacyComponentSerializer.legacySection().deserialize(newTitle));
            }
        }
        syncEntries(board, lines);
        Scoreboard sb = board.getScoreboard();
        if (!player.getScoreboard().equals(sb)) {
            player.setScoreboard(sb);
            adapter.onScoreboardCreate(player, sb);
        }
    }

    private void syncEntries(Board board, List<String> lines) {
        List<BoardEntry> entries = board.getEntries();
        int n = lines.size();
        while (entries.size() > n) {
            int last = entries.size() - 1;
            entries.get(last).remove();
            entries.remove(last);
        }
        for (int i = 0; i < n; i++) {
            String line = lines.get(i);
            int pos = i + 1;
            BoardEntry entry;
            if (i < entries.size()) {
                entry = entries.get(i);
                if (!entry.getText().equals(line)) entry.setText(line).setup();
            } else {
                entry = new BoardEntry(board, line);
            }
            entry.send(pos);
        }
    }

    /**
     * Creates a sidebar board for the player if they do not have one and do not have the
     * cElement metadata. Use when the player should start seeing the scoreboard (e.g. on join).
     */
    public void createBoard(Player player) {
        if (player.hasMetadata(C_ELEMENT_METADATA_KEY) || playerBoards.containsKey(player.getUniqueId())) return;
        playerBoards.put(player.getUniqueId(), new Board(player, adapter));
    }

    /**
     * Removes the player's board and clears all entries. Does nothing if the player has
     * cElement metadata. Use when the player should no longer see the scoreboard (e.g. on quit).
     */
    public void removeBoard(Player player) {
        if (player.hasMetadata(C_ELEMENT_METADATA_KEY)) return;
        Board board = playerBoards.remove(player.getUniqueId());
        if (board != null && !board.getEntries().isEmpty()) {
            board.getEntries().forEach(BoardEntry::remove);
            board.getEntries().clear();
        }
    }
}
