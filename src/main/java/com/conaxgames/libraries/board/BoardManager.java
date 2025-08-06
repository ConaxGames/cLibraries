package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.util.CC;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

/**
 * Manages scoreboard boards for all players.
 * This class handles the creation, updating, and cleanup of player scoreboards.
 * 
 * @author ConaxGames
 * @since 1.0
 */
public class BoardManager implements Runnable {

    // Constants
    private static final String C_ELEMENT_METADATA_KEY = "cElement";
    private static final String DEFAULT_OBJECTIVE_NAME = "Default";
    private static final String DEFAULT_OBJECTIVE_CRITERIA = "dummy";

    // Instance fields
    private final Map<UUID, Board> playerBoards = new HashMap<>();
    private final BoardAdapter adapter;

    /**
     * Creates a new board manager with the specified adapter.
     * 
     * @param adapter The adapter that provides board content
     */
    public BoardManager(BoardAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void run() {
        this.adapter.preLoop();
        
        // Clean up any boards for players with cElement metadata (zero CPU cost)
        cleanupAllCElementBoards();
        
        // Only process players who actually have boards - major performance boost
        for (Map.Entry<UUID, Board> entry : this.playerBoards.entrySet()) {
            UUID playerUUID = entry.getKey();
            Board board = entry.getValue();
            
            Player player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(playerUUID);
            if (player == null || !player.isOnline()) {
                continue;
            }
            
            // Skip players with cElement metadata - they shouldn't have boards
            if (player.hasMetadata(C_ELEMENT_METADATA_KEY)) {
                continue;
            }
            
            try {
                updatePlayerBoard(player, board);
            } catch (Exception e) {
                e.printStackTrace();
                LibraryPlugin.getInstance().getPlugin().getLogger()
                        .severe("Something went wrong while updating " + player.getName() + "'s scoreboard " + board + " - " + board.getAdapter() + ")");
            }
        }
    }

    /**
     * Updates the scoreboard for a specific player.
     * 
     * @param player The player to update the board for
     * @param board The board instance for this player
     */
    private void updatePlayerBoard(Player player, Board board) {
        Scoreboard scoreboard = board.getScoreboard();
        Objective objective = board.getObjective();

        List<String> scores = this.adapter.getScoreboard(player, board);

        if (scores == null || scores.isEmpty()) {
            // Clear all entries if no scores
            if (!board.getEntries().isEmpty()) {
                board.clearAllEntries();
            }
            return;
        }
        
        // Reverse scores for proper positioning
        Collections.reverse(scores);
        
        // Update title if changed
        String newTitle = this.adapter.getTitle(player);
        if (!objective.getDisplayName().equals(newTitle)) {
            objective.setDisplayName(newTitle);
        }

        // Efficiently update entries
        updateBoardEntries(board, scores, objective);

        this.adapter.onScoreboardCreate(player, scoreboard);
        player.setScoreboard(scoreboard);
    }
    
    /**
     * Updates the board entries efficiently by reusing existing entries when possible.
     * 
     * @param board The board to update
     * @param scores The new scores to display
     * @param objective The objective to update
     */
    private void updateBoardEntries(Board board, List<String> scores, Objective objective) {
        List<BoardEntry> entries = board.getEntries();
        int scoresSize = scores.size();
        
        // Remove excess entries efficiently
        if (entries.size() > scoresSize) {
            for (int i = entries.size() - 1; i >= scoresSize; i--) {
                BoardEntry entry = entries.get(i);
                entry.remove();
                entries.remove(i);
            }
        }
        
        // Update or create entries
        for (int i = 0; i < scoresSize; i++) {
            String text = scores.get(i);
            int position = i + 1;
            
            BoardEntry entry;
            if (i < entries.size()) {
                // Update existing entry
                entry = entries.get(i);
                if (!entry.getText().equals(text)) {
                    entry.setText(text).setup();
                }
            } else {
                // Create new entry
                entry = new BoardEntry(board, text);
            }
            
            // Update position
            entry.send(position);
        }
    }

    /**
     * Clean up board for a specific player if they have cElement metadata.
     * 
     * @param player The player to check and clean up
     */
    public void cleanupPlayerBoardIfCElement(Player player) {
        if (player.hasMetadata(C_ELEMENT_METADATA_KEY)) {
            Board board = this.playerBoards.get(player.getUniqueId());
            if (board != null) {
                cleanupBoard(board);
                this.playerBoards.remove(player.getUniqueId());
            }
        }
    }
    
    /**
     * Clean up boards for all online players with cElement metadata.
     * This can be called periodically to ensure zero CPU cost.
     */
    public void cleanupAllCElementBoards() {
        // Use iterator to safely remove during iteration
        Iterator<Map.Entry<UUID, Board>> iterator = this.playerBoards.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Board> entry = iterator.next();
            UUID playerUUID = entry.getKey();
            Board board = entry.getValue();
            
            Player player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(playerUUID);
            if (player != null && player.isOnline() && player.hasMetadata(C_ELEMENT_METADATA_KEY)) {
                cleanupBoard(board);
                iterator.remove();
            }
        }
    }

    /**
     * Cleans up a board by removing all entries and resetting the board state.
     * 
     * @param board The board to clean up
     */
    private void cleanupBoard(Board board) {
        if (!board.getEntries().isEmpty()) {
            board.getEntries().forEach(BoardEntry::remove);
            board.getEntries().clear();
        }
    }

    // Getter methods
    public Map<UUID, Board> getPlayerBoards() {
        return this.playerBoards;
    }

    public BoardAdapter getAdapter() {
        return this.adapter;
    }
}
