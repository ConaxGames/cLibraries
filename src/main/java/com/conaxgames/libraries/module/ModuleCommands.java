package com.conaxgames.libraries.module;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.conaxgames.libraries.module.manage.ModuleMenu;
import com.conaxgames.libraries.module.type.Module;
import com.conaxgames.libraries.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("modules_%moduleCommandAlias")
@CommandPermission("%moduleCommandPermission")
public class ModuleCommands extends BaseCommand {

    private final ModuleManager moduleManager;

    public ModuleCommands(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Subcommand("list")
    @Description("Open the module management menu or lists the registered modules.")
    @CommandCompletion("@modules")
    public void onList(CommandSender sender) {
        if (sender instanceof Player) {
            new ModuleMenu(moduleManager).openMenu((Player) sender);
        } else {
            moduleManager.getModules().keySet().forEach(sender::sendMessage);
        }
    }

    @Subcommand("enable")
    @Description("Enable individual modules")
    @CommandCompletion("@modules")
    public void onEnable(CommandSender sender, Module module, boolean persistent) {
        sender.sendMessage(CC.PRIMARY + moduleManager.enableModule(module, persistent));
    }

    @Subcommand("disable")
    @Description("Disable individual modules")
    @CommandCompletion("@modules")
    public void onDisable(CommandSender sender, Module module, boolean persistent) {
        sender.sendMessage(CC.PRIMARY + moduleManager.disableModule(module, persistent));
    }

    @Default
    @HelpCommand
    @CatchUnknown
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
