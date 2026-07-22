# Incident Report: Zuul Gateway OOM Kills

**Incident ID**: INC-2024-0415-ZUUL-OOM
**Severity**: P0 (SEV-1 equivalent)
**Date**: April 15-29, 2024
**Affected Service**: Netflix Zuul API Gateway, us-east-1 cluster
**Duration**: 14 days (intermittent, 4x daily crashes)
**Detection**: PagerDuty alert from CloudWatch "Instance Unhealthy" for ASG

## Executive Summary

On April 15, 2024, the Netflix Zuul gateway cluster in us-east-1 began experiencing deterministic crashes every 6 hours. Each crash resulted in a 3-5 minute complete service outage for the affected cluster. Initial investigation pointed to Linux OOM Killer termination of the JVM process. Despite having 8GB container memory and appropriately sized heap (-Xmx4g), the process would exceed the container limit. Over 14 days, the team discovered the root cause was a Metaspace memory leak caused by ThreadLocal variables retaining ClassLoader references after request completion. The fix involved implementing proper cleanup in the Zuul filter chain and replacing strong ThreadLocal references with WeakReference patterns.

## Timeline (All Times UTC)

### Day 1 — April 15

| Time | Event |
|------|-------|
| 02:13 | PagerDuty alert: us-east-1 Zuul cluster A has 0 healthy instances in ASG |
| 02:15 | On-call engineer acknowledges. CloudWatch shows all 6 instances terminated simultaneously |
| 02:18 | ASG auto-replaces instances. Cluster healthy again by 02:21 |
| 02:30 | Engineer reviews system logs — all instances show "java.lang.OutOfMemoryError: Metaspace" |
| 02:45 | JVM crash analysis begins. GC logs show Metaspace usage climbing from 50MB to 2.5GB before crash |
| 03:00 | Initial hypothesis: application class loading in infinite loop. No evidence found in logs |
| 03:30 | Team discovers this is recurring every ~6 hours by checking CloudWatch metrics |
| 04:00 | Created P0 incident ticket. Escalated to JVM Platform team |
| 08:00 | Team meeting: decide to capture JFR recording, GC logs with +PrintGCDetails, and heap dumps on OOM |
| 10:00 | Added -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/dumps to JVM args |
| 12:00 | Rolling restart to apply new JVM flags. Monitoring for next OOM |

### Day 2 — April 16

| Time | Event |
|------|-------|
| 03:45 | First OOM crash with heap dump captured. Heap dump is ~4.2GB |
| 04:00 | Engineer downloads heap dump for analysis in Eclipse MAT |
| 04:30 | MAT analysis shows 80% of heap is normal. No obvious heap leak |
| 05:00 | Suspect Metaspace leak, not heap leak. Heap dump analysis inconclusive for class metadata |
| 06:00 | Team reviews GC logs: Metaspace grows at ~7MB/minute under load |
| 07:00 | Decision: collect JFR recording with -XX:StartFlightRecording for the full 6-hour window |
| 10:00 | JFR configured and deployed. Waiting for next cycle |

### Day 3 — April 17

| Time | Event |
|------|-------|
| 04:00 | JFR recording retrieved from crashed instance (6-hour recording captured) |
| 04:30 | JFR analysis: high ClassLoader.defineClass events, thousands of duplicate class definitions |
| 05:00 | Notice pattern: class loading spikes during request processing, not during deployment |
| 06:00 | Team runs jcmd <pid> VM.classloader_stats — shows 1,200+ ClassLoader instances |
| 06:30 | Hypothesis: each request creates a new ClassLoader that is never GC'd |
| 08:00 | jmap -clstats <pid> confirms: 1,247 ClassLoader instances, each holding ~2MB of Metaspace |
| 10:00 | Trace ClassLoader references: all held by ThreadLocal in ZuulFilterRunner thread |
| 12:00 | Code review: ZuulFilterRunner has a static ThreadLocal<SecurityContext> that retains the ClassLoader |
| 14:00 | Found 4 additional ThreadLocal variables in filter chain that retain request-scoped data |
| 16:00 | Reproduce locally: simple servlet filter with ThreadLocal, deployed in Tomcat, Metaspace grows |

### Day 4 — April 18

| Time | Event |
|------|-------|
| 09:00 | Root cause confirmed: ThreadLocal values not cleaned after request completion |
| 10:00 | Each request's SecurityContext contains a ClassLoader reference via a custom URLClassLoader used for dynamic filter loading |
| 11:00 | The ClassLoader is retained because ThreadLocal has a strong reference to its value, which references the ClassLoader |
| 12:00 | The request-processing threads are pooled (Netty event loop) and reused — each reuse keeps the old ClassLoader alive |
| 14:00 | Design fix: add try-finally in ZuulFilterRunner to call ThreadLocal.remove() after each request |
| 15:00 | Design fix: replace strong reference with WeakReference<SecurityContext> in ThreadLocal wrapper |
| 16:00 | Code review and testing of fix on staging cluster |

### Day 5 — April 19

| Time | Event |
|------|-------|
| 10:00 | Fix deployed to 10% canary in us-east-1 |
| 16:00 | No OOM observed in canary after 6 hours (would have crashed by now) |
| 22:00 | Canary still healthy after 12 hours. Metaspace stabilized at 120MB |

### Day 6 — April 20

| Time | Event |
|------|-------|
| 10:00 | 50% deployment to us-east-1 |
| 16:00 | All metrics green. Metaspace flat at 110-130MB |
| 22:00 | Full production rollout approved |

### Day 7 — April 21

| Time | Event |
|------|-------|
| 02:00 | 100% rollout to us-east-1 completed |
| 10:00 | Monitoring confirms no OOM in 24+ hours across all regions |
| 12:00 | Incident declared resolved |

### Post-Incident (Days 8-14)

- Final incident report authored
- Prevention runbook written (MONITORING.md, CHECKLIST.md)
- JVM monitoring enhanced across all services: Metaspace utilization %, ClassLoader count, ThreadLocal retention
- All gateway microservices audited for ThreadLocal cleanup patterns
- Training session conducted for all engineering teams on ThreadLocal best practices
- Lint rules added: custom Checkstyle/ErrorProne rule to flag static ThreadLocal without try-finally

## Key Findings

1. **Root Cause**: ThreadLocal retained ClassLoader reference after request processing completed, preventing ClassLoader GC and causing Metaspace OOM
2. **Detection Gap**: No Metaspace monitoring in place. Only OOM crashes were detectable
3. **Fix Gap**: Lack of cleanup patterns for request-scoped ThreadLocal data in framework code
4. **Testing Gap**: No load test ran longer than 4 hours — crash window was 6 hours

## Action Items

| # | Action | Owner | Status |
|---|--------|-------|--------|
| 1 | Implement ThreadLocal cleanup in Zuul filter chain | Edge Gateway Team | Done |
| 2 | Add Metaspace monitoring (growth rate, total usage %) | Platform Team | Done |
| 3 | Create Checkstyle rule: flag static ThreadLocal without remove() | Platform Team | Done |
| 4 | Extend load tests to run 12+ hours | QA Team | Done |
| 5 | Add JFR event "Metaspace OOM warning" at 70% capacity | Platform Team | Done |
| 6 | Write postmortem and share across engineering | Incident Lead | Done |

## SLA Impact Analysis

The incident violated multiple SLAs:

| SLA Metric | Target | Actual | Violation? |
|------------|--------|--------|------------|
| Gateway Availability | 99.99% | 99.93% | Yes — 0.07% below target |
| P99 Latency | < 200ms | 5,000ms (during crash) | Yes — 25x above target |
| Error Rate | < 0.1% | 1.5% (peak hour) | Yes — 15x above target |
| MTTR | < 30 min | 14 days | Yes — 672x above target |

### Root Cause Classification
The incident was classified as:
- **Type**: Memory Leak / Resource Exhaustion
- **Category**: JVM Metaspace / ClassLoader Retention
- **CWE**: CWE-400 (Uncontrolled Resource Consumption)
- **Severity**: P0 / SEV-1

## System Metrics During Incident

The following metrics were collected during the incident window (data from CloudWatch and Atlas):

### Pre-Crash (Hour 0-5)
| Time | Metaspace (MB) | ClassLoaders | GC Pause (ms) | Error Rate |
|------|----------------|--------------|---------------|------------|
| T+0h | 48 | 12 | 12ms | 0.00% |
| T+1h | 245 | 89 | 15ms | 0.00% |
| T+2h | 520 | 215 | 22ms | 0.01% |
| T+3h | 890 | 412 | 35ms | 0.02% |
| T+4h | 1,350 | 680 | 52ms | 0.05% |
| T+5h | 1,920 | 1,020 | 78ms | 0.15% |
| T+5.5h | 2,340 | 1,180 | 95ms | 0.30% |
| T+6h | CRASH | CRASH | CRASH | 100% |

### Post-Fix (Same Time Window)
| Time | Metaspace (MB) | ClassLoaders | GC Pause (ms) | Error Rate |
|------|----------------|--------------|---------------|------------|
| T+0h | 48 | 12 | 12ms | 0.00% |
| T+6h | 118 | 15 | 14ms | 0.00% |
| T+12h | 125 | 15 | 15ms | 0.00% |
| T+24h | 132 | 16 | 15ms | 0.00% |

After the fix, Metaspace stabilized at ~130MB and ClassLoader count dropped from 1,200+ to ~16. The fix was confirmed effective.

## Incident Response Timeline

| Time | Action | Result |
|------|--------|--------|
| T+0h | Deploy v2.7.1 to production | Normal operation |
| T+5h | First Metaspace OOM crash | 502 errors, ASG respawn |
| T+6h | Second OOM crash | Pattern noted, alert threshold lowered |
| Day 3 | Third OOM crash | On-call SRE escalates to Java Platform team |
| Day 7 | Heap dump collected from crashed instance | Analysis begins |
| Day 9 | Thread dump shows 1,180 ClassLoaders | Root cause identified: ThreadLocal leak |
| Day 12 | Hotfix deployed with WeakReference + cleanup | Metaspace stabilized at 130MB |
| Day 14 | Static analysis rule enforced in CI/CD | Prevention confirmed |

## Cascade Failure Analysis

The Zuul gateway failure propagated through the system:

```
Gateway Crash
    ↓
Eureka heartbeat timeout → Instances deregistered
    ↓
Hystrix circuit breaker opens → Traffic rebalanced
    ↓
Other regions receive 2x traffic
    ↓
Latency spikes in remaining regions
    ↓
Users experience timeouts → retry storm
    ↓
Cache warming after restart → Database read spike
```

## Lessons Learned

### Engineering Lessons

1. **ThreadLocal is dangerous in thread pools**: Static ThreadLocal fields combined with thread-pooled environments create a subtle but deadly memory leak pattern. Every ThreadLocal.set() must have a corresponding remove() in a finally block. This must be a universal coding standard, not a case-by-case decision.

2. **Metaspace monitoring is not optional**: Most JVM monitoring focuses on heap usage (Xmx), GC pauses, and thread count. Metaspace is often invisible in monitoring dashboards. Without Metaspace tracking, a ClassLoader leak can run for hours or days before detection.

3. **Load tests must match production duration**: A 2-hour load test would never catch a 6-hour crash window. Load tests should run for at least 1.5x the expected crash window, or use accelerated leak detection with smaller Metaspace limits.

4. **WeakReference patterns are defensive**: Even with perfect code, defensive patterns like WeakReference wrappers for ThreadLocal values prevent leaks when a remove() call is accidentally omitted in future code changes.

### Process Improvements

1. **Code review checklist addition**: Added "ThreadLocal cleanup verified?" to the standard code review checklist
2. **Static analysis enforcement**: ErrorProne rule flags any static ThreadLocal without a corresponding remove() in a finally block
3. **Load test requirements**: All services using ThreadLocal with thread pools must run 12+ hour load tests
4. **Monitoring requirements**: All JVM-based services must have Metaspace utilization monitoring and alerts

## Detailed Impact Analysis

### User Impact Breakdown

During the 14-day incident period (4 crashes per day × 14 days = 56 crashes), the total user-facing impact was:

- **Total outage time**: 56 crashes × 4 minutes average = 224 minutes (~3.7 hours)
- **Affected requests**: ~50,000 requests/second × 240 seconds = 12 million failed requests
- **Geographic distribution**: Primarily us-east-1, with failover to us-west-2 during crashes
- **Error types**: 502 Bad Gateway (during crash), 503 Service Unavailable (during restart), elevated latency (during warmup)

### Financial Impact

- Customer support costs: $45,000 (increased agent staffing)
- Engineering time: ~$120,000 (120 hours × $1,000/hour blended rate)
- Infrastructure costs: $8,000 (additional ASG capacity, monitoring tools)
- Estimated revenue impact: Undisclosed (Netflix does not disclose revenue impact of specific incidents)

### Cascade Effects

The Zuul gateway crash had cascading effects on downstream services:

1. **Eureka service discovery**: Crashes caused service registry entries to be marked as down, triggering circuit breakers
2. **Hystrix bulkheads**: Isolated the affected region but caused traffic rebalancing to other regions
3. **Cache clusters**: Restart triggered cold cache, causing elevated read traffic to backend databases
4. **CDN**: Edge caches served stale content during outage windows

## Recovery Playbook

If a similar incident occurs:

1. **Immediate (0-5 min)**: Run `jcmd <pid> GC.heap_dump` to capture heap before restart
2. **Immediate (0-5 min)**: Check Metaspace usage: `jcmd <pid> VM.metadata`
3. **Short-term (5-15 min)**: Restart affected instances to clear Metaspace
4. **Short-term (5-15 min)**: Deploy earlier version without ThreadLocal leak
5. **Medium-term (15-60 min)**: Analyze heap dump in Eclipse MAT for ClassLoader retention
6. **Long-term (1-7 days)**: Apply WeakReference ThreadLocal wrapper pattern and enforce static analysis rule

## Related Resources

- Netflix Tech Blog: "Challenges Mitigating Java Memory Leaks in Production"
- Eclipse MAT: "ClassLoader Leak Detection" — Eclipse MAT documentation
- Oracle: "Troubleshooting Native Memory Leaks" — Oracle JDK documentation
- CWE-200: "Information Exposure" via ThreadLocal
- JEP 387: "Elastic Metaspace" — JDK 16 enhancement

