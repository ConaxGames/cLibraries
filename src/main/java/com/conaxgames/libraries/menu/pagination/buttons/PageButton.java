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

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class PageButton extends Button {
    private int mod;
    private PaginatedMenu menu;

    @ConstructorProperties(value={"mod", "menu"})
    public PageButton(int mod, PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            new ViewAllPagesMenu(this.menu).openMenu(player, false);
            return;
        }
        
        if (!this.hasNext(player)) {
            return;
        }

        if (this.mod == -1) { // previous
            new MenuButtonPreviousEvent(player, menu, this).call();
        } else if (this.mod == 1) { // next
            new MenuButtonNextEvent(player, menu, this).call();
        }

        this.menu.modPage(player, this.mod);
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
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
        return this.mod > 0 ? XMaterial.GREEN_DYE.parseMaterial() : XMaterial.RED_DYE.parseMaterial();
    }

}

