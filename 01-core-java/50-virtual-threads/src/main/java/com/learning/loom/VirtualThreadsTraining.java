package com.learning.loom;

public class VirtualThreadsTraining {
    public static void main(String[] args) {
        System.out.println("=== Module 50: Virtual Threads (Project Loom) ===");
        demonstrateVirtualThreads();
    }

    private static void demonstrateVirtualThreads() {
        System.out.println("\n--- Virtual Threads (Java 21+) ---");
        System.out.println("- Lightweight threads, millions per application");
        System.out.println("- Platform threads: 1:1 mapping to OS threads");
        System.out.println("- Virtual threads: M:N mapping, thread-per-request");
        System.out.println("- No changes to existing code needed");
        System.out.println("\n--- Structured Concurrency ---");
        System.out.println("- Thread scope: automatic cleanup");
        System.out.println("- Scoped values: thread-local alternative");
        System.out.println("- Cancel child tasks with parent");
    }
}