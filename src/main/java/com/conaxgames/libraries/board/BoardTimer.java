package com.conaxgames.libraries.board;

import lombok.Getter;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DecimalFormat;

/**
 * Simple countdown timer for use on a {@link Board}. Created with a duration; {@link #getFormattedString}
 * returns remaining time in the chosen format. Use in {@link BoardAdapter#getScoreboard} with
 * {@link Board#getTimer} and {@link Board#addTimer} to show countdowns (e.g. event end).
 */
public class BoardTimer {

    private static final DecimalFormat SECONDS = new DecimalFormat("#0.0");
    private static final String MM_SS = "mm:ss";

    @Getter
    private final String id;
    private final long end;

    /**
     * Creates a timer that ends after the given duration in seconds from now.
     */
    public BoardTimer(String id, double durationSeconds) {
        this.id = id;
        this.end = System.currentTimeMillis() + (long) (durationSeconds * 1000);
    }

    /**
     * Returns the remaining time as a string. SECONDS gives decimal seconds (e.g. "12.3");
     * MINUTES gives "mm:ss". Returns "0.0" when expired.
     */
    public String getFormattedString(TimerType format) {
        long rem = end - System.currentTimeMillis();
        if (rem <= 0) return "0.0";
        return format == TimerType.SECONDS ? SECONDS.format(rem / 1000.0) : DurationFormatUtils.formatDuration(rem, MM_SS);
    }

    /**
     * Returns true when the timer end time has passed. Expired timers are removed by {@link Board#getTimer}.
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= end;
    }

    /**
     * Format for {@link BoardTimer#getFormattedString}: decimal seconds or mm:ss.
     */
    public enum TimerType { SECONDS, MINUTES }
}
