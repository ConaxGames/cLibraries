package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ConcurrentHashMap;

public class Board {

	private final BoardAdapter adapter;
	private final Player player;
	private List<BoardEntry> entries = new ArrayList<>();
	private Set<BoardTimer> timers = new HashSet<>();
	private Set<String> keys = new HashSet<>();
	private Scoreboard scoreboard;
	private Objective objective;
	
	// Performance optimizations
	private static final AtomicInteger keyCounter = new AtomicInteger(0);
	private static final ChatColor[] COLOR_ARRAY = ChatColor.values();
	private static final ConcurrentHashMap<String, String> keyCache = new ConcurrentHashMap<>();
	private static final int MAX_KEYS = 1000;
	
	// Memory management
	private long lastCleanup = System.currentTimeMillis();
	private static final long CLEANUP_INTERVAL = 300000; // 5 minutes

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

	public String getNewKey(BoardEntry entry) {
		// Check cache first
		String cacheKey = entry.getText().hashCode() + "";
		String cachedKey = keyCache.get(cacheKey);
		if (cachedKey != null && !keys.contains(cachedKey)) {
			keys.add(cachedKey);
			return cachedKey;
		}
		
		// Generate new key
		int counter = keyCounter.getAndIncrement();
		int colorIndex = counter % COLOR_ARRAY.length;
		ChatColor color = COLOR_ARRAY[colorIndex];
		
		String colorText = color + "" + ChatColor.WHITE;

		if (entry.getText().length() > 16) {
			String sub = entry.getText().substring(0, 16);
			colorText = colorText + ChatColor.getLastColors(sub);
		}

		// Ensure uniqueness by adding counter if needed
		String baseKey = colorText;
		int suffix = 0;
		while (keys.contains(colorText)) {
			colorText = baseKey + suffix;
			suffix++;
		}
		
		keys.add(colorText);
		
		// Cache the key for future use
		keyCache.put(cacheKey, colorText);
		
		// Clean up cache if it gets too large
		if (keyCache.size() > MAX_KEYS) {
			keyCache.clear();
		}
		
		return colorText;
	}

	public List<String> getBoardEntriesFormatted() {
		List<String> toReturn = new ArrayList<>(entries.size());
		for (BoardEntry entry : entries) {
			toReturn.add(entry.getText());
		}
		return toReturn;
	}

	public BoardEntry getByPosition(int position) {
		if (position < 0 || position >= entries.size()) {
			return null;
		}
		return this.entries.get(position);
	}

	public BoardTimer getCooldown(String id) {
		for (BoardTimer cooldown : getTimers()) {
			if (cooldown.getId().equals(id)) {
				return cooldown;
			}
		}
		return null;
	}

	public Set<BoardTimer> getTimers() {
		this.timers.removeIf(cooldown -> System.currentTimeMillis() >= cooldown.getEnd());
		return this.timers;
	}
	
	// Memory cleanup
	public void cleanup() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastCleanup > CLEANUP_INTERVAL) {
			// Remove unused keys
			Set<String> usedKeys = new HashSet<>();
			for (BoardEntry entry : entries) {
				usedKeys.add(entry.getKey());
			}
			keys.retainAll(usedKeys);
			
			// Clear old cache entries
			if (keyCache.size() > MAX_KEYS / 2) {
				keyCache.clear();
			}
			
			lastCleanup = currentTime;
		}
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
}