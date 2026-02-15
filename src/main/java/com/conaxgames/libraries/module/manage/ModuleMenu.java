package com.conaxgames.libraries.module.manage;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.module.ModuleManager;
import com.conaxgames.libraries.module.type.Module;
import org.bukkit.entity.Player;

import java.util.HashMap;
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
    public int previousPageSlot(Player player) {
        return 45;
    }

    @Override
    public int nextPageSlot(Player player) {
        return 53;
    }
}
