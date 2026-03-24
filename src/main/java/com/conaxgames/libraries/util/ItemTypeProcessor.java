package com.conaxgames.libraries.util;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ItemTypeProcessor {

    private ItemTypeProcessor() {
    }

    public static ItemType resolve(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String lower = trimmed.toLowerCase(Locale.ROOT);
        NamespacedKey key = NamespacedKey.fromString(lower);
        ItemType type = key == null ? null : Registry.ITEM.get(key);
        if (type == null || type == ItemType.AIR) {
            return null;
        }
        return type;
    }

    public static List<String> completions() {
        List<String> out = new ArrayList<>();
        for (ItemType type : Registry.ITEM) {
            if (type == ItemType.AIR) {
                continue;
            }
            NamespacedKey key = type.getKey();
            out.add(key.toString());
            if ("minecraft".equals(key.getNamespace())) {
                out.add(key.getKey());
            }
        }
        return out;
    }
}
