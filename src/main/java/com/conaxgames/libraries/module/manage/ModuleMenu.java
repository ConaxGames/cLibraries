package com.conaxgames.libraries.module.manage;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.module.ModuleManager;
import com.conaxgames.libraries.module.type.Module;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleMenu extends PaginatedMenu {
    
    private final ModuleManager moduleManager;

    public ModuleMenu(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        setReservedRows(5);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Modules";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;
        for (Module module : moduleManager.getModules().values()) {
            buttons.put(index++, new ModuleButton(moduleManager, module));
        }
        return buttons;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int total = moduleManager.getModules().size();
        long enabled = moduleManager.getModules().values().stream()
                .filter(moduleManager::isModuleEnabled)
                .count();
        int disabled = (int) (total - enabled);

        Button infoButton = new Button() {
            @Override
            public String getName(Player player) {
                return CC.GOLD + "Module Statistics";
            }

            @Override
            public List<String> getDescription(Player player) {
                List<String> lore = new ArrayList<>();
                lore.add(CC.DARK_GRAY + "Module overview");
                lore.add(" ");
                lore.add(CC.GRAY + "Total Modules: " + CC.WHITE + total);
                lore.add(CC.GRAY + "Enabled: " + CC.GREEN + enabled);
                lore.add(CC.GRAY + "Disabled: " + CC.RED + disabled);
                lore.add(" ");
                return lore;
            }

            @Override
            public Material getMaterial(Player player) {
                return XMaterial.BOOK.get();
            }
        };
        buttons.put(49, infoButton);
        return buttons;
    }

    @Override
    public int previousPageSlot(Player player) {
        return 48;
    }

    @Override
    public int nextPageSlot(Player player) {
        return 50;
    }
}
