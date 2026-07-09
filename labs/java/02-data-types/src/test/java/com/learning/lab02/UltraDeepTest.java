package com.learning.lab02;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Data Types lab.
 * Explores primitive promotion, autoboxing, and value types.
 */
class UltraDeepTest {

    @Test
    void binaryNumericPromotion() {
        byte a = 10;
        byte b = 20;
        int c = a + b;
        assertEquals(30, c, "byte + byte promotes to int");
    }

    @Test
    void integerCacheRange() {
        Integer a = 127;
        Integer b = 127;
        assertSame(a, b, "Integer values -128 to 127 should be cached");

        Integer c = 200;
        Integer d = 200;
        assertNotSame(c, d, "Integer values outside cache range should be distinct objects");
        assertEquals(c, d, "But they should still be equal");
    }

    @Test
    void wideningPrecisionLoss() {
        int big = 1234567890;
        float f = big;
        assertNotEquals(big, (int) f, "int to float widening loses precision for large values");
    }
}
