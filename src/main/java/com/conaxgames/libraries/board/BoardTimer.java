package com.conaxgames.libraries.board;

import org.apache.commons.lang.time.DurationFormatUtils;

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
    private final Board board;
    private final String id;
    private final double duration;
    private final long end;

    /**
     * Creates a new board timer with the specified parameters.
     * 
     * @param board The board this timer belongs to (can be null)
     * @param id The unique identifier for this timer
     * @param duration The duration of the timer in seconds
     */
    public BoardTimer(Board board, String id, double duration) {
        this.board = board;
        this.id = id;
        this.duration = duration;
        this.end = (long) (System.currentTimeMillis() + (duration * MILLISECONDS_PER_SECOND));

        if (board != null) {
            board.getTimers().add(this);
        }
    }

    /**
     * Gets a formatted string representation of the remaining time.
     * 
     * @param format The format type to use
     * @return Formatted string representation of the remaining time
     */
    public String getFormattedString(TimerType format) {
        long remainingTime = this.end - System.currentTimeMillis();
        
        if (format == TimerType.SECONDS) {
            return SECONDS_FORMATTER.format(remainingTime / 1000.0f);
        } else {
            return DurationFormatUtils.formatDuration(remainingTime, MINUTES_SECONDS_FORMAT);
        }
    }

    /**
     * Cancels this timer by removing it from the board.
     */
    public void cancel() {
        if (this.board != null) {
            this.board.getTimers().remove(this);
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
    public Board getBoard() {
        return this.board;
    }

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