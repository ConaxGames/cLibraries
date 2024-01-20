package com.conaxgames.libraries.module.manage;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.message.FormatUtil;
import com.conaxgames.libraries.module.ModuleManager;
import com.conaxgames.libraries.module.type.Module;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class ModuleButton extends Button {

    private final ModuleManager moduleManager;
    private final Module module;
    private final boolean enabled;

    public ModuleButton(ModuleManager moduleManager, Module module) {
        this.moduleManager = moduleManager;
        this.module = module;
        this.enabled = module.isEnabled();
    }

    @Override
    public String getName(Player var1) {
        return (this.enabled ? CC.GREEN + module.getName() : CC.RED + module.getName());
    }

    @Override
    public List<String> getDescription(Player var1) {
        String ownedPlugin = module.getJavaPlugin() == null ? "Unknown" : module.getJavaPlugin().getName();

        List<String> description = new ArrayList<>();
        description.add(CC.DARK_GRAY + ownedPlugin);
        description.add(" ");
        description.addAll(FormatUtil.wordWrap(CC.GRAY + module.getDescription()));
        description.add(" ");
        description.add(CC.GRAY + "Author: " + CC.WHITE + module.getAuthor());

        if (module.getRequiredPlugin() != null) {
            description.add(CC.GRAY + "Requires: " + CC.WHITE + module.getRequiredPlugin());
        }
        if (module.minimumServerVersion() != null) {
            description.add(CC.GRAY + "Minimum Version: " + CC.WHITE + module.minimumServerVersion());
        }

        description.add(" ");
        description.add(CC.YELLOW + (this.enabled ? "Click to disable." : "Click to enable."));
        return description;
    }

    @Override
    public Material getMaterial(Player var1) {
        return (this.enabled ? Material.LIME_DYE : Material.RED_DYE);
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        boolean persistent = clickType.isShiftClick();
        if (this.enabled) {
            moduleManager.disableModule(module, persistent);
        } else {
            moduleManager.enableModule(module, persistent);
        }

        player.sendMessage(enabled ?
                CC.RED + "You have disabled " + module.getIdentifier() + CC.GRAY + " (saved: " + persistent + ")" :
                CC.GREEN + "You have enabled " + module.getIdentifier() + CC.GRAY + " (saved: " + persistent + ")"
        );
    }
}
