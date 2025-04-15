package com.conaxgames.libraries.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.commands.impl.LibraryCommands;
import com.conaxgames.libraries.module.type.Module;
import com.conaxgames.libraries.util.EnchantmentProcessor;
import com.conaxgames.libraries.util.PotionProcessor;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CommandRegistry {

    public CommandRegistry(LibraryPlugin libraryPlugin, PaperCommandManager commandManager) {
        new CommandMessages(commandManager);
        commandManager.enableUnstableAPI("help");
        loadContexts(libraryPlugin, commandManager);
        loadCompletions(libraryPlugin, commandManager);

        commandManager.registerCommand(new LibraryCommands());
    }

    /**
     * Registers the cLibraries command contexts.
     *
     * @param commandManager - paper command manager
     */
    public void loadContexts(LibraryPlugin libraryPlugin, PaperCommandManager commandManager) {
        commandManager.getCommandContexts().registerContext(Plugin.class, c -> {
            String argument = c.popFirstArg();
            Plugin plugin = Bukkit.getPluginManager().getPlugin(argument);
            if (plugin != null) {
                return plugin;
            } else {
                throw new InvalidCommandArgument("No plugin matching " + argument + " could be found.");
            }
        });

        commandManager.getCommandContexts().registerContext(ItemStack.class, c -> {
            String argument = c.popFirstArg();
            try {
                XMaterial material = XMaterial.valueOf(argument);
                Material parsedMaterial = material.get();
                if (parsedMaterial != null) {
                    return new ItemStack(parsedMaterial);
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            }

            throw new InvalidCommandArgument("No item matching " + argument + " could be found.");
        });

        commandManager.getCommandContexts().registerContext(Enchantment.class, c -> {
            String argument = c.popFirstArg();
            Enchantment enchantment = Enchantment.getByName(EnchantmentProcessor.process(argument.toUpperCase()));
            if (enchantment != null) {
                return enchantment;
            } else {
                throw new InvalidCommandArgument("No enchantment matching " + argument + " could be found.");
            }
        });

        commandManager.getCommandContexts().registerContext(PotionEffectType.class, c -> {
            String argument = c.popFirstArg();
            PotionEffectType effectType = PotionEffectType.getByName(PotionProcessor.process(argument.toUpperCase()));
            if (effectType != null) {
                return effectType;
            } else {
                throw new InvalidCommandArgument("No potion effect matching " + argument + " could be found.");
            }
        });

        commandManager.getCommandContexts().registerContext(Module.class, c -> {
            String argument = c.popFirstArg();

            Module module = libraryPlugin.getModuleManager().getModuleByIdentifier(argument);
            if (module == null) {
                throw new InvalidCommandArgument("No module matching " + argument + " could be found.");
            }

            return module;
        });
    }


    /**
     * Registers the cLibraries command completions.
     *
     * @param commandManager - paper command manager
     */
    public void loadCompletions(LibraryPlugin libraryPlugin, PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerAsyncCompletion("plugins", c -> {
            List<String> values = new ArrayList<>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                values.add(plugin.getName().toLowerCase(Locale.ENGLISH));
            }
            return values;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("materials", c -> {
            List<String> values = new ArrayList<>();
            for (XMaterial material : XMaterial.values()) {
                values.add(material.name());
            }
            return values;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("enchantments", c ->
                EnchantmentProcessor.enchantmentmap.keySet());

        commandManager.getCommandCompletions().registerAsyncCompletion("modules", c ->
                libraryPlugin.getModuleManager().getModules()
                        .keySet()
                        .stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList()));
    }
}
