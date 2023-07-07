package com.conaxgames.libraries.nms.v1_9_R3.management.utility;

import org.bukkit.inventory.meta.ItemMeta;

public class UtilityNMSManager extends com.conaxgames.libraries.nms.management.utility.UtilityNMSManager {

    @Override
    public String translateHex(String message) {
        return message;
    }

    @Override
    public ItemMeta setUnbreakable(ItemMeta meta, boolean value) {
        meta.spigot().setUnbreakable(value);
        return meta;
    }
}
