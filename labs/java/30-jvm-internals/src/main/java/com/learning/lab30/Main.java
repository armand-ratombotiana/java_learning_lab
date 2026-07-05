package com.learning.lab30;

/**
 * Main entry point for Lab 30: JVM Internals.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   Lab 30: JVM Internals");
        System.out.println("========================================\n");

        MemoryUsageExample.showMemoryUsage();
        System.out.println();
        ClassLoadingExample.showClassLoading();
        System.out.println();
        ThreadDumpHintExample.showThreadInfo();
        System.out.println();
        JVMDiagnosticsExample.showJVMDiagnostics();

        System.out.println("\n========================================");
        System.out.println("   Lab 30 Complete");
        System.out.println("========================================");
    }
}
