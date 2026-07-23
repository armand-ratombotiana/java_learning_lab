# Interview Cheatsheet: Disaster Recovery Failover

## Key Diagnostic Commands
- `dig <service>.example.com` — DNS failover check
- `curl -I https://<health-endpoint>` — health endpoint check
- Cloud provider: Route53 health checks, Azure Traffic Manager
- `openssl s_client -connect <host>:443` — TLS up?
- `ping` and `traceroute` — network connectivity
- Multi-region metrics dashboard

## Common Metrics to Check
- Health check pass/fail rate per region
- DNS resolution latency and TTL
- Active vs. passive region traffic distribution
- Data replication lag (RDS, DynamoDB Global Tables)
- Failed request routing count
- Time to detect region failure
- Time to failover

## Typical Root Causes
- Region-wide cloud provider outage
- DNS propagation delay during failover
- Health check misconfiguration (false positives)
- Data replication not caught up (RPO breach)
- Configuration drift between primary and DR
- Load balancer/health check threshold too high
- Cold start performance (canary deployment not warmed)

## Interview Question Patterns
- "Design a multi-region disaster recovery strategy"
- "How do you test a disaster recovery plan?"
- "What's the difference between RTO and RPO?"
- "Design a system that auto-failovers between regions"

## STAR Story Template
**S**: AWS US-EAST-1 had a multi-hour partial outage
**T**: Failover to DR region and maintain availability
**A**: Activated Route53 failover policy, verified data replication was current, DNS propagated in 2 min
**R**: Service restored in DR region within 5 min, no data loss
