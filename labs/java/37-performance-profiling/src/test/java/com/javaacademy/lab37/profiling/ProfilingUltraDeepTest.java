package com.javaacademy.lab37.profiling;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.management.*;

class ProfilingUltraDeepTest {

    @Test
    void threadMXBeanCurrentThread() {
        var threadMXBean = ManagementFactory.getThreadMXBean();
        long id = Thread.currentThread().getId();
        var info = threadMXBean.getThreadInfo(id);
        assertNotNull(info);
        assertEquals(Thread.currentThread().getName(), info.getThreadName());
    }

    @Test
    void memoryMXBeanHeapUsage() {
        var memoryMXBean = ManagementFactory.getMemoryMXBean();
        var heap = memoryMXBean.getHeapMemoryUsage();
        assertTrue(heap.getInit() > 0);
        assertTrue(heap.getMax() > 0);
    }

    @Test
    void operatingSystemMXBean() {
        var os = ManagementFactory.getOperatingSystemMXBean();
        assertNotNull(os.getName());
        assertNotNull(os.getArch());
        assertTrue(os.getAvailableProcessors() > 0);
    }

    @Test
    void runtimeMXBeanInputArguments() {
        var runtime = ManagementFactory.getRuntimeMXBean();
        assertNotNull(runtime.getVmName());
        assertNotNull(runtime.getVmVersion());
    }

    @Test
    void gcMXBeanCollectorCount() {
        var gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (var gc : gcBeans) {
            assertNotNull(gc.getName());
        }
    }
}
