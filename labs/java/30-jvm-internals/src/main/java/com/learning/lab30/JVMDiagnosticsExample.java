package com.learning.lab30;

import java.lang.management.*;

/**
 * Demonstrates JVM diagnostic information using ManagementFactory beans.
 */
public class JVMDiagnosticsExample {

    public static void showJVMDiagnostics() {
        System.out.println("=== JVM Diagnostics ===");

        RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
        System.out.println("JVM name: " + runtimeBean.getVmName());
        System.out.println("JVM vendor: " + runtimeBean.getVmVendor());
        System.out.println("JVM version: " + runtimeBean.getVmVersion());
        System.out.println("Uptime: " + runtimeBean.getUptime() + "ms");
        System.out.println("Start time: " + runtimeBean.getStartTime());
        System.out.println("Input arguments: " + runtimeBean.getInputArguments());

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("OS: " + osBean.getName() + " " + osBean.getVersion());
        System.out.println("Arch: " + osBean.getArch());
        System.out.println("Available processors: " + osBean.getAvailableProcessors());
        System.out.println("System load avg: " + osBean.getSystemLoadAverage());

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = memoryBean.getHeapMemoryUsage();
        System.out.println("Heap init: " + heap.getInit() / (1024*1024) + "MB");
        System.out.println("Heap used: " + heap.getUsed() / (1024*1024) + "MB");
        System.out.println("Heap max: " + heap.getMax() / (1024*1024) + "MB");
    }
}
