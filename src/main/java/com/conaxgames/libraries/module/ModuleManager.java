package com.conaxgames.libraries.module;

import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.util.VersioningChecker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ModuleManager {

    private final LibraryPlugin library;
    @Getter
    private final Map<String, Module> modules = new HashMap<>();

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
        String required = module.getRequiredPlugin();
        if (required != null && !Bukkit.getPluginManager().isPluginEnabled(required)) {
            library.getLibraryLogger().toConsole("Module",
                    "Required plugin " + required + " is missing. Module " + module.getIdentifier() + " cannot be registered.");
            return;
        }

        String version = module.getSupportedVersion();
        if (version != null && VersioningChecker.getInstance().isServerVersionBefore(version)) {
            library.getLibraryLogger().toConsole("Module",
                    "Supported version " + version + " is not met. Module " + module.getIdentifier() + " cannot be registered.");
            return;
        }

        String id = module.getIdentifier();
        if (modules.containsKey(id)) {
            return;
        }

        modules.put(id, module);
        if (module.isConfiguredToEnable()) {
            enableModule(module, false);
        }
        library.getLibraryLogger().toConsole("ModuleManager", "Registered " + id + "!");
    }

    public String enableModule(Module module, boolean save) {
        String id = module.getIdentifier();
        if (!modules.containsKey(id)) {
            return "Cannot enable " + id + " as it is not registered.";
        }

        if (!module.enabled) {
            module.reloadConfig();
            try {
                if (module instanceof Listener listener) {
                    Bukkit.getPluginManager().registerEvents(listener, module.getJavaPlugin());
                }
                module.onEnable();
                module.enabled = true;
            } catch (Throwable t) {
                if (module instanceof Listener listener) {
                    HandlerList.unregisterAll(listener);
                }
                library.getLibraryLogger().toConsole("ModuleManager", "Failed to enable module " + module.getName(), t);
                return "Failed to enable " + id;
            }
        }

        if (save) {
            module.set("enabled", true);
        }
        String message = "Enabled " + id + "!";
        library.getLibraryLogger().toConsole("ModuleManager", message);
        return message;
    }

    public String disableModule(Module module, boolean save) {
        String id = module.getIdentifier();
        if (!modules.containsKey(id)) {
            return "Cannot disable " + id + " as it is not registered.";
        }
        if (!module.enabled) {
            return id + " is not enabled, so you cannot disable it.";
        }

        try {
            module.onDisable();
            if (module instanceof Listener listener) {
                HandlerList.unregisterAll(listener);
            }
            module.enabled = false;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("ModuleManager", "Failed to disable module " + module.getName(), t);
            return "Failed to disable " + id;
        }

        if (save) {
            module.set("enabled", false);
        }
        String message = "Disabled " + id + "!";
        library.getLibraryLogger().toConsole("ModuleManager", message);
        return message;
    }

    public Module getModuleByIdentifier(String identifier) {
        return modules.get(identifier.toLowerCase(Locale.ROOT));
    }
}
