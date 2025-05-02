package com.conaxgames.libraries.menu;

import com.cryptomorin.xseries.XEnchantment;
import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.XItemFlag;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

/**
 * Abstract class representing a clickable button in a menu interface.
 * Buttons are the building blocks of menu GUIs, handling both the visual representation
 * and click interactions for menu items.
 * 
 * <p>Each button can be customized with:</p>
 * <ul>
 *     <li>Custom display name</li>
 *     <li>Custom lore/description</li>
 *     <li>Custom material and data value</li>
 *     <li>Click handlers</li>
 *     <li>Visual effects (enchant glint, hidden attributes)</li>
 *     <li>Player skull support</li>
 * </ul>
 */
public abstract class Button {

    /**
     * Creates a placeholder button with specified material, data value, and title.
     * @param material The material type for the button
     * @param data The data value/durability for the material
     * @param title Optional title(s) to be joined into a single string
     * @return A new placeholder button
     * @deprecated Use {@link #placeholder(Material, String)} instead
     */
    @Deprecated
    public static Button placeholder(Material material, byte data, String... title) {
        return Button.placeholder(material, data, title == null || title.length == 0 ? "" : Joiner.on("").join(title));
    }

    /**
     * Creates a placeholder button with just a material.
     * @param material The material type for the button
     * @return A new placeholder button with empty title
     */
    public static Button placeholder(Material material) {
        return Button.placeholder(material, "");
    }

    /**
     * Creates a placeholder button with material and title.
     * @param material The material type for the button
     * @param title The title for the button
     * @return A new placeholder button
     */
    public static Button placeholder(Material material, String title) {
        return Button.placeholder(material, (byte)0, title);
    }

    /**
     * Creates a placeholder button with all properties specified.
     * @param material The material type for the button
     * @param data The data value/durability for the material
     * @param title The title for the button
     * @return A new placeholder button
     */
    public static Button placeholder(final Material material, final byte data, final String title) {
        return new Button() {
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
     * Creates a button from an existing ItemStack, preserving all its properties.
     * @param item The ItemStack to create the button from
     * @return A new button with the ItemStack's properties
     */
    public static Button fromItem(final ItemStack item) {
        return new Button() {
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

    /**
     * Gets the display name of the button for a specific player.
     * @param player The player viewing the button
     * @return The display name of the button
     */
    public abstract String getName(Player player);

    /**
     * Gets the lore/description of the button for a specific player.
     * @param player The player viewing the button
     * @return The list of lore lines
     */
    public abstract List<String> getDescription(Player player);

    /**
     * Gets the material type of the button for a specific player.
     * @param player The player viewing the button
     * @return The material type
     */
    public abstract Material getMaterial(Player player);

    /**
     * Gets the damage/durability value of the button for a specific player.
     * @param player The player viewing the button
     * @return The damage value
     */
    public int getDamageValue(Player player) {
        return 0;
    }

    /**
     * Handles click events on the button.
     * @param player The player who clicked
     * @param slot The inventory slot that was clicked
     * @param clickType The type of click performed
     */
    public void clicked(Player player, int slot, ClickType clickType) {
    }

    /**
     * Determines if the click event should be cancelled.
     * @param player The player who clicked
     * @param slot The inventory slot that was clicked
     * @param clickType The type of click performed
     * @return true to cancel the event (default), false to allow it
     */
    public boolean shouldCancel(Player player, int slot, ClickType clickType) {
        return true;
    }

    /**
     * Determines if enchantment glints should be hidden.
     * @param player The player viewing the button
     * @return true to hide enchants, false to show them
     */
    public boolean hideEnchants(Player player) {
        return true;
    }

    /**
     * Determines if item attributes should be hidden.
     * @param player The player viewing the button
     * @return true to hide attributes, false to show them
     */
    public boolean hideAttributes(Player player) {
        return true;
    }

    /**
     * Determines if the button should have an enchantment glint.
     * @param player The player viewing the button
     * @return true to add glint, false for no glint
     */
    public boolean shinyItem(Player player) {
        return false;
    }

    /**
     * Gets the skull owner for player head buttons.
     * @param player The player viewing the button
     * @return The name of the skull owner, or null if not a skull
     */
    public String skullOwner(Player player) {
        return null;
    }

    /**
     * Gets the stack size of the button.
     * @param player The player viewing the button
     * @return The amount to display (default 1)
     */
    public int getAmount(Player player) {
        return 1;
    }

    /**
     * Creates the ItemStack representation of the button.
     * This method combines all button properties into a final ItemStack.
     * @param player The player to create the button for
     * @return The complete ItemStack ready to be displayed
     */
    public ItemStack getButtonItem(Player player) {
        Material material = this.getMaterial(player);
        if (material == null) {
            material = XMaterial.BEDROCK.get();
        }

        ItemStack buttonItem = new ItemStack(material, this.getAmount(player), (short)this.getDamageValue(player));
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

        if (hideAttributes(player) || hideEnchants(player)) {
            XItemFlag.HIDE_ATTRIBUTES.set(meta);
            XItemFlag.HIDE_ATTRIBUTE_MODIFIERS.set(meta);
            XItemFlag.HIDE_ADDITIONAL_TOOLTIP.set(meta);
            XItemFlag.HIDE_ENCHANTS.set(meta);
        }

        if (shinyItem(player)) {
            meta.addEnchant(XEnchantment.UNBREAKING.get(), 1, true);
            XItemFlag.HIDE_ENCHANTS.set(meta);
        }

        buttonItem.setItemMeta(meta);
        return buttonItem;
    }

    /**
     * Plays a failure sound to the player.
     * @param player The player to play the sound for
     */
    public static void playFail(Player player) {
        XSound.BLOCK_GRASS_BREAK.play(player);
    }

    /**
     * Plays a success sound to the player.
     * @param player The player to play the sound for
     */
    public static void playSuccess(Player player) {
        XSound.BLOCK_NOTE_BLOCK_HARP.play(player);
    }

    /**
     * Plays a neutral/click sound to the player.
     * @param player The player to play the sound for
     */
    public static void playNeutral(Player player) {
        XSound.UI_BUTTON_CLICK.play(player);
    }
}

