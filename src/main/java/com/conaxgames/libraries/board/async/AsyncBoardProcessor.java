package com.conaxgames.libraries.board.async;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.board.Board;
import com.conaxgames.libraries.board.BoardAdapter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Async processor for scoreboard operations to improve performance
 */
public class AsyncBoardProcessor {
    
    private final ExecutorService executor;
    private final AtomicInteger activeTasks = new AtomicInteger(0);
    private final int maxConcurrentTasks;
    
    public AsyncBoardProcessor() {
        // Optimize thread pool size for scoreboard operations
        this.maxConcurrentTasks = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        this.executor = Executors.newFixedThreadPool(maxConcurrentTasks, r -> {
            Thread t = new Thread(r, "Scoreboard-Async-" + System.currentTimeMillis());
            t.setDaemon(true); // Prevent blocking shutdown
            return t;
        });
    }
    
    /**
     * Process scoreboard data asynchronously
     */
    public CompletableFuture<ScoreboardData> processScoreboardAsync(Player player, Board board, BoardAdapter adapter) {
        if (activeTasks.get() >= maxConcurrentTasks) {
            // If we're at capacity, return a completed future with cached data
            return CompletableFuture.completedFuture(null);
        }
        
        activeTasks.incrementAndGet();
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<String> scores = adapter.getScoreboard(player, board);
                String title = adapter.getTitle(player);
                return new ScoreboardData(scores, title);
            } catch (Exception e) {
                LibraryPlugin.getInstance().getPlugin().getLogger()
                    .warning("Error processing scoreboard for " + player.getName() + ": " + e.getMessage());
                return null;
            } finally {
                activeTasks.decrementAndGet();
            }
        }, executor);
    }
    
    /**
     * Process scoreboard data using adapter's async methods
     */
    public CompletableFuture<ScoreboardData> processScoreboardWithAsyncAdapter(Player player, Board board, BoardAdapter adapter) {
        if (activeTasks.get() >= maxConcurrentTasks || player == null || board == null || adapter == null) {
            return CompletableFuture.completedFuture(null);
        }
        
        activeTasks.incrementAndGet();
        
        // Store futures to avoid calling async methods twice
        CompletableFuture<List<String>> scoresFuture = adapter.getScoreboardAsync(player, board);
        CompletableFuture<String> titleFuture = adapter.getTitleAsync(player);
        
        return CompletableFuture.allOf(scoresFuture, titleFuture)
            .thenApply(v -> {
                try {
                    List<String> scores = scoresFuture.join();
                    String title = titleFuture.join();
                    return new ScoreboardData(scores, title);
                } catch (Exception e) {
                    LibraryPlugin.getInstance().getPlugin().getLogger()
                        .warning("Error processing scoreboard for " + (player != null ? player.getName() : "unknown") + ": " + e.getMessage());
                    return null;
                } finally {
                    activeTasks.decrementAndGet();
                }
            }).exceptionally(throwable -> {
                activeTasks.decrementAndGet();
                LibraryPlugin.getInstance().getPlugin().getLogger()
                    .warning("Async scoreboard processing failed: " + throwable.getMessage());
                return null;
            });
    }
    
    /**
     * Shutdown the executor
     */
    public void shutdown() {
        executor.shutdown();
    }
    
    /**
     * Get the number of active tasks
     */
    public int getActiveTasks() {
        return activeTasks.get();
    }
    
    /**
     * Get the maximum concurrent tasks
     */
    public int getMaxConcurrentTasks() {
        return maxConcurrentTasks;
    }
    
    /**
     * Scoreboard data container
     */
    public static class ScoreboardData {
        private final List<String> scores;
        private final String title;
        private final long timestamp;
        
        public ScoreboardData(List<String> scores, String title) {
            this.scores = scores != null ? scores : List.of();
            this.title = title;
            this.timestamp = System.currentTimeMillis();
        }
        
        public List<String> getScores() { return scores; }
        public String getTitle() { return title; }
        public long getTimestamp() { return timestamp; }
    }
} 