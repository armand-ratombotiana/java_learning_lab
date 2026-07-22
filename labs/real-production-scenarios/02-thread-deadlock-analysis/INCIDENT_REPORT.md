# Incident Report: Lock Coordinator Deadlock Under Peak Load

**Incident ID**: INC-2024-0520-DEADLOCK
**Severity**: P1 (SEV-2 equivalent)
**Date**: May 20-24, 2024
**Affected Service**: Distributed Lock Coordinator (global deployment)
**Duration**: 5 days (intermittent hangs, 4-5 times per hour)
**Detection**: Latency SLO breach alert for lease acquisition requests

## Executive Summary

On May 20, 2024, Google's distributed lock coordinator service experienced intermittent application hangs of exactly 30 seconds during peak traffic periods. The 30-second hang duration was caused by a watchdog thread that detected worker thread unresponsiveness and forcibly reset the node. The root cause was nested synchronized blocks with inconsistent lock ordering across code paths: some methods acquired Lock A then Lock B, while others acquired Lock B then Lock A. Under high concurrency, this created a circular wait condition. The fix involved enforcing consistent lock ordering and replacing synchronized blocks with ReentrantLock tryLock(timeout) patterns.

## Timeline (All Times UTC)

### Day 1 — May 20

| Time | Event |
|------|-------|
| 14:30 | PagerDuty alert: "LockCoordinator p99 latency > 10s" |
| 14:32 | On-call acknowledges. Observes p99 latency at 31s, baseline is 50ms |
| 14:35 | Check service dashboard: all nodes appear healthy, no errors |
| 14:40 | Graph latency over time: spikes last exactly 30s, occur every 12-15 min |
| 14:45 | Check CPU: not elevated. Check memory: normal. GC: no issues |
| 14:50 | Initial hypothesis: downstream Spanner latency causing thread pool exhaustion |
| 15:00 | Check Spanner metrics: latency normal, no contention |
| 15:15 | Check thread pool: active threads normal, pool not exhausted |
| 15:30 | Escalate to Java Platform team for deeper analysis |

### Day 2 — May 21

| Time | Event |
|------|-------|
| 09:00 | Team reviews hypothesis: thread contention or lock contention |
| 10:00 | Enable JFR recording with LockInstances event on canary node |
| 11:00 | Take first thread dump during a hang event |
| 11:05 | Thread dump shows threads in BLOCKED state, waiting on monitors |
| 11:10 | Take 3 thread dumps 10 seconds apart (standard deadlock detection practice) |
| 11:30 | JFR lock contention analysis: high contention on LeaseRegistry and LockState locks |
| 12:00 | Thread dump analysis: two threads hold opposing locks waiting for each other |

### Day 3 — May 22

| Time | Event |
|------|-------|
| 10:00 | Reproduce deadlock in local test environment |
| 10:30 | Write multi-threaded test that triggers deadlock consistently |
| 11:00 | Confirm: LeaseRegistry lock and LockState lock involved in cycle |
| 12:00 | Trace call paths for both locks |
| 13:00 | Found code path A: acquireLease() → LeaseRegistry → LockState |
| 13:15 | Found code path B: revokeLock() → LockState → LeaseRegistry ← INCONSISTENT |
| 14:00 | Also find 3 other code paths with inconsistent ordering |
| 15:00 | Design fix: enforce lock ordering (LeaseRegistry always first) |
| 15:30 | Design fix: replace synchronized with ReentrantLock tryLock(timeout) |
| 16:00 | Code review of fix |
| 17:00 | Deploy to staging, run stress test — no deadlocks |

### Day 4 — May 23

| Time | Event |
|------|-------|
| 08:00 | Canary deployment to 5% of production nodes |
| 10:00 | Monitor canary: no 30s latency spikes observed |
| 16:00 | 12 hours canary clean. Deploy to 50% of traffic |
| 20:00 | No incidents in 50% rollout. Full rollout approved |

### Day 5 — May 24

| Time | Event |
|------|-------|
| 02:00 | Full deployment to all 200+ nodes |
| 10:00 | Zero latency spikes in 8 hours post-deployment |
| 12:00 | Incident declared resolved |
| 16:00 | Postmortem meeting |

## Key Findings

1. **Root Cause**: Inconsistent lock ordering across code paths created circular wait condition under high concurrency
2. **Detection Gap**: No JFR lock contention monitoring enabled; latency anomaly was the only signal
3. **Fix Gap**: synchronized blocks have no timeout mechanism — threads wait forever
4. **Watchdog Masking**: The 30s watchdog thread masked the deadlock by resetting nodes, making diagnosis harder

## Action Items

| # | Action | Owner | Status |
|---|--------|-------|--------|
| 1 | Enforce consistent lock ordering (LeaseRegistry → LockState) | Platform Team | Done |
| 2 | Replace synchronized with ReentrantLock tryLock() with timeout | Platform Team | Done |
| 3 | Enable JFR LockInstances events on all production nodes | SRE Team | Done |
| 4 | Add thread dump automation to hang detection runbook | SRE Team | Done |
| 5 | Create Checkstyle rule to detect inconsistent lock ordering | Platform Team | Done |
| 6 | Add deadlock detection test to CI/CD pipeline | QA Team | Done |

## Detailed Timeline Analysis

### Pre-Incident Conditions

The deadlock was not introduced by a recent code change — it was latent in the codebase for approximately 18 months. Several factors combined to trigger it:

1. **Traffic growth**: The service scaled from 2,000 TPS to 10,000+ TPS over 18 months, increasing the probability of concurrent lock acquisition
2. **Node count growth**: From 50 nodes to 200+ nodes, increasing surface area
3. **No lock ordering convention**: The org had no standard for lock ordering, so different teams wrote methods with different lock orders

### Why the Watchdog Masked the Problem

The 30-second watchdog thread was designed to detect hung workers and reset them. It served as a "safety net" that:

1. Prevented permanent hangs (the service would always recover after 30 seconds)
2. Made the deadlock invisible in most monitoring (no crash, no errors, just latency spikes)
3. Delayed root cause identification by 3 days (initially assumed to be a latency issue, not a concurrency issue)

### Similar Google SRE Incidents

Google's internal postmortem database documented three similar deadlock incidents:

1. **Bigtable cell server (2016)**: Lock ordering violation between tablet metadata locks — 17-hour outage
2. **Spanner transaction manager (2018)**: Inconsistent lock ordering between read and write paths — 4-hour partial outage
3. **Google Ads serving (2020)**: Deadlock between ad selection and budget update locks — 45-minute P0

All three were fixed with the same approach: enforce consistent lock ordering and add tryLock timeouts.

## Incident Response Timeline

| Time | Action | Result |
|------|--------|--------|
| Week 1, 14:32 | P1 declared: lock coordinator unresponsive | Bridge call initiated |
| Week 1, 15:00 | Thread dumps captured | Revealed BLOCKED threads on two monitors |
| Week 1, 15:30 | Initial restart | Nodes recovered, root cause unknown |
| Week 1-2 | Recurring 30s hangs every 12-15 min | Watchdog masking the deadlock |
| Week 2, Mon | Java Platform team begins investigation | Code path analysis |
| Week 3, Wed | Deadlock reproduced locally | Root cause confirmed |
| Week 3, Thu | Fix deployed to canary | No reoccurrence in 12h |
| Week 4, Mon | Full production rollout | Deadlock eliminated |
| Week 4, Fri | Postmortem completed | Prevention measures approved |

## SLA Impact Analysis

| Metric | Target | During Incident | Impact |
|--------|--------|-----------------|--------|
| Lease Acquisition P99 | < 200ms | 31,000ms | 155x degradation |
| Service Availability | 99.99% | 99.82% | 0.17% below target |
| Error Rate | < 0.1% | 12% | 120x increase |
| MTTR | < 60 min | 5 days | 120x above target |

### System Metrics During Deadlock Events

| Metric | Normal (P50) | During Deadlock | Post-Fix |
|--------|-------------|-----------------|----------|
| P50 Latency | 12ms | 28,000ms | 13ms |
| P99 Latency | 45ms | 31,000ms | 42ms |
| Blocked Threads | 0-2 | 12-18 | 0-1 |
| Active Threads | 15-18 | 4-8 (blocked, not active) | 15-18 |
| Pending Tasks | 0-5 | 500-2,000 | 0-3 |
| Error Rate | 0.01% | 12% | 0.01% |

### Cost of the Incident

- Engineering hours: ~80 hours (5 engineers × 8 hours/day × 2 days)
- Infrastructure waste: $3,200 (extra capacity, debugging tooling)
- Downstream team impact: ~20 hours (dependent teams, incident response)
- Total estimated cost: $85,000+

### Root Cause Classification
- **Type**: Concurrency / Thread Deadlock
- **Category**: Inconsistent Lock Ordering
- **CWE**: CWE-833 (Deadlock), CWE-667 (Improper Locking)
- **Severity**: P1 / SEV-2

## Code Path Audit Results

The complete audit of the LockCoordinator class revealed:

| Method | Lines of Code | Lock Order | Risk Level | Fix Applied |
|--------|---------------|------------|------------|-------------|
| acquireLease() | 28 | LeaseRegistry → LockState | Safe | None needed |
| renewLease() | 22 | LeaseRegistry → LockState | Safe | None needed |
| getLeaseStatus() | 15 | LeaseRegistry → LockState | Safe | None needed |
| releaseLease() | 18 | LeaseRegistry → LockState | Safe | None needed |
| revokeLock() | 25 | LockState → LeaseRegistry | HIGH | Reordered |
| cancelLease() | 20 | LockState → LeaseRegistry | HIGH | Reordered |
| releaseExpiredLocks() | 30 | LockState → LeaseRegistry | HIGH | Reordered |
| bulkRevokeLeases() | 35 | LockState → LeaseRegistry | HIGH | Reordered |
| forceUnlock() | 12 | LockState → LeaseRegistry | HIGH | Reordered |

## Performance Regression Timeline

| Day | Incident Duration | Node Restarts | Lock Acquisition P99 | Root Cause Status |
|-----|------------------|---------------|---------------------|-------------------|
| Week 1, Thu | 30s | 28 | 31,000ms | Unknown |
| Week 1, Fri | 30s | 24 | 29,500ms | Under investigation |
| Week 2, Mon | 30s | 22 | 30,100ms | Under investigation |
| Week 2, Tue | 30s | 26 | 30,400ms | Thread dumps collected |
| Week 2, Wed | 30s | 20 | 28,900ms | Lock contention confirmed |
| Week 3, Mon | 30s | 18 | 30,200ms | Reproduced locally |
| Week 3, Thu | 30s | 0 (fix deployed) | 42ms | Fix deployed to canary |
| Week 4, Mon | 0 | 0 | 42ms | Full rollout completed |

## Lessons Learned

1. Inconsistent lock ordering is the most common cause of deadlocks in Java applications
2. Watchdog that restarts the JVM masks the deadlock and delays root cause discovery
3. Thread dumps are the most valuable diagnostic tool — capture at least 3 samples
4. Lock ordering conventions should be documented and enforced in code review
5. ThreadMXBean.findDeadlockedThreads() should be part of every production health check
6. ReentrantLock with tryLock(timeout) is strictly safer than synchronized for nested locking

### Process Improvements

1. **Lock ordering convention**: All methods must acquire locks in LeaseRegistry → LockState order
2. **Static analysis**: SpotBugs and ErrorProne rules for lock ordering consistency
3. **Deadlock detection**: ThreadMXBean.findDeadlockedThreads() in health check endpoint
4. **Lock monitoring**: Enable JFR LockInstances events in production

## Communication Log

During the incident, the following communications occurred:

| Time | From | To | Message |
|------|------|----|---------|
| 14:32 | On-call SRE | Incident Channel | "P1 confirmed: lock coordinator p99 at 31s, CPU normal, threads blocked" |
| 14:45 | On-call SRE | Java Platform Lead | "Need help analyzing thread dumps — suspect lock contention" |
| 15:00 | Java Platform Lead | On-call SRE | "Thread dumps show BLOCKED on LeaseRegistry/LockState locks" |
| 15:30 | SRE Manager | All | "Declaring P1 incident. Eng team assembled for bridge call" |
| Day 3, 10:00 | Java Engineer | Bridge | "Reproduced deadlock locally. Confirming root cause" |
| Day 3, 14:00 | Java Engineer | Bridge | "Fix ready: consistent lock ordering + ReentrantLock tryLock" |
| Day 4, 08:00 | SRE | Bridge | "Canary deployed. Monitoring for regression" |
| Day 5, 12:00 | Incident Commander | All | "Incident resolved. Postmortem scheduled" |

## Cost of the Incident

- Engineering hours: ~80 hours (5 engineers × 8 hours/day × 2 days)
- Infrastructure waste: $3,200 (extra capacity, debugging tooling)
- Downstream team impact: ~20 hours (dependent teams)
- Opportunity cost: ~$50,000 (delayed feature development)
- Total estimated cost: $85,000+

## Detection Gap Analysis

| Detection Method | Status | Gap |
|-----------------|--------|-----|
| P99 latency alert | Enabled | Alerted immediately but could not identify root cause |
| Thread dump analysis | Manual (on request) | Required Java Platform engineer to interpret |
| JFR LockInstances | Not enabled | Would have shown lock contention in real-time |
| Deadlock health check | Not implemented | ThreadMXBean.findDeadlockedThreads() not called |
| Watchdog restart | Enabled | Restarted JVM at 30s, masking the deadlock signature |
| Automated lock ordering check | Not implemented | ErrorProne/SpotBugs rules not configured |

## Incident Root Cause Classification

- **Type**: Concurrency / Thread Deadlock
- **Category**: Inconsistent Lock Ordering
- **CWE**: CWE-833 (Deadlock), CWE-667 (Improper Locking)
- **Severity**: P1 / SEV-2
- **Root Cause**: 5 of 9 lock-related methods acquired locks in order LockState → LeaseRegistry instead of the established LeaseRegistry → LockState order

## Post-Incident Actions

| Action | Owner | Status |
|--------|-------|--------|
| Lock ordering convention documented | Java Platform | Complete |
| SpotBugs/ErrorProne rules enforced | Platform Team | Complete |
| ThreadMXBean health check deployed | SRE | Complete |
| JFR LockInstances enabled in production | SRE | Complete |
| Deadlock recovery runbook created | SRE | Complete |

