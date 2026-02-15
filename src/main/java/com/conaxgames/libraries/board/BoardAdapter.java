package com.conaxgames.libraries.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

/**
 * Supplies per-player scoreboard content for {@link BoardManager}. The manager runs at
 * {@link #getInterval()} ticks and uses {@link #getTitle(Player)} and {@link #getScoreboard(Player, Board)}
 * to build the sidebar; lines are shown in reverse order (first list element = bottom line).
 * <p>
 * <b>Registration:</b> Implement this interface and pass it to {@link BoardManager}; then call
 * {@link com.conaxgames.libraries.LibraryPlugin#setBoardManager} so the manager is scheduled.
 */
public interface BoardAdapter {

    /**
     * Returns the lines to show on the player's sidebar (bottom to top). Null or empty clears the board.
     * Use for dynamic content (e.g. stats, placeholders). Called every tick interval.
     */
    List<String> getScoreboard(Player player, Board board);

    /**
     * Returns the sidebar title for the player. Null is treated as empty. Called every tick interval.
     */
    String getTitle(Player player);

    /**
     * Update interval in ticks (20 ticks = 1 second). Used by the library when scheduling the board task.
     */
    long getInterval();

    /**
     * Called when the player's scoreboard is first assigned (or reassigned) by the manager. Use to run
     * one-off setup when the board becomes visible.
     */
    default void onScoreboardCreate(Player player, Scoreboard board) {
    }

    /**
     * Called once per tick before updating any player's board. Use for shared pre-computation or caching.
     */
    default void preLoop() {
    }
}
