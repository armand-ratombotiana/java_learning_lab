package com.arch.circuitbreaker;

import java.time.Duration;
import java.util.function.Supplier;

public class ResilientService {
    private final CircuitBreakerStateMachine circuitBreaker;
    private final BulkheadIsolation bulkhead;
    private final int maxRetries;
    private final Duration retryDelay;

    public ResilientService(int failureThreshold, Duration waitDuration, int maxConcurrent, int maxRetries, Duration retryDelay) {
        this.circuitBreaker = new CircuitBreakerStateMachine(failureThreshold, 3, waitDuration);
        this.bulkhead = new BulkheadIsolation(maxConcurrent, 100);
        this.maxRetries = maxRetries;
        this.retryDelay = retryDelay;
    }

    public <T> T execute(String operationName, Supplier<T> operation) {
        if (!circuitBreaker.isCallAllowed()) {
            throw new CircuitBreakerException("Circuit breaker is OPEN for " + operationName);
        }
        return bulkhead.callWithSemaphore(operationName, () -> {
            Exception lastException = null;
            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                try {
                    T result = operation.get();
                    circuitBreaker.onSuccess();
                    return result;
                } catch (Exception e) {
                    lastException = e;
                    circuitBreaker.onFailure();
                    if (attempt < maxRetries) {
                        try { Thread.sleep(retryDelay.toMillis() * (attempt + 1)); }
                        catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
                    }
                }
            }
            throw new CircuitBreakerException("Operation failed after " + maxRetries + " retries", lastException);
        });
    }
}

class CircuitBreakerException extends RuntimeException {
    public CircuitBreakerException(String message) { super(message); }
    public CircuitBreakerException(String message, Throwable cause) { super(message, cause); }
}
