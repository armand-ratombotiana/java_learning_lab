package com.javaacademy.lab45.gc;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class GcDeepDiveTest {

    @Test
    void testGcComparisonRuns() {
        assertDoesNotThrow(() -> GcComparisonDemo.main(new String[]{}));
    }

    @Test
    void testG1Allocation() {
        assertDoesNotThrow(() -> G1GcDemo.main(new String[]{}));
    }

    @Test
    void testZgcAllocation() {
        assertDoesNotThrow(() -> ZGcDemo.main(new String[]{}));
    }

    @Test
    void testGcRoots() {
        GcRootExample.staticReference();
        assertDoesNotThrow(() -> GcRootExample.stackReference());
    }

    @Test
    void testGcLogging() {
        assertDoesNotThrow(() -> GcLoggingExample.main(new String[]{}));
    }
}
