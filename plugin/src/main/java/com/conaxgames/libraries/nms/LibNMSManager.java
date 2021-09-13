package com.conaxgames.libraries.nms;

import com.conaxgames.libraries.nms.management.LibNMSManagers;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class LibNMSManager {
    private static LibNMSManager nmsManager;
    private LibServerVersion serverVersion;

    @NonNull
    protected LibNMSManagers managers;

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
