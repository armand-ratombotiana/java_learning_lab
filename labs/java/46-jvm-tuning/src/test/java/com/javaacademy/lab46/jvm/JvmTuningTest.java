package com.javaacademy.lab46.jvm;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class JvmTuningTest {

    @Test
    void testHeapAllocation() {
        long elapsed = HeapSizingDemo.allocate(10);
        assertTrue(elapsed > 0);
    }

    @Test
    void testCodeCache() {
        assertDoesNotThrow(() -> CodeCacheDemo.main(new String[]{}));
    }

    @Test
    void testStringDedupWithIntern() {
        String s1 = new String("dedup-test").intern();
        String s2 = new String("dedup-test").intern();
        assertSame(s1, s2);
    }

    @Test
    void testJvmFlagReporter() {
        assertDoesNotThrow(() -> JvmFlagReporter.main(new String[]{}));
    }
}
