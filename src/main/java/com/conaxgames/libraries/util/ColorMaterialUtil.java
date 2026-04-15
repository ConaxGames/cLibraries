package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XMaterial;

import java.util.List;

public final class ColorMaterialUtil {

    private static final int DEFAULT_WOOL_DATA = 0;

    private static final List<String> COLOR_CCS = List.of(
            CC.WHITE,
            CC.GOLD,
            CC.LIGHT_PURPLE,
            CC.AQUA,
            CC.YELLOW,
            CC.GREEN,
            CC.LIGHT_PURPLE,
            CC.DARK_GRAY,
            CC.GRAY,
            CC.DARK_AQUA,
            CC.DARK_PURPLE,
            CC.BLUE,
            CC.GOLD,
            CC.DARK_GREEN,
            CC.RED,
            CC.BLACK
    );

    public static int convertCCToWoolData(String color) {
        color = normalize(color);
        return COLOR_CCS.indexOf(color);
    }

    public static String convertMaterialDataToCC(int data) {
        return switch (data) {
            case 1, 12 -> CC.GOLD;
            case 2, 6 -> CC.LIGHT_PURPLE;
            case 3 -> CC.AQUA;
            case 4 -> CC.YELLOW;
            case 5 -> CC.GREEN;
            case 7 -> CC.DARK_GRAY;
            case 8 -> CC.GRAY;
            case 9 -> CC.DARK_AQUA;
            case 10 -> CC.DARK_PURPLE;
            case 11 -> CC.BLUE;
            case 13 -> CC.DARK_GREEN;
            case 14 -> CC.RED;
            case 15 -> CC.BLACK;
            default -> CC.WHITE;
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
            return CC.WHITE;
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
        if (CC.DARK_RED.equals(color)) {
            return CC.RED;
        }
        if (CC.DARK_BLUE.equals(color)) {
            return CC.BLUE;
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
