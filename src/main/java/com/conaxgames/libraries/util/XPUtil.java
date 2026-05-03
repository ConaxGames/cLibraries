package com.conaxgames.libraries.util;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

public class XPUtil {
    private final Player player;
    private final String playerName;

    public XPUtil(final Player player) {
        Preconditions.checkNotNull(player, "Player cannot be null");
        this.player = player;
        this.playerName = player.getName();
    }

    private static int getExpAtLevel(final Player player) {
        return getExpAtLevel(player.getLevel());
    }

    public static int getExpAtLevel(final int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        }
        if (level <= 30) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;
    }

    public Player getPlayer() {
        if (this.player == null) {
            throw new IllegalStateException("Player " + this.playerName + " is not online");
        }
        return this.player;
    }

    public void setExp(final int amt) {
        this.setExp(0.0, amt);
    }

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

    public boolean hasExp(final int amt) {
        return this.getCurrentExp() >= amt;
    }

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
}