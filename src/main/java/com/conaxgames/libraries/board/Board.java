package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board {

	private final BoardAdapter adapter;
	private final Player player;
	private List<BoardEntry> entries = new ArrayList<>();
	private Set<BoardTimer> timers = new HashSet<>();
	private Set<String> keys = new HashSet<>();
	private Scoreboard scoreboard;
	private Objective objective;

	public Board(Player player, BoardAdapter adapter) {
		this.adapter = adapter;
		this.player = player;

		this.init();
	}

	private void init() {
		if (!this.player.getScoreboard()
				.equals(LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager().getMainScoreboard())) {
			this.scoreboard = this.player.getScoreboard();
		} else {
			this.scoreboard = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager().getNewScoreboard();
		}

		this.objective = this.scoreboard.registerNewObjective("Default", "dummy");
		this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		this.objective.setDisplayName(this.adapter.getTitle(player));
	}

	// Pre-computed key pool for performance with proper recycling
	private static final String[] AVAILABLE_KEYS;
	static {
		ChatColor[] colors = ChatColor.values();
		AVAILABLE_KEYS = new String[colors.length];
		for (int i = 0; i < colors.length; i++) {
			AVAILABLE_KEYS[i] = colors[i] + "" + ChatColor.WHITE;
		}
	}
	
	public String getNewKey(BoardEntry entry) {
		// Find first unused key from pre-computed pool
		for (String baseKey : AVAILABLE_KEYS) {
			String colorText = baseKey;
			
			if (entry.getText().length() > 16) {
				String sub = entry.getText().substring(0, 16);
				colorText = colorText + ChatColor.getLastColors(sub);
			}
			
			if (!keys.contains(colorText)) {
				keys.add(colorText);
				return colorText;
			}
		}
		
		throw new IndexOutOfBoundsException("No more keys available!");
	}

	public List<String> getBoardEntriesFormatted() {
		// Avoid unnecessary ArrayList creation
		List<String> toReturn = new ArrayList<>(entries.size());

		for (BoardEntry entry : entries) {
			toReturn.add(entry.getText());
		}

		return toReturn;
	}

	public BoardEntry getByPosition(int position) {
		// Direct array access instead of linear search - major performance boost
		if (position >= 0 && position < this.entries.size()) {
			return this.entries.get(position);
		}
		return null;
	}

	// Cache for timer lookups to avoid linear search
	private final Map<String, BoardTimer> timerCache = new HashMap<>();
	
	public BoardTimer getCooldown(String id) {
		// Check cache first for O(1) lookup
		BoardTimer cached = timerCache.get(id);
		if (cached != null && cached.getEnd() > System.currentTimeMillis()) {
			return cached;
		}
		
		// Remove expired timer from cache
		if (cached != null) {
			timerCache.remove(id);
		}
		
		// Search in active timers
		for (BoardTimer timer : timers) {
			if (timer.getId().equals(id) && timer.getEnd() > System.currentTimeMillis()) {
				timerCache.put(id, timer);
				return timer;
			}
		}

		return null;
	}

	private long lastTimerCleanup = 0;
	private static final long TIMER_CLEANUP_INTERVAL = 5000; // 5 seconds
	
	public Set<BoardTimer> getTimers() {
		// Only clean up expired timers periodically to reduce CPU usage
		long now = System.currentTimeMillis();
		if (now - lastTimerCleanup > TIMER_CLEANUP_INTERVAL) {
			this.timers.removeIf(cooldown -> now >= cooldown.getEnd());
			this.lastTimerCleanup = now;
			// Also clean timer cache
			timerCache.entrySet().removeIf(entry -> now >= entry.getValue().getEnd());
		}
		return this.timers;
	}

	public BoardAdapter getAdapter() {
		return this.adapter;
	}

	public Player getPlayer() {
		return this.player;
	}

	public List<BoardEntry> getEntries() {
		return this.entries;
	}

	public Set<String> getKeys() {
		return this.keys;
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public Objective getObjective() {
		return this.objective;
	}
	
	/**
	 * Efficiently clear all board entries
	 */
	public void clearAllEntries() {
		for (BoardEntry entry : entries) {
			entry.remove();
		}
		entries.clear();
		keys.clear(); // Keys will be recycled automatically
	}
}