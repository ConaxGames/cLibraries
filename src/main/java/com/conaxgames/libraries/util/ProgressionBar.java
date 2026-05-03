package com.conaxgames.libraries.util;

import com.google.common.base.Strings;

public final class ProgressionBar {

    public static String construct(int current, int max) {
        return construct(current, max, 20, '-', CC.GREEN, CC.GRAY);
    }

    public static String construct(int current, int max, int totalBars) {
        return construct(current, max, totalBars, '-', CC.GREEN, CC.GRAY);
    }

    public static String construct(int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        current = Math.min(current, max);
        float percent = max > 0 ? (float) current / max : 0f;
        int progressBars = Math.max(0, (int) (totalBars * percent));

        return Strings.repeat(completedColor + symbol, progressBars)
                + Strings.repeat(notCompletedColor + symbol, totalBars - progressBars);
    }

}
