package com.javaacademy.lab48.structuredconcurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Compares unstructured vs structured concurrency error propagation.
 * Unstructured: errors in subtasks may be silently swallowed,
 * threads leak, cancellations are ad-hoc.
 * Structured: errors propagate cleanly, scope ensures cleanup.
 */
public class UnstructuredVsStructured {

    public static void main(String[] args) throws Exception {
        System.out.println("=== Unstructured (error swallowed) ===");
        unstructuredError();
        System.out.println("\n=== Structured (error propagated) ===");
        structuredError();
    }

    static void unstructuredError() throws Exception {
        AtomicInteger successCount = new AtomicInteger();
        List<Future<Integer>> futures = new ArrayList<>();
        var pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            int taskId = i;
            futures.add(pool.submit(() -> {
                if (taskId == 2) throw new RuntimeException("Task " + taskId + " failed");
                Thread.sleep(100);
                successCount.incrementAndGet();
                return taskId;
            }));
        }
        // Error from task 2 is hidden — we have to check each future
        for (var f : futures) {
            try { f.get(); } catch (Exception e) { System.out.println("Caught: " + e.getCause().getMessage()); }
        }
        System.out.println("Success count: " + successCount.get());
        pool.shutdown();
    }

    static void structuredError() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            for (int i = 0; i < 5; i++) {
                int taskId = i;
                scope.fork(() -> {
                    if (taskId == 2) throw new RuntimeException("Task " + taskId + " failed");
                    Thread.sleep(100);
                    return taskId;
                });
            }
            scope.join();
            // scope.throwIfFailed will propagate the first failure
            try {
                scope.throwIfFailed();
            } catch (Exception e) {
                System.out.println("Structured scope propagated: " + e.getMessage());
            }
        }
    }
}
