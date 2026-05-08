package com.learning.vertx.circuitbreaker;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class CircuitBreakerLab {

    enum State { CLOSED, OPEN, HALF_OPEN }

    static class CircuitBreaker {
        private volatile State state = State.CLOSED;
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicInteger successCount = new AtomicInteger(0);
        private final int failureThreshold;
        private final int successThreshold;
        private final long timeoutMs;
        private volatile long lastFailureTime;
        private final AtomicLong totalCalls = new AtomicLong(0);
        private final AtomicLong rejectedCalls = new AtomicLong(0);

        public CircuitBreaker(int failureThreshold, int successThreshold, long timeoutMs) {
            this.failureThreshold = failureThreshold;
            this.successThreshold = successThreshold;
            this.timeoutMs = timeoutMs;
        }

        public synchronized <T> T call(Callable<T> operation, Runnable fallback) {
            totalCalls.incrementAndGet();
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
                    System.out.println("    [CB] Timeout elapsed, transitioning to HALF_OPEN");
                    state = State.HALF_OPEN;
                    successCount.set(0);
                } else {
                    rejectedCalls.incrementAndGet();
                    System.out.println("    [CB] OPEN - rejecting request");
                    if (fallback != null) fallback.run();
                    return null;
                }
            }

            try {
                T result = operation.call();
                if (state == State.HALF_OPEN) {
                    int successes = successCount.incrementAndGet();
                    if (successes >= successThreshold) {
                        System.out.println("    [CB] HALF_OPEN -> CLOSED (threshold met)");
                        state = State.CLOSED;
                        failureCount.set(0);
                    }
                } else {
                    failureCount.set(0);
                }
                return result;
            } catch (Exception e) {
                int failures = failureCount.incrementAndGet();
                lastFailureTime = System.currentTimeMillis();
                System.out.println("    [CB] Failure #" + failures + ": " + e.getMessage());

                if (state == State.HALF_OPEN || failures >= failureThreshold) {
                    System.out.println("    [CB] -> OPEN (failure threshold=" + failureThreshold + ")");
                    state = State.OPEN;
                }
                if (fallback != null) fallback.run();
                return null;
            }
        }

        public String getStatus() {
            return String.format("State=%s, failures=%d, successes=%d, total=%d, rejected=%d",
                state, failureCount.get(), successCount.get(), totalCalls.get(), rejectedCalls.get());
        }
    }

    static class SlowService {
        private final AtomicInteger callCount = new AtomicInteger(0);

        public String call() {
            int n = callCount.incrementAndGet();
            if (n <= 3) throw new RuntimeException("Service timeout (simulated)");
            if (n <= 5) { sleep(50); return "Degraded response"; }
            return "Full response #" + n;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Circuit Breaker Pattern Lab ===\n");

        basicCircuitBreaker();
        halfOpenProbing();
        fallbackDemo();
        metricsAndMonitoring();
    }

    static void basicCircuitBreaker() {
        System.out.println("--- Basic Circuit Breaker (3 failures -> OPEN -> timeout -> HALF_OPEN) ---");
        CircuitBreaker cb = new CircuitBreaker(3, 2, 500);
        SlowService service = new SlowService();

        for (int i = 0; i < 10; i++) {
            System.out.print("  Call #" + (i + 1) + ": ");
            cb.call(() -> service.call(), () -> System.out.println("    [Fallback] Using cached response"));
            sleep(100);
        }
        System.out.println("  Final: " + cb.getStatus());
    }

    static void halfOpenProbing() throws Exception {
        System.out.println("\n--- Half-Open Probing ---");
        CircuitBreaker cb = new CircuitBreaker(2, 2, 300);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < 8; i++) {
            int attempt = i + 1;
            cb.call(() -> {
                int n = counter.incrementAndGet();
                if (n < 3) throw new RuntimeException("Fail #" + n);
                return "Success #" + n;
            }, () -> {});
            Thread.sleep(50);
        }

        System.out.println("  Half-open allows probe requests through");
        System.out.println("  Status: " + cb.getStatus());
    }

    static void fallbackDemo() {
        System.out.println("\n--- Fallback Strategies ---");
        CircuitBreaker cb = new CircuitBreaker(2, 1, 200);
        AtomicInteger fallbackCount = new AtomicInteger(0);

        for (int i = 0; i < 5; i++) {
            cb.call(() -> { throw new RuntimeException("Downstream failure"); },
                () -> System.out.println("    [Fallback #" + fallbackCount.incrementAndGet()
                    + "] Serving stale data from cache"));
        }
    }

    static void metricsAndMonitoring() {
        System.out.println("\n--- Metrics & Monitoring ---");
        CircuitBreaker cb = new CircuitBreaker(2, 1, 100);

        for (int i = 0; i < 6; i++) {
            int attempt = i + 1;
            cb.call(() -> {
                if (attempt <= 2) throw new RuntimeException("Fail");
                return "OK";
            }, () -> {});
            sleep(200);
        }

        System.out.println("  " + cb.getStatus());
        System.out.println("  Metrics enable SLAs, dashboards, and auto-scaling decisions");
    }

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
