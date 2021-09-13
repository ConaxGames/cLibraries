package com.conaxgames.libraries.event;

import org.bukkit.event.Cancellable;

public class CancellableEvent extends BaseEvent implements Cancellable {

	private boolean cancelled;

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
