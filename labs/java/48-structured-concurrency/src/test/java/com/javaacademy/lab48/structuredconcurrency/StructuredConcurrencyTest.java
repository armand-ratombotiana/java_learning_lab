package com.javaacademy.lab48.structuredconcurrency;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StructuredConcurrencyTest {

    @Test
    void testStructuredTaskScopeOnSuccess() throws Exception {
        String result = StructuredTaskScopeExample.tryAnySuccessfulResultOrThrow();
        assertEquals("fast-result", result);
    }

    @Test
    void testStructuredTaskScopeOnFailure() throws Exception {
        String result = StructuredTaskScopeExample.tryAwaitAllSuccessfulOrThrow();
        assertTrue(result.contains("part-a") && result.contains("part-b"));
    }

    @Test
    void testScopedValuePropagation() throws Exception {
        ScopedValueExample.main(new String[]{});
    }

    @Test
    void testVirtualThreadStructured() throws Exception {
        long result = VirtualThreadStructuredExample.processMany(200);
        assertTrue(result > 0);
    }
}
