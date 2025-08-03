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

	public BoardManager(BoardAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void run() {
		this.adapter.preLoop();
		
		// Only process players who actually have boards - major performance boost
		for (Map.Entry<UUID, Board> entry : this.playerBoards.entrySet()) {
			UUID playerUUID = entry.getKey();
			Board board = entry.getValue();
			
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

				if (scores == null || scores.isEmpty()) {
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
				if (!objective.getDisplayName().equals(newTitle)) {
					objective.setDisplayName(newTitle);
				}

				// Efficiently update entries
				updateBoardEntries(board, scores, objective);

				this.adapter.onScoreboardCreate(player, scoreboard);
				player.setScoreboard(scoreboard);
			} catch (Exception e) {
				e.printStackTrace();
				LibraryPlugin.getInstance().getPlugin().getLogger()
						.severe("Something went wrong while updating " + player.getName() + "'s scoreboard " + board + " - " + board.getAdapter() + ")");
			}
		}
	}
	
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

	public Map<UUID, Board> getPlayerBoards() {
		return this.playerBoards;
	}

	public BoardAdapter getAdapter() {
		return this.adapter;
	}
}
