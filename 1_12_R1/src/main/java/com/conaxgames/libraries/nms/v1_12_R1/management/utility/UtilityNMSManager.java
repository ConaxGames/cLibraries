package com.conaxgames.libraries.nms.v1_12_R1.management.utility;

import org.bukkit.inventory.meta.ItemMeta;

public class UtilityNMSManager extends com.conaxgames.libraries.nms.management.utility.UtilityNMSManager {

    @Override
    public String translateHex(String message) {
        return message;
    }

    @Override
    public ItemMeta setUnbreakable(ItemMeta meta, boolean value) {
        meta.setUnbreakable(value);
        return meta;
    }
}
