package com.javaacademy.lab37.profiling;

import javax.management.*;
import java.lang.management.*;

public class JmxMonitoring {

    private final MBeanServer mbs;
    private final MemoryMXBean memoryMXBean;
    private final ThreadMXBean threadMXBean;
    private final OperatingSystemMXBean osMXBean;

    public JmxMonitoring() {
        mbs = ManagementFactory.getPlatformMBeanServer();
        memoryMXBean = ManagementFactory.getMemoryMXBean();
        threadMXBean = ManagementFactory.getThreadMXBean();
        osMXBean = ManagementFactory.getOperatingSystemMXBean();
    }

    public long getHeapMemoryUsage() {
        return memoryMXBean.getHeapMemoryUsage().getUsed();
    }

    public long getMaxHeapMemory() {
        return memoryMXBean.getHeapMemoryUsage().getMax();
    }

    public int getHeapUsagePercent() {
        MemoryUsage heap = memoryMXBean.getHeapMemoryUsage();
        return (int) (heap.getUsed() * 100 / heap.getMax());
    }

    public int getThreadCount() {
        return threadMXBean.getThreadCount();
    }

    public long getPeakThreadCount() {
        return threadMXBean.getPeakThreadCount();
    }

    public double getSystemLoadAverage() {
        return osMXBean.getSystemLoadAverage();
    }

    public int getAvailableProcessors() {
        return osMXBean.getAvailableProcessors();
    }

    public long getTotalCompilationTime() {
        return ManagementFactory.getCompilationMXBean().getTotalCompilationTime();
    }

    public long getGcCount() {
        long count = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            count += gc.getCollectionCount();
        }
        return count;
    }

    public long getGcTime() {
        long time = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            time += gc.getCollectionTime();
        }
        return time;
    }

    public void printDiagnostics() {
        System.out.println("=== JMX Diagnostics ===");
        System.out.println("Heap: " + (getHeapMemoryUsage() / 1024 / 1024) + "MB / " + (getMaxHeapMemory() / 1024 / 1024) + "MB");
        System.out.println("Threads: " + getThreadCount() + " (peak: " + getPeakThreadCount() + ")");
        System.out.println("CPU Cores: " + getAvailableProcessors());
        System.out.println("Load Avg: " + getSystemLoadAverage());
        System.out.println("GC Count: " + getGcCount() + ", GC Time: " + getGcTime() + "ms");
    }
}
