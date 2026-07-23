package com.prod.solutions.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Token bucket rate limiter implementation.
 * Each client has a bucket with a capacity (burst) and refill rate.
 * Tokens are added at the refill rate; each request consumes one token.
 * If no tokens remain, the request is rate-limited.
 */
public class TokenBucketRateLimiter {

    static class TokenBucket {
        private final long capacity;
        private final double refillPerSecond;
        private double tokens;
        private long lastRefillTime;

        TokenBucket(long capacity, double refillPerSecond) {
            this.capacity = capacity;
            this.refillPerSecond = refillPerSecond;
            this.tokens = capacity;
            this.lastRefillTime = System.nanoTime();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        synchronized long getTokens() { return (long) tokens; }

        private void refill() {
            long now = System.nanoTime();
            double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;
            if (elapsedSeconds > 0) {
                tokens = Math.min(capacity, tokens + elapsedSeconds * refillPerSecond);
                lastRefillTime = now;
            }
        }
    }

    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final long defaultCapacity;
    private final double defaultRefillRate;

    public TokenBucketRateLimiter(long capacity, double refillPerSecond) {
        this.defaultCapacity = capacity;
        this.defaultRefillRate = refillPerSecond;
    }

    public boolean allowRequest(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
                k -> new TokenBucket(defaultCapacity, defaultRefillRate));
        return bucket.tryConsume();
    }

    public long getRemainingTokens(String clientId) {
        TokenBucket bucket = buckets.get(clientId);
        return bucket != null ? bucket.getTokens() : defaultCapacity;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Token Bucket Rate Limiter Demo ===\n");

        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 2.0); // 10 burst, 2/sec

        String client = "client-001";

        // Burst of 15 requests
        System.out.println("--- Sending 15 requests (burst) ---");
        for (int i = 1; i <= 15; i++) {
            boolean allowed = limiter.allowRequest(client);
            System.out.printf("  Request %2d: %s (remaining: %d)%n",
                    i, allowed ? "✓" : "✗", limiter.getRemainingTokens(client));
        }

        // Wait for refill
        System.out.println("\n--- Waiting 3 seconds (refilling at 2/sec) ---");
        Thread.sleep(3000);

        System.out.println("--- 5 more requests ---");
        for (int i = 1; i <= 5; i++) {
            boolean allowed = limiter.allowRequest(client);
            System.out.printf("  Request %2d: %s (remaining: %d)%n",
                    i, allowed ? "✓" : "✗", limiter.getRemainingTokens(client));
        }

        System.out.printf("%nToken bucket configuration:%n");
        System.out.println("  burst = 10, refill = 2/sec");
        System.out.println("  Allows short bursts while limiting average rate.");
    }
}
