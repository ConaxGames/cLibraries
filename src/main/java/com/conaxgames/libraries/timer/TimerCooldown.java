package com.conaxgames.libraries.timer;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.timer.TimerExpireEvent;
import com.conaxgames.libraries.util.scheduler.Scheduler;

import java.util.UUID;

public final class TimerCooldown {

    private final Timer timer;
    private final UUID owner;

    private Scheduler.CancellableTask scheduledTask;
    private long expiryMillis;
    private long pauseMillis;

    TimerCooldown(Timer timer, UUID owner, long duration) {
        this.timer = timer;
        this.owner = owner;
        setRemaining(duration);
    }

    public long getRemaining() {
        return pauseMillis != 0L ? pauseMillis : expiryMillis - System.currentTimeMillis();
    }

    void setRemaining(long milliseconds) {
        if (milliseconds <= 0L) {
            cancel();
            return;
        }
        expiryMillis = System.currentTimeMillis() + milliseconds;
        cancel();
        scheduledTask = LibraryPlugin.getInstance().getScheduler()
                .runTaskLaterCancellable(
                        LibraryPlugin.getInstance().getPlugin(), this::expire, milliseconds / 50L);
    }

    boolean isPaused() {
        return pauseMillis != 0L;
    }

    void setPaused(boolean paused) {
        if (paused == isPaused()) {
            return;
        }
        if (paused) {
            pauseMillis = expiryMillis - System.currentTimeMillis();
            cancel();
        } else {
            setRemaining(pauseMillis);
            pauseMillis = 0L;
        }
    }

    void cancel() {
        if (scheduledTask != null) {
            scheduledTask.cancel();
            scheduledTask = null;
        }
    }

    private void expire() {
        scheduledTask = null;
        timer.handleExpiry(
                LibraryPlugin.getInstance().getPlugin().getServer().getPlayer(owner), owner);
        LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager()
                .callEvent(new TimerExpireEvent(owner, timer));
    }
}
