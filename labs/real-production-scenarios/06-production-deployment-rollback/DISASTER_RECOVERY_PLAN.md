# Lab 06 — Deployment Rollback: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO (rollback time) | 5 minutes |
| RPO | 0 (deployments are stateless) |
| MTD | 15 minutes before SLA breach |

## Scenarios

### Scenario A: Failed Deployment

**Trigger:** Error rate spike after deployment
**Recovery:**
1. Automatically detect error rate > 2% (canary) or > 0.5% (full)
2. Initiate rollback to previous version
3. Verify error rate returns to baseline
4. Keep bad version for debugging (don't delete artifact)

### Scenario B: Database Migration Failure

**Trigger:** Migration fails or causes data inconsistency
**Recovery:**
1. Stop deployment immediately
2. Execute rollback migration (reverse the schema change)
3. Rollback application code
4. Verify data integrity
5. Fix migration to be backward-compatible

### Scenario C: Feature Flag Gate Failure

**Trigger:** Feature flag enabled, causes issues, flag disable doesn't fix (cached code path)
**Recovery:**
1. Rollback deployment (feature flag alone isn't enough)
2. Clear any caches that cached the bad code path
3. Verify feature flag disable actually works in isolation
4. Add feature flag verification step to deployment checklist
