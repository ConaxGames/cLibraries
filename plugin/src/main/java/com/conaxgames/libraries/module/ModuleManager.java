package com.conaxgames.libraries.module;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.module.type.Module;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ModuleManager {

    public LibraryPlugin library;
    public final JavaPlugin plugin;
    public Map<String, Map.Entry<Module, Boolean>> modules = new HashMap<>();

    public ModuleManager(JavaPlugin plugin, LibraryPlugin library) {
        this.library = library;
        this.plugin = plugin;
    }

    public boolean enableOrReloadModule(Module module) {
        Validate.notNull(module, "Module can not be null");
        Validate.notNull(module.getIdentifier(), "Identifier can not be null");

        boolean status = false;
        boolean isAlreadyRegistered = isRegistered(module.getIdentifier());

        // Checks if the module depends on any other plugins and returns false if they are not found.
        if (!module.canRegister()) {
            library.getLibraryLogger().toConsole("ModuleManager", module.getIdentifier() + " cannot register.");
            return false;
        }

        // Sets up the data files which are required for the module.
        module.setupFiles();

        if (isAlreadyRegistered) {
            module.reloadConfig(); // Reload the settings.yml data
            module.onReload(); // Call the reload to the module
            status = true;
        } else if (module.isConfiguredToEnable()) {
            // enable the module here as it is not already registered.
            status = setModuleEnabled(module);
        }

        modules.put(module.getIdentifier().toLowerCase(), new AbstractMap.SimpleEntry<>(module, status));
        library.getLibraryLogger().toConsole("Module Manager", "Registered module: " + module.getName() + " with the status: " + status);
        return status;
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

    protected boolean setModuleEnabled(Module module) {
        try {
            module.onReload();
            module.onEnable();
            library.getLibraryLogger().toConsole("Module Manager", "Enabled the " + module.getName() + " module");
            return true;
        } catch (Throwable t) {
            library.getLibraryLogger().toConsole("Module Manager", "Failed to enable module " + module.getName());
            t.printStackTrace();
            return false;
        }
    }

    protected boolean setModuleDisabled(Module module) {
        try {
            module.onDisable();
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

}
