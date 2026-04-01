package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

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
        Optional<XMaterial> match = XMaterial.matchXMaterial(trimmed);
        if (!match.isPresent()) {
            return null;
        }
        Material material = match.get().get();
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
            out.add(material.name().toLowerCase(Locale.ROOT));
        }
        return new ArrayList<>(out);
    }
}
