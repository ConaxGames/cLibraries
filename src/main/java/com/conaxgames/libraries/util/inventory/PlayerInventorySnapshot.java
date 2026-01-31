package com.conaxgames.libraries.util.inventory;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Getter
public final class PlayerInventorySnapshot {

    private final ItemStack[] contents;
    private final ItemStack[] armor;
    private final ItemStack[] extraContents;
    private final Collection<PotionEffect> potionEffects;
    private final int level;
    private final float exp;

    public PlayerInventorySnapshot(ItemStack[] contents, ItemStack[] armor, ItemStack[] extraContents,
                                   Collection<PotionEffect> potionEffects, int level, float exp) {
        this.contents = contents;
        this.armor = armor;
        this.extraContents = extraContents;
        this.potionEffects = potionEffects != null ? new ArrayList<>(potionEffects) : Collections.emptyList();
        this.level = level;
        this.exp = exp;
    }
}
