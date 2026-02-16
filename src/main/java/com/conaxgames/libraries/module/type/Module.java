package com.conaxgames.libraries.module.type;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.config.CommentedConfiguration;
import com.conaxgames.libraries.config.Config;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.List;

/**
 * Base type for pluggable modules. Each module has its own config under
 * <code>/modules/{@link #getIdentifier()}/</code> (e.g. <code>settings.yml</code>) and a
 * lifecycle: register with {@link com.conaxgames.libraries.module.ModuleManager#registerModule},
 * then enable/disable at runtime.
 * <p>
 * <b>Lifecycle:</b> When enabled, {@link #onEnable()} runs and the module is registered as an
 * {@link org.bukkit.event.Listener} if it implements that interface. When disabled,
 * {@link #onDisable()} runs and listeners are unregistered. Use {@link #getResource()} or
 * {@link #getResource(String, boolean, boolean)} to load YAML that is synced from the plugin jar
 * (paths under <code>modules/{identifier}/</code>).
 */
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

    /**
     * Reloads this module's config from disk and replaces the current {@link #getResource()} result.
     * Use after changing files on disk or when toggling the module.
     */
    public void reloadConfig() {
        this.settings = getResource();
    }

    /**
     * Display name of the module (e.g. for menus and logs). Default identifier is
     * {@link #getName()} lowercased via {@link #getIdentifier()}.
     */
    public abstract String getName();

    /**
     * Unique id used for config paths and registration. Defaults to {@link #getName()} in lowercase.
     */
    public String getIdentifier() {
        return getName().toLowerCase();
    }

    /**
     * Whether the module's config has <code>enabled: true</code>. Used by
     * {@link com.conaxgames.libraries.module.ModuleManager} to auto-enable on register.
     */
    public boolean isConfiguredToEnable() {
        return getBoolean("enabled", false);
    }

    /**
     * Optional plugin name that must be enabled for this module to register. Return null for no dependency.
     */
    public String getRequiredPlugin() {
        return null;
    }

    /**
     * Short description of the module (e.g. for help or UI).
     */
    public abstract String getDescription();

    /**
     * Author or team name for the module.
     */
    public abstract String getAuthor();

    /**
     * Config keys to exclude when syncing from the jar. Return empty list to sync all keys.
     * Used by {@link #getResource(String, boolean, boolean)}.
     */
    public abstract List<String> noSync();

    /**
     * Called when the module is enabled. Register listeners, start tasks, or load data here.
     * Do not assume {@link #getResource()} has been reloaded immediately before; use
     * {@link #reloadConfig()} in here if needed.
     */
    public abstract void onEnable();

    /**
     * Called when the module is disabled. Unregister listeners, cancel tasks, and save state here.
     */
    public abstract void onDisable();

    /**
     * Whether this module can be registered (e.g. {@link #getRequiredPlugin()} is present).
     * If false, the manager logs and skips registration.
     */
    public boolean canRegister() {
        String required = getRequiredPlugin();
        if (required == null || Bukkit.getPluginManager().isPluginEnabled(required)) {
            return true;
        }
        library.getLibraryLogger().toConsole("ModuleManager",
                "Required plugin " + required + " is missing. Module " + getIdentifier() + " cannot be registered.");
        return false;
    }

    /**
     * Gets a string from this module's config with a default. Path is relative to the config root.
     */
    public String getString(String path, String def) {
        return settings.getConfig().getString(path, def);
    }

    /**
     * Gets an int from this module's config with a default.
     */
    public int getInt(String path, int def) {
        return settings.getConfig().getInt(path, def);
    }

    /**
     * Gets a long from this module's config with a default.
     */
    public long getLong(String path, long def) {
        return settings.getConfig().getLong(path, def);
    }

    /**
     * Gets a double from this module's config with a default.
     */
    public double getDouble(String path, double def) {
        return settings.getConfig().getDouble(path, def);
    }

    /**
     * Gets a boolean from this module's config with a default.
     */
    public boolean getBoolean(String path, boolean def) {
        return settings.getConfig().getBoolean(path, def);
    }

    /**
     * Gets a raw value from this module's config with a default.
     */
    public Object get(String path, Object def) {
        return settings.getConfig().get(path, def);
    }

    /**
     * Gets a configuration section for nested keys. Returns null if the path is not a section.
     */
    public ConfigurationSection getConfigSection(String path) {
        return settings.getConfig().getConfigurationSection(path);
    }

    /**
     * Gets a string list from this module's config.
     */
    public List<String> getStringList(String path) {
        return settings.getConfig().getStringList(path);
    }

    /**
     * Writes a value to this module's config and persists it. Logs the change to the library logger.
     */
    public void set(String path, Object value) {
        settings.set(path, value);
        library.getLibraryLogger().toConsole("Module", "Saved " + path + " as " + value + " in " + getIdentifier());
    }

    /**
     * Loads this module's primary config file <code>settings.yml</code> from
     * <code>/modules/{@link #getIdentifier()}/</code>, with sync from the plugin jar. Use for
     * normal module config.
     */
    public Config getResource() {
        return getResource("settings", true, true);
    }

    /**
     * Loads a config file from <code>/modules/{@link #getIdentifier()}/{destination}.yml</code>.
     * If <code>forceSync</code> or (file was just created and <code>syncOnCreation</code>), the
     * file is synced from the plugin jar; keys in {@link #noSync()} are left unchanged. Use for
     * extra config files or when you need to skip sync.
     *
     * @param destination base name without .yml (e.g. "settings")
     * @param forceSync whether to always sync from jar
     * @param syncOnCreation whether to sync when the file is newly created
     */
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

    /**
     * Whether this module is currently enabled in the {@link com.conaxgames.libraries.module.ModuleManager}.
     */
    public boolean isEnabled() {
        return library.getModuleManager().isModuleEnabled(this);
    }
}
