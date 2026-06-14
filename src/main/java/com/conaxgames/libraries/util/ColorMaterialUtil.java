package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;

import java.util.List;

public final class ColorMaterialUtil {

    private static final int DEFAULT_WOOL_DATA = 0;

    private static final List<String> COLOR_CCS = List.of(
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
    );

    public static int convertCCToWoolData(String color) {
        color = normalize(color);
        return COLOR_CCS.indexOf(color);
    }

    public static String convertMaterialDataToCC(int data) {
        return switch (data) {
            case 1, 12 -> ChatColor.GOLD.toString();
            case 2, 6 -> ChatColor.LIGHT_PURPLE.toString();
            case 3 -> ChatColor.AQUA.toString();
            case 4 -> ChatColor.YELLOW.toString();
            case 5 -> ChatColor.GREEN.toString();
            case 7 -> ChatColor.DARK_GRAY.toString();
            case 8 -> ChatColor.GRAY.toString();
            case 9 -> ChatColor.DARK_AQUA.toString();
            case 10 -> ChatColor.DARK_PURPLE.toString();
            case 11 -> ChatColor.BLUE.toString();
            case 13 -> ChatColor.DARK_GREEN.toString();
            case 14 -> ChatColor.RED.toString();
            case 15 -> ChatColor.BLACK.toString();
            default -> ChatColor.WHITE.toString();
        };
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
        return switch (data) {
            case 0 -> XMaterial.WHITE_WOOL;
            case 1, 12 -> XMaterial.ORANGE_WOOL;
            case 2, 6 -> XMaterial.PINK_WOOL;
            case 3 -> XMaterial.LIGHT_BLUE_WOOL;
            case 4 -> XMaterial.YELLOW_WOOL;
            case 5 -> XMaterial.LIME_WOOL;
            case 7 -> XMaterial.GRAY_WOOL;
            case 8 -> XMaterial.LIGHT_GRAY_WOOL;
            case 9 -> XMaterial.CYAN_WOOL;
            case 10 -> XMaterial.PURPLE_WOOL;
            case 11 -> XMaterial.BLUE_WOOL;
            case 13 -> XMaterial.GREEN_WOOL;
            case 14 -> XMaterial.RED_WOOL;
            case 15 -> XMaterial.BLACK_WOOL;
            default -> throw new AssertionError(data);
        };
    }

    private static XMaterial woolDataToXClay(int data) {
        return switch (data) {
            case 0 -> XMaterial.WHITE_TERRACOTTA;
            case 1, 12 -> XMaterial.ORANGE_TERRACOTTA;
            case 2, 6 -> XMaterial.PINK_TERRACOTTA;
            case 3 -> XMaterial.LIGHT_BLUE_TERRACOTTA;
            case 4 -> XMaterial.YELLOW_TERRACOTTA;
            case 5 -> XMaterial.LIME_TERRACOTTA;
            case 7 -> XMaterial.GRAY_TERRACOTTA;
            case 8 -> XMaterial.LIGHT_GRAY_TERRACOTTA;
            case 9 -> XMaterial.CYAN_TERRACOTTA;
            case 10 -> XMaterial.PURPLE_TERRACOTTA;
            case 11 -> XMaterial.BLUE_TERRACOTTA;
            case 13 -> XMaterial.GREEN_TERRACOTTA;
            case 14 -> XMaterial.RED_TERRACOTTA;
            case 15 -> XMaterial.BLACK_TERRACOTTA;
            default -> throw new AssertionError(data);
        };
    }

    private static XMaterial woolDataToXCarpet(int data) {
        return switch (data) {
            case 0 -> XMaterial.WHITE_CARPET;
            case 1, 12 -> XMaterial.ORANGE_CARPET;
            case 2, 6 -> XMaterial.PINK_CARPET;
            case 3 -> XMaterial.LIGHT_BLUE_CARPET;
            case 4 -> XMaterial.YELLOW_CARPET;
            case 5 -> XMaterial.LIME_CARPET;
            case 7 -> XMaterial.GRAY_CARPET;
            case 8 -> XMaterial.LIGHT_GRAY_CARPET;
            case 9 -> XMaterial.CYAN_CARPET;
            case 10 -> XMaterial.PURPLE_CARPET;
            case 11 -> XMaterial.BLUE_CARPET;
            case 13 -> XMaterial.GREEN_CARPET;
            case 14 -> XMaterial.RED_CARPET;
            case 15 -> XMaterial.BLACK_CARPET;
            default -> throw new AssertionError(data);
        };
    }

}
