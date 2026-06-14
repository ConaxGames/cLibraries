package com.conaxgames.libraries.event.impl.timer;

import com.conaxgames.libraries.timer.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class TimerStartEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final @Nullable Player player;
    private final @Nullable UUID userUUID;
    private final Timer timer;
    private final long duration;
    private boolean cancelled;

    public TimerStartEvent(@Nullable Player player, @Nullable UUID userUUID, Timer timer, long duration) {
        this.player = player;
        this.userUUID = userUUID;
        this.timer = timer;
        this.duration = duration;
    }

    public @Nullable Player getPlayer()  { return player; }
    public @Nullable UUID getUserUUID()  { return userUUID; }
    public Timer getTimer()              { return timer; }
    public long getDuration()            { return duration; }

    @Override public HandlerList getHandlers()     { return HANDLERS; }
    public static HandlerList getHandlerList()     { return HANDLERS; }
    @Override public boolean isCancelled()         { return cancelled; }
    @Override public void setCancelled(boolean c)  { this.cancelled = c; }
}
