# Incident Response Runbook Checklist: Disaster Recovery Failover

## Incident ID: ____________________
## Date: ____________________
## Responder: ____________________

## Severity Classification

- [ ] **P0/SEV1**: Full region outage — all services down, revenue-impacting
- [ ] **P1/SEV2**: Partial region degradation — some services affected
- [ ] **P2/SEV3**: Single service failure within region
- [ ] **P3/SEV4**: Monitoring anomaly, no customer impact

## Immediate Response (First 5 Minutes)

### Detection
- [ ] Confirm AWS region status:
  ```bash
  curl -s https://health.aws.amazon.com/health/status
  ```
- [ ] Check internal health endpoints for primary region
- [ ] Verify secondary/DR region health:
  ```bash
  curl -s https://api.us-west-2.acmecorp.com/health
  ```
- [ ] Check CloudWatch metrics for sudden drops
- [ ] Confirm it's region-wide (not AZ failure)

### Declaration
- [ ] Declare P0 incident in PagerDuty
- [ ] Create Slack channel: #inc-dr-failover
- [ ] Post initial situation report:
  ```
  INC-XXXX | SEV1 | Region Outage
  Affected region: [region]
  DR region: [region]
  Services affected: [all / partial]
  Started at: [time]
  DR plan version: [YYYY-MM-DD]
  ```
- [ ] Activate incident commander
- [ ] Notify executive leadership
- [ ] Update status page

## Assessment (5-15 Minutes)

### DR Readiness Check
- [ ] Retrieve current DR runbook
- [ ] Check runbook version and last update date
- [ ] Validate runbook steps are current
- [ ] Check CloudFormation DR template:
  ```bash
  aws cloudformation validate-template --template-body file://dr-template.yaml
  ```
- [ ] Check RDS replication lag:
  ```sql
  SHOW SLAVE STATUS\G
  ```
- [ ] Check ECR cross-region replication status
- [ ] Check ElastiCache replication status

### Data Loss Assessment
- [ ] Identify last committed transaction in primary
- [ ] Identify last replicated transaction in DR
- [ ] Calculate data loss gap
- [ ] Document which data may be lost
- [ ] Notify business stakeholders of expected data loss

## Failover Execution (15-60 Minutes)

### Path A: Automated Failover (Route53 ARC)
- [ ] Execute automated failover:
  ```bash
  aws route53-recovery-cluster update-routing-control-state \
    --routing-control-arn <arn> \
    --routing-control-state Off
  ```
- [ ] Verify DNS change propagation:
  ```bash
  dig api.acmecorp.com @8.8.8.8
  ```
- [ ] Confirm failover routing points to DR region
- [ ] Execute CloudFormation DR stack if not pre-warmed:
  ```bash
  aws cloudformation create-stack \
    --stack-name acmecorp-production-dr \
    --template-body file://dr-template.yaml \
    --parameters ParameterKey=Environment,ParameterValue=dr
  ```

### Path B: Manual Failover (No Automation)
- [ ] Navigate to Route53 console
- [ ] Update DNS records to point to DR region
- [ ] Reduce TTL to 60 seconds (if not already)
- [ ] Wait for DNS propagation (monitor)
- [ ] Execute DR CloudFormation template
- [ ] Monitor stack creation progress

### Database Promotion
- [ ] Verify RDS read replica exists in DR region
- [ ] Check replication lag one final time
- [ ] Promote read replica:
  ```sql
  CALL mysql.rds_promote_read_replica;
  ```
- [ ] Verify promotion success:
  ```sql
  SELECT @@read_only;
  ```
- [ ] Update application connection strings to new primary endpoint
- [ ] Verify database is accepting writes

### Application Deployment
- [ ] Verify container images in DR region ECR
- [ ] If images not replicated:
  - [ ] Configure cross-region ECR replication
  - [ ] Pull images from source or rebuild
- [ ] Deploy application stack via CloudFormation
- [ ] Update all connection strings (DB, Redis, APIs)
- [ ] Configure ElastiCache (if not replicated)
- [ ] Warm Redis cache from database:
  ```bash
  curl -X POST https://internal.us-west-2.acmecorp.com/admin/cache/warm
  ```

### Traffic Cutover
- [ ] Update CloudFront origins to point to DR region
- [ ] Invalidate CloudFront cache:
  ```bash
  aws cloudfront create-invalidation --distribution-id <id> --paths "/*"
  ```
- [ ] Verify CDN is serving from DR origin
- [ ] Monitor error rates in DR region
- [ ] Monitor latency from multiple geographic locations
- [ ] Update status page: "Service partially restored"

## Recovery Verification

### Service Health
- [ ] All critical APIs returning 200:
  ```bash
  curl -s -o /dev/null -w "%{http_code}" https://api.acmecorp.com/health
  ```
- [ ] Database accepting reads and writes
- [ ] Redis cache operational and hit rate > 80%
- [ ] CDN serving from DR origin
- [ ] SSL certificates valid in DR region
- [ ] DNS resolving to DR region from global resolvers

### Performance Baseline
- [ ] Response times within 2x of primary baseline
- [ ] Error rate < 0.1%
- [ ] Database connections < 80% of pool
- [ ] CPU utilization < 70%
- [ ] Memory utilization < 80%

### Data Integrity
- [ ] Verify data consistency in DR database
- [ ] Document data loss (if any)
- [ ] Reconcile critical transactions
- [ ] Check for duplicate records
- [ ] Verify async queue processing

## Post-Incident (After Recovery)

### DR Documentation
- [ ] Document actual RTO achieved:
  - [ ] Failover decision time: ____
  - [ ] DNS cutover time: ____
  - [ ] Database promotion time: ____
  - [ ] Service restoration time: ____
  - [ ] Total RTO: ____
- [ ] Document actual RPO achieved:
  - [ ] Last primary write: ____
  - [ ] Last DR write: ____
  - [ ] Data loss gap: ____
- [ ] Document all issues encountered
- [ ] Update runbook with corrections

### Failback Planning
- [ ] Schedule failback to primary region
- [ ] Establish data sync from DR to primary
- [ ] Test primary region readiness
- [ ] Notify stakeholders of failback window
- [ ] Execute failback with same checklist

### Immediate Fixes
- [ ] Fix TTL to 60 seconds (if not already)
- [ ] Fix ECR cross-region replication
- [ ] Fix RDS replication lag targets
- [ ] Fix CloudFormation drift
- [ ] Update DR runbook

## Long-Term Preventive Actions

### Infrastructure
- [ ] Implement pre-warmed DR environment (pilot light)
- [ ] Implement active-active multi-region architecture
- [ ] Automate full failover with Route53 ARC
- [ ] Implement container image cross-region replication
- [ ] Implement cache cross-region replication

### Process
- [ ] Schedule quarterly DR drills
- [ ] Implement CloudFormation drift detection
- [ ] Create DR change approval workflow
- [ ] Establish RTO/RPO monitoring
- [ ] Implement chaos engineering program

### Monitoring
- [ ] Deploy multi-region monitoring
- [ ] Add cross-region health checks
- [ ] Add RDS replication lag alerts
- [ ] Add CloudFormation drift alerts
- [ ] Create DR dashboard

## Escalation Contacts

| Role | Name | Phone | Email |
|------|------|-------|-------|
| Incident Commander | | | |
| Cloud Infra Lead | | | |
| DB Team Lead | | | |
| Network Engineer | | | |
| SRE Director | | | |
| VP Engineering | | | |
| AWS Support (Enterprise) | | | |
| Legal/Compliance | | | |

## Lessons Learned

### What went well:
- _________________________________________________________________

### What went wrong:
- _________________________________________________________________

### What we will improve:
- _________________________________________________________________

## Sign-Off

- [ ] Incident report completed: ____________________
- [ ] Root cause analysis completed: ____________________
- [ ] DR plan updated: ____________________
- [ ] Action items tracked: ____________________
- [ ] Post-mortem scheduled: ____________________
- [ ] RTO/RPO recorded: RTO=____ / RPO=____
- [ ] Final approval by SRE Director: ____________________

---

*This checklist references AWS Well-Architected Framework, Netflix Chaos Engineering, Google SRE Disaster Recovery, and Azure/Google Cloud DR best practices.*
