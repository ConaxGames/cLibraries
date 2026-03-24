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
        plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), delay * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(), delay, period);
    }

    @Override
    public void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS);
    }

    @Override
    public CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable) {
        return new FoliaCancellableTask(plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> runnable.run()));
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay) {
        return new FoliaCancellableTask(plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delay));
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        return new FoliaCancellableTask(plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> runnable.run(), delay, period));
    }

    @Override
    public CancellableTask runTaskTimerAsynchronouslyCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        return new FoliaCancellableTask(plugin.getServer().getAsyncScheduler().runAtFixedRate(plugin, task -> runnable.run(), delay * 50L, period * 50L, TimeUnit.MILLISECONDS));
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
