package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.util.CC;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Board {

	private static final String OBJECTIVE_NAME = "sb";
	private static final String[] ENTRY_KEY_BASES;

	static {
		String codes = "0123456789abcdefklmor";
		ENTRY_KEY_BASES = new String[codes.length()];
		for (int i = 0; i < codes.length(); i++) {
			ENTRY_KEY_BASES[i] = "\u00a7" + codes.charAt(i) + "\u00a7f";
		}
	}

	@Getter
	private final List<BoardEntry> entries = Collections.synchronizedList(new ArrayList<>());
	private final Map<String, BoardTimer> timers = new ConcurrentHashMap<>();
	@Getter
	private final Map<String, String> usedKeys = new ConcurrentHashMap<>();
	@Getter
	private final Scoreboard scoreboard;
	@Getter
	private final Objective objective;
	private volatile String lastAppliedTitle;

	public Board(Player player, BoardAdapter adapter) {
		BoardHandler.init();
		this.scoreboard = resolveScoreboard(player);
		String title = adapter.getTitle(player);
		this.lastAppliedTitle = title;
		this.objective = BoardHandler.createSidebarObjective(this.scoreboard, OBJECTIVE_NAME, title);
	}

	private static Scoreboard resolveScoreboard(Player player) {
		var sm = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager();
		return player.getScoreboard().equals(sm.getMainScoreboard())
			? sm.getNewScoreboard()
			: player.getScoreboard();
	}

	public String getNewKey(BoardEntry entry) {
		String text = entry.getText();
		int unit = BoardHandler.lineSplitUnit();
		String suffix = text.length() > unit ? CC.getLastColors(text.substring(0, unit)) : "";
		for (String base : ENTRY_KEY_BASES) {
			String key = base + suffix;
			if (usedKeys.putIfAbsent(key, text) == null) {
				return key;
			}
		}
		throw new IllegalStateException("No free board entry keys");
	}

	public BoardTimer getTimer(String id) {
		BoardTimer t = timers.get(id);
		if (t == null) {
			return null;
		}
		if (t.isExpired()) {
			timers.remove(id, t);
			return null;
		}
		return t;
	}

	public void addTimer(BoardTimer timer) {
		timers.put(timer.getId(), timer);
	}

	public void removeTimer(String id) {
		timers.remove(id);
	}

	public void clearAllEntries() {
		synchronized (entries) {
			for (BoardEntry e : entries) {
				e.remove();
			}
			entries.clear();
		}
		usedKeys.clear();
	}

	void setLastAppliedTitle(String title) {
		this.lastAppliedTitle = title;
	}

	String getLastAppliedTitle() {
		return lastAppliedTitle;
	}

}
