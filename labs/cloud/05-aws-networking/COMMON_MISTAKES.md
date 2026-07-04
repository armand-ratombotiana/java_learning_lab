# Common Mistakes — AWS Networking

## VPC Mistakes

### 1. Overlapping CIDR for Peering
**Mistake**: VPC A (10.0.0.0/16) with VPC B (10.0.0.0/16) — identical CIDR.
**Issue**: Can't establish VPC peering (overlapping ranges).
**Fix**: Plan CIDR allocation centrally. Prod: 10.0.0.0/16, Dev: 10.1.0.0/16, Shared: 10.2.0.0/16.

### 2. One NAT Gateway for All AZs
**Mistake**: Single NAT Gateway in AZ-a. AZ-b private subnets route through it.
**Issue**: AZ-a outage → all private subnets lose internet (cross-AZ NAT not HA).
**Fix**: One NAT Gateway per AZ (or use Transit Gateway + shared NAT).

### 3. NACL Rules Too Restrictive
**Mistake**: Only allowing specific inbound ports, forgetting ephemeral ports.
**Issue**: Return traffic blocked; connections hang.
**Fix**: NACL must allow ephemeral ports (1024-65535) for inbound and outbound.

### 4. Security Group Limit Exceeded
**Mistake**: 60+ rules in a single security group.
**Issue**: Can't add more rules; AWS limit hard cap.
**Fix**: Use multiple SGs per instance (max 5).

## Route 53 Mistakes

### 1. TTL Too Long During Migration
**Mistake**: TTL = 86400 (24 hours) before DNS change.
**Issue**: After cutover, users hit old IP for 24 hours.
**Fix**: Lower TTL to 60-300 seconds before planned change; restore after stable.

### 2. Not Testing Health Checks
**Mistake**: Failover routing configured without health checks or with bad health check.
**Issue**: Traffic doesn't failover when primary is down.
**Fix**: Test health check endpoint; use CloudWatch alarm-based health checks.

### 3. Alias Records vs CNAME Confusion
**Mistake**: Using CNAME for zone apex (e.g., example.com → ALB).
**Issue**: DNS spec prohibits CNAME at apex.
**Fix**: Use Route 53 Alias record (free, works at apex, no charge for queries).

## CloudFront Mistakes

### 1. Not Invalidating Cache After Updates
**Mistake**: Updating origin content but CloudFront serves old cached version.
**Fix**: CloudFront invalidation: `aws cloudfront create-invalidation --distribution-id xxx --paths "/*"`

### 2. No Origin Shield for High-Traffic Origins
**Mistake**: 1000s of edge locations all hit origin directly.
**Issue**: Origin overloaded even though cache hit rate is 90%+.
**Fix**: Enable Origin Shield (regional caching layer).

## ALB/ELB Mistakes

### 1. Cross-Zone Load Balancing Disabled
**Mistake**: Default off for NLB (on for ALB).
**Issue**: Traffic imbalances if targets are uneven across AZs.
**Fix**: Enable cross-zone load balancing.

### 2. Deregistration Delay Too Short
**Mistake**: Default 300 seconds too short for long-running requests.
**Issue**: In-flight requests fail during deployment.
**Fix**: Increase deregistration delay to match max request duration.
