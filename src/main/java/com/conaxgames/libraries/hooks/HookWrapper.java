package com.conaxgames.libraries.hooks;

import org.bukkit.plugin.Plugin;

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
