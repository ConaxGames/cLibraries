package com.conaxgames.libraries.util;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * This is used for generic Java utilities.
 */
public final class JavaUtils {

    /**
     * Character matcher used to test if a string reference is ASCII
     */
    private static final CharMatcher CHAR_MATCHER_ASCII = CharMatcher.inRange('0', '9').
            or(CharMatcher.inRange('a', 'z')).
            or(CharMatcher.inRange('A', 'Z')).
            or(CharMatcher.whitespace()).
            precomputed();

    /**
     * Regex pattern to validate a UUID.
     */
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}");

    /**
     * The default amount of decimal places to format a number to.
     */
    private static final int DEFAULT_NUMBER_FORMAT_DECIMAL_PLACES = 5;

    private JavaUtils() {
    }

    public static Integer tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    public static Double tryParseDouble(String string) {
        try {
            return Double.parseDouble(string);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /**
     * Checks if a given String is a UUID.
     *
     * @param string a string reference to check
     * @return {@code true} if the given String is a UUID
     */
    public static boolean isUUID(String string) {
        return UUID_PATTERN.matcher(string).find();
    }

    /**
     * Checks if a given string is alphanumeric.
     *
     * @param string a string reference to check
     * @return {@code true} if the given String is alphanumeric
     */
    public static boolean isAlphanumeric(String string) {
        return CHAR_MATCHER_ASCII.matchesAllOf(string);
    }

    /**
     * Checks if an Iterable contains a search character, handling {@code null}.
     *
     * @param elements the {@code Iterable} to check, entries may be null
     * @param string   the string to find
     * @return true if the iterable contains string
     */
    public static boolean containsIgnoreCase(Iterable<? extends String> elements, String string) {
        for (String element : elements) {
            if (StringUtils.containsIgnoreCase(element, string)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Formats a Number with {@link JavaUtils#DEFAULT_NUMBER_FORMAT_DECIMAL_PLACES} amount of decimal
     * places using {@link RoundingMode#HALF_DOWN} for calculating.
     *
     * @param number the {@link Number} to format
     * @return a {@code string} that has been formatted
     */
    public static String format(Number number) {
        return format(number, DEFAULT_NUMBER_FORMAT_DECIMAL_PLACES);
    }

    /**
     * Formats a Number with a given amount of decimal places using {@link RoundingMode#HALF_DOWN}
     * for calculating.
     *
     * @param number        the {@link Number} to format
     * @param decimalPlaces the decimal places to format to
     * @return a {@code string} that has been formatted
     */
    public static String format(Number number, int decimalPlaces) {
        return format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    /**
     * Formats a Number with a given amount of decimal places and a RoundingMode
     * to use for calculating.
     *
     * @param number        the {@link Number} to format
     * @param decimalPlaces the decimal places to format to
     * @param roundingMode  the {@link RoundingMode} for calculating
     * @return a {@code string} that has been formatted
     */
    public static String format(Number number, int decimalPlaces, RoundingMode roundingMode) {
        Preconditions.checkNotNull(number, "The number cannot be null");
        return new BigDecimal(number.toString()).setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
    }

    //TODO: The following below needs to be cleaned up and/or rewritten.

    /**
     * Joins a collection of strings together using {@link Joiner#join(Iterable)} as a base
     * with the last object using 'and' just before instead of the selected delimiter as a comma.
     *
     * @param collection         the collection to join
     * @param delimiterBeforeAnd if the delimiterBeforeAnd should be shown before the 'and' text
     * @return the returned list or empty string is collection is null or empty
     */
    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd) {
        return JavaUtils.andJoin(collection, delimiterBeforeAnd, ", ");
    }

    /**
     * Joins a collection of strings together using {@link Joiner#join(Iterable)} as a base
     * with the last object using 'and' just before instead of the selected delimiter.
     *
     * @param collection         the collection to join
     * @param delimiterBeforeAnd if the delimiterBeforeAnd should be shown before the 'and' text
     * @param delimiter          the delimiter to join with
     * @return the returned list or empty string is collection is null or empty
     */
    public static String andJoin(Collection<String> collection, boolean delimiterBeforeAnd, String delimiter) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }

        List<String> contents = new ArrayList<>(collection);
        String last = contents.remove(contents.size() - 1);

        StringBuilder builder = new StringBuilder(Joiner.on(delimiter).join(contents));
        if (delimiterBeforeAnd) {
            builder.append(delimiter);
        }

        return builder.append(" and ").append(last).toString();
    }

    /**
     * Parses a string describing measures of time (e.g. "1d 1m 1s") to milliseconds
     * <p>Source: http://stackoverflow.com/questions/4015196/is-there-a-java-library-that-converts-strings-describing-measures-of-time-e-g</p>
     *
     * @param input the string to parse
     * @return the parsed time in milliseconds or -1 if could not
     */
    public static long parse(String input) {
        if (input == null || input.isEmpty()) {
            return -1L;
        }

        long result = 0L;
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (Character.isDigit(c)) {
                number.append(c);
                continue;
            }

            String str;
            if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                result += convert(Integer.parseInt(str), c);
                number = new StringBuilder();
            }
        }

        return result;
    }

    /**
     * <p>Source: http://stackoverflow.com/questions/4015196/is-there-a-java-library-that-converts-strings-describing-measures-of-time-e-g</p>
     */
    private static long convert(int value, char unit) {
        switch (unit) {
            case 'y' | 'Y':
                return value * TimeUnit.DAYS.toMillis(365L);
            case 'M':
                return value * TimeUnit.DAYS.toMillis(30L);
            case 'd' | 'D':
                return value * TimeUnit.DAYS.toMillis(1L);
            case 'h' | 'H':
                return value * TimeUnit.HOURS.toMillis(1L);
            case 'm':
                return value * TimeUnit.MINUTES.toMillis(1L);
            case 's' | 'S':
                return value * TimeUnit.SECONDS.toMillis(1L);
            default:
                return -1L;
        }
    }
}
