package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ColorMaterialUtil {

    private static final int DEFAULT_WOOL_DATA = 0;

    // Indexed by legacy wool data. Brown (12) and magenta (2) have no chat colour, so they share gold and light purple.
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
        return data >= 0 && data < COLOR_CCS.size() ? COLOR_CCS.get(data) : ChatColor.WHITE.toString();
    }

    public static XMaterial convertCCToXWool(String color) {
        return legacy("WOOL", requireWoolData(color));
    }

    public static XMaterial convertCCToXClay(String color) {
        return legacy("STAINED_CLAY", requireWoolData(color));
    }

    public static XMaterial convertCCToXCarpet(String color) {
        return legacy("CARPET", requireWoolData(color));
    }

    private static int requireWoolData(String color) {
        int data = convertCCToWoolData(color);
        return data < 0 ? DEFAULT_WOOL_DATA : data;
    }

    // XMaterial resolves legacy "NAME:DATA" pairs on every version, so one lookup replaces a table per block type.
    private static XMaterial legacy(String material, int data) {
        // Light purple lands on magenta's slot, but pink is the closer block colour.
        int wool = data == 2 ? 6 : data;
        return XMaterial.matchXMaterial(material + ":" + wool).orElseThrow(() -> new AssertionError(data));
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

}
