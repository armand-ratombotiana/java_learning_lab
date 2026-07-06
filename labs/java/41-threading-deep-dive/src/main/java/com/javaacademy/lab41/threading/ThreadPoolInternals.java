package com.javaacademy.lab41.threading;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates ThreadPoolExecutor internals: core/max pool size, work queue types,
 * keep-alive time, and custom rejection handlers.
 */
public class ThreadPoolInternals {

    public static void main(String[] args) {
        System.out.println("=== ThreadPoolExecutor Internals ===\n");

        // Custom thread factory
        AtomicInteger count = new AtomicInteger(0);
        ThreadFactory factory = r -> {
            Thread t = new Thread(r, "pool-worker-" + count.incrementAndGet());
            t.setDaemon(true);
            return t;
        };

        // Rejection handler
        RejectedExecutionHandler rejection = (r, executor) ->
            System.out.println("[REJECTED] " + r + " rejected by " + executor);

        // ThreadPoolExecutor with ArrayBlockingQueue
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,                      // corePoolSize
            4,                      // maximumPoolSize
            1,                      // keepAliveTime
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2),  // bounded queue
            factory,
            new ThreadPoolExecutor.AbortPolicy()
        );

        executor.setRejectedExecutionHandler(rejection);
        executor.allowCoreThreadTimeOut(true);

        // Submit 10 tasks to demonstrate queueing and new thread creation
        for (int i = 0; i < 10; i++) {
            int taskId = i;
            try {
                executor.submit(() -> {
                    System.out.printf("Task %d running on %s (pool size: %d)%n",
                        taskId, Thread.currentThread().getName(), executor.getPoolSize());
                    try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                });
            } catch (RejectedExecutionException e) {
                System.out.println("Task " + taskId + " rejected: queue full");
            }
        }

        executor.shutdown();
        try { executor.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        System.out.println("\nFinal pool metrics:");
        System.out.println("  Largest pool size: " + executor.getLargestPoolSize());
        System.out.println("  Completed tasks: " + executor.getCompletedTaskCount());
    }
}
