package pl.szelag.gym.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Common string manipulation utilities for data normalization and consistency. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    /**
     * Trims input and converts blank or "null" literal strings to actual null references.
     * @param value raw input string
     * sanitized string or null if input was blank or "null"
     */
    public static String sanitize(String value) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("null")) {
            return null;
        }
        return value.trim();
    }

    /**
     * Normalizes input by sanitizing it and converting to lowercase for consistent database lookups.
     * @param value raw input string
     * sanitized lowercase string or null
     */
    public static String normalize(String value) {
        String sanitized = sanitize(value);
        return sanitized == null ? null : sanitized.toLowerCase();
    }
}