package com.javaacademy.lab46.jvm;

import java.lang.management.ManagementFactory;
import java.util.*;

/**
 * Reads and prints all -XX JVM flags via ManagementFactory
 * and RuntimeMXBean input arguments.
 */
public class JvmFlagReporter {

    public static void main(String[] args) {
        System.out.println("=== JVM Flag Reporter ===\n");

        // JVM input arguments
        var runtimeMx = ManagementFactory.getRuntimeMXBean();
        System.out.println("JVM Input Arguments:");
        for (String arg : runtimeMx.getInputArguments()) {
            if (arg.startsWith("-XX:")) {
                System.out.println("  " + arg);
            }
        }

        // JVM specifications
        System.out.println("\nJVM Info:");
        System.out.println("  Name: " + runtimeMx.getVmName());
        System.out.println("  Version: " + runtimeMx.getVmVersion());
        System.out.println("  Vendor: " + runtimeMx.getVmVendor());

        // Memory info
        var memMx = ManagementFactory.getMemoryMXBean();
        var heap = memMx.getHeapMemoryUsage();
        var nonHeap = memMx.getNonHeapMemoryUsage();
        System.out.println("\nMemory:");
        System.out.println("  Heap: " + heap.getInit() / 1024 / 1024 + "/" + heap.getMax() / 1024 / 1024 + " MB");
        System.out.println("  Non-Heap: " + nonHeap.getInit() / 1024 / 1024 + "/" + nonHeap.getMax() / 1024 / 1024 + " MB");

        // OS info
        var osMx = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("\nOS:");
        System.out.println("  Name: " + osMx.getName());
        System.out.println("  Arch: " + osMx.getArch());
        System.out.println("  Cores: " + osMx.getAvailableProcessors());

        // List all available system properties
        System.out.println("\nKey System Properties:");
        Properties props = System.getProperties();
        for (String key : List.of("java.version", "java.vm.name", "java.vm.version",
            "java.class.path", "sun.arch.data.model", "os.arch")) {
            System.out.println("  " + key + " = " + props.getProperty(key));
        }
    }
}
