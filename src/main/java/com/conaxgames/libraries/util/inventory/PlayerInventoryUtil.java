package com.conaxgames.libraries.util.inventory;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public final class PlayerInventoryUtil {

    public static PlayerInventorySnapshot snapshot(Player player) {
        PlayerInventory inv = player.getInventory();
        return new PlayerInventorySnapshot(
                inv.getContents().clone(),
                inv.getArmorContents().clone(),
                inv.getExtraContents().clone(),
                new ArrayList<>(player.getActivePotionEffects()),
                player.getLevel(),
                player.getExp()
        );
    }

    public static void clear(Player player) {
        if (player.getOpenInventory() != null) player.getOpenInventory().setCursor(null);
        player.clearActiveItem();
        PlayerInventory inv = player.getInventory();
        inv.clear();
        inv.setArmorContents(new ItemStack[4]);
        inv.setExtraContents(new ItemStack[inv.getExtraContents().length]);
        player.getActivePotionEffects().forEach(e -> player.removePotionEffect(e.getType()));
        player.setFireTicks(0);
        player.setAbsorptionAmount(0);
        AttributeInstance maxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) player.setHealth(maxHealth.getValue());
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        player.setLevel(0);
        player.setExp(0.0f);
        player.sendHealthUpdate();
    }

    public static void restore(Player player, PlayerInventorySnapshot snapshot) {
        if (snapshot == null) return;
        clear(player);
        PlayerInventory inv = player.getInventory();
        inv.setContents(snapshot.getContents());
        inv.setArmorContents(snapshot.getArmor());
        inv.setExtraContents(snapshot.getExtraContents());
        snapshot.getPotionEffects().forEach(player::addPotionEffect);
        player.setLevel(snapshot.getLevel());
        player.setExp(snapshot.getExp());
    }
}
