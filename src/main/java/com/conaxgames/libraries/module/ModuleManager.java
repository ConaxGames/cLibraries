package com.conaxgames.libraries.module;

import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.module.type.Module;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ModuleManager {

    public LibraryPlugin library;
    public Map<String, Map.Entry<Module, Boolean>> modules = new HashMap<>();

    public ModuleManager(LibraryPlugin library, String commandAlias, String commandPermission) {
        this.library = library;

        PaperCommandManager commandManager = this.library.getPaperCommandManager();
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

        if (!modules.containsKey(module.getIdentifier().toLowerCase())) {
            modules.put(module.getIdentifier().toLowerCase(), new AbstractMap.SimpleEntry<>(module, false));
        }

        if (module.isConfiguredToEnable()) {
            this.enableModule(module, false);
        }

        String message = "Registered " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public String enableModule(Module module, boolean save) {
        Validate.notNull(module, "Module can not be null");
        Validate.notNull(module.getIdentifier(), "Identifier can not be null");

        // The module has been called to be enabled without being registered.
        // You should use #registerModule first rather than #enableModule.
        if (!isRegistered(module.getIdentifier())) {
            this.registerModule(module);
        }

        // Sets up the data files which are required for the module.
        module.setupFiles();
        module.reloadConfig();

        // Registers as listener and calls onReload & onEnable.
        this.setModuleEnabled(module);

        // Save the new value to memory & to file.
        modules.put(module.getIdentifier().toLowerCase(), new AbstractMap.SimpleEntry<>(module, true));
        if (save) module.set("enabled", true);

        String message = "Enabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public String reloadModule(Module module) {
        Validate.notNull(module, "Module can not be null");
        Validate.notNull(module.getIdentifier(), "Identifier can not be null");

        if (!module.isEnabled()) return module.getIdentifier() + " was not enabled, so can't be reloaded.";

        this.registerModule(module); // register the module with the status of "false"

        module.setupFiles(); // Sets up the data files which are required for the module.
        module.reloadConfig(); // Reload the settings.yml data

        module.onReload(); // Call the reload to the module

        String message = "Reloaded " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public String disableModule(Module module, boolean save) {
        boolean isAlreadyRegistered = isRegistered(module.getIdentifier());
        if (!isAlreadyRegistered) {
            String message = "Cannot disable " + module.getIdentifier() + " as it is not registered...";
            library.getLibraryLogger().toConsole("Module Manager", message);
            return message;
        }

        Map.Entry<Module, Boolean> moduleAndValue = this.modules.get(module.getIdentifier());
        if (!moduleAndValue.getValue()) {
            String message = module.getIdentifier() + " is not enabled, so you cannot disable it.";
            library.getLibraryLogger().toConsole("Module Manager", message);
            return message;
        }

        setModuleDisabled(module);

        modules.put(module.getIdentifier().toLowerCase(), new AbstractMap.SimpleEntry<>(module, false));
        if (save) module.set("enabled", false);

        String message = "Disabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public Set<String> getRegisteredIdentifiers() {
        return ImmutableSet.copyOf(modules.keySet());
    }

    public boolean isRegistered(String identifier) {
        return getRegisteredIdentifiers().stream().filter(id -> id.equalsIgnoreCase(identifier)).findFirst().orElse(null) != null;
    }

    public Map<String, Module> getModules() {
        return modules.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getKey()));
    }

    public Module getModuleByIdentifier(String identifier) {
        Map.Entry<Module, Boolean> entry = modules.get(identifier.toLowerCase());
        if (entry == null) return null;
        if (entry.getKey() == null) return null;
        return entry.getKey();
    }

    public boolean getStatus(Module module) {
        if (!this.modules.containsKey(module.getIdentifier().toLowerCase())) return false;

        return this.modules.get(module.getIdentifier().toLowerCase()).getValue();
    }

    protected boolean setModuleEnabled(Module module) {
        try {
            boolean listener = false;
            if (module instanceof Listener) {
                listener = true;
                Bukkit.getPluginManager().registerEvents((Listener) module, module.getJavaPlugin());
            }

            module.onReload();
            module.onEnable();
            library.getLibraryLogger().toConsole("Module Manager", "Enabled the " + module.getName() + " module. (listener: " + listener + ")");
            return true;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("Module Manager", "Failed to enable module " + module.getName());
            t.printStackTrace();
            return false;
        }
    }

    protected boolean setModuleDisabled(Module module) {
        try {
            boolean listener = false;
            if (module instanceof Listener) {
                listener = true;
                HandlerList.unregisterAll((Listener) module);
            }

            module.onDisable();
            library.getLibraryLogger().toConsole("Module Manager", "Disabled the " + module.getName() + " module. (listener: " + listener + ")");
            return true;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("Module Manager", "Failed to disable module " + module.getName());
            t.printStackTrace();
            return false;
        }
    }

    public void disableAllModules() {
        this.getModules().forEach((name, module) -> module.onDisable());
    }

    public void reloadAllModules() {
        this.getModules().forEach((name, module) -> this.reloadModule(module));
    }

}
