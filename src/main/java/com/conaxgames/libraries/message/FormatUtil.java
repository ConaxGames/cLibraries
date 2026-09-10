package com.conaxgames.libraries.message;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Pattern;

public final class FormatUtil {

    private static final char SECTION = '\u00a7';
    private static final Pattern FORMAT_CODES = Pattern.compile("(" + SECTION + "|&)[0-9a-fklmorpst]");
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
        if (format == null || format.trim().isEmpty()) return "";
        return FORMAT_CODES.matcher(format).replaceAll("");
    }

    public static List<String> wordWrap(String s) {
        return wordWrap(s, 32, 32);
    }

    public static List<String> wordWrap(String s, int lineSize) {
        return wordWrap(s, lineSize, lineSize);
    }

    public static List<String> wordWrap(String s, int firstSegment, int lineSize) {
        String format = getFormatPrefix(s);
        List<String> words = new ArrayList<>();
        int numChars = firstSegment;
        int start = 0;
        int ix = 0;

        while (ix < s.length()) {
            ix = s.indexOf(' ', ix + 1);
            if (ix == -1) break;

            String sub = stripTrailing(s.substring(start, ix));
            int visibleLen = CC.stripAllColor(CC.translate(sub)).length() + 1;
            if (visibleLen >= numChars && !sub.isEmpty()) {
                String f = getFormatPrefix(sub);
                if (!f.isEmpty()) format = f;
                words.add(applyFormat(format, sub));
                numChars = lineSize;
                start = ix + 1;
            }
        }
        words.add(applyFormat(format, stripTrailing(s.substring(start))));
        return words;
    }

    private static String applyFormat(String format, String text) {
        if (format == null || format.isEmpty() || text.isEmpty()) return text;
        if (text.charAt(0) == '&' || text.charAt(0) == SECTION) return text;
        return format + text;
    }

    private static String getFormatPrefix(String s) {
        if (s == null || s.trim().isEmpty()) return "";
        return ChatColor.getLastColors(CC.translate(s)).replace(SECTION, '&');
    }

    // Trailing-only trim so leading indentation survives the wrap.
    private static String stripTrailing(String s) {
        int end = s.length();
        while (end > 0 && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }
        return s.substring(0, end);
    }

    public static String possessiveString(String str) {
        if (str == null || str.trim().isEmpty()) return "";
        return str + (str.endsWith("s") ? "'" : "'s");
    }

    public static String camelcase(String name) {
        if (name == null || name.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
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
