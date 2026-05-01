package com.conaxgames.libraries.event.impl.timer;

import com.conaxgames.libraries.timer.Timer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class TimerStartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Optional<Player> player;
    @Getter
    private final Optional<UUID> userUUID;
    @Getter
    private final Timer timer;
    @Getter
    private final long duration;

    private boolean cancelled;

    public TimerStartEvent(Timer timer, final long duration) {
        this.player = Optional.empty();
        this.userUUID = Optional.empty();
        this.timer = timer;
        this.duration = duration;
    }

    public TimerStartEvent(@NotNull Player player, UUID uniqueId, Timer timer, long duration) {
        this.player = Optional.of(player);
        this.userUUID = Optional.ofNullable(uniqueId);
        this.timer = timer;
        this.duration = duration;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
