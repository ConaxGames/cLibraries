package com.conaxgames.libraries.board;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DecimalFormat;

public class BoardTimer {

    private static final DecimalFormat SECONDS = new DecimalFormat("#0.0");
    private static final String MM_SS = "mm:ss";

    private final String id;
    private final long end;

    public BoardTimer(String id, double durationSeconds) {
        this.id = id;
        this.end = System.currentTimeMillis() + (long) (durationSeconds * 1000);
    }

    public String getFormattedString(TimerType format) {
        long rem = end - System.currentTimeMillis();
        if (rem <= 0) return "0.0";
        return format == TimerType.SECONDS ? SECONDS.format(rem / 1000.0) : DurationFormatUtils.formatDuration(rem, MM_SS);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= end;
    }

    public String getId() { return id; }

    public enum TimerType { SECONDS, MINUTES }
}
