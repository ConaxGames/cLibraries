package com.conaxgames.libraries.commands.impl;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.message.CC;
import org.bukkit.command.CommandSender;

@CommandAlias("library|lib|clibrary|clib")
@CommandPermission("csuite.*")
public class LibraryCommands extends BaseCommand {

    @Default
    @Description("Prints basic information about the library command.")
    public void onLibrary(CommandSender sender) {
        sender.sendMessage(CC.translate("&ecLibraries is currently on version &6" +
                LibraryPlugin.getInstance().getPlugin().getDescription().getVersion() + "&e."));
    }

    @HelpCommand
    @CatchUnknown
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
