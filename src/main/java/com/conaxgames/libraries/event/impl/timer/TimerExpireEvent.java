package com.conaxgames.libraries.event.impl.timer;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.timer.Timer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class TimerExpireEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final @Nullable UUID userUUID;
    private final Timer timer;
    private @Nullable Player player;

    public TimerExpireEvent(@Nullable UUID userUUID, Timer timer) {
        this.userUUID = userUUID;
        this.timer = timer;
    }

    public @Nullable Player getPlayer() {
        if (player == null && userUUID != null) {
            player = LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(userUUID);
        }
        return player;
    }

    public @Nullable UUID getUserUUID()  { return userUUID; }
    public Timer getTimer()              { return timer; }

    @Override public HandlerList getHandlers()  { return HANDLERS; }
    public static HandlerList getHandlerList()  { return HANDLERS; }
}
