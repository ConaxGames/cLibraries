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
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long later) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), later, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskTimer(Runnable runnable, long delay, long period) {
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), (int) delay, (int) period);
    }

    @Override
    public void runTask(Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable runnable, long later) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), later, TimeUnit.MILLISECONDS);
    }

    @Override
    public void scheduleSyncDelayedTask(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delay, TimeUnit.MILLISECONDS);
    }
}
