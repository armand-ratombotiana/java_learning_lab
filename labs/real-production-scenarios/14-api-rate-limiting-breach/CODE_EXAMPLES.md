# Lab 14 — API Rate Limiting: Code Examples

## Token Bucket Rate Limiter

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class TokenBucketRateLimiter {
    private final long capacity;        // Max burst size
    private final double refillRate;    // Tokens per second
    private final AtomicLong tokens;
    private volatile long lastRefillTime;

    public TokenBucketRateLimiter(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = new AtomicLong(capacity);
        this.lastRefillTime = System.nanoTime();
    }

    public boolean allow() {
        refill();
        return tokens.getAndUpdate(t -> t > 0 ? t - 1 : t) > 0;
    }

    public boolean tryAcquire(long permits) {
        refill();
        return tokens.getAndUpdate(t -> t >= permits ? t - permits : t) >= permits;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillTime;
        if (elapsed > 0) {
            long newTokens = (long) (elapsed * refillRate / 1_000_000_000.0);
            if (newTokens > 0) {
                tokens.updateAndGet(t -> Math.min(capacity, t + newTokens));
                lastRefillTime = now;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10, 5); // Burst 10, refill 5/sec
        for (int i = 0; i < 30; i++) {
            boolean allowed = limiter.allow();
            System.out.printf("Request %2d: %s%n", i, allowed ? "✅ ALLOWED" : "❌ BLOCKED");
            Thread.sleep(100);
        }
    }
}
```

## Sliding Window Rate Limiter (Redis-backed)

```java
import redis.clients.jedis.*;
import java.time.*;

public class SlidingWindowRateLimiter {
    private final Jedis jedis;
    private final int maxRequests;
    private final long windowSeconds;

    public SlidingWindowRateLimiter(Jedis jedis, int maxRequests, long windowSeconds) {
        this.jedis = jedis;
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public boolean allow(String clientId) {
        String key = "ratelimit:" + clientId;
        long now = Instant.now().getEpochSecond();
        long windowStart = now - windowSeconds;

        // Remove entries outside the window
        jedis.zremrangeByScore(key, 0, windowStart);

        // Check current count
        long count = jedis.zcard(key);
        if (count >= maxRequests) {
            return false;
        }

        // Add current request
        jedis.zadd(key, now, String.valueOf(now));
        jedis.expire(key, (int) windowSeconds * 2);
        return true;
    }

    public long getRemaining(String clientId) {
        String key = "ratelimit:" + clientId;
        return Math.max(0, maxRequests - jedis.zcard(key));
    }

    public long getResetTime(String clientId) {
        String key = "ratelimit:" + clientId;
        var entries = jedis.zrangeWithScores(key, 0, 0);
        if (entries.isEmpty()) return 0;
        return entries.iterator().next().getScore().longValue() + windowSeconds;
    }
}
```

## Rate Limit Filter

```java
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class RateLimitFilter implements Filter {
    private final TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(100, 10);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String clientId = getClientId(request);

        if (!limiter.allow()) {
            response.setStatus(429);
            response.setHeader("Retry-After", "10");
            response.setHeader("X-RateLimit-Limit", "100");
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"rate_limit_exceeded\",\"message\":\"Too many requests\"}");
            return;
        }

        chain.doFilter(req, res);
    }

    private String getClientId(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null) return apiKey;
        return request.getRemoteAddr();
    }
}
```

## Unit Tests

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TokenBucketRateLimiterTest {
    @Test
    void testAllowsBurstWithinCapacity() {
        var limiter = new TokenBucketRateLimiter(10, 5);
        for (int i = 0; i < 10; i++) {
            assertTrue(limiter.allow(), "Burst requests should be allowed up to capacity");
        }
    }

    @Test
    void testBlocksAfterCapacity() {
        var limiter = new TokenBucketRateLimiter(5, 10);
        for (int i = 0; i < 5; i++) limiter.allow();
        assertFalse(limiter.allow(), "Requests beyond capacity should be blocked");
    }

    @Test
    void testRefillsOverTime() throws InterruptedException {
        var limiter = new TokenBucketRateLimiter(5, 10);
        for (int i = 0; i < 5; i++) limiter.allow();
        Thread.sleep(500); // Should have refilled ~5 tokens
        assertTrue(limiter.allow(), "Should refill tokens over time");
    }
}
```
