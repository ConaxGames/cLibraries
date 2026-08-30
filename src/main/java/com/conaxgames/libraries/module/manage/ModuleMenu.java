package com.conaxgames.libraries.module.manage;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.Menu;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.message.CC;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.module.Module;
import com.conaxgames.libraries.module.ModuleManager;
import com.cryptomorin.xseries.XMaterial;

import java.util.ArrayList;
import java.util.List;

public final class ModuleMenu {

    private ModuleMenu() {
    }

    public static Menu create(ModuleManager moduleManager) {
        return PaginatedMenu.builder("Modules")
                .entries(player -> {
                    List<Button> buttons = new ArrayList<>();
                    for (Module module : moduleManager.getModules().values()) {
                        buttons.add(moduleButton(moduleManager, module));
                    }
                    return buttons;
                })
                .autoUpdate()
                .build();
    }

    private static Button moduleButton(ModuleManager moduleManager, Module module) {
        boolean enabled = module.isEnabled();

        List<String> lore = new ArrayList<>();
        lore.add("&8" + module.getJavaPlugin().getName());
        lore.add(" ");
        lore.addAll(FormatUtil.wordWrap("&7" + module.getDescription()));
        lore.add(" ");
        lore.add("&7Author: &f" + module.getAuthor());
        if (module.getRequiredPlugin() != null) {
            lore.add("&7Requires: &f" + module.getRequiredPlugin());
        }
        lore.add(" ");
        lore.add("&e" + (enabled ? "Click to disable." : "Click to enable."));
        lore.addAll(FormatUtil.wordWrap("&7(Use a Shift-Click to not save this change over reboots)"));

        return Button.builder(enabled ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                .name((enabled ? "&a" : "&c") + module.getName())
                .lore(lore)
                .onClick((player, type) -> {
                    boolean persistent = !type.isShiftClick();
                    String result = enabled
                            ? moduleManager.disableModule(module, persistent)
                            : moduleManager.enableModule(module, persistent);
                    player.sendMessage(CC.translate("&e" + result + "&7 (saved: " + persistent + ")"));
                })
                .build();
    }
}
