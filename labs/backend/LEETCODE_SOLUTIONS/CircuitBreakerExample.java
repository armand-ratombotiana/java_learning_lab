// CircuitBreakerExample.java — Resilience4j Circuit Breaker
// Demonstrates circuit breaker pattern for fault-tolerant microservices

package com.backend.academy.leetcode;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.decorators.Decorators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.function.Supplier;

@Service
public class CircuitBreakerExample {
    private static final Logger log = LoggerFactory.getLogger(CircuitBreakerExample.class);
    private final RestTemplate restTemplate;
    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerExample() {
        this.restTemplate = new RestTemplate();

        // Configure circuit breaker
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .slidingWindowSize(10)                    // Count failures in last 10 calls
            .failureRateThreshold(50)                   // Open at 50% failure rate
            .waitDurationInOpenState(Duration.ofSeconds(10))  // Wait 10s before half-open
            .permittedNumberOfCallsInHalfOpenState(3)   // Test with 3 calls in half-open
            .automaticTransitionFromOpenToHalfOpenEnabled(true)
            .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
            .minimumNumberOfCalls(5)                    // Min calls before calculating rate
            .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        this.circuitBreaker = registry.circuitBreaker("externalService");
    }

    /**
     * Calls external API with circuit breaker protection.
     * LeetCode-style: design a fault-tolerant API caller.
     */
    public String callExternalService() {
        Supplier<String> decoratedSupplier = Decorators.ofSupplier(() -> {
            log.info("Calling external service...");
            return restTemplate.getForObject(
                "https://api.example.com/v1/data", String.class);
        })
        .withCircuitBreaker(circuitBreaker)
        .withFallback(throwable -> {
            log.warn("External service failed, returning cached data: {}", throwable.getMessage());
            return getCachedData();
        })
        .decorate();

        return decoratedSupplier.get();
    }

    /**
     * Monitored call with metrics tracking.
     */
    public String monitoredCall() {
        // Record custom metrics for circuit breaker state
        CircuitBreaker.State state = circuitBreaker.getState();
        log.info("Circuit breaker state: {}", state);

        return switch (state) {
            case OPEN -> {
                log.warn("Circuit is OPEN, using fallback immediately");
                yield getCachedData();
            }
            case HALF_OPEN -> {
                log.info("Circuit is HALF_OPEN, testing external service");
                yield callExternalService();
            }
            case CLOSED -> {
                log.info("Circuit is CLOSED, normal operation");
                yield callExternalService();
            }
            case METRICS_ONLY -> {
                log.info("Metrics only mode");
                yield callExternalService();
            }
            case FORCED_OPEN -> {
                log.warn("Circuit is FORCED_OPEN, bypassing");
                yield getCachedData();
            }
            case DISABLED -> callExternalService();
        };
    }

    private String getCachedData() {
        return "{\"data\": \"cached-response\", \"source\": \"local-cache\"}";
    }

    /**
     * Example of custom event listeners for state transitions.
     */
    public void registerEventListeners() {
        circuitBreaker.getEventPublisher()
            .onSuccess(event -> log.info("Call succeeded: {}", event.getElapsedDuration()))
            .onError(event -> log.error("Call failed: {}", event.getThrowable().getMessage()))
            .onStateTransition(event -> log.warn("State transition: {} -> {}",
                event.getOldState(), event.getNewState()))
            .onCallNotPermitted(event -> log.warn("Call not permitted, circuit is OPEN"));
    }

    public CircuitBreaker.State getState() {
        return circuitBreaker.getState();
    }
}

// === LeetCode-Style Problem ===
/*
 * LeetCode 362: Design Hit Counter (with circuit breaker resilience)
 *
 * Design a hit counter that counts the number of hits received in the past 5 minutes.
 * The system should be resilient to downstream failures.
 */
class HitCounterWithResilience {
    private final java.util.Queue<Integer> hits;
    private static final int WINDOW_SECONDS = 300;

    public HitCounterWithResilience() {
        this.hits = new java.util.LinkedList<>();
    }

    public synchronized void hit(int timestamp) {
        cleanup(timestamp);
        hits.offer(timestamp);
    }

    public synchronized int getHits(int timestamp) {
        cleanup(timestamp);
        return hits.size();
    }

    private void cleanup(int timestamp) {
        while (!hits.isEmpty() && timestamp - hits.peek() >= WINDOW_SECONDS) {
            hits.poll();
        }
    }
}

/*
 * LeetCode 139: Word Break (dynamic programming)
 * Analogy: circuit breaker state transitions
 */
class WordBreak {
    public boolean wordBreak(String s, java.util.List<String> wordDict) {
        java.util.Set<String> dict = new java.util.HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && dict.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }
        return dp[s.length()];
    }
}

/*
 * LeetCode 146: LRU Cache
 * Cache implementation with eviction policy (like circuit breaker's cached fallback)
 */
class LRUCache {
    private final int capacity;
    private final java.util.LinkedHashMap<Integer, Integer> cache;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new java.util.LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    public int get(int key) {
        return cache.getOrDefault(key, -1);
    }

    public void put(int key, int value) {
        cache.put(key, value);
    }
}
