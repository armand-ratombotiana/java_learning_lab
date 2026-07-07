package com.dsacademy.lab25.spacefilling;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class SpaceFillingCurveTest {

    @Test
    void testZOrderRoundTrip2D() {
        int x = 123, y = 456;
        long z = ZOrderCurve.encode2D(x, y);
        int[] decoded = ZOrderCurve.decode2D(z);
        assertEquals(x, decoded[0]);
        assertEquals(y, decoded[1]);
    }

    @Test
    void testZOrderRoundTrip3D() {
        int x = 100, y = 200, zVal = 300;
        long m = ZOrderCurve.encode3D(x, y, zVal);
        int[] decoded = ZOrderCurve.decode3D(m);
        assertEquals(x, decoded[0]);
        assertEquals(y, decoded[1]);
        assertEquals(zVal, decoded[2]);
    }

    @Test
    void testZOrderZero() {
        long z = ZOrderCurve.encode2D(0, 0);
        assertEquals(0, z);
        int[] decoded = ZOrderCurve.decode2D(0);
        assertEquals(0, decoded[0]);
        assertEquals(0, decoded[1]);
    }

    @Test
    void testHilbertRoundTrip() {
        int order = 8;
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                long h = HilbertCurve.encode2D(x, y, order);
                int[] decoded = HilbertCurve.decode2D(h, order);
                assertEquals(x, decoded[0]);
                assertEquals(y, decoded[1]);
            }
        }
    }

    @Test
    void testHilbertUniqueValues() {
        int order = 4;
        int max = 1 << order;
        java.util.HashSet<Long> set = new java.util.HashSet<>();
        for (int x = 0; x < max; x++) {
            for (int y = 0; y < max; y++) {
                set.add(HilbertCurve.encode2D(x, y, order));
            }
        }
        assertEquals(max * max, set.size());
    }

    @Test
    void testZOrderDistance() {
        long close = ZOrderCurve.distance2D(
            ZOrderCurve.encode2D(5, 5),
            ZOrderCurve.encode2D(5, 6)
        );
        assertTrue(close > 0);
    }

    @Test
    void testEncodeDecodeConsistency() {
        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                long z = ZOrderCurve.encode2D(x, y);
                int[] dec = ZOrderCurve.decode2D(z);
                assertEquals(x, dec[0]);
                assertEquals(y, dec[1]);
            }
        }
    }
}
