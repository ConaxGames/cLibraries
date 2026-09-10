package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ColorMaterialUtil {

    private static final int DEFAULT_WOOL_DATA = 0;

    private static final List<String> COLOR_CCS = Collections.unmodifiableList(Arrays.asList(
            ChatColor.WHITE.toString(),
            ChatColor.GOLD.toString(),
            ChatColor.LIGHT_PURPLE.toString(),
            ChatColor.AQUA.toString(),
            ChatColor.YELLOW.toString(),
            ChatColor.GREEN.toString(),
            ChatColor.LIGHT_PURPLE.toString(),
            ChatColor.DARK_GRAY.toString(),
            ChatColor.GRAY.toString(),
            ChatColor.DARK_AQUA.toString(),
            ChatColor.DARK_PURPLE.toString(),
            ChatColor.BLUE.toString(),
            ChatColor.GOLD.toString(),
            ChatColor.DARK_GREEN.toString(),
            ChatColor.RED.toString(),
            ChatColor.BLACK.toString()
    ));

    public static int convertCCToWoolData(String color) {
        color = normalize(color);
        return COLOR_CCS.indexOf(color);
    }

    public static String convertMaterialDataToCC(int data) {
        switch (data) {
            case 1:
            case 12:
                return ChatColor.GOLD.toString();
            case 2:
            case 6:
                return ChatColor.LIGHT_PURPLE.toString();
            case 3:
                return ChatColor.AQUA.toString();
            case 4:
                return ChatColor.YELLOW.toString();
            case 5:
                return ChatColor.GREEN.toString();
            case 7:
                return ChatColor.DARK_GRAY.toString();
            case 8:
                return ChatColor.GRAY.toString();
            case 9:
                return ChatColor.DARK_AQUA.toString();
            case 10:
                return ChatColor.DARK_PURPLE.toString();
            case 11:
                return ChatColor.BLUE.toString();
            case 13:
                return ChatColor.DARK_GREEN.toString();
            case 14:
                return ChatColor.RED.toString();
            case 15:
                return ChatColor.BLACK.toString();
            default:
                return ChatColor.WHITE.toString();
        }
    }

    public static XMaterial convertCCToXWool(String color) {
        return woolDataToXWool(requireWoolData(color));
    }

    public static XMaterial convertCCToXClay(String color) {
        return woolDataToXClay(requireWoolData(color));
    }

    public static XMaterial convertCCToXCarpet(String color) {
        return woolDataToXCarpet(requireWoolData(color));
    }

    private static int requireWoolData(String color) {
        int data = convertCCToWoolData(color);
        return data < 0 ? DEFAULT_WOOL_DATA : data;
    }

    private static String normalize(String color) {
        if (color == null) {
            return ChatColor.WHITE.toString();
        }
        for (int i = 0; i < color.length() - 1; i++) {
            if (color.charAt(i) != '§' && color.charAt(i) != '&') {
                continue;
            }
            char code = Character.toLowerCase(color.charAt(i + 1));
            if ((code >= '0' && code <= '9') || (code >= 'a' && code <= 'f')) {
                color = "§" + code;
                break;
            }
        }
        if (ChatColor.DARK_RED.toString().equals(color)) {
            return ChatColor.RED.toString();
        }
        if (ChatColor.DARK_BLUE.toString().equals(color)) {
            return ChatColor.BLUE.toString();
        }
        return color;
    }

    private static XMaterial woolDataToXWool(int data) {
        switch (data) {
            case 0:
                return XMaterial.WHITE_WOOL;
            case 1:
            case 12:
                return XMaterial.ORANGE_WOOL;
            case 2:
            case 6:
                return XMaterial.PINK_WOOL;
            case 3:
                return XMaterial.LIGHT_BLUE_WOOL;
            case 4:
                return XMaterial.YELLOW_WOOL;
            case 5:
                return XMaterial.LIME_WOOL;
            case 7:
                return XMaterial.GRAY_WOOL;
            case 8:
                return XMaterial.LIGHT_GRAY_WOOL;
            case 9:
                return XMaterial.CYAN_WOOL;
            case 10:
                return XMaterial.PURPLE_WOOL;
            case 11:
                return XMaterial.BLUE_WOOL;
            case 13:
                return XMaterial.GREEN_WOOL;
            case 14:
                return XMaterial.RED_WOOL;
            case 15:
                return XMaterial.BLACK_WOOL;
            default:
                throw new AssertionError(data);
        }
    }

    private static XMaterial woolDataToXClay(int data) {
        switch (data) {
            case 0:
                return XMaterial.WHITE_TERRACOTTA;
            case 1:
            case 12:
                return XMaterial.ORANGE_TERRACOTTA;
            case 2:
            case 6:
                return XMaterial.PINK_TERRACOTTA;
            case 3:
                return XMaterial.LIGHT_BLUE_TERRACOTTA;
            case 4:
                return XMaterial.YELLOW_TERRACOTTA;
            case 5:
                return XMaterial.LIME_TERRACOTTA;
            case 7:
                return XMaterial.GRAY_TERRACOTTA;
            case 8:
                return XMaterial.LIGHT_GRAY_TERRACOTTA;
            case 9:
                return XMaterial.CYAN_TERRACOTTA;
            case 10:
                return XMaterial.PURPLE_TERRACOTTA;
            case 11:
                return XMaterial.BLUE_TERRACOTTA;
            case 13:
                return XMaterial.GREEN_TERRACOTTA;
            case 14:
                return XMaterial.RED_TERRACOTTA;
            case 15:
                return XMaterial.BLACK_TERRACOTTA;
            default:
                throw new AssertionError(data);
        }
    }

    private static XMaterial woolDataToXCarpet(int data) {
        switch (data) {
            case 0:
                return XMaterial.WHITE_CARPET;
            case 1:
            case 12:
                return XMaterial.ORANGE_CARPET;
            case 2:
            case 6:
                return XMaterial.PINK_CARPET;
            case 3:
                return XMaterial.LIGHT_BLUE_CARPET;
            case 4:
                return XMaterial.YELLOW_CARPET;
            case 5:
                return XMaterial.LIME_CARPET;
            case 7:
                return XMaterial.GRAY_CARPET;
            case 8:
                return XMaterial.LIGHT_GRAY_CARPET;
            case 9:
                return XMaterial.CYAN_CARPET;
            case 10:
                return XMaterial.PURPLE_CARPET;
            case 11:
                return XMaterial.BLUE_CARPET;
            case 13:
                return XMaterial.GREEN_CARPET;
            case 14:
                return XMaterial.RED_CARPET;
            case 15:
                return XMaterial.BLACK_CARPET;
            default:
                throw new AssertionError(data);
        }
    }

}
