package com.conaxgames.libraries.util.resolvers;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;

import java.util.*;

public final class ItemTypeProcessor {

    private ItemTypeProcessor() {
    }

    public static Material resolve(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        Material material = XMaterial.matchXMaterial(trimmed)
                .map(XMaterial::get)
                .orElse(null);
        if (material == null || material == Material.AIR) {
            return null;
        }
        return material;
    }

    public static List<String> completions() {
        Set<String> out = new LinkedHashSet<>();
        for (XMaterial x : XMaterial.values()) {
            if (!x.isSupported()) {
                continue;
            }
            Material material = x.get();
            if (material == null || material == Material.AIR) {
                continue;
            }
            out.add(x.name().toLowerCase(Locale.ROOT));
            for (String name : x.getNames()) {
                if (!name.isEmpty()) {
                    out.add(name.toLowerCase(Locale.ROOT));
                }
            }
            out.add(material.name().toLowerCase(Locale.ROOT));
        }
        return new ArrayList<>(out);
    }
}
