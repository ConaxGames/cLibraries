package com.conaxgames.libraries.hooks;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.event.impl.LibraryPluginEnableEvent;
import com.conaxgames.libraries.hooks.impl.conax.*;
import com.conaxgames.libraries.hooks.impl.spigot.PlaceholderHook;
import com.conaxgames.libraries.hooks.impl.spigot.TitleManagerHook;
import com.conaxgames.libraries.hooks.impl.spigot.VaultHook;
import com.conaxgames.libraries.hooks.impl.spigot.WorldGuardHook;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class HookManager implements Listener {

    public LibraryPlugin plugin;
    public Set<Hook> hooks = new HashSet<>();
    public Set<Hook> disabledHooks = new HashSet<>();

    public GamemodeType serverType = GamemodeType.UNKNOWN;

    public HookManager(LibraryPlugin plugin) {
        this.plugin = plugin;

        registerHook(new PlaceholderHook(HookType.PAPI));
        registerHook(new VaultHook(HookType.VAULT));
        registerHook(new TitleManagerHook(HookType.TITLE_MANAGER));
        registerHook(new WorldGuardHook(HookType.WORLD_GUARD));

        registerHook(new cSuiteHook(HookType.CSUITE));
        registerHook(new ArenaPvPHook(HookType.ARENAPVP));
        registerHook(new KitPvPHook(HookType.KITPVP));
        registerHook(new MangoHook(HookType.MANGO));
        registerHook(new NeonUHCHook(HookType.UHC));
        registerHook(new PearHCFHook(HookType.HCF));
        registerHook(new SkyblockHook(HookType.SKYBLOCK));
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
                LibraryPlugin.getInstance().sendConsoleMessage(CC.GRAY + "(This was determined through the " + hook.getPluginFromAnnotation() + " plugin being enabled.)");
                break;
            }
        }
    }

    /**
     * Register a {@link Hook} object using {@link HookAnnotation}.
     * @param hook The hook to attempt to find.
     */
    public void registerHook(Hook hook) {
        try {
            Class<?> clazz = hook.getClass();

            /* The clazz is not annotated with the @HookAnnotation so we are not going to enable it. */
            if (!clazz.isAnnotationPresent(HookAnnotation.class)) {
                plugin.getLogger().info("[cLibraries] The class " + clazz.getSimpleName() + " is not annotated with @HookAnnotation");
                return;
            }

            /* The annotation provides a name and description for the hook to display. */
            HookAnnotation annotation = clazz.getAnnotation(HookAnnotation.class);
            hook.pluginFromAnnotation = annotation.plugin();

            if (Bukkit.getPluginManager().isPluginEnabled(annotation.plugin())) {
                hook.setPlugin(Bukkit.getPluginManager().getPlugin(annotation.plugin()));

                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(EventHandler.class)) {
                        this.plugin.getServer().getPluginManager().registerEvents(hook, this.plugin);
                        break;
                    }
                }

                /* Adding the module to the set as it is enabled and usable. */
                hooks.add(hook);
                LibraryPlugin.getInstance().sendConsoleMessage(CC.PRIMARY + "Hooked into " + CC.SECONDARY + hook.getPluginFromAnnotation() + CC.PRIMARY + " version " + CC.SECONDARY + hook.getPlugin().getDescription().getVersion() + CC.PRIMARY + "."
                        + CC.GRAY + " (" + (hook.getPlugin().getDescription() == null ? "" : hook.getPlugin().getDescription().getDescription()) + CC.GRAY + ")");
            } else {
                disabledHooks.add(hook);
            }
        } catch (Exception e) {
            plugin.getLogger().info("[cLibraries] Unable to load hook because of exception: ");
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