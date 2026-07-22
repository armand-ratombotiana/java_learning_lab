# Incident Response Runbook Checklist — Bad Deployment Rollback

## Incident Type: SEV1/P0 — Deployment Failure / Node Outage

### Classification
- [ ] Incident severity: SEV1 (Critical) / P0 (Immediate)
- [ ] Incident type: Deployment Failure
- [ ] Affected service(s): ________________________________
- [ ] Affected region(s): ________________________________
- [ ] Deployment version(s): ________________________________
- [ ] Current incident commander: ________________________________
- [ ] Current comms lead: ________________________________

---

## Phase 1: Detection and Triage (0–5 minutes)

### Immediate Detection
- [ ] Confirm alert from Azure Monitor / PagerDuty
- [ ] Verify error rate spike in Application Insights dashboard
- [ ] Check node health status: `kubectl get pods -n production -o wide | Select-String "CrashLoopBackOff|Error|ImagePullBackOff"`
- [ ] Check deployment status: `kubectl rollout status deployment/user-preference-service -n production`
- [ ] Determine scope: how many nodes/pods are affected?

### Initial Assessment
- [ ] Is this a new deployment (within last 30 minutes)?
- [ ] Which version was deployed and from what previous version?
- [ ] Review canary analysis results — did the canary pass?
- [ ] Check feature flag status — are any new flags enabled?
- [ ] Is there a known issue or recent change outside of deployment?

### Communication
- [ ] Declare SEV1 incident in #incidents channel
- [ ] Open bridge/call with primary SRE, secondary SRE, feature team lead
- [ ] Notify incident commander to bridge
- [ ] Post initial status update to #prod-status channel

---

## Phase 2: Containment (5–15 minutes)

### Rollback Decision
- [ ] DECISION POINT: Rollback or fix-forward?
- [ ] If rollback: initiate rollback in Azure DevOps
- [ ] If fix-forward: hotfix branch created, CI/CD triggered

### Rollback Execution
- [ ] Click "Rollback" in Azure DevOps release dashboard
- [ ] Monitor pod termination and new pod creation
- [ ] Verify connection draining: `kubectl get events -n production | Select-String "draining"`
- [ ] Verify old pods becoming healthy: `kubectl get pods -n production -l app=user-preference-service`

### Feature Flag Override
- [ ] Disable all feature flags associated with this deployment in LaunchDarkly
- [ ] Verify flags are disabled: `ldctl evaluate user-preference-cache-enabled --user "test-user-001" --environment production`
- [ ] Confirm flag evaluation returns false for all users

### Traffic Management
- [ ] If blue-green: switch traffic manager back to blue environment
- [ ] If progressive rollout: revert traffic split to 100% previous version
- [ ] Verify traffic distribution: `az network traffic-manager endpoint show`

---

## Phase 3: Recovery Verification (15–30 minutes)

### Health Verification
- [ ] All 48 nodes report healthy: `kubectl get pods -n production | Select-String "Running" | Measure-Object`
- [ ] Error rate back to baseline (< 0.1%): check Application Insights
- [ ] Latency back to baseline (< 150ms p95): check Application Insights
- [ ] Active sessions recovering: check Azure Load Balancer metrics
- [ ] All health checks passing: liveness, readiness, startup probes

### Data Integrity Check
- [ ] Verify no data corruption from incomplete writes
- [ ] Check database connection pools for connection leaks
- [ ] Verify cache coherence (Redis/Memcached)
- [ ] Run data reconciliation if applicable

### Stakeholder Communication
- [ ] Post recovery confirmation to #prod-status
- [ ] Update incident timeline with recovery timestamp
- [ ] Notify customer support team of incident status
- [ ] Prepare initial incident summary for management

---

## Phase 4: Root Cause Investigation (30–120 minutes)

### Log Analysis
- [ ] Collect stack traces from Application Insights: `exceptions | where timestamp > ago(2h) | where problemId contains "NullPointer"`
- [ ] Review deployment logs: `kubectl logs -n production -l app=user-preference-service --tail=500`
- [ ] Check audit logs for deployment approvals
- [ ] Review canary analysis logs for any missed signals

### Code Review
- [ ] Identify the failing code path and line number
- [ ] Review PR #4283 for the deployment
- [ ] Check unit test coverage for the failing path
- [ ] Verify mock scenarios in tests

### Pipeline Review
- [ ] Review Azure DevOps pipeline YAML for safety gates
- [ ] Check feature flag integration (or lack thereof)
- [ ] Review canary analysis configuration
- [ ] Check automated rollback trigger configuration

---

## Phase 5: Fix and Re-deploy (120–240 minutes)

### Code Fix
- [ ] Implement null check / defensive programming fix
- [ ] Add unit tests for all edge cases (null, empty, exception)
- [ ] Add integration test with wiremock for downstream failure
- [ ] Run full test suite: `mvn clean verify`
- [ ] Run SonarQube analysis: `mvn sonar:sonar`

### Feature Flag Integration
- [ ] Integrate LaunchDarkly SDK (if not already done)
- [ ] Create feature flag for new code path
- [ ] Add feature flag evaluation in service code
- [ ] Test flag evaluation in dev/staging environments

### Pipeline Hardening
- [ ] Extend canary duration to 60 minutes
- [ ] Implement progressive traffic shift stages
- [ ] Configure automated rollback trigger at 1% error rate
- [ ] Increase connection draining timeout to 300 seconds

### Re-deployment
- [ ] Deploy fixed version to dev → staging → canary → production
- [ ] Monitor canary for full 60 minutes
- [ ] Progress traffic: 25% → 50% → 75% → 100%
- [ ] Enable feature flag for canary users first
- [ ] Gradually enable feature flag for all users

---

## Phase 6: Post-Incident Activities (After Resolution)

### Incident Report
- [ ] Complete INCIDENT_REPORT.md with full timeline
- [ ] Complete ROOT_CAUSE.md with 5 Whys analysis
- [ ] Update monitoring dashboards with new metrics
- [ ] Document lessons learned in team wiki

### Action Items
- [ ] Create JIRA tickets for all action items
- [ ] Assign owners and target dates
- [ ] Schedule follow-up review in 2 weeks
- [ ] Update incident response runbook with this checklist

### Prevention Measures
- [ ] Implement mandatory feature flag policy
- [ ] Configure automated rollback for all production deployments
- [ ] Update code review checklist with deployment safety items
- [ ] Schedule chaos engineering session to test rollback

### Blameless Post-Mortem
- [ ] Schedule blameless post-mortem meeting
- [ ] Invite all involved engineers
- [ ] Prepare data and timeline
- [ ] Focus on system improvements, not individual mistakes

---

## Emergency Contacts

| Role | Name | Phone | Slack |
|------|------|-------|-------|
| SRE Primary | John Smith | +1-555-0100 | @john.smith |
| SRE Secondary | Maria Rodriguez | +1-555-0101 | @maria.rodriguez |
| Feature Team Lead | Alex Kim | +1-555-0102 | @alex.kim |
| DevOps Lead | Sarah Chen | +1-555-0103 | @sarah.chen |
| Incident Commander | David Park | +1-555-0104 | @david.park |
| Engineering Director | Lisa Wang | +1-555-0105 | @lisa.wang |

---

## Quick Reference Commands

```powershell
# Check deployment status
kubectl rollout status deployment/user-preference-service -n production

# Check pod health
kubectl get pods -n production -l app=user-preference-service -o wide

# View pod logs
kubectl logs -n production -l app=user-preference-service --tail=200

# Check events
kubectl get events -n production --sort-by='.lastTimestamp'

# Rollback deployment
kubectl rollout undo deployment/user-preference-service -n production

# Set traffic split (Azure Front Door)
az network traffic-manager endpoint update --name prod-endpoint --profile-name prod-tm --weight 0

# Check Azure Monitor metrics
az monitor metrics list --resource {cluster-id} --metric "requests/errors" --interval PT1M
```

---

## Checklist Version

- Version: 1.0
- Last Updated: 2026-07-16
- Approved By: SRE Team Lead
- Next Review Date: 2026-10-16
