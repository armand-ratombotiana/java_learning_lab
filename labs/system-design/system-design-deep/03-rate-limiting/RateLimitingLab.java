package com.systemdesign.deep.lab03;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lab 03: Rate Limiting — Token Bucket, Leaky Bucket, Fixed Window,
 * Sliding Window, and Redis-based Distributed Rate Limiting.
 */
public class RateLimitingLab {

    // ──────────────────────────────────────────────
    // 1. Token Bucket
    // ──────────────────────────────────────────────
    static class TokenBucket {
        final long capacity;
        final double refillRate; // tokens per second
        double tokens;
        long lastRefillNanos;
        final ReentrantLock lock = new ReentrantLock();

        TokenBucket(long capacity, double refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity;
            this.lastRefillNanos = System.nanoTime();
        }

        boolean allow() {
            lock.lock();
            try {
                refill();
                if (tokens >= 1.0) {
                    tokens -= 1.0;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefillNanos) / 1_000_000_000.0;
            tokens = Math.min(capacity, tokens + elapsed * refillRate);
            lastRefillNanos = now;
        }

        int availableTokens() {
            lock.lock();
            try {
                refill();
                return (int) Math.floor(tokens);
            } finally {
                lock.unlock();
            }
        }
    }

    // ──────────────────────────────────────────────
    // 2. Leaky Bucket
    // ──────────────────────────────────────────────
    static class LeakyBucket {
        final long capacity;
        final long leakRate; // requests per second
        long water;
        long lastLeakNanos;
        final ReentrantLock lock = new ReentrantLock();

        LeakyBucket(long capacity, long leakRate) {
            this.capacity = capacity;
            this.leakRate = leakRate;
            this.water = 0;
            this.lastLeakNanos = System.nanoTime();
        }

        boolean allow() {
            lock.lock();
            try {
                leak();
                if (water < capacity) {
                    water++;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        private void leak() {
            long now = System.nanoTime();
            long elapsed = (now - lastLeakNanos) / 1_000_000_000;
            water = Math.max(0, water - elapsed * leakRate);
            lastLeakNanos = now;
        }

        int currentWater() {
            lock.lock();
            try { leak(); return (int) water; } finally { lock.unlock(); }
        }
    }

    // ──────────────────────────────────────────────
    // 3. Fixed Window Counter
    // ──────────────────────────────────────────────
    static class FixedWindowCounter {
        final long maxRequests;
        final long windowDurationNanos;
        long windowStart;
        long counter;
        final ReentrantLock lock = new ReentrantLock();

        FixedWindowCounter(long maxRequests, long windowDurationSec) {
            this.maxRequests = maxRequests;
            this.windowDurationNanos = windowDurationSec * 1_000_000_000L;
            this.windowStart = System.nanoTime();
            this.counter = 0;
        }

        boolean allow() {
            lock.lock();
            try {
                long now = System.nanoTime();
                if (now - windowStart >= windowDurationNanos) {
                    windowStart = now;
                    counter = 0;
                }
                if (counter < maxRequests) {
                    counter++;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }
    }

    // ──────────────────────────────────────────────
    // 4. Sliding Window Log
    // ──────────────────────────────────────────────
    static class SlidingWindowLog {
        final long maxRequests;
        final long windowNanos;
        final Deque<Long> timestamps = new ConcurrentLinkedDeque<>();

        SlidingWindowLog(long maxRequests, long windowSec) {
            this.maxRequests = maxRequests;
            this.windowNanos = windowSec * 1_000_000_000L;
        }

        boolean allow() {
            long now = System.nanoTime();
            long cutoff = now - windowNanos;
            while (!timestamps.isEmpty() && timestamps.peek() < cutoff)
                timestamps.poll();
            if (timestamps.size() < maxRequests) {
                timestamps.add(now);
                return true;
            }
            return false;
        }

        int recentCount() { return timestamps.size(); }
    }

    // ──────────────────────────────────────────────
    // 5. Distributed Rate Limiter (Redis Simulation)
    // ──────────────────────────────────────────────
    static class RedisRateLimiter {
        // Simulates Redis sorted set with atomic Lua script
        static class RedisSortedSet {
            final TreeMap<Long, String> entries = new TreeMap<>();
            final ReentrantLock lock = new ReentrantLock();

            int removeRangeByScore(long min, long max) {
                lock.lock();
                try {
                    int removed = 0;
                    var iter = entries.entrySet().iterator();
                    while (iter.hasNext()) {
                        long score = iter.next().getKey();
                        if (score >= min && score <= max) {
                            iter.remove();
                            removed++;
                        }
                    }
                    return removed;
                } finally {
                    lock.unlock();
                }
            }

            void add(long score, String member) {
                lock.lock();
                try { entries.put(score, member); } finally { lock.unlock(); }
            }

            int size() { lock.lock(); try { return entries.size(); } finally { lock.unlock(); } }
        }

        final RedisSortedSet store = new RedisSortedSet();
        final long limit;
        final long windowMs;

        RedisRateLimiter(long limit, long windowSec) {
            this.limit = limit;
            this.windowMs = windowSec * 1000L;
        }

        boolean allow(String clientId) {
            long now = System.currentTimeMillis();
            long cutoff = now - windowMs;
            store.removeRangeByScore(0, cutoff);
            if (store.size() < limit) {
                store.add(now, clientId + ":" + now + ":" + ThreadLocalRandom.current().nextInt());
                return true;
            }
            return false;
        }
    }

    // ──────────────────────────────────────────────
    // Benchmark
    // ──────────────────────────────────────────────
    static void benchmark() {
        System.out.println("=== Rate Limiter Benchmark ===\n");
        int requests = 100_000;
        int workers = 10;

        record Result(String name, long accepted, long rejected, long timeMs) {}

        Result runTest(String name, Object limiter, java.util.function.Predicate<Object> allowFn) {
            var executor = Executors.newFixedThreadPool(workers);
            var accepted = new AtomicInteger();
            var rejected = new AtomicInteger();
            long start = System.nanoTime();
            var futures = new java.util.ArrayList<Future<?>>();
            for (int i = 0; i < workers; i++) {
                futures.add(executor.submit(() -> {
                    for (int j = 0; j < requests / workers; j++) {
                        if (allowFn.test(limiter)) accepted.incrementAndGet();
                        else rejected.incrementAndGet();
                    }
                }));
            }
            futures.forEach(f -> { try { f.get(); } catch (Exception e) {} });
            long time = (System.nanoTime() - start) / 1_000_000;
            executor.shutdown();
            return new Result(name, accepted.get(), rejected.get(), time);
        }

        var tb = new TokenBucket(100, 100);
        var lb = new LeakyBucket(100, 100);
        var fw = new FixedWindowCounter(100, 1);
        var sw = new SlidingWindowLog(100, 1);

        var results = List.of(
                runTest("Token Bucket", tb, o -> ((TokenBucket) o).allow()),
                runTest("Leaky Bucket", lb, o -> ((LeakyBucket) o).allow()),
                runTest("Fixed Window", fw, o -> ((FixedWindowCounter) o).allow()),
                runTest("Sliding Log ", sw, o -> ((SlidingWindowLog) o).allow())
        );

        System.out.printf("%-15s %10s %10s %10s%n", "Algorithm", "Accepted", "Rejected", "Time(ms)");
        System.out.println("-".repeat(50));
        for (var r : results) {
            System.out.printf("%-15s %10d %10d %10d%n", r.name, r.accepted, r.rejected, r.timeMs);
        }
        System.out.println();
    }

    // ──────────────────────────────────────────────
    // Main
    // ──────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║  Lab 03: Rate Limiting Deep-Dive            ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        // Token Bucket
        System.out.println("1. Token Bucket (capacity=5, refill=2/sec)");
        var tb = new TokenBucket(5, 2);
        for (int i = 0; i < 8; i++) {
            System.out.println("  Request " + (i + 1) + ": " + (tb.allow() ? "ALLOWED" : "DENIED")
                    + " (tokens: " + tb.availableTokens() + ")");
            sleep(100);
        }
        System.out.println();

        // Leaky Bucket
        System.out.println("2. Leaky Bucket (capacity=5, leak=1/sec)");
        var lb = new LeakyBucket(5, 1);
        for (int i = 0; i < 8; i++) {
            System.out.println("  Request " + (i + 1) + ": " + (lb.allow() ? "ALLOWED" : "DENIED")
                    + " (water: " + lb.currentWater() + ")");
            sleep(50);
        }
        System.out.println();

        // Fixed Window
        System.out.println("3. Fixed Window (max=3, window=2s)");
        var fw = new FixedWindowCounter(3, 2);
        for (int i = 0; i < 5; i++) {
            System.out.println("  Request " + (i + 1) + ": " + (fw.allow() ? "ALLOWED" : "DENIED"));
            sleep(100);
        }
        System.out.println();

        // Sliding Window Log
        System.out.println("4. Sliding Window Log (max=3, window=2s)");
        var sw = new SlidingWindowLog(3, 2);
        for (int i = 0; i < 5; i++) {
            System.out.println("  Request " + (i + 1) + ": " + (sw.allow() ? "ALLOWED" : "DENIED")
                    + " (recent: " + sw.recentCount() + ")");
            sleep(100);
        }
        System.out.println();

        // Distributed Rate Limiting (Redis simulation)
        System.out.println("5. Redis-based Distributed Rate Limiter (limit=5, window=5s)");
        var redisLimiter = new RedisRateLimiter(5, 5);
        for (int i = 0; i < 8; i++) {
            System.out.println("  Request " + (i + 1) + ": " + (redisLimiter.allow("client-A") ? "ALLOWED" : "DENIED"));
            sleep(50);
        }
        System.out.println();

        // Benchmark
        benchmark();

        System.out.println("All rate limiting algorithms demonstrated successfully.");
    }

    static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
