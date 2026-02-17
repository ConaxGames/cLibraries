package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.timer.TimerClearEvent;
import com.conaxgames.libraries.event.impl.timer.TimerExtendEvent;
import com.conaxgames.libraries.event.impl.timer.TimerPauseEvent;
import com.conaxgames.libraries.event.impl.timer.TimerStartEvent;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Base class for timers that track per-player cooldowns. Manages {@link TimerCooldown} instances
 * keyed by player UUID and fires events ({@link TimerStartEvent}, {@link TimerExtendEvent},
 * {@link TimerClearEvent}, {@link TimerPauseEvent}) for lifecycle changes.
 * <p>
 * <b>Usage:</b> Extend this class and implement any custom behavior. Use {@link #setCooldown} to
 * start or extend a cooldown, {@link #getRemaining} to check time left, and {@link #clearCooldown}
 * to remove a cooldown early.
 */
public abstract class PlayerTimer extends Timer {

	protected final Map<UUID, TimerCooldown> cooldowns = new ConcurrentHashMap<>();

	/**
	 * Creates a player timer with the given name and default cooldown duration (in milliseconds).
	 */
	public PlayerTimer(String name, long defaultCooldown) {
		super(name, defaultCooldown);
	}

	/**
	 * Called when a cooldown expires. Removes the cooldown from the map. Override to add custom
	 * expiry behavior (e.g. notifications, cleanup).
	 */
	protected void handleExpiry(Player player, UUID playerUUID) {
		this.cooldowns.remove(playerUUID);
	}

	/**
	 * Clears the cooldown for the given UUID. Returns the removed cooldown or null if none existed.
	 */
	public TimerCooldown clearCooldown(UUID uuid) {
		return this.clearCooldown(null, uuid);
	}

	/**
	 * Clears the cooldown for the given player. Returns the removed cooldown or null if none existed.
	 */
	public TimerCooldown clearCooldown(Player player) {
		Objects.requireNonNull(player);
		return clearCooldown(player, player.getUniqueId());
	}

	/**
	 * Clears the cooldown for the given player UUID. Cancels the scheduled task and fires
	 * {@link TimerClearEvent}. Returns the removed cooldown or null if none existed.
	 */
	public TimerCooldown clearCooldown(Player player, UUID playerUUID) {
		TimerCooldown runnable = this.cooldowns.remove(playerUUID);
		if (runnable != null) {
			runnable.cancel();
			if (player == null) {
				LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().callEvent(new TimerClearEvent(playerUUID, this));
			} else {
				LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().callEvent(new TimerClearEvent(player, this));
			}
		}

		return runnable;
	}

	/**
	 * Returns true if the player's cooldown is currently paused.
	 */
	public boolean isPaused(Player player) {
		return this.isPaused(player.getUniqueId());
	}

	/**
	 * Returns true if the player UUID's cooldown is currently paused.
	 */
	public boolean isPaused(UUID playerUUID) {
		TimerCooldown runnable = this.cooldowns.get(playerUUID);
		return runnable != null && runnable.isPaused();
	}

	/**
	 * Pauses or unpauses the cooldown for the given player UUID. Fires {@link TimerPauseEvent}
	 * which can be cancelled. Does nothing if the cooldown doesn't exist or is already in the
	 * requested state.
	 */
	public void setPaused(UUID playerUUID, boolean paused) {
		TimerCooldown runnable = this.cooldowns.get(playerUUID);
		if (runnable != null && runnable.isPaused() != paused) {
			TimerPauseEvent event = new TimerPauseEvent(playerUUID, this, paused);
			LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				runnable.setPaused(paused);
			}
		}
	}

	/**
	 * Returns the remaining cooldown time in milliseconds for the player, or 0 if no cooldown exists.
	 */
	public long getRemaining(Player player) {
		return this.getRemaining(player.getUniqueId());
	}

	/**
	 * Returns the remaining cooldown time in milliseconds for the player UUID, or 0 if no cooldown exists.
	 */
	public long getRemaining(UUID playerUUID) {
		TimerCooldown runnable = this.cooldowns.get(playerUUID);
		return runnable == null ? 0L : runnable.getRemaining();
	}

	/**
	 * Sets a cooldown for the player using the default duration. Returns false if a cooldown
	 * already exists and the new duration is shorter than the remaining time.
	 */
	public boolean setCooldown(Player player, UUID playerUUID) {
		return this.setCooldown(player, playerUUID, this.defaultCooldown, false);
	}

	/**
	 * Sets a cooldown for the player with the given duration. If overwrite is false and a
	 * cooldown exists with more time remaining, returns false. Otherwise extends or starts
	 * the cooldown and returns true.
	 */
	public boolean setCooldown(Player player, UUID playerUUID, long duration, boolean overwrite) {
		return this.setCooldown(player, playerUUID, duration, overwrite, null);
	}

	/**
	 * Sets a cooldown for the player with the given duration and optional predicate. If duration
	 * is 0 or less, clears the cooldown. Otherwise:
	 * <ul>
	 *   <li>If a cooldown exists: fires {@link TimerExtendEvent} and extends if not cancelled</li>
	 *   <li>If no cooldown exists: fires {@link TimerStartEvent} and creates a new cooldown if not cancelled</li>
	 * </ul>
	 * The predicate can prevent extension by returning false when tested with the current remaining time.
	 * Returns false if the event is cancelled, the predicate rejects, or overwrite is false and
	 * duration is less than remaining time.
	 *
	 * @param player the player (may be null)
	 * @param playerUUID the player UUID
	 * @param duration cooldown duration in milliseconds
	 * @param overwrite if false, won't reduce an existing cooldown
	 * @param currentCooldownPredicate optional predicate that must return true to extend (tested with current remaining time)
	 * @return true if the cooldown was set/extended, false otherwise
	 */
	public boolean setCooldown(Player player, UUID playerUUID, long duration, boolean overwrite,
	                           Predicate<Long> currentCooldownPredicate) {
		TimerCooldown runnable = duration > 0L ? this.cooldowns.get(playerUUID) : this.clearCooldown(player, playerUUID);
		if (runnable != null) {
			long remaining = runnable.getRemaining();
			if (!overwrite && remaining > 0L && duration <= remaining) {
				return false;
			}

			TimerExtendEvent event = new TimerExtendEvent(player, playerUUID, this, remaining, duration);
			LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}

			boolean flag = true;
			if (currentCooldownPredicate != null) {
				flag = currentCooldownPredicate.test(remaining);
			}

			if (flag) {
				runnable.setRemaining(duration);
			}

			return flag;
		} else {
			TimerStartEvent event = new TimerStartEvent(player, playerUUID, this, duration);
			LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}

			runnable = new TimerCooldown(this, playerUUID, duration);
		}

		this.cooldowns.put(playerUUID, runnable);
		return true;
	}

	/**
	 * Returns the map of all active cooldowns keyed by player UUID.
	 */
	public Map<UUID, TimerCooldown> getCooldowns() {
		return this.cooldowns;
	}
}