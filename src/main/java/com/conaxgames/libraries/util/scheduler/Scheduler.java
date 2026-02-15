package com.conaxgames.libraries.util.scheduler;

import org.bukkit.plugin.Plugin;

/**
 * Abstraction over Bukkit/Paper and Folia schedulers so the same code works on both.
 * All delays and periods are in <b>ticks</b> (20 ticks = 1 second).
 * <p>
 * <b>When to use sync vs async:</b> Use sync (no "Asynchronously" in the name) when the task
 * touches the server (players, worlds, blocks, inventories). Use async for I/O, heavy computation,
 * or anything that must not block the server tick.
 */
public interface Scheduler {

    /**
     * Run once on the next server tick. Use when you need to run something on the main thread soon
     * (e.g. after an event or to defer work by one tick).
     */
    void runTask(Plugin plugin, Runnable runnable);

    /**
     * Run once as soon as possible on an async thread. Use for I/O or CPU work that must not
     * block the server; do not call Bukkit API from inside the runnable.
     */
    void runTaskAsynchronously(Plugin plugin, Runnable runnable);

    /**
     * Run once after the given delay (in ticks). Use for delayed one-off work on the main thread
     * (e.g. run something in 20 ticks).
     */
    void runTaskLater(Plugin plugin, Runnable runnable, long delay);

    /**
     * Run once after the given delay (in ticks) on an async thread. Use for delayed async work;
     * do not call Bukkit API from inside the runnable.
     */
    void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay);

    /**
     * Run repeatedly: first after {@code delay} ticks, then every {@code period} ticks. Use for
     * recurring main-thread work (e.g. scoreboard updates, countdowns). If you need to cancel
     * the task later, use {@link #runTaskTimerCancellable} instead.
     */
    void runTaskTimer(Plugin plugin, Runnable runnable, long delay, long period);

    /**
     * Run repeatedly on an async thread: first after {@code delay} ticks, then every {@code period}
     * ticks. Use for recurring async work; do not call Bukkit API from inside the runnable.
     */
    void runTaskTimerAsynchronously(Plugin plugin, Runnable runnable, long delay, long period);

    /**
     * Same as {@link #runTask} but returns a handle so the task can be cancelled. Use when you may
     * need to cancel the run before it executes.
     */
    CancellableTask runTaskCancellable(Plugin plugin, Runnable runnable);

    /**
     * Same as {@link #runTaskLater} but returns a cancellable handle. Use when the delayed task
     * might become unnecessary (e.g. player logs out before the delay ends).
     */
    CancellableTask runTaskLaterCancellable(Plugin plugin, Runnable runnable, long delay);

    /**
     * Same as {@link #runTaskTimer} but returns a handle so the repeating task can be cancelled.
     * Use for any repeating task you need to stop later (e.g. when a menu closes or a cooldown ends).
     */
    CancellableTask runTaskTimerCancellable(Plugin plugin, Runnable runnable, long delay, long period);

    /**
     * Same as {@link #runTaskTimerAsynchronously} but returns a cancellable handle.
     */
    CancellableTask runTaskTimerAsynchronouslyCancellable(Plugin plugin, Runnable runnable, long delay, long period);

    /**
     * Handle for a scheduled task that can be cancelled. Safe to call {@link #cancel()} more than once.
     */
    interface CancellableTask {
        /** Stops the task from running (or from running again if it is repeating). */
        void cancel();
        /** Returns true if the task has been cancelled or has already finished. */
        boolean isCancelled();
    }
}
