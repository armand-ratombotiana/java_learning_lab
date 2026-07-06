package com.javaacademy.lab52.performanceantipatterns;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Recreates a ThreadLocal leak pattern commonly found in application
 * servers. When thread pools reuse threads, ThreadLocal values set by
 * a request can leak to subsequent requests if not cleaned up.
 * This is especially problematic with custom ClassLoaders (e.g., in
 * application servers with hot-deploy).
 */
public class ThreadLocalLeakDemo {

    // Simulating a "heavy" per-request context object
    static class RequestContext {
        final byte[] data = new byte[1024 * 100]; // 100KB per context
        final String requestId;

        RequestContext(String requestId) { this.requestId = requestId; }
    }

    private static final ThreadLocal<RequestContext> contextHolder = new ThreadLocal<>();
    private static final int TASKS = 100;

    public static void main(String[] args) throws Exception {
        System.out.println("=== ThreadLocal Leak Demo ===");
        System.out.println("Each context holds 100KB. With " + TASKS + " tasks on fixed pool,");
        System.out.println("ThreadLocals persist even after task completes.");

        // Fixed thread pool (small) — threads get reused
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < TASKS; i++) {
                final String reqId = "req-" + i;
                pool.submit(() -> {
                    // Set context (simulates framework code)
                    contextHolder.set(new RequestContext(reqId));
                    // Do work...
                    String id = contextHolder.get().requestId;
                    // BUG: contextHolder is never removed!
                    // ThreadLocal values persist in thread-local map
                    System.out.println("Processing " + id);
                });
            }
        }

        // To fix: add a try-finally with contextHolder.remove() in each task
        System.out.println("Fix: use try-finally { contextHolder.remove(); }");
        System.out.println("ThreadLocal leak demo complete.");
    }
}
