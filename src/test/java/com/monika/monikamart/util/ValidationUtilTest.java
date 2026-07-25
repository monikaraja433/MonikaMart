package com.monika.monikamart.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ValidationUtilTest {

    @Test
    public void testEmailValidation() {
        Assertions.assertTrue(ValidationUtil.isValidEmail("buyer@monikamart.com"));
        Assertions.assertTrue(ValidationUtil.isValidEmail("student.anna.univ@dept.edu.in"));
        Assertions.assertFalse(ValidationUtil.isValidEmail("invalid-email"));
        Assertions.assertFalse(ValidationUtil.isValidEmail("missing@domain"));
        Assertions.assertFalse(ValidationUtil.isValidEmail(null));
    }

    @Test
    public void testPasswordValidation() {
        Assertions.assertTrue(ValidationUtil.isValidPassword("StrongPass123!"));
        Assertions.assertTrue(ValidationUtil.isValidPassword("AnnaUniv2025"));
        Assertions.assertFalse(ValidationUtil.isValidPassword("short1A")); // less than 8 chars
        Assertions.assertFalse(ValidationUtil.isValidPassword("alllowercase1")); // no uppercase
        Assertions.assertFalse(ValidationUtil.isValidPassword("ALLUPPERCASE1")); // no lowercase
        Assertions.assertFalse(ValidationUtil.isValidPassword("NoDigitsPassword")); // no digits
    }

    @Test
    public void testXssSanitization() {
        String unsafe = "<script>alert('xss')</script>&\"'";
        String safe = ValidationUtil.sanitize(unsafe);
        Assertions.assertFalse(safe.contains("<"));
        Assertions.assertFalse(safe.contains(">"));
        Assertions.assertTrue(safe.contains("&lt;script&gt;"));
    }
}
