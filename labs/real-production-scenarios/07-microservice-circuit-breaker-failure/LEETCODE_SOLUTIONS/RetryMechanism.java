package com.prod.solutions.circuitbreaker;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Demonstrates exponential backoff retry with jitter for resilient
 * service-to-service communication. This prevents retry storms during
 * transient failures.
 *
 * FIX: Instead of immediate retries (which overwhelm the failing service),
 * use exponential backoff with random jitter to spread retry attempts.
 */
public class RetryMechanism {

    @FunctionalInterface
    interface RetryableOperation<T> {
        T execute() throws Exception;
    }

    static class RetryConfig {
        final int maxRetries;
        final long baseDelayMs;
        final long maxDelayMs;

        RetryConfig(int maxRetries, long baseDelayMs, long maxDelayMs) {
            this.maxRetries = maxRetries;
            this.baseDelayMs = baseDelayMs;
            this.maxDelayMs = maxDelayMs;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Retry with Exponential Backoff Demo ===\n");

        RetryConfig config = new RetryConfig(3, 100, 2000);

        // Simulate flaky service
        Supplier<String> flakyService = () -> {
            if (ThreadLocalRandom.current().nextDouble() < 0.6) {
                throw new RuntimeException("Service temporarily unavailable");
            }
            return "Success!";
        };

        // Without retry
        System.out.println("--- Without retry ---");
        try {
            String result = flakyService.get();
            System.out.printf("Result: %s%n", result);
        } catch (Exception e) {
            System.out.printf("Failed: %s%n", e.getMessage());
        }

        // With retry
        System.out.println("\n--- With exponential backoff retry ---");
        try {
            String result = executeWithRetry(flakyService::get, config);
            System.out.printf("Result: %s%n", result);
        } catch (Exception e) {
            System.out.printf("All retries exhausted: %s%n", e.getMessage());
        }

        // Show backoff values
        System.out.println("\n--- Backoff sequence for config ---");
        System.out.printf("Max retries: %d, Base delay: %dms, Max delay: %dms%n",
                config.maxRetries, config.baseDelayMs, config.maxDelayMs);
        for (int attempt = 1; attempt <= config.maxRetries; attempt++) {
            long delay = computeBackoffWithJitter(attempt, config);
            System.out.printf("  Retry %d: delay = %d ms%n", attempt, delay);
        }

        System.out.println("\nWithout jitter: all retries happen simultaneously (retry storm).");
        System.out.println("With jitter:    retries are spread across the window.");
    }

    static <T> T executeWithRetry(RetryableOperation<T> operation,
                                   RetryConfig config) throws Exception {
        Exception lastException = null;
        for (int attempt = 0; attempt <= config.maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    long delay = computeBackoffWithJitter(attempt, config);
                    System.out.printf("  Retry attempt %d/%d (waiting %d ms)%n",
                            attempt, config.maxRetries, delay);
                    Thread.sleep(delay);
                }
                return operation.execute();
            } catch (Exception e) {
                lastException = e;
                System.out.printf("  Attempt %d failed: %s%n", attempt + 1, e.getMessage());
            }
        }
        throw lastException;
    }

    static long computeBackoffWithJitter(int attempt, RetryConfig config) {
        long exponentialDelay = config.baseDelayMs * (long) Math.pow(2, attempt - 1);
        long clampedDelay = Math.min(exponentialDelay, config.maxDelayMs);
        // Add random jitter: ±25% of the delay
        double jitter = 0.75 + ThreadLocalRandom.current().nextDouble() * 0.5;
        return (long) (clampedDelay * jitter);
    }
}
