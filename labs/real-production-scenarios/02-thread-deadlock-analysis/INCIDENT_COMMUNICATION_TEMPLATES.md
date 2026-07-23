# Lab 02 — Thread Deadlock: Incident Communication Templates

## Initial Page Alert

```
Title: [SEV1] Lock Coordinator Service Hang — 30-second Freezes — us-east-1
Service: Distributed Lock Coordinator
Severity: SEV1

Alert: Service latency P99 > 5s (baseline: 50ms) for 5 consecutive minutes
Error rate: 15% lease acquisition timeouts
Affected: 200+ nodes in us-east-1

Impact: Cascading timeouts to dependent services
Duration per event: ~30 seconds, recurring 4-5x per hour

Action: Acknowledge and investigate immediately
```

## Status Update Templates

### Investigating

```
STATUS #1 — INC-2024-002 — INVESTIGATING

What: Lock coordinator service experiencing intermittent 30-second hangs
Impact: 200+ nodes affected, lease acquisition failures, cascading timeouts
Severity: SEV1

Actions:
- Capturing 3-sample thread dumps at 5-second intervals
- Analyzing lock contention via JFR
- Checking for recent deployments (none in 72h)
- Checking database latency (normal)

Next update: 30 minutes
```

### Identified

```
STATUS #2 — INC-2024-002 — IDENTIFIED

Root Cause: Nested synchronized blocks with inconsistent lock ordering.
Code path A acquires LeaseRegistry then LockState lock.
Code path B acquires LockState then LeaseRegistry lock.
Two threads executing these paths simultaneously create a classic deadlock.

Mitigation: Deploying fix with ReentrantLock.tryLock(100ms).
Rolling out to canary (10%) now.
```

### Resolved

```
STATUS #3 — INC-2024-002 — RESOLVED

Fix deployed to 100% of instances.
- P99 latency: 45ms (baseline restored)
- Error rate: 0.02%
- Zero deadlocks detected in 60 minutes of monitoring

Post-mortem scheduled for Wednesday.
All lock acquisition patterns are being audited for ordering consistency.
```

## Post-Incident Summary

```
INCIDENT SUMMARY — INC-2024-002

Duration: 5 days (intermittent deadlocks)
Root Cause: Inconsistent lock ordering in LeaseRegistry/LockState
Fix: Replaced nested synchronized with ReentrantLock.tryLock()
Impact: 4-5 hangs per hour, 30 seconds each, during peak traffic
Action Items:
1. Audit all lock acquisition patterns in codebase
2. Add ArchUnit test for lock ordering
3. Add JFR-based lock contention dashboard
4. Implement ThreadMXBean deadlock detection in health checks
```
