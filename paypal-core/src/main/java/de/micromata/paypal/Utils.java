package de.micromata.paypal;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Utils {

    /**
     * Ensures scale 2.
     *
     * @param amount Round this amount.
     * @return the rounded amount.
     */
    public static BigDecimal roundAmount(BigDecimal amount) {
        if (amount == null) {
            return amount;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Doesn't round.
     *
     * @param values The values to add (null values supported).
     * @return The sum of all values or null if no non-null value found.
     */
    public static BigDecimal add(BigDecimal... values) {
        BigDecimal sum = null;
        for (BigDecimal value : values) {
            if (value != null) {
                if (sum == null) {
                    sum = value;
                } else {
                    sum = sum.add(value);
                }
            }
        }
        return sum;
    }

    public static String asString(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.toString();
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNotBlank(String value) {
        return value != null && value.trim().length() > 0;
    }

    public static String ensureMaxLength(String value, int length) {
        final String abbrevMarker = "...";
        if (value == null || value.length() <= length || value.length() < 3) {
            return value;
        }
        return value.substring(0, length - 3) + abbrevMarker;
    }
}
