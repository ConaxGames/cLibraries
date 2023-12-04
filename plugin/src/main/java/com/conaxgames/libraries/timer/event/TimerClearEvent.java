package com.conaxgames.libraries.timer.event;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.timer.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class TimerClearEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();
	private final Optional<UUID> userUUID;
	private final Timer timer;
	private Optional<Player> player;

	public TimerClearEvent(Timer timer) {
		this.userUUID = Optional.empty();
		this.timer = timer;
	}

	public TimerClearEvent(UUID userUUID, Timer timer) {
		this.userUUID = Optional.of(userUUID);
		this.timer = timer;
	}

	public TimerClearEvent(Player player, Timer timer) {
		Objects.requireNonNull(player);

		this.player = Optional.of(player);
		this.userUUID = Optional.of(player.getUniqueId());
		this.timer = timer;
	}

	public static HandlerList getHandlerList() {
		return TimerClearEvent.HANDLERS;
	}

	public Optional<Player> getPlayer() {
		if (player == null) {
			player = this.userUUID.isPresent() ?
					Optional.ofNullable(LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(this.userUUID.get())) : Optional.empty();
		}

		return player;
	}

	public Optional<UUID> getUserUUID() {
		return this.userUUID;
	}

	public Timer getTimer() {
		return this.timer;
	}

	@Override
	public HandlerList getHandlers() {
		return TimerClearEvent.HANDLERS;
	}
}
