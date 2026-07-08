package com.arch.circuitbreaker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

class CircuitBreakerStateMachineTest {
    @Test
    void shouldStartInClosedState() {
        CircuitBreakerStateMachine cb = new CircuitBreakerStateMachine(3, 2, Duration.ofSeconds(5));
        assertEquals(CircuitBreakerStateMachine.State.CLOSED, cb.getState());
    }

    @Test
    void shouldOpenAfterFailures() {
        CircuitBreakerStateMachine cb = new CircuitBreakerStateMachine(3, 2, Duration.ofSeconds(5));
        assertTrue(cb.isCallAllowed());
        cb.onFailure(); cb.onFailure(); cb.onFailure();
        assertEquals(CircuitBreakerStateMachine.State.OPEN, cb.getState());
    }

    @Test
    void shouldRejectCallsWhenOpen() {
        CircuitBreakerStateMachine cb = new CircuitBreakerStateMachine(2, 2, Duration.ofSeconds(60));
        cb.onFailure(); cb.onFailure();
        assertFalse(cb.isCallAllowed());
    }

    @Test
    void shouldResetOnSuccess() {
        CircuitBreakerStateMachine cb = new CircuitBreakerStateMachine(3, 2, Duration.ofSeconds(5));
        cb.onFailure(); cb.onFailure();
        cb.onSuccess();
        assertEquals(0, cb.getFailureCount());
    }
}

class BulkheadIsolationTest {
    @Test
    void shouldExecuteUnderBulkhead() {
        BulkheadIsolation bulkhead = new BulkheadIsolation(5, 100);
        String result = bulkhead.callWithSemaphore("test", () -> "done");
        assertEquals("done", result);
    }
}
