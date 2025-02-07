package com.conaxgames.libraries.util;

import org.bukkit.inventory.ItemFlag;

public class ItemFlagHelper {

    /**
     * This method checks for the tooltip hiding flag based on the Spigot version.
     * In newer versions, it's HIDE_ADDITIONAL_TOOLTIP, while in older versions it's HIDE_POTION_EFFECTS.
     *
     * @return the correct ItemFlag based on the current version.
     */
    public static ItemFlag getHideTooltipFlag() {
        try {
            return ItemFlag.valueOf("HIDE_ADDITIONAL_TOOLTIP");
        } catch (IllegalArgumentException e) {
            return ItemFlag.valueOf("HIDE_POTION_EFFECTS");
        }
    }
}
