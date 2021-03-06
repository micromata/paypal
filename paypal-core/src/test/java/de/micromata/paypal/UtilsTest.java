package de.micromata.paypal;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void ensureLengthTest() {
        assertNull(Utils.ensureMaxLength(null, 5));
        assertEquals("", Utils.ensureMaxLength("", 5));
        assertEquals("1", Utils.ensureMaxLength("1", 5));
        assertEquals("12345", Utils.ensureMaxLength("12345", 5));
        assertEquals("12...", Utils.ensureMaxLength("123456", 5));
        assertEquals("12...", Utils.ensureMaxLength("1234567890", 5));
    }

    @Test
    void isBlankTest() {
        assertTrue(Utils.isBlank(null));
        assertTrue(Utils.isBlank(""));
        assertTrue(Utils.isBlank("    "));
        assertFalse(Utils.isBlank("."));
        assertFalse(Utils.isBlank("  d  "));
    }


    @Test
    void isNotBlankTest() {
        assertFalse(Utils.isNotBlank(null));
        assertFalse(Utils.isNotBlank(""));
        assertFalse(Utils.isNotBlank("    "));
        assertTrue(Utils.isNotBlank("."));
        assertTrue(Utils.isNotBlank("  d  "));
    }

    @Test
    void addBigDecimalsTest() {
        assertNull(null);
        assertEquals("1", Utils.add(BigDecimal.ONE).toString());
        assertEquals("1", Utils.add(BigDecimal.ZERO, null, BigDecimal.ONE).toString());
        assertEquals("11", Utils.add(null, null, BigDecimal.ZERO, null, BigDecimal.ONE, BigDecimal.TEN).toString());
    }
}