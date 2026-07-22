# Incident Report — Production Deployment Rollback

## Incident ID: INC-2026-0715-003

**Date**: July 15, 2026  
**Reported By**: SRE Team Lead (Jane Chen)  
**Status**: Resolved — Root Cause Identified  
**Severity**: SEV1 / P0  

---

## Timeline (All Times UTC)

### Pre-Incident

| Time | Event |
|------|-------|
| 13:00 | Code merged to `main` branch — PR #4283 (feature/user-preference-cache) approved after 2 approvals, all checks passing |
| 13:15 | Azure DevOps build pipeline triggered — build ID 20260715.15 |
| 13:22 | Build complete — unit tests (487 passed, 0 failed), integration tests (142 passed, 0 failed), code coverage 82.3% |
| 13:25 | Container image pushed to ACR (Azure Container Registry) — tag `v2.3.45-rc1` |
| 13:30 | Deployment to dev environment initiated via Azure DevOps release pipeline |
| 13:38 | Dev deployment complete — smoke tests passed |
| 13:40 | Deployment to staging environment initiated |
| 13:48 | Staging deployment complete — integration tests passed |
| 13:50 | Canary deployment initiated — 5% traffic routed to new pods (3 of 48 nodes) |
| 14:05 | Canary analysis window completed (15 minutes) — metrics within threshold: error rate 0.02% (control 0.01%), p95 latency 142ms (control 138ms) |
| 14:06 | Automated pipeline approval granted — full rollout initiated |

### Incident Onset

| Time | Event |
|------|-------|
| 14:08 | 100% traffic shift initiated — Azure DevOps pipeline scales up new pods to 48 nodes, begins scaling down old pods |
| 14:09 | First alerts triggered — Azure Monitor detects spike in HTTP 500 errors (East US region, pods running new version) |
| 14:10 | HTTP 500 error rate reaches 5% globally, 12% in East US region |
| 14:11 | PagerDuty incident triggered — SRE primary (John Smith) acknowledged |
| 14:12 | SRE confirms widespread deployment failure — new pods serving errors on all user profile endpoints |
| 14:13 | SRE secondary (Maria Rodriguez) paged — joined bridge |
| 14:14 | Feature team lead (Alex Kim) joined bridge — identified the new caching code path as likely culprit |
| 14:15 | Error rate peaks at 15.2% globally — node health probes failing for 12 pods |
| 14:16 | Decision made to initiate manual rollback — rollback button pressed in Azure DevOps release dashboard |
| 14:17 | Rollback begins — Azure DevOps initiates scale-down of new pods and scale-up of old pods |
| 14:19 | Connection draining phase starts — 60-second drain timeout configured |
| 14:22 | First old pods become healthy and receive traffic — error rate begins declining |
| 14:25 | Error rate drops below 5% |
| 14:30 | Error rate drops below 1% |
| 14:35 | Rollback complete — all 48 nodes running previous version (v2.3.44) |
| 14:40 | Verification complete — error rate at baseline (0.01%), all health checks passing |
| 14:45 | Post-incident review initiated |
| 14:55 | Root cause identified: `NullPointerException` in `UserPreferenceCacheService.java` line 47 — missing null check on user preference data from downstream service |

### Post-Incident

| Time | Event |
|------|-------|
| 15:30 | Hotfix branch created from `main` at pre-merge commit |
| 15:45 | Null check fix implemented and committed — PR #4291 |
| 16:00 | Fix deployed through full pipeline with additional canary gates |
| 16:30 | Canary at 2% for 30 minutes — zero errors observed |
| 16:35 | Progressive rollout: 25% → 50% → 75% → 100%, each stage with 10-minute observation window |
| 16:55 | Full rollout complete — all nodes healthy, zero errors attributable to new code |
| 17:00 | Incident declared resolved |

### Blameless Post-Mortem Findings

**Human Factors**:
- Developer was under time pressure to deliver the feature for quarterly OKR commitment
- Code review focused on business logic correctness and missed edge case handling for downstream service unavailability
- Developer assumed downstream preference service would always return non-null data
- No automated test covered the null-return scenario from the preference service

**Process Factors**:
- Pipeline lacked automated rollback triggers — rollback required human decision and manual button click
- Canary analysis window was too short (15 minutes) for detecting cold-start issues
- Feature flag was not used — the new caching behavior was enabled immediately on deployment
- Load testing did not cover the scenario where the preference service is degraded or slow

**Technical Factors**:
- `UserPreferenceCacheService` called `DownstreamPreferenceClient.getPreferences()` without null check on return value
- The downstream service had a race condition that caused periodic null returns under high load
- Connection draining timeout was set to 60 seconds, but pods with active connections required up to 120 seconds to drain
- Health probes (liveness/readiness) continued passing on unhealthy pods because the error surfaced only on specific API endpoints

### Metrics Summary

| Metric | Pre-Incident Baseline | During Incident | Post-Recovery |
|--------|----------------------|-----------------|---------------|
| Error Rate | 0.01% | 15.2% (peak) | 0.01% |
| p95 Latency | 138ms | 3,400ms | 140ms |
| Active Sessions | 1,420,000 | 890,000 (dropped) | 1,380,000 |
| Healthy Nodes | 48/48 | 36/48 | 48/48 |
| CPU Utilization | 42% | 78% (retry storm) | 44% |
| Memory Utilization | 61% | 89% | 63% |

### Root Cause Category

- [x] Software Bug (Null Pointer Exception)
- [ ] Configuration Error
- [ ] Infrastructure Failure
- [x] Process Gap (No automated rollback)
- [ ] External Dependency
- [x] Monitoring Gap (No feature-level error alert)

### Action Items

| # | Action | Owner | Target Date | Status |
|---|--------|-------|-------------|--------|
| 1 | Add null check in `UserPreferenceCacheService` | Alex Kim | 2026-07-16 | Done |
| 2 | Implement automated rollback in Azure DevOps pipeline | Maria Rodriguez | 2026-07-30 | In Progress |
| 3 | Increase canary window to 60 minutes with progressive exposure | Jane Chen | 2026-07-23 | In Progress |
| 4 | Wrap new caching feature in LaunchDarkly feature flag | Alex Kim | 2026-07-18 | In Progress |
| 5 | Add error budget tracking for deployment health | SRE Team | 2026-08-01 | Planned |
| 6 | Implement connection draining timeout increase to 300 seconds | DevOps Team | 2026-07-20 | In Progress |
| 7 | Add automated chaos testing for downstream service failures | QA Team | 2026-08-15 | Planned |

## Expanded Incident Analysis

### Blameless Post-Mortem Culture
The post-incident review (PIR) was conducted following Google SRE's blameless post-mortem principles. The focus was on systemic failures rather than individual errors. Key observations from the PIR:

**Systemic Gaps Identified**:
1. The deployment pipeline lacked a mandatory "deployment safety checklist" that would have identified the missing null check
2. Code review guidelines did not require reviewers to check for downstream service null handling
3. The feature flag policy was advisory, not mandatory — teams could opt out without escalation
4. Connection draining configuration was inherited from Kubernetes defaults without performance testing
5. The canary analysis tool compared aggregate metrics but did not perform per-endpoint or per-version comparison

**Human Factors Addressed**:
1. The developer was not blamed — the focus was on why the testing framework didn't catch the null scenario
2. The code reviewer was not penalized — the focus was on why the review checklist didn't include null-safety for downstream calls
3. The SRE who approved the canary was not criticized — the focus was on why the canary window was insufficient

### Decision Log
The following key decisions were made during the incident, along with their rationale and alternatives considered:

**Decision 1: Rollback vs. Fix-Forward**
- Chosen: Rollback to previous version
- Rationale: The null pointer exception was in a core code path affecting all user profile requests. A fix-forward would require code changes, CI/CD pipeline run, and re-deployment — estimated 45+ minutes. Rollback could be completed in ~20 minutes.
- Alternative considered: Fix-forward with hotfix branch bypassing CI/CD — rejected due to risk management concerns

**Decision 2: Manual vs. Automated Rollback**
- Chosen: Manual rollback (automated rollback was not available)
- Rationale: The team had not yet implemented automated rollback triggers. The decision was forced by the current system capabilities.
- Action Item: Implement automated rollback within 2 weeks

**Decision 3: Full Rollback vs. Partial Node Replacement**
- Chosen: Full rollback to previous version on all 48 nodes
- Rationale: With 12 nodes already unhealthy and the error rate at 15%, partial replacement would take longer and risk leaving some nodes in an inconsistent state
- Alternative considered: Replacing only the 12 unhealthy nodes — rejected due to risk of remaining nodes having the bug triggered under load

### Expanded Metrics Analysis

**Error Rate Decomposition**:
The 15.2% peak error rate was not uniform across all endpoints:
- `GET /api/v1/users/{id}/profile`: 62% error rate (affected by NPE)
- `GET /api/v1/users/{id}/preferences`: 48% error rate (calls the affected code path)
- `PUT /api/v1/users/{id}/profile`: 12% error rate (indirectly affected by thread pool contention)
- `GET /api/v1/products`: 0.1% error rate (unaffected, different service path)
- `GET /api/v1/health`: 0% error rate (health endpoint not affected)

This decomposition highlights the importance of per-endpoint monitoring. An aggregate error rate of 15% masked the fact that some endpoints had 62% error rates while others were completely healthy.

**Latency Impact**:
The p95 latency increase from 138ms to 3,400ms was driven by:
1. Thread pool queuing (average wait time: 2,100ms)
2. TCP connection timeouts (30-second timeout, average actual wait: 12 seconds before client-side timeout)
3. Retry amplification (each failed request generated 2.4 additional requests on average)
4. Garbage collection pauses (GC time increased from 2% to 18% due to memory pressure from queued requests)

### Technical Debt Detail

The following technical debt items contributed to this incident, all of which had been known but deprioritized:

1. **Connection Draining Configuration**: The 60-second draining timeout was set during initial cluster setup 18 months ago. A JIRA ticket (OPS-1428) recommended increasing to 300 seconds but was deprioritized 4 times.

2. **Feature Flag Infrastructure**: LaunchDarkly SDK integration was in evaluation phase for 6 months. The team had not allocated engineering time for production integration.

3. **Automated Rollback Triggers**: Documented as "Phase 2" of the deployment pipeline project. Phase 1 (basic deployment automation) was completed, but Phase 2 was deferred for 3 consecutive quarters.

4. **Canary Analysis Toolkit**: The custom metrics comparison tool was built by an intern and used simple percentage-based thresholds. A more sophisticated statistical analysis tool was planned but never prioritized.

5. **Null Safety Standards**: The team had no coding standards requiring null checks on downstream service calls. A proposal to add this to the coding standards was pending review for 4 months.

### Expanded Communication Timeline

**Detailed Communication Flow**:
14:09 — PagerDuty alert sent to SRE primary (John Smith)
14:10 — John acknowledged alert, confirmed error rate spike in Application Insights
14:11 — John posted initial update to #incidents channel: "Investigating HTTP 500 spike on user-profile endpoints"
14:12 — John paged SRE secondary (Maria Rodriguez) via PagerDuty escalation
14:13 — Maria joined incident bridge (Zoom), John provided initial briefing
14:14 — Maria paged feature team lead (Alex Kim) via direct Slack message
14:15 — Alex joined bridge, reviewed code changes from latest deployment
14:16 — Alex identified UserPreferenceCacheService as the likely culprit
14:17 — John posted status update: "Root cause identified — new caching code path causing NPE. Rollback initiated."
14:18 — Maria notified DevOps team about potential connection draining issues
14:20 — John updated #prod-status: "Rollback in progress — 25% of nodes affected, expected recovery within 15 minutes"
14:30 — John posted recovery update: "Error rate declining — 40% of nodes now healthy"
14:35 — John updated #prod-status: "All nodes healthy, rollback complete"
14:40 — John declared incident contained in bridge call
14:45 — Post-incident review scheduled for 16:00

**Communication Lessons Learned**:
1. The feature team lead should have been paged automatically, not via manual Slack message (3-minute delay)
2. The DevOps team was not included in the initial page — they were manually contacted 9 minutes into the incident
3. #prod-status updates were posted manually — should be automated via pipeline status integration
4. No customer-facing status page was updated during the incident — customers learned about the outage from error messages
5. The communication template for "rollback in progress" was created ad-hoc — there was no standard template

### Incident Response Effectiveness Metrics

**Detection Effectiveness**:
- Time from fault to first alert: 2 minutes (error rate spike detected by Azure Monitor)
- Time from fault to human acknowledgment: 3 minutes
- Time from fault to root cause identification: 7 minutes
- MTTD (Mean Time to Detection): 3 minutes (good — within target of 5 minutes)

**Response Effectiveness**:
- Time from acknowledgment to rollback decision: 4 minutes
- Time from rollback decision to rollback initiation: 2 minutes (pipeline button press)
- Time from rollback initiation to first healthy pod: 5 minutes
- Time from rollback initiation to full recovery: 21 minutes
- MTTR (Mean Time to Recovery): 21 minutes (acceptable — target is 30 minutes)

**Areas for Improvement**:
- Automated rollback would reduce MTTR from 21 minutes to ~5 minutes
- Feature flag kill switch would reduce MTTR to under 1 minute
- Connection draining optimization would reduce recovery time
