# Lab 01 — Java Memory Leak: Disaster Recovery Plan

## Service: Zuul API Gateway

### Recovery Objectives

| Metric | Target | Measurement |
|--------|--------|-------------|
| **RTO** (Recovery Time Objective) | 5 minutes | Time from incident declaration to traffic restored |
| **RPO** (Recovery Point Objective) | 0 minutes | No data loss for in-flight requests (stateless gateway) |
| **MTD** (Maximum Tolerable Downtime) | 15 minutes | Before cascading failures affect dependent services |

### Disaster Scenarios

#### Scenario A: Metaspace OOM (Single Instance)

| Parameter | Value |
|-----------|-------|
| Trigger | JVM OOM: Metaspace error in instance |
| Impact | Single instance goes down, ~16.7% of cluster capacity lost |
| RTO | 2 minutes |
| Recovery Action | Auto-scaling group replaces instance automatically |

**Recovery Steps:**
1. ASG detects unhealthy instance via ELB health checks
2. ASG terminates unhealthy instance
3. ASG launches new instance (pulls latest AMI)
4. New instance registers with ELB (2-3 min warmup)
5. Traffic resumes to new instance

**Verification:**
- ELB health check shows instance as healthy
- Gateway metrics (request count, error rate) return to normal
- Metaspace utilization < 40% on new instance

#### Scenario B: Metaspace OOM (Cluster-wide)

| Parameter | Value |
|-----------|-------|
| Trigger | All instances in cluster reach OOM at similar time |
| Impact | Complete loss of API gateway in region |
| RTO | 5 minutes |
| Recovery Action | Failover to secondary region |

**Recovery Steps:**
1. Declare regional incident
2. Route53 DNS update: shift traffic to us-west-2 cluster
3. us-west-2 cluster scales up to handle additional load
4. Monitor us-west-2 metrics for capacity pressure
5. us-east-1 cluster restarts with fix deployed
6. After verification, shift traffic back to us-east-1

**Rollback Plan:**
- If us-west-2 cannot handle load, shift traffic back to us-east-1 (even with degraded performance)
- If both regions affected, route to static error page with status information

**Verification:**
- Traffic flowing to us-west-2 cluster
- Error rate < 1% in us-west-2
- P99 latency < 500ms in us-west-2
- No cascading failures in dependent services

#### Scenario C: Memory Leak — Slow Metaspace Growth

| Parameter | Value |
|-----------|-------|
| Trigger | Metaspace utilization > 80% with growth trend |
| Impact | Unstable — will lead to OOM within projected timeframe |
| RTO | N/A (mitigation before impact) |
| Recovery Action | Proactive restart + deploy fix |

**Recovery Steps:**
1. Alert fires: Metaspace > 70%, growth rate > 50MB/hour
2. On-call evaluates: is this a new deployment? Config change?
3. If yes to either → rollback deployment/configuration
4. If no to both → proactive rolling restart:
   - Drain connections from instance 1
   - Restart JVM on instance 1
   - Verify Metaspace reset on instance 1
   - Repeat for remaining instances (rolling)
5. Investigate root cause in parallel
6. Deploy permanent fix (ThreadLocal cleanup)
7. Monitor for 24 hours after fix

**Verification:**
- Metaspace < 40% on all instances
- Metaspace growth rate < 1 MB/hour flat
- ClassLoader count not increasing
- Class unloading events present in GC logs

### Backup and Restore Procedures

#### Gateway Configuration

| Item | Backup Method | Frequency | Retention | Restore Time |
|------|--------------|-----------|-----------|-------------|
| Filter chain configuration | Git repository | Every commit | Forever | 1 minute |
| Route definitions | Git repository | Every commit | Forever | 1 minute |
| Rate limiting config | Git repository | Every commit | Forever | 1 minute |
| SSL/TLS certificates | Secrets vault | On update | Per certificate | 2 minutes |
| Instance AMI | Automated AMI builder | Weekly | 30 days | 5 minutes |

**Restore Procedure for Configuration:**
1. Check out last known-good commit from Git
2. Deploy via CI/CD pipeline (Spinnaker)
3. Verify configuration applied to canary instance
4. Apply to remaining instances via rolling deployment
5. Verify routes and filters working on all instances

#### JVM Diagnostic Data

| Item | Backup | Location | Retention |
|------|--------|----------|-----------|
| Heap dumps | S3/GCS bucket | /data/dumps/ | 30 days |
| JFR recordings | S3/GCS bucket | /data/jfr/ | 30 days |
| GC logs | Centralized logging | /var/log/gc/ | 90 days |
| Thread dumps | S3/GCS bucket | /data/threads/ | 14 days |
| Crash logs (hs_err) | S3/GCS bucket | /data/crash/ | 90 days |

### Failover and Failback Procedures

#### Cross-Region Failover

**Failover to Secondary (us-west-2):**

1. Verify us-west-2 cluster has capacity for 2x load
2. Update Route53 health check to mark us-east-1 as unhealthy
3. Wait for DNS propagation (TTL: 60 seconds)
4. Scale up us-west-2 cluster (+50% instances)
5. Monitor error rate and latency in us-west-2
6. Notify stakeholders of region change

**Failback to Primary (us-east-1):**

1. Verify us-east-1 cluster is healthy (all instances up, Metaspace < 40%)
2. Update Route53 to route traffic back to us-east-1
3. Scale down us-west-2 cluster (remove extra capacity)
4. Monitor both regions for 30 minutes
5. Confirm primary region stable

### Data Integrity Verification

After recovery from OOM event:

| Check | Method | Expected Result |
|-------|--------|----------------|
| Request data integrity | Verify idempotency keys against downstream logs | No duplicate or lost requests |
| Session consistency | Check session store (Redis) vs. gateway logs | No corrupted sessions |
| Rate limit counters | Verify rate limit counters in Redis are consistent | No over- or under-counted limits |
| Circuit breaker state | Check Hystrix/Resilience4j state | Correct open/closed states |
| Configuration | Verify all routes and filters are loaded | All configured routes active |

**Verification Commands:**

```bash
# Check gateway health endpoint
curl -s https://gateway.example.com/health | jq '.status'

# Verify all routes loaded
curl -s https://gateway.example.com/admin/routes | jq '. | length'

# Check Metaspace utilization
jcmd <pid> VM.metaspace | grep "Used"

# Verify circuit breaker states
curl -s https://gateway.example.com/actuator/health | jq '.components.circuitBreakers'
```

### Testing Schedule

| Test | Frequency | Type | Success Criteria |
|------|-----------|------|-----------------|
| Health check verification | Every 30 seconds (automated) | Passive | All instances healthy in ELB |
| Metaspace trend analysis | Every hour (automated) | Passive | Growth rate < 1 MB/hour |
| Cross-region failover | Monthly | Active drill | RTO < 5 minutes, zero data loss |
| Instance replacement | Weekly (ASG rolling update) | Active | New instance serves traffic within 3 minutes |
| Load test (Metaspace) | Quarterly | Active | No Metaspace growth over 12 hours at 2x peak load |
| ThreadLocal audit | Quarterly | Code review | No unchecked ThreadLocal usage |
| Chaos test (OOM simulation) | Quarterly | Active drill | Graceful degradation, no cascading failures |
| Post-mortem review | Monthly | Review | All action items completed or in progress |

### Runbook Quick Reference

```yaml
incident_type: "OOM_Metaspace"
symptoms:
  - "JVM crashes with java.lang.OutOfMemoryError: Metaspace"
  - "ELB health check failures"
  - "Error rate spikes"
  - "Instance count drops in ASG"

immediate_actions:
  - "Acknowledge page"
  - "Check cluster status in AWS console"
  - "Verify auto-scaling is replacing instances"
  - "If multiple instances affected, declare SEV1"

diagnosis_steps:
  - "Check hs_err_pid files: grep 'OutOfMemoryError' /var/log/*.log"
  - "Check GC logs: search for 'Metaspace' and class unloading counts"
  - "Capture thread dump: jcmd <pid> Thread.print"
  - "Check Metaspace usage: jcmd <pid> VM.metaspace"
  - "Check ClassLoader count: jcmd <pid> VM.classloader_stats"

mitigation_options:
  - "Option A: Proactive restart (if Metaspace > 70%)"
  - "Option B: Failover to secondary region (if cluster-wide)"
  - "Option C: Rollback recent deployment (if correlated)"
  - "Option D: Scale up cluster (temporary capacity)"

permanent_fix:
  - "Add ThreadLocal.remove() in finally block"
  - "Replace ThreadLocal with WeakThreadLocal wrapper"
  - "Add Metaspace growth monitoring"
  - "Add static analysis rule for ThreadLocal cleanup"

verification:
  - "Metaspace stable at < 40% for 24 hours"
  - "ClassLoader count not increasing"
  - "No OOM errors in logs"
  - "Error rate at baseline (< 0.1%)"
```
