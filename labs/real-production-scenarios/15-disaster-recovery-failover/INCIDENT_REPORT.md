# Incident Report: DR Failover — AWS us-east-1 Outage

## Incident ID: INC-2026-1127-DR
## Date: November 27, 2026 (Black Friday week)
## Severity: SEV1 (Critical — Full Region Outage)

## Timeline (All times UTC)

### 09:47 — AWS us-east-1 Degradation
- AWS Health Dashboard reports "Elevated Error Rates for EC2, RDS, and ElastiCache in US-EAST-1"
- PagerDuty alert `AWSHealthEvent` fires
- Primary SRE on-call acknowledges

### 09:48 — Initial Assessment
- SRE checks AWS Health Dashboard, CloudWatch metrics
- us-east-1 showing > 50% error rates across EC2, RDS, and ElastiCache
- Multi-AZ RDS failover did not resolve (entire region affected, not single AZ)
- SRE declares potential SEV1 and alerts secondary on-call

### 09:50 — Runbook Retrieved
- SRE retrieves DR runbook from internal wiki
- Runbook last updated: September 2025 (14 months ago)
- First issue: Runbook references deprecated AMI IDs, incorrect security group IDs

### 09:52 — Full Region Failure Confirmed
- AWS Health Dashboard updated: "Service degradation in US-EAST-1"
- All EC2 instances in us-east-1 unreachable
- RDS primary unreachable; Multi-AZ standby also affected
- ElastiCache Redis cluster in failing state
- SRE declares SEV1 incident

### 09:55 — War Room Established
- Slack #inc-aws-us-east-1-failover created
- Zoom bridge established with 14 engineers
- Incident commander assigned (SRE Lead)
- Communication lead assigned (drafting status page updates)

### 10:02 — DR Plan Review
- Team reviews runbook; identifies 3 critical issues:
  1. Runbook references `ami-0abcdef1234567890` which no longer exists
  2. Security group CIDR ranges are outdated (office IP changes)
  3. CloudFormation template references deprecated RDS instance class `db.r5.large`

### 10:08 — CloudFormation Template Fix
- SRE updates DR CloudFormation template:
  - AMI ID: Updated to latest Amazon Linux 2 AMI
  - Instance type: `db.r5.large` → `db.r6g.large`
  - Security groups: Updated CIDR ranges
- Template validation: `aws cloudformation validate-template --template-body file://dr-template.yaml`

### 10:15 — DNS TTL Issue Identified
- Route53 records have TTL=86400 (24 hours)
- Even after DNS cutover, clients with cached DNS will route to dead region
- Team decides to:
  1. Reduce TTL to 60 seconds NOW (pre-change for future)
  2. Proceed with failover despite high TTL (accept propagation delay)

### 10:18 — Cross-Region RDS Promotion Begins
- us-west-2 read replica identified
- Replication lag check:
  ```sql
  SHOW SLAVE STATUS\G
  Seconds_Behind_Master: 720 (12 minutes lag)
  ```
- Last committed transaction in us-east-1 before failure: `2026-11-27T09:35:22Z`
- us-west-2 replica is at: `2026-11-27T09:23:14Z`
- Data loss gap: 12 minutes 8 seconds

### 10:22 — Replication Lag Decision
- Team decides to proceed despite 12-minute data loss
- Data loss cannot be recovered (us-east-1 completely unreachable)
- RPO of 5 minutes breached (actual: 17 minutes)
- RDS promotion command:
  ```sql
  CALL mysql.rds_promote_read_replica;
  ```

### 10:28 — RDS Promotion Complete
- Read replica promoted to standalone primary
- DNS endpoint for RDS updated in CloudFormation template
- Database starts accepting writes
- Verification:
  ```sql
  SELECT @@read_only; -- Should return 0
  ```

### 10:35 — Application Stack Deployment
- CloudFormation stack deployment in us-west-2:
  ```bash
  aws cloudformation create-stack \
    --stack-name acmecorp-production-dr \
    --template-body file://dr-template.yaml \
    --parameters ParameterKey=Environment,ParameterValue=dr
  ```
- Stack creation hangs: security group dependency error
- Manual fix required: update VPC peering references

### 10:48 — Stack Creation Fixed
- Security group dependency resolved
- CloudFormation stack continues; ECS services deploy
- Application containers start pulling images from ECR
- ECR repo in us-east-1 is unavailable (region down)
- Team discovers: ECR images are not replicated to us-west-2

### 10:55 — Container Image Replication
- Team configures cross-region ECR replication to us-west-2
- Docker images pulled from ECR public gallery (workaround)
- Custom application images must be rebuilt from source
- CI/CD pipeline triggered to rebuild and push to us-west-2 ECR

### 11:15 — Application Startup
- First application containers starting in us-west-2
- Connection strings updated for us-west-2 RDS endpoint
- ElastiCache Redis cluster provisioning started in us-west-2

### 11:28 — Redis Cache Warming
- ElastiCache Redis cluster available in us-west-2
- Cache is empty (no cross-region replication configured)
- Application performance degraded without cache
- Cache warming process initiated:
  ```bash
  # Load critical cache entries from database
  curl -X POST https://internal-api.us-west-2.acmecorp.com/admin/cache/warm
  ```

### 11:35 — Route53 DNS Cutover
- Route53 health checks updated for us-west-2 endpoints
- DNS records updated:
  ```bash
  aws route53 change-resource-record-sets \
    --hosted-zone-id ZONEID \
    --change-batch file://dns-cutover.json
  ```
- `us-west-2` DNS records set as primary (failover routing policy)
- DNS TTL remains at 86400 (24 hours) — pre-change not done

### 12:10 — CloudFront Origin Update
- CloudFront distributions updated to point to us-west-2 ALB
- Origin domain name updated in CloudFront console
- Cache invalidation triggered:
  ```bash
  aws cloudfront create-invalidation \
    --distribution-id DISTRIBUTION \
    --paths "/*"
  ```
- Invalidation takes 12 minutes to complete

### 12:22 — First Successful User Traffic
- Internal test confirms traffic routing to us-west-2
- curl https://api.acmecorp.com/v2/health returns 200
- Response time: 340ms (higher than us-east-1's 45ms due to geographic distance)

### 12:35 — DNS Propagation Still in Progress
- Only ~35% of traffic routing to us-west-2
- DNS resolvers worldwide still returning cached us-east-1 IP
- Estimated full DNS propagation: 24 hours (TTL=86400)
- Workaround: Users in affected regions can flush DNS or use different resolvers

### 13:15 — Performance Assessment
- Application performance in us-west-2:
  - Average latency: 320ms (baseline: 45ms)
  - Error rate: 3.5% (baseline: < 0.1%)
  - Database connections: 47/200 (baseline: 120/200)
  - Cache hit rate: 12% (baseline: 94%)
- Auto-scaling groups increasing capacity in us-west-2
- Team notes need for pre-warmed DR infrastructure

### 13:47 — All Critical Services Running
- All 8 critical services confirmed operational in us-west-2
- Remaining non-critical services (reporting, analytics) still pending
- Support tickets: 12,400+ from customers
- Status page updated: "Service partially restored — degraded performance"

### 14:30 — AWS us-east-1 Recovery
- AWS Health Dashboard: "We are seeing improvement in error rates"
- Some us-east-1 services recovering
- Team decides to continue in us-west-2 (don't fail back until stable)

### 16:57 — Incident Declared Resolved
- All services operational in us-west-2
- 95% of traffic routing correctly (DNS propagation mostly complete)
- AWS confirmed us-east-1 fully recovered
- DR infrastructure to remain active for 72 hours before planning failback

## Detailed Outage Analysis

### AWS Service Impact

```
AWS us-east-1 Service Status at 09:47 UTC:

EC2: 
  - 72% error rate on API calls
  - 47% of instances unreachable
  - New instance launches: FAILING
  - Status: IMPAIRED

RDS:
  - Primary: UNREACHABLE
  - Multi-AZ standby: UNREACHABLE  
  - Read replicas: UNREACHABLE
  - New DB instances: FAILING
  - Status: UNAVAILABLE

ElastiCache:
  - Redis cluster: DEGRADED
  - Memcached cluster: UNAVAILABLE
  - New cache clusters: FAILING
  - Status: IMPAIRED

Route53:
  - Health checks: WORKING
  - DNS resolution: WORKING
  - Status: OPERATIONAL

CloudFront:
  - Edge locations: WORKING
  - Origin connectivity: FAILING (origin in us-east-1)
  - Status: IMPAIRED (for affected origins)
```

### Failover Issue Log

Each issue encountered during failover, with resolution time:

| # | Issue | Discovered | Resolved | Time Lost | Root Cause |
|---|-------|-----------|----------|-----------|------------|
| 1 | AMI ID deprecated | 10:02 | 10:06 | 4 min | Runbook not updated |
| 2 | RDS instance class deprecated | 10:02 | 10:08 | 6 min | Template drift |
| 3 | Security group CIDR wrong | 10:02 | 10:07 | 5 min | Office IP change |
| 4 | RDS replication lag 12 min | 10:18 | 10:28 | 10 min | Async replication config |
| 5 | CloudFormation SG dependency | 10:35 | 10:48 | 13 min | VPC peering mismatch |
| 6 | ECR images not in us-west-2 | 10:48 | 11:15 | 27 min | No cross-region replication |
| 7 | Empty Redis cache | 11:20 | 11:35 | 15 min | No global datastore |
| 8 | DNS TTL 86400 | 10:15 | 11:35 | 80 min (waiting) | Unchanged from default |
| 9 | CloudFront cache invalidation | 12:00 | 12:12 | 12 min | Default settings |
| **Total** | **9 issues** | | | **~172 min** | |

### Communication Log

```
09:48 — PagerDuty alert sent to on-call SRE
09:50 — Slack #ops-alerts: "AWS us-east-1 degradation, investigating"
09:52 — Slack #inc-aws-us-east-1-failover created
09:55 — Zoom bridge established (14 participants)
10:00 — Status page updated: "Investigating — us-east-1 region issues"
10:15 — Status page updated: "Identified — initiating failover to us-west-2"
10:28 — Slack: "RDS promotion in progress, 12 min data loss expected"
11:00 — Status page updated: "Failover in progress — expected restoration ETA 12:30 UTC"
12:22 — Status page updated: "Service partially restored — us-west-2 active"
12:35 — Email to enterprise clients: "Incident update — failover complete"
13:47 — Status page updated: "All critical services operational"
16:57 — Status page: "Resolved"
Day+1: Post-mortem scheduled email to all stakeholders
```

### RDS Promotion Verification

```sql
-- Check replication status before promotion
SHOW SLAVE STATUS\G
*************************** 1. row ***************************
               Slave_IO_State: Waiting for master to send event
                  Master_Host: primary.abc123.us-east-1.rds.amazonaws.com
                  Master_User: replication_user
                Master_Log_File: mysql-bin.001234
            Read_Master_Log_Pos: 847291234
               Relay_Log_File: relay-bin.001234
                Relay_Log_Pos: 847123456
        Relay_Master_Log_File: mysql-bin.001234
             Slave_IO_Running: Yes
            Slave_SQL_Running: Yes
        Seconds_Behind_Master: 720  <-- 12 minutes!
          
-- Promote read replica
CALL mysql.rds_promote_read_replica;

-- Verify promotion
SELECT @@read_only;
+-------------+
| @@read_only |
+-------------+
|           0 |  <-- Read-write now!
+-------------+

-- Check data after promotion
SELECT COUNT(*) FROM orders WHERE created_at > '2026-11-27T09:23:14Z';
+----------+
| COUNT(*) |
+----------+
|        0 |  <-- No data after last replication point
+----------+

-- Data loss confirmed: 12 minutes 8 seconds of transactions
-- Estimated 847 orders lost
```

### Failover Cost Comparison

| Resource | Normal (us-east-1) Monthly | DR (us-west-2) Monthly | Delta |
|----------|---------------------------|----------------------|-------|
| EC2 compute | $12,847 | $12,847 | $0 |
| RDS database | $3,847 | $3,847 | $0 |
| ElastiCache | $1,234 | $1,234 | $0 |
| ALB | $847 | $847 | $0 |
| NAT Gateway | $234 | $234 | $0 |
| Data transfer | $847 | $1,234 | +$387 |
| **Total DR cost** | | | **+$387/month** |

The DR environment would cost only $387/month more to keep running (pre-warmed).
This is 0.05% of the revenue lost during the 7-hour outage ($742,000).

### Engineer Resource Cost

| Role | Count | Time Invested | Hourly Rate | Cost |
|------|-------|---------------|-------------|------|
| SRE Lead (IC) | 1 | 7h 10m | $220 | $1,577 |
| SRE Engineer | 2 | 7h 10m | $180 | $2,580 |
| Cloud Infra Engineer | 2 | 5h | $200 | $2,000 |
| DB Engineer | 1 | 4h | $220 | $880 |
| Network Engineer | 1 | 3h | $200 | $600 |
| App Developer | 2 | 2h | $180 | $720 |
| Customer Support | 5 | 7h 10m | $80 | $2,867 |
| Communications Lead | 1 | 7h 10m | $150 | $1,075 |
| **Total** | **15** | | | **$12,299** |

## Post-Incident Actions

1. Quarterly DR drills with full failover testing
2. Automated failover with Route53 Application Recovery Controller (ARC)
3. Cross-region RDS replication with < 5 second lag target
4. Route53 DNS TTL reduced to 60 seconds for production records
5. Multi-region monitoring deployment (Prometheus/Grafana in both regions)
6. CloudFormation drift detection and automated syncing
7. Chaos engineering implementation (chaos experiments for region failure)
8. Container image cross-region replication (ECR replication)
9. Active-active multi-region architecture design
10. RTO/RPO monitoring with automated alerting
11. Pre-warmed DR environment (pilot light strategy)
12. ElastiCache Global Datastore for cross-region cache replication
