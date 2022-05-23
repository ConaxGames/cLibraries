package com.conaxgames.libraries.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.impl.HookMenu;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@CommandAlias("library|lib|clibrary|clib")
@CommandPermission("library.admin")

public class LibraryCommands extends BaseCommand {

    @Default
    @Description("Prints basic information about the library command.")
    public void onLibrary(CommandSender sender) {
        sender.sendMessage(CC.PRIMARY + "cLibraries is currently on version " + CC.SECONDARY +
                LibraryPlugin.getInstance().getDescription().getVersion() + CC.PRIMARY + ".");
    }

    @Subcommand("hooks")
    @Description("Prints loaded plugins that hook into cLibraries' utilities.")
    public void onHook(CommandSender sender) {

        if (LibraryPlugin.getInstance().getHooked().size() <= 0) {
            throw new ConditionFailedException("No plugins are hooked.");
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(CC.PRIMARY + "Hooked plugins: ");
            for (Plugin plugin : LibraryPlugin.getInstance().getHooked()) {
                sender.sendMessage(CC.PRIMARY + "- " + CC.SECONDARY + plugin.getName() + CC.PRIMARY + " (" + plugin.getDescription().getVersion() + ")");
            }
            LibraryPlugin.getInstance().getHookManager().getHooks().forEach(hook -> sender.sendMessage(CC.PRIMARY + "- " + CC.SECONDARY + hook.getHookType().name() + CC.PRIMARY + " (" + hook.getPlugin().getDescription().getVersion() + ")"));
        } else {
            new HookMenu().openMenu((Player) sender);
        }

    }

    @Subcommand("check|c|ishooked")
    @CommandCompletion("@plugins")
    @Description("Checks whether a plugin requires cLibraries to load.")
    public void onCheck(CommandSender sender, Plugin plugin) {

        boolean hooked = plugin.getDescription().getDepend().contains("cLibraries");
        boolean loadBefore = plugin.getDescription().getLoadBefore().contains("cLibraries");
        boolean softDepend = plugin.getDescription().getSoftDepend().contains("cLibraries");

        sender.sendMessage(CC.GRAY + FormatUtil.possessiveString(plugin.getName()) + " relation to cLibraries:");
        sender.sendMessage(CC.PRIMARY + "Depend: " + CC.SECONDARY + hooked);
        sender.sendMessage(CC.PRIMARY + "Soft depend: " + CC.SECONDARY + softDepend);
        sender.sendMessage(CC.PRIMARY + "Load before: " + CC.SECONDARY + loadBefore);
    }

    @Subcommand("test")
    @Description("Magic command; Tests any function written into this command.")
    public void onTest(CommandSender sender, @Single String permission) {

        Set<Permissible> users = Bukkit.getPluginManager().getPermissionSubscriptions(permission);
        for (Permissible permissible : users) {
            if (permissible instanceof Player) {
                sender.sendMessage(((Player) permissible).getName() + " is subscribed to " + permission + ".");
            }
        }
    }

    @Subcommand("reload")
    @Description("Reload the settings for cLibraries!")
    public void onReload(CommandSender sender) {
        LibraryPlugin.getInstance().getSettings().reload();
        sender.sendMessage(CC.GREEN + "Reloaded settings.yml!");
    }

    @HelpCommand
    @CatchUnknown
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
