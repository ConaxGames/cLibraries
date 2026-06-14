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
import java.util.Map;

public final class ModuleMenu {

    private ModuleMenu() {
    }

    public static Menu create(ModuleManager moduleManager) {
        return PaginatedMenu.builder("Modules")
                .rows(6)
                .maxPerPage(45)
                .previousSlot(48)
                .nextSlot(50)
                .set(49, statistics(moduleManager))
                .entries(player -> moduleButtons(moduleManager))
                .autoUpdate()
                .build();
    }

    private static List<Button> moduleButtons(ModuleManager moduleManager) {
        List<Button> buttons = new ArrayList<>();
        for (Module module : moduleManager.getModules().values()) {
            buttons.add(moduleButton(moduleManager, module));
        }
        return buttons;
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

    private static Button statistics(ModuleManager moduleManager) {
        Map<String, Module> registered = moduleManager.getModules();
        int total = registered.size();
        long enabled = registered.values().stream().filter(moduleManager::isModuleEnabled).count();

        return Button.builder(XMaterial.BOOK)
                .name("&6Module Statistics")
                .lore(
                        "&8Module overview",
                        " ",
                        "&7Total Modules: &f" + total,
                        "&7Enabled: &a" + enabled,
                        "&7Disabled: &c" + (total - enabled),
                        " "
                )
                .build();
    }
}
