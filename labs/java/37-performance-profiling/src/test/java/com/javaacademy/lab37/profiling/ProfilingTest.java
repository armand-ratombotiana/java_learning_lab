package com.javaacademy.lab37.profiling;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ProfilingTest {

    private ProfilingTarget target;

    @BeforeEach
    void setUp() {
        target = new ProfilingTarget();
    }

    @Test
    @DisplayName("Compute Pi produces expected value")
    void computePi() {
        double pi = target.computePi(1000000);
        assertEquals(Math.PI, pi, 0.001);
    }

    @Test
    @DisplayName("Fibonacci recursive produces correct value")
    void fibonacci() {
        assertEquals(55, target.fibonacci(10));
        assertEquals(6765, target.fibonacci(20));
    }

    @Test
    @DisplayName("Fibonacci iterative produces same value")
    void fibonacciIterative() {
        assertEquals(55, target.fibonacciIterative(10));
        assertEquals(target.fibonacci(15), target.fibonacciIterative(15));
    }

    @Test
    @DisplayName("Sum of squares calculation")
    void sumOfSquares() {
        assertEquals(385, target.sumOfSquares(10));
    }

    @Test
    @DisplayName("JMX monitoring returns non-zero heap")
    void jmxHeapUsage() {
        JmxMonitoring jmx = new JmxMonitoring();
        assertTrue(jmx.getHeapMemoryUsage() > 0);
    }

    @Test
    @DisplayName("JMX thread count is positive")
    void jmxThreadCount() {
        JmxMonitoring jmx = new JmxMonitoring();
        assertTrue(jmx.getThreadCount() >= 1);
    }

    @Test
    @DisplayName("JFR event workload runs without error")
    void jfrWorkload() {
        JfrEventExample jfr = new JfrEventExample();
        assertDoesNotThrow(jfr::runWorkload);
    }

    @Test
    @DisplayName("MemoryLeakExample creates leak")
    void memoryLeak() {
        MemoryLeakExample leak = new MemoryLeakExample();
        int before = leak.getLeakBucketSize();
        leak.leakMemory(1);
        assertTrue(leak.getLeakBucketSize() > before);
        leak.clearLeak();
    }

    @Test
    @DisplayName("Profiling target sorts array correctly")
    void sortArray() {
        int[] sorted = target.sortLargeArray(1000);
        for (int i = 0; i < sorted.length - 1; i++) {
            assertTrue(sorted[i] <= sorted[i + 1]);
        }
    }
}
