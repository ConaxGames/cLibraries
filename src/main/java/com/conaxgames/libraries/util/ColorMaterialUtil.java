package com.conaxgames.libraries.util;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ColorMaterialUtil {

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
        if (color == null) {
            return 0;
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
        // The dark shades have no wool of their own.
        if (ChatColor.DARK_RED.toString().equals(color)) {
            color = ChatColor.RED.toString();
        } else if (ChatColor.DARK_BLUE.toString().equals(color)) {
            color = ChatColor.BLUE.toString();
        }
        return COLOR_CCS.indexOf(color);
    }

    public static String convertMaterialDataToCC(int data) {
        return data >= 0 && data < COLOR_CCS.size() ? COLOR_CCS.get(data) : ChatColor.WHITE.toString();
    }

    public static XMaterial convertCCToXWool(String color) {
        return legacy("WOOL", color);
    }

    public static XMaterial convertCCToXClay(String color) {
        return legacy("STAINED_CLAY", color);
    }

    public static XMaterial convertCCToXCarpet(String color) {
        return legacy("CARPET", color);
    }

    // XMaterial resolves legacy "NAME:DATA" pairs on every version, so one lookup replaces a table per block type.
    private static XMaterial legacy(String material, String color) {
        int data = convertCCToWoolData(color);
        // Unknown colours fall back to white; light purple lands on magenta's slot but pink is the closer block colour.
        int wool = data < 0 ? 0 : data == 2 ? 6 : data;
        return XMaterial.matchXMaterial(material + ":" + wool).orElseThrow(() -> new AssertionError(data));
    }
}
