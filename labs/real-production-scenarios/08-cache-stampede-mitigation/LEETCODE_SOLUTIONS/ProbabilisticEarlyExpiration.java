package com.prod.solutions.cachestampede;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implements Probabilistic Early Expiration (XFetch algorithm) to
 * prevent cache stampede by proactively refreshing cache entries
 * before they expire, based on the probability of concurrent access.
 *
 * Instead of all keys expiring at the same TTL, each key gets a
 * randomized early refresh window proportional to the expected
 * request rate.
 */
public class ProbabilisticEarlyExpiration {

    static class CacheEntry {
        final String value;
        final long createdAt;
        final long ttlMs;

        CacheEntry(String value, long ttlMs) {
            this.value = value;
            this.createdAt = System.currentTimeMillis();
            this.ttlMs = ttlMs;
        }

        boolean isExpired() {
            return System.currentTimeMillis() - createdAt >= ttlMs;
        }

        long ageMs() {
            return System.currentTimeMillis() - createdAt;
        }

        /**
         * XFetch probabilistic early expiration.
         * Returns true if this entry should be proactively refreshed,
         * with probability increasing as the entry approaches its TTL.
         */
        boolean shouldProbabilisticallyRefresh(int requestCount, long computeTimeMs) {
            long age = ageMs();
            if (age >= ttlMs) return true; // Already expired

            float beta = 1.0f;
            float ratio = (float) age / ttlMs;
            float delta = beta * (float) computeTimeMs / ttlMs;

            // Probability increases as the entry ages
            float probability = (float) Math.pow(ratio, delta) * requestCount;
            float clampedProbability = Math.min(probability, 1.0f);

            return ThreadLocalRandom.current().nextFloat() < clampedProbability;
        }
    }

    private static final AtomicInteger refreshCount = new AtomicInteger(0);
    private static final AtomicInteger dbQueryCount = new AtomicInteger(0);
    private static CacheEntry currentEntry;
    private static int requestCounter = 0;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Probabilistic Early Expiration (XFetch) Demo ===\n");

        currentEntry = new CacheEntry("initial_value", 2000);

        // Simulate requests over time
        for (int i = 0; i < 50; i++) {
            Thread.sleep(100);
            requestCounter++;
            String value = getValueWithPEE(requestCounter);
            System.out.printf("[%d] age=%dms, expired=%b, refreshes=%d, dbQueries=%d, value=%s%n",
                    requestCounter, currentEntry.ageMs(), currentEntry.isExpired(),
                    refreshCount.get(), dbQueryCount.get(), value);
        }

        System.out.printf("%nTotal DB queries: %d (vs. ~50 without PEE)%n", dbQueryCount.get());
        System.out.printf("Proactive refreshes: %d%n", refreshCount.get());
        System.out.println("\nXFetch spreads cache refreshes across the TTL window,");
        System.out.println("preventing the thundering herd at expiry time.");
    }

    static String getValueWithPEE(int requestCount) throws InterruptedException {
        boolean needsRefresh = currentEntry.shouldProbabilisticallyRefresh(
                requestCount, 200); // compute time ~200ms

        if (currentEntry.isExpired() || needsRefresh) {
            refreshCount.incrementAndGet();
            dbQueryCount.incrementAndGet();
            currentEntry = new CacheEntry("refreshed_" + System.currentTimeMillis(), 2000);
        }

        return currentEntry.value;
    }
}
