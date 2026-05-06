package com.learning.memory;

public class MemoryProfilingTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 47: Memory Profiling ===");
        demonstrateMemory();
    }

    private static void demonstrateMemory() {
        System.out.println("\n--- JVM Memory ---");
        System.out.println("Heap: Young Gen (Eden, S0, S1), Old Gen");
        System.out.println("Metaspace: Class metadata");
        System.out.println("Stack: Per-thread, method execution");
        System.out.println("\nTools: VisualVM, JConsole, MAT, async-profiler");
    }
}