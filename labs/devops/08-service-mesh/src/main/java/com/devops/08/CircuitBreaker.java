package com.devops.servicemesh;

import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;
    private final int failureThreshold;
    private final AtomicInteger failureCount = new AtomicInteger(0);

    public CircuitBreaker(int failureThreshold) {
        this.failureThreshold = failureThreshold;
    }

    public synchronized boolean allowRequest() {
        if (state == State.OPEN) {
            System.out.println("Circuit OPEN - request blocked");
            return false;
        }
        return true;
    }

    public synchronized void recordFailure() {
        int failures = failureCount.incrementAndGet();
        System.out.println("Failure count: " + failures + "/" + failureThreshold);
        if (failures >= failureThreshold) {
            state = State.OPEN;
            System.out.println("Circuit OPENED due to failures");
        }
    }

    public synchronized void recordSuccess() {
        failureCount.set(0);
        if (state == State.HALF_OPEN) {
            state = State.CLOSED;
            System.out.println("Circuit CLOSED after success");
        }
    }

    public static void main(String[] args) {
        CircuitBreaker cb = new CircuitBreaker(3);
        for (int i = 0; i < 5; i++) {
            if (cb.allowRequest()) {
                if (i < 3) {
                    cb.recordFailure();
                } else {
                    cb.recordSuccess();
                }
            }
        }
    }
}
