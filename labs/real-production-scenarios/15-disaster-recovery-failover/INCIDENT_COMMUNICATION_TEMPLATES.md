# Lab 15 — Disaster Recovery Failover: Communication Templates

## Initial Alert

```
Title: [P0] Regional Outage — us-east-1 — All Services Unavailable
Service: Global Application (Multi-Region)
Severity: P0

Primary region: us-east-1 — ALL endpoints unhealthy
Secondary region: us-west-2 — HEALTHY
Impact: 100% of traffic through us-east-1 affected
Duration: 2 minutes since first health check failure
Cause: AWS us-east-1 availability zone failure

Action: INITIATING FAILOVER TO us-west-2
```

## Status Updates

### Failover Initiated

```
STATUS #1 — Failover to us-west-2 Initiated

Actions:
1. ✅ Database: Promoting us-west-2 replica to primary
2. ✅ DNS: Route53 failover to us-west-2 (TTL: 60s)
3. 🔄 Scaling: us-west-2 cluster scaling up (estimated 3 min)
4. 📋 Stakeholder notification sent

Expected traffic restoration: 5 minutes
RPO: < 1 second (async replication lag before failure)
RTO: Targeting < 5 minutes
```

### Failover Complete

```
STATUS #2 — Failover Complete — All Traffic on us-west-2

us-west-2 serving all traffic:
- Error rate: 0.1% (normal)
- P99 latency: 120ms (normal)
- All services healthy
- Data integrity verified (0 lost transactions)

us-east-1: assessing damage, no ETA for restoration
Will failback after us-east-1 is verified healthy.

Post-incident: Full post-mortem within 72 hours.
Failover drill scheduled to verify improved RTO.
```

### Failback Complete

```
STATUS #3 — Failed Back to us-east-1

us-east-1 restored:
- Root cause: AWS network event in AZ-1
- Failback completed at 16:30 UTC
- All traffic returned to us-east-1
- us-west-2 scaled back to normal capacity

Lessons learned:
- Failover completed in 4 min 30 sec (within RTO)
- Data loss: 0 transactions (RPO = 0)
- Improvement: automate scaling step (was manual)
```

## Executive Summary

```
EXECUTIVE BRIEF — REGIONAL FAILOVER EVENT

What: us-east-1 region outage due to AWS infrastructure failure
When: June 15, 2024, 14:30-16:30 UTC
Impact: ~5 minutes of downtime during failover transition

DR Metrics:
- RTO achieved: 4 minutes 30 seconds (target: 5 min)
- RPO achieved: 0 seconds (no data loss)
- Customer impact: ~4.5 minutes of elevated latency for 100% of traffic

What went well:
1. Automated health checks detected failure within 30 seconds
2. DR runbook followed precisely
3. Stakeholder communication timely (updates every 5 minutes)

What we improved:
1. Automated secondary region scaling (was manual)
2. Added cross-region read-only mode during failover
3. Enhanced replication monitoring
```
