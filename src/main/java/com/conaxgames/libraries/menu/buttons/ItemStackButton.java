package com.conaxgames.libraries.menu.buttons;

import com.conaxgames.libraries.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemStackButton extends Button {

    private ItemStack stack;

    public ItemStackButton(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return (stack);
    }

    public String getName(Player var1) {
        return null;
    }
    public List<String> getDescription(Player var1) {
        return null;
    }
    public Material getMaterial(Player var1) {
        return null;
    }
}
