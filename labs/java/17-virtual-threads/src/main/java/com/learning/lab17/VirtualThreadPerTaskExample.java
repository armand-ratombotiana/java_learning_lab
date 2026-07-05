package com.learning.lab17;

import java.util.concurrent.*;

/**
 * Demonstrates Executors.newVirtualThreadPerTaskExecutor() for running tasks on virtual threads.
 */
public class VirtualThreadPerTaskExample {

    public static void showNewVirtualThreadPerTask() throws InterruptedException {
        System.out.println("=== newVirtualThreadPerTaskExecutor() ===");

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = java.util.stream.IntStream.range(0, 10)
                .mapToObj(i -> executor.submit(() -> {
                    String name = Thread.currentThread().getName();
                    boolean isVirtual = Thread.currentThread().isVirtual();
                    System.out.println("  Task " + i + " on " + name + " (virtual=" + isVirtual + ")");
                    return i;
                }))
                .toList();

            long sum = futures.stream()
                .mapToInt(f -> {
                    try { return f.get(); } catch (Exception e) { return 0; }
                })
                .sum();
            System.out.println("Sum of task results: " + sum);
        }
    }
}
