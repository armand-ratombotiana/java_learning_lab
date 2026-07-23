package com.prod.solutions.circuitbreaker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates the bulkhead pattern: isolating thread pools for each
 * downstream dependency so a failure in one doesn't exhaust threads
 * for all others.
 *
 * Inspired by Netflix Hystrix & Resilience4j bulkhead implementations.
 */
public class BulkheadPattern {

    static class Bulkhead {
        private final String name;
        private final int maxConcurrentCalls;
        private final AtomicInteger activeCalls = new AtomicInteger(0);
        private final AtomicInteger rejectedCalls = new AtomicInteger(0);

        public Bulkhead(String name, int maxConcurrentCalls) {
            this.name = name;
            this.maxConcurrentCalls = maxConcurrentCalls;
        }

        boolean tryAcquire() {
            while (true) {
                int current = activeCalls.get();
                if (current >= maxConcurrentCalls) {
                    rejectedCalls.incrementAndGet();
                    return false;
                }
                if (activeCalls.compareAndSet(current, current + 1)) {
                    return true;
                }
            }
        }

        void release() {
            activeCalls.decrementAndGet();
        }

        void printStats() {
            System.out.printf("  Bulkhead '%s': active=%d/%d, rejected=%d%n",
                    name, activeCalls.get(), maxConcurrentCalls, rejectedCalls.get());
        }
    }

    static class DownstreamService {
        private final String name;
        private final long simulatedDelay;
        private final boolean shouldFail;

        DownstreamService(String name, long delay, boolean shouldFail) {
            this.name = name;
            this.simulatedDelay = delay;
            this.shouldFail = shouldFail;
        }

        void call() throws Exception {
            Thread.sleep(simulatedDelay);
            if (shouldFail) throw new Exception(name + " failed!");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Bulkhead Pattern Demo ===\n");

        Bulkhead paymentBulkhead = new Bulkhead("payment-service", 3);
        Bulkhead inventoryBulkhead = new Bulkhead("inventory-service", 5);
        Bulkhead notificationBulkhead = new Bulkhead("notification-service", 2);

        DownstreamService paymentService = new DownstreamService("payment", 200, false);
        DownstreamService inventoryService = new DownstreamService("inventory", 50, false);
        DownstreamService notificationService = new DownstreamService("notification", 100, false);

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Send mixed traffic
        for (int i = 0; i < 20; i++) {
            final int reqId = i;
            executor.submit(() -> {
                try {
                    processRequest(reqId, paymentBulkhead, paymentService,
                            inventoryBulkhead, inventoryService,
                            notificationBulkhead, notificationService);
                } catch (Exception e) {
                    System.out.printf("[Request %d] %s%n", reqId, e.getMessage());
                }
            });
            Thread.sleep(30);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("\n--- Bulkhead Statistics ---");
        paymentBulkhead.printStats();
        inventoryBulkhead.printStats();
        notificationBulkhead.printStats();

        System.out.println("\nWithout bulkheads: a failure in one service blocks ALL threads.");
        System.out.println("With bulkheads:    services are isolated, failures contained.");
    }

    static void processRequest(int reqId, Bulkhead paymentBulkhead,
                                DownstreamService payment,
                                Bulkhead inventoryBulkhead,
                                DownstreamService inventory,
                                Bulkhead notificationBulkhead,
                                DownstreamService notification) throws Exception {

        if (paymentBulkhead.tryAcquire()) {
            try { payment.call(); }
            finally { paymentBulkhead.release(); }
        }

        if (inventoryBulkhead.tryAcquire()) {
            try { inventory.call(); }
            finally { inventoryBulkhead.release(); }
        }

        if (notificationBulkhead.tryAcquire()) {
            try { notification.call(); }
            finally { notificationBulkhead.release(); }
        }
    }
}
