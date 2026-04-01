package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XPotion;
import org.bukkit.NamespacedKey;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class PotionProcessor {

    private PotionProcessor() {
    }

    public static PotionEffectType resolve(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return XPotion.of(trimmed)
                .map(XPotion::get)
                .orElse(null);
    }

    public static List<String> completions() {
        Set<String> out = new LinkedHashSet<>();
        for (XPotion x : XPotion.REGISTRY.getValues()) {
            if (!x.isSupported()) {
                continue;
            }
            PotionEffectType type = x.get();
            if (type == null) {
                continue;
            }
            out.add(x.name().toLowerCase(Locale.ROOT));
            for (String name : x.getNames()) {
                if (!name.isEmpty()) {
                    out.add(name.toLowerCase(Locale.ROOT));
                }
            }
            String bukkitName = type.getName();
            if (!bukkitName.isEmpty()) {
                out.add(bukkitName.toLowerCase(Locale.ROOT));
            }
        }
        return new ArrayList<>(out);
    }
}
