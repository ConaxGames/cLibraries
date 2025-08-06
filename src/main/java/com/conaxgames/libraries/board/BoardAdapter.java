package com.conaxgames.libraries.board;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

/**
 * Interface for providing scoreboard content and behavior.
 * Implementations of this interface define how scoreboards are created and updated.
 * 
 * @author ConaxGames
 * @since 1.0
 */
public interface BoardAdapter {

    /**
     * Gets the scoreboard lines for a specific player.
     * 
     * @param player The player to get scoreboard lines for
     * @param board The board instance for this player
     * @return List of scoreboard lines (will be displayed in reverse order)
     */
    List<String> getScoreboard(Player player, Board board);

    /**
     * Gets the title for the scoreboard.
     * 
     * @param player The player to get the title for
     * @return The scoreboard title
     */
    String getTitle(Player player);

    /**
     * Gets the update interval for the scoreboard in ticks.
     * 
     * @return Update interval in ticks
     */
    long getInterval();

    /**
     * Called when a scoreboard is created for a player.
     * This method is called after the scoreboard is set up but before it's applied to the player.
     * 
     * @param player The player the scoreboard is being created for
     * @param board The scoreboard instance
     */
    void onScoreboardCreate(Player player, Scoreboard board);

    /**
     * Called before the main scoreboard update loop begins.
     * This method is called once per update cycle before processing all players.
     */
    void preLoop();

}