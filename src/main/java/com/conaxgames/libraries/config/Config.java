package com.conaxgames.libraries.config;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Config {

    private final YamlConfiguration config;
    @Getter
    private final File configFile;
    @Getter
    private final boolean wasCreated;

    public Config(String name, JavaPlugin plugin) {
        this.configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");
        boolean created = false;
        if (!this.configFile.exists()) {
            try {
                File parent = this.configFile.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }
                this.configFile.createNewFile();
                created = true;
            } catch (IOException e) {
                LibraryPlugin.getInstance().getLibraryLogger().toConsole("Config",
                        "Could not create configuration file " + this.configFile.getPath(), e);
            }
        }
        this.wasCreated = created;
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void save() {
        try {
            this.config.save(configFile);
        } catch (IOException e) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("Config",
                    "Could not save configuration file " + this.configFile.getPath(), e);
        }
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void set(String path, Object value) {
        this.config.set(path, value);
        this.save();
    }
}
