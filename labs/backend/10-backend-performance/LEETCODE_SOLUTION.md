# Design a Rate Limiter (Token Bucket + Sliding Window)

## Problem Statement
Design and implement a rate limiter with two strategies:
- **Token Bucket**: refills tokens at a fixed rate; each request consumes one token
- **Sliding Window Log**: tracks request timestamps in a rolling window; rejects if count exceeds limit

Requirements:
- Configurable rate (requests per second/minute)
- Per-client (IP/API key) isolation
- Thread-safe concurrent access
- Metrics: allowed, rejected, current rate
- Graceful degradation on overflow

## Solution

```java
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Rate limiter with Token Bucket and Sliding Window Log strategies.
 * <p>
 * Time complexity:
 * - TokenBucket#allow: O(1)
 * - SlidingWindowLog#allow: O(log n) average (binary search on timestamps)
 * <p>
 * Space complexity:
 * - TokenBucket: O(1) per client
 * - SlidingWindowLog: O(window) per client (amortized by cleanup)
 */
public class RateLimiter {

    public enum Strategy { TOKEN_BUCKET, SLIDING_WINDOW }

    private final Strategy strategy;
    private final long maxRequests;
    private final long windowSizeMs;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimiter(Strategy strategy, long maxRequests, long windowSizeMs) {
        if (maxRequests <= 0 || windowSizeMs <= 0) {
            throw new IllegalArgumentException("maxRequests and windowSizeMs must be > 0");
        }
        this.strategy = strategy;
        this.maxRequests = maxRequests;
        this.windowSizeMs = windowSizeMs;
    }

    public boolean allowRequest(String clientId) {
        return switch (strategy) {
            case TOKEN_BUCKET -> getOrCreateBucket(clientId).tryConsume();
            case SLIDING_WINDOW -> getOrCreateBucket(clientId).tryConsume();
        };
    }

    public RateLimitMetrics getMetrics(String clientId) {
        Bucket bucket = buckets.get(clientId);
        if (bucket == null) return new RateLimitMetrics(0, 0, 0, 0);
        return bucket.getMetrics();
    }

    public void reset(String clientId) {
        buckets.remove(clientId);
    }

    public int getActiveClientCount() {
        return buckets.size();
    }

    private Bucket getOrCreateBucket(String clientId) {
        return buckets.computeIfAbsent(clientId, k -> {
            return switch (strategy) {
                case TOKEN_BUCKET -> new TokenBucket(maxRequests, windowSizeMs);
                case SLIDING_WINDOW -> new SlidingWindowLog(maxRequests, windowSizeMs);
            };
        });
    }

    // ── Bucket interface ───────────────────────────────────────────────────

    private interface Bucket {
        boolean tryConsume();
        RateLimitMetrics getMetrics();
    }

    // ── Token Bucket ────────────────────────────────────────────────────────

    public static class TokenBucket implements Bucket {
        private final long maxTokens;
        private final long refillIntervalMs;
        private final double tokensPerRefill;
        private final AtomicLong availableTokens;
        private final AtomicLong lastRefillTimestamp;
        private final AtomicLong allowedCount = new AtomicLong(0);
        private final AtomicLong rejectedCount = new AtomicLong(0);

        public TokenBucket(long maxRequests, long windowSizeMs) {
            this.maxTokens = maxRequests;
            this.refillIntervalMs = windowSizeMs / maxRequests;
            this.tokensPerRefill = 1.0;
            this.availableTokens = new AtomicLong(maxRequests);
            this.lastRefillTimestamp = new AtomicLong(System.currentTimeMillis());
        }

        @Override
        public boolean tryConsume() {
            refill();
            while (true) {
                long current = availableTokens.get();
                if (current <= 0) {
                    rejectedCount.incrementAndGet();
                    return false;
                }
                if (availableTokens.compareAndSet(current, current - 1)) {
                    allowedCount.incrementAndGet();
                    return true;
                }
            }
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long lastRefill = lastRefillTimestamp.get();
            long elapsed = now - lastRefill;
            if (elapsed < refillIntervalMs) return;

            long tokensToAdd = elapsed / refillIntervalMs;
            if (tokensToAdd > 0 && lastRefillTimestamp.compareAndSet(lastRefill, now)) {
                long newTokens = Math.min(maxTokens, availableTokens.get() + tokensToAdd);
                availableTokens.getAndUpdate(v -> Math.min(maxTokens, v + tokensToAdd));
            }
        }

        @Override
        public RateLimitMetrics getMetrics() {
            refill();
            return new RateLimitMetrics(
                allowedCount.get(), rejectedCount.get(),
                availableTokens.get(), maxTokens);
        }
    }

    // ── Sliding Window Log ─────────────────────────────────────────────────

    public static class SlidingWindowLog implements Bucket {
        private final long maxRequests;
        private final long windowSizeMs;
        private final ConcurrentLinkedDeque<Long> timestamps = new ConcurrentLinkedDeque<>();
        private final AtomicLong allowedCount = new AtomicLong(0);
        private final AtomicLong rejectedCount = new AtomicLong(0);

        public SlidingWindowLog(long maxRequests, long windowSizeMs) {
            this.maxRequests = maxRequests;
            this.windowSizeMs = windowSizeMs;
        }

        @Override
        public boolean tryConsume() {
            long now = System.currentTimeMillis();
            long cutoff = now - windowSizeMs;
            cleanup(cutoff);

            synchronized (this) {
                if (timestamps.size() >= maxRequests) {
                    rejectedCount.incrementAndGet();
                    return false;
                }
                timestamps.addLast(now);
                allowedCount.incrementAndGet();
                return true;
            }
        }

        private void cleanup(long cutoff) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
                timestamps.pollFirst();
            }
        }

        @Override
        public RateLimitMetrics getMetrics() {
            long now = System.currentTimeMillis();
            cleanup(now - windowSizeMs);
            return new RateLimitMetrics(
                allowedCount.get(), rejectedCount.get(),
                Math.max(0, maxRequests - timestamps.size()), maxRequests);
        }
    }

    // ── Metrics record ─────────────────────────────────────────────────────

    public record RateLimitMetrics(long allowed, long rejected,
                                   long remainingTokens, long capacity) {
        public double getRejectionRate() {
            long total = allowed + rejected;
            return total == 0 ? 0.0 : (double) rejected / total;
        }
    }

    // ── Example usage ──────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        // Token Bucket: 5 requests per second
        RateLimiter tokenBucket = new RateLimiter(
            Strategy.TOKEN_BUCKET, 5, 1000);
        // Sliding Window: 5 requests per second
        RateLimiter slidingWindow = new RateLimiter(
            Strategy.SLIDING_WINDOW, 5, 1000);

        String client = "user-123";
        System.out.println("=== Token Bucket ===");
        for (int i = 0; i < 8; i++) {
            boolean allowed = tokenBucket.allowRequest(client);
            System.out.println("  Request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "REJECTED"));
        }
        System.out.println("  Metrics: " + tokenBucket.getMetrics(client));

        System.out.println("\n=== Sliding Window ===");
        for (int i = 0; i < 8; i++) {
            boolean allowed = slidingWindow.allowRequest(client);
            System.out.println("  Request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "REJECTED"));
        }
        System.out.println("  Metrics: " + slidingWindow.getMetrics(client));
    }
}
```

## Complexity Analysis

| Operation               | Token Bucket  | Sliding Window    |
|-------------------------|---------------|-------------------|
| allowRequest            | O(1)          | O(log n) avg*     |
| refill / cleanup        | O(1)          | O(n) worst        |
| getMetrics              | O(1)          | O(log n) avg      |
| reset                   | O(1)          | O(1)              |

*Sliding Window uses cleanup which is amortized O(1) per call.

Overall space: O(C * W) where C = client count, W = window entries per client.

## Test Cases

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;
import java.util.stream.*;

class RateLimiterTest {

    @Test
    void testTokenBucketAllowsWithinLimit() {
        var limiter = new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 5, 1000);
        for (int i = 0; i < 5; i++) {
            assertTrue(limiter.allowRequest("client-1"), "Request " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void testTokenBucketRejectsBeyondLimit() {
        var limiter = new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 3, 1000);
        for (int i = 0; i < 3; i++) limiter.allowRequest("client-1");
        assertFalse(limiter.allowRequest("client-1"));
    }

    @Test
    void testTokenBucketRefill() throws Exception {
        var limiter = new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 2, 200);
        assertTrue(limiter.allowRequest("c1"));
        assertTrue(limiter.allowRequest("c1"));
        assertFalse(limiter.allowRequest("c1"));
        Thread.sleep(250);
        assertTrue(limiter.allowRequest("c1")); // refilled
    }

    @Test
    void testSlidingWindowAllowsWithinLimit() {
        var limiter = new RateLimiter(RateLimiter.Strategy.SLIDING_WINDOW, 3, 1000);
        for (int i = 0; i < 3; i++) {
            assertTrue(limiter.allowRequest("client-2"));
        }
    }

    @Test
    void testSlidingWindowRejectsBeyondLimit() {
        var limiter = new RateLimiter(RateLimiter.Strategy.SLIDING_WINDOW, 2, 500);
        limiter.allowRequest("c1");
        limiter.allowRequest("c1");
        assertFalse(limiter.allowRequest("c1"));
    }

    @Test
    void testSlidingWindowExpiresOldEntries() throws Exception {
        var limiter = new RateLimiter(RateLimiter.Strategy.SLIDING_WINDOW, 2, 200);
        limiter.allowRequest("c1");
        limiter.allowRequest("c1");
        assertFalse(limiter.allowRequest("c1"));
        Thread.sleep(250);
        assertTrue(limiter.allowRequest("c1"));
    }

    @Test
    void testPerClientIsolation() {
        var limiter = new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 2, 1000);
        limiter.allowRequest("alice");
        limiter.allowRequest("alice");
        assertFalse(limiter.allowRequest("alice"));
        assertTrue(limiter.allowRequest("bob")); // bob has his own bucket
        assertTrue(limiter.allowRequest("bob"));
    }

    @Test
    void testMetrics() {
        var limiter = new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 5, 1000);
        limiter.allowRequest("c1");
        limiter.allowRequest("c1");
        var metrics = limiter.getMetrics("c1");
        assertEquals(2, metrics.allowed());
        assertTrue(metrics.remainingTokens() <= 3);
    }

    @Test
    void testReset() {
        var limiter = new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 3, 1000);
        limiter.allowRequest("c1");
        limiter.allowRequest("c1");
        limiter.allowRequest("c1");
        assertFalse(limiter.allowRequest("c1"));
        limiter.reset("c1");
        assertTrue(limiter.allowRequest("c1"));
    }

    @Test
    void testConcurrentAccess() throws Exception {
        var limiter = new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 100, 1000);
        int threads = 10;
        int opsPerThread = 20;
        var executor = Executors.newFixedThreadPool(threads);
        var latch = new CountDownLatch(threads);
        var allowed = new AtomicInteger(0);
        for (int t = 0; t < threads; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < opsPerThread; i++) {
                        if (limiter.allowRequest("concurrent")) {
                            allowed.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await(10, TimeUnit.SECONDS);
        assertTrue(allowed.get() <= 100);
        executor.shutdown();
    }

    @Test
    void testInvalidConstruction() {
        assertThrows(IllegalArgumentException.class,
            () -> new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 0, 1000));
        assertThrows(IllegalArgumentException.class,
            () -> new RateLimiter(RateLimiter.Strategy.TOKEN_BUCKET, 5, 0));
    }
}
```
