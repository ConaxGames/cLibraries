package com.conaxgames.libraries.timer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import com.conaxgames.libraries.message.TimeUtil;
import com.conaxgames.libraries.util.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class QuickTimerManager {

    public static HashMap<UUID, QuickTimerType> timerPlayersMap = new HashMap<>();
    /*
     * Add Timer begins
     */
    public static void addTimer(UUID uuid, String ability, long time) {
        if(!timerPlayersMap.containsKey(uuid)) {
            timerPlayersMap.put(uuid, new QuickTimerType(uuid, System.currentTimeMillis() + time, false));
        }

        if(hasTimer(uuid, ability)) return;
        timerPlayersMap.get(uuid).timerMap.put(ability, new QuickTimerType(uuid, System.currentTimeMillis() + time, false));
    }

    public static void addTimer(UUID uuid, String ability, long time, boolean announce) {
        if(!timerPlayersMap.containsKey(uuid)) {
            timerPlayersMap.put(uuid, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
        }

        if(hasTimer(uuid, ability)) return;
        timerPlayersMap.get(uuid).timerMap.put(ability, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
    }

    public static void addTimer(Player player, String ability, long time) {
        UUID uuid = player.getUniqueId();
        if(!timerPlayersMap.containsKey(uuid)) {
            timerPlayersMap.put(uuid, new QuickTimerType(uuid, System.currentTimeMillis() + time, false));
        }

        if(hasTimer(uuid, ability)) return;
        timerPlayersMap.get(uuid).timerMap.put(ability, new QuickTimerType(uuid, System.currentTimeMillis() + time, false));
    }

    public static void addTimer(Player player, String ability, long time, boolean announce) {
        UUID uuid = player.getUniqueId();
        if(!timerPlayersMap.containsKey(uuid)) {
            timerPlayersMap.put(uuid, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
        }

        if(hasTimer(uuid, ability)) return;
        timerPlayersMap.get(uuid).timerMap.put(ability, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
    }

    /*
     * Add Timer ends
     */

    /*
     * Contains Timer starts
     */
    public static boolean hasTimer(UUID uuid, String ability) {
        if(!timerPlayersMap.containsKey(uuid)) {
            return false;
        }

        if(!timerPlayersMap.get(uuid).timerMap.containsKey(ability)) {
            return false;
        }

        if(QuickTimerManager.getRemaining(uuid, ability) <= 0L) {
            QuickTimerManager.removeTimer(uuid, ability);
            return false;
        }
        return true;
    }

    public static boolean hasAndMessage(Player player, String ability) {
        if(hasTimer(player, ability)) {
            long remaining = QuickTimerManager.getRemaining(player.getUniqueId(), ability);
            player.sendMessage(CC.RED + "You must wait " + CC.B_RED + TimeUtil.timeAsString(remaining) + CC.RED + "before doing this again.");
            return true;
        }
        return false;
    }

    public static boolean hasTimer(Player player, String ability) {
        UUID uuid = player.getUniqueId();
        if(!timerPlayersMap.containsKey(uuid)) {
            return false;
        }

        if(!timerPlayersMap.get(uuid).timerMap.containsKey(ability)) {
            return false;
        }

        if(QuickTimerManager.getRemaining(uuid, ability) <= 0L) {
            QuickTimerManager.removeTimer(uuid, ability);
            return false;
        }
        return true;
    }
    /*
     * Contains Timer ends
     */

    /*
     * Remove Timer starts
     */

    public static void removeTimer(UUID uuid, String ability) {
        if (!timerPlayersMap.containsKey(uuid)) {
            return;
        }
        if (!timerPlayersMap.get(uuid).timerMap.containsKey(ability)) {
            return;
        }
        QuickTimerType timerType = timerPlayersMap.get(uuid);

        timerType.getTimerMap().remove(ability);

    }

    public static void removeTimer(Player player, String ability) {
        UUID uuid = player.getUniqueId();
        if (!timerPlayersMap.containsKey(uuid)) {
            return;
        }
        if (!timerPlayersMap.get(uuid).timerMap.containsKey(ability)) {
            return;
        }
        QuickTimerType timerType = timerPlayersMap.get(uuid);

        timerType.getTimerMap().remove(ability);
    }
    /*
     * Remove Timer ends
     */

    public static long getRemaining(UUID key, String ability) {
        if(!timerPlayersMap.containsKey(key)) return 0L;
        if(!timerPlayersMap.get(key).timerMap.containsKey(ability)) return 0L;
        return (timerPlayersMap.get(key).timerMap.get(ability)).getTime() - System.currentTimeMillis();
    }

    public static void clearCacheCooldown() {
        if(timerPlayersMap.isEmpty()) {
            return;
        }
        for(Iterator<UUID> it = timerPlayersMap.keySet().iterator(); it.hasNext();) {
            UUID key = it.next();
            for(Iterator<String> iter = timerPlayersMap.get(key).timerMap.keySet().iterator(); iter.hasNext();) {
                String name = iter.next();
                if(getRemaining(key, name) < 0.0) {
                    removeTimer(key, name);
                }
            }
        }
    }

}
