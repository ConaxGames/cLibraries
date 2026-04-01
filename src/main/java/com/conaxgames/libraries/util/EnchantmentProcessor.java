package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XEnchantment;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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
        Optional<?> match = XEnchantment.REGISTRY.getByName(trimmed);
        if (match.isPresent()) {
            XEnchantment xEnchantment = (XEnchantment) match.get();
            Enchantment enchantment = xEnchantment.get();
            if (enchantment != null) {
                return enchantment;
            }
        }
        return Enchantment.getByName(trimmed.toUpperCase(Locale.ROOT).replace('-', '_').replace('.', '_'));
    }

    @SuppressWarnings("unchecked")
    public static List<String> completions() {
        Set<String> out = new LinkedHashSet<>();
        boolean modernKeys = !VersioningChecker.getInstance().isServerVersionBefore("1.13");
        Collection<XEnchantment> enchantments = XEnchantment.REGISTRY.getValues();
        for (XEnchantment x : enchantments) {
            if (!x.isSupported()) {
                continue;
            }
            Enchantment enchantment = x.get();
            if (enchantment == null) {
                continue;
            }
            out.add(x.name().toLowerCase(Locale.ROOT));
            if (modernKeys) {
                org.bukkit.NamespacedKey key = enchantment.getKey();
                out.add(key.toString());
                if ("minecraft".equals(key.getNamespace())) {
                    out.add(key.getKey());
                }
            }
        }
        return new ArrayList<>(out);
    }
}
