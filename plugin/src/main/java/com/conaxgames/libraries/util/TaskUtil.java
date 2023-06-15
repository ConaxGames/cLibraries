package com.conaxgames.libraries.util;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.Callable;

public class TaskUtil {

    public static void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(LibraryPlugin.getInstance(), runnable);
    }

    public static void runLater(Runnable runnable, long later) {
        Bukkit.getScheduler().runTaskLater(LibraryPlugin.getInstance(), runnable, later);
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(LibraryPlugin.getInstance(), runnable);
    }

    public static void runLaterAsync(Runnable runnable, long later) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(LibraryPlugin.getInstance(), runnable, later);
    }


    public static BukkitTask runTaskLater(Runnable run, long delay) {
        return Bukkit.getServer().getScheduler().runTaskLater(LibraryPlugin.getInstance(), run, delay);
    }

    public static BukkitTask runTaskTimer(Runnable run, long start, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimer(LibraryPlugin.getInstance(), run, start, repeat);
    }

    public static BukkitTask runTaskTimer(Runnable run, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimer(LibraryPlugin.getInstance(), run, 0, repeat);
    }

    public static BukkitTask runTaskTimerAsync(Runnable run, long start, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(LibraryPlugin.getInstance(), run, start, repeat);
    }

    public static BukkitTask runTaskTimerAsync(Runnable run, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(LibraryPlugin.getInstance(), run, 0, repeat);
    }


    public static int scheduleTask(Runnable run, long delay) {
        return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(LibraryPlugin.getInstance(), run, delay);
    }

    public static BukkitTask runTask(Runnable run) {
        if (!LibraryPlugin.getInstance().isEnabled()) {
            return null;
        }
        return Bukkit.getServer().getScheduler().runTask(LibraryPlugin.getInstance(), run);
    }

    public static <T> T runTaskSync(Callable<T> run) throws Exception {
        return Bukkit.getScheduler().callSyncMethod(LibraryPlugin.getInstance(), run).get();
    }

    public static int runTaskNextTick(Runnable run) {
        if (!LibraryPlugin.getInstance().isEnabled()) {
            run.run();
            return 0;
        }
        return scheduleTask(run, 1);
    }

    public static void runTaskAsync(Runnable run) {
        if (!LibraryPlugin.getInstance().isEnabled()) {
            run.run();
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(LibraryPlugin.getInstance(), run);
    }

    public static void runTaskLaterAsync(Runnable run, long delay) {
        if (!LibraryPlugin.getInstance().isEnabled()) {
            run.run();
            return;
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(LibraryPlugin.getInstance(), run, delay);
    }

    public static void catchNonAsyncThread() {
        if (Bukkit.getServer().isPrimaryThread()) {
            throw new IllegalStateException("Illegal call on main thread");
        }
    }

}

