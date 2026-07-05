package com.sd.availability;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class CircuitBreaker {

    public enum State { CLOSED, OPEN, HALF_OPEN }

    public static class CircuitBreakerConfig {
        public final int failureThreshold;
        public final long timeoutMs;
        public final int halfOpenMaxRequests;

        public CircuitBreakerConfig(int failureThreshold, long timeoutMs, int halfOpenMaxRequests) {
            this.failureThreshold = failureThreshold;
            this.timeoutMs = timeoutMs;
            this.halfOpenMaxRequests = halfOpenMaxRequests;
        }
    }

    public static class CircuitBreakerInstance {
        private final String name;
        private final CircuitBreakerConfig config;
        private State state;
        private final AtomicInteger failureCount;
        private final AtomicInteger halfOpenRequests;
        private long lastFailureTime;

        public CircuitBreakerInstance(String name, CircuitBreakerConfig config) {
            this.name = name;
            this.config = config;
            this.state = State.CLOSED;
            this.failureCount = new AtomicInteger(0);
            this.halfOpenRequests = new AtomicInteger(0);
        }

        public synchronized boolean isAvailable() {
            if (state == State.CLOSED) return true;
            if (state == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime > config.timeoutMs) {
                    state = State.HALF_OPEN;
                    halfOpenRequests.set(0);
                    System.out.println("[" + name + "] Half-open -> allowing trial requests");
                    return true;
                }
                return false;
            }
            return halfOpenRequests.incrementAndGet() <= config.halfOpenMaxRequests;
        }

        public synchronized void onSuccess() {
            if (state == State.HALF_OPEN) {
                state = State.CLOSED;
                failureCount.set(0);
                System.out.println("[" + name + "] Closed after successful trial");
            }
        }

        public synchronized void onFailure() {
            lastFailureTime = System.currentTimeMillis();
            if (state == State.HALF_OPEN) {
                state = State.OPEN;
                System.out.println("[" + name + "] Re-opened after trial failure");
            } else {
                int failures = failureCount.incrementAndGet();
                System.out.println("[" + name + "] Failure " + failures + "/" + config.failureThreshold);
                if (failures >= config.failureThreshold) {
                    state = State.OPEN;
                    System.out.println("[" + name + "] OPEN - circuit tripped");
                }
            }
        }

        public State getState() { return state; }
    }

    public static class CircuitBreakerProxy {
        private final CircuitBreakerInstance breaker;

        public CircuitBreakerProxy(CircuitBreakerInstance breaker) {
            this.breaker = breaker;
        }

        public String call(String request) {
            if (!breaker.isAvailable()) {
                return "FALLBACK: " + request + " (circuit open)";
            }
            try {
                if (Math.random() > 0.4) {
                    breaker.onSuccess();
                    return "SUCCESS: " + request;
                }
                throw new RuntimeException("Service error");
            } catch (Exception e) {
                breaker.onFailure();
                return "FALLBACK: " + request + " (error: " + e.getMessage() + ")";
            }
        }
    }

    public static void main(String[] args) {
        CircuitBreakerConfig config = new CircuitBreakerConfig(3, 2000, 2);
        CircuitBreakerInstance breaker = new CircuitBreakerInstance("user-service", config);
        CircuitBreakerProxy proxy = new CircuitBreakerProxy(breaker);

        System.out.println("=== Circuit Breaker ===");
        for (int i = 0; i < 10; i++) {
            String result = proxy.call("req-" + i);
            System.out.println("  " + result + " | State: " + breaker.getState());
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
    }
}
