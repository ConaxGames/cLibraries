package com.conaxgames.libraries.util.scheduler;

import org.bukkit.plugin.Plugin;

public interface Scheduler {

    void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period);

    void runTask(Plugin plugin, Runnable runnable);

    void runTaskAsynchronously(Plugin plugin, Runnable runnable);

    void runTaskLater(Plugin plugin, Runnable runnable, long delay);

    void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long later);

    void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period);

    void runTaskTimer(Runnable runnable, long delay, long period);

    void runTask(Runnable runnable);

    void runTaskAsynchronously(Runnable runnable);

    void runTaskLater(Runnable runnable, long delay);

    void runTaskLaterAsynchronously(Runnable runnable, long later);

    void runTaskTimerAsynchronously(Runnable runnable, long delay, long period);

    void scheduleSyncDelayedTask(Plugin plugin, Runnable runnable, long delay);
}
