package com.conaxgames.libraries.board;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DecimalFormat;

/**
 * Represents a timer that can be displayed on a scoreboard.
 * This class provides functionality for creating, managing, and formatting timers.
 * 
 * @author ConaxGames
 * @since 1.0
 */
public class BoardTimer {

    // Constants
    private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
    private static final String MINUTES_SECONDS_FORMAT = "mm:ss";
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // Instance fields
    private final String id;
    private final double duration;
    private final long end;

    /**
     * Creates a new board timer with the specified parameters.
     * 
     * @param id The unique identifier for this timer
     * @param duration The duration of the timer in seconds
     */
    public BoardTimer(String id, double duration) {
        this.id = id;
        this.duration = duration;
        this.end = System.currentTimeMillis() + (long) (duration * MILLISECONDS_PER_SECOND);
    }

    /**
     * Gets a formatted string representation of the remaining time.
     * 
     * @param format The format type to use
     * @return Formatted string representation of the remaining time
     */
    public String getFormattedString(TimerType format) {
        long remainingTime = this.end - System.currentTimeMillis();
        
        if (remainingTime <= 0) {
            return "0.0";
        }
        
        if (format == TimerType.SECONDS) {
            return SECONDS_FORMATTER.format(remainingTime / 1000.0);
        } else {
            return DurationFormatUtils.formatDuration(remainingTime, MINUTES_SECONDS_FORMAT);
        }
    }

    /**
     * Checks if this timer has expired.
     * 
     * @return true if the timer has expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() >= this.end;
    }

    /**
     * Gets the remaining time in milliseconds.
     * 
     * @return The remaining time in milliseconds, or 0 if expired
     */
    public long getRemainingTime() {
        long remaining = this.end - System.currentTimeMillis();
        return Math.max(0, remaining);
    }

    // Getter methods
    public String getId() {
        return this.id;
    }

    public double getDuration() {
        return this.duration;
    }

    public long getEnd() {
        return this.end;
    }

    /**
     * Enum representing different timer format types.
     */
    public enum TimerType {
        /** Display time in seconds with decimal precision */
        SECONDS,
        /** Display time in minutes and seconds format */
        MINUTES,
        /** Display time in hours, minutes and seconds format */
        HOURS
    }
}