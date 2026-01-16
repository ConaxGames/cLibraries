package com.conaxgames.libraries.module.manage;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.module.ModuleManager;
import lombok.var;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ModuleMenu extends PaginatedMenu {

    private final ModuleManager moduleManager;

    public ModuleMenu() {
        this.moduleManager = LibraryPlugin.getInstance().getModuleManager();
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Modules";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        int index = 0;
        for (var entry : moduleManager.getModules().entrySet()) {
            buttons.put(index++, new ModuleButton(moduleManager, entry.getValue()));
        }
        return buttons;
    }

    @Override
    public int previousPageSlot(Player player) {
        return 0;
    }

    @Override
    public int nextPageSlot(Player player) {
        return 8;
    }
}
