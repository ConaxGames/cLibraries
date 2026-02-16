package com.conaxgames.libraries.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Config {

    private final YamlConfiguration config;
    @Getter
    private final File configFile;
    @Getter
    protected boolean wasCreated;

    public Config(String name, JavaPlugin plugin) {
        this.configFile = new File(plugin.getDataFolder() + "/" + name + ".yml");
        if (!this.configFile.exists()) {
            try {
                this.configFile.getParentFile().mkdirs();
                this.configFile.createNewFile();
                this.wasCreated = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
    }

    public void save() {
        try {
            this.config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
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
