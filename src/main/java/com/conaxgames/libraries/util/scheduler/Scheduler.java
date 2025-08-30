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
     * Runs a task and returns a cancellable task instance.
     * 
     * @param plugin The plugin instance
     * @param runnable The task to run
     * @return Cancellable task instance
     */
    CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable);

    /**
     * Runs a delayed task and returns a cancellable task instance.
     * 
     * @param plugin The plugin instance
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @return Cancellable task instance
     */
    CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay);

    /**
     * Runs a repeating task and returns a cancellable task instance.
     * 
     * @param plugin The plugin instance
     * @param runnable The task to run
     * @param delay The initial delay in ticks
     * @param period The period between executions in ticks
     * @return Cancellable task instance
     */
    CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period);

    /**
     * Runs a task and returns a cancellable task instance (uses library plugin).
     * 
     * @param runnable The task to run
     * @return Cancellable task instance
     */
    CancellableTask runTaskCancellable(Runnable runnable);

    /**
     * Runs a delayed task and returns a cancellable task instance (uses library plugin).
     * 
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @return Cancellable task instance
     */
    CancellableTask runTaskLaterCancellable(Runnable runnable, long delay);

    /**
     * Runs a repeating task and returns a cancellable task instance (uses library plugin).
     * 
     * @param runnable The task to run
     * @param delay The initial delay in ticks
     * @param period The period between executions in ticks
     * @return Cancellable task instance
     */
    CancellableTask runTaskTimerCancellable(Runnable runnable, long delay, long period);

    /**
     * Interface for cancellable tasks that work across both Bukkit and Folia.
     */
    interface CancellableTask {
        /**
         * Cancels this task.
         */
        void cancel();
        
        /**
         * Checks if this task is cancelled.
         * @return true if cancelled, false otherwise
         */
        boolean isCancelled();
    }
}
