# Solution: API Rate Limiting Fix

## Step 1: Kong Rate Limiter Configuration (Burst + Graduated Limiting)

```json
{
  "name": "rate-limiting",
  "config": {
    "second": null,
    "minute": 100,
    "hour": null,
    "day": null,
    "month": null,
    "year": null,
    "limit_by": "consumer",
    "policy": "redis",
    "redis_host": "redis-ratelimit.production.svc.cluster.local",
    "redis_port": 6379,
    "redis_password": null,
    "redis_database": 0,
    "redis_timeout": 2000,
    "fault_tolerant": true,
    "hide_client_headers": false,
    "burst": 50,
    "warn_threshold": 80,
    "warn_headers": true
  }
}
```

### Key Changes:
- `burst: 50` — Token bucket now has 150 tokens (100 limit + 50 burst)
- `fault_tolerant: true` — Redis failures don't cascade to Kong
- `warn_threshold: 80` — Warning at 80% of limit
- `warn_headers: true` — Return X-RateLimit headers

## Step 2: Java Code — Token Bucket Rate Limiter

```java
package com.acmecorp.apigateway.ratelimit;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucketRateLimiter {

    private static final ConcurrentMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    public static class TokenBucket {
        private final long capacity;
        private final long refillRate;
        private final Duration refillDuration;
        private final AtomicLong tokens;
        private volatile Instant lastRefill;

        public TokenBucket(long capacity, long refillRate, Duration refillDuration) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.refillDuration = refillDuration;
            this.tokens = new AtomicLong(capacity);
            this.lastRefill = Instant.now();
        }

        public boolean tryConsume(int count) {
            refill();
            while (true) {
                long current = tokens.get();
                if (current < count) {
                    return false;
                }
                if (tokens.compareAndSet(current, current - count)) {
                    return true;
                }
            }
        }

        private void refill() {
            Instant now = Instant.now();
            Instant last = lastRefill;
            long elapsed = Duration.between(last, now).toMillis();
            long refillIntervalMs = refillDuration.toMillis();

            if (elapsed >= refillIntervalMs) {
                long refillCycles = elapsed / refillIntervalMs;
                long newTokens = Math.min(capacity,
                    tokens.get() + (refillRate * refillCycles));
                tokens.set(newTokens);
                lastRefill = now;
            }
        }

        public long getAvailableTokens() { refill(); return tokens.get(); }
        public long getCapacity() { return capacity; }
    }

    private final TokenBucket warnBucket;
    private final TokenBucket softBucket;
    private final TokenBucket hardBucket;

    public TokenBucketRateLimiter(long limitPerMinute) {
        long burstCapacity = limitPerMinute + (limitPerMinute / 2);
        this.warnBucket = new TokenBucket(
            (long) (limitPerMinute * 0.8), 80, Duration.ofMinutes(1));
        this.softBucket = new TokenBucket(
            limitPerMinute, 100, Duration.ofMinutes(1));
        this.hardBucket = new TokenBucket(
            burstCapacity, (long) (limitPerMinute * 1.5), Duration.ofMinutes(1));
    }

    public enum RateLimitStatus {
        OK(200, false),
        WARNING(200, true),
        SOFT_LIMITED(200, true),
        HARD_LIMITED(429, false);

        private final int httpStatus;
        private final boolean delayRequest;

        RateLimitStatus(int httpStatus, boolean delayRequest) {
            this.httpStatus = httpStatus;
            this.delayRequest = delayRequest;
        }

        public int getHttpStatus() { return httpStatus; }
        public boolean shouldDelayRequest() { return delayRequest; }
    }

    public RateLimitResult checkRateLimit(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(clientId,
            k -> new TokenBucket(hardBucket.getCapacity(),
                hardBucket.getAvailableTokens(), Duration.ofMinutes(1)));

        if (!hardBucket.tryConsume(1)) {
            return new RateLimitResult(RateLimitStatus.HARD_LIMITED, 0, 60);
        }

        if (!softBucket.tryConsume(1)) {
            return new RateLimitResult(RateLimitStatus.SOFT_LIMITED, 100, 0);
        }

        if (!warnBucket.tryConsume(1)) {
            return new RateLimitResult(RateLimitStatus.WARNING,
                hardBucket.getAvailableTokens(), 0);
        }

        return new RateLimitResult(RateLimitStatus.OK,
            hardBucket.getAvailableTokens(), 0);
    }

    public static class RateLimitResult {
        private final RateLimitStatus status;
        private final long remainingTokens;
        private final long retryAfterSeconds;

        public RateLimitResult(RateLimitStatus status,
                              long remainingTokens,
                              long retryAfterSeconds) {
            this.status = status;
            this.remainingTokens = remainingTokens;
            this.retryAfterSeconds = retryAfterSeconds;
        }

        public RateLimitStatus getStatus() { return status; }
        public long getRemainingTokens() { return remainingTokens; }
        public long getRetryAfterSeconds() { return retryAfterSeconds; }
    }

    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(100);

        // Simulate 200 requests from a single client
        String clientId = "client_test_1";
        int allowed = 0;
        int warned = 0;
        int softLimited = 0;
        int hardLimited = 0;

        for (int i = 0; i < 200; i++) {
            RateLimitResult result = limiter.checkRateLimit(clientId);
            switch (result.getStatus()) {
                case OK: allowed++; break;
                case WARNING: warned++; break;
                case SOFT_LIMITED: softLimited++; break;
                case HARD_LIMITED: hardLimited++; break;
            }
            Thread.sleep(100);
        }

        System.out.println("=== Rate Limiter Test ===");
        System.out.println("Allowed: " + allowed);
        System.out.println("Warned: " + warned);
        System.out.println("Soft limited: " + softLimited);
        System.out.println("Hard limited (429): " + hardLimited);
    }
}
```

## Step 3: Redis Rate Limiter Backend with Proper Eviction

```java
package com.acmecorp.apigateway.ratelimit;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

import java.time.Instant;

public class RedisRateLimiter {

    private static final String RATE_LIMIT_SCRIPT =
        "local key = KEYS[1]\n" +
        "local limit = tonumber(ARGV[1])\n" +
        "local window = tonumber(ARGV[2])\n" +
        "local now = tonumber(ARGV[3])\n" +
        "local window_start = now - (now % window)\n" +
        "local window_key = key .. ':' .. window_start\n" +
        "local count = redis.call('INCR', window_key)\n" +
        "if count == 1 then\n" +
        "    redis.call('PEXPIRE', window_key, window * 1000)\n" +
        "end\n" +
        "return {count, limit}";

    private static final String FIXED_WINDOW_SCRIPT =
        "local key = KEYS[1]\n" +
        "local limit = tonumber(ARGV[1])\n" +
        "local ttl = tonumber(ARGV[2])\n" +
        "local current = redis.call('INCR', key)\n" +
        "if current == 1 then\n" +
        "    redis.call('PEXPIRE', key, ttl)\n" +
        "end\n" +
        "return {current, limit}";

    private final JedisPool jedisPool;
    private final int limit;
    private final int windowSeconds;

    public RedisRateLimiter(JedisPool jedisPool, int limit, int windowSeconds) {
        this.jedisPool = jedisPool;
        this.limit = limit;
        this.windowSeconds = windowSeconds;
    }

    public static class RateLimitResult {
        private final boolean allowed;
        private final int currentCount;
        private final int limit;
        private final long retryAfterSeconds;

        public RateLimitResult(boolean allowed, int currentCount,
                              int limit, long retryAfterSeconds) {
            this.allowed = allowed;
            this.currentCount = currentCount;
            this.limit = limit;
            this.retryAfterSeconds = retryAfterSeconds;
        }

        public boolean isAllowed() { return allowed; }
        public int getCurrentCount() { return currentCount; }
        public int getLimit() { return limit; }
        public long getRetryAfterSeconds() { return retryAfterSeconds; }
    }

    public RateLimitResult checkRateLimit(String clientId, String endpoint) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "ratelimit:" + clientId + ":" + endpoint;
            long epochSecond = Instant.now().getEpochSecond();
            long windowStart = epochSecond - (epochSecond % windowSeconds);
            String windowKey = key + ":" + windowStart;

            Object result = jedis.eval(FIXED_WINDOW_SCRIPT, 1,
                windowKey,
                String.valueOf(limit),
                String.valueOf(windowSeconds * 1000L));

            if (result instanceof java.util.List) {
                java.util.List<Long> values = (java.util.List<Long>) result;
                long count = values.get(0);
                long maxLimit = values.get(1);

                boolean allowed = count <= maxLimit;
                long retryAfter = allowed ? 0 :
                    windowSeconds - (epochSecond - windowStart);

                return new RateLimitResult(allowed, (int) count,
                    (int) maxLimit, retryAfter);
            }
        }
        // fault_tolerant: true — allow on Redis failure
        return new RateLimitResult(true, 0, limit, 0);
    }
}
```

## Step 4: Retry-After Header Filter

```java
package com.acmecorp.apigateway.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RateLimitHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                        FilterChain chain) throws IOException, ServletException {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("X-RateLimit-Limit",
                String.valueOf(100));
            httpResponse.setHeader("X-RateLimit-Remaining",
                String.valueOf(getRemainingTokens(request)));
            httpResponse.setHeader("X-RateLimit-Reset",
                String.valueOf(getResetEpoch()));
        }
        chain.doFilter(request, response);
    }

    private long getRemainingTokens(ServletRequest request) {
        String clientId = request.getParameter("client_id");
        return rateLimiter.checkRateLimit(clientId).getRemainingTokens();
    }

    private long getResetEpoch() {
        return Instant.now().getEpochSecond() + 60;
    }

    private final TokenBucketRateLimiter rateLimiter =
        new TokenBucketRateLimiter(100);
}
```

## Step 5: Client Retry Logic (Exponential Backoff with Jitter)

```java
package com.acmecorp.api.client;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ResilientApiClient {

    private static final int MAX_RETRIES = 5;
    private static final long BASE_DELAY_MS = 1000;
    private static final long MAX_DELAY_MS = 60000;
    private final Random random = new Random();
    private final HttpClient httpClient;

    public ResilientApiClient() {
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    }

    public HttpResponse<String> callApi(String url, String apiKey)
            throws InterruptedException, IOException {
        return callWithRetry(url, apiKey, 0);
    }

    private HttpResponse<String> callWithRetry(String url, String apiKey,
                                              int attempt)
            throws InterruptedException, IOException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + apiKey)
            .header("Accept", "application/json")
            .GET()
            .build();

        HttpResponse<String> response = httpClient.send(request,
            HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 429) {
            long retryAfter = extractRetryAfter(response);
            if (attempt < MAX_RETRIES) {
                long delay = calculateBackoff(attempt, retryAfter);
                System.out.println("Rate limited (attempt " +
                    (attempt + 1) + "). Waiting " + delay + "ms...");
                Thread.sleep(delay);
                return callWithRetry(url, apiKey, attempt + 1);
            } else {
                throw new RuntimeException("Max retries exceeded for " + url);
            }
        }

        return response;
    }

    private long extractRetryAfter(HttpResponse<String> response) {
        return response.headers().firstValueAsLong("Retry-After")
            .orElse(60L) * 1000; // Default 60 seconds
    }

    private long calculateBackoff(int attempt, long retryAfter) {
        // Exponential backoff: 1s, 2s, 4s, 8s, 16s
        long exponentialDelay = BASE_DELAY_MS * (long) Math.pow(2, attempt);
        // Use server-specified retry-after if available and reasonable
        long delay = Math.min(exponentialDelay, retryAfter);
        // Cap at max delay
        delay = Math.min(delay, MAX_DELAY_MS);
        // Add jitter: ±20%
        double jitter = 0.8 + (random.nextDouble() * 0.4);
        return (long) (delay * jitter);
    }

    public static void main(String[] args) throws Exception {
        ResilientApiClient client = new ResilientApiClient();
        String url = "https://api.acmecorp.com/v2/orders";
        String apiKey = "sk_test_" + System.currentTimeMillis();

        for (int i = 0; i < 500; i++) {
            try {
                HttpResponse<String> response = client.callApi(url, apiKey);
                System.out.println("Request " + i + ": " + response.statusCode());
            } catch (Exception e) {
                System.err.println("Request " + i + " failed: " + e.getMessage());
            }
            Thread.sleep(200); // 5 req/s
        }
    }
}
```

## Step 6: Verification Commands

```bash
# Check Kong rate limiter configuration
curl -s http://localhost:8001/plugins | jq '.data[] | select(.name=="rate-limiting")'

# Verify rate limit headers
curl -sI https://api.acmecorp.com/v2/orders \
  -H "Authorization: Bearer sk_test_key" | grep -i "x-ratelimit"

# Test rate limiting
for i in $(seq 1 150); do
  curl -s -o /dev/null -w "%{http_code}\n" \
    https://api.acmecorp.com/v2/orders \
    -H "Authorization: Bearer sk_test_key"
  sleep 0.5
done

# Check Redis memory
redis-cli -h redis-ratelimit.production INFO memory | grep -E "used_memory|maxmemory|evicted"

# Check Kong 429 rate
curl -s http://localhost:8001/status | jq '.server.rate_limiting'
```
