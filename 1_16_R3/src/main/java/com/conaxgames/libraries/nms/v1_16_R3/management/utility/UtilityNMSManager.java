package com.conaxgames.libraries.nms.v1_16_R3.management.utility;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public class UtilityNMSManager extends com.conaxgames.libraries.nms.management.utility.UtilityNMSManager {

    private static final Pattern HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])([A-Fa-f0-9])");

    @Override
    public String translateHex(String message) {
        return message;
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', HEX_PATTERN.matcher(message).replaceAll("&x&$1&$2&$3&$4&$5&$6"));
    }
}
