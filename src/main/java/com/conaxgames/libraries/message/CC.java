package com.conaxgames.libraries.message;

import com.conaxgames.libraries.util.VersioningChecker;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class CC {

    private CC() {}

    private static final char SECTION = ChatColor.COLOR_CHAR;
    private static final Pattern HEX = Pattern.compile("(?i)[&" + SECTION + "]#([0-9a-f]{6})");
    private static final Pattern STRIP = Pattern.compile("(?i)" + SECTION + "x(?:" + SECTION + "[0-9a-f]){6}"
            + "|[&" + SECTION + "]#[0-9a-f]{6}|[&" + SECTION + "][0-9a-fk-orstp]");
    private static final int[] LEGACY_RGB = {
            0x000000, 0x0000AA, 0x00AA00, 0x00AAAA, 0xAA0000, 0xAA00AA, 0xFFAA00, 0xAAAAAA,
            0x555555, 0x5555FF, 0x55FF55, 0x55FFFF, 0xFF5555, 0xFF55FF, 0xFFFF55, 0xFFFFFF
    };

    private static String primary = "", secondary = "", tertiary = "";

    /**
     * Shared legacy serializer for the modern (Adventure) code paths. Adventure is only loaded on first call, so
     * this class still initialises on servers without it.
     */
    public static LegacyComponentSerializer legacy() {
        return Legacy.SERIALIZER;
    }

    public static void setColors(String primary, String secondary, String tertiary) {
        CC.primary = primary == null ? "" : primary;
        CC.secondary = secondary == null ? "" : secondary;
        CC.tertiary = tertiary == null ? "" : tertiary;
    }

    public static String translate(String input) {
        if (input == null) return null;
        String out = vars(input);
        Matcher matcher = HEX.matcher(out);
        if (matcher.find()) {
            // Matcher only accepts StringBuffer for appendReplacement/appendTail on Java 8.
            StringBuffer sb = new StringBuffer(out.length() + 16);
            do {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(hex(matcher.group(1))));
            } while (matcher.find());
            out = matcher.appendTail(sb).toString();
        }
        return ChatColor.translateAlternateColorCodes('&', out);
    }

    public static List<String> translate(List<String> input) {
        return input == null ? null : input.stream().map(CC::translate).collect(Collectors.toList());
    }

    public static String stripAllColor(String input) {
        return input == null ? null : ChatColor.stripColor(STRIP.matcher(vars(input)).replaceAll(""));
    }

    public static String getLastColors(String input) {
        return input == null ? "" : ChatColor.getLastColors(translate(input));
    }

    private static String vars(String s) {
        if (!primary.isEmpty()) s = s.replace("&p", primary);
        if (!secondary.isEmpty()) s = s.replace("&s", secondary);
        if (!tertiary.isEmpty()) s = s.replace("&t", tertiary);
        return s;
    }

    private static String hex(String rgb) {
        if (VersioningChecker.supports("1.16")) {
            StringBuilder sb = new StringBuilder(14).append(SECTION).append('x');
            for (int i = 0; i < 6; i++) sb.append(SECTION).append(rgb.charAt(i));
            return sb.toString();
        }
        int value = Integer.parseInt(rgb, 16), r = (value >> 16) & 0xFF, g = (value >> 8) & 0xFF, b = value & 0xFF;
        int best = 15;
        long bestDistance = Long.MAX_VALUE;
        for (int i = 0; i < LEGACY_RGB.length; i++) {
            int dr = r - ((LEGACY_RGB[i] >> 16) & 0xFF), dg = g - ((LEGACY_RGB[i] >> 8) & 0xFF), db = b - (LEGACY_RGB[i] & 0xFF);
            long distance = (long) dr * dr + (long) dg * dg + (long) db * db;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
        }
        return String.valueOf(SECTION) + Character.forDigit(best, 16);
    }

    private static final class Legacy {
        static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
                .character(LegacyComponentSerializer.SECTION_CHAR)
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .build();
    }
}
