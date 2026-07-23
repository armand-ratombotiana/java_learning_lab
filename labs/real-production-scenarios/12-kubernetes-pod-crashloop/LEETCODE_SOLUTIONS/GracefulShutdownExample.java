package com.prod.solutions.kubernetes;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Demonstrates graceful shutdown in Java — handling SIGTERM,
 * draining active requests, and cleaning up resources before
 * the process exits.
 *
 * Without graceful shutdown: in-flight requests are interrupted,
 * connections are cut, and data may be lost.
 */
public class GracefulShutdownExample {

    static class GracefulShutdown {
        private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
        private final ExecutorService requestPool = Executors.newFixedThreadPool(4);
        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        private volatile int activeRequests = 0;
        private static final long SHUTDOWN_TIMEOUT_MS = 5000;

        void start() {
            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

            System.out.println("Service started. Press Ctrl+C to simulate SIGTERM.\n");
        }

        boolean handleRequest(int reqId) {
            if (shuttingDown.get()) {
                System.out.printf("[Request %d] REJECTED: Service shutting down%n", reqId);
                return false;
            }

            activeRequests++;
            requestPool.submit(() -> {
                try {
                    System.out.printf("[Request %d] Processing... (active: %d)%n",
                            reqId, activeRequests);
                    Thread.sleep(2000); // Simulate work
                    System.out.printf("[Request %d] Completed%n", reqId);
                } catch (InterruptedException e) {
                    System.out.printf("[Request %d] Interrupted during shutdown%n", reqId);
                    Thread.currentThread().interrupt();
                } finally {
                    activeRequests--;
                }
            });
            return true;
        }

        void shutdown() {
            System.out.println("\n=== SIGTERM received. Initiating graceful shutdown... ===");

            shuttingDown.set(true);

            // Step 1: Stop accepting new requests
            System.out.println("Step 1: Stopped accepting new requests");

            // Step 2: Wait for active requests to complete (with timeout)
            System.out.println("Step 2: Draining " + activeRequests + " active requests...");
            requestPool.shutdown();
            try {
                if (!requestPool.awaitTermination(SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                    System.out.println("Warning: " + activeRequests + " requests still active after timeout");
                    requestPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                requestPool.shutdownNow();
                Thread.currentThread().interrupt();
            }

            // Step 3: Clean up resources
            System.out.println("Step 3: Closing connections...");
            scheduler.shutdown();

            // Step 4: Final cleanup
            System.out.println("Step 4: Releasing resources...");
            try { Thread.sleep(500); } catch (InterruptedException e) {}

            System.out.println("=== Graceful shutdown complete ===");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Graceful Shutdown Demo ===\n");

        GracefulShutdown service = new GracefulShutdown();
        service.start();

        // Submit a few requests
        for (int i = 1; i <= 5; i++) {
            service.handleRequest(i);
            Thread.sleep(100);
        }

        // Simulate shutdown after a short delay
        Thread.sleep(500);
        service.shutdown();

        // Attempt to send request after shutdown
        System.out.println("\n--- Attempting request after shutdown ---");
        service.handleRequest(99);

        System.out.printf("%nGraceful shutdown ensures:%n");
        System.out.println("  - In-flight requests complete (up to timeout)");
        System.out.println("  - Connections are cleanly closed");
        System.out.println("  - No data loss or corruption");
        System.out.println("  - K8s respects terminationGracePeriodSeconds");
    }
}
