# Root Cause Analysis — Circuit Breaker Cascading Failure

## Incident: INC-2026-0720-007

**Analysis Method**: 5 Whys + Fault Tree Analysis  
**Analyst**: Anna Lee, Senior Platform Engineer  
**Date**: July 21, 2026  

---

## Executive Summary

A cascading failure affected all 15 microservices in the production environment, causing a 94-minute incident with 78% peak error rate and 2.1 million failed requests. The immediate trigger was a latency increase in the payment service, but the root cause was a dangerously misconfigured circuit breaker that failed to open under stress. This document traces the causal chain using the 5 Whys methodology.

---

## 5 Whys Analysis

### Why 1: Why did the failure cascade to all 15 microservices?

**Answer**: Because the circuit breaker in `order-service` did not open when `payment-service` began failing. Instead of failing fast (returning immediately when the circuit is open), the service continued attempting requests with 30-second timeouts. This caused the thread pool in `order-service` to fill with blocked threads. Once the thread pool was exhausted, the API Gateway's thread pool also filled. Retry logic in downstream services (`inventory-service`, `fraud-detection-service`) exacerbated the problem by generating additional requests, consuming thread pools in 8 services within 3 minutes.

**Evidence**:
- Zipkin traces show: 15 services all have spans with duration > 25,000ms (timing out)
- Hystrix dashboard shows: `order-service` circuit breaker state = CLOSED throughout the incident window
- Thread pool metrics from Spring Boot Actuator: `order-service` thread pool reached 20/20 active threads by T+2 minutes
- AWS CloudWatch Logs: retry events in `inventory-service` at T+3 minutes: "Retry attempt 2/3 for payment verification"

### Why 2: Why did the circuit breaker not open?

**Answer**: Because the `failureRateThreshold` was set to 80 (`circuitBreaker.failureRateThreshold=80`), meaning the circuit would only open when 80% of requests in the sliding window failed. With the 30-second timeout, only 30-40% of requests were actually timing out (the rest completed, albeit slowly). The 80% threshold was never reached. Additionally, the sliding window size was 20, requiring 16 failed requests out of 20 to open — but many requests were still succeeding (slowly), keeping the failure rate below 80%.

**Evidence**:
- `application.yml` in `order-service`: `resilience4j.circuitbreaker.configs.default.failureRateThreshold: 80`
- Log files show: "Circuit breaker state: CLOSED (current failure rate: 35%)" at the height of the incident
- Configuration was copy-pasted from development environment where it was set to 80 intentionally for testing
- No production-specific circuit breaker configuration existed — all services used defaults or dev configs

### Why 3: Why was there no thread pool isolation (bulkhead)?

**Answer**: Because the team had not implemented the bulkhead pattern. All downstream dependencies shared the same thread pool in `order-service`. When `payment-service` consumed all threads, `inventory-service` and `shipping-service` requests also blocked because no threads were available. A proper bulkhead would allocate separate thread pools per dependency, ensuring that `payment-service` failures do not affect other dependencies.

**Evidence**:
- Thread pool configuration: `resilience4j.thread-pool-bulkhead.core-thread-pool-size: 20` — one pool for all services
- No per-dependency bulkhead configuration existed
- Infrastructure-as-code repository shows: bulkhead implementation was on the roadmap for Q3 2026 but not started
- Developer interview: "We knew bulkhead was important but prioritized feature work over resilience"

### Why 4: Why was there no fallback method for the circuit breaker?

**Answer**: Because the team assumed circuit breakers would always open before thread pool exhaustion occurred. The fallback method was never implemented because:
1. The development team considered it "future work" after the initial circuit breaker rollout
2. No testing scenario covered the circuit-breaker-not-opening case
3. Performance testing always used healthy downstream services
4. The team lacked awareness of the interaction between thread pool size, timeout duration, and failure rate thresholds

**Evidence**:
- `order-service` code shows: `@CircuitBreaker(name = "paymentService", fallbackMethod = "none")` — fallback method not specified
- Sprint backlog: "Implement fallback for circuit breakers" — deprioritized 3 sprints in a row
- Regression test suite: no tests for circuit breaker OPEN or HALF_OPEN states

### Why 5 (Root Cause): Why was the resilience architecture insufficient to prevent cascading failure?

**Answer**: Because the organization treated circuit breaker implementation as a checkbox requirement rather than a comprehensive resilience strategy. Four specific organizational failures:

1. **Configuration Standards Gap**: No standardized circuit breaker configuration existed for production environments. Each team configured circuit breakers independently, often copying from development or internet examples without understanding the production implications.

2. **Resilience Testing Gap**: The system was never tested under realistic failure conditions. No chaos engineering practices were in place. The team had no way to validate that their circuit breaker configuration would actually protect against cascading failure.

3. **Monitoring and Alerting Gap**: Circuit breaker state changes (OPEN/HALF_OPEN) were not monitored or alerted. The Hystrix dashboard was available but not integrated into the alerting pipeline. The team monitored error rates but not the system state that predicts error rates.

4. **Organizational Silos**: The circuit breaker was owned by the Platform Team, but the fallback implementation was owned by individual service teams. Neither team fully understood the other's configuration. No cross-team review of resilience configuration existed.

**Evidence**:
- Organizational structure: Platform Team (3 engineers), Service Teams (4 teams, 12 engineers total)
- Wiki page: "Circuit Breaker Best Practices" — last updated 8 months ago, referenced Hystrix (deprecated in favor of Resilience4j)
- No chaos engineering tools or practices in place
- Meeting notes: "Resilience testing" — discussed and deferred 4 times in the past year
- Job description for Platform Engineer: did not mention resilience or chaos engineering

---

## Fault Tree Analysis

```
Cascading Failure (15 services affected)
    |
    |-- AND Gate --
    |
    |-- Circuit Breaker Failed to Open
    |       |
    |       |-- failureRateThreshold=80 (too high)
    |       |-- slidingWindowSize=20 (too large)
    |       |-- timeout=30s (too long)
    |       |-- No automated circuit state alerts
    |
    |-- Thread Pool Exhaustion
    |       |
    |       |-- No bulkhead isolation
    |       |-- Single shared thread pool
    |       |-- Thread pool size=20 (undersized)
    |       |-- No thread pool monitoring
    |
    |-- No Fallback Mechanism
    |       |
    |       |-- Fallback method not implemented
    |       |-- No degradation strategy documented
    |       |-- Default error response (500) instead of graceful degradation
    |
    |-- Retry Storm Amplification
            |
            |-- inventory-service: 3 retries (exponential backoff)
            |-- fraud-detection-service: 3 retries (immediate)
            |-- API Gateway: no retry limiting
```

---

## Root Cause Statement

**The root cause of this incident is organizational: the absence of standardized production circuit breaker configuration, combined with missing bulkhead isolation and fallback implementation, allowed a single service latency spike to cascade into a system-wide failure of all 15 microservices.**

### Contributing Factors (Priority Order)

| Factor | Type | Impact |
|--------|------|--------|
| failureRateThreshold=80 (should be 50) | Technical | Critical |
| No bulkhead isolation | Technical | Critical |
| 30-second timeout (should be 5s) | Technical | High |
| No fallback implementation | Technical | High |
| No circuit breaker state alerts | Process | High |
| No resilience testing | Process | High |
| Retry storms without backoff | Technical | Medium |
| Thread pool undersized | Technical | Medium |

### References
- Google SRE Book — Chapter 22: "Managing Cascading Failures"
- Netflix Tech Blog: "Fault Tolerance in a High Volume, Distributed System"
- AWS re:Invent 2018 — DOP302: "Prime Day 2018: Failure Analysis"
- Microsoft Azure Architecture Center: "Circuit Breaker Pattern"
- Resilience4j Documentation: "Circuit Breaker and Bulkhead"

---

## Expanded Technical Root Cause Analysis

### Configuration Comparison: Dev vs. Production

The circuit breaker configuration that caused this incident was originally created for the development environment and was never updated for production:

| Parameter | Dev Configuration | Production Configuration (Should Be) | Actual (What Was Deployed) |
|-----------|------------------|--------------------------------------|---------------------------|
| failureRateThreshold | 80 | 50 | 80 (copied from dev) |
| slidingWindowSize | 20 | 10 | 20 (copied from dev) |
| timeout (ms) | 30000 (30s) | 5000 (5s) | 30000 (copied from dev) |
| automaticTransitionFromOpenToHalfOpenEnabled | false | true | false (copied from dev) |
| waitDurationInOpenState (ms) | 60000 | 30000 | 60000 (copied from dev) |
| permittedNumberOfCallsInHalfOpenState | undefined | 3 | undefined (copied from dev) |
| fallbackMethod | none | required | none (copied from dev) |

**Why Dev Configuration is Different from Production**:
In development environments, services are not under load. A 30-second timeout is acceptable because:
- No concurrent users (usually 1-2 developers testing)
- No downstream dependency load
- No thread pool contention
- Monitoring is not alerting on error rates

In production, the same configuration is catastrophic because:
- Thousands of concurrent users create thread pool contention
- 30-second timeouts block threads for extended periods
- The sliding window of 20 requests with 80% threshold means 16 failures are needed to open
- With 30-second timeouts and 20 threads, it takes 90 seconds to get 16 failures

### Detailed Thread Pool Exhaustion Mathematics

The timeline of thread pool exhaustion can be calculated precisely:

**Initial Conditions**:
- Thread pool size: 20
- Payment-service timeout: 30 seconds
- Request arrival rate: 40 requests/second (to order-service)
- Payment-service share: 50% of requests (20 requests/second)
- Payment-service success rate during degradation: 60%
- Payment-service failure rate during degradation: 40% (30-second timeout)

**Thread Pool Filling Calculation**:
- Each failed request blocks a thread for 30 seconds
- Failed request rate: 20 req/s × 40% = 8 failed requests/second
- After 1 second: 8 threads blocked
- After 2 seconds: 16 threads blocked
- After 2.5 seconds: 20 threads blocked (pool full)
- After 3 seconds: queue begins filling (queue capacity: 10)
- After 4 seconds: queue full → requests rejected

**Total time to thread pool exhaustion: ~3 seconds**

**Recovery Time Calculation**:
- Circuit breaker opened at T+12 minutes
- Open circuit means requests fail fast (no thread blocking)
- Each blocked thread will be freed after its 30-second timeout
- With 20 threads all blocked at different times, the last thread frees:
  - Worst case: 30 seconds after circuit opens + initial 30-second timeout
  - Actual: ~45 seconds for all blocked threads to clear

### Resilience4j vs. Hystrix: Migration Issues

The team was in the process of migrating from Hystrix (deprecated) to Resilience4j. This migration introduced configuration inconsistencies:

**Configuration Mapping Issues**:
| Hystrix Parameter | Resilience4j Equivalent | Migration Issue |
|-------------------|------------------------|-----------------|
| circuitBreaker.requestVolumeThreshold | slidingWindowSize | Different default values |
| circuitBreaker.errorThresholdPercentage | failureRateThreshold | Same concept, different default |
| circuitBreaker.sleepWindowInMilliseconds | waitDurationInOpenState | Same concept, different default |
| threadPool.coreSize | thread-pool-bulkhead.coreThreadPoolSize | Different configuration namespace |

**Migration State at Time of Incident**:
- 8 services migrated to Resilience4j (including order-service)
- 7 services still on Hystrix
- Configuration was managed in separate files (Resilience4j in application.yml, Hystrix in separate config)
- No migration validation testing was performed
- The Hystrix dashboard was still in use (not compatible with Resilience4j metrics)

This inconsistent migration state meant:
- The team was monitoring circuit breaker state using a tool (Hystrix dashboard) that did not reflect the actual circuit breaker implementation (Resilience4j)
- Configuration validation tools only checked Hystrix configuration, not Resilience4j
- Team members expected Hystrix default values (which are safer) but got Resilience4j defaults (which allowed the incident)

### Expanded 5 Whys Analysis — Alternative Perspectives

**Why Analysis — Monitoring Perspective**:
Why didn't monitoring catch this?
1. Circuit breaker state alerts were not configured (only error rate alerts)
2. Thread pool utilization was not monitored
3. Hystrix dashboard was not actively viewed (it was available but no alerting)
4. No automated "cascading failure detection" existed
5. The monitoring team assumed circuit breakers would self-protect without monitoring

**Why Analysis — Testing Perspective**:
Why didn't testing catch this?
1. Load tests used healthy downstream services
2. No latency injection testing was performed
3. Circuit breaker behavior was unit tested but not integration tested under load
4. The test environment circuit breaker configuration matched dev (80% threshold), masking the production issue
5. No chaos engineering practices were in place

### Organizational Learning and Change Management

**Resistance to Change**:
The "move fast" culture at the organization created resistance to the systematic changes needed:
- Some engineers argued that adding bulkhead would increase complexity
- Feature teams resisted fallback method requirements (additional development effort)
- The platform team's recommendation for standardized circuit breaker config was seen as "bureaucratic"
- The SRE team's authority over production configuration was not established

**Change Management Strategy**:
Following this incident, resistance decreased significantly. The change management strategy used:
1. Incident data: presented the concrete impact (2.1M failed requests, $188K revenue loss)
2. Executive sponsorship: VP of Engineering mandated circuit breaker standards
3. Gradual rollout: standards applied to new services first, then existing services migrated
4. Tooling investment: created automated configuration validation tools
5. Training: all backend engineers completed Resilience4j training within 2 weeks
