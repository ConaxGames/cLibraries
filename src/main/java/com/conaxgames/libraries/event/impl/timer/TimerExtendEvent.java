package com.conaxgames.libraries.event.impl.timer;

import com.conaxgames.libraries.timer.Timer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public class TimerExtendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    @Getter
    private final Optional<Player> player;
    @Getter
    private final Optional<UUID> userUUID;
    @Getter
    private final Timer timer;
    @Getter
    private final long previousDuration;
    private boolean cancelled;
    @Setter
    @Getter
    private long newDuration;

    public TimerExtendEvent(Timer timer, long previousDuration, long newDuration) {
        this.player = Optional.empty();
        this.userUUID = Optional.empty();
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }

    public TimerExtendEvent(@NotNull Player player, UUID uniqueId, Timer timer, long previousDuration,
                            long newDuration) {
        this.player = Optional.of(player);
        this.userUUID = Optional.ofNullable(uniqueId);
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }

    public static HandlerList getHandlerList() {
        return TimerExtendEvent.HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return TimerExtendEvent.HANDLERS;
    }
}
