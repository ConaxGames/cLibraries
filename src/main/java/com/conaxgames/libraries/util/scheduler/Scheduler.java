package com.conaxgames.libraries.util.scheduler;

import org.bukkit.plugin.Plugin;

public interface Scheduler {

    void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period);

    void runTask(Plugin plugin, Runnable runnable);

    void runTaskAsynchronously(Plugin plugin, Runnable runnable);

    void runTaskLater(Plugin plugin, Runnable runnable, long delay);

    void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long later);

    void runTaskTimer(Runnable runnable, long delay, long period);

    void runTask(Runnable runnable);

    void runTaskAsynchronously(Runnable runnable);

    void runTaskLater(Runnable runnable, long delay);

    void runTaskLaterAsynchronously(Runnable runnable, long later);

    void scheduleSyncDelayedTask(Plugin plugin, Runnable runnable, long delay);

    /**
     * Cancels all tasks for the specified plugin.
     * Note: This is a best-effort operation and may not cancel all tasks immediately.
     * 
     * @param plugin The plugin whose tasks should be cancelled
     */
    void cancelTasks(Plugin plugin);
}
