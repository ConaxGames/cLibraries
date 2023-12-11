package com.conaxgames.libraries.nms;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class LibNMSManager {

    public static LibServerVersion serverVersion;

    public static LibServerVersion getInstance() {
        return serverVersion == null ? serverVersion = newInstance() : serverVersion;
    }

    private static LibServerVersion newInstance() {
        try {
            String bukkitNMSVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];

            try {
                setServerVersion(LibServerVersion.valueOf(bukkitNMSVersion)); // set the enum
            } catch (IllegalArgumentException e) {
                LibraryPlugin.getInstance().getLibraryLogger().toConsole("LibNMSManager", "UNABLE TO FIND NMS VERSION MATCHING " +
                        bukkitNMSVersion + ". Disabling cLibraries to avoid further complications compatibility issues...", new UnsupportedOperationException(bukkitNMSVersion));
                LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().disablePlugin(LibraryPlugin.getInstance().getPlugin());
            }
            return serverVersion;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setServerVersion(LibServerVersion version) {
        serverVersion = version;
    }
}