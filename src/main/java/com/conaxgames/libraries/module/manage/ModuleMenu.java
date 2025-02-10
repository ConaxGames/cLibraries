package com.conaxgames.libraries.module.manage;

import com.conaxgames.libraries.LibraryPlugin;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.module.ModuleManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleMenu extends PaginatedMenu {

    private final ModuleManager moduleManager;

    public ModuleMenu() {
        this.moduleManager = LibraryPlugin.getInstance().getModuleManager();
    }

    @Override
    public String getPrePaginatedTitle(Player var1) {
        return "Modules";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player var1) {
        Map<Integer, Button> buttons = new HashMap<>();

        AtomicInteger index = new AtomicInteger(0);
        moduleManager.getModules().forEach((id, module) ->
                buttons.put(index.getAndIncrement(), new ModuleButton(this.moduleManager, module)));

        return buttons;
    }

    @Override
    public int previousPageSlot(Player var1) {
        return 0;
    }

    @Override
    public int nextPageSlot(Player var1) {
        return 8;
    }

}
