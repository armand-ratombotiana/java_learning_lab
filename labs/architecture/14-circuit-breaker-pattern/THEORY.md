# Circuit Breaker Pattern — Theory

## 1. Introduction
The Circuit Breaker pattern prevents cascading failures in distributed systems by detecting when a downstream service is failing and stopping further calls until the service recovers. Named after electrical circuit breakers, it provides system resilience through controlled failure handling.

## 2. Problem Context
Distributed systems face cascading failure scenarios: a slow downstream service causes upstream thread pool exhaustion, connection pool saturation propagates across services, failed retries amplify load on already-stressed services, and unbounded timeouts consume resources indefinitely.

## 3. Circuit Breaker States

### 3.1 CLOSED State
Normal operation. Requests pass through to the downstream service. Failure count is tracked within a sliding window.

### 3.2 OPEN State
Requests are immediately rejected without calling the downstream service. A failure threshold has been exceeded. Calls fail fast with an exception or fallback.

### 3.3 HALF_OPEN State
After a configured timeout, the circuit allows a limited number of probe requests. If they succeed, the circuit closes. If they fail, the circuit reopens.

## 4. Configuration Parameters

### 4.1 Failure Threshold
Number or percentage of failures within a sliding window before the circuit opens. Common values: 5 failures or 50 percent failure rate.

### 4.2 Sliding Window
Count-based (last N calls), time-based (calls within last M seconds), or composite (both).

### 4.3 Wait Duration
Time to wait before transitioning from OPEN to HALF_OPEN. Typical range: 5-60 seconds.

### 4.4 Half-Open Threshold
Number of probe requests allowed in HALF_OPEN state. Typically 1-3 requests.

## 5. Bulkhead Pattern

### 5.1 Concept
Isolate resources into separate pools (bulkheads) so that failure in one pool doesn't affect others. Named after ship bulkheads.

### 5.2 Thread Pool Isolation
Each downstream service gets its own thread pool. Exhaustion in one pool doesn't affect calls to other services.

### 5.3 Semaphore Isolation
Limit concurrent calls using semaphores without thread pool overhead. Suitable for low-latency operations.

## 6. Resilience4J Implementation
Provides CircuitBreaker, Bulkhead, RateLimiter, Retry, TimeLimiter, and Cache modules. Key features include functional composition, sliding window configurations, event publishing, and thread pool or semaphore bulkhead variants.

## 7. Advanced Resilience Patterns

### 7.1 Retry with Backoff
Exponential backoff with jitter to prevent thundering herd problems during recovery.

### 7.2 Fallback Strategies
Static fallback responses, stale cached data serving, graceful degradation, and throttled mode.

### 7.3 Timeout Management
Distinct timeouts per downstream service with fast-fail behavior.

## 8. Monitoring and Alerting
Circuit state transitions, call success/failure rates, bulkhead pool utilization, thread pool rejection rates, and latency percentiles.
