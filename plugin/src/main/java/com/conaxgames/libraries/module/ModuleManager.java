package com.conaxgames.libraries.module;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.module.type.Module;
import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ModuleManager {

    public LibraryPlugin library;
    public final JavaPlugin javaPlugin;
    public Map<String, Map.Entry<Module, Boolean>> modules = new HashMap<>();

    public ModuleManager(JavaPlugin plugin, LibraryPlugin library) {
        this.library = library;
        this.javaPlugin = plugin;
    }

    public String registerModule(Module module) {
        if (module.canRegister() && !modules.containsKey(module.getIdentifier().toLowerCase())) {
            modules.put(module.getIdentifier().toLowerCase(), new AbstractMap.SimpleEntry<>(module, false));
        } else {
            String message = module.getIdentifier() + " cannot be registered as one of its required plugins cannot be found.";
            library.getLibraryLogger().toConsole("ModuleManager", message);
            return message;
        }

        if (module.isConfiguredToEnable()) {
            this.enableModule(module);
        }

        String message = "Registered " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public String enableModule(Module module) {
        Validate.notNull(module, "Module can not be null");
        Validate.notNull(module.getIdentifier(), "Identifier can not be null");

        // The module has been called to be enabled without being registered.
        // You should use #registerModule first rather than #enableModule.
        if (!isRegistered(module.getIdentifier())) {
            this.registerModule(module);
        }

        module.setupFiles(); // Sets up the data files which are required for the module.
        module.reloadConfig(); // Reload the settings.yml data

        // Registers as listener and calls onReload & onEnable.
        this.setModuleEnabled(module);

        // Save the new value to memory & to file.
        modules.put(module.getIdentifier().toLowerCase(), new AbstractMap.SimpleEntry<>(module, true));
        module.set("enabled", true);

        String message = "Enabled " + module.getIdentifier() + "!";
        library.getLibraryLogger().toConsole("Module Manager", message);
        return message;
    }

    public String reloadModule(Module module) {
        Validate.notNull(module, "Module can not be null");
        Validate.notNull(module.getIdentifier(), "Identifier can not be null");

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

    protected boolean setModuleEnabled(Module module) {
        try {
            boolean listener = false;
            if (module instanceof Listener) {
                listener = true;
                Bukkit.getPluginManager().registerEvents((Listener) module, this.getJavaPlugin());
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
