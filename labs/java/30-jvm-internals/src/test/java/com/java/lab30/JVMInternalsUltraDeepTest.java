package com.java.lab30;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.management.*;

class JVMInternalsUltraDeepTest {

    @Test
    void runtimeAvailableProcessors() {
        int processors = Runtime.getRuntime().availableProcessors();
        assertTrue(processors >= 1);
    }

    @Test
    void runtimeMaxMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        assertTrue(maxMemory > 0);
    }

    @Test
    void classLoaderHierarchy() {
        ClassLoader cl = getClass().getClassLoader();
        assertNotNull(cl);
        ClassLoader parent = cl.getParent();
        assertNotNull(parent);
    }

    @Test
    void systemPropertiesNonEmpty() {
        var props = System.getProperties();
        assertFalse(props.isEmpty());
    }

    @Test
    void managementBeansExist() {
        var memoryMXBean = ManagementFactory.getMemoryMXBean();
        assertNotNull(memoryMXBean);
        assertNotNull(memoryMXBean.getHeapMemoryUsage());
    }
}
