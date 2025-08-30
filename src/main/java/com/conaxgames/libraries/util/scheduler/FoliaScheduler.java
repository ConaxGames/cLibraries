package com.conaxgames.libraries.util.scheduler;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

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
    public void cancelTasks(Plugin plugin) {
        // Folia doesn't provide a direct way to cancel all tasks for a plugin.
        // Tasks are automatically cleaned up when the plugin is disabled.
        // This is a limitation of Folia's design - tasks are tied to the plugin lifecycle.
        // Individual tasks can be cancelled by their ScheduledTask instances, but we don't track those here.
    }
}
