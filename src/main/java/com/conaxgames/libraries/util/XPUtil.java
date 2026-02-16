package com.conaxgames.libraries.util;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

/**
 * Utility for managing player experience points (XP) and levels. Provides methods to set, get,
 * and calculate experience using Minecraft's 1.8+ experience formula.
 * <p>
 * <b>Usage:</b> Create an instance with a player, then use {@link #setExp(int)} or
 * {@link #setExp(double)} to set their XP, {@link #getCurrentExp()} to get their total XP,
 * and {@link #hasExp(int)} to check if they have enough XP.
 */
public class XPUtil {
    private final Player player;
    private final String playerName;

    /**
     * Creates an XP utility instance for the given player.
     *
     * @param player The player to manage XP for
     * @throws NullPointerException if player is null
     */
    public XPUtil(final Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null");
        this.player = player;
        this.playerName = player.getName();
    }

    /**
     * Gets the player associated with this utility. Throws an exception if the player is no longer online.
     *
     * @return The player instance
     * @throws IllegalStateException if the player is not online
     */
    public Player getPlayer() {
        if (this.player == null) {
            throw new IllegalStateException("Player " + this.playerName + " is not online");
        }
        return this.player;
    }

    /**
     * Sets the player's total experience to the given amount (as an integer).
     *
     * @param amt The total experience points to set
     */
    public void setExp(final int amt) {
        this.setExp(0.0, amt);
    }

    /**
     * Sets the player's total experience to the given amount (as a double).
     *
     * @param amt The total experience points to set
     */
    public void setExp(final double amt) {
        this.setExp(0.0, amt);
    }

    private void setExp(final double base, final double amt) {
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        int amount = (int) amt;
        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;
            if (amount >= 0) {
                player.giveExp(expToLevel);
            } else {
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

    /**
     * Gets the player's current total experience points, calculated from their level and progress bar.
     *
     * @return The total experience points the player currently has
     */
    public int getCurrentExp() {
        int exp = Math.round(getExpAtLevel(player) * player.getExp());
        int currentLevel = player.getLevel();

        while (currentLevel > 0) {
            currentLevel--;
            exp += getExpAtLevel(currentLevel);
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    /**
     * Checks if the player has at least the specified amount of experience points.
     *
     * @param amt The amount of experience to check for
     * @return true if the player has at least the specified amount of XP
     */
    public boolean hasExp(final int amt) {
        return this.getCurrentExp() >= amt;
    }

    /**
     * Calculates the total experience points needed to reach the specified level from level 0.
     *
     * @param level The target level (must be >= 0)
     * @return The total experience points needed to reach that level
     * @throws IllegalArgumentException if level is negative
     */
    public int getXpNeededToLevelUp(final int level) {
        Preconditions.checkArgument(level >= 0, "Level may not be negative.");
        int currentLevel = 0;
        int exp = 0;

        while (currentLevel < level) {
            exp += getExpAtLevel(currentLevel);
            currentLevel++;
        }
        if (exp < 0) {
            exp = Integer.MAX_VALUE;
        }
        return exp;
    }

    private static int getExpAtLevel(final Player player) {
        return getExpAtLevel(player.getLevel());
    }

    /**
     * Gets the experience points required to level up from the given level to the next level.
     * Uses Minecraft's 1.8+ experience formula.
     *
     * @param level The current level
     * @return The experience points needed to reach the next level
     */
    public static int getExpAtLevel(final int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        }
        if (level <= 30) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }
}