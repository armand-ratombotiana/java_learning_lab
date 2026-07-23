// RateLimiterExample.java — Bucket4j Rate Limiter
// Demonstrates token bucket rate limiting for backend APIs

package com.backend.academy.leetcode;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/ratelimited")
public class RateLimiterExample {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Per-user rate limit: 100 requests per minute
    private static final int REQUESTS_PER_MINUTE = 100;
    // Premium users: 500 requests per minute
    private static final int PREMIUM_REQUESTS_PER_MINUTE = 500;

    /**
     * Endpoint with per-IP rate limiting using token bucket algorithm.
     */
    @GetMapping("/data")
    public ResponseEntity<String> getData(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Bucket bucket = resolveBucket(clientIp, false);

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok("Request allowed at " + System.currentTimeMillis());
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .header("X-RateLimit-Limit", String.valueOf(REQUESTS_PER_MINUTE))
            .header("X-RateLimit-Remaining", "0")
            .header("Retry-After", "60")
            .body("Rate limit exceeded. Try again later.");
    }

    /**
     * Premium endpoint with higher rate limit.
     */
    @GetMapping("/premium-data")
    public ResponseEntity<String> getPremiumData(@RequestParam String apiKey) {
        Bucket bucket = resolveBucket(apiKey, true);

        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok("Premium request allowed");
        }
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Premium rate limit exceeded");
    }

    private Bucket resolveBucket(String key, boolean isPremium) {
        return buckets.computeIfAbsent(key, k -> createBucket(isPremium));
    }

    private Bucket createBucket(boolean isPremium) {
        int capacity = isPremium ? PREMIUM_REQUESTS_PER_MINUTE : REQUESTS_PER_MINUTE;
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(1)));
        return Bucket4j.builder().addLimit(limit).build();
    }

    /**
     * Sliding window rate limiter alternative.
     * LeetCode-style: implement a sliding window rate limiter.
     */
    static class SlidingWindowRateLimiter {
        private final Map<String, java.util.Deque<Long>> requestLogs = new ConcurrentHashMap<>();
        private final int maxRequests;
        private final long windowMillis;

        public SlidingWindowRateLimiter(int maxRequests, long windowSeconds) {
            this.maxRequests = maxRequests;
            this.windowMillis = windowSeconds * 1000;
        }

        public synchronized boolean allowRequest(String userId) {
            long now = System.currentTimeMillis();
            requestLogs.putIfAbsent(userId, new java.util.LinkedList<>());
            java.util.Deque<Long> timestamps = requestLogs.get(userId);

            // Remove timestamps outside the window
            while (!timestamps.isEmpty() && now - timestamps.peekFirst() > windowMillis) {
                timestamps.pollFirst();
            }

            if (timestamps.size() < maxRequests) {
                timestamps.offerLast(now);
                return true;
            }
            return false;
        }
    }
}

// === LeetCode-Style Problems ===

/*
 * LeetCode 353: Design Snake Game (movement-based rate limiting analogy)
 */
class SnakeGame {
    private int width, height;
    private int[][] food;
    private int foodIndex;
    private java.util.Deque<int[]> snake;
    private java.util.Set<String> snakeSet;
    private int score;

    public SnakeGame(int width, int height, int[][] food) {
        this.width = width;
        this.height = height;
        this.food = food;
        this.foodIndex = 0;
        this.snake = new java.util.LinkedList<>();
        this.snakeSet = new java.util.HashSet<>();
        this.snake.offerFirst(new int[]{0, 0});
        this.snakeSet.add("0,0");
        this.score = 0;
    }

    public int move(String direction) {
        int[] head = snake.peekFirst();
        int row = head[0], col = head[1];

        switch (direction) {
            case "U" -> row--;
            case "D" -> row++;
            case "L" -> col--;
            case "R" -> col++;
        }

        // Check bounds
        if (row < 0 || row >= height || col < 0 || col >= width) return -1;

        // Check if food is available
        if (foodIndex < food.length && food[foodIndex][0] == row && food[foodIndex][1] == col) {
            foodIndex++;
            score++;
        } else {
            // Remove tail
            int[] tail = snake.pollLast();
            snakeSet.remove(tail[0] + "," + tail[1]);
        }

        // Check if new head collides with body
        if (snakeSet.contains(row + "," + col)) return -1;

        snake.offerFirst(new int[]{row, col});
        snakeSet.add(row + "," + col);
        return score;
    }
}

/*
 * LeetCode 362: Design Hit Counter
 */
class HitCounter {
    private java.util.Queue<Integer> hits;

    public HitCounter() {
        this.hits = new java.util.LinkedList<>();
    }

    public void hit(int timestamp) {
        hits.offer(timestamp);
    }

    public int getHits(int timestamp) {
        while (!hits.isEmpty() && timestamp - hits.peek() >= 300) {
            hits.poll();
        }
        return hits.size();
    }
}

/*
 * LeetCode 460: LFU Cache
 */
class LFUCache {
    private int capacity;
    private int minFreq;
    private Map<Integer, Integer> keyToVal;
    private Map<Integer, Integer> keyToFreq;
    private Map<Integer, java.util.LinkedHashSet<Integer>> freqToKeys;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 1;
        this.keyToVal = new java.util.HashMap<>();
        this.keyToFreq = new java.util.HashMap<>();
        this.freqToKeys = new java.util.HashMap<>();
        this.freqToKeys.put(1, new java.util.LinkedHashSet<>());
    }

    public int get(int key) {
        if (!keyToVal.containsKey(key)) return -1;
        increaseFreq(key);
        return keyToVal.get(key);
    }

    public void put(int key, int value) {
        if (capacity <= 0) return;
        if (keyToVal.containsKey(key)) {
            keyToVal.put(key, value);
            increaseFreq(key);
            return;
        }
        if (keyToVal.size() >= capacity) {
            evict();
        }
        keyToVal.put(key, value);
        keyToFreq.put(key, 1);
        freqToKeys.computeIfAbsent(1, k -> new java.util.LinkedHashSet<>()).add(key);
        minFreq = 1;
    }

    private void increaseFreq(int key) {
        int freq = keyToFreq.get(key);
        freqToKeys.get(freq).remove(key);
        if (freqToKeys.get(freq).isEmpty() && freq == minFreq) {
            minFreq++;
        }
        keyToFreq.put(key, freq + 1);
        freqToKeys.computeIfAbsent(freq + 1, k -> new java.util.LinkedHashSet<>()).add(key);
    }

    private void evict() {
        java.util.LinkedHashSet<Integer> keys = freqToKeys.get(minFreq);
        int keyToEvict = keys.iterator().next();
        keys.remove(keyToEvict);
        keyToVal.remove(keyToEvict);
        keyToFreq.remove(keyToEvict);
    }
}
