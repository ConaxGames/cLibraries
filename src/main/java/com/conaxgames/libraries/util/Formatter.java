package com.conaxgames.libraries.util;

import java.text.NumberFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Formatter {

    private static final NavigableMap<Double, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000D, "k");
        suffixes.put(1_000_000D, "M");
        suffixes.put(1_000_000_000D, "B");
        suffixes.put(1_000_000_000_000D, "T");
        suffixes.put(1_000_000_000_000_000D, "QD");
        suffixes.put(1_000_000_000_000_000_000D, "QT");
        suffixes.put(1_000_000_000_000_000_000_000D, "SX");
        suffixes.put(1_000_000_000_000_000_000_000_000D, "ST");
        suffixes.put(1_000_000_000_000_000_000_000_000_000D, "O");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000D, "N");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000D, "D");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000_000D, "U");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000_000_000D, "DU");
        suffixes.put(1_000_000_000_000_000_000_000_000_000_000_000_000_000_000D, "TD");
    }

    public static String commaFormatInteger(Integer integer) {
        return NumberFormat.getIntegerInstance().format(integer);
    }

    public static String formatMoneyKMBT(double value) {
        if (value < 0) return "-" + formatMoneyKMBT(-value);
        if (value < 1000) return Integer.toString((int) value);

        Map.Entry<Double, String> e = suffixes.floorEntry(value);
        Double divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = (long) (value / (divideBy / 10));
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    public static Double parseMoney(String input) {
        if (input == null) {
            return null;
        }

        String raw = input.replace(",", "").replace("$", "").trim();
        int end = raw.length();
        double multiplier = 1D;
        for (Map.Entry<Double, String> entry : suffixes.entrySet()) {
            String candidate = entry.getValue();
            int at = raw.length() - candidate.length();
            if (at > 0 && at < end && raw.regionMatches(true, at, candidate, 0, candidate.length())) {
                end = at;
                multiplier = entry.getKey();
            }
        }

        Double value = JavaUtils.tryParseDouble(raw.substring(0, end));
        if (value == null) {
            return null;
        }
        double parsed = value * multiplier;
        return Double.isFinite(parsed) ? parsed : null;
    }
}
