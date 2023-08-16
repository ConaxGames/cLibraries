package com.conaxgames.libraries.menu.pagination;

import com.conaxgames.libraries.event.impl.menu.MenuButtonJumpToEvent;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class JumpToPageButton extends Button {

    private int page;
    private PaginatedMenu menu;

    @ConstructorProperties(value={"page", "menu"})
    public JumpToPageButton(int page, PaginatedMenu menu) {
        this.page = page;
        this.menu = menu;
    }

    @Override
    public String getName(Player player) {
        return CC.SECONDARY + "Page " + this.page;
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();

        description.add(CC.PRIMARY + "Click to view!");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BOOK;
    }

    @Override
    public int getAmount(Player player) {
        return this.page;
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        new MenuButtonJumpToEvent(player, menu, this).call();

        this.menu.modPage(player, this.page - this.menu.getPage());
    }

}

