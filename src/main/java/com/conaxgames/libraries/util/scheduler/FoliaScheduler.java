package com.conaxgames.libraries.util.scheduler;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class FoliaScheduler implements Scheduler {

    private final LibraryPlugin plugin;

    @Override
    public void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
    }

    @Override
    public void runTask(Plugin plugin, Runnable runnable) {
        plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long later) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), later * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskTimer(Runnable runnable, long delay, long period) {
        plugin.getPlugin().getServer().getGlobalRegionScheduler().runAtFixedRate(plugin.getPlugin(), scheduledTask -> runnable.run(), (int) delay, (int) period);
    }

    @Override
    public void runTask(Runnable runnable) {
        plugin.getPlugin().getServer().getGlobalRegionScheduler().run(plugin.getPlugin(), scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        plugin.getPlugin().getServer().getAsyncScheduler().runNow(plugin.getPlugin(), scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        plugin.getPlugin().getServer().getGlobalRegionScheduler().runDelayed(plugin.getPlugin(), scheduledTask -> runnable.run(), delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable runnable, long later) {
        plugin.getPlugin().getServer().getAsyncScheduler().runDelayed(plugin.getPlugin(), scheduledTask -> runnable.run(), later * 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void scheduleSyncDelayedTask(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay);
    }

    @Override
    public CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable) {
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay) {
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskCancellable(Runnable runnable) {
        ScheduledTask task = plugin.getPlugin().getServer().getGlobalRegionScheduler().run(plugin.getPlugin(), scheduledTask -> runnable.run());
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Runnable runnable, long delay) {
        ScheduledTask task = plugin.getPlugin().getServer().getGlobalRegionScheduler().runDelayed(plugin.getPlugin(), scheduledTask -> runnable.run(), delay);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Runnable runnable, long delay, long period) {
        ScheduledTask task = plugin.getPlugin().getServer().getGlobalRegionScheduler().runAtFixedRate(plugin.getPlugin(), scheduledTask -> runnable.run(), (int) delay, (int) period);
        return new FoliaCancellableTask(task);
    }

    /**
     * Wrapper for Folia's ScheduledTask to implement CancellableTask interface.
     */
    private static class FoliaCancellableTask implements CancellableTask {
        private final ScheduledTask task;
        private volatile boolean cancelled = false;

        public FoliaCancellableTask(ScheduledTask task) {
            this.task = task;
        }

        @Override
        public void cancel() {
            cancelled = true;
            task.cancel();
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }
    }
}
