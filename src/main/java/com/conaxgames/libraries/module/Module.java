package com.conaxgames.libraries.module;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.config.CommentedConfiguration;
import com.conaxgames.libraries.config.Config;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

@Getter
public abstract class Module {

    private final JavaPlugin javaPlugin;
    private Config settings;
    boolean enabled;

    public Module(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        reloadConfig();
    }

    public void reloadConfig() {
        String id = getIdentifier();
        this.settings = new Config("/modules/" + id + "/settings", javaPlugin);

        try (InputStream in = javaPlugin.getResource("modules/" + id + "/settings.yml")) {
            if (in != null) {
                CommentedConfiguration.loadConfiguration(settings.getConfigFile())
                        .syncWithConfig(settings.getConfigFile(), in, noSync().toArray(new String[0]));
                settings.getConfig().load(settings.getConfigFile());
            }
        } catch (Exception exception) {
            LibraryPlugin.getInstance().getLibraryLogger().toConsole("Module",
                    "Unable to sync /modules/" + id + "/settings.yml with config.", exception);
        }
    }

    public abstract String getName();

    public String getIdentifier() {
        return getName().toLowerCase(Locale.ROOT);
    }

    public boolean isConfiguredToEnable() {
        return getBoolean("enabled", false);
    }

    public String getRequiredPlugin() {
        return null;
    }

    public String getSupportedVersion() {
        return null;
    }

    public abstract String getDescription();

    public abstract String getAuthor();

    public List<String> noSync() {
        return List.of();
    }

    public abstract void onEnable();

    public abstract void onDisable();

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

    public ConfigurationSection getConfigSection(String path) {
        return settings.getConfig().getConfigurationSection(path);
    }

    public List<String> getStringList(String path) {
        return settings.getConfig().getStringList(path);
    }

    public void set(String path, Object value) {
        settings.set(path, value);
    }
}
