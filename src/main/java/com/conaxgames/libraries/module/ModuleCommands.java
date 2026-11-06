package com.conaxgames.libraries.module;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.message.CC;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.util.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("modules_%moduleCommandAlias")
@CommandPermission("%moduleCommandPermission")
public class ModuleCommands extends BaseCommand {

    private final ModuleManager moduleManager;

    public ModuleCommands(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Subcommand("list")
    @Description("Opens the module menu, or lists the registered modules from console.")
    public void onList(CommandSender sender) {
        if (!(sender instanceof Player)) {
            moduleManager.getModules().keySet().forEach(sender::sendMessage);
            return;
        }
        PaginatedMenu.builder("Modules")
                .entries(viewer -> {
                    List<Button> buttons = new ArrayList<>();
                    for (Module module : moduleManager.getModules().values()) {
                        boolean enabled = module.isEnabled();
                        String required = module.getRequiredPlugin();
                        String version = module.getSupportedVersion();

                        List<String> lore = new ArrayList<>();
                        lore.add("&8" + module.getJavaPlugin().getName());
                        lore.add(" ");
                        lore.addAll(FormatUtil.wordWrap("&7" + module.getDescription()));
                        lore.add(" ");
                        lore.add("&7Author: &f" + module.getAuthor());
                        if (required != null) {
                            lore.add("&7Requires: &f" + required);
                        }
                        if (version != null) {
                            lore.add("&7Version: &f" + version);
                        }
                        lore.add(" ");
                        lore.add("&e" + (enabled ? "Click to disable." : "Click to enable."));
                        lore.addAll(FormatUtil.wordWrap("&7(Use a Shift-Click to not save this change over reboots)"));

                        buttons.add(Button.of(ItemBuilder.of(enabled ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                                .name((enabled ? "&a" : "&c") + module.getName())
                                .lore(lore)
                                .build(), (player, type) -> {
                            boolean persistent = !type.isShiftClick();
                            player.sendMessage(CC.translate("&e" + (enabled
                                    ? moduleManager.disableModule(module, persistent)
                                    : moduleManager.enableModule(module, persistent)) + "&7 (saved: " + persistent + ")"));
                        }));
                    }
                    return buttons;
                })
                .autoUpdate(20L)
                .build()
                .open((Player) sender);
    }

    @Subcommand("enable")
    @Description("Enables a module.")
    @CommandCompletion("@modules")
    public void onEnable(CommandSender sender, Module module, boolean persistent) {
        sender.sendMessage(CC.translate("&e" + moduleManager.enableModule(module, persistent)));
    }

    @Subcommand("disable")
    @Description("Disables a module.")
    @CommandCompletion("@modules")
    public void onDisable(CommandSender sender, Module module, boolean persistent) {
        sender.sendMessage(CC.translate("&e" + moduleManager.disableModule(module, persistent)));
    }

    @Default
    @HelpCommand
    @CatchUnknown
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }
}
