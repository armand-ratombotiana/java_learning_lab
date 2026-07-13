# Rate Limiting Code Deep Dive

This lab provides a pure Java implementation of the Token Bucket algorithm. It demonstrates how to calculate token refills based on time elapsed without needing a dedicated background thread.

## 💻 Pure Java Implementation

```java file="labs/system-design/09-api-gateways/rate-limiting/SOLUTION/TokenBucketRateLimiter.java"
package systemdesign.apigateways.ratelimiting;

/**
 * A fundamental implementation of the Token Bucket algorithm.
 * This implementation avoids a background refill thread by calculating 
 * the required refill at the exact moment a request arrives.
 */
public class TokenBucketRateLimiter {

    private final long maxBucketSize;
    private final long refillRatePerSecond;

    private double currentTokens;
    private long lastRefillTimestamp;

    /**
     * @param maxBucketSize The maximum burst capacity.
     * @param refillRatePerSecond How many tokens are added per second.
     */
    public TokenBucketRateLimiter(long maxBucketSize, long refillRatePerSecond) {
        this.maxBucketSize = maxBucketSize;
        this.refillRatePerSecond = refillRatePerSecond;
        
        // Start with a full bucket
        this.currentTokens = maxBucketSize;
        this.lastRefillTimestamp = System.nanoTime();
    }

    /**
     * Attempts to consume 1 token.
     * 
     * @return true if the request is allowed, false if it should be rejected (HTTP 429).
     */
    public synchronized boolean allowRequest() {
        refill();

        if (currentTokens >= 1.0) {
            currentTokens -= 1.0;
            return true;
        }
        
        return false;
    }

    /**
     * Calculates how much time has passed since the last request and adds the appropriate
     * number of tokens to the bucket, capping at maxBucketSize.
     */
    private void refill() {
        long now = System.nanoTime();
        
        // Calculate time elapsed in seconds
        double elapsedTimeInSeconds = (now - lastRefillTimestamp) / 1_000_000_000.0;
        
        // Calculate how many tokens should be added based on the elapsed time
        double tokensToAdd = elapsedTimeInSeconds * refillRatePerSecond;

        if (tokensToAdd > 0) {
            // Add tokens, but do not exceed the bucket capacity
            currentTokens = Math.min(currentTokens + tokensToAdd, maxBucketSize);
            
            // Update the timestamp. We only update it if we actually added tokens
            // to prevent precision loss on very fast, consecutive requests.
            lastRefillTimestamp = now;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Create a limiter that allows a burst of 5 requests, and refills at 2 requests per second.
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(5, 2);

        System.out.println("--- Simulating an immediate burst of 7 requests ---");
        for (int i = 1; i <= 7; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.printf("Request %d: %s%n", i, allowed ? "ALLOWED ✅" : "REJECTED ❌ (HTTP 429)");
        }
        // Expected: 1-5 Allowed, 6-7 Rejected

        System.out.println("\n--- Waiting for 1.5 seconds to allow bucket to refill... ---");
        Thread.sleep(1500); // Should refill ~3 tokens (1.5s * 2 tokens/s)

        System.out.println("\n--- Simulating another burst of 4 requests ---");
        for (int i = 8; i <= 11; i++) {
            boolean allowed = limiter.allowRequest();
            System.out.printf("Request %d: %s%n", i, allowed ? "ALLOWED ✅" : "REJECTED ❌ (HTTP 429)");
        }
        // Expected: 8-10 Allowed, 11 Rejected
    }
}
```

## 🔍 Key Takeaways
1. **No Background Thread**: A naive implementation might use a `ScheduledExecutorService` to add 1 token every second. This is terribly inefficient if you have 100,000 users, as you'd need 100,000 threads (or one massive loop running constantly). By calculating the elapsed time inside `allowRequest()`, the memory and CPU footprint is almost zero.
2. **Double Precision**: Notice `currentTokens` is a `double`. If a user is allowed 1 token per second, and they make a request after 0.5 seconds, they earn 0.5 tokens. Using integers would truncate this to 0, artificially penalizing the user.
3. **Concurrency**: The `allowRequest` method is `synchronized`. In a highly concurrent environment, multiple threads handling requests for the same user could cause race conditions when updating `currentTokens`. In a distributed environment, this logic is often moved into a Redis Lua script to guarantee atomicity across multiple API Gateway servers.