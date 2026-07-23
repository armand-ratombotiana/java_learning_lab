package com.prod.solutions.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements a token bucket rate limiter for API endpoint protection.
 * Prevents brute force attacks, DDoS, and API abuse by limiting
 * requests per client within a time window.
 *
 * Production: Redis-based distributed rate limiter (Sliding Window
 * or Token Bucket), enforced at API gateway level.
 */
public class RateLimiterExample {

    static class TokenBucket {
        private final long capacity;
        private final long refillRatePerSecond;
        private long tokens;
        private long lastRefillTimestamp;

        TokenBucket(long capacity, long refillRatePerSecond) {
            this.capacity = capacity;
            this.refillRatePerSecond = refillRatePerSecond;
            this.tokens = capacity;
            this.lastRefillTimestamp = System.currentTimeMillis();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsedSeconds = (now - lastRefillTimestamp) / 1000;
            if (elapsedSeconds > 0) {
                tokens = Math.min(capacity, tokens + elapsedSeconds * refillRatePerSecond);
                lastRefillTimestamp = now;
            }
        }

        synchronized long getTokens() { return tokens; }
    }

    static class RateLimiter {
        private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
        private final long defaultCapacity;
        private final long defaultRefillRate;

        RateLimiter(long defaultCapacity, long defaultRefillRate) {
            this.defaultCapacity = defaultCapacity;
            this.defaultRefillRate = defaultRefillRate;
        }

        boolean allowRequest(String clientId) {
            TokenBucket bucket = buckets.computeIfAbsent(clientId,
                    k -> new TokenBucket(defaultCapacity, defaultRefillRate));
            return bucket.tryConsume();
        }

        long getRemainingTokens(String clientId) {
            TokenBucket bucket = buckets.get(clientId);
            return bucket != null ? bucket.getTokens() : defaultCapacity;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== API Rate Limiter Demo ===\n");

        RateLimiter limiter = new RateLimiter(5, 1); // 5 burst, 1/sec refill

        String client = "client-001";

        // Burst of requests
        System.out.println("--- Sending 10 requests in quick succession ---");
        for (int i = 1; i <= 10; i++) {
            boolean allowed = limiter.allowRequest(client);
            long remaining = limiter.getRemainingTokens(client);
            System.out.printf("  Request %d: %s (remaining: %d)%n",
                    i, allowed ? "ALLOWED" : "BLOCKED", remaining);
        }

        // Wait for refill
        System.out.println("\n--- Waiting 3 seconds for token refill ---");
        Thread.sleep(3000);

        System.out.println("--- Sending 5 more requests ---");
        for (int i = 1; i <= 5; i++) {
            boolean allowed = limiter.allowRequest(client);
            long remaining = limiter.getRemainingTokens(client);
            System.out.printf("  Request %d: %s (remaining: %d)%n",
                    i, allowed ? "ALLOWED" : "BLOCKED", remaining);
        }

        System.out.printf("%nRate limiting prevents:%n");
        System.out.println("  - Brute force login attacks");
        System.out.println("  - API abuse by misbehaving clients");
        System.out.println("  - Resource exhaustion from high traffic");
        System.out.println("  - DDoS amplification");
    }
}
