package com.net.gateway;

import java.util.*;
import java.util.concurrent.*;

public class RateLimiter {

    public static class TokenBucket {
        private final long capacity;
        private final double refillRate;
        private double tokens;
        private long lastRefillTime;

        public TokenBucket(long capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity;
            this.lastRefillTime = System.nanoTime();
        }

        public synchronized boolean tryAcquire() {
            refill();
            if (tokens >= 1) {
                tokens -= 1;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefillTime) / 1_000_000_000.0;
            tokens = Math.min(capacity, tokens + elapsed * refillRate);
            lastRefillTime = now;
        }
    }

    public static class SlidingWindowRateLimiter {
        private final int maxRequests;
        private final long windowSizeMs;
        private final Map<String, Deque<Long>> requestLogs = new ConcurrentHashMap<>();

        public SlidingWindowRateLimiter(int maxRequests, long windowSizeMs) {
            this.maxRequests = maxRequests;
            this.windowSizeMs = windowSizeMs;
        }

        public boolean allowRequest(String clientId) {
            long now = System.currentTimeMillis();
            Deque<Long> log = requestLogs.computeIfAbsent(clientId, k -> new LinkedList<>());

            synchronized (log) {
                while (!log.isEmpty() && now - log.peekFirst() > windowSizeMs) {
                    log.pollFirst();
                }
                if (log.size() < maxRequests) {
                    log.addLast(now);
                    return true;
                }
                return false;
            }
        }

        public int getRemainingTokens(String clientId) {
            Deque<Long> log = requestLogs.get(clientId);
            if (log == null) return maxRequests;
            synchronized (log) {
                long now = System.currentTimeMillis();
                while (!log.isEmpty() && now - log.peekFirst() > windowSizeMs) {
                    log.pollFirst();
                }
                return maxRequests - log.size();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Token Bucket ===");
        TokenBucket bucket = new TokenBucket(5, 2);

        for (int i = 0; i < 10; i++) {
            boolean allowed = bucket.tryAcquire();
            System.out.println("Request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "RATE LIMITED"));
            Thread.sleep(100);
        }

        System.out.println("\n=== Sliding Window ===");
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(3, 1000);

        for (int i = 0; i < 5; i++) {
            String client = "client-" + (i % 2 + 1);
            boolean allowed = limiter.allowRequest(client);
            System.out.println(client + " request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "RATE LIMITED")
                + " (remaining: " + limiter.getRemainingTokens(client) + ")");
        }
    }
}
