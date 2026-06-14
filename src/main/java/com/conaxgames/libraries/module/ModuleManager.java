package com.conaxgames.libraries.module;

import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.LibraryPlugin;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
public class ModuleManager {

    private final LibraryPlugin library;
    private final Map<String, ModuleState> modules = new HashMap<>();

    public ModuleManager(LibraryPlugin library, String commandAlias, String commandPermission) {
        this.library = library;

        PaperCommandManager commandManager = library.getPaperCommandManager();
        commandManager.getCommandReplacements().addReplacements(
                "moduleCommandAlias", commandAlias,
                "moduleCommandPermission", commandPermission
        );
        commandManager.registerCommand(new ModuleCommands(this));
    }

    public void registerModule(Module module) {
        if (!module.canRegister()) {
            return;
        }

        String id = module.getIdentifier();
        if (modules.containsKey(id)) {
            return;
        }

        modules.put(id, new ModuleState(module, false));
        if (module.isConfiguredToEnable()) {
            enableModule(module, false);
        } else {
            setupModule(module);
        }

        library.getLibraryLogger().toConsole("ModuleManager", "Registered " + module.getIdentifier() + "!");
    }

    public String enableModule(Module module, boolean save) {
        String id = module.getIdentifier();
        if (!modules.containsKey(id)) {
            registerModule(module);
        }

        ModuleState state = modules.get(id);
        if (state == null) {
            return "Cannot enable " + module.getIdentifier() + " as it is not registered.";
        }
        if (!state.enabled) {
            setupModule(module);
            if (setModuleEnabled(module)) {
                state.enabled = true;
            } else {
                return "Failed to enable " + module.getIdentifier();
            }
        }

        if (save) module.set("enabled", true);
        String message = "Enabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("ModuleManager", message);
        return message;
    }

    public String disableModule(Module module, boolean save) {
        String id = module.getIdentifier();
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
            return "Failed to disable " + module.getIdentifier();
        }
        state.enabled = false;
        if (save) module.set("enabled", false);
        String message = "Disabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("ModuleManager", message);
        return message;
    }

    public Map<String, Module> getModules() {
        Map<String, Module> result = HashMap.newHashMap(modules.size());
        for (Map.Entry<String, ModuleState> entry : modules.entrySet()) {
            result.put(entry.getKey(), entry.getValue().module);
        }
        return result;
    }

    public Module getModuleByIdentifier(String identifier) {
        ModuleState state = modules.get(identifier.toLowerCase(Locale.ROOT));
        return state != null ? state.module : null;
    }

    public boolean isModuleEnabled(Module module) {
        ModuleState state = modules.get(module.getIdentifier());
        return state != null && state.enabled;
    }

    private void setupModule(Module module) {
        module.reloadConfig();
    }

    private boolean setModuleEnabled(Module module) {
        try {
            if (module instanceof Listener listener) {
                Bukkit.getPluginManager().registerEvents(listener, module.getJavaPlugin());
            }
            module.onEnable();
            return true;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("ModuleManager", "Failed to enable module " + module.getName(), t);
            return false;
        }
    }

    private boolean setModuleDisabled(Module module) {
        try {
            module.onDisable();
            if (module instanceof Listener listener) {
                HandlerList.unregisterAll(listener);
            }
            return true;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("ModuleManager", "Failed to disable module " + module.getName(), t);
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
