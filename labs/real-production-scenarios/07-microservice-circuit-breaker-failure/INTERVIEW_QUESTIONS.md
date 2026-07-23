# Lab 07 — Microservice Circuit Breaker Failure: Interview Questions

**Q1: Explain the circuit breaker pattern in microservices.**

**Answer:** The circuit breaker is a resilience pattern that prevents cascading failures. Three states: CLOSED (normal — requests pass through), OPEN (failure threshold exceeded — requests fail fast without calling the downstream service), HALF_OPEN (after timeout — limited requests pass to test if service recovered). States transition: CLOSED → OPEN when failure count exceeds threshold; OPEN → HALF_OPEN after timeout; HALF_OPEN → CLOSED if test requests succeed, or OPEN if they fail.

**Q2: How does the circuit breaker pattern differ from retry with exponential backoff?**

**Answer:** Circuit breaker: prevents calls entirely when downstream is failing, giving it time to recover. Retry: repeats failed calls hoping they succeed next time. Circuit breaker protects the downstream from overload during failures. Retry can amplify the problem if downstream is already failing (retry storm). Best practice: use circuit breaker for protection, use retry with caution (limited attempts, exponential backoff, jitter).

**Q3: What is a cascading failure and how does the circuit breaker prevent it?**

**Answer:** A cascading failure is when one service fails, causing its dependents to also fail, then their dependents fail, etc. The circuit breaker prevents this by: 1) Detecting that downstream is failing (OPEN state). 2) Failing fast immediately (not waiting for timeout). 3) This prevents threads from being held waiting for the failing service. 4) The service can continue serving other requests unaffected. Example: Service A calls B, B calls C. If C fails, without circuit breaker, A's threads wait for B, B's threads wait for C — all threads exhaust, A and B also fail.

**Q4: What metrics do you monitor for circuit breaker health?**

**Answer:** 1) Circuit breaker state (CLOSED, OPEN, HALF_OPEN) per downstream service. 2) Failure rate (failed calls / total calls). 3) Call volume (requests per second). 4) Rejection rate (requests rejected because circuit is OPEN). 5) Latency of calls (only when CLOSED). 6) Transition rate (how often circuit opens/closes). 7) Time in OPEN state. Alert on: circuit OPEN for > 5 min, rapid OPEN/CLOSE transitions (flux).

**Q5: How do you set circuit breaker thresholds for a new service?**

**Answer:** Start with conservative defaults and adjust based on production data: 1) Failure threshold: 50% of calls failing in a rolling window of 100 calls. 2) Wait duration (OPEN → HALF_OPEN): 30 seconds (enough for transient issues). 3) Half-open threshold: 3-5 successful calls to close. 4) Metrics window: rolling window of 10 seconds. Monitor and adjust: if circuit opens too often → increase threshold; if circuit doesn't protect at all → decrease threshold.

**Q6: Tell me about a time the circuit breaker pattern saved your system.**

**Answer:** Situation: A downstream payment service started timing out on 90% of calls due to database connection exhaustion. Task: Our order service had to remain available for non-payment operations. Action: Hystrix circuit breaker detected the failure rate exceeding 50% threshold and opened the circuit after 10 seconds. All payment requests failed fast (no waiting), and order processing continued for non-payment operations. The payment team fixed the database issue without pressure from a cascading outage. Result: Zero cascading failures, payment service recovered in 15 min without impact to other services.

**Q7: What is the difference between Hystrix and Resilience4j?**

**Answer:** Hystrix (Netflix, now in maintenance mode): thread pool isolation, bulkhead pattern, request caching, circuit breaker. Resilience4j: lighter, modular, functional programming-friendly, uses semaphore isolation instead of thread pools (lower overhead). Hystrix uses separate thread pools for each downstream call (high overhead but complete isolation). Resilience4j uses semaphores in the calling thread (lower overhead but less isolation). Resilience4j is the recommended replacement for Hystrix.

**Q8: Design a fault-tolerant microservice architecture.**

**Answer:** 1) Circuit breaker on all downstream calls. 2) Retry with exponential backoff + jitter for transient failures. 3) Timeout on all calls (connect + read). 4) Bulkhead pattern — separate thread pools for different downstream services. 5) Fallback — return cached/default response when downstream unavailable. 6) Health check API for each service. 7) Graceful degradation — disable non-critical features when dependencies fail. 8) Distributed tracing to identify dependency issues. 9) Chaos engineering to regularly test fault tolerance.

**Q9: How would you debug a circuit that keeps opening and closing?**

**Answer:** Circuit flapping indicates the downstream is in a degraded state — partially available. Investigate: 1) Check downstream metrics — is its error rate just above threshold? 2) Check the failure window — is the threshold appropriate? 3) Check if the downstream has intermittent issues (GC pauses, periodic batch jobs). 4) Check if there's a traffic pattern causing the issue (periodic spikes). 5) Adjust threshold or wait duration to reduce flapping. 6) Add a minimum number of calls before circuit opens to avoid false positives.

**Q10: What's the relationship between circuit breaker and bulkhead pattern?**

**Answer:** Both are resilience patterns: circuit breaker prevents cascading failures by failing fast; bulkhead isolates resources so failure in one area doesn't consume all resources. Together: circuit breaker protects against downstream failures, bulkhead protects against resource exhaustion within the service. Example: bulkhead allocates max 10 threads for payment service calls. If payment is slow, only 10 threads are affected. If payment fails completely, circuit breaker opens and the 10 threads are freed.
