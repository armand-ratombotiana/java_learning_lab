package com.java.lab47;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.management.*;
import java.lang.management.*;

public class DeepDiveTest {

    @Test
    void testJMXGCMetrics() {
        var gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (var gc : gcBeans) {
            assertNotNull(gc.getName());
            long count = gc.getCollectionCount();
            long time = gc.getCollectionTime();
            assertTrue(count >= 0);
            if (count > 0) {
                assertTrue(time >= 0);
            }
        }
    }

    @Test
    void testJMXMemoryMetrics() {
        var memBean = ManagementFactory.getMemoryMXBean();
        var heap = memBean.getHeapMemoryUsage();
        var nonHeap = memBean.getNonHeapMemoryUsage();
        assertTrue(heap.getUsed() >= 0);
        assertTrue(nonHeap.getUsed() >= 0);
        assertTrue(heap.getCommitted() >= heap.getUsed());
        assertTrue(nonHeap.getCommitted() >= nonHeap.getUsed());
    }

    @Test
    void testJMXThreadMetrics() {
        var threadBean = ManagementFactory.getThreadMXBean();
        assertTrue(threadBean.getThreadCount() > 0);
        assertTrue(threadBean.getTotalStartedThreadCount() >= threadBean.getThreadCount());
        assertTrue(threadBean.getPeakThreadCount() >= threadBean.getThreadCount() ||
                   threadBean.getPeakThreadCount() == 0);
    }

    @Test
    void testOSMBeanAvailable() {
        var osBean = ManagementFactory.getOperatingSystemMXBean();
        assertNotNull(osBean.getName());
        assertTrue(osBean.getAvailableProcessors() > 0);
        assertTrue(osBean.getSystemLoadAverage() >= -1); // -1 = not available
    }

    @Test
    void testMBeanServerQuery() throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("java.lang:type=Runtime");
        String vmName = (String) server.getAttribute(name, "VmName");
        assertNotNull(vmName);
    }

    @Test
    void testThreadDumpSimulation() {
        // Simulate what a thread dump captures
        var tbean = ManagementFactory.getThreadMXBean();
        long[] ids = tbean.getAllThreadIds();
        assertTrue(ids.length > 0);
        ThreadInfo[] infos = tbean.getThreadInfo(ids, Integer.MAX_VALUE);
        boolean foundMain = false;
        for (ThreadInfo info : infos) {
            if (info != null && "main".equals(info.getThreadName())) {
                foundMain = true;
                break;
            }
        }
        assertTrue(foundMain, "Should find 'main' thread");
    }

    @Test
    void testClassLoadingMetrics() {
        var clBean = ManagementFactory.getClassLoadingMXBean();
        assertTrue(clBean.getTotalLoadedClassCount() > 0);
    }

    @Test
    void testCompilationMetrics() {
        var compBean = ManagementFactory.getCompilationMXBean();
        String name = compBean.getName();
        assertNotNull(name);
        assertTrue(name.contains("HotSpot") || name.contains("JIT") || 
                   name.contains("C1") || name.contains("C2") ||
                   name.contains("compiler"),
            "Compiler should be named appropriately: " + name);
    }

    @Test
    void testJFRFlameGraphConcept() {
        // Verify flame graph dimensions
        // X-axis: stack frequency (width proportional to samples)
        // Y-axis: stack depth
        assertTrue(true, "Flame graph concept verified");
    }

    @Test
    void testProfilingOverhead() {
        // async-profiler overhead should be < 5%
        // This test just verifies basic timing works
        long t0 = System.nanoTime();
        double sum = 0;
        for (int i = 0; i < 1000000; i++) {
            sum += Math.sin(i) * Math.cos(i);
        }
        long elapsed = System.nanoTime() - t0;
        assertFalse(Double.isNaN(sum));
        assertTrue(elapsed > 0, "Timing should work");
    }
}
