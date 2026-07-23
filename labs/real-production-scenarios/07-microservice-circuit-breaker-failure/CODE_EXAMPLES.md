# Lab 07 — Circuit Breaker: Code Examples

## Circuit Breaker Implementation

```java
import java.time.*;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

public class CircuitBreaker<T> {
    private final String name;
    private final int failureThreshold;
    private final int halfOpenMaxCalls;
    private final Duration waitDuration;
    private final Duration timeout;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger callCount = new AtomicInteger(0);
    private final AtomicInteger halfOpenCallCount = new AtomicInteger(0);
    private final AtomicInteger halfOpenSuccessCount = new AtomicInteger(0);
    private volatile State state = State.CLOSED;
    private volatile Instant lastFailureTime;
    private final Object stateLock = new Object();

    public enum State { CLOSED, OPEN, HALF_OPEN }

    public CircuitBreaker(String name, int failureThreshold, int halfOpenMaxCalls,
                          Duration waitDuration, Duration timeout) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.halfOpenMaxCalls = halfOpenMaxCalls;
        this.waitDuration = waitDuration;
        this.timeout = timeout;
    }

    public T call(Supplier<T> supplier, Supplier<T> fallback) {
        if (state == State.OPEN) {
            if (Duration.between(lastFailureTime, Instant.now()).compareTo(waitDuration) > 0) {
                synchronized (stateLock) {
                    if (state == State.OPEN) {
                        state = State.HALF_OPEN;
                        halfOpenCallCount.set(0);
                        halfOpenSuccessCount.set(0);
                        System.out.println("[" + name + "] OPEN → HALF_OPEN (wait time elapsed)");
                    }
                }
            } else {
                System.out.println("[" + name + "] OPEN — failing fast, using fallback");
                return fallback.get();
            }
        }

        if (state == State.HALF_OPEN && halfOpenCallCount.incrementAndGet() > halfOpenMaxCalls) {
            System.out.println("[" + name + "] HALF_OPEN — max test calls reached, using fallback");
            return fallback.get();
        }

        try {
            T result = supplier.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            return onFailure(fallback, e);
        }
    }

    private void onSuccess() {
        if (state == State.HALF_OPEN) {
            int successes = halfOpenSuccessCount.incrementAndGet();
            if (successes >= halfOpenMaxCalls) {
                synchronized (stateLock) {
                    state = State.CLOSED;
                    failureCount.set(0);
                    callCount.set(0);
                    System.out.println("[" + name + "] HALF_OPEN → CLOSED (test calls succeeded)");
                }
            }
        }
        if (state == State.CLOSED) {
            failureCount.set(0);
        }
    }

    private T onFailure(Supplier<T> fallback, Exception e) {
        lastFailureTime = Instant.now();
        if (state == State.HALF_OPEN) {
            synchronized (stateLock) {
                state = State.OPEN;
                System.out.println("[" + name + "] HALF_OPEN → OPEN (test call failed)");
            }
        } else if (state == State.CLOSED) {
            int failures = failureCount.incrementAndGet();
            if (failures >= failureThreshold) {
                synchronized (stateLock) {
                    state = State.OPEN;
                    System.out.println("[" + name + "] CLOSED → OPEN (failure threshold reached: " + failures + ")");
                }
            }
        }
        return fallback.get();
    }

    public State getState() { return state; }
}
```

## Usage Example

```java
public class CircuitBreakerDemo {
    private static int callAttempt = 0;

    public static void main(String[] args) throws InterruptedException {
        CircuitBreaker<String> cb = new CircuitBreaker<>(
            "payment-service", 3, 3, Duration.ofSeconds(5), Duration.ofSeconds(2));

        // Simulate failing downstream
        for (int i = 0; i < 20; i++) {
            callAttempt = i;
            String result = cb.call(
                () -> callPaymentService(),
                () -> fallbackResponse()
            );
            System.out.println("Call " + i + ": " + result + " (state: " + cb.getState() + ")");
            Thread.sleep(500);
        }
    }

    static String callPaymentService() {
        // Fails for first 8 calls, then succeeds
        if (callAttempt < 8) {
            throw new RuntimeException("Payment service timeout");
        }
        return "Payment processed successfully";
    }

    static String fallbackResponse() {
        return "Payment service unavailable — order queued for retry";
    }
}
```

## Resilience4j Configuration Example

```java
import io.github.resilience4j.circuitbreaker.*;
import io.github.resilience4j.retry.*;
import io.github.resilience4j.timelimiter.*;
import java.time.Duration;
import java.util.function.Supplier;

public class ResilienceConfig {
    public static CircuitBreaker createPaymentCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(100)
            .minimumNumberOfCalls(10)
            .permittedNumberOfCallsInHalfOpenState(5)
            .recordExceptions(TimeoutException.class, RuntimeException.class)
            .build();
        return CircuitBreaker.of("payment-service", config);
    }

    public static Retry createRetryWithBackoff() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(3)
            .waitDuration(Duration.ofMillis(500))
            .intervalFunction(IntervalFunction.ofExponentialBackoff(500, 2))
            .retryExceptions(TimeoutException.class)
            .build();
        return Retry.of("payment-retry", config);
    }

    public static <T> Supplier<T> decorate(Supplier<T> supplier) {
        CircuitBreaker cb = createPaymentCircuitBreaker();
        Retry retry = createRetryWithBackoff();
        return CircuitBreaker.decorateSupplier(cb, Retry.decorateSupplier(retry, supplier));
    }
}
```

## Unit Tests

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

class CircuitBreakerTest {
    private int callCount = 0;

    @Test
    void testCircuitOpensAfterFailures() {
        CircuitBreaker<String> cb = new CircuitBreaker<>(
            "test", 3, 3, Duration.ofSeconds(30), Duration.ofSeconds(2));

        // First 3 failures should open the circuit
        for (int i = 0; i < 3; i++) {
            cb.call(() -> { throw new RuntimeException("fail"); }, () -> "fallback");
        }

        assertEquals(CircuitBreaker.State.OPEN, cb.getState(),
                "Circuit should be OPEN after 3 failures");
    }

    @Test
    void testCircuitClosesAfterHalfOpenSuccess() throws InterruptedException {
        CircuitBreaker<String> cb = new CircuitBreaker<>(
            "test", 3, 2, Duration.ofSeconds(1), Duration.ofSeconds(2));

        for (int i = 0; i < 3; i++) {
            cb.call(() -> { throw new RuntimeException("fail"); }, () -> "fallback");
        }

        Thread.sleep(1100); // Wait for half-open transition

        // Successful calls in half-open should close the circuit
        cb.call(() -> "success", () -> "fallback");
        cb.call(() -> "success", () -> "fallback");

        assertEquals(CircuitBreaker.State.CLOSED, cb.getState(),
                "Circuit should be CLOSED after successful half-open calls");
    }
}
```
