package com.prod.solutions.ratelimit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Demonstrates proper rate limit response headers (RFC 6585).
 * When a client is rate-limited, respond with:
 * - HTTP 429 Too Many Requests
 * - Retry-After header: seconds until client can retry
 * - X-RateLimit-Limit: max requests per window
 * - X-RateLimit-Remaining: remaining requests in window
 * - X-RateLimit-Reset: timestamp when limit resets
 */
public class RateLimitHeadersExample {

    static class RateLimitInfo {
        final long limit;
        final long remaining;
        final long resetTimestamp;
        final long retryAfterSeconds;

        RateLimitInfo(long limit, long remaining, long resetTimestamp, long retryAfterSeconds) {
            this.limit = limit;
            this.remaining = remaining;
            this.resetTimestamp = resetTimestamp;
            this.retryAfterSeconds = retryAfterSeconds;
        }

        String toHeaders() {
            return """
                    HTTP/1.1 429 Too Many Requests
                    X-RateLimit-Limit: %d
                    X-RateLimit-Remaining: %d
                    X-RateLimit-Reset: %d
                    Retry-After: %d
                    Content-Type: application/json
                    
                    {"error":"rate_limit_exceeded","message":"Too many requests. Retry after %d seconds."}
                    """.formatted(limit, remaining, resetTimestamp,
                    retryAfterSeconds, retryAfterSeconds);
        }
    }

    static class RateLimitHandler {
        private final Map<String, RateLimitInfo> clientRateInfo = new ConcurrentHashMap<>();

        RateLimitInfo checkRateLimit(String clientId, long limit, long windowMs) {
            long now = System.currentTimeMillis();
            long windowStart = now / windowMs;
            long windowEnd = (windowStart + 1) * windowMs / 1000;
            long retryAfter = (windowEnd - now / 1000);

            RateLimitInfo info = new RateLimitInfo(
                    limit,
                    limit - 1,
                    windowEnd,
                    retryAfter
            );

            clientRateInfo.put(clientId, info);
            return info;
        }

        RateLimitInfo getInfo(String clientId) {
            return clientRateInfo.get(clientId);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Rate Limit Response Headers Demo ===\n");

        RateLimitHandler handler = new RateLimitHandler();

        // Simulate rate-limited response
        String clientId = "api-key-123";

        System.out.println("Rate-limited response:");
        RateLimitInfo info = handler.checkRateLimit(clientId, 100, 60_000);
        System.out.println(info.toHeaders());

        System.out.println("--- Header Explanation ---");
        System.out.println("X-RateLimit-Limit:      Maximum requests allowed in the window");
        System.out.println("X-RateLimit-Remaining:  Requests remaining in the current window");
        System.out.println("X-RateLimit-Reset:      Unix timestamp when the limit resets");
        System.out.println("Retry-After:            Seconds to wait before retrying (RFC 6585)");
        System.out.println("                         Clients MUST respect Retry-After headers");

        System.out.println("\nClient best practices:");
        System.out.println("  1. Check X-RateLimit-Remaining before sending requests");
        System.out.println("  2. If you receive 429, wait Retry-After seconds before retrying");
        System.out.println("  3. Implement exponential backoff if Retry-After is missing");
        System.out.println("  4. DO NOT ignore 429 responses and retry immediately");
    }
}
