package com.sd.scalability;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class RateLimiterAlgorithms {

    public static class FixedWindowRateLimiter {
        private final int maxRequests;
        private final long windowSizeMs;
        private final AtomicLong windowStart;
        private final AtomicInteger counter;

        public FixedWindowRateLimiter(int maxRequests, long windowSizeMs) {
            this.maxRequests = maxRequests;
            this.windowSizeMs = windowSizeMs;
            this.windowStart = new AtomicLong(System.currentTimeMillis());
            this.counter = new AtomicInteger(0);
        }

        public boolean tryAcquire() {
            long now = System.currentTimeMillis();
            long start = windowStart.get();
            if (now - start > windowSizeMs) {
                windowStart.set(now);
                counter.set(0);
            }
            return counter.incrementAndGet() <= maxRequests;
        }
    }

    public static class SlidingWindowLog {
        private final int maxRequests;
        private final long windowSizeMs;
        private final Deque<Long> timestamps = new ConcurrentLinkedDeque<>();

        public SlidingWindowLog(int maxRequests, long windowSizeMs) {
            this.maxRequests = maxRequests;
            this.windowSizeMs = windowSizeMs;
        }

        public synchronized boolean tryAcquire() {
            long now = System.currentTimeMillis();
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowSizeMs) {
                timestamps.pollFirst();
            }
            if (timestamps.size() < maxRequests) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Fixed Window ===");
        FixedWindowRateLimiter fixed = new FixedWindowRateLimiter(3, 1000);
        for (int i = 0; i < 5; i++) {
            System.out.println("  Request " + (i + 1) + ": " + (fixed.tryAcquire() ? "ALLOWED" : "DENIED"));
        }

        System.out.println("\n=== Sliding Window Log ===");
        SlidingWindowLog sliding = new SlidingWindowLog(3, 1000);
        for (int i = 0; i < 5; i++) {
            System.out.println("  Request " + (i + 1) + ": " + (sliding.tryAcquire() ? "ALLOWED" : "DENIED"));
        }
    }
}
