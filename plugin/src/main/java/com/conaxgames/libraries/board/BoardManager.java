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
		for (Player player : LibraryPlugin.getInstance().getServer().getOnlinePlayers()) {
			Board board = this.playerBoards.get(player.getUniqueId());
			if (board == null) {
				continue;
			}
			try {
				Scoreboard scoreboard = board.getScoreboard();

				List<String> scores = this.adapter.getScoreboard(player, board);

				if (scores != null) {
					Collections.reverse(scores);

					Objective objective = board.getObjective();

					if (!objective.getDisplayName().equals(this.adapter.getTitle(player))) {
						objective.setDisplayName(this.adapter.getTitle(player));
					}

					if (scores.isEmpty()) {
						Iterator<BoardEntry> iter = board.getEntries().iterator();
						while (iter.hasNext()) {
							BoardEntry boardEntry = iter.next();
							boardEntry.remove();
							iter.remove();
						}
						continue;
					}

					forILoop:
					for (int i = 0; i < scores.size(); i++) {
						String text = scores.get(i);
						int position = i + 1;

						for (BoardEntry boardEntry : new LinkedList<>(board.getEntries())) {
							Score score = objective.getScore(boardEntry.getKey());

							if (score != null && boardEntry.getText().equals(text)) {
								if (score.getScore() == position) {
									continue forILoop;
								}
							}
						}

						Iterator<BoardEntry> iter = board.getEntries().iterator();
						while (iter.hasNext()) {
							BoardEntry boardEntry = iter.next();
							int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntry.getKey()).getScore();
							if (entryPosition > scores.size()) {
								boardEntry.remove();
								iter.remove();
							}
						}

						int positionToSearch = position - 1;

						BoardEntry entry = board.getByPosition(positionToSearch);
						if (entry == null) {
							entry = new BoardEntry(board, text).send(position);
						}

						entry.setText(text).setup().send(position);

						if (board.getEntries().size() > scores.size()) {
							iter = board.getEntries().iterator();
							while (iter.hasNext()) {
								BoardEntry boardEntry = iter.next();
								if (!scores.contains(boardEntry.getText()) || Collections.frequency(board.getBoardEntriesFormatted(), boardEntry.getText()) > 1) {
									boardEntry.remove();
									iter.remove();
								}
							}
						}
					}
				} else {
					if (!board.getEntries().isEmpty()) {
						board.getEntries().forEach(BoardEntry::remove);
						board.getEntries().clear();
					}
				}

				this.adapter.onScoreboardCreate(player, scoreboard);

				player.setScoreboard(scoreboard);
			} catch (Exception e) {
				e.printStackTrace();

				LibraryPlugin.getInstance().getLogger()
						.severe("Something went wrong while updating " + player.getName() + "'s scoreboard " + board + " - " + board.getAdapter() + ")");
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
