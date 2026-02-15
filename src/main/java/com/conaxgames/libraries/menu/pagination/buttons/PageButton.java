package com.conaxgames.libraries.menu.pagination.buttons;

import com.conaxgames.libraries.event.impl.menu.MenuButtonNextEvent;
import com.conaxgames.libraries.event.impl.menu.MenuButtonPreviousEvent;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.menu.pagination.ViewAllPagesMenu;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class PageButton extends Button {

    private final int mod;
    private final PaginatedMenu menu;

    public PageButton(int mod, PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(this.menu).openMenu(player, false);
            return;
        }
        if (this.mod == -1) {
            new MenuButtonPreviousEvent(player, menu, this).call();
        } else if (this.mod == 1) {
            new MenuButtonNextEvent(player, menu, this).call();
        }
        int targetPage = this.menu.getPage() + this.mod;
        if (targetPage > 0 && this.menu.getPages(player) >= targetPage) {
            this.menu.modPage(player, this.mod);
        }
    }

    @Override
    public String getName(Player player) {
        return this.mod > 0 ? CC.GREEN + "Next Page" : CC.RED + "Previous Page";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public Material getMaterial(Player player) {
        return this.mod > 0 ? XMaterial.GREEN_DYE.get() : XMaterial.RED_DYE.get();
    }
}
