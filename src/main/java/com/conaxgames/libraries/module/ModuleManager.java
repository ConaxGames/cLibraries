package com.conaxgames.libraries.module;

import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.module.type.Module;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registers and controls the lifecycle of {@link Module}s. When constructed, registers a command
 * (alias and permission from {@link LibraryPlugin}) for listing, enabling, and disabling modules.
 * <p>
 * <b>Registration:</b> Call {@link #registerModule(Module)} for each module; if the module's
 * config has <code>enabled: true</code>, it is enabled immediately. Otherwise it is only set up
 * (config loaded) and can be enabled later via command or {@link #enableModule}.
 * <p>
 * <b>Persistence:</b> When enabling or disabling with <code>save = true</code>, the module's
 * config <code>enabled</code> key is updated so the state survives restarts.
 */
@Getter
public class ModuleManager {

    private final LibraryPlugin library;
    private final Map<String, ModuleState> modules = new HashMap<>();

    /**
     * Creates the manager and registers the module command. The command alias and permission are
     * replaced via ACF (e.g. <code>modules_%moduleCommandAlias</code>).
     *
     * @param library the library plugin instance
     * @param commandAlias alias for the module command (e.g. "module")
     * @param commandPermission permission root (e.g. "yourplugin.modules")
     */
    public ModuleManager(LibraryPlugin library, String commandAlias, String commandPermission) {
        this.library = library;

        PaperCommandManager commandManager = library.getPaperCommandManager();
        commandManager.getCommandReplacements().addReplacements(
                "moduleCommandAlias", commandAlias,
                "moduleCommandPermission", commandPermission
        );
        commandManager.registerCommand(new ModuleCommands(this));
    }

    /**
     * Registers a module. If {@link Module#canRegister()} is false, registration is skipped and
     * logged. If the module is already registered, only a log message is emitted. When newly
     * registered, config is loaded and the module is enabled if {@link Module#isConfiguredToEnable()}.
     */
    public void registerModule(Module module) {
        if (!module.canRegister()) {
            String message = module.getIdentifier() + " cannot be registered as one of its required plugins cannot be found.";
            library.getLibraryLogger().toConsole("ModuleManager", message);
            return;
        }

        String id = module.getIdentifier().toLowerCase();
        if (!modules.containsKey(id)) {
            modules.put(id, new ModuleState(module, false));
            if (module.isConfiguredToEnable()) {
                enableModule(module, false);
            } else {
                setupModule(module);
            }
        }

        String message = "Registered " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("ModuleManager", message);
    }

    /**
     * Enables a module: reloads config, runs {@link Module#onEnable()}, and if the module
     * implements {@link org.bukkit.event.Listener}, registers it with the server. If not yet
     * registered, registers it first. Use <code>save = true</code> to persist <code>enabled: true</code>.
     *
     * @param module the module to enable
     * @param save whether to write <code>enabled: true</code> to the module config
     * @return a short status message (e.g. "Enabled example!")
     */
    public String enableModule(Module module, boolean save) {
        Validate.notNull(module, "Module can not be null");
        Validate.notNull(module.getIdentifier(), "Identifier can not be null");

        String id = module.getIdentifier().toLowerCase();
        if (!modules.containsKey(id)) {
            registerModule(module);
        }

        ModuleState state = modules.get(id);
        if (!state.enabled) {
            setupModule(module);
            if (setModuleEnabled(module)) {
                state.enabled = true;
            } else {
                String failMessage = "Failed to enable " + module.getIdentifier();
                library.getLibraryLogger().toConsole("ModuleManager", failMessage);
                return failMessage;
            }
        }

        if (save) module.set("enabled", true);
        String message = "Enabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("ModuleManager", message);
        return message;
    }

    /**
     * Disables a module: runs {@link Module#onDisable()} and unregisters it as a listener if
     * applicable. Use <code>save = true</code> to persist <code>enabled: false</code>.
     *
     * @param module the module to disable
     * @param save whether to write <code>enabled: false</code> to the module config
     * @return a short status message, or an error message if not registered or not enabled
     */
    public String disableModule(Module module, boolean save) {
        Validate.notNull(module, "Module cannot be null");
        String id = module.getIdentifier().toLowerCase();
        ModuleState state = modules.get(id);
        if (state == null) {
            String message = "Cannot disable " + module.getIdentifier() + " as it is not registered.";
            library.getLibraryLogger().toConsole("ModuleManager", message);
            return message;
        }
        if (!state.enabled) {
            String message = module.getIdentifier() + " is not enabled, so you cannot disable it.";
            library.getLibraryLogger().toConsole("ModuleManager", message);
            return message;
        }
        if (!setModuleDisabled(module)) {
            String failMessage = "Failed to disable " + module.getIdentifier();
            library.getLibraryLogger().toConsole("ModuleManager", failMessage);
            return failMessage;
        }
        state.enabled = false;
        if (save) module.set("enabled", false);
        String message = "Disabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("ModuleManager", message);
        return message;
    }

    /**
     * Returns all registered modules by identifier. Use to list modules or check registration.
     */
    public Map<String, Module> getModules() {
        return modules.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().module));
    }

    /**
     * Returns the module with the given identifier (case-insensitive), or null if not registered.
     */
    public Module getModuleByIdentifier(String identifier) {
        ModuleState state = modules.get(identifier.toLowerCase());
        return state != null ? state.module : null;
    }

    /**
     * Returns whether the given module is currently enabled. False if the module is not registered.
     */
    public boolean isModuleEnabled(Module module) {
        ModuleState state = modules.get(module.getIdentifier().toLowerCase());
        return state != null && state.enabled;
    }

    private void setupModule(Module module) {
        module.reloadConfig();
    }

    private boolean setModuleEnabled(Module module) {
        try {
            if (module instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) module, module.getJavaPlugin());
            }
            module.onEnable();
            return true;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("ModuleManager", "Failed to enable module " + module.getName());
            t.printStackTrace();
            return false;
        }
    }

    private boolean setModuleDisabled(Module module) {
        try {
            module.onDisable();
            if (module instanceof Listener) {
                HandlerList.unregisterAll((Listener) module);
            }
            return true;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("ModuleManager", "Failed to disable module " + module.getName());
            t.printStackTrace();
            return false;
        }
    }

    private static class ModuleState {
        final Module module;
        boolean enabled;

        ModuleState(Module module, boolean enabled) {
            this.module = module;
            this.enabled = enabled;
        }
    }
}
