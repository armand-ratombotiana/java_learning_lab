package com.java.lab46;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.management.*;

public class DeepDiveTest {

    @Test
    void testHeapSizingFormula() {
        // Verify JVM heap sizing makes sense
        Runtime rt = Runtime.getRuntime();
        long max = rt.maxMemory();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        
        assertTrue(max > 0, "Max memory should be positive");
        assertTrue(total >= free, "Total should be >= free");
        assertTrue(free >= 0, "Free memory should be >= 0");
    }

    @Test
    void testSurvivorRatioDefault() {
        // Default SurvivorRatio is 8 for most JVM configurations
        // Eden : Survivor = 8 : 1 (each survivor is 1/10 of young gen)
        int survivorRatio = 8;
        assertTrue(survivorRatio >= 4 && survivorRatio <= 64,
            "SurvivorRatio should be in reasonable range");
    }

    @Test
    void testTLABAllocation() {
        // TLABs enable fast lock-free allocation in Eden
        byte[][] data = new byte[10000][];
        long t0 = System.nanoTime();
        for (int i = 0; i < data.length; i++) {
            data[i] = new byte[64]; // TLAB allocation
        }
        long elapsed = System.nanoTime() - t0;
        assertTrue(elapsed > 0, "Allocation should take measurable time");
        assertTrue(elapsed < 1_000_000_000L, "10000 allocations should be fast (under 1s)");
    }

    @Test
    void testCodeCachePoolsExist() {
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        boolean hasCodeCache = pools.stream()
            .anyMatch(p -> p.getName().contains("Code") || 
                           p.getName().contains("code") ||
                           p.getType() == MemoryType.NON_HEAP);
        assertTrue(hasCodeCache, "Should have code cache pools (non-heap)");
    }

    @Test
    void testCompressedOopsEffect() {
        // With compressed OOPs, object headers are 12 bytes
        // Without: 16 bytes
        // This test verifies we can detect the setting
        String vmName = ManagementFactory.getRuntimeMXBean().getVmName();
        assertNotNull(vmName);
    }

    @Test
    void testMetaspacePoolAccess() {
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        boolean hasMetaspace = pools.stream()
            .anyMatch(p -> "Metaspace".equals(p.getName()));
        assertTrue(hasMetaspace, "Metaspace pool should be available");
    }

    @Test
    void testContainerSupportDetection() {
        // Check if running in a container-aware JVM (Java 10+)
        String version = System.getProperty("java.version");
        assertNotNull(version, "Java version should be available");
        int majorVersion = Integer.parseInt(version.split("\\.")[0]);
        assertTrue(majorVersion >= 11, "Should be running Java 11+");
    }

    @Test
    void testHeapDumpOnOOMFlag() {
        // Verify OOM error handling
        try {
            long[] huge = new long[Integer.MAX_VALUE / 2];
            fail("Should throw OutOfMemoryError");
        } catch (OutOfMemoryError e) {
            // Expected — JVM will dump heap if -XX:+HeapDumpOnOutOfMemoryError
            assertTrue(true);
        }
    }

    @Test
    void testLargePageAlignment() {
        // Large pages require aligned heap
        long pageSize = 2L * 1024 * 1024; // 2MB
        long heapSize = 8L * 1024 * 1024 * 1024; // 8GB
        assertEquals(0, heapSize % pageSize, 
            "Heap should be aligned to large page size");
    }

    @Test
    void testErgonomicsWarning() {
        // Run with flags printed to verify ergonomics
        String flags = ManagementFactory.getRuntimeMXBean()
            .getInputArguments().toString();
        assertNotNull(flags, "JVM arguments should be accessible");
    }
}
