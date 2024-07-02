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
            // Retrieve the NMS version from the server class name
            String bukkitNMSVersion = Bukkit.getServer().getClass().getName().split("\\.")[3];
            System.out.println("LibNMSManager: Detected NMS version: " + bukkitNMSVersion);

            try {
                // Attempt to set the server version
                setServerVersion(LibServerVersion.valueOf(bukkitNMSVersion));
                System.out.println("LibNMSManager: Successfully set server version to: " + bukkitNMSVersion);
            } catch (IllegalArgumentException e) {
                // Handle the case where the NMS version is not found
                System.out.println("LibNMSManager: UNABLE TO FIND NMS VERSION MATCHING " +
                        bukkitNMSVersion + ". Disabling cLibraries to avoid further complications and compatibility issues...");
                LibraryPlugin.getInstance().getPlugin().getServer().getPluginManager().disablePlugin(LibraryPlugin.getInstance().getPlugin());
            }
            return serverVersion;
        } catch (Exception e) {
            // Catch and log any unexpected exceptions
            System.out.println("LibNMSManager: Exception occurred while trying to set NMS version: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void setServerVersion(LibServerVersion version) {
        serverVersion = version;
    }
}