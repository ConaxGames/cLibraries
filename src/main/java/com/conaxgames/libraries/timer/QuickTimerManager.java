package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.message.TimeUtil;
import com.conaxgames.libraries.util.CC;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/**
 * Simple static utility for managing string-based ability cooldowns per player. Stores expiry times
 * in a map keyed by player UUID and ability name. Unlike {@link PlayerTimer}, this does not use
 * events or scheduled tasks; expiry is checked on-demand via {@link #hasTimer}.
 * <p>
 * <b>Usage:</b> Use for simple cooldowns where you don't need the full timer API. Call
 * {@link #addTimer} to start a cooldown, {@link #hasTimer} to check if active, and
 * {@link #getRemaining} to get time left. Call {@link #clearCacheCooldown} periodically to
 * clean up expired entries.
 */
public class QuickTimerManager {

	public static HashMap<UUID, QuickTimerType> timerPlayersMap = new HashMap<>();

	/**
	 * Adds a cooldown timer for the given UUID and ability name with the specified duration (in milliseconds).
	 * Does nothing if a cooldown already exists for this ability.
	 */
	public static void addTimer(UUID uuid, String ability, long time) {
		addTimer(uuid, ability, time, false);
	}

	/**
	 * Adds a cooldown timer for the given UUID and ability name with the specified duration (in milliseconds)
	 * and announce flag. Does nothing if a cooldown already exists for this ability.
	 */
	public static void addTimer(UUID uuid, String ability, long time, boolean announce) {
		if (!timerPlayersMap.containsKey(uuid)) {
			timerPlayersMap.put(uuid, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
		}

		if (hasTimer(uuid, ability)) return;
		timerPlayersMap.get(uuid).timerMap.put(ability, new QuickTimerType(uuid, System.currentTimeMillis() + time, announce));
	}

	/**
	 * Adds a cooldown timer for the given player and ability name with the specified duration (in milliseconds).
	 */
	public static void addTimer(Player player, String ability, long time) {
		addTimer(player.getUniqueId(), ability, time, false);
	}

	/**
	 * Adds a cooldown timer for the given player and ability name with the specified duration (in milliseconds)
	 * and announce flag.
	 */
	public static void addTimer(Player player, String ability, long time, boolean announce) {
		addTimer(player.getUniqueId(), ability, time, announce);
	}

	/**
	 * Returns true if the UUID has an active cooldown for the given ability. Automatically removes
	 * expired cooldowns and returns false.
	 */
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

	/**
	 * Returns true if the player has an active cooldown for the given ability.
	 */
	public static boolean hasTimer(Player player, String ability) {
		return hasTimer(player.getUniqueId(), ability);
	}

	/**
	 * Returns true if the player has an active cooldown for the ability, and if so, sends them
	 * a formatted message with the remaining time. Useful for ability usage checks.
	 */
	public static boolean hasAndMessage(Player player, String ability) {
		if (hasTimer(player, ability)) {
			long remaining = getRemaining(player.getUniqueId(), ability);
			player.sendMessage(CC.RED + "You must wait " + CC.B_RED + TimeUtil.timeAsString(remaining) + CC.RED + " before doing this again.");
			return true;
		}
		return false;
	}

	/**
	 * Removes the cooldown timer for the given UUID and ability name. Does nothing if it doesn't exist.
	 */
	public static void removeTimer(UUID uuid, String ability) {
		if (!timerPlayersMap.containsKey(uuid)) {
			return;
		}
		if (!timerPlayersMap.get(uuid).timerMap.containsKey(ability)) {
			return;
		}
		timerPlayersMap.get(uuid).timerMap.remove(ability);
	}

	/**
	 * Removes the cooldown timer for the given player and ability name.
	 */
	public static void removeTimer(Player player, String ability) {
		removeTimer(player.getUniqueId(), ability);
	}

	/**
	 * Returns the remaining cooldown time in milliseconds for the given UUID and ability, or 0 if
	 * no cooldown exists or it has expired.
	 */
	public static long getRemaining(UUID key, String ability) {
		if (!timerPlayersMap.containsKey(key)) return 0L;
		if (!timerPlayersMap.get(key).timerMap.containsKey(ability)) return 0L;
		return timerPlayersMap.get(key).timerMap.get(ability).getTime() - System.currentTimeMillis();
	}

	/**
	 * Cleans up expired cooldown entries from the cache. Call this periodically (e.g. in a scheduled
	 * task) to prevent memory leaks from expired timers that were never checked.
	 */
	public static void clearCacheCooldown() {
		if (timerPlayersMap.isEmpty()) {
			return;
		}
		for (Iterator<UUID> it = timerPlayersMap.keySet().iterator(); it.hasNext();) {
			UUID key = it.next();
			for (Iterator<String> iter = timerPlayersMap.get(key).timerMap.keySet().iterator(); iter.hasNext();) {
				String name = iter.next();
				if (getRemaining(key, name) < 0L) {
					removeTimer(key, name);
				}
			}
		}
	}
}
