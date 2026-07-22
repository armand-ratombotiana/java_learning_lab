# Incident Report — Microservice Circuit Breaker Cascading Failure

## Incident ID: INC-2026-0720-007

**Date**: July 20, 2026  
**Reported By**: Platform SRE Team (Mike Chen)  
**Status**: Resolved  
**Severity**: SEV1 / P0  

---

## Timeline (All Times UTC)

### Pre-Incident Conditions

| Time | Event |
|------|-------|
| 09:30 | Payment service experiences increased latency due to database connection pool exhaustion (caused by an unoptimized query deployed the previous day) |
| 09:35 | DBA team alerted about slow queries in payment-service database — investigation started |
| 09:45 | Payment-service p95 latency increases from 50ms to 500ms |
| 09:50 | Connection pool in payment-service reaches 80% utilization (40 of 50 connections active) |
| 10:00 | Payment-service begins timing out on 30% of requests (timeout configured at 30 seconds) |

### Incident Onset and Escalation

| Time | Event |
|------|-------|
| 10:01 | Order-service circuit breaker monitors sliding window: 30% failure rate (threshold: 80%) — circuit remains CLOSED |
| 10:02 | Order-service thread pool (20 threads) — 15 threads blocked waiting on payment-service responses (30s timeout) |
| 10:03 | Order-service thread pool fully exhausted (20/20 threads blocked) |
| 10:04 | API Gateway (Zuul) begins queuing requests to order-service — thread pool expands from default 50 to 200 threads (max) |
| 10:05 | API Gateway thread pool hits 200/200 — new requests rejected at gateway level |
| 10:06 | Retry logic in inventory-service and fraud-detection-service triggers: each retry consumes additional threads upstream |
| 10:07 | All 5 services in the `order-service` dependency chain now affected |
| 10:08 | Zipkin dashboard shows span context propagating failures across all 15 microservices |
| 10:09 | PagerDuty alert: "Critical — 6+ services reporting >50% error rate" — SEV1 declared |
| 10:10 | SRE team joined incident bridge — primary (Mike Chen), secondary (Anna Lee) |
| 10:11 | Zipkin analysis confirms: root fault is payment-service latency; order-service circuit breaker should have opened |
| 10:12 | Decision: Forced circuit breaker open on payment-service in order-service |
| 10:13 | Hystrix dashboard shows: order-service circuit breaker still CLOSED — `failureRateThreshold` set to 80 |
| 10:14 | Operations team manually transitions circuit breaker to OPEN via Hystrix management endpoint |
| 10:15 | Order-service begins failing fast for payment-service requests — thread pool starts draining |
| 10:17 | API Gateway thread pool utilization begins declining (requests failing fast instead of timing out) |
| 10:20 | Inventory-service and fraud-detection-service retries stop — their thread pools begin draining |
| 10:25 | 8 of 15 services showing recovery — error rate dropping below 50% |
| 10:30 | Bulkhead configuration applied dynamically via Spring Cloud Config refresh |
| 10:35 | 12 of 15 services healthy — thread pools at normal utilization |
| 10:40 | All 15 services reporting healthy — error rate below 1% |
| 10:45 | Payment-service database query optimized — connection pool utilization drops to 60% |
| 10:50 | Payment-service latency returns to baseline (50ms p95) |
| 10:55 | Circuit breaker reset to CLOSED in order-service — traffic flows normally |
| 11:00 | Post-incident review initiated |

### Recovery and Stabilization

| Time | Event |
|------|-------|
| 11:00 | All services healthy — error rate 0.05%, latency at baseline |
| 11:05 | Circuit breaker configuration updated via Spring Cloud Config: `failureRateThreshold=50`, `slidingWindowSize=10`, `timeout=5s` |
| 11:10 | Bulkhead configuration applied to all services — 10 threads per dependency isolation |
| 11:15 | Fallback methods verified for all circuit breakers |
| 11:20 | Graceful degradation tests executed in staging environment |
| 11:30 | Monitoring dashboards updated with circuit breaker metrics |
| 11:34 | Incident formally closed |

### Metrics Summary

| Metric | Baseline | Peak During Incident | Post-Recovery |
|--------|----------|---------------------|---------------|
| Global Error Rate | 0.05% | 78% | 0.05% |
| Total Failed Requests | — | 2,100,000 | — |
| Affected Services | 0 | 15 | 0 |
| p95 Latency (all services) | 120ms | 32,000ms | 125ms |
| Thread Pool Utilization | 35% | 100% (all pools) | 38% |
| Circuit Breaker Opens | 0 | 1 (manual) | 0 |
| CPU Utilization (average) | 45% | 92% | 47% |
| Revenue Impact | — | ~$188,000 (94 min) | — |

### Post-Mortem Findings

**Root Cause**: Circuit breaker misconfiguration in `order-service` — `failureRateThreshold=80` (too high) combined with 30-second timeout and no fallback implementation. The 80% threshold meant the circuit would not open until 8 of 10 requests in the sliding window failed. With 30-second timeouts and 20 thread pool threads, it took 90 seconds for the thread pool to fully exhaust before any requests failed fast.

**Compounding Factors**:
1. No bulkhead isolation — order-service's single thread pool handled ALL downstream dependencies
2. No fallback method configured for the payment-service circuit breaker
3. Retry storms from inventory-service and fraud-detection-service amplified the failure
4. No thread pool monitoring alerts — exhausted thread pools were detected via downstream error rate spikes
5. Hystrix dashboard was not actively monitored — alerts were configured for error rates, not circuit breaker state changes

### Action Items

| # | Action | Owner | Target | Status |
|---|--------|-------|--------|--------|
| 1 | Fix circuit breaker thresholds: `failureRateThreshold=50`, `slidingWindowSize=10` | Platform Team | 07/21 | Done |
| 2 | Implement bulkhead pattern for all downstream dependencies | Platform Team | 07/25 | In Progress |
| 3 | Implement fallback methods for all circuit breakers | Service Teams | 07/30 | Planned |
| 4 | Add thread pool monitoring alerts (95% utilization) | SRE Team | 07/22 | In Progress |
| 5 | Optimize payment-service database query | DBA Team | 07/21 | Done |
| 6 | Implement automatic circuit breaker state alerts (OPEN transitions) | SRE Team | 07/23 | Planned |
| 7 | Conduct chaos engineering exercise — inject latency in dependencies | QA Team | 08/01 | Planned |

## Expanded Incident Analysis

### Detailed Service Impact Matrix

| Service | Role | Impact Level | Error Rate Peak | Recovery Time | Root Cause |
|---------|------|-------------|-----------------|---------------|------------|
| gateway-service | API Gateway | Critical | 78% | 30 min | Thread pool exhaustion from upstream timeouts |
| order-service | Order Processing | Critical | 74% | 25 min | Circuit breaker failed to open, thread pool exhaustion |
| payment-service | Payment Processing | Moderate | 45% | 15 min | Database connection pool exhaustion |
| inventory-service | Inventory Check | Critical | 68% | 22 min | Retry storm from order-service |
| shipping-service | Shipping Label | High | 55% | 18 min | Cascading from order-service unavailability |
| fraud-detection-service | Fraud Analysis | High | 52% | 20 min | Retry storm from payment-service |
| warehouse-service | Warehouse Ops | Moderate | 35% | 12 min | Cascading from inventory-service |
| carrier-service | Carrier API | Low | 15% | 8 min | Cascading from shipping-service |
| user-service | User Profiles | Low | 8% | 5 min | Indirect impact (gateway contention) |
| token-service | Auth Tokens | Low | 5% | 3 min | Indirect impact |
| auth-service | Authentication | Low | 3% | 2 min | Indirect impact |
| catalog-service | Product Catalog | Low | 2% | 1 min | Minimal impact (different dependency tree) |
| product-service | Product Data | None | 0% | 0 | Not in affected dependency chain |
| pricing-service | Pricing Engine | None | 0% | 0 | Not in affected dependency chain |
| config-service | Configuration | None | 0% | 0 | Isolated infrastructure service |
| discovery-service | Service Discovery | None | 0% | 0 | Isolated infrastructure service |

### Communication and Coordination Analysis

**PagerDuty Escalation Timeline**:
- T+0 (10:01): Payment-service latency alert fires (warning) — acknowledged but not escalated
- T+5 (10:06): Order-service error rate alert fires (critical) — SRE primary paged
- T+8 (10:09): Multi-service outage alert fires (critical) — SEV1 declared
- T+10 (10:11): All 6 service owners paged
- T+12 (10:13): Platform team lead paged

**Communication Gaps Identified**:
1. The initial warning alert (payment-service latency) was acknowledged but the potential for cascading failure was not recognized
2. No pre-established escalation path for "single service degradation with cascading potential"
3. Service owners joined the bridge sequentially rather than simultaneously — each service owner had to independently diagnose their service before identifying the root cause
4. The discovery of the root cause (circuit breaker not opening) took 8 minutes because the Hystrix dashboard was not part of the standard incident response workflow

**Improved Communication Protocol**:
Following this incident, the follow protocol was established:
- Any service latency increase > 3x baseline for > 2 minutes triggers a coordination call with downstream services
- Circuit breaker state is checked immediately on any error rate increase
- A "cascading failure" template is used for the initial incident page, prompting responders to check circuit breaker state, thread pool utilization, and dependency health

### Decision Log

**Key Decision 1: Manual Circuit Breaker Override**
- Time: T+12 (10:12)
- Decision: Force circuit breaker to OPEN in order-service via Hystrix management endpoint
- Rationale: The circuit breaker was not opening automatically (failureRateThreshold=80 was too high). Manual override was necessary to stop the thread pool exhaustion.
- Alternatives: Restart order-service (would clear thread pool but issue would recur), force-OPEN all downstream circuits (more aggressive than necessary)
- Outcome: Order-service began failing fast, thread pool started draining within 1 minute

**Key Decision 2: Bulkhead Dynamic Configuration**
- Time: T+18 (10:18)
- Decision: Apply bulkhead configuration dynamically via Spring Cloud Config refresh
- Rationale: Even with circuit breaker OPEN, the shared thread pool meant other dependencies were still affected
- Implementation: Updated application.yml with per-dependency thread pool configurations
- Outcome: Each dependency got an isolated thread pool, preventing future recurrence within the same incident

**Key Decision 3: Retry Policy Modification**
- Time: T+22 (10:22)
- Decision: Reduce retry count to 0 for all services temporarily
- Rationale: Retry storms were amplifying the load, preventing recovery
- Implementation: Spring Cloud Config refresh with `resilience4j.retry.backends.*.maxRetryAttempts: 0`
- Outcome: Request volume dropped significantly as retries stopped

### Post-Incident Metrics Analysis

**Thread Pool Utilization Graphs**:
The thread pool for order-service showed a classic "exhaustion" pattern:
- 09:45-10:00: Baseline utilization 35% (7 of 20 threads active)
- 10:01-10:03: Rapid increase to 100% (payment-service latency spike)
- 10:03-10:12: Sustained at 100% (threads blocked, queue growing)
- 10:12-10:15: Decline begins (circuit breaker forced open)
- 10:15-10:25: Gradual decline (threads finishing or timing out)
- 10:25+: Back to baseline

**Error Rate by Service (Time Series)**:
The error rate propagation showed a clear cascading pattern:
1. payment-service errors began at 10:01 (30% error rate)
2. order-service errors began at 10:02 (25% error rate)
3. gateway-service errors began at 10:03 (15% error rate)
4. inventory/shipping/fraud errors began at 10:04-10:05 (10-20% error rates)
5. All services affected by 10:08 (30-78% error rates)

The cascading took 7 minutes to propagate from the root cause service to all 15 services. This delay provides a critical window for automated detection and response — if automated rollback or circuit breaker intervention had been triggered within 2-3 minutes of the first anomaly, the cascading could have been prevented.

### Expanded Technical Details

**Hystrix Thread Pool Configuration Details**:
```
Default thread pool configuration (before fix):
  coreSize: 10
  maximumSize: 20
  maxQueueSize: 10
  queueSizeRejectionThreshold: 5
  keepAliveTime: 1 minute
  allowMaximumSizeToDivergeFromCoreSize: true
  
Impact: Thread pool could grow from 10 to 20 threads, but once all 20 were blocked on slow payment-service calls, no threads were available for other services.
```

**Resilience4j Configuration Details (after fix)**:
```
payment-service thread pool:
  coreSize: 5
  maximumSize: 10
  queueCapacity: 5
  
inventory-service thread pool:
  coreSize: 5
  maximumSize: 10
  queueCapacity: 5
  
shipping-service thread pool:
  coreSize: 5
  maximumSize: 10
  queueCapacity: 5
  
Total threads: 15 (vs. previously 20 shared)
Key improvement: payment-service can use max 10 threads (not 20), leaving capacity for other services
```

### Lessons Learned Documented

**Technical Lessons**:
1. Circuit breaker configuration must be validated against realistic failure scenarios
2. Thread pool isolation (bulkhead) is not optional — it is a critical resilience mechanism
3. Fallback methods must be implemented for every circuit breaker
4. Retry policies must include exponential backoff and jitter
5. Timeout values must be aggressive (3-5 seconds, not 30 seconds)

**Process Lessons**:
1. Resilience testing must include latency injection scenarios
2. Circuit breaker state must be actively monitored and alerted
3. Deployment pipeline must validate circuit breaker configuration
4. Cross-team ownership of resilience configuration requires coordination

**Cultural Lessons**:
1. "Set up once and forget" approach to circuit breakers is dangerous
2. Production configuration (dev vs. prod) must be explicitly managed
3. Feature teams need platform guidance on resilience patterns
4. Error budgets should include deployment-specific consumption tracking
