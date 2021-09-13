package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.util.CC;
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
            new ViewAllPagesMenu(this.menu).openMenu(player);
        } else if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
        }
    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }

    @Override
    public String getName(Player player) {
        return this.mod > 0 ? CC.GREEN + "Next Page" /*+ " (" + (this.menu.getPage() + 1) + "/" + this.menu.getPages(player) + ")"*/ : CC.GREEN + "Previous Page" /* + " (" + (this.menu.getPage() - 1) + "/" + this.menu.getPages(player) + ")" */;
    }

    @Override
    public List<String> getDescription(Player player) {
        if (!this.hasNext(player)) {
            return new ArrayList<String>();
        }
        ArrayList<String> description = new ArrayList<>();
        description.add(CC.GRAY + "(" + this.menu.getPage() + "/" + this.menu.getPages(player) + ")");
        description.add(" ");
        description.add(CC.YELLOW + "Click to turn page!");

        return description;
    }
//
//    @Override
//    public int getDamageValue(Player player) {
//        return this.hasNext(player) ? (byte)11 : 7;
//    }

    @Override
    public Material getMaterial(Player player) {
        return Material.ARROW;
    }

}

