package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Manages registration and retrieval of {@link Timer} instances. Automatically registers timers
 * as event listeners if they implement {@link Listener}. Use {@link #registerTimer} to add timers
 * and {@link #getTimer} to retrieve them by class.
 * <p>
 * <b>Usage:</b> Access via {@link com.conaxgames.libraries.LibraryPlugin#getTimerManager}. Register
 * your timer implementations during plugin initialization.
 */
@Getter
public class TimerManager implements Listener {
	
	private final Set<Timer> timers = new LinkedHashSet<>();

	/**
	 * Registers a timer instance. If the timer implements {@link Listener}, it is automatically
	 * registered as an event listener. Use {@link #getTimer} to retrieve it later.
	 */
	public void registerTimer(Timer timer) {
		this.timers.add(timer);
		if (timer instanceof Listener) {
			LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().registerEvents((Listener) timer, LibraryPlugin.getInstance().getPlugin());
		}
	}

	/**
	 * Unregisters a timer instance. Does not unregister it as an event listener if it was one.
	 */
	public void unregisterTimer(Timer timer) {
		this.timers.remove(timer);
	}

	/**
	 * Returns the timer instance of the given class, or null if not registered. Uses type-safe
	 * casting via {@link Class#isInstance} and {@link Class#cast}.
	 *
	 * @param timerClass the timer class to find
	 * @param <T> the timer type
	 * @return the timer instance or null if not found
	 */
	public <T extends Timer> T getTimer(Class<T> timerClass) {
		for (Timer timer : this.timers) {
			if (timerClass.isInstance(timer)) {
				return timerClass.cast(timer);
			}
		}
		return null;
	}
}
