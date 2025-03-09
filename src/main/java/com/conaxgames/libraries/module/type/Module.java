package com.conaxgames.libraries.module.type;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.config.CommentedConfiguration;
import com.conaxgames.libraries.util.Config;
import com.conaxgames.libraries.util.VersioningChecker;
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

    public LibraryPlugin library;
    public JavaPlugin javaPlugin;
    private Config settings;

    /**
     * Create a module object which can be enabled and disabled
     * on command. Modules are perfect for small to medium features.
     *
     * @param javaPlugin The JavaPlugin which owns this module,
     *                   including its files which will be found
     *                   in the /modules/... directory..
     */
    public Module(JavaPlugin javaPlugin) {
        this.library = LibraryPlugin.getInstance();
        this.javaPlugin = javaPlugin;
        this.settings = getResource();
    }

    public void reloadConfig() {
        this.settings = getResource();
    }

    /**
     * The name of this module
     *
     * @return The unique name of the module
     */
    public abstract String getName();

    /**
     * The placeholder identifier of this module
     *
     * @return placeholder identifier that is associated with this module
     */
    public String getIdentifier() {
        return getName().toLowerCase();
    }

    /**
     * Check if the module is enabled in config.
     *
     * @return if module can be enabled.
     */
    public boolean isConfiguredToEnable() {
        // Sub Modules are handled inside their parent module
        if (this instanceof SubModule) {
            return true;
        }
        return getBoolean("enabled", false);
    }

    /**
     * The name of the plugin that this module hooks into.
     *
     * @return plugin name that this module requires to function
     */
    public String getRequiredPlugin() {
        return null;
    }

    public abstract String getDescription();

    public abstract String getAuthor();

    public abstract List<String> noSync();

    public abstract void setupFiles();

    public abstract void onEnable();

    public abstract void onReload();

    public abstract void onDisable();

    /**
     * If any requirements need to be checked before this module should register, you can check
     * them here
     *
     * @return true if this hook meets all the requirements to register
     */
    public boolean canRegister() {
        if (getRequiredPlugin() == null || Bukkit.getPluginManager().isPluginEnabled(getRequiredPlugin())) {
            return true;
        }
        library.getLibraryLogger().toConsole("ModuleManager",
                "Required plugin " + getRequiredPlugin() + " is missing. Module " + getIdentifier() + " cannot be registered.");
        return false;
    }

    public String getString(String path, String def) {
        return this.settings.getConfig().getString(path, def);
    }

    public int getInt(String path, int def) {
        return this.settings.getConfig().getInt(path, def);
    }

    public long getLong(String path, long def) {
        return this.settings.getConfig().getLong(path, def);
    }

    public double getDouble(String path, double def) {
        return this.settings.getConfig().getDouble(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return this.settings.getConfig().getBoolean(path, def);
    }

    public Object get(String path, Object def) {
        return this.settings.getConfig().get(path, def);
    }

    public ConfigurationSection getConfigSection(String path) {
        return this.settings.getConfig().getConfigurationSection(path);
    }

    public List<String> getStringList(String path) {
        return this.settings.getConfig().getStringList(path);
    }

    public void set(String path, Object value) {
        this.settings.set(path, value);
        this.getLibrary().getLibraryLogger().toConsole("Module", "Saved " + path + " as " + value + " in " + this.getIdentifier());
    }

    public Config getResource(boolean sync, boolean syncOnCreation) {
        return getResource("settings", sync, syncOnCreation);
    }

    public Config getResource() {
        return getResource("settings", true, true);
    }

    public Config getResource(@NonNull String destination, boolean forceSync, boolean syncOnCreation) {
        String dest = destination.replace(".yml", "");
        Config config = new Config("/modules/" + this.getIdentifier() + "/" + dest, javaPlugin);

        if (config.getConfigFile() == null) {
            library.getLibraryLogger().toConsole("Module", "Configuration was null when attempting to getResource. (" + this.getIdentifier() + ", " + this.getJavaPlugin().getName() + ")");
            return config;
        }

        InputStream fileStream = javaPlugin.getResource("modules/" + this.getIdentifier() + "/" + dest + ".yml");
        if (fileStream == null) {
            library.getLibraryLogger().toConsole("Module", "Input stream was null when attempting to getResource. (Id: " + this.getIdentifier() + ", JavaPlugin: " + this.getJavaPlugin().getName() +
                    ")");
            return config;
        }

        if (forceSync || (config.isWasCreated() && syncOnCreation)) {
            try {
                String[] dontSync = (this.noSync() == null ? new String[0] : this.noSync().toArray(new String[0]));

                CommentedConfiguration commentedConfiguration = CommentedConfiguration.loadConfiguration(config.getConfigFile());
                commentedConfiguration.syncWithConfig(config.getConfigFile(), fileStream, dontSync);

                library.getLibraryLogger().toConsole("Module", "Sync'd " + "/modules/" + this.getIdentifier() + "/" + dest + ".yml" + " with config.");
            } catch (Exception exception) {
                library.getLibraryLogger().toConsole("Module", "Unable to sync " + "/modules/" + this.getIdentifier() + "/" + dest + ".yml" + " with config.", exception);
            }
        }

        return config;
    }

    public boolean isEnabled() {
        return LibraryPlugin.getInstance().getModuleManager().getStatus(this);
    }

}
