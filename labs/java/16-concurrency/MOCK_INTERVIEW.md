# Mock Interview Transcript: Concurrency

## Interviewer: Staff Engineer, Amazon
## Candidate: Senior Java developer
## Time: 50 minutes
## Focus: Thread pools, synchronization, CompletableFuture

---

**Q1: You're designing a thread pool for a web server. Walk through the parameters.**

**Candidate**: Core pool size = number of CPUs, max pool size = burst capacity, queue = bounded. For I/O-bound work: more threads (CPU * (1 + wait/compute)). For CPU-bound: fewer threads (close to CPU count). ThreadPoolExecutor params: `corePoolSize`, `maxPoolSize`, `keepAliveTime`, `workQueue`, `threadFactory`, `rejectedExecutionHandler`.

**Interviewer**: What happens when all core threads are busy and queue is full?

**Candidate**: When core threads are all busy, new tasks go to the queue. If the queue is full, a new thread is created (up to maxPoolSize). If maxPoolSize is reached and queue is full, the `RejectedExecutionHandler` is invoked. The default `AbortPolicy` throws `RejectedExecutionException`. Other policies: `CallerRunsPolicy` (runs in submitting thread — backpressure), `DiscardPolicy`, `DiscardOldestPolicy`.

**Interviewer**: Write a method that calls three services in parallel and combines results.

**Candidate**: 
```java
CompletableFuture<Response> fetchCombinedData(Request req) {
    CompletableFuture<User> user = CompletableFuture.supplyAsync(() -> userService.getUser(req.userId()));
    CompletableFuture<Order> order = CompletableFuture.supplyAsync(() -> orderService.getOrder(req.orderId()));
    CompletableFuture<Inventory> inventory = CompletableFuture.supplyAsync(() -> invService.check(req.sku()));
    
    return CompletableFuture.allOf(user, order, inventory)
        .thenApply(v -> new Response(user.join(), order.join(), inventory.join()));
}
```

**Interviewer**: What happens if one of the futures throws an exception?

**Candidate**: `allOf` completes even if one future fails. Calling `join()` on the failed future throws `CompletionException` wrapping the original exception. Better approach:
```java
CompletableFuture.allOf(user, order, inventory)
    .thenApply(v -> new Response(
        user.exceptionally(e -> fallbackUser()).join(),
        order.join(), inventory.join()))
    .exceptionally(e -> new Response("error"));
```

**Interviewer**: Explain `synchronized` at the JVM level. What bytecode is generated?

**Candidate**: A `synchronized` block generates `monitorenter` at the start and `monitorexit` at the end (with exception table entries to ensure unlock even on exceptions). The object's monitor is acquired: (1) First attempt: biased locking (if enabled and thread ID matches), (2) Second: CAS on object header (thin lock), (3) Third: inflated lock (OS mutex). The JVM tries to avoid OS-level locks.

**Interviewer**: What about `synchronized` methods?

**Candidate**: They don't use `monitorenter/monitorexit`. Instead, the method's `ACC_SYNCHRONIZED` flag in the access_flags tells the JVM to acquire the monitor on method entry and release on return (or exception). The behavior is the same, just different bytecode encoding.

**Interviewer**: Design a rate limiter that allows 100 requests per second.

**Candidate**: 
```java
class RateLimiter {
    private final long capacity;
    private final double refillPerSecond;
    private double tokens;
    private long lastRefill = System.nanoTime();
    
    RateLimiter(long capacity, double refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
        this.tokens = capacity;
    }
    
    synchronized boolean tryAcquire() {
        refill();
        if (tokens >= 1) {
            tokens--;
            return true;
        }
        return false;
    }
    
    private void refill() {
        long now = System.nanoTime();
        tokens = Math.min(capacity, tokens + 
            (now - lastRefill) * refillPerSecond / 1_000_000_000.0);
        lastRefill = now;
    }
}
```

**Interviewer**: How would you make this lock-free?

**Candidate**: Use `AtomicLong` for tokens stored as a fixed-point number:
```java
class LockFreeRateLimiter {
    private final AtomicLong tokens;
    private final long capacityNanos;
    private final long refillInterval;
    private final AtomicLong lastRefill = new AtomicLong(System.nanoTime());
    
    boolean tryAcquire() {
        while (true) {
            long now = System.nanoTime();
            long last = lastRefill.get();
            long elapsed = now - last;
            if (elapsed > refillInterval && lastRefill.compareAndSet(last, now)) {
                tokens.addAndGet(elapsed);
                if (tokens.get() > capacityNanos) tokens.set(capacityNanos);
            }
            long current = tokens.get();
            if (current >= refillInterval && tokens.compareAndSet(current, current - refillInterval)) {
                return true;
            }
            return false;
        }
    }
}
```

---

## Feedback

**Strengths**:
- Comprehensive thread pool understanding
- Correct CompletableFuture composition with error handling
- Deep synchronized internals knowledge
- Implements both lock-based and lock-free rate limiter

**Areas for Improvement**:
- Could discuss virtual threads as an alternative
- Might mention `StructuredTaskScope` for better error propagation

**Score**: 4.5/5 — Strong concurrency skills
