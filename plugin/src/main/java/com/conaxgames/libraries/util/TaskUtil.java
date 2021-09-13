package com.conaxgames.libraries.util;

import com.conaxgames.libraries.LibraryPlugin;
import org.bukkit.Bukkit;

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
}

