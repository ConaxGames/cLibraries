package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.message.TimeUtil;
import com.conaxgames.libraries.util.CC;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class QuickTimerManager {

    public static HashMap<UUID, QuickTimerType> timerPlayersMap = new HashMap<>();

    public static void addTimer(UUID uuid, String ability, long time) {
        addTimer(uuid, ability, time, false);
    }

    public static void addTimer(UUID uuid, String ability, long time, boolean announce) {
        if (!timerPlayersMap.containsKey(uuid)) {
            timerPlayersMap.put(uuid, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
        }

        if (hasTimer(uuid, ability)) return;
        timerPlayersMap.get(uuid).timerMap.put(ability, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
    }

    public static void addTimer(Player player, String ability, long time) {
        addTimer(player.getUniqueId(), ability, time, false);
    }

    public static void addTimer(Player player, String ability, long time, boolean announce) {
        addTimer(player.getUniqueId(), ability, time, announce);
    }

    public static boolean hasTimer(UUID uuid, String ability) {
        if (!timerPlayersMap.containsKey(uuid)) {
            return false;
        }

        if (!timerPlayersMap.get(uuid).timerMap.containsKey(ability)) {
            return false;
        }

        if (getRemaining(uuid, ability) <= 0L) {
            removeTimer(uuid, ability);
            return false;
        }
        return true;
    }

    public static boolean hasTimer(Player player, String ability) {
        return hasTimer(player.getUniqueId(), ability);
    }

    public static boolean hasAndMessage(Player player, String ability) {
        if (hasTimer(player, ability)) {
            long remaining = getRemaining(player.getUniqueId(), ability);
            player.sendMessage(CC.RED + "You must wait " + CC.B_RED + TimeUtil.timeAsString(remaining) + CC.RED + " before doing this again.");
            return true;
        }
        return false;
    }

    public static void removeTimer(UUID uuid, String ability) {
        if (!timerPlayersMap.containsKey(uuid)) {
            return;
        }
        if (!timerPlayersMap.get(uuid).timerMap.containsKey(ability)) {
            return;
        }
        timerPlayersMap.get(uuid).timerMap.remove(ability);
    }

    public static void removeTimer(Player player, String ability) {
        removeTimer(player.getUniqueId(), ability);
    }

    public static long getRemaining(UUID key, String ability) {
        if (!timerPlayersMap.containsKey(key)) return 0L;
        if (!timerPlayersMap.get(key).timerMap.containsKey(ability)) return 0L;
        return timerPlayersMap.get(key).timerMap.get(ability).getTime() - System.currentTimeMillis();
    }

    public static void clearCacheCooldown() {
        if (timerPlayersMap.isEmpty()) {
            return;
        }
        for (Iterator<UUID> it = timerPlayersMap.keySet().iterator(); it.hasNext(); ) {
            UUID key = it.next();
            for (Iterator<String> iter = timerPlayersMap.get(key).timerMap.keySet().iterator(); iter.hasNext(); ) {
                String name = iter.next();
                if (getRemaining(key, name) < 0L) {
                    removeTimer(key, name);
                }
            }
        }
    }
}
