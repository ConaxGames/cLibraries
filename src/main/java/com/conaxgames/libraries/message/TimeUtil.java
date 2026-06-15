package com.conaxgames.libraries.message;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    private TimeUtil() {}

    public static long getCurrentMillis() {
        return System.currentTimeMillis();
    }

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

    public static long parseTime(String time) {
        long totalTime = 0L;
        boolean found = false;
        Matcher matcher = Pattern.compile("\\d+\\D+").matcher(time);

        while (matcher.find()) {
            String s = matcher.group();
            Long value = Long.parseLong(s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
            String type = s.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[1];
            switch (type) {
                case "s" -> totalTime += value;
                case "m" -> totalTime += value * 60;
                case "h" -> totalTime += value * 60 * 60;
                case "d" -> totalTime += value * 60 * 60 * 24;
                case "w" -> totalTime += value * 60 * 60 * 24 * 7;
                case "M" -> totalTime += value * 60 * 60 * 24 * 30;
                case "y" -> totalTime += value * 60 * 60 * 24 * 365;
                default -> {
                    continue;
                }
            }
            found = true;
        }

        return !found ? -1 : totalTime * 1000;
    }

    public static String timeAsString(long timePeriod) {
        long millis = timePeriod;
        StringBuilder output = new StringBuilder();

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output.append(days).append("d ");
        else if (days == 1) output.append(days).append("d ");

        if (hours > 1) output.append(hours).append("h ");
        else if (hours == 1) output.append(hours).append("h ");

        if (minutes > 1) output.append(minutes).append("m ");
        else if (minutes == 1) output.append(minutes).append("m ");

        if (seconds > 1) output.append(seconds).append("s");
        else if (seconds == 1) output.append(seconds).append("s");

        if (output.isEmpty()) {
            output.append("0s");
        }

        return output.toString();
    }

    private static String pluralize(long value, String unit) {
        return value + " " + unit + (value == 1 ? "" : "s");
    }
}
