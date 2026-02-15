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

    public String registerModule(Module module) {
        if (!module.canRegister()) {
            String message = module.getIdentifier() + " cannot be registered as one of its required plugins cannot be found.";
            library.getLibraryLogger().toConsole("ModuleManager", message);
            return message;
        }

        String id = module.getIdentifier().toLowerCase();
        if (!modules.containsKey(id)) {
            modules.put(id, new ModuleState(module, false));
            setupModule(module);
        }

        if (module.isConfiguredToEnable()) {
            enableModule(module, false);
        }

        String message = "Registered " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

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
            setModuleEnabled(module);
            state.enabled = true;
        }

        if (save) module.set("enabled", true);
        String message = "Enabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public String disableModule(Module module, boolean save) {
        String id = module.getIdentifier().toLowerCase();
        ModuleState state = modules.get(id);
        
        if (state == null) {
            String message = "Cannot disable " + module.getIdentifier() + " as it is not registered.";
            library.getLibraryLogger().toConsole("Module Manager", message);
            return message;
        }

        if (!state.enabled) {
            String message = module.getIdentifier() + " is not enabled, so you cannot disable it.";
            library.getLibraryLogger().toConsole("Module Manager", message);
            return message;
        }

        setModuleDisabled(module);
        state.enabled = false;
        if (save) module.set("enabled", false);

        String message = "Disabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public Map<String, Module> getModules() {
        return modules.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().module));
    }

    public Module getModuleByIdentifier(String identifier) {
        ModuleState state = modules.get(identifier.toLowerCase());
        return state != null ? state.module : null;
    }

    public boolean isModuleEnabled(Module module) {
        ModuleState state = modules.get(module.getIdentifier().toLowerCase());
        return state != null && state.enabled;
    }

    private void setupModule(Module module) {
        module.reloadConfig();
    }

    private void setModuleEnabled(Module module) {
        try {
            if (module instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) module, module.getJavaPlugin());
            }
            module.onEnable();
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("Module Manager", "Failed to enable module " + module.getName());
            t.printStackTrace();
        }
    }

    private void setModuleDisabled(Module module) {
        try {
            if (module instanceof Listener) {
                HandlerList.unregisterAll((Listener) module);
            }
            module.onDisable();
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("Module Manager", "Failed to disable module " + module.getName());
            t.printStackTrace();
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
