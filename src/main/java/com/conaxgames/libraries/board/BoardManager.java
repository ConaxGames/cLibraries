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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
    private static final int ASYNC_THREAD_POOL_SIZE = 2; // Small pool for board data preparation
    private static final long CACHE_EXPIRY_MS = 100L; // Cache board data for 100ms to reduce redundant calls

    // Thread-safe data structures for async/sync communication
    private final Map<UUID, Board> playerBoards = new ConcurrentHashMap<>();
    private final BoardAdapter adapter;
    private final ExecutorService asyncExecutor;
    
    // Cache structures for performance optimization
    private final Map<UUID, BoardUpdateData> pendingUpdates = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastUpdateTimes = new ConcurrentHashMap<>();
    
    // Queue for batched sync operations
    private final ConcurrentLinkedQueue<Runnable> syncOperations = new ConcurrentLinkedQueue<>();
    
    // Performance monitoring (optional)
    private long totalAsyncTime = 0;
    private long totalSyncTime = 0;
    private int updateCount = 0;
    private volatile boolean shutdown = false;

    /**
     * Creates a new board manager with the specified adapter.
     * 
     * @param adapter The adapter that provides board content
     */
    public BoardManager(BoardAdapter adapter) {
        this.adapter = adapter;
        this.asyncExecutor = Executors.newFixedThreadPool(ASYNC_THREAD_POOL_SIZE, r -> {
            Thread thread = new Thread(r, "BoardManager-Async-" + System.currentTimeMillis());
            thread.setDaemon(true);
            return thread;
        });
    }
    
    /**
     * Shuts down the async executor and cleans up resources.
     * Should be called when the plugin is disabled.
     */
    public void shutdown() {
        shutdown = true;
        asyncExecutor.shutdown();
        try {
            if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                asyncExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            asyncExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Internal class to hold board update data prepared asynchronously.
     */
    private static class BoardUpdateData {
        final List<String> scores;
        final String title;
        final long timestamp;
        final UUID playerUUID;
        
        BoardUpdateData(UUID playerUUID, List<String> scores, String title) {
            this.playerUUID = playerUUID;
            this.scores = scores;
            this.title = title;
            this.timestamp = System.currentTimeMillis();
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
    }

    @Override
    public void run() {
        if (shutdown) return;
        
        long startTime = System.nanoTime();
        
        this.adapter.preLoop();
        
        // Clean up any boards for players with cElement metadata (zero CPU cost)
        cleanupAllCElementBoards();
        
        // Step 1: Prepare board data asynchronously for all players
        List<CompletableFuture<Void>> asyncTasks = new ArrayList<>();
        
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
            
            // Check if we have recent cached data to avoid redundant async calls
            BoardUpdateData cached = pendingUpdates.get(playerUUID);
            Long lastUpdate = lastUpdateTimes.get(playerUUID);
            long now = System.currentTimeMillis();
            
            if (cached != null && !cached.isExpired()) {
                // Use cached data, skip async preparation
                continue;
            }
            
            // Prepare data asynchronously
            CompletableFuture<Void> asyncTask = CompletableFuture.runAsync(() -> {
                try {
                    long asyncStart = System.nanoTime();
                    
                    // These expensive calls now run off the main thread
                    List<String> scores = this.adapter.getScoreboard(player, board);
                    String title = this.adapter.getTitle(player);
                    
                    // Store prepared data for sync processing
                    pendingUpdates.put(playerUUID, new BoardUpdateData(playerUUID, scores, title));
                    
                    totalAsyncTime += (System.nanoTime() - asyncStart);
                } catch (Exception e) {
                    LibraryPlugin.getInstance().getPlugin().getLogger()
                            .warning("Error preparing board data for " + player.getName() + ": " + e.getMessage());
                }
            }, asyncExecutor);
            
            asyncTasks.add(asyncTask);
        }
        
        // Step 2: Wait for all async tasks to complete (with timeout)
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(
            asyncTasks.toArray(new CompletableFuture[0])
        );
        
        try {
            // Wait for async tasks with timeout to prevent blocking
            allTasks.get(50, TimeUnit.MILLISECONDS); // Max 50ms wait
        } catch (Exception e) {
            // Log timeout but continue - we'll use cached data or skip updates
            if (updateCount % 100 == 0) { // Only log occasionally to avoid spam
                LibraryPlugin.getInstance().getPlugin().getLogger()
                        .fine("Board async preparation timeout - continuing with available data");
            }
        }
        
        // Step 3: Apply all scoreboard updates synchronously in batch
        long syncStart = System.nanoTime();
        processPendingUpdates();
        totalSyncTime += (System.nanoTime() - syncStart);
        
        updateCount++;
        
        // Optional: Log performance metrics periodically
        if (updateCount % 200 == 0) {
            logPerformanceMetrics();
        }
    }

    /**
     * Processes all pending board updates synchronously.
     * This method applies the data prepared asynchronously to the actual scoreboards.
     */
    private void processPendingUpdates() {
        for (Map.Entry<UUID, BoardUpdateData> entry : pendingUpdates.entrySet()) {
            UUID playerUUID = entry.getKey();
            BoardUpdateData updateData = entry.getValue();
            
            // Skip expired data
            if (updateData.isExpired()) {
                continue;
            }
            
            Board board = playerBoards.get(playerUUID);
            if (board == null) continue;
            
            Player player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(playerUUID);
            if (player == null || !player.isOnline()) {
                continue;
            }
            
            // Skip players with cElement metadata - they shouldn't have boards
            if (player.hasMetadata(C_ELEMENT_METADATA_KEY)) {
                continue;
            }
            
            try {
                updatePlayerBoardSync(player, board, updateData);
                lastUpdateTimes.put(playerUUID, System.currentTimeMillis());
            } catch (Exception e) {
                LibraryPlugin.getInstance().getPlugin().getLogger()
                        .severe("Error updating scoreboard for " + player.getName() + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // Clean up expired data
        pendingUpdates.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Updates the scoreboard for a specific player using pre-prepared data.
     * This method only performs the synchronous scoreboard API operations.
     * 
     * @param player The player to update the board for
     * @param board The board instance for this player
     * @param updateData Pre-prepared update data from async thread
     */
    private void updatePlayerBoardSync(Player player, Board board, BoardUpdateData updateData) {
        Scoreboard scoreboard = board.getScoreboard();
        Objective objective = board.getObjective();

        List<String> scores = updateData.scores;

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
        String newTitle = updateData.title;
        if (newTitle != null && !objective.getDisplayName().equals(newTitle)) {
            objective.setDisplayName(newTitle);
        }

        // Efficiently update entries
        updateBoardEntries(board, scores, objective);

        this.adapter.onScoreboardCreate(player, scoreboard);
        player.setScoreboard(scoreboard);
    }
    
    /**
     * Logs performance metrics for monitoring board update performance.
     */
    private void logPerformanceMetrics() {
        if (updateCount == 0) return;
        
        long avgAsyncTime = totalAsyncTime / updateCount;
        long avgSyncTime = totalSyncTime / updateCount;
        
        LibraryPlugin.getInstance().getPlugin().getLogger().info(
            String.format("Board Performance: Updates=%d, AvgAsync=%dns, AvgSync=%dns, ActiveBoards=%d", 
                updateCount, avgAsyncTime, avgSyncTime, playerBoards.size())
        );
        
        // Reset counters periodically to prevent overflow
        if (updateCount >= 1000) {
            totalAsyncTime = 0;
            totalSyncTime = 0;
            updateCount = 0;
        }
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
