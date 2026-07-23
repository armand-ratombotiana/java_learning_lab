package com.prod.solutions.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Sliding window log rate limiter implementation.
 * Tracks request timestamps per client and rejects requests
 * that exceed the limit within the time window.
 *
 * More accurate than fixed window (avoids boundary spikes)
 * but uses more memory (stores all timestamps per window).
 */
public class SlidingWindowRateLimiter {

    static class SlidingWindow {
        private final long windowSizeMs;
        private final long maxRequests;
        private final long[] timestamps;
        private int head;
        private int count;

        SlidingWindow(long windowSizeMs, long maxRequests) {
            this.windowSizeMs = windowSizeMs;
            this.maxRequests = maxRequests;
            this.timestamps = new long[(int) maxRequests * 2];
            this.head = 0;
            this.count = 0;
        }

        synchronized boolean tryConsume() {
            long now = System.currentTimeMillis();
            long cutoff = now - windowSizeMs;

            // Remove expired timestamps
            while (count > 0 && timestamps[head] < cutoff) {
                head = (head + 1) % timestamps.length;
                count--;
            }

            if (count < maxRequests) {
                int idx = (head + count) % timestamps.length;
                timestamps[idx] = now;
                count++;
                return true;
            }

            return false;
        }

        synchronized int getCurrentCount() { return count; }
    }

    private final Map<String, SlidingWindow> windows = new ConcurrentHashMap<>();
    private final long windowSizeMs;
    private final long maxRequests;

    public SlidingWindowRateLimiter(long windowSizeMs, long maxRequests) {
        this.windowSizeMs = windowSizeMs;
        this.maxRequests = maxRequests;
    }

    public boolean allowRequest(String clientId) {
        SlidingWindow window = windows.computeIfAbsent(clientId,
                k -> new SlidingWindow(windowSizeMs, maxRequests));
        return window.tryConsume();
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Sliding Window Rate Limiter Demo ===\n");

        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(5000, 5); // 5 req per 5 sec

        String client = "client-001";

        // 8 rapid requests
        System.out.println("--- Sending 8 requests ---");
        for (int i = 1; i <= 8; i++) {
            boolean allowed = limiter.allowRequest(client);
            System.out.printf("  Request %d: %s%n", i, allowed ? "ALLOWED" : "BLOCKED");
            Thread.sleep(50);
        }

        // Wait for window to slide
        System.out.println("\n--- Waiting 3 seconds (window slides, oldest expire) ---");
        Thread.sleep(3000);

        System.out.println("--- 3 more requests ---");
        for (int i = 1; i <= 3; i++) {
            boolean allowed = limiter.allowRequest(client);
            System.out.printf("  Request %d: %s%n", i, allowed ? "ALLOWED" : "BLOCKED");
        }

        System.out.printf("%nSliding window advantages:%n");
        System.out.println("  - Avoids burst at window boundaries (fixed window problem)");
        System.out.println("  - Smooth rate limiting without spikes");
        System.out.println("  - More accurate than token bucket for strict limits");
    }
}
