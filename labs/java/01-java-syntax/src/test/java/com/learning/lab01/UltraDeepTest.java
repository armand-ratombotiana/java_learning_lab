package com.learning.lab01;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Java Syntax lab.
 * Explores Unicode escapes, desugaring, and JLS edge cases.
 */
class UltraDeepTest {

    @Test
    void unicodeEscapeInIdentifier() {
        int \u0076\u0061\u0072 = 42;
        assertEquals(42, var, "Unicode escape \\u0076\\u0061\\u0072 should produce 'var'");
    }

    @Test
    void tryWithResourcesSuppressedException() {
        class FailingResource implements AutoCloseable {
            public void close() {
                throw new RuntimeException("close failed");
            }
        }
        Exception primary = assertThrows(RuntimeException.class, () -> {
            try (FailingResource r = new FailingResource()) {
                throw new RuntimeException("primary");
            }
        });
        assertEquals("primary", primary.getMessage());
        assertEquals(1, primary.getSuppressed().length);
        assertEquals("close failed", primary.getSuppressed()[0].getMessage());
    }

    @Test
    void stringConcatWithInvokedynamic() {
        String a = "hello";
        String b = "world";
        String result = a + " " + b;
        assertEquals("hello world", result);
        assertInstanceOf(String.class, result);
    }
}
