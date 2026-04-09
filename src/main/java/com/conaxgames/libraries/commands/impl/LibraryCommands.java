package com.conaxgames.libraries.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.util.CC;
import org.bukkit.command.CommandSender;

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
