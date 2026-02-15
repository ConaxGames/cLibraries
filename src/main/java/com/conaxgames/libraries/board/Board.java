package com.conaxgames.libraries.board;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Per-player sidebar scoreboard. Holds the Bukkit {@link #getScoreboard() scoreboard} and
 * {@link #getObjective() objective}, and a list of {@link BoardEntry} lines. Used by
 * {@link BoardManager} to sync content from {@link BoardAdapter#getScoreboard}.
 * <p>
 * <b>Keys:</b> Each line uses a unique key (via {@link #getNewKey}) for team/score display;
 * {@link #getUsedKeys()} tracks them for reuse. Optional {@link BoardTimer}s can be stored
 * with {@link #addTimer} and {@link #getTimer} for countdown display in adapter lines.
 */
public class Board {

    private static final String OBJECTIVE_NAME = "sb";
    private static final String[] KEYS;
    static {
        ChatColor[] colors = ChatColor.values();
        KEYS = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            KEYS[i] = colors[i].toString() + ChatColor.WHITE;
        }
    }

    @Getter
    private final List<BoardEntry> entries = new ArrayList<>();
    private final Map<String, BoardTimer> timers = new HashMap<>();
    @Getter
    private final Map<String, String> usedKeys = new HashMap<>();
    @Getter
    private final Scoreboard scoreboard;
    @Getter
    private final Objective objective;

    /**
     * Creates a board for the player using the adapter's title. Uses a new scoreboard if the
     * player is on the main scoreboard, otherwise reuses the player's current one.
     */
    public Board(Player player, BoardAdapter adapter) {
        ScoreboardManager sm = LibraryPlugin.getInstance().getPlugin().getServer().getScoreboardManager();
        this.scoreboard = player.getScoreboard().equals(sm.getMainScoreboard())
            ? sm.getNewScoreboard()
            : player.getScoreboard();
        String titleStr = adapter.getTitle(player);
        Component title = titleStr != null
            ? LegacyComponentSerializer.legacySection().deserialize(titleStr)
            : Component.empty();
        this.objective = this.scoreboard.registerNewObjective(OBJECTIVE_NAME, Criteria.DUMMY, title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    /**
     * Allocates a unique key for a new entry based on the entry text and used keys. Throws
     * if no key is available. Used by {@link BoardEntry} constructor.
     */
    public String getNewKey(BoardEntry entry) {
        String text = entry.getText();
        String suffix = text.length() > 16 ? ChatColor.getLastColors(text.substring(0, 16)) : "";
        for (String base : KEYS) {
            String key = base + suffix;
            if (!usedKeys.containsKey(key)) {
                usedKeys.put(key, text);
                return key;
            }
        }
        throw new IllegalStateException("No free board entry keys");
    }

    /**
     * Returns the timer with the given id, or null if missing or {@link BoardTimer#isExpired}.
     * Expired timers are removed from the map.
     */
    public BoardTimer getTimer(String id) {
        BoardTimer t = timers.get(id);
        if (t == null || t.isExpired()) {
            if (t != null) timers.remove(id);
            return null;
        }
        return t;
    }

    /**
     * Stores a timer by id for use in adapter lines (e.g. countdown display via {@link BoardTimer#getFormattedString}).
     */
    public void addTimer(BoardTimer timer) {
        timers.put(timer.getId(), timer);
    }

    /**
     * Removes the timer with the given id.
     */
    public void removeTimer(String id) {
        timers.remove(id);
    }

    /**
     * Removes all entries from the scoreboard and clears used keys. Called by the manager when
     * lines are null/empty or when the board is removed.
     */
    public void clearAllEntries() {
        for (BoardEntry e : entries) e.remove();
        entries.clear();
        usedKeys.clear();
    }
}
