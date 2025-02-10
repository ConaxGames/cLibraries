package com.conaxgames.libraries.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class PotionProcessor {

    /*
    Receives commonly used PotionEffectType aliases and returns its Bukkit PotionEffectType.
    Example: '/potion strength 60 1' --> INCREASE_DAMAGE, 60 seconds, level 2
     */
    private static final Map<String,String> potionmap = ImmutableMap.<String,String>builder()
            .put("1", "SPEED")                       // speed
            .put("speed", "SPEED")
            .put("2", "SLOW")                        // slow
            .put("slow", "SLOW")
            .put("slowness", "SLOW")
            .put("3", "FAST_DIGGING")                // haste
            .put("haste", "FAST_DIGGING")
            .put("fastdigging", "FAST_DIGGING")
            .put("fast_digging", "FAST_DIGGING")
            .put("4", "SLOW_DIGGING")                // slow digging
            .put("slowdigging", "SLOW_DIGGING")
            .put("mining_fatigue", "SLOW_DIGGING")
            .put("miningfatigue", "SLOW_DIGGING")
            .put("fatigue", "SLOW_DIGGING")
            .put("5", "INCREASE_DAMAGE")              // strength
            .put("strength", "INCREASE_DAMAGE")
            .put("increase_damage", "INCREASE_DAMAGE")
            .put("increasedamage", "INCREASE_DAMAGE")
            .put("6", "HEAL")                        // health
            .put("heal", "HEAL")
            .put("instanthealth", "HEAL")
            .put("7", "HARM")                        // harm
            .put("instantdamage", "HARM")
            .put("instant_damage", "HARM")
            .put("harm", "HARM")
            .put("8", "JUMP")                        // jump boost
            .put("jump", "JUMP")
            .put("jumpboost", "JUMP")
            .put("jump_boost", "JUMP")
            .put("9", "CONFUSION")                   // confusion
            .put("confusion", "CONFUSION")
            .put("nausea", "CONFUSION")
            .put("10", "REGENERATION")               // regen
            .put("regen", "REGENERATION")
            .put("regeneration", "REGENERATION")
            .put("11", "DAMAGE_RESISTANCE")            // damage res
            .put("resistance", "DAMAGE_RESISTANCE")
            .put("damage_res", "DAMAGE_RESISTANCE")
            .put("damage_resistance", "DAMAGE_RESISTANCE")
            .put("damageres", "DAMAGE_RESISTANCE")
            .put("damageresistence", "DAMAGE_RESISTANCE")
            .put("12", "FIRE_RESISTANCE")            // fire res
            .put("fireres", "FIRE_RESISTANCE")
            .put("fire_res", "FIRE_RESISTANCE")
            .put("fres", "FIRE_RESISTANCE")
            .put("fire_resistance", "FIRE_RESISTANCE")
            .put("fireresistance", "FIRE_RESISTANCE")
            .put("13", "WATER_BREATHING")            // water breathing
            .put("water", "WATER_BREATHING")
            .put("water_breathing", "WATER_BREATHING")
            .put("waterbreathing", "WATER_BREATHING")
            .put("14", "INVISIBILITY")              // invis
            .put("invis", "INVISIBILITY")
            .put("invisibility", "INVISIBILITY")
            .put("vanish", "INVISIBILITY")
            .put("15", "BLINDESS")                 // blind
            .put("blind", "BLINDESS")
            .put("blindness", "BLINDESS")
            .put("16", "NIGHT_VISION")             // night vis
            .put("night_vision", "NIGHT_VISION")
            .put("nightvision", "NIGHT_VISION")
            .put("nightvis", "NIGHT_VISION")
            .put("night", "NIGHT_VISION")
            .put("17", "HUNGER")                    // hunger
            .put("hunger", "HUNGER")
            .put("18", "WEAKNESS")                  // weak
            .put("weakness", "WEAKNESS")
            .put("weak", "WEAKNESS")
            .put("19", "POISON")                      // poison
            .put("poison", "POISON")
            .put("20", "WITHER")                       // wither
            .put("wither", "WITHER")
            .put("21", "HEALTH_BOOST")                    // health boost
            .put("healthboost", "HEALTH_BOOST")
            .put("health_boost", "HEALTH_BOOST")
            .put("22", "ABSORPTION")                // absorption
            .put("absorption", "ABSORPTION")
            .put("23", "SATURATION")                   // saturation
            .put("saturation", "SATURATION")
            .put("sat", "SATURATION")
            .put("nohunger", "SATURATION")
            .build();

    public static String process(String potion) {
        String result;
        String key = potion;
        result = potionmap.get(key.toLowerCase());
        return result;
    }
}
