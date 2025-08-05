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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import com.conaxgames.libraries.board.async.AsyncBoardProcessor;
import java.util.Iterator;
import java.util.HashMap;

public class BoardManager implements Runnable {

	private final Map<UUID, Board> playerBoards = new ConcurrentHashMap<>();
	private final BoardAdapter adapter;
	
	// Performance optimization: Cache for player online status
	private final Set<UUID> onlinePlayers = new HashSet<>();
	private long lastOnlineStatusUpdate = 0;
	private static final long ONLINE_STATUS_UPDATE_INTERVAL = 2000; // 2 seconds for better performance
	
	// Async processing components
	private final AsyncBoardProcessor asyncProcessor = new AsyncBoardProcessor();
	private final Map<UUID, CompletableFuture<AsyncBoardProcessor.ScoreboardData>> pendingUpdates = new ConcurrentHashMap<>();
	private final Map<UUID, AsyncBoardProcessor.ScoreboardData> lastProcessedData = new ConcurrentHashMap<>();



	public BoardManager(BoardAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void run() {
		this.adapter.preLoop();
		
		// Update online players cache periodically
		long now = System.currentTimeMillis();
		if (now - lastOnlineStatusUpdate > ONLINE_STATUS_UPDATE_INTERVAL) {
			updateOnlinePlayersCache();
			lastOnlineStatusUpdate = now;
		}
		
		// Process async scoreboard generation for online players
		processAsyncScoreboardGeneration();
		
		// Apply completed async updates on main thread
		applyAsyncUpdates();
		
		// Clean up old data
		cleanupOldData();
	}
	
	/**
	 * Process scoreboard generation asynchronously for better performance
	 */
	private void processAsyncScoreboardGeneration() {
		for (Map.Entry<UUID, Board> entry : this.playerBoards.entrySet()) {
			UUID playerUUID = entry.getKey();
			Board board = entry.getValue();
			
			// Use cached online status for faster checks
			if (!onlinePlayers.contains(playerUUID)) {
				continue;
			}
			
			Player player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(playerUUID);
			if (player == null || !player.isOnline()) {
				continue;
			}
			
			// Skip players with cElement metadata - they shouldn't have boards
			if (player.hasMetadata("cElement")) {
				continue;
			}
			
			// Check if we already have a pending update for this player
			if (pendingUpdates.containsKey(playerUUID)) {
				continue;
			}
			
			// Start async processing using the async processor with timeout
			CompletableFuture<AsyncBoardProcessor.ScoreboardData> future = 
				asyncProcessor.processScoreboardWithAsyncAdapter(player, board, this.adapter)
					.orTimeout(5000, java.util.concurrent.TimeUnit.MILLISECONDS); // 5 second timeout
			
			// Only add if not already present (double-check pattern)
			if (!pendingUpdates.containsKey(playerUUID)) {
				pendingUpdates.put(playerUUID, future);
			}
		}
	}
	
	/**
	 * Apply completed async updates on the main thread
	 */
	private void applyAsyncUpdates() {
		// Use a copy of the entry set to avoid concurrent modification
		Map<UUID, CompletableFuture<AsyncBoardProcessor.ScoreboardData>> updatesCopy = new HashMap<>(pendingUpdates);
		
		for (Map.Entry<UUID, CompletableFuture<AsyncBoardProcessor.ScoreboardData>> entry : updatesCopy.entrySet()) {
			UUID playerUUID = entry.getKey();
			CompletableFuture<AsyncBoardProcessor.ScoreboardData> future = entry.getValue();
			
			if (future.isDone()) {
				try {
					AsyncBoardProcessor.ScoreboardData data = future.get();
					if (data != null) {
						applyScoreboardUpdate(playerUUID, data);
					}
				} catch (Exception e) {
					LibraryPlugin.getInstance().getPlugin().getLogger()
						.severe("Error applying scoreboard update for player " + playerUUID + ": " + e.getMessage());
				} finally {
					pendingUpdates.remove(playerUUID);
				}
			}
		}
	}
	
	/**
	 * Apply scoreboard update on main thread
	 */
	private void applyScoreboardUpdate(UUID playerUUID, AsyncBoardProcessor.ScoreboardData data) {
		// Check if data is null
		if (data == null) {
			return;
		}
		
		Board board = playerBoards.get(playerUUID);
		if (board == null) {
			return;
		}
		
		Player player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(playerUUID);
		if (player == null || !player.isOnline()) {
			return;
		}
		
		try {
			Scoreboard scoreboard = board.getScoreboard();
			Objective objective = board.getObjective();
			
			// Safety check for null scoreboard or objective
			if (scoreboard == null || objective == null) {
				LibraryPlugin.getInstance().getPlugin().getLogger()
					.warning("Scoreboard or objective is null for player " + player.getName());
				return;
			}

			List<String> scores = data.getScores();

			// Safety check for null scores
			if (scores == null) {
				scores = new ArrayList<>();
			}

			if (scores.isEmpty()) {
				// Clear all entries if no scores
				if (!board.getEntries().isEmpty()) {
					board.clearAllEntries();
				}
				return;
			}
			
			// Reverse scores for proper positioning
			Collections.reverse(scores);
			
			// Update title if changed
			String newTitle = data.getTitle();
			if (newTitle != null && !objective.getDisplayName().equals(newTitle)) {
				objective.setDisplayName(newTitle);
			}

			// Efficiently update entries with smart change detection
			updateBoardEntriesOptimized(board, scores, objective);

			this.adapter.onScoreboardCreate(player, scoreboard);
			player.setScoreboard(scoreboard);
			
			// Cache the processed data
			lastProcessedData.put(playerUUID, data);
			
		} catch (Exception e) {
			e.printStackTrace();
			LibraryPlugin.getInstance().getPlugin().getLogger()
					.severe("Something went wrong while updating " + player.getName() + "'s scoreboard " + board + " - " + board.getAdapter() + ")");
			
			// Try to recover by clearing and recreating the board
			try {
				board.clearAllEntries();
				player.setScoreboard(LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager().getNewScoreboard());
			} catch (Exception recoveryException) {
				LibraryPlugin.getInstance().getPlugin().getLogger()
					.severe("Failed to recover scoreboard for " + player.getName() + ": " + recoveryException.getMessage());
			}
		}
	}
	
	/**
	 * Clean up old data to prevent memory leaks
	 */
	private void cleanupOldData() {
		long now = System.currentTimeMillis();
		long maxAge = 30000; // 30 seconds
		
		lastProcessedData.entrySet().removeIf(entry -> 
			(now - entry.getValue().getTimestamp()) > maxAge
		);
	}
	
	/**
	 * Shutdown async executor when plugin disables
	 */
	public void shutdown() {
		// Cancel all pending updates
		pendingUpdates.values().forEach(future -> future.cancel(true));
		pendingUpdates.clear();
		lastProcessedData.clear();
		
		// Shutdown the async processor
		asyncProcessor.shutdown();
	}
	
	private void updateOnlinePlayersCache() {
		onlinePlayers.clear();
		for (Player player : LibraryPlugin.getInstance().getPlugin().getServer().getOnlinePlayers()) {
			onlinePlayers.add(player.getUniqueId());
		}
		// Clean up disconnected players from playerBoards to prevent memory leaks
		// Use more efficient removal to avoid concurrent modification
		playerBoards.entrySet().removeIf(entry -> {
			boolean shouldRemove = !onlinePlayers.contains(entry.getKey());
			if (shouldRemove) {
				// Clean up the board properly
				Board board = entry.getValue();
				if (board != null) {
					board.clearAllEntries();
				}
			}
			return shouldRemove;
		});
	}
	
	private void updateBoardEntriesOptimized(Board board, List<String> scores, Objective objective) {
		List<BoardEntry> entries = board.getEntries();
		int scoresSize = scores.size();
		
		// Safety check for null or empty scores
		if (scores == null || scoresSize == 0) {
			// Clear all entries if no scores
			if (!entries.isEmpty()) {
				board.clearAllEntries();
			}
			return;
		}
		
		// Remove excess entries efficiently - CopyOnWriteArrayList doesn't support iterator.remove()
		if (entries.size() > scoresSize) {
			// Create a list of entries to remove
			List<BoardEntry> toRemove = new ArrayList<>();
			for (int i = scoresSize; i < entries.size(); i++) {
				BoardEntry entry = entries.get(i);
				if (entry != null) {
					board.returnToPool(entry);
					toRemove.add(entry);
				}
			}
			// Remove all excess entries at once
			entries.removeAll(toRemove);
		}
		
		// Update or create entries with minimal operations
		for (int i = 0; i < scoresSize; i++) {
			String text = scores.get(i);
			int position = i + 1;
			
			BoardEntry entry;
			if (i < entries.size()) {
				// Update existing entry only if text changed
				entry = entries.get(i);
				if (entry != null && !entry.getText().equals(text)) {
					entry.setText(text);
					entry.sendOptimized(position);
				} else if (entry != null) {
					// Only update position if it changed
					entry.sendPositionOnly(position);
				} else {
					// Entry is null, create new one
					entry = board.getPooledEntry(text);
					entries.set(i, entry);
					entry.sendOptimized(position);
				}
			} else {
				// Create new entry using object pool
				entry = board.getPooledEntry(text);
				entries.add(entry); // Add to entries list
				entry.sendOptimized(position);
			}
		}
	}

	public Map<UUID, Board> getPlayerBoards() {
		return this.playerBoards;
	}

	public BoardAdapter getAdapter() {
		return this.adapter;
	}
}
