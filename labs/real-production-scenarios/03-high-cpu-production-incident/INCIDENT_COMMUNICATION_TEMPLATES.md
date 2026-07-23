# Lab 03 — High CPU / ReDoS: Incident Communication Templates

## Initial Alert

```
Title: [P0] CPU > 95% — Content Moderation Service — Global
Service: Content Moderation / Regex Engine
Severity: P0

Metrics:
- CPU per node: 95%+ (normal: 30-40%)
- P99 latency: 10s (normal: 50ms)
- Error rate: 35% timeout (normal: < 0.1%)
- Thread pool: 200/200 active (normal: 60/200)

Impact: All content uploads experiencing 10s+ latency or timeout
Region: Global

Action: Acknowledge immediately — disable problematic policy rule
```

## Status Updates

### Investigating

```
STATUS #1 — INC-2024-003 — INVESTIGATING

What: CPU spike to 95%+ globally on content moderation service
Impact: All content uploads failing or timing out
Severity: P0

Actions:
- async-profiler sampling in progress
- Analyzing thread dumps for hot methods
- Checking recent deployments (none in 24h)
- Correlating with traffic patterns

Next update: 15 minutes
```

### Identified

```
STATUS #2 — INC-2024-003 — IDENTIFIED

Root Cause: ReDoS attack — single user post with crafted payload triggered
catastrophic backtracking in regex policy rule #1427.

Action: Disabling rule #1427 via feature flag — expect recovery in 2 minutes.
Permanent fix in progress (atomic groups + input validation + regex timeout).
```

### Resolved

```
STATUS #3 — INC-2024-003 — RESOLVED

Rule #1427 disabled at 02:30 UTC.
CPU dropped from 95% to 30% within 2 minutes.
Latency returned to baseline: P50 50ms, P99 80ms.
Error rate: 0.02%.

Post-mortem actions:
1. Rewrite rule #1427 with atomic groups
2. Add input length validation (max 1024 chars)
3. Add regex timeout wrapper (100ms default)
4. Scan all 2,147 existing patterns for ReDoS vulnerabilities
5. Add automated ReDoS detection in CI/CD

This is the FINAL update.
```
