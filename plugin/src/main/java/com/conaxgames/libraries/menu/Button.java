package com.conaxgames.libraries.menu;

import com.cryptomorin.xseries.XSound;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public abstract class Button {

    @Deprecated
    public static Button placeholder(Material material, byte data, String ... title) {
        return Button.placeholder(material, data, title == null || title.length == 0 ? "" : Joiner.on("").join(title));
    }

    public static Button placeholder(Material material) {
        return Button.placeholder(material, "");
    }

    public static Button placeholder(Material material, String title) {
        return Button.placeholder(material, (byte)0, title);
    }

    public static Button placeholder(final Material material, final byte data, final String title) {
        return new Button(){

            @Override
            public String getName(Player player) {
                return title;
            }

            @Override
            public List<String> getDescription(Player player) {
                return ImmutableList.of();
            }

            @Override
            public Material getMaterial(Player player) {
                return material;
            }

            @Override
            public int getDamageValue(Player player) {
                return data;
            }
        };
    }

    /**
     * Uses an existing ItemStack and creates a button using the ItemStack's attributes.
     *
     * @param item - desired ItemStack
     * @return - a button with 'item' attributes
     */
    public static Button fromItem(final ItemStack item) {
        return new Button(){

            @Override
            public ItemStack getButtonItem(Player player) {
                return item;
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
        };
    }

    public abstract String getName(Player var1);

    public abstract List<String> getDescription(Player var1);

    public abstract Material getMaterial(Player var1);

    public int getDamageValue(Player player) {
        return 0;
    }

    public void clicked(Player player, int slot, ClickType clickType) {
    }

    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    public boolean hideEnchants(Player player) {
        return false;
    }

    public boolean hideAttributes(Player player) {
        return false;
    }

    public boolean shinyItem(Player player) {
        return false;
    }

    public String skullOwner(Player player) {
        return null;
    }

    public int getAmount(Player player) {
        return 1;
    }

    public ItemStack getButtonItem(Player player) {
        ItemStack buttonItem = new ItemStack(this.getMaterial(player), this.getAmount(player), (short)this.getDamageValue(player));
        ItemMeta meta = buttonItem.getItemMeta();
        meta.setDisplayName(this.getName(player));
        List<String> description = this.getDescription(player);

        if (description != null) {
            meta.setLore(description);
        }

        if (skullOwner(player) != null) {
            SkullMeta skullMeta = (SkullMeta) meta;
            skullMeta.setOwner(skullOwner(player));
        }

        if (hideAttributes(player)) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS,
                    ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_PLACED_ON);
        }

        if (hideEnchants(player)) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (shinyItem(player)) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        buttonItem.setItemMeta(meta);

        return buttonItem;
    }

    public static void playFail(Player player) {
        XSound.BLOCK_GRASS_BREAK.play(player);
    }

    public static void playSuccess(Player player) {
        XSound.BLOCK_NOTE_BLOCK_HARP.play(player);
    }

    public static void playNeutral(Player player) {
        XSound.UI_BUTTON_CLICK.play(player);
    }

}

