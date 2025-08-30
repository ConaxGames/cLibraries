package com.conaxgames.libraries.util.scheduler;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class BukkitScheduler implements Scheduler {

    private final LibraryPlugin plugin;

    @Override
    public void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period) {
        plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, period);
    }

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
    public void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long later) {
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, runnable, later);
    }

    @Override
    public void runTaskTimer(Runnable runnable, long delay, long period) {
        plugin.getPlugin().getServer().getScheduler().runTaskTimer(plugin.getPlugin(), runnable, delay, period);
    }

    @Override
    public void runTask(Runnable runnable) {
        plugin.getPlugin().getServer().getScheduler().runTask(plugin.getPlugin(), runnable);
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        plugin.getPlugin().getServer().getScheduler().runTaskAsynchronously(plugin.getPlugin(), runnable);
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        plugin.getPlugin().getServer().getScheduler().runTaskLater(plugin.getPlugin(), runnable, delay);
    }

    @Override
    public void runTaskLaterAsynchronously(Runnable runnable, long later) {
        plugin.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(plugin.getPlugin(), runnable, later);
    }

    @Override
    public void scheduleSyncDelayedTask(Plugin plugin, Runnable runnable, long delay) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }
}
