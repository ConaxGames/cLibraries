package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Utility for mapping between chat colors and colored block materials (wool, terracotta, carpet)
 * or legacy data values. Supports {@link ChatColor}, {@link CC} color strings, and legacy wool data.
 * <p>
 * <b>Usage:</b> Use {@link #convertChatColorToXWool(ChatColor)} or {@link #convertCCToXWool(String)}
 * to get wool materials for GUIs; use {@link #convertChatColorToXClay(ChatColor)} or
 * {@link #convertChatColorToXCarpet(ChatColor)} for terracotta or carpet variants.
 */
public final class ColorMaterialUtil {

    private static final List<String> COLOR_CCS = Collections.unmodifiableList(Arrays.asList(
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
            CC.BLACK,
            CC.DARK_GREEN,
            CC.RED,
            CC.BLACK
    ));

    /**
     * Converts a {@link ChatColor} to the legacy wool data value (0–15).
     *
     * @param color The chat color
     * @return The wool data value for that color
     */
    public static int convertChatColorToWoolData(ChatColor color) {
        switch (color) {
            case BLACK:
                return 15;
            case DARK_BLUE:
            case BLUE:
                return 11;
            case DARK_GREEN:
                return 13;
            case DARK_AQUA:
                return 9;
            case DARK_RED:
            case RED:
                return 14;
            case DARK_PURPLE:
                return 10;
            case GOLD:
                return 1;
            case GRAY:
                return 8;
            case DARK_GRAY:
                return 7;
            case GREEN:
                return 5;
            case AQUA:
                return 3;
            case LIGHT_PURPLE:
                return 2;
            case YELLOW:
                return 4;
            default:
                return 0;
        }
    }

    /**
     * Converts a legacy wool (or stained clay) data value to the corresponding {@link ChatColor}.
     *
     * @param data The material data value (0–15)
     * @return The matching chat color
     */
    public static ChatColor convertMaterialDataToChatColor(int data) {
        switch (data) {
            case 1:
            case 12:
                return ChatColor.GOLD;
            case 2:
            case 6:
                return ChatColor.LIGHT_PURPLE;
            case 3:
                return ChatColor.AQUA;
            case 4:
                return ChatColor.YELLOW;
            case 5:
                return ChatColor.GREEN;
            case 7:
                return ChatColor.DARK_GRAY;
            case 8:
                return ChatColor.GRAY;
            case 9:
                return ChatColor.DARK_AQUA;
            case 10:
                return ChatColor.DARK_PURPLE;
            case 11:
                return ChatColor.BLUE;
            case 13:
                return ChatColor.DARK_GREEN;
            case 14:
                return ChatColor.RED;
            case 15:
                return ChatColor.BLACK;
            default:
                return ChatColor.WHITE;
        }
    }

    /**
     * Converts a {@link CC} color string to the legacy wool data value (0–15).
     * Treats {@link CC#DARK_RED} as {@link CC#RED} and {@link CC#DARK_BLUE} as {@link CC#BLUE}.
     *
     * @param color A CC color constant (e.g. {@link CC#RED})
     * @return The wool data value, or -1 if not found
     */
    public static int convertCCToWoolData(String color) {
        if (Objects.equals(color, CC.DARK_RED)) {
            color = CC.RED;
        }
        if (Objects.equals(color, CC.DARK_BLUE)) {
            color = CC.BLUE;
        }
        return COLOR_CCS.indexOf(color);
    }

    /**
     * Converts a color name string (e.g. "BLACK", "BLUE") to the corresponding wool {@link XMaterial}.
     *
     * @param color The color name (e.g. "RED", "LIGHT_PURPLE")
     * @return The matching wool material
     */
    public static XMaterial convertCCToXWool(String color) {
        switch (color) {
            case "BLACK":
                return XMaterial.BLACK_WOOL;
            case "DARK_BLUE":
            case "BLUE":
                return XMaterial.BLUE_WOOL;
            case "DARK_GREEN":
                return XMaterial.GREEN_WOOL;
            case "DARK_AQUA":
                return XMaterial.CYAN_WOOL;
            case "DARK_RED":
            case "RED":
                return XMaterial.RED_WOOL;
            case "DARK_PURPLE":
                return XMaterial.PURPLE_WOOL;
            case "GOLD":
                return XMaterial.ORANGE_WOOL;
            case "GRAY":
                return XMaterial.LIGHT_GRAY_WOOL;
            case "DARK_GRAY":
                return XMaterial.GRAY_WOOL;
            case "GREEN":
                return XMaterial.LIME_WOOL;
            case "AQUA":
                return XMaterial.LIGHT_BLUE_WOOL;
            case "LIGHT_PURPLE":
                return XMaterial.PINK_WOOL;
            case "YELLOW":
                return XMaterial.YELLOW_WOOL;
            default:
                return XMaterial.WHITE_WOOL;
        }
    }

    /**
     * Converts a {@link ChatColor} to the matching wool {@link XMaterial}.
     *
     * @param color The chat color
     * @return The matching wool material
     */
    public static XMaterial convertChatColorToXWool(ChatColor color) {
        switch (color) {
            case BLACK:
                return XMaterial.BLACK_WOOL;
            case DARK_BLUE:
            case BLUE:
                return XMaterial.BLUE_WOOL;
            case DARK_GREEN:
                return XMaterial.GREEN_WOOL;
            case DARK_AQUA:
                return XMaterial.CYAN_WOOL;
            case DARK_RED:
            case RED:
                return XMaterial.RED_WOOL;
            case DARK_PURPLE:
                return XMaterial.PURPLE_WOOL;
            case GOLD:
                return XMaterial.ORANGE_WOOL;
            case GRAY:
                return XMaterial.LIGHT_GRAY_WOOL;
            case DARK_GRAY:
                return XMaterial.GRAY_WOOL;
            case GREEN:
                return XMaterial.LIME_WOOL;
            case AQUA:
                return XMaterial.LIGHT_BLUE_WOOL;
            case LIGHT_PURPLE:
                return XMaterial.PINK_WOOL;
            case YELLOW:
                return XMaterial.YELLOW_WOOL;
            default:
                return XMaterial.WHITE_WOOL;
        }
    }

    /**
     * Converts a {@link ChatColor} to the matching terracotta (stained clay) {@link XMaterial}.
     *
     * @param color The chat color
     * @return The matching terracotta material
     */
    public static XMaterial convertChatColorToXClay(ChatColor color) {
        switch (color) {
            case BLACK:
                return XMaterial.BLACK_TERRACOTTA;
            case DARK_BLUE:
            case BLUE:
                return XMaterial.BLUE_TERRACOTTA;
            case DARK_GREEN:
                return XMaterial.GREEN_TERRACOTTA;
            case DARK_AQUA:
                return XMaterial.CYAN_TERRACOTTA;
            case DARK_RED:
            case RED:
                return XMaterial.RED_TERRACOTTA;
            case DARK_PURPLE:
                return XMaterial.PURPLE_TERRACOTTA;
            case GOLD:
                return XMaterial.ORANGE_TERRACOTTA;
            case GRAY:
                return XMaterial.LIGHT_GRAY_TERRACOTTA;
            case DARK_GRAY:
                return XMaterial.GRAY_TERRACOTTA;
            case GREEN:
                return XMaterial.LIME_TERRACOTTA;
            case AQUA:
                return XMaterial.LIGHT_BLUE_TERRACOTTA;
            case LIGHT_PURPLE:
                return XMaterial.PINK_TERRACOTTA;
            case YELLOW:
                return XMaterial.YELLOW_TERRACOTTA;
            default:
                return XMaterial.WHITE_TERRACOTTA;
        }
    }

    /**
     * Converts a {@link ChatColor} to the matching carpet {@link XMaterial}.
     *
     * @param color The chat color
     * @return The matching carpet material
     */
    public static XMaterial convertChatColorToXCarpet(ChatColor color) {
        switch (color) {
            case BLACK:
                return XMaterial.BLACK_CARPET;
            case DARK_BLUE:
            case BLUE:
                return XMaterial.BLUE_CARPET;
            case DARK_GREEN:
                return XMaterial.GREEN_CARPET;
            case DARK_AQUA:
                return XMaterial.CYAN_CARPET;
            case DARK_RED:
            case RED:
                return XMaterial.RED_CARPET;
            case DARK_PURPLE:
                return XMaterial.PURPLE_CARPET;
            case GOLD:
                return XMaterial.ORANGE_CARPET;
            case GRAY:
                return XMaterial.LIGHT_GRAY_CARPET;
            case DARK_GRAY:
                return XMaterial.GRAY_CARPET;
            case GREEN:
                return XMaterial.LIME_CARPET;
            case AQUA:
                return XMaterial.LIGHT_BLUE_CARPET;
            case LIGHT_PURPLE:
                return XMaterial.PINK_CARPET;
            case YELLOW:
                return XMaterial.YELLOW_CARPET;
            default:
                return XMaterial.WHITE_CARPET;
        }
    }

}
