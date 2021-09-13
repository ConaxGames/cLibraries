package com.conaxgames.libraries.hooks.impl.spigot;

import com.conaxgames.libraries.hooks.Hook;
import com.conaxgames.libraries.hooks.HookAnnotation;
import com.conaxgames.libraries.hooks.HookType;
import org.bukkit.plugin.Plugin;

@HookAnnotation(plugin = "Vault")
public class VaultHook extends Hook {

    public final HookType type;
    public Plugin plugin;

    public VaultHook(HookType type) {
        this.type = type;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public HookType getHookType() {
        return type;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
