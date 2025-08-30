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
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        if (delay < 0 || period < 0) {
            throw new IllegalArgumentException("Delay and period must be non-negative");
        }
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
    }

    @Override
    public void runTask(Plugin plugin, Runnable runnable) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        // Convert ticks to milliseconds for async scheduler
        long delayMs = delay * 50L;
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskTimer(Runnable runnable, long delay, long period) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        if (delay < 0 || period < 0) {
            throw new IllegalArgumentException("Delay and period must be non-negative");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        libraryPlugin.getServer().getGlobalRegionScheduler().runAtFixedRate(libraryPlugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
    }

    @Override
    public void runTask(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        libraryPlugin.getServer().getGlobalRegionScheduler().run(libraryPlugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        libraryPlugin.getServer().getAsyncScheduler().runNow(libraryPlugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        libraryPlugin.getServer().getGlobalRegionScheduler().runDelayed(libraryPlugin, scheduledTask -> runnable.run(), delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable runnable, long delay) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        // Convert ticks to milliseconds for async scheduler
        long delayMs = delay * 50L;
        libraryPlugin.getServer().getAsyncScheduler().runDelayed(libraryPlugin, scheduledTask -> runnable.run(), delayMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void scheduleSyncDelayedTask(Plugin plugin, Runnable runnable, long delay) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay);
    }

    @Override
    public CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period) {
        if (plugin == null || runnable == null) {
            throw new IllegalArgumentException("Plugin and runnable cannot be null");
        }
        if (delay < 0 || period < 0) {
            throw new IllegalArgumentException("Delay and period must be non-negative");
        }
        ScheduledTask task = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskCancellable(Runnable runnable) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        ScheduledTask task = libraryPlugin.getServer().getGlobalRegionScheduler().run(libraryPlugin, scheduledTask -> runnable.run());
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskLaterCancellable(Runnable runnable, long delay) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        if (delay < 0) {
            throw new IllegalArgumentException("Delay must be non-negative");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        ScheduledTask task = libraryPlugin.getServer().getGlobalRegionScheduler().runDelayed(libraryPlugin, scheduledTask -> runnable.run(), delay);
        return new FoliaCancellableTask(task);
    }

    @Override
    public CancellableTask runTaskTimerCancellable(Runnable runnable, long delay, long period) {
        if (runnable == null) {
            throw new IllegalArgumentException("Runnable cannot be null");
        }
        if (delay < 0 || period < 0) {
            throw new IllegalArgumentException("Delay and period must be non-negative");
        }
        Plugin libraryPlugin = plugin.getPlugin();
        if (libraryPlugin == null) {
            throw new IllegalStateException("Library plugin is not available");
        }
        ScheduledTask task = libraryPlugin.getServer().getGlobalRegionScheduler().runAtFixedRate(libraryPlugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
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
            if (!cancelled) {
                cancelled = true;
                if (task != null) {
                    task.cancel();
                }
            }
        }

        @Override
        public boolean isCancelled() {
            return cancelled || (task != null && task.isCancelled());
        }
    }
}
