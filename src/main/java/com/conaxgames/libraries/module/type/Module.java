package com.conaxgames.libraries.module.type;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.config.CommentedConfiguration;
import com.conaxgames.libraries.util.Config;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.List;

@Getter
@Setter
public abstract class Module {

    private final LibraryPlugin library = LibraryPlugin.getInstance();
    private final JavaPlugin javaPlugin;
    private Config settings;

    public Module(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.settings = getResource();
    }

    public void reloadConfig() {
        this.settings = getResource();
    }

    public abstract String getName();

    public String getIdentifier() {
        return getName().toLowerCase();
    }

    public boolean isConfiguredToEnable() {
        return getBoolean("enabled", false);
    }

    public String getRequiredPlugin() {
        return null;
    }

    public abstract String getDescription();

    public abstract String getAuthor();

    public abstract List<String> noSync();

    public abstract void setupFiles();

    public abstract void onEnable();

    public abstract void onDisable();

    public boolean canRegister() {
        String required = getRequiredPlugin();
        if (required == null || Bukkit.getPluginManager().isPluginEnabled(required)) {
            return true;
        }
        library.getLibraryLogger().toConsole("ModuleManager",
                "Required plugin " + required + " is missing. Module " + getIdentifier() + " cannot be registered.");
        return false;
    }

    public String getString(String path, String def) {
        return settings.getConfig().getString(path, def);
    }

    public int getInt(String path, int def) {
        return settings.getConfig().getInt(path, def);
    }

    public long getLong(String path, long def) {
        return settings.getConfig().getLong(path, def);
    }

    public double getDouble(String path, double def) {
        return settings.getConfig().getDouble(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return settings.getConfig().getBoolean(path, def);
    }

    public Object get(String path, Object def) {
        return settings.getConfig().get(path, def);
    }

    public ConfigurationSection getConfigSection(String path) {
        return settings.getConfig().getConfigurationSection(path);
    }

    public List<String> getStringList(String path) {
        return settings.getConfig().getStringList(path);
    }

    public void set(String path, Object value) {
        settings.set(path, value);
        library.getLibraryLogger().toConsole("Module", "Saved " + path + " as " + value + " in " + getIdentifier());
    }

    public Config getResource() {
        return getResource("settings", true, true);
    }

    public Config getResource(boolean sync, boolean syncOnCreation) {
        return getResource("settings", sync, syncOnCreation);
    }

    public Config getResource(@NonNull String destination, boolean forceSync, boolean syncOnCreation) {
        String dest = destination.replace(".yml", "");
        Config config = new Config("/modules/" + getIdentifier() + "/" + dest, javaPlugin);

        if (config.getConfigFile() == null) {
            library.getLibraryLogger().toConsole("Module", "Configuration was null when attempting to getResource. (" + getIdentifier() + ", " + javaPlugin.getName() + ")");
            return config;
        }

        InputStream fileStream = javaPlugin.getResource("modules/" + getIdentifier() + "/" + dest + ".yml");
        if (fileStream == null) {
            library.getLibraryLogger().toConsole("Module", "Input stream was null when attempting to getResource. (Id: " + getIdentifier() + ", JavaPlugin: " + javaPlugin.getName() + ")");
            return config;
        }

        if (forceSync || (config.isWasCreated() && syncOnCreation)) {
            try {
                String[] dontSync = (noSync() == null ? new String[0] : noSync().toArray(new String[0]));
                CommentedConfiguration commentedConfiguration = CommentedConfiguration.loadConfiguration(config.getConfigFile());
                commentedConfiguration.syncWithConfig(config.getConfigFile(), fileStream, dontSync);
                library.getLibraryLogger().toConsole("Module", "Sync'd " + "/modules/" + getIdentifier() + "/" + dest + ".yml" + " with config.");
            } catch (Exception exception) {
                library.getLibraryLogger().toConsole("Module", "Unable to sync " + "/modules/" + getIdentifier() + "/" + dest + ".yml" + " with config.", exception);
            }
        }

        return config;
    }

    public boolean isEnabled() {
        return library.getModuleManager().getStatus(this);
    }
}
