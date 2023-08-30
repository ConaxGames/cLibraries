package com.conaxgames.libraries.hooks;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.LibraryPluginEnableEvent;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HookManager implements Listener {

    public LibraryPlugin plugin;
    public Set<Hook> hooks = new HashSet<>();
    public Set<Hook> disabledHooks = new HashSet<>();

    public GamemodeType serverType = GamemodeType.UNKNOWN;

    public HookManager(LibraryPlugin plugin) {
        this.plugin = plugin;

        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            HookType type = Arrays.stream(HookType.values()).filter(t -> t.name().equalsIgnoreCase(p.getName())).findFirst().orElse(null);
            if (type == null) {
                continue;
            }
            registerHook(new HookWrapper(type, p));
        }
    }

    @EventHandler
    public void onPluginEnable(LibraryPluginEnableEvent event) {
        loadGamemodeTypeFromHooks();

        if (serverType == GamemodeType.UNKNOWN) {
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                for (Hook disabled : disabledHooks) {
                    if (plugin.isEnabled() && plugin.getName().contains(disabled.getPluginFromAnnotation())) {
                        if (disabled.getHookType().getGamemode() != GamemodeType.UNKNOWN) {
                            serverType = disabled.getHookType().getGamemode();
                            LibraryPlugin.getInstance().sendConsoleMessage(CC.PRIMARY + "Automatically determined the server is a " + CC.SECONDARY + serverType.getDisplay() + CC.PRIMARY + " server.");
                            LibraryPlugin.getInstance().sendConsoleMessage(CC.GRAY + "(This was determined through the " + plugin.getName() + " plugin being enabled.)");
                            break;
                        }
                    }
                }
            }
        }
    }

    public void loadGamemodeTypeFromHooks() {
        for (Hook hook : hooks) {
            if (hook.getHookType().getGamemode() != null && hook.getHookType().getGamemode() != GamemodeType.UNKNOWN) {
                serverType = hook.getHookType().getGamemode();
                LibraryPlugin.getInstance().sendConsoleMessage(CC.PRIMARY + "Automatically determined the server is a " + CC.SECONDARY + serverType.getDisplay() + CC.PRIMARY + " server.");
                LibraryPlugin.getInstance().sendConsoleMessage(CC.GRAY + "(This was determined through the " + hook.getPlugin().getName() + " plugin being enabled.)");
                break;
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
                LibraryPlugin.getInstance().sendConsoleMessage(CC.PRIMARY + "Hooked into " + CC.SECONDARY + hook.getHookType() + CC.PRIMARY + " version " + CC.SECONDARY + hook.getPlugin().getDescription().getVersion() + CC.PRIMARY + "."
                        + CC.GRAY + " (" + (hook.getPlugin().getDescription() == null ? "" : hook.getPlugin().getDescription().getDescription()) + CC.GRAY + ")");
            }
        } catch (Exception e) {
            plugin.getLogger().info("[cLibraries] Unable to load hook " + hook.getHookType().name() + " because of exception: ");
            e.printStackTrace();
        }
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

    public GamemodeType getServerType() {
        return this.serverType;
    }
}