# Interview Cheatsheet: Circuit Breaker Failure

## Key Diagnostic Commands
- Circuit breaker state metrics (CLOSED / OPEN / HALF_OPEN)
- Hystrix stream: `/hystrix.stream` — real-time metrics
- Resilience4j actuator: `/actuator/health` — circuit breaker status
- `curl <service>/actuator/circuitbreakers` — Spring Boot
- Downstream service health check endpoint

## Common Metrics to Check
- Circuit breaker state transitions (open/close count)
- Failure rate (per circuit)
- Call count (per circuit)
- Slow call count (per circuit)
- Thread pool active/queue/rejected count (bulkhead)
- Timeout exception rate

## Typical Root Causes
- Downstream service degradation
- Short timeout threshold (false positives)
- No retry mechanism after circuit opens
- Bulkhead thread pool too small
- Shared thread pool across circuits (no isolation)
- Half-open test request fails repeatedly
- Cascading circuit breaker opens

## Interview Question Patterns
- "Design a circuit breaker for a microservice architecture"
- "How does a circuit breaker prevent cascading failures?"
- "Compare Hystrix, Resilience4j, and Sentinel"
- "How would you test that a circuit breaker works?"

## STAR Story Template
**S**: Payment service started timing out → circuit breaker opened → entire checkout flow blocked
**T**: Restore checkout flow and fix cascading failure
**A**: Added bulkhead per downstream service, increased timeout, added fallback cache
**R**: Checkout availability improved from 99.0% to 99.99%
