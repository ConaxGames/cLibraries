package com.conaxgames.libraries.nms;

import com.conaxgames.libraries.nms.management.LibNMSManagers;
import org.bukkit.Bukkit;

public abstract class LibNMSManager {
    private static LibNMSManager nmsManager;
    private LibServerVersion serverVersion;

    public LibNMSManager(LibNMSManagers libNMSManagers) {
    }



    public static LibNMSManager getInstance() {
        return LibNMSManager.nmsManager == null ? LibNMSManager.nmsManager = newInstance() : LibNMSManager.nmsManager;
    }

    private static LibNMSManager newInstance() {
        try {
            String bukkitNMSVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];

            LibNMSManager nmsManager = (LibNMSManager) Class.forName(LibNMSManager.class.getName().replace(".LibNMSManager", "." + bukkitNMSVersion + ".LibNMSManager")).newInstance();
            nmsManager.setServerVersion(LibServerVersion.valueOf(bukkitNMSVersion));

            return nmsManager;
        } catch (Throwable t) {
            throw new IllegalArgumentException(t);
        }
    }

    public LibServerVersion getServerVersion() {
        return this.serverVersion;
    }

    public void setServerVersion(LibServerVersion serverVersion) {
        this.serverVersion = serverVersion;
    }
}
