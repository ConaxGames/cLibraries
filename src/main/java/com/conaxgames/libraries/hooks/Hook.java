package com.conaxgames.libraries.hooks;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class Hook implements Listener {

    public String pluginFromAnnotation;

    public abstract Plugin getPlugin();

    public abstract HookType getHookType();

    public String getPluginFromAnnotation() {
        return this.pluginFromAnnotation;
    }

    public void setPluginFromAnnotation(String pluginFromAnnotation) {
        this.pluginFromAnnotation = pluginFromAnnotation;
    }
}
