# Incident Report: Content Moderation Service High CPU

**Incident ID**: INC-2024-0610-REDOS
**Severity**: P0 (SEV-1)
**Date**: June 10, 2024
**Affected Service**: Content Moderation Regex Evaluation Engine
**Duration**: 8 hours (active mitigation + permanent fix)
**Detection**: CPU utilization alert > 95% on 500 nodes globally

## Executive Summary

On June 10, 2024, Meta's content moderation service experienced a global latency spike from 50ms P99 to 10+ seconds P99 during peak traffic. The incident was triggered by a user post containing a crafted text payload that exploited a catastrophic backtracking vulnerability in the Java regex engine. The regex pattern used for content policy scanning had nested quantifiers and alternation that caused exponential backtracking on certain inputs. A single request could consume 100% CPU for 30+ seconds. With 200 threads per node, 4-5 concurrent ReDoS attacks saturated all threads, causing cascading queuing and 10s+ latency for all users. The fix involved adding input length validation, replacing the vulnerable regex with an atomic group pattern, and configuring a regex execution timeout via a custom Pattern wrapper.

## Timeline (All Times UTC)

### Day 1 — June 10

| Time | Event |
|------|-------|
| 14:00 | PagerDuty alert: "CPU > 95%" for content-moderation service in us-east-1 |
| 14:02 | On-call acknowledges. CPU at 99% across 500 nodes globally |
| 14:05 | Check memory: normal. GC: normal, no GC thrashing |
| 14:08 | Check thread pools: all 200 threads active, queue growing |
| 14:10 | P99 latency: 10.2 seconds (baseline: 50ms) |
| 14:12 | Initial hypothesis: traffic surge. Check request rate: normal |
| 14:15 | Check downstream services: all healthy. Problem is in the moderation service itself |
| 14:20 | Run `top -H -p <pid>` — threads at 100% CPU, all in `java.util.regex` package |
| 14:25 | Collect async-profiler flame graph: `profiler.sh -d 60 -e cpu <pid> > flame.html` |
| 14:30 | Flame graph shows 85% of CPU in `java.util.regex.Pattern$GroupHead.match` |
| 14:35 | Identify the specific regex pattern causing the issue (content policy rule #1427) |
| 14:40 | Create test input that triggers the backtracking |
| 14:45 | Run the attack payload locally: confirms ReDoS vulnerability |
| 15:00 | Escalate to Content AI team and SRE leadership |

### Mitigation Phase

| Time | Event |
|------|-------|
| 15:15 | Short-term mitigation: disable content policy rule #1427 via feature flag |
| 15:20 | CPU drops from 99% to 30% immediately |
| 15:25 | P99 latency recovers from 10s to 100ms |
| 15:30 | All regions recover. Incident downgraded to P1 |
| 16:00 | Team begins analyzing the vulnerable regex pattern |

### Root Cause Analysis

| Time | Event |
|------|-------|
| 16:00 | Regex pattern analysis: `^(?=.*[A-Z])(?=.*[!@#$%^&*()])(?=.*[0-9])((?=.*[a-z])|(?=.*upper_seq))(?=.*(.)\\1{2,}).{8,40}$` |
| 16:15 | Identified problematic construct: `((?=.*[a-z])|(?=.*upper_seq))` combined with backtracking |
| 16:30 | Simplified reproduction: `(a|aa|aaa|aaaa)+b` vs input "aaaaaaaaac" |
| 16:45 | Java's Pattern uses NFA backtracking engine — exponential on nested quantifiers |
| 17:00 | Discover no input length validation: inputs up to 64KB accepted |
| 17:15 | Discover no regex execution timeout configured |
| 17:30 | Fix design: rewrite pattern with atomic groups `(?>...)` |
| 17:45 | Fix design: add input length limit to 1024 characters |
| 18:00 | Fix design: add regex timeout via custom Pattern with interruptible thread |

### Fix and Deployment

| Time | Event |
|------|-------|
| 18:30 | Rewrite pattern: replace alternation with single lookahead |
| 19:00 | Add input validation: reject payloads > 1024 chars |
| 19:30 | Implement PatternTimeout wrapper with Thread.interrupt on timeout |
| 20:00 | Build and test fix in staging environment |
| 20:30 | Deploy fix to canary cluster |
| 21:00 | Canary healthy: CPU at 25%, latency 50ms P99 |
| 22:00 | Full production rollout completed |
| 22:30 | Re-enable content policy rule #1427 — no CPU spike |
| 22:45 | Incident declared resolved |

## Key Findings

1. **Root Cause**: ReDoS vulnerability in regex pattern used for content policy scanning
2. **Trigger**: Single crafted user post exploited nested quantifiers causing exponential backtracking
3. **Amplification**: 200-thread pool saturated by 4-5 concurrent attacks → cascading latency
4. **Gaps**: No input length validation, no regex timeout, no regex testing for ReDoS patterns
5. **Detection**: CPU alerts caught it immediately; ReDoS-specific detection was absent

## Action Items

| # | Action | Owner | Status |
|---|--------|-------|--------|
| 1 | Replace all regex patterns with atomic groups / possessive quantifiers | Content AI Team | Done |
| 2 | Add input length validation (max 1024 chars) | Content AI Team | Done |
| 3 | Implement regex timeout via custom Pattern wrapper | Platform Team | Done |
| 4 | Build ReDoS vulnerability scanner for regex patterns | Security Team | In Progress |
| 5 | Add ReDoS stress test to CI/CD pipeline | QA Team | Done |
| 6 | Review all existing regex patterns for ReDoS risk | Content AI Team | In Progress |

## Detailed Attack Analysis

### Attack Payload
The crafted input that triggered the ReDoS was a 37-character string that satisfied the first three lookaheads but caused the alternation to backtrack exponentially. The payload was designed to exploit the nested quantifier structure by matching the opening patterns (uppercase, special char, digit) while failing the repeated-character check after the alternation group.

### Thread Pool Saturation Timeline
The attack started with 5 concurrent requests and grew as existing requests held threads. Within 35 seconds, all 200 threads were occupied by attack requests, each holding a thread for 30+ seconds. Normal requests queued behind the stuck threads, causing the observed 10s+ P99 latency.

### Network and System Impact
During the incident, the affected nodes showed 99% CPU utilization with near-zero outbound traffic (requests were stuck processing internally). The load balancer health checks began failing as the application threads became unresponsive, triggering instance replacement in the auto-scaling group.

## Incident Response Communication Summary

| Time | From | Key Message |
|------|------|-------------|
| 14:02 | On-call SRE | "CPU 99% globally. All nodes affected. Investigating." |
| 14:15 | SRE Team | "Flame graph shows 85% CPU in java.util.regex. Suspected ReDoS attack." |
| 14:40 | Content AI Team | "Vulnerable pattern identified: policy rule #1427. Disabling via feature flag." |
| 14:45 | Incident Commander | "CPU dropping after pattern disabled. Incident downgraded to P1." |
| 22:30 | SRE Lead | "Fix deployed globally. Pattern re-enabled. No regression." |

## Post-Incident Statistics

- Total incident duration: 8 hours (14:00 - 22:00 UTC)
- Active attack window: ~45 minutes (until pattern disabled)
- Total affected requests: ~3.7 million (based on 10K TPS × 45 minutes × 500 nodes)
- Regex evaluations timed out: ~47,000 (after fix deployed, before pattern re-enabled)
- Patterns audited post-incident: 2,147
- Vulnerable patterns found: 23 (1.1% of total)
- Patterns rewritten with atomic groups: 23

## Incident Response Timeline

| Time | Action | Result |
|------|--------|--------|
| 14:22 | CPU alert: 100% on 200 threads | P0 declared, bridge call initiated |
| 14:25 | Thread dumps collected | All threads in RUNNABLE, stack shows Pattern matcher |
| 14:28 | async-profiler started | Flame graph shows 85% CPU in java.util.regex |
| 14:32 | Attack payload identified | 37-char input causing catastrophic backtracking |
| 14:35 | Regex pattern disabled via feature flag | CPU drops to normal, service recovers |
| 14:40 | WAF rule added to block attack payload | Immediate blocking confirmed |
| 14:45 | Pattern rewritten with atomic groups | Deployed to canary |
| 15:05 | Full audit of 2,147 patterns begins | 23 additional vulnerable patterns found |
| 18:00 | All vulnerable patterns fixed | Prevention measures documented |

## SLA Impact Analysis

| Metric | Target | During Incident | Degradation |
|--------|--------|-----------------|-------------|
| Content Moderation P99 | < 200ms | 10,200ms | 51x |
| Service Availability | 99.99% | 97.8% | 2.19% below target |
| Error Rate | < 0.1% | 32% | 320x |
| Throughput | Normal | Near zero | Complete stall |

### Attack Statistics
- Total attack requests observed: ~18,000
- Unique attack payloads: 3 (variants of the same pattern)
- Source IPs: ~47 (distributed, likely botnet)
- Duration of active attack: ~45 minutes (until pattern disabled)
- Peak attack rate: ~10 requests/second

### Resource Cost
- Extra compute capacity required: 3x normal (auto-scaling)
- JFR recordings collected: 48 (from affected nodes)
- async-profiler runs: 12
- Patterns audited post-incident: 2,147
- Vulnerable patterns found and fixed: 23

### Root Cause Classification
- **Type**: Denial of Service / Application-level
- **Category**: ReDoS (Regular Expression Denial of Service)
- **CWE**: CWE-400 (Uncontrolled Resource Consumption), CWE-1333 (Inefficient Regex)
- **Severity**: P0 / SEV-1

## Performance Regression Trend

The vulnerable pattern was deployed 3 months before the incident:

| Month | Pattern Added? | Tests Passed | Evaluation Time (normal input) | Notes |
|-------|---------------|--------------|-------------------------------|-------|
| Mar 2024 | Deployed | All passed | < 1ms | Initial deployment, tested with valid inputs |
| Apr 2024 | No changes | All passed | < 1ms | No performance issues |
| May 2024 | No changes | All passed | < 1ms | No performance issues |
| Jun 2024 | No changes | All passed | < 1ms | PRESENT DAY: exploit discovered |

The pattern had been in production for 3 months without issue because no one had tried a ReDoS-style input against it.

## Detection Gap Analysis

| Detection Method | Status | Gap |
|-----------------|--------|-----|
| CPU utilization alert | Enabled | Alerted at 100% but could not identify root cause |
| Thread dump analysis | Manual | Required engineer to recognize Pattern matcher stacks |
| async-profiler | On-demand | Not part of standard incident runbook |
| JFR auto-recording | Enabled | Recorded but not analyzed until manually reviewed |
| ReDoS vulnerability scan | Not implemented | Would have detected before deployment |
| Regex performance test | Not implemented | Performance testing used valid inputs only |
| Input length validation | Not implemented | Allowed 64KB inputs |

## Lessons Learned

1. All regex patterns must be validated for ReDoS vulnerability before deployment
2. Input length validation is the cheapest and most effective defense
3. Regex timeout is a critical safety net — no eval should run indefinitely
4. Thread pool sizing must account for worst-case per-request execution time
5. CPU profiling with async-profiler should be part of every on-call engineer's toolkit

## Process Improvements

1. **Input validation**: All regex endpoint inputs must be limited to 1000 characters
2. **Regex timeout**: Pattern matching must use a 2-second timeout via Future or custom matcher
3. **Vulnerability scanning**: All regex patterns must pass ReDoS detection in CI/CD
4. **Regular audit**: All 2,147 existing patterns reviewed and 23 vulnerable patterns fixed
5. **Training**: Engineering team trained on ReDoS patterns and prevention

## Recovery Steps

1. Identify the vulnerable pattern via flame graph or thread dump
2. Disable the pattern via feature flag immediately
3. Add WAF rule to block attack payload
4. Rewrite pattern with atomic groups (?>
5. Audit all regex patterns for similar ReDoS vulnerability
6. Enforce input length validation and regex timeout as safety net

## Incident Root Cause Classification

- **Type**: Denial of Service / Application-level
- **Category**: ReDoS (Regular Expression Denial of Service)
- **CWE**: CWE-400 (Uncontrolled Resource Consumption), CWE-1333 (Inefficient Regex)
- **Severity**: P0 / SEV-1
- **Root Cause**: Catastrophic backtracking in regex pattern with overlapping alternation and nested quantifiers

## Attack Pattern Details

| Field | Value |
|-------|-------|
| Attack vector | HTTP POST to /api/content/moderate |
| Payload length | 37 characters |
| Payload pattern | Repeated alphanumeric characters matching lookahead branches |
| Attack duration | ~45 minutes |
| Attack sources | ~47 IP addresses (distributed botnet) |
| Peak rate | ~10 requests/second |
| WAF rule added | Block requests matching the attack signature |

## Post-Incident Actions

| Action | Owner | Status |
|--------|-------|--------|
| All 2,147 patterns audited | Platform Team | Complete |
| 23 vulnerable patterns rewritten | Platform Team | Complete |
| Input length validation (1000 chars) | Platform Team | Complete |
| Regex timeout (2s) implemented | Platform Team | Complete |
| ReDoS scanner added to CI/CD | Platform Team | Complete |
| WAF rate limiting added | Security Team | Complete |

## Related Vulnerabilities Found

| Pattern File | Vulnerability Type | Fix Applied |
|-------------|-------------------|-------------|
| ContentModerationFilter.java | Nested quantifier: (a+)+ | Atomic group: (?>a+)+ |
| EmailValidator.java | Overlapping alternation: (a|aa)+ | Atomic group: (?>a|aa)+ |
| URLMatcher.java | Capturing group in repetition: (https?)+ | Atomic group: (?>https?)+ |
| TextSanitizer.java | Optional group before required: (x)?y | Removed optional group |
| InputNormalizer.java | Lookahead inside quantifier: (?=\w)+ | Restructured pattern |

## Recovery Steps

1. Identify the vulnerable pattern and disable via feature flag
2. Add WAF rule to block the attack payload
3. Rewrite the pattern using atomic groups (?>
4. Audit all patterns for similar vulnerability
5. Deploy regex timeout and input length validation

