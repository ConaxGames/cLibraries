package com.conaxgames.libraries.hooks;

import org.bukkit.plugin.Plugin;

public class HookWrapper extends Hook {

    public HookType type;
    public Plugin plugin;

    public HookWrapper(HookType type, Plugin plugin) {
        this.type = type;
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public HookType getHookType() {
        return type;
    }
}
