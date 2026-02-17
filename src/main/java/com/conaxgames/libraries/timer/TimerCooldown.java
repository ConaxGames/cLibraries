package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.timer.TimerExpireEvent;
import com.conaxgames.libraries.util.scheduler.Scheduler;
import lombok.Getter;

import java.util.UUID;

/**
 * Represents a single cooldown instance for a timer. Tracks expiry time, pause state, and schedules
 * the expiry task. When the cooldown expires, fires {@link TimerExpireEvent} and calls
 * {@link PlayerTimer#handleExpiry} if applicable.
 */
public class TimerCooldown {

	@Getter
	private final Timer timer;
	private final UUID owner;
	private Scheduler.CancellableTask scheduledTask = null;
	@Getter
	private long expiryMillis;

	private long pauseMillis;

	/**
	 * Creates a cooldown for the given timer and player UUID with the specified duration (in milliseconds).
	 */
	protected TimerCooldown(Timer timer, UUID playerUUID, long duration) {
		this.timer = timer;
		this.owner = playerUUID;
		this.setRemaining(duration);
	}

	/**
	 * Returns the remaining cooldown time in milliseconds, accounting for pause state.
	 */
	public long getRemaining() {
		return this.getRemaining(false);
	}

	/**
	 * Sets the remaining cooldown duration in milliseconds. Cancels any existing scheduled task
	 * and schedules a new expiry task. If duration is 0 or less, cancels the cooldown.
	 */
	protected void setRemaining(long milliseconds) throws IllegalStateException {
		if (milliseconds <= 0L) {
			this.cancel();
			return;
		}

		long expiryMillis = System.currentTimeMillis() + milliseconds;
		if (expiryMillis != this.expiryMillis) {
			this.expiryMillis = expiryMillis;
			this.cancel();

			long ticks = milliseconds / 50L;
			Scheduler scheduler = LibraryPlugin.getInstance().getScheduler();
			
			this.scheduledTask = scheduler.runTaskLaterCancellable(LibraryPlugin.getInstance().getPlugin(), () -> {
				if (TimerCooldown.this.timer instanceof PlayerTimer && owner != null) {
					((PlayerTimer) timer).handleExpiry(
							LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(TimerCooldown.this.owner), TimerCooldown.this.owner);
				}

				LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().callEvent(
						new TimerExpireEvent(TimerCooldown.this.owner, TimerCooldown.this.timer));
				
				TimerCooldown.this.scheduledTask = null;
			}, ticks);
		}
	}

	/**
	 * Returns the remaining cooldown time. If ignorePaused is false and the cooldown is paused,
	 * returns the paused remaining time. Otherwise returns time until expiry.
	 */
	protected long getRemaining(boolean ignorePaused) {
		if (!ignorePaused && this.pauseMillis != 0L) {
			return this.pauseMillis;
		} else {
			return this.expiryMillis - System.currentTimeMillis();
		}
	}

	/**
	 * Returns true if this cooldown is currently paused.
	 */
	protected boolean isPaused() {
		return this.pauseMillis != 0L;
	}

	/**
	 * Pauses or unpauses the cooldown. When paused, the remaining time is stored and the expiry
	 * task is cancelled. When unpaused, a new expiry task is scheduled with the stored remaining time.
	 */
	public void setPaused(boolean paused) {
		if (paused != this.isPaused()) {
			if (paused) {
				this.pauseMillis = this.getRemaining(true);
				this.cancel();
			} else {
				this.setRemaining(this.pauseMillis);
				this.pauseMillis = 0L;
			}
		}
	}

	/**
	 * Cancels the scheduled expiry task. Safe to call multiple times.
	 */
	protected void cancel() throws IllegalStateException {
		if (this.scheduledTask != null) {
			this.scheduledTask.cancel();
			this.scheduledTask = null;
		}
	}
}
