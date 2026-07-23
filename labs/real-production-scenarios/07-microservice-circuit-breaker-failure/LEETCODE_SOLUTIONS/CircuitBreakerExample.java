package com.prod.solutions.circuitbreaker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.time.Instant;

/**
 * Custom circuit breaker implementation with three states: CLOSED, OPEN, HALF_OPEN.
 *
 * CLOSED → OPEN when failure threshold exceeded
 * OPEN → HALF_OPEN after timeout period
 * HALF_OPEN → CLOSED if test request succeeds, → OPEN if it fails
 */
public class CircuitBreakerExample {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    static class CircuitBreaker {
        private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
        private final int failureThreshold;
        private final long timeoutMs;
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private Instant lastFailureTime;
        private final String name;

        public CircuitBreaker(String name, int failureThreshold, long timeoutMs) {
            this.name = name;
            this.failureThreshold = failureThreshold;
            this.timeoutMs = timeoutMs;
        }

        public boolean allowRequest() {
            State currentState = state.get();
            switch (currentState) {
                case CLOSED:
                    return true;
                case OPEN:
                    if (Instant.now().isAfter(lastFailureTime.plusMillis(timeoutMs))) {
                        if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
                            System.out.printf("[%s] OPEN → HALF_OPEN (timeout elapsed)%n", name);
                            return true;
                        }
                    }
                    return false;
                case HALF_OPEN:
                    return true; // Allow one test request
                default:
                    return false;
            }
        }

        public void onSuccess() {
            State currentState = state.get();
            if (currentState == State.HALF_OPEN) {
                if (state.compareAndSet(State.HALF_OPEN, State.CLOSED)) {
                    System.out.printf("[%s] HALF_OPEN → CLOSED (test request succeeded)%n", name);
                    failureCount.set(0);
                }
            } else if (currentState == State.CLOSED) {
                failureCount.set(0); // Reset on consecutive success
            }
        }

        public void onFailure() {
            if (state.get() == State.HALF_OPEN) {
                System.out.printf("[%s] HALF_OPEN → OPEN (test request failed)%n", name);
                state.set(State.OPEN);
                lastFailureTime = Instant.now();
                return;
            }

            int failures = failureCount.incrementAndGet();
            System.out.printf("[%s] Failure %d/%d%n", name, failures, failureThreshold);

            if (failures >= failureThreshold) {
                if (state.compareAndSet(State.CLOSED, State.OPEN)) {
                    System.out.printf("[%s] CLOSED → OPEN (threshold reached)%n", name);
                    lastFailureTime = Instant.now();
                }
            }
        }

        public State getState() { return state.get(); }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Circuit Breaker Demo ===");

        CircuitBreaker cb = new CircuitBreaker("payment-service", 3, 2000);

        // Simulate failures
        System.out.println("\n--- Sending failures to trip circuit breaker ---");
        for (int i = 1; i <= 5; i++) {
            if (cb.allowRequest()) {
                cb.onFailure();
            } else {
                System.out.printf("[payment-service] Request %d REJECTED (circuit OPEN)%n", i);
            }
            Thread.sleep(100);
        }

        // Wait for timeout
        System.out.println("\n--- Waiting for circuit breaker timeout (2s) ---");
        Thread.sleep(2000);

        // Test request (HALF_OPEN)
        System.out.println("\n--- Sending test request ---");
        if (cb.allowRequest()) {
            cb.onSuccess(); // Test succeeds
        }

        System.out.printf("%nFinal circuit state: %s%n", cb.getState());
        System.out.println("\nCircuit breaker prevented cascading failures.");
    }
}
