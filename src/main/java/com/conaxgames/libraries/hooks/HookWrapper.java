package com.conaxgames.libraries.hooks;

import org.bukkit.plugin.Plugin;

/**
 * Simple wrapper implementation of {@link Hook} that wraps a {@link Plugin} instance.
 * Used by {@link HookManager} to automatically track all enabled plugins. For custom
 * hook behavior, extend {@link Hook} directly instead.
 */
public class HookWrapper extends Hook {

    private final Plugin plugin;

    public HookWrapper(Plugin plugin) {
        super(plugin != null ? plugin.getName() : null);
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
