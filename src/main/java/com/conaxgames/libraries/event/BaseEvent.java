package com.conaxgames.libraries.event;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

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
		JavaPlugin plugin = LibraryPlugin.getInstance().getPlugin();

		if (plugin == null) {
			Bukkit.getServer().getPluginManager().callEvent(this);
		} else {
			LibraryPlugin.getInstance().getScheduler().runTask(plugin, () -> 
				Bukkit.getServer().getPluginManager().callEvent(this)
			);
		}

		return this instanceof Cancellable && ((Cancellable) this).isCancelled();
	}
}
