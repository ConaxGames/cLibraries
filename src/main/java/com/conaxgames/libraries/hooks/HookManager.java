package com.conaxgames.libraries.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class HookManager {

    private final Map<HookType, Hook> hooks;

    public HookManager(JavaPlugin plugin) {
        EnumMap<HookType, Hook> discovered = new EnumMap<>(HookType.class);
        for (Plugin p : plugin.getServer().getPluginManager().getPlugins()) {
            HookType.fromPluginName(p.getName())
                    .ifPresent(type -> discovered.put(type, new Hook(type, p)));
        }
        this.hooks = Collections.unmodifiableMap(discovered);
    }

    public Optional<Hook> getHook(HookType type) {
        return Optional.ofNullable(hooks.get(type));
    }

    public Optional<Hook> getHook(String pluginName) {
        return HookType.fromPluginName(pluginName).flatMap(this::getHook);
    }

    public boolean isHooked(HookType type) {
        return hooks.containsKey(type);
    }

    public Set<HookType> hookedTypes() {
        return hooks.keySet();
    }

    public Collection<Hook> hooks() {
        return hooks.values();
    }

    public List<Plugin> dependents() {
        return Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(p -> p.getDescription().getDepend().contains("cLibraries"))
                .toList();
    }
}
