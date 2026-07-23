# Lab 15 — Disaster Recovery Failover: DR Plan

## Recovery Objectives

| Metric | Target | Measurement |
|--------|--------|-------------|
| RTO | 5 minutes | Time from DR declaration to traffic served from secondary |
| RPO | 5 seconds | Max data loss due to replication lag |
| MTD | 15 minutes | Before SLA breach triggers penalties |

## Active-Passive Architecture

```
Normal State:
  [Users] → [Route53] → [us-east-1 (PRIMARY)] → [DB Primary]
                        → [us-west-2 (STANDBY)] → [DB Replica]

Failover State:
  [Users] → [Route53] → [us-west-2 (PRIMARY)] → [DB Promoted to Primary]
```

## Failover Procedure

### Step-by-Step Runbook

**Phase 1: Detection (0-2 min)**
1. Health checks fail for primary region (3 consecutive failures)
2. Cross-region monitoring confirms secondary is healthy
3. Incident commander declares regional disaster (SEV1/P0)

**Phase 2: Decision (2-3 min)**
1. Verify primary is unrecoverable within RTO
2. Verify secondary has capacity for 2x traffic
3. Decision: failover to secondary region

**Phase 3: Execution (3-5 min)**
1. Database: promote secondary to primary
2. DNS: update Route53 failover policy
3. Compute: scale up secondary (add 50% capacity)
4. Cache: warm critical cache entries
5. Verification: health check, data integrity, latency check

**Phase 4: Verification (5-8 min)**
1. Run end-to-end health checks on secondary
2. Verify error rate < 0.5%
3. Verify P99 latency < 500ms
4. Confirm data integrity (compare row counts, checksums)
5. Send stakeholder notification: "Failover complete"

### Failback Procedure

1. Verify primary region is fully healthy (all services, all dependencies)
2. Re-establish replication from secondary back to primary
3. Wait for replication to catch up (lag < 1s)
4. Promote primary back to active state
5. Update DNS back to primary
6. Scale down secondary to normal capacity
7. Verify all traffic served from primary

## Testing Schedule

| Test | Frequency | Criteria |
|------|-----------|----------|
| Cross-region replication lag | Continuous (automated) | Lag < 5 seconds |
| Health check accuracy | Every minute | Zero false positives |
| DR runbook review | Quarterly | Runbook updated and accurate |
| Failover drill (full) | Quarterly | RTO < 5 min, RPO < 5s |
| Failover drill (tabletop) | Monthly | Team knows their roles |
| Data integrity check | Quarterly | Data in secondary matches primary |
| Chaos engineering test | Quarterly | Inject region failure, verify auto-recovery |

## Backup and Restore

| Component | Backup Method | Frequency | Retention | Restore RTO |
|-----------|--------------|-----------|-----------|-------------|
| Database | Automated snapshots | Hourly | 30 days | 2 hours |
| File storage | Cross-region replication | Continuous | 90 days | 5 min |
| Configuration | Git + IaC | Every commit | Forever | 10 min |
| Container images | Cross-region replication | Every build | 90 days | 2 min |
| DNS config | Route53 backup | Every change | Forever | 1 min |

## Runbook: Region Outage

```yaml
symptoms:
  - "All services in primary region unhealthy"
  - "Health checks failing across services"
  - "No response from region endpoints"
  - "Secondary region healthy"

immediate_actions:
  - "Declare P0/SEV1 regional disaster"
  - "Notify: Incident Commander, DR team, stakeholders"
  - "Begin failover procedure"

failover_steps:
  - "1. Verify secondary region health"
  - "2. Promote database: secondary → primary"
  - "3. Update DNS: Route53 to secondary"
  - "4. Scale up secondary region (2x capacity)"
  - "5. Run health checks on secondary"
  - "6. Verify data integrity"
  - "7. Confirm: all services operational"
  - "8. Announce failover complete"

post_failover:
  - "Monitor secondary region for 1 hour"
  - "Investigate primary region root cause"
  - "Plan failback when primary restored"
  - "Post-mortem within 72 hours"
```
