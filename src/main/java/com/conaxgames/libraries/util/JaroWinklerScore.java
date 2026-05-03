package com.conaxgames.libraries.util;

public class JaroWinklerScore {

    public static double compute(final String s1, final String s2) {

        if (s1 == null || s2 == null || s1.isEmpty() || s2.isEmpty()) {
            return 0;
        }

        if (s1.equals(s2)) {
            return 1;
        }

        int prefixMatch = 0;
        int matches = 0;
        int transpositions = 0;
        int maxLength = Math.max(s1.length(), s2.length());
        int maxMatchDistance = Math.max((int) Math.floor(maxLength / 2.0) - 1, 0);

        final String shorter = s1.length() < s2.length() ? s1 : s2;
        final String longer = s1.length() >= s2.length() ? s1 : s2;
        for (int i = 0; i < shorter.length(); i++) {

            boolean match = shorter.charAt(i) == longer.charAt(i);
            if (match) {
                if (i < 4) {

                    prefixMatch++;
                }
                matches++;
                continue;
            }

            for (int j = Math.max(i - maxMatchDistance, 0); j < Math.min(i + maxMatchDistance, longer.length()); j++) {
                if (i == j) {

                    continue;
                }

                match = shorter.charAt(i) == longer.charAt(j);
                if (match) {
                    transpositions++;
                    break;
                }
            }
        }

        if (matches == 0) {
            return 0;
        }

        transpositions = (int) (transpositions / 2.0);

        double score = 0.3334 * (matches / (double) longer.length() + matches / (double) shorter.length() + (matches - transpositions)
                / (double) matches);
        if (score < 0.7) {
            return score;
        }

        return score + prefixMatch * 0.1 * (1.0 - score);
    }
}