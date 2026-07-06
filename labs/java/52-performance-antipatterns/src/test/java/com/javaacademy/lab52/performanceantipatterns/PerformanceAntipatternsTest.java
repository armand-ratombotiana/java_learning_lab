package com.javaacademy.lab52.performanceantipatterns;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PerformanceAntipatternsTest {

    @Test
    void testBoxingPerformance() {
        BoxingPerformance.main(new String[]{});
    }

    @Test
    void testThreadLocalLeak() throws Exception {
        ThreadLocalLeakDemo.main(new String[]{});
    }

    @Test
    void testDeadlockDetector() throws Exception {
        DeadlockDetector.main(new String[]{});
    }

    @Test
    void testContentionExample() throws Exception {
        ContentionExample.main(new String[]{});
    }

    @Test
    void testStringConcat() {
        String result = StringConcatAntiPattern.concatWithStringBuilder();
        assertTrue(result.startsWith("item-0,"));
        assertTrue(result.endsWith(","));
        assertEquals(ITERATIONS * 12, result.length()); // approximate
    }

    static final int ITERATIONS = StringConcatAntiPattern.ITERATIONS;
}
