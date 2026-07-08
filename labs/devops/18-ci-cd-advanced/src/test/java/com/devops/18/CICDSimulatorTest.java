package com.devops.eighteen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

class CICDSimulatorTest {
    private CICDSimulator simulator;

    @BeforeEach
    void setUp() {
        simulator = new CICDSimulator("test-sim");
    }

    @Test
    @DisplayName("Should simulate operations successfully")
    void testSimulate() {
        var result = simulator.simulateOperation("test-op");
        assertNotNull(result);
        assertNotNull(result.operationType());
        assertNotNull(result.timestamp());
        assertTrue(result.durationMs() >= 0);
    }

    @RepeatedTest(5)
    @DisplayName("Should track operation count")
    void testOperationCount() {
        long before = simulator.getOperationCount();
        simulator.simulateOperation("op-" + before);
        assertEquals(before + 1, simulator.getOperationCount());
    }

    @Test
    @DisplayName("Should handle multiple operations")
    void testMultipleOperations() {
        for (int i = 0; i < 10; i++) {
            var result = simulator.simulateOperation("batch-op-" + i);
            assertNotNull(result);
        }
        assertEquals(10, simulator.getOperationCount());
    }

    @Test
    @DisplayName("Should record valid timestamps")
    void testTimestamps() {
        var result = simulator.simulateOperation("timestamp-test");
        assertNotNull(result.timestamp());
        assertFalse(result.timestamp().isEmpty());
    }

    @Test
    @DisplayName("Should handle concurrent operations")
    void testConcurrentOperations() throws Exception {
        var threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            int num = i;
            threads[i] = new Thread(() -> simulator.simulateOperation("concurrent-" + num));
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        assertEquals(5, simulator.getOperationCount());
    }
}
