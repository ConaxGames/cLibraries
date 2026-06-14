package com.conaxgames.libraries.message;

public final class TimeUtil {

    private TimeUtil() {}

    public static String millisToRoundedTime(long millis) {
        millis += 1L;
        long seconds = millis / 1_000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;

        if (years > 0) return pluralize(years, "year");
        if (months > 0) return pluralize(months, "month");
        if (weeks > 0) return pluralize(weeks, "week");
        if (days > 0) return pluralize(days, "day");
        if (hours > 0) return pluralize(hours, "hour");
        if (minutes > 0) return pluralize(minutes, "minute");
        return pluralize(seconds, "second");
    }

    private static String pluralize(long value, String unit) {
        return value + " " + unit + (value == 1 ? "" : "s");
    }
}
