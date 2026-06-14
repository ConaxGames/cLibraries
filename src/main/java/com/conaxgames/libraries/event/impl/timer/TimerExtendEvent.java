package com.conaxgames.libraries.event.impl.timer;

import com.conaxgames.libraries.timer.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class TimerExtendEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final @Nullable Player player;
    private final @Nullable UUID userUUID;
    private final Timer timer;
    private final long previousDuration;
    private long newDuration;
    private boolean cancelled;

    public TimerExtendEvent(@Nullable Player player, @Nullable UUID userUUID,
                            Timer timer, long previousDuration, long newDuration) {
        this.player = player;
        this.userUUID = userUUID;
        this.timer = timer;
        this.previousDuration = previousDuration;
        this.newDuration = newDuration;
    }

    public @Nullable Player getPlayer()  { return player; }
    public @Nullable UUID getUserUUID()  { return userUUID; }
    public Timer getTimer()              { return timer; }
    public long getPreviousDuration()    { return previousDuration; }
    public long getNewDuration()         { return newDuration; }
    public void setNewDuration(long d)   { this.newDuration = d; }

    @Override public HandlerList getHandlers()     { return HANDLERS; }
    public static HandlerList getHandlerList()     { return HANDLERS; }
    @Override public boolean isCancelled()         { return cancelled; }
    @Override public void setCancelled(boolean c)  { this.cancelled = c; }
}
