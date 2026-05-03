package com.conaxgames.libraries.message;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class ItemNameUtil {

    private static final Map<String, String> potionmap = ImmutableMap.<String, String>builder()
            .put("speed", "Speed")
            .put("slowness", "Slowness")
            .put("haste", "Haste")
            .put("mining_fatigue", "Mining Fatigue")
            .put("strength", "Strength")
            .put("instant_health", "Instant Health")
            .put("instant_damage", "Instant Damage")
            .put("jump_boost", "Jump Boost")
            .put("nausea", "Nausea")
            .put("regeneration", "Regeneration")
            .put("resistance", "Resistance")
            .put("fire_resistance", "Fire Resistance")
            .put("water_breathing", "Water Breathing")
            .put("invisibility", "Invisibility")
            .put("blindness", "Blindness")
            .put("night_vision", "Night Vision")
            .put("hunger", "Hunger")
            .put("weakness", "Weakness")
            .put("poison", "Poison")
            .put("wither", "Wither")
            .put("health_boost", "Health Boost")
            .put("absorption", "Absorption")
            .put("saturation", "Saturation")
            .build();

    public static String potionLookup(PotionEffectType potion) {
        String result;
        String key = potion.getName().toLowerCase();
        result = potionmap.get(key);

        if (result == null) {
            result = WordUtils.capitalizeFully(potion.getName().replace("_", " "));
        }

        return result;
    }

    public static String enchantLookup(Enchantment enchantment, Player player) {
        String key = enchantment.getKey().getKey();
        return WordUtils.capitalizeFully(key.replace('_', ' ').replace('-', ' '));
    }
}

