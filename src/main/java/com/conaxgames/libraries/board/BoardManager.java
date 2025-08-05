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

public class BoardManager implements Runnable {

	private final Map<UUID, Board> playerBoards = new HashMap<>();
	private final BoardAdapter adapter;
	
	// Performance optimization: Cache for player online status
	private final Set<UUID> onlinePlayers = new HashSet<>();
	private long lastOnlineStatusUpdate = 0;
	private static final long ONLINE_STATUS_UPDATE_INTERVAL = 1000; // 1 second

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
		
		// Only process players who actually have boards - major performance boost
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
			
			try {
				Scoreboard scoreboard = board.getScoreboard();
				Objective objective = board.getObjective();

				List<String> scores = this.adapter.getScoreboard(player, board);

				// Safety check for null scores
				if (scores == null) {
					scores = new ArrayList<>();
				}

				if (scores.isEmpty()) {
					// Clear all entries if no scores
					if (!board.getEntries().isEmpty()) {
						board.clearAllEntries();
					}
					continue;
				}
				
				// Reverse scores for proper positioning
				Collections.reverse(scores);
				
				// Update title if changed
				String newTitle = this.adapter.getTitle(player);
				if (newTitle != null && !objective.getDisplayName().equals(newTitle)) {
					objective.setDisplayName(newTitle);
				}

				// Efficiently update entries with smart change detection
				updateBoardEntriesOptimized(board, scores, objective);

				this.adapter.onScoreboardCreate(player, scoreboard);
				player.setScoreboard(scoreboard);
			} catch (Exception e) {
				e.printStackTrace();
				LibraryPlugin.getInstance().getPlugin().getLogger()
						.severe("Something went wrong while updating " + player.getName() + "'s scoreboard " + board + " - " + board.getAdapter() + ")");
			}
		}
	}
	
	private void updateOnlinePlayersCache() {
		onlinePlayers.clear();
		for (Player player : LibraryPlugin.getInstance().getPlugin().getServer().getOnlinePlayers()) {
			onlinePlayers.add(player.getUniqueId());
		}
		// Clean up disconnected players from playerBoards to prevent memory leaks
		playerBoards.entrySet().removeIf(entry -> !onlinePlayers.contains(entry.getKey()));
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
		
		// Remove excess entries efficiently - use iterator to avoid index issues
		if (entries.size() > scoresSize) {
			// Create a list of entries to remove to avoid concurrent modification
			List<BoardEntry> toRemove = new ArrayList<>();
			for (int i = scoresSize; i < entries.size(); i++) {
				toRemove.add(entries.get(i));
			}
			
			// Remove the excess entries
			for (BoardEntry entry : toRemove) {
				board.returnToPool(entry);
				entries.remove(entry);
			}
		}
		
		// Update or create entries with minimal operations
		for (int i = 0; i < scoresSize; i++) {
			String text = scores.get(i);
			int position = i + 1;
			
			BoardEntry entry;
			if (i < entries.size()) {
				// Update existing entry only if text changed
				entry = entries.get(i);
				if (!entry.getText().equals(text)) {
					entry.setText(text);
					entry.sendOptimized(position);
				} else {
					// Only update position if it changed
					entry.sendPositionOnly(position);
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
