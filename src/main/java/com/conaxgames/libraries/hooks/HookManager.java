package com.conaxgames.libraries.hooks;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.LibraryPluginEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class HookManager implements Listener {

    public LibraryPlugin plugin;
    public Set<Hook> hooks = new HashSet<>();
    public Set<Hook> disabledHooks = new HashSet<>();

    public HookManager(LibraryPlugin plugin) {
        this.plugin = plugin;

        for (Plugin p : plugin.getPlugin().getServer().getPluginManager().getPlugins()) {
            HookType type = Arrays.stream(HookType.values()).filter(t -> t.name().equalsIgnoreCase(p.getName())).findFirst().orElse(null);
            if (type == null) {
                continue;
            }
            registerHook(new HookWrapper(type, p));
        }
    }

    @EventHandler
    public void onPluginEnable(LibraryPluginEnableEvent event) {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            Iterator<Hook> iterator = disabledHooks.iterator();
            while (iterator.hasNext()) {
                Hook disabled = iterator.next();
                if (plugin.isEnabled() && plugin.getName().equals(disabled.getPluginFromAnnotation())) {
                    hooks.add(disabled);
                    iterator.remove();
                    break;
                }
            }
        }
    }

    /**
     * Register a {@link Hook}.
     * @param hook The hook to attempt to find.
     */
    public void registerHook(Hook hook) {
        try {
            hooks.add(hook);
            if (LibraryPlugin.getInstance().getSettings().debug) {
                LibraryPlugin.getInstance().getLibraryLogger().toConsole("Hook Manager",
                        "Hooked into " + hook.getHookType() + " version " + hook.getPlugin().getDescription().getVersion() + "."
                                + " (" + hook.getPlugin().getDescription().getDescription() + ")");
            }
        } catch (Exception e) {
            plugin.getPlugin().getLogger().info("[cLibraries] Unable to load hook " + hook.getHookType().name() + " because of exception: ");
            e.printStackTrace();
        }
    }

    public List<Plugin> getDepends() {
        List<Plugin> libraryPluginList = new ArrayList<>();
        Plugin[] bukkitPluginList = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : bukkitPluginList) {
            if (plugin.getDescription().getDepend().contains("cLibraries")) {
                libraryPluginList.add(plugin);
            }
        }

        return libraryPluginList;
    }

    public Hook getHookByPluginName(String pluginName) {
        return getHooks().stream().filter(hook -> hook.getPluginFromAnnotation().equals(pluginName)).findFirst().orElse(null);
    }

    public Hook getHookByType(HookType type) {
        return getHooks().stream().filter(hook -> hook.getHookType().equals(type)).findFirst().orElse(null);
    }

    public boolean isHooked(HookType type) {
        return (getHookByType(type) != null);
    }

    public LibraryPlugin getPlugin() {
        return this.plugin;
    }

    public Set<Hook> getHooks() {
        return this.hooks;
    }

    public Set<Hook> getDisabledHooks() {
        return this.disabledHooks;
    }
}
