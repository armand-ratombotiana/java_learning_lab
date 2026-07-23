# Lab 03 — High CPU / ReDoS: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 5 minutes (feature flag disable of vulnerable pattern) |
| RPO | 0 (no data loss — requests fail but don't lose data) |
| MTD | 15 minutes before cascading failures |

## Disaster Scenarios

### Scenario A: Active ReDoS Attack

**Trigger:** Malicious regex payload causing CPU storm
**Impact:** 95%+ CPU across all nodes, 10s+ latency
**Recovery:**
1. Identify the vulnerable pattern via async-profiler
2. Disable the pattern via feature flag (2 minutes)
3. CPU and latency return to normal
4. Block the attack payload by IP/pattern
5. Fix the regex permanently

### Scenario B: Accidental ReDoS (Internal Bug)

**Trigger:** New regex pattern with nested quantifiers deployed
**Impact:** Gradual CPU increase as more requests hit the pattern
**Recovery:**
1. Rollback deployment containing the vulnerable pattern
2. CPU returns to normal within 5 minutes
3. Fix pattern with atomic groups
4. Add pre-deployment ReDoS scanning to CI/CD

### Scenario C: Multiple Patterns Vulnerable

**Trigger:** Audit reveals 10+ vulnerable patterns
**Impact:** Accumulated risk of ReDoS from various patterns
**Recovery:**
1. Implement regex timeout wrapper as immediate defense
2. Add input length validation globally
3. Fix patterns in priority order (by traffic volume)
4. Add runtime ReDoS detection and auto-disable

## Runbook Quick Reference

```yaml
symptoms:
  - "CPU > 90% across service"
  - "P99 latency > 1s"
  - "Thread pool saturated"
  - "Stack traces in java.util.regex.Pattern"

immediate_actions:
  - "Run async-profiler: profiler.sh -d 30 -e cpu -f flame.html <pid>"
  - "If flame graph shows 85%+ in Pattern.* → ReDoS attack"
  - "Disable the most recently deployed or most CPU-intensive pattern"
  - "Monitor CPU drop"

fix:
  - "Add atomic groups: (?>...) to prevent backtracking"
  - "Add input length validation: max 1024 chars"
  - "Add regex timeout: 100ms via Future or watchdog"
  - "Scan all patterns for ReDoS via automated tool"
```
