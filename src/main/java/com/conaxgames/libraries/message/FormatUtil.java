package com.conaxgames.libraries.message;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class FormatUtil {

    private static final char SECTION = '\u00a7';
    private static final Pattern FORMATTING = Pattern.compile("^.*(?<format>(" + SECTION + "[0-9a-fklmor])+).*");
    private static final Pattern FORMAT_CODES = Pattern.compile("(" + SECTION + "|&)[0-9a-fklmor]");
    private static final TreeMap<Integer, String> ROMAN_NUMERALS = new TreeMap<>();

    static {
        ROMAN_NUMERALS.put(1000, "M");
        ROMAN_NUMERALS.put(900, "CM");
        ROMAN_NUMERALS.put(500, "D");
        ROMAN_NUMERALS.put(400, "CD");
        ROMAN_NUMERALS.put(100, "C");
        ROMAN_NUMERALS.put(90, "XC");
        ROMAN_NUMERALS.put(50, "L");
        ROMAN_NUMERALS.put(40, "XL");
        ROMAN_NUMERALS.put(10, "X");
        ROMAN_NUMERALS.put(9, "IX");
        ROMAN_NUMERALS.put(5, "V");
        ROMAN_NUMERALS.put(4, "IV");
        ROMAN_NUMERALS.put(1, "I");
        ROMAN_NUMERALS.put(0, "");
    }

    private FormatUtil() {}

    public static String stripFormatting(String format) {
        if (format == null || format.isBlank()) return "";
        return FORMAT_CODES.matcher(format).replaceAll("");
    }

    public static List<String> wordWrap(String s) {
        return wordWrap(s, 32, 32);
    }

    public static List<String> wordWrap(String s, int lineSize) {
        return wordWrap(s, lineSize, lineSize);
    }

    public static List<String> wordWrap(String s, int firstSegment, int lineSize) {
        var format = getFormatPrefix(s);
        var words = new ArrayList<String>();
        int numChars = firstSegment;
        int start = 0;
        int ix = 0;

        while (ix < s.length()) {
            ix = s.indexOf(' ', ix + 1);
            if (ix == -1) break;

            var sub = s.substring(start, ix).trim();
            int visibleLen = stripFormatting(sub).length() + 1;
            if (visibleLen >= numChars && !sub.isEmpty()) {
                var f = getFormatPrefix(sub);
                if (f != null && sub.startsWith(f)) format = f;
                words.add(applyFormat(format, sub));
                numChars = lineSize;
                start = ix + 1;
            }
        }
        words.add(applyFormat(format, s.substring(start).trim()));
        return words;
    }

    private static String applyFormat(String format, String text) {
        return (format != null && !text.startsWith(String.valueOf(SECTION))) ? format + text : text;
    }

    private static String getFormatPrefix(String s) {
        if (s == null) return null;
        var m = FORMATTING.matcher(s);
        return m.matches() ? m.group("format") : null;
    }

    public static String possessiveString(String str) {
        if (str == null || str.isBlank()) return "";
        return str + (str.endsWith("s") ? "'" : "'s");
    }

    public static String camelcase(String name) {
        if (name == null || name.isEmpty()) return "";
        var sb = new StringBuilder();
        for (String part : name.split("[ _]")) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    public static String formatTps(double tps) {
        double roundedTps = Math.min(tps, 20.0);
        ChatColor color = tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED;
        String asterisk = tps > 20.0 ? "*" : "";
        return color + asterisk + String.format("%.2f", roundedTps);
    }

    public static String toRoman(int number) {
        int l = ROMAN_NUMERALS.floorKey(number);
        if (number == l) return ROMAN_NUMERALS.get(number);
        return ROMAN_NUMERALS.get(l) + toRoman(number - l);
    }

    public static String getItemName(ItemStack item) {
        return item.getType().toString().replace("_", "");
    }
}
