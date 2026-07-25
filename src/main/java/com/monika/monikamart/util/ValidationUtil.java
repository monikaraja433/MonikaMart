package com.monika.monikamart.util;

import com.monika.monikamart.exception.ValidationException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Password must be at least 8 characters, containing at least 1 digit, 1 lower, 1 upper case
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    );

    private ValidationUtil() {}

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static String sanitize(String input) {
        if (input == null) return null;
        return input.trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }

    public static void validatePositivePrice(BigDecimal price, String fieldName, Map<String, String> errors) {
        if (price == null) {
            errors.put(fieldName, fieldName + " is required");
        } else if (price.compareTo(BigDecimal.ZERO) <= 0) {
            errors.put(fieldName, fieldName + " must be greater than 0");
        }
    }

    public static void validatePositiveQuantity(Integer qty, String fieldName, Map<String, String> errors) {
        if (qty == null) {
            errors.put(fieldName, fieldName + " is required");
        } else if (qty < 0) {
            errors.put(fieldName, fieldName + " cannot be negative");
        }
    }

    public static void validateRequiredString(String value, String fieldName, int minLen, int maxLen, Map<String, String> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.put(fieldName, fieldName + " cannot be empty");
        } else if (value.trim().length() < minLen || value.trim().length() > maxLen) {
            errors.put(fieldName, fieldName + " must be between " + minLen + " and " + maxLen + " characters");
        }
    }

    public static void checkErrors(Map<String, String> errors) {
        if (errors != null && !errors.isEmpty()) {
            throw new ValidationException("Validation failed with " + errors.size() + " error(s)", errors);
        }
    }
}
