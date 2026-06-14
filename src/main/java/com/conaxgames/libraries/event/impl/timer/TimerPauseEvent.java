package com.conaxgames.libraries.event.impl.timer;

import com.conaxgames.libraries.timer.Timer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class TimerPauseEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final @Nullable UUID userUUID;
    private final Timer timer;
    private final boolean paused;
    private boolean cancelled;

    public TimerPauseEvent(@Nullable UUID userUUID, Timer timer, boolean paused) {
        this.userUUID = userUUID;
        this.timer = timer;
        this.paused = paused;
    }

    public @Nullable UUID getUserUUID()  { return userUUID; }
    public Timer getTimer()              { return timer; }
    public boolean isPaused()            { return paused; }

    @Override public HandlerList getHandlers()     { return HANDLERS; }
    public static HandlerList getHandlerList()     { return HANDLERS; }
    @Override public boolean isCancelled()         { return cancelled; }
    @Override public void setCancelled(boolean c)  { this.cancelled = c; }
}
