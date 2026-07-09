package com.learning.lab06;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for OOP Basics lab.
 * Explores object layout, identity, equals/hashCode contract.
 */
class UltraDeepTest {

    @Test
    void objectIdentityVsEquality() {
        String a = new String("hello");
        String b = new String("hello");
        assertNotSame(a, b, "Different objects");
        assertEquals(a, b, "But semantically equal");
    }

    @Test
    void recordValueSemantics() {
        record Point(int x, int y) {}
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        assertEquals(p1, p2, "Records have value-based equality");
        assertEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void cloneShallowCopy() {
        record Data(int[] values) {}
        Data original = new Data(new int[]{1, 2, 3});
        int[] values = original.values();
        values[0] = 99;
        assertEquals(99, original.values()[0], "Mutating reference mutates record state");
    }
}
