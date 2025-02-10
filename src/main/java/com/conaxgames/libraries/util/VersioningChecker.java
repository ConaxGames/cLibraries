package com.conaxgames.libraries.util;

import org.bukkit.Bukkit;
import static org.bukkit.Bukkit.getLogger;

public class VersioningChecker {

    private static final VersioningChecker instance = new VersioningChecker();

    private VersioningChecker() {}

    public static VersioningChecker getInstance() {
        return instance;
    }

    public boolean isServerVersionBefore(String targetVersion) {
        return compareVersions(Bukkit.getBukkitVersion().split("-")[0], targetVersion) < 0;
    }

    private int compareVersions(String version1, String version2) {
        String[] parts1 = version1.split("\\.");
        String[] parts2 = version2.split("\\.");
        int length = Math.max(parts1.length, parts2.length);

        for (int i = 0; i < length; i++) {
            int part1 = (i < parts1.length) ? parseVersionPart(parts1[i]) : 0;
            int part2 = (i < parts2.length) ? parseVersionPart(parts2[i]) : 0;

            if (part1 != part2) return Integer.compare(part1, part2);
        }
        return 0;
    }

    private int parseVersionPart(String part) {
        try {
            return Integer.parseInt(part);
        } catch (NumberFormatException e) {
            getLogger().severe("Error parsing version part: " + e.getMessage());
            return 0;
        }
    }
}
