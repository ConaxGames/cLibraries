package com.conaxgames.libraries.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.impl.HookMenu;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.module.manage.ModuleMenu;
import com.conaxgames.libraries.module.type.Module;
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
@CommandPermission("csuite.*")
public class LibraryCommands extends BaseCommand {

    @Default
    @Description("Prints basic information about the library command.")
    public void onLibrary(CommandSender sender) {
        sender.sendMessage(CC.PRIMARY + "cLibraries is currently on version " + CC.SECONDARY +
                LibraryPlugin.getInstance().getPlugin().getDescription().getVersion() + CC.PRIMARY + ".");
    }

    @HelpCommand
    @CatchUnknown
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
