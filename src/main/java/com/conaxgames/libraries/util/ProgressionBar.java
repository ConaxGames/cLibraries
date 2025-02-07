package com.conaxgames.libraries.util;

import com.conaxgames.libraries.util.CC;
import com.google.common.base.Strings;

public class ProgressionBar {

    public static String construct(int current, int max) {
        return construct(current, max, 20, '-', CC.GREEN, CC.GRAY);
    }

    public static String construct(int current, int max, int totalBars) {
        return construct(current, max, totalBars, '-', CC.GREEN, CC.GRAY);
    }

    public static String construct(int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        /* Fixes overflow */
        if (current > max) {
            current = max;
        }

        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        /* Fixes negative bars (?) */
        if (progressBars < 0) {
            progressBars = 0;
        }

        return Strings.repeat(completedColor + symbol, progressBars)
                + Strings.repeat(notCompletedColor + symbol, totalBars - progressBars);
    }

}
