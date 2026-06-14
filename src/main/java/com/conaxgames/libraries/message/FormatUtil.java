package com.conaxgames.libraries.message;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class FormatUtil {

    private static final char SECTION = '\u00a7';
    private static final Pattern FORMATTING = Pattern.compile("^.*(?<format>(" + SECTION + "[0-9a-fklmor])+).*");
    private static final Pattern FORMAT_CODES = Pattern.compile("(" + SECTION + "|&)[0-9a-fklmor]");

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
}
