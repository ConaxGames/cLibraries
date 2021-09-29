package com.conaxgames.libraries.message;


import com.conaxgames.libraries.util.DateTimeFormats;
import com.conaxgames.libraries.util.Duration;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TimeUtil {

    private TimeUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static Timestamp addDuration(long duration) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + duration));
    }

    public static Timestamp truncateTimestamp(Timestamp timestamp) {
        if (timestamp.toLocalDateTime().getYear() > 2037) {
            timestamp.setYear(2037);
        }
        return timestamp;
    }

    public static Timestamp addDuration(Timestamp timestamp) {
        return truncateTimestamp(new Timestamp(System.currentTimeMillis() + timestamp.getTime()));
    }

    public static Timestamp fromMillis(long millis) {
        return new Timestamp(millis);
    }

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static long getCurrentMillis() {
        return System.currentTimeMillis();
    }

    public static long toMillis(String time) {
        if (time == null) {
            return -1;
        }

        String type = time.substring(time.length() - 1, time.length());
        Duration duration = Duration.getByName(type);
        if (duration == null) {
            return -1;
        }

        int rawTime = Integer.parseInt(time.substring(0, time.length() - 1));

        return rawTime * duration.getDuration();
    }

    public static String millisToRoundedTime(long millis) {
        millis += 1L; // for good shit men

        long seconds = millis / 1000L;
        long minutes = seconds / 60L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        long weeks = days / 7L;
        long months = weeks / 4L;
        long years = months / 12L;

        if (years > 0) {
            return years + " year" + (years == 1 ? "" : "s");
        } else if (months > 0) {
            return months + " month" + (months == 1 ? "" : "s");
        } else if (weeks > 0) {
            return weeks + " week" + (weeks == 1 ? "" : "s");
        } else if (days > 0) {
            return days + " day" + (days == 1 ? "" : "s");
        } else if (hours > 0) {
            return hours + " hour" + (hours == 1 ? "" : "s");
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes == 1 ? "" : "s");
        } else {
            return seconds + " second" + (seconds == 1 ? "" : "s");
        }
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
                case "s":
                    totalTime += value;
                    found = true;
                    break;
                case "m":
                    totalTime += value * 60;
                    found = true;
                    break;
                case "h":
                    totalTime += value * 60 * 60;
                    found = true;
                    break;
                case "d":
                    totalTime += value * 60 * 60 * 24;
                    found = true;
                    break;
                case "w":
                    totalTime += value * 60 * 60 * 24 * 7;
                    found = true;
                    break;
                case "M":
                    totalTime += value * 60 * 60 * 24 * 30;
                    found = true;
                    break;
                case "y":
                    totalTime += value * 60 * 60 * 24 * 365;
                    found = true;
                    break;
            }
        }

        return !found ? -1 : totalTime * 1000;
    }

    public static String timeAsStringLongFormat(long timePeriod){
        long millis = timePeriod;
        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += days + " days ";
        else if (days == 1) output += days + " day ";

        if (hours > 1) output += hours + " hours ";
        else if (hours == 1) output += hours + " hour ";

        if (minutes > 1) output += minutes + " minutes ";
        else if (minutes == 1) output += minutes + " minute ";

        if (seconds > 1) output += seconds + " seconds ";
        else if (seconds == 1) output += seconds + " second ";

        return output;
    }

    public static String timeAsString(long timePeriod){
        long millis = timePeriod;
        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        if (days > 1) output += days + "d ";
        else if (days == 1) output += days + "d ";

        if (hours > 1) output += hours + "h ";
        else if (hours == 1) output += hours + "h ";

        if (minutes > 1) output += minutes + "m ";
        else if (minutes == 1) output += minutes + "m ";

        if (seconds > 1) output += seconds + "s";
        else if (seconds == 1) output += seconds + "s";

        if (output.isEmpty()) {
            output += "0s";
        }

        return output;
    }

    public static String timeAsStringCondensed(long timePeriod){
        long millis = timePeriod;
        String output = "";

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        if (days > 1) output += days + "d ";
        else if (days == 1) output += days + "d ";

        if (hours > 1) output += hours + "h ";
        else if (hours == 1) output += hours + "h ";

        if (minutes > 1) output += minutes + "m ";
        else if (minutes == 1) output += minutes + "m ";

        return output;
    }

    private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1L);

    public static String getTimerRemaining(long millis, boolean milliseconds) {
        return getTimerRemaining(millis, milliseconds, true);
    }

    public static String getTimerRemaining(long duration, boolean milliseconds, boolean trail) {
        if (milliseconds && duration < MINUTE) {
            return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
        } else {
            return DurationFormatUtils.formatDuration(duration, (duration >= HOUR ? "HH:" : "") + "mm:ss");
        }
    }
}
