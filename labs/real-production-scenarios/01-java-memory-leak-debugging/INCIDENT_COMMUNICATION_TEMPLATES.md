# Lab 01 — Java Memory Leak: Incident Communication Templates

## Initial Alert / Page Content

### PagerDuty Alert Payload

```
Title: [CRITICAL] Metaspace OOM — Gateway Cluster us-east-1 — 6-Hour Crash Pattern
Service: Zuul API Gateway
Severity: P0 (CRITICAL)
Region: us-east-1

Alert Details:
- 4 out of 6 gateway instances have crashed in the last 30 minutes
- JVM crash log: java.lang.OutOfMemoryError: Metaspace
- Error rate: 100% during crash windows
- Duration per crash: 3-5 minutes (JVM restart + cache warmup)
- Frequency: Every 6 hours (predictable)

Impact:
- All API requests to us-east-1 gateway cluster failing during crash windows
- Estimated 50,000+ requests/min affected per crash

Suggested Actions:
1. Acknowledge this incident
2. Join bridge: [link]
3. Follow runbook: https://runbooks/zuul-oom-metaspace
4. Consider failing over to us-west-2 cluster if impact persists

Runbook URL: https://runbooks/zuul-oom-investigation
Dashboard: https://grafana/d/zuul-oom
```

## Status Update Templates

### Investigating Status

```
STATUS UPDATE #1 — INC-2024-001

Status: 🔍 INVESTIGATING
Severity: P0
Date/Time: 2024-03-15 02:15 UTC

What is happening:
The Zuul API Gateway cluster in us-east-1 is experiencing JVM crashes due to 
java.lang.OutOfMemoryError: Metaspace. Crashes occur every 6 hours, with 3-5 
minutes of complete service unavailability during JVM restart.

Impact:
- Services affected: All API traffic through us-east-1 Zuul cluster
- Users affected: 100% of traffic through this region (estimated 50K+ RPM)
- Dependent services: All microservices behind the gateway
- Duration: 3-5 minutes per crash event (recurring every 6 hours)

What we are doing:
1. Analyzing heap dumps and JFR recordings from crashed instances (in progress)
2. Checking GC logs for class unloading statistics (in progress)
3. Reviewing recent deployments (none in last 72 hours — pre-existing issue)
4. Preparing manual failover script for next crash window

Next update: 02:45 UTC or upon significant findings

IC: Jane Smith (#jane)
Ops Lead: John Doe (#john)
Comms Lead: Alice Brown (#alice)
```

### Identified Status

```
STATUS UPDATE #2 — INC-2024-001

Status: ✅ IDENTIFIED
Severity: P0
Date/Time: 2024-03-15 04:30 UTC

Root cause identified:
A ThreadLocal leak in the request filter chain is preventing ClassLoader garbage 
collection. Each request creates a SecurityContext stored in a static ThreadLocal 
field without calling remove() after request completion. The ThreadLocal retains 
a strong reference to a URLClassLoader, which prevents Metaspace reclamation.

Over 6 hours, stale ThreadLocal entries accumulate across all thread pool 
threads, consuming available Metaspace until OOM.

Mitigation plan:
1. [IMMEDIATE] Restart gateway instances every 4 hours during low traffic to 
   reset Metaspace before OOM (temporary workaround)
2. [WITHIN 2 HOURS] Deploy fix: add try-finally with ThreadLocal.remove() in 
   filter chain error handling
3. [WITHIN 4 HOURS] Deploy fix: replace ThreadLocal with WeakThreadLocal wrapper

Mitigation ETA: 06:00 UTC

Impact update:
- 4 crash events mitigated via automated restart
- No change to customer impact pattern — still occurring every 6 hours

Next update: 06:00 UTC or upon deployment of fix
```

### Mitigating Status

```
STATUS UPDATE #3 — INC-2024-001

Status: 🔧 MITIGATING
Severity: P0
Date/Time: 2024-03-15 05:45 UTC

Mitigation in progress:
1. ✅ Deployed temporary workaround: hourly Metaspace health check + proactive 
   restart if utilization > 70%
2. ✅ Filter fix (ThreadLocal.remove()) deployed to canary (10% traffic)
3. ✅ Canary showing Metaspace stabilization — no growth in 30 minutes
4. 🔄 Rolling out fix to 100% of instances (estimated 15 min)

Impact:
- No crash events in the last 2 hours (proactive restart preventing OOM)
- Canary instances: Metaspace utilization stable at 35% (no growth)
- Non-canary instances: still on proactive restart schedule

Next update: 07:00 UTC or upon full rollout
```

### Resolved Status

```
STATUS UPDATE #4 — INC-2024-001

Status: ✅ RESOLVED
Severity: P0
Date/Time: 2024-03-15 07:30 UTC

Resolution details:
- Fix deployed to 100% of instances at 06:15 UTC
- Metaspace utilization stabilized at 30-35% on all instances (flat, no growth)
- Proactive restart schedule cancelled
- All metrics returning to baseline
- Error rate: 0.02% (baseline)
- P99 latency: 45ms (baseline)

Post-incident actions scheduled:
1. Post-mortem meeting: 2024-03-18 14:00 UTC
2. Add Metaspace growth rate alert
3. Add ClassLoader count metric
4. Add Checkstyle rule for ThreadLocal cleanup
5. Review all existing ThreadLocal usage across codebase

Final metrics:
- Total incidents: 14 crash events over 7 days
- Total downtime: ~49 minutes (14 events × 3.5 minutes average)
- Total engineering hours: ~120 hours
- Customer tickets: 847 related tickets

This is the FINAL update for INC-2024-001.
```

### Status Update for Executives

```
EXECUTIVE INCIDENT BRIEF — INC-2024-001

Subject: Executive Summary — Zuul Gateway OOM Incident

What happened:
The Zuul API Gateway cluster in us-east-1 experienced recurring OutOfMemory 
crashes every 6 hours due to a memory leak in the request filter chain.

Business impact:
- Total customer-facing downtime: ~49 minutes over 7 days
- Peak-hour traffic affected: ~50,000 requests/min during crash windows
- Customer support tickets spiked 300% during incident period
- Estimated revenue impact: $XX,XXX (quantified by finance team)

What we did:
1. Temporary mitigation: proactive instance restart before OOM
2. Root cause fix: added ThreadLocal cleanup in filter chain error handling
3. Prevention: added static analysis rules to prevent future ThreadLocal leaks

What we're doing to prevent recurrence:
1. ClassLoader count and Metaspace growth monitoring added
2. Mandatory try-with-resources for all resource cleanup
3. 12-hour load test in CI/CD pipeline
4. Code review checklist entry for ThreadLocal usage

Status: RESOLVED at 06:15 UTC on 2024-03-15

For detailed information, see the full post-mortem: [link]
```

## Stakeholder Communication Templates

### Internal Team (Slack)

```
🚨 INC-2024-001 — Zuul OOM Incident

@here — The us-east-1 Zuul cluster is crashing with OOM:Metaspace every 6 hours. 
We've identified the root cause as a ThreadLocal leak in the filter chain. 

Current actions:
- Patching the leak (add try-finally remove())
- Temporary workaround: proactive restart every 4 hours
- Canary deployed at 10%, monitoring for Metaspace stabilization

If you are affected by gateway issues, please:
1. Check if your service has retry logic for gateway failures
2. Implement exponential backoff if not already done
3. Use circuit breakers to gateway if your service can serve degraded

Post-mortem scheduled for Monday. Please add your notes to the incident doc.

Channel: #inc-zuul-oom
```

### Customer-Facing (Status Page)

```
GATEWAY PERFORMANCE DEGRADATION — INVESTIGATING

We are currently investigating performance issues with our API gateway in the 
us-east-1 region. Some customers may experience intermittent errors or timeouts 
when accessing our services.

Status: Investigating
Affected: API Gateway — us-east-1
Started: March 15, 2024 at 02:13 UTC
Last update: March 15, 2024 at 02:45 UTC

We will provide updates as more information becomes available.

—

GATEWAY PERFORMANCE — RESOLVED

The issue with our API gateway has been identified and resolved. All systems 
are operating normally. A full post-mortem will be published within 72 hours.

Status: Resolved
Duration: 5 hours 17 minutes (intermittent events over 7 days)
Resolution: Memory leak identified and patched

If you experienced issues during this time, please contact support if you need 
assistance with any affected transactions.
```

## Post-Incident Summary Template

```
POST-INCIDENT SUMMARY — INC-2024-001

Title: Zuul Gateway Metaspace OOM
Date: 2024-03-15
Duration: 7 days (recurring), final resolution: 5h 17m
Severity: P0
Root Cause: ThreadLocal leak preventing ClassLoader GC

Impact Summary:
- Total downtime: 49 minutes (14 crash events × 3.5 min)
- Users affected: 100% of us-east-1 API traffic per event
- Services affected: All microservices behind Zuul gateway
- Data loss: None

Key Metrics:
| Metric | Before Fix | After Fix | Improvement |
|--------|-----------|-----------|-------------|
| Metaspace growth | 50 MB/hour | 0.5 MB/hour | 100x |
| Crash frequency | Every 6 hours | None | Eliminated |
| P99 latency | 2-5s (post-restart) | 45ms | 50-100x |
| Error rate | 100% (during crash) | 0.02% | Eliminated |

Timeline:
See detailed timeline in the incident document.

Action Items:
1. ✅ Add Metaspace monitoring to gateway dashboard
2. ✅ Add ThreadLocal cleanup Checkstyle rule
3. 🔄 Review all ThreadLocal usage in codebase (due: 2 weeks)
4. 🔄 Add 12-hour load test to CI/CD (due: 1 month)
5. 📋 Evaluate migration to ScopedValue in Java 20+ (due: 3 months)

Lessons Learned:
- ThreadLocal cleanup should be enforced by static analysis
- Metaspace growth trends should be monitored proactively
- Leak detection thresholds should be seconds, not minutes
- Load tests should run for extended periods to catch slow leaks
