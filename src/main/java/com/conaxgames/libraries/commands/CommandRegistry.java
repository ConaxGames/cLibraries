package com.conaxgames.libraries.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.commands.impl.LibraryCommands;
import com.conaxgames.libraries.module.type.Module;
import com.conaxgames.libraries.util.resolvers.EnchantmentProcessor;
import com.conaxgames.libraries.util.resolvers.ItemTypeProcessor;
import com.conaxgames.libraries.util.resolvers.PotionProcessor;
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
            Material material = ItemTypeProcessor.resolve(argument);
            if (material != null) {
                return new ItemStack(material, 1);
            }
            throw new InvalidCommandArgument("No item matching " + argument + " could be found.");
        });

        commandManager.getCommandContexts().registerContext(Material.class, c -> {
            String argument = c.popFirstArg();
            Material material = ItemTypeProcessor.resolve(argument);
            if (material != null) {
                return material;
            }
            throw new InvalidCommandArgument("No item matching " + argument + " could be found.");
        });

        commandManager.getCommandContexts().registerContext(Enchantment.class, c -> {
            String argument = c.popFirstArg();
            Enchantment enchantment = EnchantmentProcessor.resolve(argument);
            if (enchantment != null) {
                return enchantment;
            }
            throw new InvalidCommandArgument("No enchantment matching " + argument + " could be found.");
        });

        commandManager.getCommandContexts().registerContext(PotionEffectType.class, c -> {
            String argument = c.popFirstArg();
            PotionEffectType effectType = PotionProcessor.resolve(argument);
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

        commandManager.getCommandCompletions().registerAsyncCompletion("materials", c ->
                ItemTypeProcessor.completions());

        commandManager.getCommandCompletions().registerAsyncCompletion("enchantments", c ->
                EnchantmentProcessor.completions());

        commandManager.getCommandCompletions().registerAsyncCompletion("potions", c ->
                PotionProcessor.completions());

        commandManager.getCommandCompletions().registerAsyncCompletion("modules", c ->
                libraryPlugin.getModuleManager().getModules()
                        .keySet()
                        .stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList()));
    }
}
