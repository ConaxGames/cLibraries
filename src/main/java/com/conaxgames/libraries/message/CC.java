package com.conaxgames.libraries.message;

import com.conaxgames.libraries.util.VersioningChecker;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CC {

    private CC() {}

    private static final char SECTION = ChatColor.COLOR_CHAR;
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)[&" + SECTION + "]#([0-9a-f]{6})");
    private static final Pattern SECTION_HEX_STRIP = Pattern.compile("(?i)" + SECTION + "x(?:" + SECTION + "[0-9a-f]){6}");
    private static final Pattern CODE_STRIP = Pattern.compile("(?i)[&" + SECTION + "][0-9a-fk-orstp]");

    private static final int[] LEGACY_RGB = {
            0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
            0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };

    private static String primary = "";
    private static String secondary = "";
    private static String tertiary = "";

    private static Boolean hexSupported;

    public static void setPrimary(String color) {
        primary = color == null ? "" : color;
    }

    public static void setSecondary(String color) {
        secondary = color == null ? "" : color;
    }

    public static void setTertiary(String color) {
        tertiary = color == null ? "" : color;
    }

    public static String getPrimary() {
        return primary;
    }

    public static String getSecondary() {
        return secondary;
    }

    public static String getTertiary() {
        return tertiary;
    }

    public static String translate(String input) {
        if (input == null) return null;
        String out = input;
        if (!primary.isBlank()) out = out.replace("&p", primary);
        if (!secondary.isBlank()) out = out.replace("&s", secondary);
        if (!tertiary.isBlank()) out = out.replace("&t", tertiary);
        return ChatColor.translateAlternateColorCodes('&', translateHex(out));
    }

    public static List<String> translate(List<String> input) {
        return input == null ? null : input.stream().map(CC::translate).toList();
    }

    public static List<String> translate(String... input) {
        return Arrays.stream(input).map(CC::translate).toList();
    }

    public static String stripAllColor(String input) {
        if (input == null) return null;
        String out = input;
        if (!primary.isBlank()) out = out.replace("&p", primary);
        if (!secondary.isBlank()) out = out.replace("&s", secondary);
        if (!tertiary.isBlank()) out = out.replace("&t", tertiary);
        String stripped = SECTION_HEX_STRIP.matcher(out).replaceAll("");
        stripped = HEX_PATTERN.matcher(stripped).replaceAll("");
        stripped = CODE_STRIP.matcher(stripped).replaceAll("");
        return ChatColor.stripColor(stripped);
    }

    public static String getLastColors(String input) {
        if (input == null) return "";
        return ChatColor.getLastColors(translate(input));
    }

    private static String translateHex(String input) {
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder out = new StringBuilder(input.length() + 16);
        while (matcher.find()) {
            matcher.appendReplacement(out, Matcher.quoteReplacement(hex(matcher.group(1))));
        }
        return matcher.appendTail(out).toString();
    }

    private static String hex(String rgb) {
        if (!hexSupported()) return nearestLegacy(rgb);
        StringBuilder out = new StringBuilder(14).append(SECTION).append('x');
        for (int i = 0; i < 6; i++) out.append(SECTION).append(rgb.charAt(i));
        return out.toString();
    }

    private static String nearestLegacy(String rgb) {
        int value = Integer.parseInt(rgb, 16);
        int r = (value >> 16) & 0xFF, g = (value >> 8) & 0xFF, b = value & 0xFF;
        int best = 15;
        long bestDistance = Long.MAX_VALUE;
        for (int i = 0; i < LEGACY_RGB.length; i++) {
            int dr = r - ((LEGACY_RGB[i] >> 16) & 0xFF);
            int dg = g - ((LEGACY_RGB[i] >> 8) & 0xFF);
            int db = b - (LEGACY_RGB[i] & 0xFF);
            long distance = (long) dr * dr + (long) dg * dg + (long) db * db;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return String.valueOf(SECTION) + Character.forDigit(best, 16);
    }

    private static boolean hexSupported() {
        if (hexSupported == null) {
            try {
                hexSupported = !VersioningChecker.getInstance().isServerVersionBefore("1.16");
            } catch (Throwable ignored) {
                hexSupported = true;
            }
        }
        return hexSupported;
    }
}
