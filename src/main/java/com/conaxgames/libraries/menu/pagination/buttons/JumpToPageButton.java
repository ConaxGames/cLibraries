package com.conaxgames.libraries.menu.pagination.buttons;

import com.conaxgames.libraries.event.impl.menu.MenuButtonJumpToEvent;
import com.conaxgames.libraries.menu.Button;
import com.conaxgames.libraries.menu.pagination.PaginatedMenu;
import com.conaxgames.libraries.util.CC;
import com.cryptomorin.xseries.XMaterial;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class JumpToPageButton extends Button {

    private final int page;
    private final PaginatedMenu menu;
    @Setter
    private boolean glowing = false;

    @ConstructorProperties(value={"page", "menu"})
    public JumpToPageButton(int page, PaginatedMenu menu) {
        this.page = page;
        this.menu = menu;
    }

    @Override
    public String getName(Player player) {
        String prefix = this.glowing ? CC.GOLD + "► " : CC.SECONDARY;
        return prefix + "Page " + this.page;
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = new ArrayList<>();
        
        int currentPage = this.menu.getPage();
        int totalPages = this.menu.getPages(player);
        
        if (this.page == currentPage) {
            description.add(CC.GRAY + "This is your current page");
        } else {
            description.add(CC.GRAY + "Current Page: " + currentPage);
            description.add(CC.GRAY + "Total Pages: " + totalPages);
            description.add(CC.GRAY + "Target Page: " + this.page);
        }
        
        description.add(" ");
        description.add(CC.YELLOW + "Click to jump to this page!");

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return this.glowing ? XMaterial.WRITABLE_BOOK.parseMaterial() : XMaterial.BOOK.parseMaterial();
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

