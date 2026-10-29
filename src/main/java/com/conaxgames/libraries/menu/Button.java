package com.conaxgames.libraries.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class Button {

    @FunctionalInterface
    public interface Click {
        void on(Player player, ClickType type);
    }

    final ItemStack icon;
    final Click click;
    final boolean editable;

    private Button(ItemStack icon, Click click, boolean editable) {
        this.icon = icon;
        this.click = click;
        this.editable = editable;
    }

    public static Button of(ItemStack icon, Click click) {
        return new Button(icon, click, false);
    }

    public static Button editable(ItemStack initial) {
        return new Button(initial, null, true);
    }
}
