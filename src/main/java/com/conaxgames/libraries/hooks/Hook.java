package com.conaxgames.libraries.hooks;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Base class for plugin hooks. Represents a connection to another plugin that can be checked
 * for availability and accessed via {@link HookManager}. All hooks are automatically registered
 * when their plugin enables and unregistered when it disables.
 * <p>
 * <b>Implementation:</b> Extend this class and implement {@link #getPlugin()} to return the
 * hooked plugin instance. Use {@link HookWrapper} for simple plugin wrapping, or create custom
 * implementations for plugins that need special handling.
 */
public abstract class Hook implements Listener {

    private final String pluginName;

    /**
     * Returns the {@link Plugin} instance this hook represents. May return null if the plugin
     * has been disabled or unloaded.
     */
    public abstract Plugin getPlugin();

    protected Hook(String pluginName) {
        this.pluginName = pluginName;
    }

    /**
     * Returns the name of the hooked plugin. This is set at construction and does not change,
     * even if the plugin is later disabled.
     */
    public String getPluginName() {
        return pluginName;
    }
}
