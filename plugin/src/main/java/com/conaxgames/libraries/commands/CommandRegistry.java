package com.conaxgames.libraries.commands;

import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.conaxgames.libraries.commands.impl.LibraryCommands;
import com.conaxgames.libraries.util.EnchantmentProcessor;
import com.conaxgames.libraries.util.PotionProcessor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommandRegistry {

    public CommandRegistry(PaperCommandManager commandManager) {

        new CommandMessages(commandManager);
        commandManager.enableUnstableAPI("help");
        loadContexts(commandManager);
        loadCompletions(commandManager);

        commandManager.registerCommand(new LibraryCommands());
    }

    /**
     * Registers the cLibraries command contexts.
     *
     * @param commandManager - paper command manager
     */
    public void loadContexts(PaperCommandManager commandManager) {
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

            Material material = Material.getMaterial(argument);
            if (material != null) {
                return new ItemStack(material);
            } else {
                throw new InvalidCommandArgument("No item matching " + argument + " could be found.");
            }
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
    }


    /**
     * Registers the cLibraries command completions.
     *
     * @param commandManager - paper command manager
     */
    public void loadCompletions(PaperCommandManager commandManager) {
        commandManager.getCommandCompletions().registerAsyncCompletion("plugins", c -> {
            List<String> values = new ArrayList<>();
            for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                values.add(plugin.getName().toLowerCase(Locale.ENGLISH));
            }
            return values;
        });

        commandManager.getCommandCompletions().registerAsyncCompletion("materials", c -> {
            List<String> values = new ArrayList<>();
            for (Material material : Material.values()) {
                values.add(material.name());
            }
            return values;
        });
    }
}
