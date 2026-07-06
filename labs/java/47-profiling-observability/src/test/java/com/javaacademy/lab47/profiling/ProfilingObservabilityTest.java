package com.javaacademy.lab47.profiling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProfilingObservabilityTest {

    @Test
    void testCpuProfilingComputation() {
        long result = CpuProfilingTarget.computeHeavy(42);
        assertTrue(result != 0, "Computation should produce non-zero result");
    }

    @Test
    void testAllocationProfilingRuns() {
        AllocationProfilingTarget.main(new String[]{});
    }

    @Test
    void testLockProfilingCounter() throws InterruptedException {
        LockProfilingTarget.main(new String[]{});
    }

    @Test
    void testJmxMBeanRegistration() throws Exception {
        JmxMonitorExample.main(new String[]{});
    }
}
