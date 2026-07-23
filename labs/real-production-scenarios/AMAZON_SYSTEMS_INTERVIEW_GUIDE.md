# Amazon Systems Interview Guide — Real Production Scenarios Academy

## Interview Process for Systems/Infra Roles

### Rounds
1. **Phone Screen (60 min)**: Systems design or debugging scenario. Often focused on a specific AWS service or distributed system problem.
2. **Onsite (5 rounds, 60 min each)**:
   - **Systems Design Round 1**: Design a large-scale distributed system (e.g., design S3, design DynamoDB, design a load balancer)
   - **Systems Design Round 2**: Focus on operational excellence — design for failure detection, auto-remediation, self-healing
   - **Coding Round**: LeetCode medium, often with systems flavor (file system operations, network protocols)
   - **Bar Raiser (Behavioral)**: Deep-dive into Amazon Leadership Principles, especially "Operational Excellence", "Dive Deep", "Have Backbone"
   - **Manager Round**: Vision for operational excellence, team culture

### Systems/Infra-Specific Expectations
- Amazon uses the "Working Backwards" method — start with the customer need
- Operational Excellence is a Leadership Principle — every design must include monitoring, alarming, runbooks, rollback
- Systems interviews focus on fault tolerance, durability, availability — the CAP theorem is a starting point
- Must understand AWS services deeply (S3, DynamoDB, EC2, Lambda, Route 53, CloudWatch)
- Expect "what would you do if this component fails?" at every step of design
- Amazon uses "6-pager" documents — practice structured written communication

### Round Breakdown
- Systems Design: 50% — most important round
- Behavioral (LP): 30% — Amazon is obsessive about LPs
- Coding: 15% — applied problems
- Manager: 5% — cultural fit

### AWS CLI Cheat Sheet for Systems Interviews

Master these AWS CLI commands for interview scenarios:

**S3 operations**:
```bash
# Check bucket public access
aws s3api get-public-access-block --bucket my-bucket

# Block public access
aws s3api put-public-access-block --bucket my-bucket \
  --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true

# List large objects
aws s3api list-objects --bucket my-bucket --query "Contents[?Size>\`1073741824\`].{Key: Key, Size: Size}"
```

**EC2/ASG operations**:
```bash
# Describe ASG instances by launch template version
aws autoscaling describe-auto-scaling-groups --auto-scaling-group-names my-asg \
  --query "AutoScalingGroups[].Instances[?LaunchTemplateVersion!='\$Default']"

# Stop deployment
aws deploy stop-deployment --deployment-id d-EXAMPLE

# Rollback ASG to previous launch template
aws autoscaling update-auto-scaling-group --auto-scaling-group-name my-asg \
  --launch-template LaunchTemplateName=my-template,Version='$Default'
```

**RDS operations**:
```bash
# Kill idle connections
aws rds describe-db-instances --db-instance-identifier my-db \
  --query "DBInstances[].Endpoint.Address"

# Promote read replica
aws rds promote-read-replica --db-instance-identifier my-db-replica
```

**Route 53 / DNS**:
```bash
# Failover DNS record
aws route53 change-resource-record-sets --hosted-zone-id ZONEID \
  --change-batch '{"Changes":[{"Action":"UPSERT","ResourceRecordSet":{"Name":"api.example.com.","Type":"A","AliasTarget":{"HostedZoneId":"ZONEID","DNSName":"prod-alb-123.us-east-1.elb.amazonaws.com","EvaluateTargetHealth":true}}}]}'
```

## Top Incidents Aligned to Amazon Systems Focus

### Incident: Disk Space Full — Log Flood (Lab 09)
#### Problem Scenario
An EC2 instance running a critical payment processing service runs out of disk space at 2 AM. The service stops processing payments. CloudWatch alarm for `DiskSpaceUtilization` fires. The latest deployment rolled out verbose debug logging that writes 200GB/hour to disk.

#### Interview Walkthrough
**Step 1 — Assess the blast radius**: Check if this is a single AZ issue or region-wide. The alarm is for one instance in us-east-1a. The ASG has 4 instances across 3 AZs — only this one AZ is affected.

**Step 2 — Immediate mitigation**: SSH into the instance. Run `df -h` to confirm disk is 100% full. Run `du -sh /var/log/* | sort -rh` to find the largest logs. The `payment-debug.log` is 180GB.

**Step 3 — Clear space**: `truncate -s 0 /var/log/payment-debug.log` to free space immediately. The service resumes processing.

**Step 4 — Root cause**: A deployment changed log level from `INFO` to `DEBUG` for the `PaymentProcessor` class. Each payment transaction writes 5KB of debug logs. At 500 TPS, that's 2.5MB/s → 9GB/hour → 216GB/day. The root volume is 100GB.

**Step 5 — Long-term fix**: Implement log rotation (logrotate with max size 1GB and keep 3 rotations). Set CloudWatch alarm at 80% disk usage. Add log-level validation in deployment pipeline.

**What Amazon evaluates**: Speed of response (MTTR); understanding of blast radius; prioritization of fix vs root cause; operational runbook quality.

#### Solution
```bash
# Logrotate configuration
cat > /etc/logrotate.d/payment-service << 'EOF'
/var/log/payment-*.log {
    daily
    rotate 7
    maxsize 1G
    compress
    delaycompress
    missingok
    notifempty
    copytruncate
}
EOF

# CloudWatch alarm via AWS CLI
aws cloudwatch put-metric-alarm \
  --alarm-name "payment-service-disk-utilization-critical" \
  --metric-name DiskSpaceUtilization \
  --namespace CWAgent \
  --statistic Average \
  --period 300 \
  --threshold 80 \
  --comparison-operator GreaterThanThreshold \
  --evaluation-periods 2 \
  --alarm-actions arn:aws:sns:us-east-1:123456789012:oncall
```

**Post-mortem**: Add deployment pipeline check that blocks deployment if log level is `DEBUG` in production config. Add canary deploy that monitors disk usage for 5 minutes.

#### Follow-ups
- **At Amazon scale**: Use centralized logging (CloudWatch Logs) with log group policies. Set per-instance disk budget.
- **Auto-remediation**: Create an AWS Lambda + Systems Manager Automation that truncates logs when disk exceeds 90%.

### Incident: Connection Pool Exhaustion — RDS (Lab 04)
#### Problem Scenario
An RDS MySQL instance for the "order-processing" service hits "Too many connections" error. The application layer sees 100% error rate for new orders. RDS `DBConnections` metric spikes to the max (400).

#### Interview Walkthrough
**Step 1 — Check RDS metrics**: CloudWatch shows `DatabaseConnections = 400` (max). `CPUUtilization = 95%`. `ReadLatency = 50ms` (10x normal).

**Step 2 — Identify the source**: Run `SHOW FULL PROCESSLIST` on RDS. 350 connections are in `Sleep` state with no active query. 50 connections are running slow queries.

**Step 3 — Root cause**: The application's HikariCP leak detection was disabled. A transaction opens connections but doesn't close them on exception. Additionally, the slow queries are from a new reporting feature doing full table scans.

**Step 4 — Immediate fix**: Kill idle connections: `CALL mysql.rds_kill(id)` for each idle thread. Increase max_connections temporarily to 800.

**Step 5 — Long-term fix**: Enable HikariCP leak detection. Add connection timeout. Optimize the slow query with an index.

**What Amazon evaluates**: Knowledge of RDS; SQL query analysis; connection pool tuning; ability to prioritize immediate vs long-term fixes.

#### Solution
```java
HikariConfig config = new HikariConfig();
config.setJdbcUrl("jdbc:mysql://orders.c9yjc8w5kdfk.us-east-1.rds.amazonaws.com:3306/orders");
config.setUsername("app_user");
config.setPassword("****");
config.setMaximumPoolSize(50);
config.setMinimumIdle(10);
config.setConnectionTimeout(5000);
config.setIdleTimeout(300000);
config.setMaxLifetime(600000);
config.setLeakDetectionThreshold(30000);
```

**Post-mortem**: Add RDS connection count alarm at 80% of max. Add slow query logging. Enable Performance Insights on RDS.

#### Follow-ups
- **At Amazon scale**: Use Amazon RDS Proxy to manage connection pooling. Implement Aurora for auto-scaling.
- **Prevention**: Set CloudWatch composite alarms — connections > 80% AND latency > 10ms triggers runbook.

### Incident: Deployment Rollback — Bad AMI (Lab 06)
#### Problem Scenario
A new AMI is deployed via Auto Scaling Group rolling update. After replacing 30% of instances, the error rate increases from 0.1% to 15%. The deployment is to the "search-service".

#### Interview Walkthrough
**Step 1 — Detect the issue**: CloudWatch alarm for `ErrorRate` fires. The deployment tool (CodeDeploy) shows the latest deployment in progress.

**Step 2 — Immediate action**: Stop the deployment: `aws deploy stop-deployment --deployment-id d-EXAMPLE`. Rollback to the previous deployment.

**Step 3 — Isolate the bad instances**: Describe the ASG to find instances with the new AMI. Get instance IDs.

**Step 4 — Root cause**: The new AMI has a misconfigured `nginx` reverse proxy that adds a 5-second delay to all responses. The AMI build pipeline didn't include an integration test.

**Step 5 — Fix**: Revert AMI. Add smoke tests to the AMI build pipeline. Add canary deployment.

**What Amazon evaluates**: Knowledge of CodeDeploy/ASG; rollback speed; AMI lifecycle understanding.

#### Solution
```yaml
# CloudFormation ASG with Rollback
Type: AWS::AutoScaling::AutoScalingGroup
Properties:
  MinSize: '3'
  MaxSize: '20'
  DesiredCapacity: '5'
  LaunchTemplate:
    LaunchTemplateId: !Ref LaunchTemplate
    Version: !Ref LatestVersion
  AvailabilityZones:
    - us-east-1a
    - us-east-1b
    - us-east-1c
  HealthCheckType: ELB
  HealthCheckGracePeriod: 300
  TerminationPolicies:
    - OldestLaunchTemplate
    - Default
```

#### Follow-ups
- **At Amazon scale**: Use canary deployments with 1% traffic to new AMI for 15 minutes.
- **Prevention**: AMI bake pipeline must pass integration tests before marking as "production-ready".

### Incident: TLS Certificate Expiry — Route 53 Health Checks (Lab 12)
#### Problem Scenario
Route 53 health checks for `api.amazon.com` start failing. ELB reports unhealthy instances. The TLS certificate for the wildcard domain `*.amazon.com` expired at midnight.

#### Interview Walkthrough
**Step 1 — Verify the certificate**: `aws acm describe-certificate --certificate-arn arn:aws:acm:us-east-1:123456789012:certificate/xxxxx`. Shows `Status: EXPIRED`.

**Step 2 — Assess impact**: Health checks are failing because the ELB cannot establish TLS connections.

**Step 3 — Immediate fix**: Request a new certificate via ACM and re-associate with the ELB listener. `aws acm request-certificate --domain-name *.amazon.com --validation-method DNS`. Update DNS validation record.

**Step 4 — Root cause**: No certificate expiry monitoring. Auto-renewal was not configured.

**Step 5 — Long-term fix**: Enable ACM managed renewal. Set up CloudWatch event + Lambda to notify 30 days before expiry.

**What Amazon evaluates**: Understanding of ACM/ELB/Route 53 integration; incident response time.

#### Solution
```bash
CERT_ARN=$(aws acm list-certificates --region us-east-1 \
  --query "CertificateSummaryList[?contains(DomainName,'amazon.com')]|[?Status!='EXPIRED']|[0].CertificateArn" \
  --output text)
EXPIRY=$(aws acm describe-certificate --certificate-arn $CERT_ARN \
  --query "Certificate.NotAfter" --output text)
DAYS_LEFT=$(( ($(date -d "$EXPIRY" +%s) - $(date +%s)) / 86400 ))
if [ "$DAYS_LEFT" -lt 30 ]; then
  aws acm renew-certificate --certificate-arn $CERT_ARN
fi
```

#### Follow-ups
- **At Amazon scale**: Use ACM with automatic DNS validation across all regions. AWS Config rule to detect certificates expiring within 30 days.
- **Blast radius reduction**: Separate certificates per service, not a wildcard.

### Incident: Cache Stampede — DynamoDB (Lab 08)
#### Problem Scenario
A product detail service uses DynamoDB with DAX caching. TTL is set to 5 minutes. Every 5 minutes, read latency spikes from 2ms to 200ms and DAX hit rate drops from 99% to 10%.

#### Interview Walkthrough
**Step 1 — Identify the pattern**: CloudWatch metric `DAX-HitRate` shows drops to 10% every 5 minutes. `DynamoDB-ReadThrottleEvents` spikes during these windows.

**Step 2 — Root cause**: All cache entries have the same TTL and were populated by a batch warm-up job. They all expire simultaneously.

**Step 3 — Fix**: Implement TTL jitter. DAX doesn't support atomic add for stampede prevention, so implement client-side probabilistic early expiration (XFetch).

**What Amazon evaluates**: Understanding of DAX/DynamoDB integration; TTL management; cache stampede prevention.

#### Solution
```java
public Product getProduct(String productId) {
    String cacheKey = "product:" + productId;
    Product cached = daxClient.get(cacheKey);
    if (cached == null) return fetchAndCache(productId, cacheKey);
    int ttl = getRemainingTTL(cacheKey);
    int originalTtl = 300;
    double p = (originalTtl - ttl) / (double) originalTtl;
    double threshold = Math.random();
    if (p > threshold) {
        daxClient.delete(cacheKey);
        return fetchAndCache(productId, cacheKey);
    }
    return cached;
}

private Product fetchAndCache(String productId, String cacheKey) {
    Product product = dynamoDBClient.getItem(productId);
    daxClient.set(cacheKey, product, 300 + ThreadLocalRandom.current().nextInt(60));
    return product;
}
```

### Incident: Slow Query — DynamoDB Scan (Lab 05)
#### Problem Scenario
A DynamoDB table "orders" stores 500M records. A new API endpoint `/orders/search` is released. Users report the page takes 30 seconds to load.

#### Interview Walkthrough
**Step 1 — Check the query pattern**: The API does a `Scan` on the `orders` table filtering by `status` and `created_date`. Scan reads every item.

**Step 2 — Root cause**: No Global Secondary Index (GSI) on status/created_date. The developer used `Scan` instead of `Query`.

**Step 3 — Fix**: Create a GSI on `status-created_date-index`. Change to use `Query` on the GSI.

**What Amazon evaluates**: DynamoDB query patterns; GSI design; Scan vs Query differences.

#### Solution
```java
QueryRequest queryRequest = new QueryRequest()
    .withTableName("orders")
    .withIndexName("status-created_date-index")
    .withKeyConditionExpression("#status = :status AND #created >= :date")
    .withExpressionAttributeNames(Map.of("#status", "status", "#created", "created_date"))
    .withExpressionAttributeValues(Map.of(":status", new AttributeValue("PROCESSING"), ":date", new AttributeValue().withS(date)));
QueryResult result = dynamoDB.query(queryRequest);
```

### Incident: API Rate Limiting — Application Load Balancer (Lab 14)
#### Problem Scenario
A new product launch causes a 20x traffic spike to the product detail service. The ALB returns 503 errors. The service's target group shows "unhealthy hosts" because the health check endpoint is also rate-limited.

#### Interview Walkthrough
**Step 1 — Check ALB metrics**: CloudWatch shows `RequestCount` at 50k/minute (normal is 2.5k). `TargetResponseTime` at 10 seconds. `HTTP_503s` increasing.

**Step 2 — Root cause**: The health check endpoint (`/health`) is handled by the same application code that processes API requests. Under load, the health check also experiences high latency, causing the ALB to mark instances as unhealthy and stop sending traffic.

**Step 3 — Fix**: Implement a separate health check endpoint that uses a dedicated thread pool. Add ALB-based rate limiting (AWS WAF). Use surge queue length in target group to absorb spikes.

#### Solution
```java
// Dedicated health check endpoint — no DB calls, no external dependencies
@GetMapping("/health/lb")
@ResponseStatus(HttpStatus.OK)
public String healthCheck() {
    return "OK";
}

// In application.properties
server.tomcat.threads.max=200
management.endpoint.health.show-details=never
```

#### Follow-ups
- **At Amazon scale**: Use ALB connection draining and slow start to handle traffic spikes. Use AWS WAF rate-based rules for API protection. Implement adaptive concurrency limits.
- **Auto-scaling**: Pre-warm ASG before product launches using scheduled scaling policies.

### Incident: Security Breach — S3 Bucket Misconfiguration (Lab 10)
#### Problem Scenario
AWS Config reports that an S3 bucket `customer-data-prod` has `PublicAccessBlockConfiguration` disabled. The bucket contains 50M customer records with PII data (names, addresses, payment details).

#### Interview Walkthrough
**Step 1 — Verify the finding**: `aws s3api get-public-access-block --bucket customer-data-prod`. Shows `BlockPublicAcls: false`, `BlockPublicPolicy: false`. The bucket is publicly accessible.

**Step 2 — Check bucket policy**: `aws s3api get-bucket-policy --bucket customer-data-prod`. The policy allows `s3:GetObject` from `Principal: "*"`. Any anonymous user can read any object.

**Step 3 — Immediate containment**: `aws s3api put-public-access-block --bucket customer-data-prod --public-access-block-configuration BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true`.

**Step 4 — Root cause**: A developer ran a Terraform script that applied an overly permissive bucket policy. The Terraform plan wasn't reviewed. There was no SCP denying public S3 access at the organization level.

**Step 5 — Fix**: Add an SCP that blocks public S3 buckets. Enable Amazon Macie for data classification. Add Terraform policy as code (`tfsec` or `checkov`).

**What Amazon evaluates**: Security incident response; S3 security best practices; containment speed; prevention at scale.

#### Solution
```bash
# Immediate containment
aws s3api put-public-access-block \
  --bucket customer-data-prod \
  --public-access-block-configuration \
    BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true

# Audit access logs
aws s3api get-bucket-logging --bucket customer-data-prod
aws s3api list-objects --bucket customer-data-prod --prefix audit/

# Organization SCP to prevent public buckets
# In AWS Organizations, create an SCP:
{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Deny",
    "Action": [
      "s3:PutBucketPublicAccessBlock",
      "s3:PutBucketPolicy"
    ],
    "Resource": "*",
    "Condition": {
      "StringNotEquals": {
        "s3:x-amz-public-access-block-configuration": "BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true"
      }
    }
  }]
}
```

#### Follow-ups
- **At Amazon scale**: Use AWS Config managed rules `s3-bucket-public-read-prohibited` and `s3-bucket-public-write-prohibited`. Use Amazon GuardDuty for anomaly detection.
- **Prevention**: Add `checkov` to CI/CD pipeline that blocks Terraform plans with `s3_bucket_public_access_block` disabled.

### Incident: DR Failover — Multi-Region Outage (Lab 15)
#### Problem Scenario
The us-east-1 region experiences a major outage affecting an e-commerce platform deployed in a single region. The RTO is 4 hours and RPO is 15 minutes. The platform needs to fail over to us-west-2.

#### Interview Walkthrough
**Step 1 — Assess the situation**: us-east-1 is completely unavailable. Route 53 health checks are failing. The platform uses RDS with cross-region read replicas in us-west-2.

**Step 2 — Promote read replica**: `aws rds promote-read-replica --db-instance-identifier orders-db-replica`. This promotes the us-west-2 read replica to a standalone writer.

**Step 3 — Update Route 53**: Point the production DNS record to the us-west-2 ELB. `aws route53 change-resource-record-sets --hosted-zone-id ZONEID --change-batch file://dns-update.json`.

**Step 4 — Verify the failover**: Check that all services are operational. The promotion resulted in 5 minutes of data loss (within RPO).

**Step 5 — Root cause**: The architecture was single-region. No DR plan existed. This was a design failure, not an operational failure.

**What Amazon evaluates**: DR planning; RTO/RPO understanding; multi-region architecture; ability to execute failover under pressure.

#### Solution
```bash
# Promote RDS read replica to master
aws rds promote-read-replica \
  --db-instance-identifier orders-db-replica \
  --region us-west-2

# Update Route 53 to point to us-west-2
cat > dns-update.json << 'EOF'
{
  "Changes": [{
    "Action": "UPSERT",
    "ResourceRecordSet": {
      "Name": "api.example.com.",
      "Type": "A",
      "AliasTarget": {
        "HostedZoneId": "USWEST2ELBZONE",
        "DNSName": "prod-us-west-2.elb.amazonaws.com",
        "EvaluateTargetHealth": true
      }
    }
  }]
}
EOF
aws route53 change-resource-record-sets \
  --hosted-zone-id ZONEID \
  --change-batch file://dns-update.json

# Verify failover
aws rds describe-db-instances --db-instance-identifier orders-db-replica --region us-west-2
```

#### Follow-ups
- **At Amazon scale**: Use Aurora Global Database for 1-second RPO cross-region replication. Implement Route 53 active-passive failover with health checks. Automate the entire DR process with AWS Systems Manager Automation.
- **Chaos experiment**: Run a GameDay that simulates a regional outage to validate the DR plan.

## System Design for Reliability

### Design Question 1: Design S3 for 99.999999999% Durability
Design the object storage system that achieves 11 9s of durability. Discuss replication strategies (erasure coding vs replication), failure domains (AZ, region), consistency model, and data integrity verification.

**Key points**: Erasure coding (12+4 scheme) across AZs. CRCs for data integrity. Background scrubber for silent data corruption. Versioning for overwrite protection.

### Design Question 2: Design an Auto-Remediation System
Design a system that automatically detects and remediates common production incidents. Discuss detect → classify → remediate → verify → escalate. How do you ensure the auto-remediation doesn't cause cascading failure?

**Key points**: CloudWatch alarms trigger Lambda or Systems Manager Automation. Classification via incident severity. Rollback with circuit breaker pattern. Rate limiting on remediation actions.

### Design Question 3: Design DynamoDB Auto-Scaling
Design capacity management for DynamoDB. Discuss prediction models, cooldown periods, and handling of traffic spikes.

## Incident Command Behavioral

### Question 1: Tell me about a time you handled a critical system failure. (Have Backbone)
**STAR**: During a disk space incident (Lab 09), I had to push back against a team wanting to add monitoring only. I insisted on immediate log rotation + deployment pipeline validation. I escalated to the manager, and after explaining the blast radius risk, they agreed.

### Question 2: Describe a time you dived deep into a complex problem. (Dive Deep)
**STAR**: Connection pool exhaustion (Lab 04) was dismissed as "just need more connections." I dived deep, profiled all connection usage, found the leaking transaction, and also found the slow query. I fixed both, reducing required connections from 400 to 50.

### Question 3: How do you ensure operational excellence? (Operational Excellence)
**STAR**: After the deployment rollback incident (Lab 06), I built a canary deployment pipeline with automated rollback. I also added smoke tests to the AMI build pipeline. Error rate went from 15% on bad deployments to zero rollbacks needed in 6 months.

### Question 4: Tell me about a time you made a decision without all the data. (Bias for Action)
**STAR**: During the TLS expiry (Lab 12), I couldn't immediately find the exact certificate. I issued a new one via ACM within 5 minutes and deployed it, then investigated the root cause after restoring service.

### Question 5: Describe a time you invented a simpler solution. (Invent and Simplify)
**STAR**: The cache stampede issue (Lab 08) was going to require a complex distributed locking system. I proposed using probabilistic early expiration (XFetch) with just 4 lines of additional code.

### Question 6: How do you earn trust with your team? (Earn Trust)
**STAR**: I transparently shared the post-mortem for the RDS connection pool incident (Lab 04), including my mistakes in not enabling leak detection earlier. I then led the effort to add automated leak detection across all services.

### Question 7: Describe a time you thought big about a solution. (Think Big)
**STAR**: The S3 security breach (Lab 10) affected one bucket. Instead of just fixing that bucket, I thought big: I created an organization-level SCP that blocks all public S3 buckets across 50 accounts, and a centralized Config aggregator that monitors S3 security posture in real time.

### Question 8: How do you ensure you're customer-obsessed? (Customer Obsession)
**STAR**: During the DR failover (Lab 15), I started the failover process within 5 minutes. I communicated the ETA to all stakeholders and provided status updates every 15 minutes. After failover, I prioritized read-only mode for the shopping page before the full checkout system.

### Question 9: Tell me about a time you helped a teammate grow. (Hire and Develop the Best)
**STAR**: A junior engineer was overwhelmed during the TLS expiry incident (Lab 12). After the incident, I paired with them to automate certificate renewal, explaining the ACM/KV/ELB integration. They now lead our certificate management runbook.

### Question 10: How do you deal with a situation where processes get in the way? (Bias for Action)
**STAR**: During the cache stampede (Lab 08), the change management process required 3-day approval for any code change. I argued this was an emergency and got a 30-minute expedited review. The fix was deployed in 2 hours, not 3 days.

### Question 11: Describe a time you built a system that lasted for years. (Deliver Results)
**STAR**: I designed the auto-remediation system for disk space incidents (Lab 09). The Lambda + Systems Manager Automation solution has been running for 3 years without changes, handling 200+ incidents automatically each month.

### Question 12: How do you ensure your team learns from incidents? (Insist on the Highest Standards)
**STAR**: After the S3 security breach (Lab 10), I created a "security incident playbook" and ran monthly GameDays where teams practice detecting and containing public S3 buckets. We reduced mean time to containment from 4 hours to 15 minutes.

### Question 13: Tell me about a time you simplified a complex system. (Simplify)
**STAR**: The DR failover process (Lab 15) was a 20-page document. I automated it with Systems Manager Automation runbooks, reducing the failover process to a single command with 5 verification steps. RTO improved from 2 hours to 20 minutes.

### Question 14: How do you stay updated on AWS services?
**STAR**: I follow AWS re:Invent announcements, run a weekly "AWS This Week" internal newsletter for my team, and maintain a lab environment where we test new services (like Aurora Limitless Database) before recommending them for production.

### Question 15: Describe a time you had to make a tradeoff between speed and quality. (Frugality)
**STAR**: For the DynamoDB scan optimization (Lab 05), I could have spent 3 weeks building the ideal solution (Elasticsearch for search). Instead, I spent 2 hours adding a GSI, solving 95% of the problem with 0.5% of the effort.

## Study Plan

### Priority Labs for Amazon Systems
1. **Lab 09 (Disk Space)** — Most common infra incident
2. **Lab 04 (Connection Pool)** — RDS reliability is critical
3. **Lab 06 (Deployment Rollback)** — Deployment processes
4. **Lab 12 (TLS Expiry)** — Certificate lifecycle management
5. **Lab 08 (Cache Stampede)** — DynamoDB capacity planning
6. **Lab 05 (Slow Query)** — DynamoDB query optimization
7. **Lab 14 (API Rate Limiting)** — Scaling under load

### Recommended Schedule
- **Week 1-2**: Labs 09, 04, 06 (infra operations)
- **Week 3**: Labs 12, 08, 05 (automation + caching + DynamoDB)
- **Week 4**: Lab 14 + system design + LP preparation
- **Week 5**: Mock interviews with AWS CLI practice

## Tips

### Amazon Systems Interview Strategies
1. **Leadership Principles are everything**: Weave LPs into every answer. "Dive Deep" for technical, "Operational Excellence" for process, "Have Backbone" for disagreements, "Bias for Action" for incident response.
2. **Think in terms of AWS**: Every design should leverage AWS services. Know S3, DynamoDB, EC2, Lambda, CloudWatch, Route 53, ELB, ASG deeply.
3. **Use the "Working Backwards" method**: Start with "what does the customer need?" and work backwards from there.
4. **Blast radius analysis**: In every incident discussion, talk about blast radius, containment strategy, and isolation.
5. **Automation is expected**: Show that you think about auto-remediation and self-healing systems.
6. **6-pager thinking**: Write structured, concise technical documents. Organize your thoughts before speaking.
7. **Failures are learning opportunities**: Use "what did we learn" and "how did we improve" in every behavioral answer.
8. **Know the Well-Architected Framework**: Reliability Pillar, Operational Excellence Pillar, Security Pillar, Cost Optimization, Performance Efficiency.
9. **Practice AWS CLI commands**: Many interview scenarios involve fixing issues using AWS CLI or CloudFormation.
10. **BAR RAISER round**: This is often the most challenging. Have 5-6 well-prepared STAR stories that demonstrate different LPs.
