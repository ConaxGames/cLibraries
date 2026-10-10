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

                        buttons.add(Button.builder(enabled ? XMaterial.GREEN_WOOL : XMaterial.RED_WOOL)
                                .name((enabled ? "&a" : "&c") + module.getName())
                                .lore(lore)
                                .onClick((p, type) -> {
                                    boolean persistent = !type.isShiftClick();
                                    String result = enabled
                                            ? moduleManager.disableModule(module, persistent)
                                            : moduleManager.enableModule(module, persistent);
                                    p.sendMessage(CC.translate("&e" + result + "&7 (saved: " + persistent + ")"));
                                })
                                .build());
                    }
                    return buttons;
                })
                .autoUpdate()
                .build();
    }
}
