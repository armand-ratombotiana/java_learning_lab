# Incident Response Runbook Checklist — Circuit Breaker Cascading Failure

## Incident Type: SEV1/P0 — Cascading Microservice Failure

### Classification
- [ ] Incident severity: SEV1 (Critical) / P0 (Immediate)
- [ ] Incident type: Cascading Failure / Circuit Breaker Failure
- [ ] Number of affected services: ____ out of ____
- [ ] Root cause service (if identified): ________________________________
- [ ] Current incident commander: ________________________________
- [ ] Current comms lead: ________________________________

---

## Phase 1: Detection and Triage (0–5 minutes)

### Immediate Detection
- [ ] Confirm PagerDuty alert: circuit breaker OPEN or thread pool exhaustion
- [ ] Check number of circuit breakers in OPEN state
- [ ] Check thread pool utilization across all services
- [ ] Verify error rate in monitoring dashboard
- [ ] Identify if this is a single service failure or cascading

### Initial Assessment
- [ ] Is this a cascading failure (multiple services affected)?
- [ ] What is the root cause service (bottom of dependency chain)?
- [ ] Are fallbacks returning valid responses?
- [ ] Is there a retry storm in progress?
- [ ] Has there been a recent deployment or configuration change?

### Communication
- [ ] Declare SEV1 incident in #incidents channel
- [ ] Open bridge with SRE team and affected service owners
- [ ] Notify incident commander
- [ ] Post initial status: "Cascading failure detected — {{N}} services affected"
- [ ] Post to #prod-status: "Investigating multi-service degradation"

---

## Phase 2: Containment (5–15 minutes)

### Isolate Failing Service
- [ ] Identify the root cause service (use Zipkin/Jaeger trace analysis)
- [ ] Remove failing service from service discovery (Eureka/Consul)
- [ ] Force circuit breaker OPEN upstream: `curl -X POST http://service/actuator/circuitbreakers/{name}/transitionOpen`
- [ ] Disable retries temporarily via feature flag or config refresh

### Protect Upstream Services
- [ ] Verify all upstream services have working fallbacks
- [ ] Check thread pool utilization is declining
- [ ] Check network traffic to failing service has stopped
- [ ] Verify API Gateway is failing fast (not timing out)

### Stop Retry Storm
- [ ] Disable retry mechanisms in all affected services
- [ ] Set retry count to 0 in configuration
- [ ] Verify no new retry attempts in logs
- [ ] Check all retry-related alerts are clearing

---

## Phase 3: Recovery (15–45 minutes)

### Drain Thread Pools
- [ ] Monitor thread pool utilization returning to normal
- [ ] Check active connection count to failing service
- [ ] Verify no threads are blocked on the failing dependency
- [ ] Monitor error rate declining

### Fix Root Cause
- [ ] Identify circuit breaker misconfiguration (threshold, timeout)
- [ ] Check bulkhead isolation configuration
- [ ] Review fallback implementation
- [ ] Fix circuit breaker configuration via config refresh
- [ ] Verify fix: `curl http://service/actuator/health`

### Restore Services
- [ ] Reconnect root cause service to service discovery
- [ ] Close circuit breakers: `curl -X POST http://service/actuator/circuitbreakers/{name}/transitionClose`
- [ ] Re-enable retries (with proper backoff)
- [ ] Verify traffic flows normally
- [ ] Check error rates returning to baseline

---

## Phase 4: Root Cause Investigation (45–120 minutes)

### Circuit Breaker Analysis
- [ ] Review circuit breaker configuration for all affected services
- [ ] Check failureRateThreshold value (should be ≤ 50)
- [ ] Check timeout values (should be ≤ 5s)
- [ ] Check slidingWindowSize (should be 10-20)
- [ ] Check waitDurationInOpenState (should be ≥ 30s)
- [ ] Verify automaticTransitionFromOpenToHalfOpenEnabled=true

### Thread Pool Analysis
- [ ] Review bulkhead configuration (per-dependency isolation?)
- [ ] Check thread pool sizes (core/max per dependency)
- [ ] Check queue capacity
- [ ] Review thread pool utilization before/during/after incident

### Fallback Analysis
- [ ] Verify fallback method exists for all circuit breakers
- [ ] Check fallback response validity
- [ ] Review fallback logging
- [ ] Test fallback behavior in staging

### Retry Analysis
- [ ] Review retry configuration for all services
- [ ] Check maxRetryAttempts (should be ≤ 2)
- [ ] Verify exponential backoff is enabled
- [ ] Check retry exception types (should exclude 4xx)

---

## Phase 5: System-Wide Remediation (120–360 minutes)

### Configuration Fixes
- [ ] Update circuit breaker configuration to production standards
- [ ] Implement bulkhead pattern for all downstream dependencies
- [ ] Add fallback methods to all circuit breakers
- [ ] Configure timeouts (max 5s)
- [ ] Enable automatic circuit recovery

### Monitoring Fixes
- [ ] Add circuit breaker state alerts (OPEN transition)
- [ ] Add thread pool utilization alerts
- [ ] Add fallback invocation rate alerts
- [ ] Add retry storm detection alerts
- [ ] Add cascading failure detection (multiple OPEN)

### Testing
- [ ] Unit test: circuit breaker opens at correct threshold
- [ ] Unit test: fallback returns valid degraded response
- [ ] Integration test: bulkhead isolation
- [ ] Load test: latency injection in dependencies
- [ ] Chaos test: single and multiple dependency failures

---

## Phase 6: Post-Incident (After Resolution)

### Documentation
- [ ] Complete INCIDENT_REPORT.md with timeline
- [ ] Complete ROOT_CAUSE.md with 5 Whys analysis
- [ ] Update runbooks with circuit breaker configuration standards
- [ ] Document bulkhead configuration templates

### Action Items
- [ ] Create JIRA tickets for all remediation actions
- [ ] Assign owners and target dates
- [ ] Schedule resilience testing for next sprint
- [ ] Schedule follow-up review

### Prevention
- [ ] Enforce circuit breaker configuration validation in CI/CD
- [ ] Add circuit breaker check to deployment pipeline gates
- [ ] Schedule quarterly resilience review
- [ ] Integrate chaos engineering into CI/CD pipeline

---

## Emergency Commands

```powershell
# Check circuit breaker state
curl http://order-service:8080/actuator/circuitbreakers

# Force circuit breaker OPEN
curl -X POST http://order-service:8080/actuator/circuitbreakers/paymentService/transitionOpen

# Force circuit breaker CLOSED
curl -X POST http://order-service:8080/actuator/circuitbreakers/paymentService/transitionClose

# Check thread pool status
curl http://order-service:8080/actuator/metrics/resilience4j.bulkhead.active.thread.count

# Check health (includes circuit breaker state)
curl http://order-service:8080/actuator/health

# Remove service from discovery (Eureka)
curl -X DELETE http://eureka:8761/eureka/apps/PAYMENT-SERVICE/{instance-id}

# Disable retries (via config refresh)
curl -X POST http://order-service:8080/actuator/refresh -d '{"resilience4j.retry.backends.paymentService.maxRetryAttempts": 0}'

# View Zipkin traces for failing service
curl "http://zipkin:9411/api/v2/traces?serviceName=payment-service&limit=50&lookback=3600000"
```

---

## Emergency Contacts

| Role | Name | Phone | Slack |
|------|------|-------|-------|
| SRE Primary | Mike Chen | +1-555-0200 | @mike.chen |
| SRE Secondary | Anna Lee | +1-555-0201 | @anna.lee |
| Platform Lead | James Wilson | +1-555-0202 | @james.wilson |
| Payment Team | Priya Sharma | +1-555-0203 | @priya.sharma |
| Incident Commander | Tom Mueller | +1-555-0204 | @tom.mueller |

---

## Checklist Version
- Version: 1.0
- Last Updated: 2026-07-21
- Approved By: Platform SRE Lead
- Next Review Date: 2026-10-21
