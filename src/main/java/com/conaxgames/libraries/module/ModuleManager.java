package com.conaxgames.libraries.module;

import co.aikar.commands.InvalidCommandArgument;
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
        commandManager.getCommandContexts().registerContext(Module.class, c -> {
            String argument = c.popFirstArg();
            Module module = getModuleByIdentifier(argument);
            if (module == null) {
                throw new InvalidCommandArgument("No module matching " + argument + " could be found.");
            }
            return module;
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("modules", c -> modules.keySet());
        commandManager.registerCommand(new ModuleCommands(this));
    }

    public void registerModule(Module module) {
        String id = module.getIdentifier();
        String required = module.getRequiredPlugin();
        if (required != null && !Bukkit.getPluginManager().isPluginEnabled(required)) {
            log("Required plugin " + required + " is missing. Module " + id + " cannot be registered.");
            return;
        }
        String version = module.getSupportedVersion();
        if (version != null && !VersioningChecker.supports(version)) {
            log("Supported version " + version + " is not met. Module " + id + " cannot be registered.");
            return;
        }
        if (modules.putIfAbsent(id, module) != null) {
            return;
        }
        log("Registered " + id + "!");
        if (module.getBoolean("enabled", false)) {
            enableModule(module, false);
        }
    }

    public String enableModule(Module module, boolean save) {
        if (!module.enabled) {
            module.reloadConfig();
            try {
                if (module instanceof Listener) {
                    Bukkit.getPluginManager().registerEvents((Listener) module, module.getJavaPlugin());
                }
                module.onEnable();
                module.enabled = true;
            } catch (Throwable t) {
                if (module instanceof Listener) {
                    HandlerList.unregisterAll((Listener) module);
                }
                return log("Failed to enable " + module.getIdentifier(), t);
            }
        }
        if (save) {
            module.set("enabled", true);
        }
        return log("Enabled " + module.getIdentifier() + "!");
    }

    public String disableModule(Module module, boolean save) {
        if (module.enabled) {
            try {
                module.onDisable();
                if (module instanceof Listener) {
                    HandlerList.unregisterAll((Listener) module);
                }
                module.enabled = false;
            } catch (Throwable t) {
                return log("Failed to disable " + module.getIdentifier(), t);
            }
        }
        if (save) {
            module.set("enabled", false);
        }
        return log("Disabled " + module.getIdentifier() + "!");
    }

    public Module getModuleByIdentifier(String identifier) {
        return modules.get(identifier.toLowerCase(Locale.ROOT));
    }

    private String log(String message, Throwable... throwables) {
        library.getLibraryLogger().toConsole("ModuleManager", message, throwables);
        return message;
    }
}
