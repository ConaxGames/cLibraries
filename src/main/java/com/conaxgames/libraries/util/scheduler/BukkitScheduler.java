package com.conaxgames.libraries.util.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * {@link Scheduler} implementation for Bukkit and Paper (non-Folia). Uses the standard
 * {@link org.bukkit.scheduler.BukkitScheduler}. Chosen when Folia's regionized server is not present.
 */
public class BukkitScheduler implements Scheduler {

    @Override
    public void runTask(Plugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    @Override
    public void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    @Override
    public CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable) {
        BukkitTask task = plugin.getServer().getScheduler().runTask(plugin, runnable);
        return new BukkitCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
        return new BukkitCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
        return new BukkitCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerAsynchronouslyCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period);
        return new BukkitCancellableTask(task);
    }

    private static class BukkitCancellableTask implements CancellableTask {
        private final BukkitTask task;

        BukkitCancellableTask(BukkitTask task) {
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
