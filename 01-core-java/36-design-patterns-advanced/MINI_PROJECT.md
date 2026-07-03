# Module 36: Advanced Design Patterns - Mini Project

**Project Name**: Fault-Tolerant API Client  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Apply Advanced Design Patterns (Circuit Breaker, Promise/Future, and Retry) to build a highly resilient client for an unreliable third-party API.

## 📝 Requirements

### Core Features

1. **The Unreliable Service (Mock)**:
   - Create a `WeatherService` class with a method `String getWeather(String city)`.
   - Implement logic inside this method that randomly fails:
     - 50% chance to return a valid weather string.
     - 25% chance to throw a `RuntimeException("Network Timeout")`.
     - 25% chance to sleep for 3000ms and then throw an exception.

2. **Promise/Future Pattern**:
   - Create an `AsyncWeatherClient`.
   - Wrap the call to `getWeather` inside a `CompletableFuture.supplyAsync()`. Provide a dedicated `ExecutorService` so the slow API calls do not block the main application thread.

3. **Circuit Breaker Pattern (Custom Implementation)**:
   - Do not use a library like Resilience4j; build a simple one from scratch.
   - Create a `CircuitBreaker` class that wraps the `getWeather` call.
   - **State Machine**: Implement states `CLOSED` (normal operation), `OPEN` (failing fast), and `HALF_OPEN` (testing recovery).
   - If the call fails 3 consecutive times, switch to `OPEN`.
   - While `OPEN`, immediately throw a `CircuitOpenException` or return a cached fallback value without calling the actual `WeatherService`.
   - After 5 seconds in the `OPEN` state, switch to `HALF_OPEN` and allow exactly one request to pass through. If it succeeds, switch to `CLOSED`. If it fails, switch back to `OPEN`.

---

## 💡 Solution Blueprint

**The Circuit Breaker Logic**:
```java
public class CircuitBreaker {
    private enum State { CLOSED, OPEN, HALF_OPEN }
    
    private State state = State.CLOSED;
    private int failureCount = 0;
    private long openTime = 0;
    
    public String execute(Supplier<String> action, Supplier<String> fallback) {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - openTime > 5000) {
                state = State.HALF_OPEN;
            } else {
                return fallback.get(); // Fail fast / Fallback
            }
        }
        
        try {
            String result = action.get();
            reset(); // Success! Close the circuit
            return result;
        } catch (Exception e) {
            recordFailure();
            return fallback.get();
        }
    }
    
    private void recordFailure() {
        failureCount++;
        if (failureCount >= 3) {
            state = State.OPEN;
            openTime = System.currentTimeMillis();
        } else if (state == State.HALF_OPEN) {
            state = State.OPEN;
            openTime = System.currentTimeMillis();
        }
    }
    
    private void reset() {
        state = State.CLOSED;
        failureCount = 0;
    }
}
```