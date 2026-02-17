package com.conaxgames.libraries.timer;

import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;

/**
 * Internal data structure for {@link QuickTimerManager}. Stores a player UUID, expiry time (in milliseconds),
 * announce flag, and a map of ability names to their timer instances. Used to organize multiple
 * ability cooldowns per player.
 */
@Getter
public class QuickTimerType {

	public UUID player;
	public long time;
	private final boolean announce;
	public HashMap<String, QuickTimerType> timerMap = new HashMap<>();

	/**
	 * Creates a timer type for the given player UUID with the specified expiry time (in milliseconds)
	 * and announce flag.
	 */
	public QuickTimerType(UUID player, long time, boolean announce) {
		this.player = player;
		this.time = time;
		this.announce = announce;
	}
}
