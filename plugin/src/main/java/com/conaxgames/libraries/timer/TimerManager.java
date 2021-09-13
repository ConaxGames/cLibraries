package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.event.Listener;

import java.util.LinkedHashSet;
import java.util.Set;

public class TimerManager implements Listener {
	
	private final Set<Timer> timers = new LinkedHashSet<>();

	public TimerManager() {
	}

	public void registerTimer(Timer timer) {
		this.timers.add(timer);
		if (timer instanceof Listener) {
			LibraryPlugin.getInstance().getServer().getPluginManager().registerEvents((Listener) timer, LibraryPlugin.getInstance());
		}
	}

	public void unregisterTimer(Timer timer) {
		this.timers.remove(timer);
	}

	public <T extends Timer> T getTimer(Class<T> timerClass) {
		for (Timer timer : this.timers) {
			if (timer.getClass().equals(timerClass)) {
				return (T) timer;
			}
		}

		return null;
	}

	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof TimerManager)) return false;
		final TimerManager other = (TimerManager) o;
		if (!other.canEqual((Object) this)) return false;
		final Object this$timers = this.getTimers();
		final Object other$timers = other.getTimers();
		if (this$timers == null ? other$timers != null : !this$timers.equals(other$timers)) return false;
		return true;
	}

	protected boolean canEqual(final Object other) {
		return other instanceof TimerManager;
	}

	public int hashCode() {
		final int PRIME = 59;
		int result = 1;
		final Object $timers = this.getTimers();
		result = result * PRIME + ($timers == null ? 43 : $timers.hashCode());
		return result;
	}

	public String toString() {
		return "TimerManager(timers=" + this.getTimers() + ")";
	}

	public Set<Timer> getTimers() {
		return this.timers;
	}
}
