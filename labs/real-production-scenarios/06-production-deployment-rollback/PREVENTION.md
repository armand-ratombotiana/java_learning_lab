# Prevention Guide — Avoiding Bad Deployment Incidents

## How to Prevent Recurrence of SEV1 Deployment Failures

This document outlines the engineering and process changes required to prevent deployment-related incidents at the SEV1 level. Drawing from Google SRE best practices, Netflix's canary analysis methodology, and Microsoft Azure Well-Architected Framework guidance, these prevention measures address the systemic gaps identified in the root cause analysis.

---

## 1. Mandatory Feature Flag Gating

### Policy
All code changes that introduce new code paths, modify request/response handling, or alter caching behavior MUST be wrapped in a feature flag. Exceptions require Architecture Review Board approval and are limited to:
- Bug fixes with zero new code paths
- Configuration-only changes (properties, environment variables)
- Logging and monitoring changes (read-only)

### Implementation Requirements
- Feature flag SDK (LaunchDarkly or equivalent) must be integrated into all services
- Feature flags must have a kill-switch capability — disabling the flag must immediately revert to previous behavior
- Feature flags must be included in deployment pipeline verification (pipeline verifies flag evaluation before completing deployment)
- Feature flags must have an owner and expiration date — stale flags are automatically archived

### Enforcement
- Pipeline gate validates that new code paths have corresponding feature flags (code analysis tool scans for feature flag annotations)
- Architecture Review Board audits feature flag usage quarterly
- Error budget tracking includes feature flag coverage as a metric

---

## 2. Automated Rollback Triggers

### Mandatory Rollback Conditions
Automated rollback MUST trigger immediately when any of the following conditions are met during the canary or progressive rollout window:
- Error rate exceeds 1% (relative to baseline) for 2 consecutive minutes
- p95 latency exceeds 200ms (or 1.5x baseline, whichever is higher)
- Any security alert (OOM kill, panic, segfault) detected in the new deployment
- Health check failure rate exceeds 5%
- Throughput drops below 80% of baseline

### Rollback Automation Requirements
- Rollback must be fully automated — no human-in-the-loop for the trigger decision
- Rollback must complete within 5 minutes of trigger detection
- Rollback must preserve existing traffic (zero-downtime rollback)
- Rollback must disable associated feature flags automatically
- Rollback must send notification to incident response channel

### Implementation Priority
1. Automated rollback trigger (Phase 1 — within 1 week of incident)
2. Connection draining optimization (Phase 1)
3. Progressive traffic shift automation (Phase 2 — within 2 weeks)
4. Feature flag auto-disable on rollback (Phase 2)

---

## 3. Enhanced Canary Analysis

### Canary Duration Requirements
| Traffic Percentage | Minimum Duration | Success Criteria |
|-------------------|-----------------|------------------|
| 2% | 30 minutes | Error rate < 0.1%, no alerts |
| 25% | 15 minutes | Error rate < 0.5%, latency < 1.5x baseline |
| 50% | 10 minutes | Error rate < 0.5%, all health checks passing |
| 75% | 10 minutes | Same as 50% + no security events |
| 100% | Monitoring continues for 60 min | All metrics within SLO |

### Statistical Significance Requirements
- Canary analysis must achieve 95% statistical confidence before promoting
- Minimum sample size: 10,000 requests per canary group
- Canary analysis must be per-endpoint, not aggregate
- Machine learning anomaly detection (statistical hypothesis testing) preferred over static thresholds

### Canary Exclusions
Canary analysis must exclude the following traffic to avoid false positives:
- Health probe traffic (liveness/readiness probes)
- Synthetic monitoring traffic
- Pre-warming traffic
- Internal service mesh traffic

---

## 4. Error Budget Policy

### Error Budget Definition
- SLO: 99.95% availability for user profile service (monthly)
- Error budget: 0.05% = ~21.6 minutes of downtime per month
- Deployment safety: deployments consume error budget at 10x rate during rollout windows

### Deployment Gates Based on Error Budget
| Error Budget Remaining | Deployment Policy |
|-----------------------|-------------------|
| > 50% | Normal deployment process |
| 25% — 50% | Canary window extended 2x, manual approval required |
| 10% — 25% | Deployments frozen, only hotfixes allowed |
| < 10% | Complete deployment freeze until budget replenishes |

### Error Budget Tracking
- Real-time error budget dashboard visible to all engineering teams
- Error budget consumed by deployments tracked separately from general availability
- Monthly error budget review in SRE team meeting
- Error budget violations trigger automated incident review

---

## 5. Testing Requirements

### Mandatory Test Types for All Deployments
| Test Type | Requirements | Pass Criteria |
|-----------|-------------|---------------|
| Unit Tests | Edge cases: null inputs, empty collections, timeout exceptions | 100% pass |
| Integration Tests | Wiremock for all downstream dependencies, negative scenarios | 100% pass |
| Load Tests | 2x peak production traffic, all endpoints | No error rate increase |
| Chaos Tests | Random pod kill, downstream service degradation | Degrade gracefully |
| Security Scan | Dependency vulnerabilities, SAST, container scan | Zero critical/high |

### Mock Coverage Requirements
- Mocks must cover ALL return types: valid response, null, empty, exception, timeout
- Mock verification: verify that all mock scenarios are exercised in tests
- Contract testing (Pact framework) for service-to-service interactions

---

## 6. Organizational Changes

### SRE Adoption
- Establish dedicated SRE team with deployment safety ownership
- Implement weekly deployment review meetings
- Create error budget ownership per service team
- Conduct quarterly incident response drills

### Code Review Enhancement
- Code review checklist must include: feature flag requirement, null safety, downstream service failure handling
- Code review must include production readiness review for all new code paths
- Random security review for all deployments

### Training Requirements
- All engineers must complete SRE fundamentals training
- Incident response training (Tabletop exercises) quarterly
- Chaos engineering workshop annually
- LaunchDarkly feature flag training for all developers

---

## 7. Pipeline Hardening

### Deployment Pipeline Security Gates
1. Static code analysis (SonarQube) — must pass quality gate
2. Dependency vulnerability scan (Trivy/Snyk) — zero critical
3. Container image scan (Aqua/Twistlock) — zero high-severity
4. License compliance scan — all approved licenses
5. Secret scanning — no hardcoded credentials

### Deployment Phases with Safety Checks
```
[Build] → [Unit Tests] → [SAST Scan] → [Container Build]
    ↓
[Dev Deploy] → [Smoke Tests] → [Integration Tests]
    ↓
[Staging Deploy] → [Load Test] → [Chaos Test] → [Security Scan]
    ↓
[Canary 2% — 30 min] → [Auto-rollback trigger active]
    ↓
[Canary 25% — 15 min] → [Auto-rollback trigger active]
    ↓
[Progressive Rollout — 50/75/100%] → [Auto-rollback trigger active]
    ↓
[Post-deployment monitoring — 60 min]
```

### Connection Draining Configuration
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-preference-service
  namespace: production
spec:
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 25%
      maxUnavailable: 0
      terminationGracePeriodSeconds: 300  # increased from 60
  template:
    spec:
      terminationGracePeriodSeconds: 300
      containers:
      - name: user-preference-service
        lifecycle:
          preStop:
            exec:
              command: ["/bin/sh", "-c", "sleep 120 && exit 0"]
```

---

## 8. Monitoring and Alerting Improvements

### Deployment-Specific Monitoring
| Metric | Alert Threshold | Response Time |
|--------|----------------|---------------|
| Error rate delta (post-deployment vs baseline) | > 0.5% | Immediate (auto-rollback) |
| Latency delta (p95 post-deployment vs baseline) | > 50ms | 2 minutes |
| Container restart count per deployment | > 3 in 5 minutes | Immediate |
| Feature flag evaluation error rate | > 0.1% | 5 minutes |
| Canary analysis confidence interval | < 95% | Alert (not auto-rollback) |

### Monitoring Dashboard Requirements
- Real-time deployment health dashboard showing: error rate, latency, throughput per deployment version
- Side-by-side comparison of old vs new deployment metrics
- Feature flag evaluation count and failure rate
- Rollback trigger status (armed/disarmed)
- Connection draining progress

---

## Summary of Prevention Measures

| # | Measure | Owner | Target Completion | Impact Level |
|---|---------|-------|-------------------|--------------|
| 1 | Feature flag mandate for all new code | Platform Team | Q3 2026 | Critical |
| 2 | Automated rollback triggers | SRE Team | Q3 2026 | Critical |
| 3 | Extended canary duration | DevOps Team | Q3 2026 | High |
| 4 | Error budget policy | SRE Team | Q3 2026 | High |
| 5 | Enhanced test coverage | QA Team | Q4 2026 | High |
| 6 | Connection draining fix | DevOps Team | Q3 2026 | Medium |
| 7 | Incident response training | SRE Team | Q3 2026 | Medium |

### References
- Google SRE Book — Chapter 4: "Service Level Objectives" (Error Budgets)
- Google SRE Book — Chapter 13: "Emergency Response"
- Netflix Tech Blog: "Canary Analysis and Automated Rollback at Netflix"
- Microsoft Azure Well-Architected Framework: "Operational Excellence — Deployment"
- LaunchDarkly: "Feature Flag Best Practices for Production Deployments"
- AWS re:Invent 2019 — "Deploying Safely at Amazon" (DOP205-R1)
