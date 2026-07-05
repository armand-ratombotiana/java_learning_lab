package com.learning.lab17;

import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Demonstrates virtual thread performance vs platform threads for I/O-bound tasks.
 */
public class VirtualThreadPerformanceExample {

    public static void showPerformance() throws InterruptedException {
        System.out.println("=== Virtual Thread Performance ===");

        int taskCount = 100;

        long platformTime = measureExecution(() -> {
            try (var executor = Executors.newFixedThreadPool(10)) {
                submitTasks(executor, taskCount);
            }
        });
        System.out.println("Platform threads (pool=10): " + platformTime + "ms");

        long virtualTime = measureExecution(() -> {
            try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
                submitTasks(executor, taskCount);
            }
        });
        System.out.println("Virtual threads: " + virtualTime + "ms");
        System.out.println("Virtual threads are faster for I/O-bound tasks");
    }

    static void submitTasks(ExecutorService executor, int count) {
        var futures = IntStream.range(0, count)
            .mapToObj(i -> executor.submit(() -> {
                try { Thread.sleep(10); } catch (InterruptedException e) { }
                return i;
            }))
            .toList();
        futures.forEach(f -> { try { f.get(); } catch (Exception e) { } });
    }

    static long measureExecution(Runnable task) {
        long start = System.nanoTime();
        task.run();
        return (System.nanoTime() - start) / 1_000_000;
    }
}
