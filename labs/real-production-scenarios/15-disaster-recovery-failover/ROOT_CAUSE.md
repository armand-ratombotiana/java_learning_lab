# Root Cause Analysis: DR Failover — Region Outage

## RCA ID: RCA-2026-1127-001
## Severity: SEV1 — Full Region Outage
## Duration: 7 hours 10 minutes
## Total Impact: $742,000 revenue loss + regulatory penalties

## Executive Summary

On November 27, 2026, AWS us-east-1 experienced a severe regional outage affecting EC2, RDS, and ElastiCache. The failover to the DR region (us-west-2) took 1 hour 47 minutes (target: 15 minutes) and resulted in 12 minutes of unrecoverable data loss. The root cause was not the AWS outage itself, but the organization's failure to maintain and test the disaster recovery plan — the runbook was 14 months stale, never tested, and infrastructure had drifted significantly from DR templates.

## What Happened

AWS us-east-1 suffered a regional outage at 09:47 UTC. The DR runbook was retrieved but contained outdated AMI IDs, incorrect instance types, and stale security group configurations. CloudFormation DR templates had drifted from production. RDS cross-region replication lag was 12 minutes (configured for async replication with cost optimization). Route53 DNS had TTL=86400 causing 35+ minutes of propagation delay. Container images were not replicated to the DR region. The result: a failover that took 7x the target RTO and breached RPO by 3x.

## Direct Cause

The organization had no tested, validated disaster recovery plan. The runbook was 14 months old, DR infrastructure had drifted from production, and cross-region replication was configured for cost rather than recovery requirements.

## The 5 Whys Analysis

### Why 1: Why did failover take 1 hour 47 minutes (7x RTO)?

The failover process required 47 manual steps across 8 AWS services. Each step encountered issues:
1. Runbook referenced deprecated AMI ID → 8 minutes to find replacement
2. CloudFormation template referenced invalid RDS instance class → 6 minutes to fix
3. Security group CIDR ranges were outdated → 5 minutes to update
4. ECR images not replicated to us-west-2 → 20 minutes to rebuild from source
5. Redis cache in DR region was empty → 15 minutes to warm
6. DNS propagation delayed by TTL=86400 → 35+ minutes of waiting
7. CloudFormation stack creation failed due to dependency error → 12 minutes to debug

**Evidence**: Runbook git log shows last update September 2025. CloudFormation drift detection shows 27 configuration differences between production and DR templates.

### Why 2: Why was the runbook 14 months stale?

The DR runbook was created in September 2025 and never updated. No process existed for keeping DR documentation synchronized with infrastructure changes. The infrastructure team made 47 production changes in 14 months (new AMIs, instance types, security groups, VPC configurations) but none of these changes were reflected in the DR runbook.

**Evidence**: Infrastructure change log shows 47 production changes, 0 DR runbook updates. No DR runbook update was included in any change management ticket.

### Why 3: Why was the DR plan never tested?

The organization required quarterly DR drills in policy but had not conducted any. The last 3 scheduled drills were cancelled:
1. Q4 2025: Cancelled — "Holiday freeze period"
2. Q1 2026: Cancelled — "Critical feature release"
3. Q2 2026: Cancelled — "Executive prioritization of migration project"

**Evidence**: Meeting calendar shows 3 cancelled DR drill events with the stated reasons. No DR drill had been conducted in the organization's 4-year history.

### Why 4: Why was RDS cross-region replication lag set to 12 minutes?

The RDS read replica was configured with:
- `async` replication (not `synchronous`)
- `db.r6g.large` instance (lowest recommended tier)
- Replication lag target: "Not specified" (default AWS configuration)

The configuration was selected for cost optimization ($0.48/hour instead of $0.96/hour for synchronous). No RPO requirement was defined for the DR system. When asked about RPO targets, the team said "we assumed AWS handled it."

**Evidence**: RDS console shows replication mode: ASYNCHRONOUS. Instance class: db.r6g.large. No custom parameter group for replication tuning.

### Why 5: Why was there no organizational priority on DR readiness?

**Organizational Root Cause**: Executive leadership had deprioritized disaster recovery in favor of feature development. Specific decisions included:
1. SRE team reduced from 8 to 4 engineers (2025 reorg)
2. DR drill budget eliminated (cost savings)
3. Multi-region architecture deferred to "Phase 3" (never funded)
4. Chaos engineering initiative "not a priority" (compared to AI/ML features)
5. "AWS handles it" mentality — assumption that AWS Multi-AZ was sufficient

The organization had a culture of "availability by default" — believing that AWS's infrastructure guarantees were sufficient for disaster recovery. This ignored the shared responsibility model.

**Systemic Root Cause**: The organization lacked:
1. Executive ownership of disaster recovery readiness
2. Funded DR drill program with quarterly schedule
3. Automated DR infrastructure (CloudFormation drift detection, auto-sync)
4. RTO/RPO definitions for all critical services
5. Multi-region architecture investment
6. Chaos engineering capability
7. DR documentation maintenance process

## Contributing Factors

1. **DNS TTL too high**: Route53 production records had TTL=86400 (24 hours). Industry best practice for critical records: TTL=60.
2. **Single-region monitoring**: Prometheus/Grafana stack only in us-east-1. No visibility into DR region during failover.
3. **No container image replication**: ECR was not configured for cross-region replication. Images had to be rebuilt from source.
4. **No cache replication**: ElastiCache Redis was not configured for cross-region replication. DR region started with cold cache.
5. **No pre-warmed DR infrastructure**: DR environment was not kept running (cost). Had to provision from scratch during failover.
6. **Incorrect security groups**: Office IP ranges had changed; DR security groups blocked engineering access during failover.

## Verification

The root cause was verified by:
1. Runbook git log showing 14 months without update
2. CloudFormation drift detection report (27 configuration differences)
3. Calendar evidence of cancelled DR drills
4. RDS configuration showing async replication with 12-minute lag
5. Route53 TTL=86400 configuration
6. ECR cross-region replication configuration = disabled
7. ElastiCache replication configuration = disabled
8. Monitor deployment showing us-east-1 only

## Detailed Evidence

### DR Runbook Staleness Analysis

The runbook was stored as an internal wiki page last edited September 2025:

```
Wiki page: "Disaster Recovery Runbook — AWS us-east-1 to us-west-2 Failover"
Last edited: 2025-09-15 by jsmith (who left the company in March 2026)
Version: 1.3
Review status: Not reviewed (no review process existed)

Specific issues found in runbook:
1. AMI IDs referenced ami-0abcdef1234567890 — deprecated since Amazon Linux 2 2026.03 release
2. RDS instance class db.r5.large — deprecated in favor of db.r6g.large
3. Security group CIDR 203.0.113.0/24 — office IP range changed to 198.51.100.0/24 in Feb 2026
4. VPC ID vpc-12345678 — VPC was replaced in Q1 2026
5. Subnet IDs — all different subnets
6. CloudFormation stack name "acmecorp-dr-v1" — doesn't match current naming convention
7. S3 bucket for backups "acmecorp-dr-backup — doesn't exist
8. KMS key ID — wrong key (key was rotated)
9. IAM role names — outdated (roles renamed in March 2026)
10. SNS topic for notifications — wrong ARN
```

### DR Drill Cancellation History

```
Q4 2025 — Scheduled: December 15, 2025
  Cancelled: December 10, 2025
  Reason: "Holiday freeze — too risky before end-of-year"
  Rescheduled: Not rescheduled

Q1 2026 — Scheduled: March 15, 2026
  Cancelled: March 10, 2026
  Reason: "Critical feature release (Order Management System v2)"
  Rescheduled: Not rescheduled

Q2 2026 — Scheduled: June 15, 2026
  Cancelled: June 8, 2026
  Reason: "Executive prioritization — migration to new CI/CD pipeline"
  Rescheduled: Not rescheduled

Q3 2026 — Not scheduled
  Reason: "No capacity — team focused on AI/ML initiatives"
  
Next scheduled: Not scheduled (incident occurred before scheduling)

Total time since last drill: NEVER (company 4 years old, no DR drill performed)
```

### Executive Decision Log

```
Decision: "Can we afford to keep DR infrastructure running?"
Date: January 2026
Decision Maker: VP Engineering
Decision: "No — DR costs $387/month more. We'll save the money."
Outcome: DR infrastructure powered down, must provision from scratch

Decision: "Should we maintain DR runbook?"
Date: March 2026
Decision Maker: SRE Lead (departed)
Decision: "Runbook is low priority — monitoring and alerting is more important."
Outcome: Runbook not updated for 14 months

Decision: "Chaos engineering implementation?"
Date: May 2026
Decision Maker: CTO
Decision: "Not a priority. Focus on product features for Q3."
Outcome: No chaos engineering capability

Decision: "Can we test failover?"
Date: Multiple occasions
Decision Maker: SRE Team
Decision: "No budget for cross-region testing. Too expensive."
Outcome: Failover never tested
```

### Organizational Responsibility Assignment

The failure resulted from gaps at every organizational level:

| Level | Responsible Party | Failure |
|-------|------------------|---------|
| Executive | CTO, VP Engineering | No DR culture, no investment |
| Management | SRE Director | 3 cancelled drills, no accountability |
| Process | SRE Lead (departed) | No runbook maintenance process |
| Implementation | SRE Team | No DR automation, no testing |
| Monitoring | Observability Team | Single-region monitoring only |
| Application | App Teams | No DR-aware application design |

### Failover Procedure Completeness

The 47-step manual failover procedure had the following issues:

```
Phase 1: Preparation (8 steps)
  - Step 1: AMI ID lookup → FAILED (deprecated AMI)
  - Step 2: Security group update → FAILED (wrong CIDR)
  - Step 3: Parameter validation → PASSED
  - Step 4: VPC verification → FAILED (wrong VPC ID)
  - Step 5: Subnet validation → FAILED (wrong subnets)
  - Step 6: IAM role check → FAILED (wrong roles)
  - Step 7: KMS key verification → FAILED (wrong key)
  - Step 8: SNS topic verification → FAILED (wrong ARN)
  
  Total issues in Phase 1: 7 of 8 steps had errors (87.5% failure rate)

Phase 2: Database (5 steps)
  - All 5 steps completed with issues
  - RDS lag monitoring → not configured
  - Replication lag → 12 minutes

Phase 3: Infrastructure (12 steps)
  - 10 of 12 steps had issues
  - CloudFormation template required modification
  - 3 manual interventions needed

Phase 4: Application (15 steps)
  - 8 of 15 steps had issues
  - Container images missing
  - Cache not replicated
  - Connection strings hardcoded

Phase 5: Network (7 steps)
  - 3 of 7 steps had issues
  - DNS TTL causing slow propagation
  - CloudFront cache invalidation slow

Overall: 30 of 47 steps (64%) had issues during execution
```

### RPO/RTO Definition Gap

| Service | RTO Defined | RPO Defined | Actual RTO | Actual RPO |
|---------|------------|------------|------------|------------|
| API Gateway | Not defined | Not defined | 107 min | 12 min |
| Order Service | Not defined | Not defined | 130 min | 12 min |
| Payment Service | Not defined | Not defined | 107 min | 12 min |
| User Service | Not defined | Not defined | 125 min | 12 min |
| Product Catalog | Not defined | Not defined | 150 min | 12 min |
| Search Service | Not defined | Not defined | 5+ hours | 12 min |
| Admin Dashboard | Not defined | Not defined | 180 min | 12 min |
| Reporting | Not defined | Not defined | 5+ hours | 12 min |

No RTO or RPO was defined for any service before this incident.

### Root Cause Confirmation

The root cause is confirmed by the following evidence chain:

```
1. AWS us-east-1 outage (uncontrollable)
    ↓
2. DR failover required (necessary response)
    ↓
3. Runbook 14 months stale (controllable — organizational failure)
    ↓
4. DR infrastructure not maintained (controllable — process failure)
    ↓
5. No DR drills ever conducted (controllable — executive failure)
    ↓
6. Failover executed with untested process (result)
    ↓
7. 9 issues encountered during 47-step procedure (direct evidence)
    ↓
8. RTO exceeded by 7x, RPO exceeded by 3x (outcome)
```

The AWS outage was unavoidable. The 7-hour outage was not.

### Comparison with Industry Standards

| Standard | Requirement | Our State | Gap |
|----------|------------|-----------|-----|
| AWS Well-Architected — Reliability | Test DR procedures annually at minimum | Never tested | Critical |
| AWS Well-Architected — Reliability | Define RTO/RPO for all workloads | Not defined | Critical |
| AWS Well-Architected — Reliability | Automate recovery where possible | Manual only | Critical |
| Google SRE — Disaster Recovery | Regular disaster recovery testing | Never tested | Critical |
| Google SRE — Disaster Recovery | Game days and chaos engineering | Not implemented | Critical |
| Netflix Simian Army | Chaos Monkey for resilience testing | Not implemented | Critical |
| SOC 2 — CC7.1 | Documented & tested recovery procedures | Documented only | High |
| PCI DSS 4.0 — 11.5 | Test incident response plan annually | Not tested | High |

## Recommendations Matrix

| Priority | Recommendation | Effort | Impact | Owner | Timeline |
|----------|---------------|--------|--------|-------|----------|
| P0 | Reduce Route53 TTL to 60 seconds | 1h | Critical | Network | Week 1 |
| P0 | Schedule first DR drill | 2h | Critical | SRE | Month 1 |
| P0 | Define RTO/RPO for all services | 8h | Critical | SRE Lead | Week 2 |
| P1 | Implement Route53 ARC failover | 16h | High | Cloud Infra | Month 2 |
| P1 | Cross-region RDS sync replication | 4h | High | DB Team | Month 2 |
| P1 | ECR cross-region replication | 2h | High | DevOps | Week 2 |
| P2 | CloudFormation drift detection | 8h | Medium | Cloud Infra | Month 2 |
| P2 | Pre-warm DR infrastructure | 16h | Medium | Cloud Infra | Month 3 |
| P2 | Multi-region monitoring | 8h | Medium | Observability | Month 2 |
| P2 | Chaos engineering implementation | 40h | Medium | SRE | Quarter 3 |
| P3 | Active-active architecture design | 80h | Low | Architecture | Quarter 4 |
| P3 | ElastiCache Global Datastore | 8h | Low | DB Team | Month 4 |

## Recommendations Summary

1. **Immediate**: Quarterly DR drill mandate with executive attendance
2. **Immediate**: Reduce Route53 TTL to 60 seconds for production records
3. **Short-term**: Implement automated failover with Route53 ARC
4. **Short-term**: Configure cross-region RDS replication with < 5 second lag
5. **Medium-term**: Deploy active-active multi-region architecture
6. **Medium-term**: Implement CloudFormation drift detection with auto-remediation
7. **Medium-term**: Deploy Chaos Monkey and conduct game days
8. **Long-term**: Define and monitor RTO/RPO for all critical services
9. **Long-term**: Full multi-region active-active deployment
