package com.conaxgames.libraries.hooks;

import org.bukkit.plugin.Plugin;

import java.util.Objects;

public final class Hook {

    private final HookType type;
    private final Plugin plugin;

    public Hook(HookType type, Plugin plugin) {
        this.type = Objects.requireNonNull(type, "type");
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    public HookType type() {
        return type;
    }

    public Plugin plugin() {
        return plugin;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Hook)) {
            return false;
        }
        Hook other = (Hook) obj;
        return type == other.type && plugin.equals(other.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, plugin);
    }

    @Override
    public String toString() {
        return "Hook[type=" + type + ", plugin=" + plugin + "]";
    }
}
