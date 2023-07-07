package com.conaxgames.libraries.nms.management.utility;

import org.bukkit.inventory.meta.ItemMeta;

public abstract class UtilityNMSManager {

    public abstract String translateHex(String message);

    public abstract ItemMeta setUnbreakable(ItemMeta meta, boolean value);
}
