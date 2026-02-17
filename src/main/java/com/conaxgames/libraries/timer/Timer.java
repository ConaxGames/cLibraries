package com.conaxgames.libraries.timer;

import lombok.Getter;

/**
 * Base class for timer implementations. Provides a name and default cooldown duration.
 * Extend this class to create custom timer types (e.g. {@link PlayerTimer} for per-player cooldowns).
 * <p>
 * <b>Registration:</b> Register timer instances with {@link TimerManager#registerTimer} so they can
 * be retrieved via {@link TimerManager#getTimer}.
 */
public abstract class Timer {

	protected final String name;
	@Getter
	protected final long defaultCooldown;

	/**
	 * Creates a timer with the given name and default cooldown duration (in milliseconds).
	 */
	public Timer(String name, long defaultCooldown) {
		this.name = name;
		this.defaultCooldown = defaultCooldown;
	}

	/**
	 * Returns the display name of this timer.
	 */
	public final String getDisplayName() {
		return this.name;
	}
}