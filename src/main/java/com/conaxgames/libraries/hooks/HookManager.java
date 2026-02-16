package com.conaxgames.libraries.hooks;

import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HookManager implements Listener {

    @Getter
    private final LibraryPlugin plugin;
    private final Map<String, Hook> hooks = new ConcurrentHashMap<>();

    public HookManager(LibraryPlugin plugin) {
        this.plugin = plugin;

        for (Plugin p : plugin.getPlugin().getServer().getPluginManager().getPlugins()) {
            if (p.isEnabled() && p != plugin.getPlugin()) {
                registerHook(new HookWrapper(p));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin enabledPlugin = event.getPlugin();
        if (enabledPlugin != plugin.getPlugin() && !hooks.containsKey(enabledPlugin.getName())) {
            registerHook(new HookWrapper(enabledPlugin));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin disabledPlugin = event.getPlugin();
        if (disabledPlugin != plugin.getPlugin()) {
            unregisterHook(disabledPlugin.getName());
        }
    }

    public void registerHook(Hook hook) {
        if (hook == null || hook.getPlugin() == null) {
            return;
        }

        String pluginName = hook.getPluginName();
        if (pluginName == null || pluginName.isEmpty()) {
            return;
        }

        try {
            hooks.put(pluginName.toLowerCase(), hook);
        } catch (Exception e) {
            plugin.getPlugin().getLogger().warning("[cLibraries] Unable to register hook " + pluginName + ": " + e.getMessage());
        }
    }

    public void unregisterHook(String pluginName) {
        if (pluginName != null) {
            hooks.remove(pluginName.toLowerCase());
        }
    }

    public List<Plugin> getDepends() {
        List<Plugin> libraryPluginList = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getDescription().getDepend().contains("cLibraries")) {
                libraryPluginList.add(plugin);
            }
        }
        return libraryPluginList;
    }

    public Hook getHookByPluginName(String pluginName) {
        if (pluginName == null) return null;
        return hooks.get(pluginName.toLowerCase());
    }

    public boolean isHooked(String pluginName) {
        return pluginName != null && hooks.containsKey(pluginName.toLowerCase());
    }

    public Set<Hook> getHooks() {
        return new HashSet<>(hooks.values());
    }
}
