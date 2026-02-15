package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemStackButton extends Button {

    private final ItemStack stack;

    public ItemStackButton(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return this.stack;
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}
