package com.conaxgames.libraries.util.resolvers;

import com.cryptomorin.xseries.XEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.util.*;

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
        return XEnchantment.of(trimmed)
                .map(XEnchantment::get)
                .orElse(null);
    }

    public static List<String> completions() {
        Set<String> out = new LinkedHashSet<>();
        for (XEnchantment x : XEnchantment.REGISTRY.getValues()) {
            if (!x.isSupported()) {
                continue;
            }
            out.add(x.name().toLowerCase(Locale.ROOT));
            for (String name : x.getNames()) {
                if (!name.isEmpty()) {
                    out.add(name.toLowerCase(Locale.ROOT));
                }
            }
        }
        return new ArrayList<>(out);
    }
}
