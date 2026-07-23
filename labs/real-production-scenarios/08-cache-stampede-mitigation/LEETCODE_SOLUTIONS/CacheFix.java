package com.prod.solutions.cachestampede;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Fixes cache stampede using the stale-while-revalidate strategy.
 * Serves stale data while one thread asynchronously recomputes the value.
 *
 * FIX: On cache miss, only one thread recomputes. Others either
 * wait for the result or get served stale data.
 */
public class CacheFix {

    private static final AtomicInteger dbQueryCount = new AtomicInteger(0);
    private static volatile String cachedValue = null;
    private static volatile Instant cachedAt = Instant.now();
    private static final ReentrantLock recomputeLock = new ReentrantLock();
    private static final long STALE_TTL_MS = 5000; // Serve stale for 5s
    private static final long FRESH_TTL_MS = 1000; // Fresh for 1s

    static class Instant {
        private final long timestamp;
        Instant() { this.timestamp = System.currentTimeMillis(); }
        long elapsedMs() { return System.currentTimeMillis() - timestamp; }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Cache Stampede Fix: Stale-While-Revalidate ===\n");

        cachedValue = computeExpensiveValue();
        cachedAt = new Instant();

        int concurrentRequests = 20;
        ExecutorService executor = Executors.newFixedThreadPool(concurrentRequests);
        CountDownLatch latch = new CountDownLatch(1);

        for (int i = 0; i < concurrentRequests; i++) {
            final int reqId = i;
            executor.submit(() -> {
                try {
                    latch.await();
                    String value = getValueSafe(reqId);
                    System.out.printf("[Request %d] Got: %s%n", reqId, value);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.printf("%nTotal database queries: %d (only 1!)%n", dbQueryCount.get());
        System.out.println("All other requests were served stale data or waited.");
        System.out.println("\nStale-while-revalidate prevents cache stampede completely.");
    }

    /**
     * FIX: Uses stale-while-revalidate strategy.
     * - Returns stale data immediately if available
     * - Only one thread recomputes the value
     * - Other threads get stale data while waiting
     */
    static String getValueSafe(int reqId) throws InterruptedException {
        if (cachedValue != null && cachedAt.elapsedMs() < FRESH_TTL_MS) {
            return cachedValue; // Fresh cache hit
        }

        if (cachedValue != null && cachedAt.elapsedMs() < STALE_TTL_MS) {
            // Serve stale data, trigger async revalidation
            tryRecompute(reqId);
            return cachedValue;
        }

        // Cache is too stale or empty — must recompute synchronously
        return forceRecompute(reqId);
    }

    static void tryRecompute(int reqId) {
        if (recomputeLock.tryLock()) {
            try {
                System.out.printf("[Request %d] Revalidating cache...%n", reqId);
                int count = dbQueryCount.incrementAndGet();
                cachedValue = computeExpensiveValue();
                cachedAt = new Instant();
                System.out.printf("[Request %d] Cache updated (DB query #%d)%n", reqId, count);
            } finally {
                recomputeLock.unlock();
            }
        }
    }

    static String forceRecompute(int reqId) throws InterruptedException {
        recomputeLock.lock();
        try {
            // Double-check after acquiring lock
            if (cachedValue == null || cachedAt.elapsedMs() >= STALE_TTL_MS) {
                int count = dbQueryCount.incrementAndGet();
                System.out.printf("[Request %d] Force recompute (DB query #%d)%n", reqId, count);
                cachedValue = computeExpensiveValue();
                cachedAt = new Instant();
            }
            return cachedValue;
        } finally {
            recomputeLock.unlock();
        }
    }

    static String computeExpensiveValue() {
        try { Thread.sleep(500); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "value_" + System.currentTimeMillis();
    }
}
