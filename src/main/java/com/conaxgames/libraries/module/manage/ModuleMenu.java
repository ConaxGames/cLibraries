package com.conaxgames.libraries.module.manage;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.buttons.CloseButton;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.module.ModuleManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ModuleMenu extends PaginatedMenu {

    private final ModuleManager moduleManager;

    public ModuleMenu(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.setReservedRows(5);
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
    public Map<Integer, Button> getGlobalButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();
        buttons.put(49, new CloseButton());
        return buttons;
    }

    @Override
    public int previousPageSlot(Player var1) {
        return 48;
    }

    @Override
    public int nextPageSlot(Player var1) {
        return 50;
    }

}
