package com.conaxgames.libraries.hooks;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Hook implements Listener {

    private final String pluginName;

    public abstract Plugin getPlugin();

    protected Hook(String pluginName) {
        this.pluginName = pluginName;
    }

    public String getPluginName() {
        return pluginName;
    }
}
