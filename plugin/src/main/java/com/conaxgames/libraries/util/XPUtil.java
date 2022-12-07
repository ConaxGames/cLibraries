package com.conaxgames.libraries.util;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class XPUtil {
    private final Player player;
    private final String playerName;

    public XPUtil(final Player player) {
        Preconditions.checkNotNull((Object) player, (Object) "Player cannot be null");
        this.player =player;
        this.playerName = player.getName();
    }

    public Player getPlayer() {
        final Player p = this.player;
        if (p == null) {
            throw new IllegalStateException("Player " + this.playerName + " is not online");
        }
        return p;
    }

    private static int calculateLevelForExp(final int exp) {
        int level = 0;
        for (int curExp = 7, incr = 10; curExp <= exp; curExp += incr, ++level, incr += ((level % 2 == 0) ? 3 : 4)) {
        }
        return level;
    }

    public void setExp(final int amt) {
        this.setExp(0.0, amt);
    }

    public void setExp(final double amt) {
        this.setExp(0.0, amt);
    }

    // credit: essentials
    private void setExp(final double base, final double amt) {
        player.setExp(0);
        player.setLevel(0);
        player.setTotalExperience(0);

        //This following code is technically redundant now, as bukkit now calulcates levels more or less correctly
        //At larger numbers however... player.getExp(3000), only seems to give 2999, putting the below calculations off.
        int amount = (int) amt;
        while (amount > 0) {
            final int expToLevel = getExpAtLevel(player);
            amount -= expToLevel;
            if (amount >= 0) {
                // give until next level
                player.giveExp(expToLevel);
            } else {
                // give the rest
                amount += expToLevel;
                player.giveExp(amount);
                amount = 0;
            }
        }
    }

    // credit: essentials
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

    // credit essentials
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

    //new Exp Math from 1.8
    public static int getExpAtLevel(final int level) {
        if (level <= 15) {
            return (2 * level) + 7;
        }
        if ((level >= 16) && (level <= 30)) {
            return (5 * level) - 38;
        }
        return (9 * level) - 158;

    }
}