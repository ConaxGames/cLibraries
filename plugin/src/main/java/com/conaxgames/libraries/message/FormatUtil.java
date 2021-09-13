package com.conaxgames.libraries.message;

import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Format utility
 */
public enum FormatUtil {;
    private static final Pattern FORMATTING = Pattern.compile("^.*(?<format>(\u00a7[0-9a-fklmor])+).*");
    public static String stripFormatting(String format) {
        if (format == null || format.trim().isEmpty()) {
            return "";
        }
        return format.replaceAll("(\u00a7|&)[0-9a-fklmor]", "");
    }

    public static String normalize(String format) {
        if (format == null || format.trim().isEmpty()) {
            return "";
        }
        return format.replaceAll("(\u00a7|&)([0-9a-fklmor])", "\u00a7$2");
    }

    public static List<String> wordWrap(String s) {
        return wordWrap(s, 24, 24);
    }

    public static List<String> wordWrap(String s, int lineSize) {
        return wordWrap(s, lineSize, lineSize);
    }

    /**
     * Wraps the string rather lazyly around the linesize (over).
     *
     * I.e.
     * <pre>
     *   this is a line of words
     * </pre>
     * Will break into the following with linesize of 11:
     * <pre>
     *   this is a line
     *   of words
     * </pre>
     * Note that the first line is longer than 11 (14).
     */
    public static List<String> wordWrap(String s, int firstSegment, int lineSize) {
        String format = getFormat(s);
        if (format == null || !s.startsWith(format)) {
            format = "";
        }
        List<String> words = new ArrayList<>();
        int numChars = firstSegment;
        int ix = 0;
        int jx = 0;
        while (ix < s.length()) {
            ix = s.indexOf(' ', ix+1);
            if (ix != -1) {
                String subString = s.substring(jx, ix).trim();
                String f = getFormat(subString);
                int chars = stripFormatting(subString).length() + 1; // remember the space
                if (chars >= numChars) {
                    if (f != null) {
                        format = f;
                    }
                    if (!subString.isEmpty()) {
                        words.add(withFormat(format, subString));
                        numChars = lineSize;
                        jx = ix + 1;
                    }
                }
            } else {
                break;
            }
        }
        words.add(withFormat(format, s.substring(jx).trim()));
        return words;
    }

    public static List<String> wordWrapStrict(String s, int lineLength) {
        List<String> lines = new ArrayList<>();
        String format = getFormat(s);
        if (format == null || !s.startsWith(format)) {
            format = "";
        }
        String[] words = s.split(" ");
        String line = "";
        for (String word: words) {
            String test = stripFormatting(line + " " + word).trim();
            if (test.length() <= lineLength) {
                // add word
                line += (line.isEmpty() ? "" : " ") + word;
            } else if (line.isEmpty() || stripFormatting(word).length() > lineLength) {
                // add word truncated
                String f = getFormat(word);
                String strip = stripFormatting(word);
                do {
                    int len = Math.min(strip.length(), lineLength-line.length()-1);
                    lines.add(withFormat(format, line + (line.isEmpty() ? "" : " ") + strip.substring(0, len)));
                    strip = strip.substring(len);
                    if (f != null) {
                        format = f;
                    }
                } while (strip.length() > lineLength);
                line = strip;
            } else {
                // add line, then start a new
                lines.add(withFormat(format, line));
                String f = getFormat(line);
                if (f != null) {
                    format = f;
                }
                line = word;
            }
        }
        if (!line.isEmpty()) {
            lines.add(withFormat(format, line));
        }
        return lines;
    }

    private static String withFormat(String format, String subString) {
        String sf = null;
        if (!subString.startsWith("\u00a7")) {
            sf = format + subString;
        } else {
            sf = subString;
        }
        return sf;
    }

    private static String getFormat(String s) {
        Matcher m = FORMATTING.matcher(s);
        String format = null;
        if (m.matches() && m.group("format") != null) {
            format = m.group("format");
        }
        return format;
    }

    public static String join(List<String> list, String separator) {
        String joined = "";
        for (String s : list) {
            joined += s + separator;
        }
        joined = !list.isEmpty() ? joined.substring(0, joined.length() - separator.length()) : joined;
        return joined;
    }

    public static List<String> prefix(List<String> list, String prefix) {
        List<String> prefixed = new ArrayList<>(list.size());
        for (String s : list) {
            prefixed.add(prefix + s);
        }
        return prefixed;
    }

    public static String camelcase(String name) {
        if (name == null || name.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String part : name.split("[ _]")) {
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * Escapes formatting by "denormalizing" back to using &amp; instead of §.
     * @param formatString A formatstring (formerly normalized).
     * @return A non-format string using &amp; instead of §.
     * @since 1.10
     */
    public static String escape(String formatString) {
        String escaped = normalize(formatString);
        escaped = escaped
                .replaceAll("\u00a7", "&");
        return escaped;
    }

    public static String possessiveString(String str) {
        return str + (str.endsWith("s") ? "'": "'s");
    }

    public static String formatTps(double tps) {
        //TODO FAST MATH BOWP OLIVER WTF
        return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED)
                .toString()
                + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
    }

    private final static TreeMap<Integer, String> ROMAN_NUMERALS = new TreeMap<>();

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
        ROMAN_NUMERALS.put(0, ""); // Safety
    }

    public static String toRoman(int number) {
        int l = ROMAN_NUMERALS.floorKey(number);
        if (number == l) {
            return ROMAN_NUMERALS.get(number);
        }
        return ROMAN_NUMERALS.get(l) + toRoman(number - l);
    }

    /**
     * Returns the name of the item according to the creative inventory. If an item
     * has a custom name, the custom name will be returned. This can be used in any place that
     * resolves an item name.
     *
     * @param item the item to lookup
     * @return name of the item
     */
    public static String getItemName(ItemStack item) {
        return item.getType().toString().replace("_", "");
//        if (LibNMSManager.getInstance().getServerVersion().after(LibServerVersion.v1_8_R3)) {
//            return org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack.asNMSCopy(item).getName().getString();
//        }  else {
//            return CraftItemStack.asNMSCopy(item).getName();
//        }
    }

    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd) {
        return andJoin(collection, delimiterBeforeAnd, ", ");
    }

    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd, String delimiter) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        List<String> contents = new ArrayList<String>(collection);
        String last = contents.remove(contents.size() - 1);
        StringBuilder builder = new StringBuilder(Joiner.on(delimiter).join(contents));
        if (delimiterBeforeAnd) {
            builder.append(delimiter);
        }
        return builder.append((collection.size() <= 1 ? "" : " and ")).append(last).toString();
    }

}
