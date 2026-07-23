# Lab 07 — Circuit Breaker: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 30 seconds (circuit opens, fallback activates) |
| RPO | 0 (no data loss — deferred processing) |
| MTD | 5 minutes before fallback must activate |

## Scenarios

### Scenario A: Downstream Service Failure

**Trigger:** Payment service timeout > 50% of calls
**Recovery:**
1. Circuit breaker opens automatically (50% failure threshold)
2. All payment calls fail fast → order queued for retry
3. Deferred payment processor retries every 5 minutes
4. When payment service recovers, circuit closes, normal flow resumes

### Scenario B: Circuit Flapping

**Trigger:** Circuit opens/closes rapidly due to intermittent timeouts
**Recovery:**
1. Detect rapid transitions (flapping)
2. Force circuit OPEN (manual override) to give downstream time to stabilize
3. Increase wait duration (OPEN → HALF_OPEN timer)
4. Investigate downstream instability causes
5. Manual HALF_OPEN when stabilized

### Scenario C: Fallback Failure

**Trigger:** Fallback (cached response) also failing
**Recovery:**
1. Circuit breaker on fallback path (meta-circuit)
2. Return static error message to user
3. Alert immediately — fallback should never fail
4. Investigate cache/fallback data source health

## Runbook

```yaml
symptoms:
  - "Circuit breaker OPEN"
  - "Error rate spike on downstream"
  - "Fallback rate > 50%"

immediate_actions:
  - "Check downstream service health"
  - "Verify circuit opened correctly (not false positive)"
  - "Increase wait duration if flapping"

diagnosis:
  - "Check circuit breaker metrics: state, failure rate, call volume"
  - "Check downstream: is it healthy? Slow? Dead?"
  - "Check for recent changes to downstream"

recovery:
  - "Automatic: circuit will transition HALF_OPEN after wait duration"
  - "Manual: force CLOSED if downstream recovered and circuit hasn't transitioned"
  - "Emergency: increase failure threshold if false positive"

fix:
  - "Fix downstream service issue"
  - "Adjust circuit breaker thresholds if needed"
  - "Test fallback paths for correctness"
```
