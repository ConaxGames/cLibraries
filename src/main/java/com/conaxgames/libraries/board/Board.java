package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board {

	private final BoardAdapter adapter;
	private final Player player;
	private List<BoardEntry> entries = new CopyOnWriteArrayList<>();
	private Set<BoardTimer> timers = new HashSet<>();
	private Set<String> keys = new HashSet<>();
	private Scoreboard scoreboard;
	private Objective objective;
	
	// Performance optimization: Object pool for BoardEntry reuse
	private final List<BoardEntry> entryPool = Collections.synchronizedList(new ArrayList<>());
	private static final int MAX_POOL_SIZE = 50;

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
	
	// Performance optimization: Key reuse system
	private final Map<String, Boolean> usedKeys = new ConcurrentHashMap<>();
	private final java.util.concurrent.atomic.AtomicInteger keyIndex = new java.util.concurrent.atomic.AtomicInteger(0);
	
	public String getNewKey(BoardEntry entry) {
		// Safety check for null entry
		if (entry == null) {
			return "board_" + System.currentTimeMillis() + "_" + (Math.random() * 1000);
		}
		
		// Use atomic counter for thread safety and better performance
		int currentIndex = keyIndex.getAndIncrement();
		String key = AVAILABLE_KEYS[currentIndex % AVAILABLE_KEYS.length];
		
		// Fast path: if key is not used, return immediately
		if (!keys.contains(key)) {
			keys.add(key);
			return key;
		}
		
		// Fallback: find next available key efficiently
		for (int i = 1; i < AVAILABLE_KEYS.length; i++) {
			String candidateKey = AVAILABLE_KEYS[(currentIndex + i) % AVAILABLE_KEYS.length];
			if (!keys.contains(candidateKey)) {
				keys.add(candidateKey);
				return candidateKey;
			}
		}
		
		// Last resort: generate unique key
		String uniqueKey = "board_" + System.currentTimeMillis() + "_" + (Math.random() * 1000);
		keys.add(uniqueKey);
		return uniqueKey;
	}
	
	/**
	 * Get a recycled BoardEntry from pool or create new one
	 */
	public BoardEntry getPooledEntry(String text) {
		synchronized (entryPool) {
			if (!entryPool.isEmpty()) {
				BoardEntry entry = entryPool.remove(entryPool.size() - 1);
				// Reset the entry state for reuse
				entry.resetForReuse(text);
				return entry;
			}
		}
		return new BoardEntry(this, text);
	}
	
	/**
	 * Return BoardEntry to pool for reuse
	 */
	public void returnToPool(BoardEntry entry) {
		if (entry == null) {
			return;
		}
		
		synchronized (entryPool) {
			if (entryPool.size() < MAX_POOL_SIZE) {
				// Clean up the entry before pooling
				entry.cleanupForPool();
				entryPool.add(entry);
			} else {
				// If pool is full, just remove the entry
				entry.remove();
			}
		}
		// Remove from entries list to prevent memory leaks - thread-safe without sync
		entries.remove(entry);
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
		// Return entries to pool for reuse
		for (BoardEntry entry : entries) {
			returnToPool(entry);
		}
		entries.clear();
		keys.clear(); // Keys will be recycled automatically
	}
}