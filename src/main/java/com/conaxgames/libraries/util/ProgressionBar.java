package com.conaxgames.libraries.util;

import com.google.common.base.Strings;

/**
 * Utility for building text-based progress bars (e.g. for chat, scoreboards, or action bars).
 * Renders a bar as repeated symbols with configurable completed vs incomplete colors.
 * <p>
 * <b>Usage:</b> Call {@link #construct(int, int)} with current and max values for a default
 * 20-segment bar, or use the overloads to set segment count, symbol, and {@link CC} colors.
 */
public final class ProgressionBar {

    /**
     * Builds a progress bar with default 20 segments, hyphen symbol, and green/gray colors.
     *
     * @param current Current value (clamped to max)
     * @param max     Maximum value
     * @return A colored string of segments representing the progress
     */
    public static String construct(int current, int max) {
        return construct(current, max, 20, '-', CC.GREEN, CC.GRAY);
    }

    /**
     * Builds a progress bar with the given number of segments and default symbol and colors.
     *
     * @param current    Current value (clamped to max)
     * @param max        Maximum value
     * @param totalBars  Number of segments in the bar
     * @return A colored string of segments representing the progress
     */
    public static String construct(int current, int max, int totalBars) {
        return construct(current, max, totalBars, '-', CC.GREEN, CC.GRAY);
    }

    /**
     * Builds a progress bar with full control over segments, symbol, and completed/incomplete colors.
     *
     * @param current           Current value (clamped to max)
     * @param max               Maximum value
     * @param totalBars         Number of segments in the bar
     * @param symbol            Character used for each segment
     * @param completedColor    Color string (e.g. {@link CC#GREEN}) for filled segments
     * @param notCompletedColor  Color string (e.g. {@link CC#GRAY}) for unfilled segments
     * @return A colored string of segments representing the progress
     */
    public static String construct(int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        current = Math.min(current, max);
        float percent = max > 0 ? (float) current / max : 0f;
        int progressBars = Math.max(0, (int) (totalBars * percent));

        return Strings.repeat(completedColor + symbol, progressBars)
                + Strings.repeat(notCompletedColor + symbol, totalBars - progressBars);
    }

}
