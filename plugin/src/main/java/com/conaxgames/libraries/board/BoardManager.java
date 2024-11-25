package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BoardManager implements Runnable {

	private final Map<UUID, Board> playerBoards = new ConcurrentHashMap<>();
	private final BoardAdapter adapter;

	public BoardManager(BoardAdapter adapter) {
		this.adapter = adapter;
	}

	@Override
	public void run() {
		this.adapter.preLoop();
		for (Player player : LibraryPlugin.getInstance().getPlugin().getServer().getOnlinePlayers()) {
			Board board = this.playerBoards.get(player.getUniqueId());
			if (board == null) {
				continue;
			}

			try {
				Scoreboard scoreboard = board.getScoreboard();
				if (scoreboard == null) {
					continue;
				}

				Objective objective = board.getObjective();
				if (objective == null) {
					continue;
				}

				List<String> scores = this.adapter.getScoreboard(player, board);

				synchronized (board.getEntries()) {
					if (scores != null) {
						Collections.reverse(scores);

						String newTitle = this.adapter.getTitle(player);
						if (!objective.getDisplayName().equals(newTitle)) {
							objective.setDisplayName(newTitle);
						}

						List<BoardEntry> entriesCopy = new ArrayList<>(board.getEntries());

						for (BoardEntry boardEntry : entriesCopy) {
							if (!scores.contains(boardEntry.getText())) {
								boardEntry.remove();
								board.getEntries().remove(boardEntry);
							}
						}

						for (int i = 0; i < scores.size(); i++) {
							String text = scores.get(i);
							int position = i + 1;

							BoardEntry entry = board.getByPosition(i);
							if (entry == null || !entry.getText().equals(text)) {
								if (entry != null) {
									entry.remove();
								}
								entry = new BoardEntry(board, text);
								entry.setup().send(position);
							}
						}
					} else {
						for (BoardEntry entry : new ArrayList<>(board.getEntries())) {
							entry.remove();
							board.getEntries().remove(entry);
						}
					}
				}

				this.adapter.onScoreboardCreate(player, scoreboard);
				player.setScoreboard(scoreboard);

			} catch (Exception e) {
				e.printStackTrace();
				LibraryPlugin.getInstance().getPlugin().getLogger()
						.severe("Error updating " + player.getName() + "'s scoreboard: " + board);
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
