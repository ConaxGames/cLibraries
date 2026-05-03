package com.conaxgames.libraries.util.scheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FoliaScheduler implements Scheduler {

    private static final long MILLIS_PER_TICK = 50L;

    private static GlobalRegionScheduler global(Plugin plugin) {
        return plugin.getServer().getGlobalRegionScheduler();
    }

    private static AsyncScheduler async(Plugin plugin) {
        return plugin.getServer().getAsyncScheduler();
    }

    private static Consumer<ScheduledTask> wrap(Runnable runnable) {
        return task -> runnable.run();
    }

    @Override
    public void runTask(Plugin plugin, Runnable runnable) {
        global(plugin).run(plugin, wrap(runnable));
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        async(plugin).runNow(plugin, wrap(runnable));
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        global(plugin).runDelayed(plugin, wrap(runnable), delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        async(plugin).runDelayed(plugin, wrap(runnable), delay * MILLIS_PER_TICK, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        global(plugin).runAtFixedRate(plugin, wrap(runnable), delay, period);
    }

    @Override
    public void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period) {
        async(plugin).runAtFixedRate(plugin, wrap(runnable), delay * MILLIS_PER_TICK, period * MILLIS_PER_TICK, TimeUnit.MILLISECONDS);
    }

    @Override
    public CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable) {
        return new Task(global(plugin).run(plugin, wrap(runnable)));
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay) {
        return new Task(global(plugin).runDelayed(plugin, wrap(runnable), delay));
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        return new Task(global(plugin).runAtFixedRate(plugin, wrap(runnable), delay, period));
    }

    @Override
    public CancellableTask runTaskTimerAsynchronouslyCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        return new Task(async(plugin).runAtFixedRate(plugin, wrap(runnable), delay * MILLIS_PER_TICK, period * MILLIS_PER_TICK, TimeUnit.MILLISECONDS));
    }

    private record Task(ScheduledTask task) implements CancellableTask {

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
