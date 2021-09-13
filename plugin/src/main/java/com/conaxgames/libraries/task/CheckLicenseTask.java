package com.conaxgames.libraries.task;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.util.License;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckLicenseTask extends BukkitRunnable {

    public CheckLicenseTask() {
        runTaskTimerAsynchronously(LibraryPlugin.getInstance(), 3600L, 3600 * 20L);
    }

    @Override
    public void run() {
        new License(LibraryPlugin.getInstance().getSettings().license, "https://conaxgames.com/license/verify.php", LibraryPlugin.getInstance()).register();
    }
}