package com.conaxgames.libraries.util.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;
import org.bukkit.plugin.Plugin;

/**
 * {@link Scheduler} for Folia: sync work on the
 * {@link io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler}, async work on the
 * {@link io.papermc.paper.threadedregions.scheduler.AsyncScheduler}. Used when
 * {@code io.papermc.paper.threadedregions.RegionizedServer} is present at runtime.
 */
public class FoliaScheduler implements Scheduler {

    @Override
    public void runTask(Plugin plugin, Runnable runnable) {
        plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        if (delay <= 0) {
            runTask(plugin, runnable);
            return;
        }
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        if (delay <= 0) {
            runTaskAsynchronously(plugin, runnable);
            return;
        }
        long delayMs = delay * 50L;
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), delayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        long safePeriod = Math.max(1L, period);
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(), delay, safePeriod);
    }

    @Override
    public void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        long delayMs = delay * 50L;
        long periodMs = Math.max(50L, period * 50L);
        plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), delayMs, periodMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable) {
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().run(plugin, t -> runnable.run());
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay) {
        if (delay <= 0) {
            return runTaskCancellable(plugin, runnable);
        }
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, t -> runnable.run(), delay);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        long safePeriod = Math.max(1L, period);
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, t -> runnable.run(), delay, safePeriod);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerAsynchronouslyCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        long delayMs = delay * 50L;
        long periodMs = Math.max(50L, period * 50L);
        ScheduledTask task = plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, t -> runnable.run(), delayMs, periodMs, TimeUnit.MILLISECONDS);
        return new FoliaCancellableTask(task);
    }

    private static class FoliaCancellableTask implements CancellableTask {
        private final ScheduledTask task;

        FoliaCancellableTask(ScheduledTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return task.isCancelled();
        }
    }
}
