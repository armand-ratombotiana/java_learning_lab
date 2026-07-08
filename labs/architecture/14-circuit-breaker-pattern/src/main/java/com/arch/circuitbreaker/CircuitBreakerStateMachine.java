package com.arch.circuitbreaker;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CircuitBreakerStateMachine {
    public enum State { CLOSED, OPEN, HALF_OPEN }

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final int failureThreshold;
    private final int successThreshold;
    private final Duration waitDuration;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private volatile Instant lastFailureTime;
    private volatile Instant stateChangedAt;

    public CircuitBreakerStateMachine(int failureThreshold, int successThreshold, Duration waitDuration) {
        this.failureThreshold = failureThreshold;
        this.successThreshold = successThreshold;
        this.waitDuration = waitDuration;
        this.stateChangedAt = Instant.now();
    }

    public boolean isCallAllowed() {
        State currentState = state.get();
        return switch (currentState) {
            case CLOSED -> true;
            case OPEN -> {
                if (Duration.between(stateChangedAt, Instant.now()).compareTo(waitDuration) >= 0) {
                    state.compareAndSet(State.OPEN, State.HALF_OPEN);
                    stateChangedAt = Instant.now();
                    yield true;
                }
                yield false;
            }
            case HALF_OPEN -> true;
        };
    }

    public void onSuccess() {
        State currentState = state.get();
        switch (currentState) {
            case CLOSED -> failureCount.set(0);
            case HALF_OPEN -> {
                if (successCount.incrementAndGet() >= successThreshold) {
                    reset();
                }
            }
        }
    }

    public void onFailure() {
        State currentState = state.get();
        switch (currentState) {
            case CLOSED -> {
                if (failureCount.incrementAndGet() >= failureThreshold) {
                    state.set(State.OPEN);
                    stateChangedAt = Instant.now();
                }
                lastFailureTime = Instant.now();
            }
            case HALF_OPEN -> {
                state.set(State.OPEN);
                stateChangedAt = Instant.now();
                successCount.set(0);
            }
        }
    }

    private void reset() {
        state.set(State.CLOSED);
        failureCount.set(0);
        successCount.set(0);
        stateChangedAt = Instant.now();
    }

    public State getState() { return state.get(); }
    public int getFailureCount() { return failureCount.get(); }
    public Instant getLastFailureTime() { return lastFailureTime; }
}
