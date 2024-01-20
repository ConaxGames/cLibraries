package com.conaxgames.libraries;

import com.conaxgames.libraries.commands.message.ACFCoreMessage;
import com.conaxgames.libraries.config.CommentedConfiguration;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Settings {

    public boolean debug;
    public Map<ACFCoreMessage, String> acfMessages = new HashMap<>();

    public Settings() {
        reload();
    }

    public void reload() {
        File file = new File(LibraryPlugin.getInstance().getPlugin().getDataFolder(), "settings.yml");
        if (!file.exists()) {
            LibraryPlugin.getInstance().getPlugin().saveResource("settings.yml", false);
        }

        CommentedConfiguration settings = CommentedConfiguration.loadConfiguration(file);

        try {
            settings.syncWithConfig(file, LibraryPlugin.getInstance().getPlugin().getResource("settings.yml"));
        } catch (Exception exception) {
            Bukkit.getLogger().info("Unable to load settings.yml");
        }

        debug = settings.getBoolean("serverdata.debug");

        for (ACFCoreMessage enumeration : ACFCoreMessage.values()) {
            String path = enumeration.name().replace("__", ".");
            path = path.replace("_", "-");
            path = path.toLowerCase(java.util.Locale.ROOT);
            path = "commands." + path;

            if (settings.contains(path)) {
                acfMessages.put(enumeration, String.valueOf(settings.get(path)));
            } else {
                acfMessages.put(enumeration, enumeration.getMessage());
            }
        }
    }
}
