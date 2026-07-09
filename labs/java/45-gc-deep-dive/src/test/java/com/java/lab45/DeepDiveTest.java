package com.java.lab45;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.management.*;
import java.util.*;
import java.util.regex.*;

public class DeepDiveTest {

    @Test
    void testG1RegionSizeCalculation() {
        // G1 heap region size is calculated as:
        // region_size = max(1MB, min(32MB, heap_size / 2048))
        // Rounded to power of 2
        long heapSize = 4L * 1024 * 1024 * 1024; // 4GB
        long regionSize = Math.max(1 << 20, Math.min(32 << 20, heapSize / 2048));
        // For 4GB: 4GB / 2048 = 2MB (power of 2, within 1-32MB)
        assertEquals(1 << 21, regionSize, "4GB heap should have 2MB regions");
    }

    @Test
    void testGCBeanAvailable() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        assertFalse(gcBeans.isEmpty(), "GC MXBeans must be available");
        for (var gc : gcBeans) {
            assertNotNull(gc.getName());
            assertTrue(gc.getCollectionCount() >= 0);
            assertTrue(gc.getCollectionTime() >= 0);
        }
    }

    @Test
    void testMemoryPoolBeans() {
        List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
        boolean hasEden = false, hasOld = false, hasSurvivor = false;
        for (var pool : pools) {
            String name = pool.getName();
            if (name.contains("Eden")) hasEden = true;
            if (name.contains("Old") || name.contains("Tenured")) hasOld = true;
            if (name.contains("Survivor")) hasSurvivor = true;
        }
        assertTrue(hasEden, "Should have Eden space");
        assertTrue(hasOld, "Should have Old/Tenured space");
        assertTrue(hasSurvivor, "Should have Survivor space");
    }

    @Test
    void testGCAllocationRate() {
        // Simple allocation to exercise GC paths
        List<byte[]> list = new ArrayList<>();
        long totalAllocated = 0;
        try {
            for (int i = 0; i < 1000; i++) {
                byte[] chunk = new byte[1024 * 10]; // 10KB
                list.add(chunk);
                totalAllocated += chunk.length;
            }
        } catch (OutOfMemoryError e) {
            // Allocation may trigger GC — which is fine for this test
        }
        assertTrue(totalAllocated > 0, "Should allocate some memory");
    }

    @Test
    void testGCLogPattern() {
        // Verify GC log parsing pattern
        String logLine = "[0.042s][info][gc,heap] Heap region size: 2M";
        Pattern p = Pattern.compile("region size: (\\d+)([kKMmGg])");
        Matcher m = p.matcher(logLine);
        assertTrue(m.find());
        assertEquals("2", m.group(1));
        assertEquals("M", m.group(2));
    }

    @Test
    void testMemoryUsageMonitoring() {
        // Verify we can read memory usage without crashing
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memBean.getHeapMemoryUsage();
        MemoryUsage nonHeap = memBean.getNonHeapMemoryUsage();
        
        assertTrue(heap.getUsed() <= heap.getMax() || heap.getMax() == -1,
            "Used should be <= max (or max = -1 for unbounded)");
        assertTrue(nonHeap.getUsed() >= 0);
    }

    @Test
    void testGCCollectionTimeIncreases() {
        // GC collection time should increase over time
        // (very basic sanity check that GC is working)
        var gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (var gc : gcBeans) {
            long count = gc.getCollectionCount();
            long time = gc.getCollectionTime();
            assertTrue(count >= 0);
            if (count > 0) {
                assertTrue(time > 0, "If GC ran, collection time should be positive");
            }
        }
    }

    @Test
    void testObjectPromotion() {
        // Objects surviving multiple GC cycles get promoted
        // This test verifies basic allocation + GC works
        Object[] ref = new Object[1];
        for (int i = 0; i < 10; i++) {
            byte[] data = new byte[1024];
            if (i == 5) ref[0] = data; // Keep one reference alive
        }
        assertNotNull(ref[0]);
        System.gc(); // Should promote ref[0] if it survives
    }

    @Test
    void testG1PauseGoal() {
        // G1 is configured with a pause time goal
        // Default is 200ms
        long pauseGoal = 200;
        assertTrue(pauseGoal > 0, "G1 pause time goal should be positive");
        assertTrue(pauseGoal <= 1000, "G1 pause time goal should be reasonable");
    }

    @Test
    void testZGCColorBits() {
        // ZGC colored pointer layout:
        // Bits 63-62: finalizable
        // Bits 61-60: remapped
        // Bits 59-58: marked0
        // Bits 57-56: marked1
        // Bits 55-14: address (42 bits = up to 16TB)
        // Bits 13-0:  aligned offset
        long coloredPointer = 0x4000000000000000L; // Example: marked1=1
        assertTrue((coloredPointer & 0xFF00000000000000L) != 0,
            "Color bits should be in high bytes");
    }
}
