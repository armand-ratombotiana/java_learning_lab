package com.prod.solutions.cachestampede;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates a cache stampede (thundering herd) where a hot key expires
 * and all concurrent requests try to recompute it simultaneously,
 * overwhelming the backend database.
 *
 * BUG: No coordination on cache miss. Every request recomputes the value.
 */
public class CacheStampedeSimulator {

    private static final AtomicInteger dbQueryCount = new AtomicInteger(0);
    private static String cachedValue = null;
    private static final long CACHE_TTL_MS = 300;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Cache Stampede (Thundering Herd) Demo ===\n");

        // Set initial cache
        cachedValue = computeExpensiveValue();

        // Simulate TTL expiry
        System.out.println("Cache TTL expired. Simulating concurrent requests...\n");

        int concurrentRequests = 20;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < concurrentRequests; i++) {
            final int reqId = i;
            executor.submit(() -> {
                try {
                    latch.await(); // All threads start simultaneously
                    getValueWithStampede(reqId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown(); // Release all threads at once
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.printf("%nTotal database queries: %d (expected 1 with coordination)%n",
                dbQueryCount.get());
        System.out.printf("Without stampede protection: %d concurrent DB calls!%n",
                dbQueryCount.get());

        if (dbQueryCount.get() > 10) {
            System.out.println("\n!!! CACHE STAMPEDE DETECTED !!!");
            System.out.println("The database was overwhelmed by concurrent recomputations.");
        }
    }

    static String getValueWithStampede(int reqId) throws InterruptedException {
        if (cachedValue != null) {
            return cachedValue; // Cache hit
        }

        // BUG: No coordination — every request recomputes
        int count = dbQueryCount.incrementAndGet();
        System.out.printf("[Request %d] Cache miss! Querying DB (#%d)%n", reqId, count);

        String value = computeExpensiveValue();
        cachedValue = value;
        Thread.sleep(500);
        return value;
    }

    static String computeExpensiveValue() {
        // Simulate expensive database query (500ms)
        try { Thread.sleep(500); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "expensive_value_" + System.currentTimeMillis();
    }
}
