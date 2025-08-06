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

/**
 * Represents a scoreboard board for a specific player.
 * This class manages the scoreboard entries, timers, and provides utilities for board management.
 * 
 * @author ConaxGames
 * @since 1.0
 */
public class Board {

    // Constants
    private static final String OBJECTIVE_NAME = "Default";
    private static final String OBJECTIVE_CRITERIA = "dummy";
    private static final long TIMER_CLEANUP_INTERVAL = 5000L; // 5 seconds
    private static final int MAX_TEXT_LENGTH = 16;
    private static final int MAX_PREFIX_LENGTH = 64;
    private static final int MAX_SUFFIX_LENGTH = 64;

    // Pre-computed key pool for performance with proper recycling
    private static final String[] AVAILABLE_KEYS;
    static {
        ChatColor[] colors = ChatColor.values();
        AVAILABLE_KEYS = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            AVAILABLE_KEYS[i] = colors[i] + "" + ChatColor.WHITE;
        }
    }

    // Instance fields
    private final BoardAdapter adapter;
    private final Player player;
    private final List<BoardEntry> entries = new ArrayList<>();
    private final Set<BoardTimer> timers = new HashSet<>();
    private final Set<String> keys = new HashSet<>();
    private final Map<String, BoardTimer> timerCache = new HashMap<>();
    
    private Scoreboard scoreboard;
    private Objective objective;
    private long lastTimerCleanup = 0;

    /**
     * Creates a new board for the specified player with the given adapter.
     * 
     * @param player The player this board belongs to
     * @param adapter The adapter that provides board content
     */
    public Board(Player player, BoardAdapter adapter) {
        this.adapter = adapter;
        this.player = player;
        this.init();
    }

    /**
     * Initializes the scoreboard and objective for this board.
     */
    private void init() {
        if (!this.player.getScoreboard()
                .equals(LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager().getMainScoreboard())) {
            this.scoreboard = this.player.getScoreboard();
        } else {
            this.scoreboard = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager().getNewScoreboard();
        }

        this.objective = this.scoreboard.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_CRITERIA);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.objective.setDisplayName(this.adapter.getTitle(player));
    }

    /**
     * Generates a new unique key for a board entry.
     * 
     * @param entry The board entry to generate a key for
     * @return A unique key for the entry
     * @throws IndexOutOfBoundsException if no more keys are available
     */
    public String getNewKey(BoardEntry entry) {
        for (String baseKey : AVAILABLE_KEYS) {
            String colorText = baseKey;
            
            if (entry.getText().length() > MAX_TEXT_LENGTH) {
                String sub = entry.getText().substring(0, MAX_TEXT_LENGTH);
                colorText = colorText + ChatColor.getLastColors(sub);
            }
            
            if (!keys.contains(colorText)) {
                keys.add(colorText);
                return colorText;
            }
        }
        
        throw new IndexOutOfBoundsException("No more keys available for board entries!");
    }

    /**
     * Returns a formatted list of all board entries.
     * 
     * @return List of formatted board entry texts
     */
    public List<String> getBoardEntriesFormatted() {
        List<String> toReturn = new ArrayList<>(entries.size());
        for (BoardEntry entry : entries) {
            toReturn.add(entry.getText());
        }
        return toReturn;
    }

    /**
     * Gets a board entry by its position.
     * 
     * @param position The position of the entry (0-based)
     * @return The board entry at the specified position, or null if not found
     */
    public BoardEntry getByPosition(int position) {
        if (position >= 0 && position < this.entries.size()) {
            return this.entries.get(position);
        }
        return null;
    }

    /**
     * Gets an active timer by its ID.
     * 
     * @param id The timer ID
     * @return The active timer, or null if not found or expired
     */
    public BoardTimer getTimer(String id) {
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

    /**
     * Gets all active timers for this board.
     * This method also performs periodic cleanup of expired timers.
     * 
     * @return Set of active timers
     */
    public Set<BoardTimer> getTimers() {
        // Only clean up expired timers periodically to reduce CPU usage
        long now = System.currentTimeMillis();
        if (now - lastTimerCleanup > TIMER_CLEANUP_INTERVAL) {
            this.timers.removeIf(timer -> now >= timer.getEnd());
            this.lastTimerCleanup = now;
            // Also clean timer cache
            timerCache.entrySet().removeIf(entry -> now >= entry.getValue().getEnd());
        }
        return this.timers;
    }

    /**
     * Efficiently clears all board entries and resets the board state.
     */
    public void clearAllEntries() {
        for (BoardEntry entry : entries) {
            entry.remove();
        }
        entries.clear();
        keys.clear(); // Keys will be recycled automatically
    }

    // Getter methods
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
     * @deprecated Use {@link #getTimer(String)} instead for better clarity
     */
    @Deprecated
    public BoardTimer getCooldown(String id) {
        return getTimer(id);
    }
}