package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a scoreboard board for a specific player.
 * This class manages the scoreboard entries and provides utilities for board management.
 * 
 * @author ConaxGames
 * @since 1.0
 */
public class Board {

    // Constants
    private static final String OBJECTIVE_NAME = "Default";
    private static final String OBJECTIVE_CRITERIA = "dummy";

    // Pre-computed key pool for performance
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
    private final Map<String, BoardTimer> timers = new HashMap<>();
    private final Map<String, String> usedKeys = new HashMap<>();
    
    private Scoreboard scoreboard;
    private Objective objective;

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
            
            if (entry.getText().length() > 16) {
                String sub = entry.getText().substring(0, 16);
                colorText = colorText + ChatColor.getLastColors(sub);
            }
            
            if (!usedKeys.containsKey(colorText)) {
                usedKeys.put(colorText, entry.getText());
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
        BoardTimer timer = timers.get(id);
        if (timer != null && !timer.isExpired()) {
            return timer;
        }
        
        // Remove expired timer
        if (timer != null) {
            timers.remove(id);
        }
        
        return null;
    }

    /**
     * Gets all active timers for this board.
     * 
     * @return Map of active timers
     */
    public Map<String, BoardTimer> getTimers() {
        // Clean up expired timers
        timers.entrySet().removeIf(entry -> entry.getValue().isExpired());
        return this.timers;
    }

    /**
     * Adds a timer to this board.
     * 
     * @param timer The timer to add
     */
    public void addTimer(BoardTimer timer) {
        timers.put(timer.getId(), timer);
    }

    /**
     * Removes a timer from this board.
     * 
     * @param id The timer ID to remove
     */
    public void removeTimer(String id) {
        timers.remove(id);
    }

    /**
     * Efficiently clears all board entries and resets the board state.
     */
    public void clearAllEntries() {
        for (BoardEntry entry : entries) {
            entry.remove();
        }
        entries.clear();
        usedKeys.clear();
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

    public Map<String, String> getUsedKeys() {
        return this.usedKeys;
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