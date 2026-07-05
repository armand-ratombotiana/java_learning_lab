package com.learning.lab17;

/**
 * Main entry point for Lab 17: Virtual Threads.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   Lab 17: Virtual Threads");
        System.out.println("========================================\n");

        VirtualThreadExample.showVirtualThreads();
        System.out.println();
        VirtualThreadPerTaskExample.showNewVirtualThreadPerTask();
        System.out.println();
        StructuredConcurrencyExample.showStructuredConcurrency();

        System.out.println("\n========================================");
        System.out.println("   Lab 17 Complete");
        System.out.println("========================================");
    }
}
