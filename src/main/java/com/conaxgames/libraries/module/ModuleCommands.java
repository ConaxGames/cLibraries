package com.conaxgames.libraries.module;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.module.ModuleManager;
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
            new ModuleMenu().openMenu((Player) sender);
        } else {
            moduleManager.getModules().forEach((id, module) -> {
                sender.sendMessage(id);
            });
        }
    }

    @Subcommand("enable")
    @Description("Reload individual modules")
    @CommandCompletion("@modules")
    public void onEnable(CommandSender sender, Module module, boolean persistent) {
        String result = moduleManager.enableModule(module, persistent);
        sender.sendMessage(CC.PRIMARY + result);
    }

    @Subcommand("disable")
    @Description("Reload individual modules")
    @CommandCompletion("@modules")
    public void onDisable(CommandSender sender, Module module, boolean persistent) {
        String result = moduleManager.disableModule(module, persistent);
        sender.sendMessage(CC.PRIMARY + result);
    }

    @Subcommand("reload")
    @Description("Reload individual modules")
    @CommandCompletion("@modules")
    public void onReload(CommandSender sender, Module module) {
        String result = moduleManager.reloadModule(module);
        sender.sendMessage(CC.PRIMARY + result);
    }

    @Default
    @HelpCommand
    @CatchUnknown
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

}
