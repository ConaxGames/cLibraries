package com.conaxgames.libraries.util;

import java.text.NumberFormat;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

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

    public static String formatMoneyKMBT(double value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Double.MIN_VALUE) return formatMoneyKMBT(Double.MIN_VALUE + 1);
        if (value < 0) return "-" + formatMoneyKMBT(-value);
        if (value < 1000) return Integer.toString((int)value); //deal with easy case

        Map.Entry<Double, String> e = suffixes.floorEntry(value);
        Double divideBy = e.getKey();
        String suffix = e.getValue();

        double truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? String.format("%.1f", (truncated / 10d)) + suffix : String.format("%.0f", (truncated / 10)) + suffix;
    }

    public static String formatTimeMMSS(long secs) {
        return formatTimeMMSS((int) TimeUnit.MILLISECONDS.toSeconds(secs));
    }

    /**
     * Formats the time into a format of HH:MM:SS. Example: 3600 (1 hour) displays as '01:00:00'
     *
     * @param secs The input time, in seconds.
     * @return The HH:MM:SS formatted time.
     */
    public static String formatTimeMMSS(int secs) {
        // Calculate the seconds to display:
        int seconds = secs % 60;
        secs -= seconds;

        // Calculate the minutes:
        long minutesCount = secs / 60;
        long minutes = minutesCount % 60;
        minutesCount -= minutes;

        long hours = minutesCount / 60;

        return (hours > 0 ? (hours < 10 ? "0" : "") + hours + ":" : "") + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

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
    }
}
