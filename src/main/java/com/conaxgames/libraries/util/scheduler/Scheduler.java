package com.conaxgames.libraries.util.scheduler;

import org.bukkit.plugin.Plugin;

public interface Scheduler {

    void runTask(Plugin plugin, Runnable runnable);

    void runTaskAsynchronously(Plugin plugin, Runnable runnable);

    void runTaskLater(Plugin plugin, Runnable runnable, long delay);

    void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay);

    void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period);

    void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period);

    CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable);

    CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay);

    CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period);

    CancellableTask runTaskTimerAsynchronouslyCancellable(Plugin plugin, Runnable runnable, long delay, long period);

    interface CancellableTask {
        void cancel();
        boolean isCancelled();
    }
}
