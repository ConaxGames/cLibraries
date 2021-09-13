package com.conaxgames.libraries;

import com.conaxgames.libraries.config.CommentedConfiguration;
import org.bukkit.Bukkit;

import java.io.File;

public class Settings {

    public final String license;
    public final Boolean autoupdate;
    public final Boolean tab;

    public Settings() {
        File file = new File(LibraryPlugin.getInstance().getDataFolder(), "settings.yml");
        if (!file.exists()) {
            LibraryPlugin.getInstance().saveResource("settings.yml", false);
        }

        CommentedConfiguration settings = CommentedConfiguration.loadConfiguration(file);

        try {
            settings.syncWithConfig(file, LibraryPlugin.getInstance().getResource("settings.yml"),  "serverdata", "settings");
        } catch (Exception exception) {
            Bukkit.getLogger().info("Unable to load settings.yml");
        }

        license = settings.getString("serverdata.license");
        autoupdate = settings.getBoolean("serverdata.auto-update");
        tab = settings.getBoolean("settings.tab");
    }
}
