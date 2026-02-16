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

/**
 * Manages plugin hooks, automatically tracking all enabled plugins and providing methods to
 * check if specific plugins are available. Hooks are registered when plugins enable and
 * unregistered when they disable.
 * <p>
 * <b>Usage:</b> Access via {@link LibraryPlugin#getHookManager()}, then use {@link #isHooked(String)}
 * to check if a plugin is available, or {@link #getHookByPluginName(String)} to get the hook instance.
 * Plugin name lookups are case-insensitive.
 */
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

    /**
     * Registers a hook. If the hook or its plugin is null, or the plugin name is empty, registration
     * is skipped. Duplicate registrations for the same plugin name replace the previous hook.
     *
     * @param hook The hook to register
     */
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

    /**
     * Unregisters a hook by plugin name. Safe to call even if the hook is not registered.
     *
     * @param pluginName The name of the plugin to unregister (case-insensitive)
     */
    public void unregisterHook(String pluginName) {
        if (pluginName != null) {
            hooks.remove(pluginName.toLowerCase());
        }
    }

    /**
     * Returns all plugins that depend on cLibraries (have "cLibraries" in their plugin.yml
     * depend list). Useful for identifying which plugins require this library.
     *
     * @return List of plugins that depend on cLibraries
     */
    public List<Plugin> getDepends() {
        List<Plugin> libraryPluginList = new ArrayList<>();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getDescription().getDepend().contains("cLibraries")) {
                libraryPluginList.add(plugin);
            }
        }
        return libraryPluginList;
    }

    /**
     * Gets the hook for a specific plugin by name. Returns null if the plugin is not hooked
     * or has been disabled.
     *
     * @param pluginName The name of the plugin (case-insensitive)
     * @return The hook instance, or null if not found
     */
    public Hook getHookByPluginName(String pluginName) {
        if (pluginName == null) return null;
        return hooks.get(pluginName.toLowerCase());
    }

    /**
     * Checks if a plugin is currently hooked (enabled and registered). Use this to check
     * plugin availability before accessing plugin-specific APIs.
     *
     * @param pluginName The name of the plugin to check (case-insensitive)
     * @return True if the plugin is hooked, false otherwise
     */
    public boolean isHooked(String pluginName) {
        return pluginName != null && hooks.containsKey(pluginName.toLowerCase());
    }

    /**
     * Returns all currently registered hooks. The returned set is a snapshot and modifications
     * to it will not affect the internal hook registry.
     *
     * @return Set of all registered hooks
     */
    public Set<Hook> getHooks() {
        return new HashSet<>(hooks.values());
    }
}
