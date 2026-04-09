package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BoardManager implements Runnable {

	private static final String C_ELEMENT_METADATA_KEY = "cElement";
	private final Map<UUID, Board> playerBoards = new ConcurrentHashMap<>();
	@Getter
	private final BoardAdapter adapter;

	public BoardManager(BoardAdapter adapter) {
		BoardHandler.init();
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
				clearBoardEntries(board);
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

	private static void clearBoardEntries(Board board) {
		if (!board.getEntries().isEmpty()) {
			board.clearAllEntries();
		}
	}

	private void updateBoard(Player player, Board board) {
		List<String> raw = adapter.getLines(player, board);
		if (raw == null || raw.isEmpty()) {
			if (!board.getEntries().isEmpty()) {
				board.clearAllEntries();
			}
			return;
		}
		List<String> lines = new ArrayList<>(raw);
		Collections.reverse(lines);

		String newTitle = adapter.getTitle(player);
		if (!java.util.Objects.equals(newTitle, board.getLastAppliedTitle())) {
			board.setLastAppliedTitle(newTitle);
			BoardHandler.applyObjectiveTitle(board.getObjective(), newTitle);
		}

		syncEntries(board, lines);
		Scoreboard sb = board.getScoreboard();
		if (!player.getScoreboard().equals(sb)) {
			player.setScoreboard(sb);
			adapter.onScoreboardCreate(player, sb);
		}
	}

	private void syncEntries(Board board, List<String> lines) {
		List<BoardEntry> entryList = board.getEntries();
		synchronized (entryList) {
			int n = lines.size();
			while (entryList.size() > n) {
				int last = entryList.size() - 1;
				entryList.get(last).remove();
				entryList.remove(last);
			}
			for (int i = 0; i < n; i++) {
				String line = lines.get(i);
				int pos = i + 1;
				BoardEntry entry;
				if (i < entryList.size()) {
					entry = entryList.get(i);
					if (!entry.getText().equals(line)) {
						entry.setText(line).setup();
					}
				} else {
					entry = new BoardEntry(board, line);
				}
				entry.send(pos);
			}
		}
	}

	public void createBoard(Player player) {
		if (player.hasMetadata(C_ELEMENT_METADATA_KEY) || playerBoards.containsKey(player.getUniqueId())) {
			return;
		}
		playerBoards.put(player.getUniqueId(), new Board(player, adapter));
	}

	public void removeBoard(Player player) {
		if (player.hasMetadata(C_ELEMENT_METADATA_KEY)) {
			return;
		}
		Board board = playerBoards.remove(player.getUniqueId());
		if (board != null && !board.getEntries().isEmpty()) {
			board.clearAllEntries();
		}
	}
}
