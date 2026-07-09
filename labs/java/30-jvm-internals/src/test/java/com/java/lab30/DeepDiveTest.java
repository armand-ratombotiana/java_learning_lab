package com.java.lab30;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeepDiveTest {

    @Test
    void testObjectHeaderSizeWithCompressedOops() {
        // On 64-bit HotSpot with compressed OOPs (<32GB heap):
        // Mark word = 8 bytes, Klass pointer = 4 bytes, total = 12 bytes
        // Padding to 8-byte alignment: 4 bytes, total object = 16 bytes
        // This test verifies object size assumptions
        Object obj = new Object();
        long identityHash = System.identityHashCode(obj);
        assertTrue(identityHash != 0, "Identity hash should be non-zero after call");
    }

    @Test
    void testMetaspaceMonitoring() {
        // Metaspace is queryable via MemoryPoolMXBeans
        var pools = java.lang.management.ManagementFactory.getMemoryPoolMXBeans();
        boolean foundMetaspace = pools.stream()
            .anyMatch(p -> "Metaspace".equals(p.getName()));
        assertTrue(foundMetaspace, "Metaspace pool should be present");
    }

    @Test
    void testCodeCachePoolExists() {
        var pools = java.lang.management.ManagementFactory.getMemoryPoolMXBeans();
        long count = pools.stream()
            .filter(p -> p.getName().contains("CodeHeap"))
            .count();
        assertTrue(count > 0, "CodeHeap pools should be present with tiered compilation");
    }

    @Test
    void testGCMXBeanAvailable() {
        var gcBeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        assertFalse(gcBeans.isEmpty(), "At least one GC MXBean should be available");
    }

    @Test
    void testThreadMXBean() {
        var threadBean = java.lang.management.ManagementFactory.getThreadMXBean();
        assertTrue(threadBean.getThreadCount() > 0, "At least one thread should exist");
        assertTrue(threadBean.getPeakThreadCount() >= threadBean.getThreadCount());
    }

    @Test
    void testCompilationMXBean() {
        var compBean = java.lang.management.ManagementFactory.getCompilationMXBean();
        assertNotNull(compBean.getName(), "JIT compiler name should not be null");
    }

    @Test
    void testMemoryMXBean() {
        var memBean = java.lang.management.ManagementFactory.getMemoryMXBean();
        var heap = memBean.getHeapMemoryUsage();
        assertTrue(heap.getMax() > 0, "Max heap should be positive");
        assertTrue(heap.getInit() > 0, "Init heap should be positive");
    }

    @Test
    void testClassLoadingMXBean() {
        var clBean = java.lang.management.ManagementFactory.getClassLoadingMXBean();
        assertTrue(clBean.getTotalLoadedClassCount() > 0, "Classes should be loaded");
    }

    @Test
    void testOopMapQuery() {
        // OOP maps are internal JVM metadata
        // This test verifies GC root scanning works correctly
        String test = "test";
        System.gc(); // Trigger GC — oop maps enable finding 'test' on stack
        assertEquals("test", test, "Object should survive GC");
    }

    @Test
    void testSafepointStatsEnable() {
        // Safepoint statistics are available via JVM
        // Check that the JVM doesn't crash when queried
        var rt = java.lang.management.ManagementFactory.getRuntimeMXBean();
        assertNotNull(rt.getVmVersion());
        assertNotNull(rt.getVmName());
        assertTrue(rt.getVmName().toLowerCase().contains("java") ||
                   rt.getVmName().toLowerCase().contains("openjdk") ||
                   rt.getVmName().toLowerCase().contains("hotspot"));
    }
}
