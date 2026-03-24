package com.conaxgames.libraries.util;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class EnchantmentProcessor {

    private EnchantmentProcessor() {
    }

    public static Enchantment resolve(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String lower = trimmed.toLowerCase(Locale.ROOT);
        NamespacedKey key = NamespacedKey.fromString(lower);
        Enchantment fromRegistry = key == null ? null : Registry.ENCHANTMENT.get(key);
        if (fromRegistry != null) {
            return fromRegistry;
        }
        return Enchantment.getByName(trimmed.toUpperCase(Locale.ROOT).replace('-', '_').replace('.', '_'));
    }

    public static List<String> completions() {
        List<String> out = new ArrayList<>();
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            NamespacedKey key = enchantment.getKey();
            out.add(key.toString());
            if ("minecraft".equals(key.getNamespace())) {
                out.add(key.getKey());
            }
        }
        return out;
    }
}
