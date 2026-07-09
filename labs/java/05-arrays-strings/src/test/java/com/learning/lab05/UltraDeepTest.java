package com.learning.lab05;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Arrays and Strings lab.
 * Explores array covariance, String interning, compact strings.
 */
class UltraDeepTest {

    @Test
    void arrayCovarianceThrowsArrayStoreException() {
        Object[] objects = new String[10];
        objects[0] = "hello";
        assertThrows(ArrayStoreException.class, () -> {
            objects[1] = Integer.valueOf(42);
        });
    }

    @Test
    void stringInterning() {
        String a = "hello";
        String b = "hel" + "lo";
        assertSame(a, b, "Compile-time constant concatenation should intern");

        String c = new String("hello");
        assertNotSame(a, c, "new String() creates new object");
        assertEquals(a, c, "But equals() should be true");

        String d = c.intern();
        assertSame(a, d, "intern() returns the pooled instance");
    }

    @Test
    void compactStringInternal() {
        String latin1 = "hello";
        String utf16 = "héllo — café";
        assertNotNull(latin1);
        assertNotNull(utf16);
    }
}
