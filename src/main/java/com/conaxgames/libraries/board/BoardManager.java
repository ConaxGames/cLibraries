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
import java.util.concurrent.atomic.AtomicInteger;

public class BoardManager implements Runnable {

	private final Map<UUID, Board> playerBoards = new ConcurrentHashMap<>();
	private final Map<UUID, BoardState> boardStates = new ConcurrentHashMap<>();
	private final BoardAdapter adapter;
	private final Set<UUID> processingPlayers = ConcurrentHashMap.newKeySet();
	
	// Performance optimizations
	private final AtomicInteger batchSize = new AtomicInteger(10);
	private final Map<UUID, Long> lastUpdateTime = new ConcurrentHashMap<>();
	private static final long MIN_UPDATE_INTERVAL = 50; // 50ms minimum between updates per player

	public BoardManager(BoardAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * Check if a player should have a scoreboard
	 * @param player The player to check
	 * @return true if the player should have a scoreboard, false otherwise
	 */
	public boolean shouldHaveScoreboard(Player player) {
		return player != null && player.isOnline() && !player.hasMetadata("cElement");
	}
	
	/**
	 * Clean up boards for players who shouldn't have scoreboards
	 */
	private void cleanupInvalidBoards() {
		Iterator<Map.Entry<UUID, Board>> iterator = playerBoards.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<UUID, Board> entry = iterator.next();
			UUID playerId = entry.getKey();
			Board board = entry.getValue();
			
			Player player = board.getPlayer();
			if (!shouldHaveScoreboard(player)) {
				// Clean up the board
				if (!board.getEntries().isEmpty()) {
					board.getEntries().forEach(BoardEntry::remove);
					board.getEntries().clear();
				}
				iterator.remove();
				boardStates.remove(playerId);
				lastUpdateTime.remove(playerId);
				processingPlayers.remove(playerId);
			}
		}
	}

	@Override
	public void run() {
		this.adapter.preLoop();
		
		// Clean up invalid boards periodically
		cleanupInvalidBoards();
		
		// Process players in batches to reduce CPU spikes
		List<Player> onlinePlayers = new ArrayList<>(LibraryPlugin.getInstance().getPlugin().getServer().getOnlinePlayers());
		
		// Sort players by last update time to prioritize those who haven't been updated recently
		onlinePlayers.sort((p1, p2) -> {
			long time1 = lastUpdateTime.getOrDefault(p1.getUniqueId(), 0L);
			long time2 = lastUpdateTime.getOrDefault(p2.getUniqueId(), 0L);
			return Long.compare(time1, time2);
		});
		
		// Process in smaller batches for better performance
		int currentBatchSize = Math.min(batchSize.get(), onlinePlayers.size());
		List<Player> batch = onlinePlayers.subList(0, Math.min(currentBatchSize, onlinePlayers.size()));
		
		for (Player player : batch) {
			if (!shouldHaveScoreboard(player)) {
				continue;
			}
			
			UUID playerId = player.getUniqueId();
			long currentTime = System.currentTimeMillis();
			
			// Rate limiting per player
			Long lastUpdate = lastUpdateTime.get(playerId);
			if (lastUpdate != null && (currentTime - lastUpdate) < MIN_UPDATE_INTERVAL) {
				continue;
			}
			
			// Skip if already processing this player
			if (!processingPlayers.add(playerId)) {
				continue;
			}
			
			try {
				updatePlayerScoreboard(player);
				lastUpdateTime.put(playerId, currentTime);
			} catch (Exception e) {
				LibraryPlugin.getInstance().getPlugin().getLogger()
					.warning("Error updating scoreboard for " + player.getName() + ": " + e.getMessage());
			} finally {
				processingPlayers.remove(playerId);
			}
		}
		
		// Dynamically adjust batch size based on performance
		adjustBatchSize(onlinePlayers.size());
	}
	
	private void adjustBatchSize(int totalPlayers) {
		int currentSize = batchSize.get();
		if (totalPlayers > 50 && currentSize < 20) {
			batchSize.incrementAndGet();
		} else if (totalPlayers < 20 && currentSize > 5) {
			batchSize.decrementAndGet();
		}
	}
	
	private void updatePlayerScoreboard(Player player) {
		Board board = this.playerBoards.get(player.getUniqueId());
		if (board == null) {
			return;
		}
		
		Scoreboard scoreboard = board.getScoreboard();
		List<String> newScores = this.adapter.getScoreboard(player, board);
		
		if (newScores == null) {
			clearScoreboard(board);
			return;
		}
		
		// Check if scoreboard actually needs updating
		BoardState currentState = boardStates.get(player.getUniqueId());
		if (currentState != null && currentState.isSame(newScores, adapter.getTitle(player))) {
			return; // No changes needed
		}
		
		// Update the scoreboard
		updateScoreboard(board, newScores, player);
		
		// Update state cache
		boardStates.put(player.getUniqueId(), new BoardState(newScores, adapter.getTitle(player)));
		
		// Apply the scoreboard
		player.setScoreboard(scoreboard);
	}
	
	private void updateScoreboard(Board board, List<String> scores, Player player) {
		Collections.reverse(scores);
		Objective objective = board.getObjective();
		
		// Update title if needed
		String newTitle = this.adapter.getTitle(player);
		if (!objective.getDisplayName().equals(newTitle)) {
			objective.setDisplayName(newTitle);
		}
		
		if (scores.isEmpty()) {
			clearScoreboard(board);
			return;
		}
		
		// Optimized scoreboard update with minimal operations
		Map<Integer, String> newScoreMap = new HashMap<>();
		for (int i = 0; i < scores.size(); i++) {
			newScoreMap.put(i + 1, scores.get(i));
		}
		
		// Batch remove old entries
		List<BoardEntry> toRemove = new ArrayList<>();
		for (BoardEntry entry : board.getEntries()) {
			try {
				Score score = objective.getScore(entry.getKey());
				int position = score.getScore();
				
				if (!newScoreMap.containsKey(position) || !newScoreMap.get(position).equals(entry.getText())) {
					toRemove.add(entry);
				}
			} catch (Exception e) {
				toRemove.add(entry);
			}
		}
		
		// Remove old entries in batch
		for (BoardEntry entry : toRemove) {
			entry.remove();
			board.getEntries().remove(entry);
		}
		
		// Add or update entries efficiently
		for (Map.Entry<Integer, String> entry : newScoreMap.entrySet()) {
			int position = entry.getKey();
			String text = entry.getValue();
			
			BoardEntry boardEntry = findEntryByPosition(board, position);
			
			if (boardEntry == null) {
				// Create new entry
				boardEntry = new BoardEntry(board, text);
				boardEntry.send(position);
			} else if (!boardEntry.getText().equals(text)) {
				// Update existing entry
				boardEntry.setText(text).setup().send(position);
			}
		}
	}
	
	private BoardEntry findEntryByPosition(Board board, int position) {
		for (BoardEntry entry : board.getEntries()) {
			try {
				Score score = board.getObjective().getScore(entry.getKey());
				if (score.getScore() == position) {
					return entry;
				}
			} catch (Exception e) {
				// Ignore invalid scores
			}
		}
		return null;
	}
	
	private void clearScoreboard(Board board) {
		Iterator<BoardEntry> iter = board.getEntries().iterator();
		while (iter.hasNext()) {
			BoardEntry boardEntry = iter.next();
			boardEntry.remove();
			iter.remove();
		}
	}

	public Map<UUID, Board> getPlayerBoards() {
		return this.playerBoards;
	}

	public BoardAdapter getAdapter() {
		return this.adapter;
	}
	
	// Performance monitoring
	public int getBatchSize() {
		return batchSize.get();
	}
	
	public int getProcessingPlayers() {
		return processingPlayers.size();
	}
	
	// Helper class to track board state for change detection
	private static class BoardState {
		private final List<String> scores;
		private final String title;
		private final int hashCode;
		
		public BoardState(List<String> scores, String title) {
			this.scores = new ArrayList<>(scores);
			this.title = title;
			this.hashCode = Objects.hash(scores, title);
		}
		
		public boolean isSame(List<String> newScores, String newTitle) {
			return Objects.equals(this.title, newTitle) && 
				   this.scores.size() == newScores.size() && 
				   this.scores.equals(newScores);
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			BoardState that = (BoardState) obj;
			return Objects.equals(scores, that.scores) && Objects.equals(title, that.title);
		}
	}
}
