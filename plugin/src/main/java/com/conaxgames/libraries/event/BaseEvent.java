package com.conaxgames.libraries.event;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean call() {
		LibraryPlugin.getInstance().getServer().getPluginManager().callEvent(this);
		return this instanceof Cancellable && ((Cancellable) this).isCancelled();
	}

}
