package com.conaxgames.libraries.util;

import java.text.NumberFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Formatter {

    public static String commaFormatInteger(Integer integer) {
        return NumberFormat.getIntegerInstance().format(integer);
    }

    public static String oneDecimalFormat(Double number) {
        return String.format("%.1f", number);
    }

    public static String twoDecimalFormat(Double number) {
        return String.format("%.2f", number);
    }

    public static String formatMoneyKMBT(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return formatMoneyKMBT(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatMoneyKMBT(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
}
