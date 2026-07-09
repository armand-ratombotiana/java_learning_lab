package com.learning.lab10;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Ultra-deep tests for Exceptions lab.
 * Explores suppressed exceptions, multi-catch, stack trace lazy construction.
 */
class UltraDeepTest {

    @Test
    void suppressedExceptionFromTryWithResources() {
        class FailingResource implements AutoCloseable {
            private final String name;
            FailingResource(String name) { this.name = name; }
            public void close() {
                throw new RuntimeException("close failed: " + name);
            }
        }
        Exception primary = assertThrows(RuntimeException.class, () -> {
            try (FailingResource r1 = new FailingResource("r1");
                 FailingResource r2 = new FailingResource("r2")) {
                throw new RuntimeException("primary");
            }
        });
        assertEquals("primary", primary.getMessage());
        assertEquals(2, primary.getSuppressed().length);
    }

    @Test
    void multiCatchBytecodeSameHandler() {
        assertDoesNotThrow(() -> {
            try {
                throw new IllegalArgumentException("test");
            } catch (IllegalArgumentException | NullPointerException e) {
                assertEquals("test", e.getMessage());
            }
        });
    }
}
