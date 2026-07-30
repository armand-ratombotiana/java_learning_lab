# Implementation Guide: Rate Limiting

## 1. Token Bucket Algorithm

### Concept
A bucket holds tokens. Tokens are added at a fixed rate (e.g., 10 tokens/second). Each request consumes one token. If no tokens remain, the request is rejected or queued.

### Implementation
```java
class TokenBucket {
    final long capacity;     // max tokens
    final double refillRate; // tokens per second
    double tokens;
    long lastRefill;

    synchronized boolean allow() {
        refill();
        if (tokens >= 1) { tokens--; return true; }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsed = (now - lastRefill) / 1_000_000_000.0;
        tokens = Math.min(capacity, tokens + elapsed * refillRate);
        lastRefill = now;
    }
}
```

### Characteristics
- Allows bursts up to `capacity`
- Smooth average rate
- Simple and efficient

## 2. Leaky Bucket Algorithm

### Concept
Requests enter a queue (bucket) and are processed at a fixed rate. If the queue is full, requests are discarded.

### Implementation
```java
class LeakyBucket {
    final long capacity;
    final long leakRate;  // per second
    long water;
    long lastLeak;

    synchronized boolean allow() {
        leak();
        if (water < capacity) { water++; return true; }
        return false;
    }

    private void leak() {
        long now = System.nanoTime();
        long elapsed = (now - lastLeak) / 1_000_000_000;
        water = Math.max(0, water - elapsed * leakRate);
        lastLeak = now;
    }
}
```

## 3. Fixed Window Counter

### Concept
Reset counter at the start of each time window (e.g., every minute). If counter exceeds limit, block until next window.

### Spike Problem
If limit is 100 req/min, a client can send 100 requests at 00:59 and 100 at 01:01 — effectively 200 requests in 2 seconds.

## 4. Sliding Window Log

### Concept
Maintain a sorted list of timestamps. Remove timestamps older than the window. Count remaining timestamps.

### Implementation
```java
class SlidingWindow {
    final long windowSize; // nanoseconds
    final long maxRequests;
    final Deque<Long> timestamps = new ConcurrentLinkedDeque<>();

    boolean allow() {
        long now = System.nanoTime();
        while (!timestamps.isEmpty() && timestamps.peek() < now - windowSize)
            timestamps.poll();
        if (timestamps.size() < maxRequests) {
            timestamps.add(now);
            return true;
        }
        return false;
    }
}
```

## 5. Distributed Rate Limiting (Redis)

### Redis Lua Script (Atomic)
```lua
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local window = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

redis.call('ZREMRANGEBYSCORE', key, 0, now - window)
local count = redis.call('ZCARD', key)
if count < limit then
    redis.call('ZADD', key, now, now .. ':' .. math.random())
    redis.call('EXPIRE', key, window / 1000)
    return 1
else
    return 0
end
```

### Considerations
- Network latency to Redis adds overhead
- Use local token bucket + Redis sync for lower latency
- Handle Redis failover gracefully
