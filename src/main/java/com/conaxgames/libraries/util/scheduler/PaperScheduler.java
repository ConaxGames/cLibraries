package com.conaxgames.libraries.util.scheduler;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

public class PaperScheduler implements Scheduler {

    private static BukkitScheduler backend(Plugin plugin) {
        return plugin.getServer().getScheduler();
    }

    @Override
    public void runTask(Plugin plugin, Runnable runnable) {
        backend(plugin).runTask(plugin, runnable);
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        backend(plugin).runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        backend(plugin).runTaskLater(plugin, runnable, delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        backend(plugin).runTaskLaterAsynchronously(plugin, runnable, delay);
    }

    @Override
    public void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        backend(plugin).runTaskTimer(plugin, runnable, delay, period);
    }

    @Override
    public void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        backend(plugin).runTaskTimerAsynchronously(plugin, runnable, delay, period);
    }

    @Override
    public CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable) {
        return new Task(backend(plugin).runTask(plugin, runnable));
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay) {
        return new Task(backend(plugin).runTaskLater(plugin, runnable, delay));
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        return new Task(backend(plugin).runTaskTimer(plugin, runnable, delay, period));
    }

    @Override
    public CancellableTask runTaskTimerAsynchronouslyCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        return new Task(backend(plugin).runTaskTimerAsynchronously(plugin, runnable, delay, period));
    }

    private record Task(BukkitTask task) implements CancellableTask {

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
