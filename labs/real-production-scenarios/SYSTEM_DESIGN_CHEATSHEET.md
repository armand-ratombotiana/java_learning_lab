# System Design Cheatsheet for SRE / Production Engineering

## How to Use This Cheatsheet

Each section covers a system design domain critical for SRE interviews. Templates at the bottom provide reusable response structures for common SRE system design questions ("Design an incident response system", "Design monitoring for 10K microservices"). Cross-reference each domain with the relevant incident-response labs.

---

## Section 1: Monitoring & Alerting Design

### Prometheus + Grafana + Alertmanager

**Architecture:**
```
Service Metrics → Prometheus (pull) → Alertmanager → PagerDuty/Slack/Email
                                       → Grafana Dashboards
                                       → Thanos/Cortex (long-term storage)
```

**Metric Design Principles:**

| Principle | Explanation | Example |
|-----------|-------------|---------|
| Cardinality bound | Limit label combinations | `http_requests_total{method,path,status}` — path as label is high-cardinality |
| Rate before absolute | Always convert counters to rates | `rate(http_requests_total[5m])` not raw counter |
| RED over USE | Microservices: Rate, Errors, Duration | Infrastructure: USE (Utilization, Saturation, Errors) |
| Multi-window alerts | Short + long windows reduce false positives | 1h at 14.4x burn rate + 6h at 6x burn rate |

**SLO-Based Alerting (from Google SRE Workbook):**

```promql
# Burn rate alert: error rate > 14.4x SLO target over 1 hour
# SLO target: 99.9% availability → 0.1% max error rate
# 14.4x → 1.44% error rate
sum(rate(http_requests_total{status=~"5.."}[1h])) /
sum(rate(http_requests_total[1h])) > 0.0144
```

**Lab Connection:** See each lab's `SLI_SLO_METRICS.md` for specific metric designs. Lab 03 (high CPU) teaches CPU saturation metrics. Lab 07 (circuit breaker) teaches error rate monitoring.

### Multi-Dimensional Monitoring

| Dimension | Why | Example |
|-----------|-----|---------|
| Region | Geographic fault isolation | us-east-1 vs eu-west-1 latency |
| Service | Microservice ownership | Checkout vs Cart vs Payment |
| Version | Deployment monitoring | v2.3.1 vs v2.4.0-canary |
| Tenant | Multi-tenant isolation | Enterprise vs Free tier |
| Instance | Per-host debugging | ip-10-0-1-42 vs ip-10-0-1-43 |

---

## Section 2: Incident Response System Design

### On-Call Scheduling

**Design considerations:**
- **Follow-the-sun:** 3 regions × 8h shifts (Americas, EMEA, APAC)
- **Primary + Secondary:** Primary handles pages; secondary is backup
- **Rotation frequency:** 1 week on-call → 3 weeks off (Google SRE standard)
- **Escalation policy:** Engineer → Team Lead → Manager → Director

```yaml
# PagerDuty-style schedule:
schedule:
  - name: primary-sre
    rotation: weekly
    handoff_day: Monday
    handoff_time: "09:00 UTC"
    escalation_delay: 15 minutes
  - name: secondary-sre
    rotation: weekly
    escalation_delay: 15 minutes
```

### Escalation Policies

| Level | Escalation Time | Responder | Action |
|-------|----------------|-----------|--------|
| L1 | Immediate | Primary On-Call | Investigate, mitigate |
| L2 | 15 min | Secondary On-Call | Assist, escalate if SEV1 |
| L3 | 30 min | Service Team Lead | Cross-team coordination |
| L4 | 60 min | Engineering Manager | Stakeholder communication |
| L5 | 120 min | Director/VP | Executive communication |

### Incident Management Workflow

```
Detection (Alert / User Report)
  ↓
Triage (Severity Classification)
  ↓
Incident Commander Assigned
  ↓
Ops Lead → Diagnosis → Mitigation (rollback, feature flag)
Comms Lead → Status Updates (internal + external)
Scribe → Timeline Logging
  ↓
Resolution (customer impact ends)
  ↓
Post-Mortem (within 72h)
  ↓
Action Items → Tracked → Closed
```

**Lab Connection:** Lab 06 (deployment rollback) is a classic incident response scenario. Lab 10 (security breach) requires additional legal/comms escalation path.

### War Room Setup

- **Primary channel:** Slack/Discord `#inc-{id}`
- **Bridge:** Zoom/Meet with recording
- **Dashboard:** Grafana playback of incident timeline
- **Document:** Collaborative timeline document (Google Docs/Coda)
- **Status page:** Update external status page (e.g., Statuspage.io)
- **Tools:** PagerDuty, Jira, Splunk, Datadog, Slack

---

## Section 3: Chaos Engineering

### Fault Injection Design

| Fault Type | Implementation | Blast Radius Control |
|------------|---------------|---------------------|
| Pod kill | Chaos Mesh PodChaos | Namespace + label selector |
| Network latency | Toxiproxy / tc | Port + latency range |
| Packet loss | iptables / tc | Percentage of traffic |
| CPU stress | stress-ng | Core count + duration |
| Disk fill | dd / fallocate | Directory + fill percentage |
| TLS cert expiry | Manual cert rotation | Single host first |
| Region failover | Traffic manager | 1% user traffic initially |

### Blast Radius Control Hierarchy

```
1. Test environment (isolated)
2. Canary instance (1 pod, 1 host)
3. Single AZ subset (1% of traffic)
4. Single region non-critical
5. Production canary (opt-in users)
6. Full production (gameday, scheduled)
```

### Automated Recovery Testing

```yaml
# GameDay scenario template
name: "disk-fill-experiment"
schedule: "0 10 * * 1"  # every Monday 10 AM
steps:
  - inject: disk_fill
    target: payment-service-canary
    duration: 5m
    fill_percent: 95
  - verify:
      - alert_triggered: "disk-space-critical" within 2m
      - log_rotation_activated: true
      - service_stays_up: p99_latency < 500ms
  - rollback:
      - clear_disk_fill
      - verify_cleanup
  - report:
      - steady_state_deviations
      - action_items
```

**Lab Connection:** Lab 07 (circuit breaker) shows what happens without chaos engineering. Lab 08 (cache stampede) shows cache failure patterns. Lab 09 (disk space) is directly testable via chaos experiments.

---

## Section 4: Disaster Recovery Design

### RTO / RPO Calculation

| Strategy | RTO | RPO | Cost | Use Case |
|----------|-----|-----|------|----------|
| Backup & Restore | Hours | 24h | Low | Non-critical data |
| Pilot Light | Minutes | Minutes | Medium | Core services |
| Warm Standby | Minutes | Seconds | High | Critical services |
| Multi-Region Active-Active | Seconds | Seconds | Very High | Global services |

**RTO Formula:** `RTO = detection_time + decision_time + recovery_time + verification_time`
**RPO Formula:** `RPO = data_loss_window = last_backup_time - failure_time`

### Failover Strategies

| Strategy | How It Works | Example |
|----------|-------------|---------|
| Active-Passive | Primary handles traffic; standby is idle. DNS/CNAME switch on failure. | Traditional RDBMS |
| Active-Active | Both regions handle traffic. DNS load balancing distributes. | Web tier, stateless |
| Active-Active with local writes | Each region writes locally; async replication. Stale reads possible. | Social feeds |
| Cold Standby | No running resources. Recover from snapshots/backups. | Dev/test environments |

### Backup Verification

```yaml
backup_policy:
  frequency: daily
  retention:
    daily: 30 days
    weekly: 12 weeks
    monthly: 12 months
  verification:
    - restore_test: weekly (automated)
    - integrity_check: daily (checksums)
    - recovery_drill: quarterly (full DR test)
```

**Lab Connection:** Lab 15 (disaster recovery failover) is the capstone DR lab covering active-passive failover, DNS propagation, and cross-region replication.

### Cross-Region Replication

| System | Replication Method | Lag | Consistency |
|--------|-------------------|-----|-------------|
| MySQL | Async binlog replication | Sub-second to seconds | Eventually consistent |
| PostgreSQL | Streaming replication | Usually sub-second | Synchronous option |
| DynamoDB Global Tables | Multi-master | Sub-second | Eventually consistent |
| Kafka MirrorMaker | Topic replication | Seconds to minutes | Asynchronous |
| S3 CRR | Object replication | Minutes to hours | Eventually consistent |

---

## Section 5: Capacity Planning

### Traffic Modeling

```
Daily peak throughput = daily_requests / (86400 × peak_to_avg_ratio)
Example: 100M req/day / (86400 × 0.2) = ~5,787 req/s peak
```

**Estimation factors:**
- Read/Write ratio (typically 90/10 for web apps)
- Peak to average ratio (2-10x depending on traffic pattern)
- Growth rate (weekly/monthly/quarterly)
- Seasonal spikes (Black Friday, New Year, product launches)

### Resource Estimation

| Resource | Estimation Formula | Example |
|----------|-------------------|---------|
| CPU cores | `req/s × cpu_time_per_req / cores_per_host` | 5000 rps × 50ms / 4 cores = ~63 hosts |
| Memory | `concurrent_users × memory_per_session` | 10K users × 50MB = 500GB |
| Disk I/O | `write_rate × iops_per_write` | 1000 writes/s × 2 IOPS = 2000 IOPS |
| Network | `req/s × bytes_per_response` | 5000 × 100KB = 500 MB/s ~ 4 Gbps |
| Connections | `peak_req_per_sec × avg_latency_seconds` | 5000 × 0.2 = 1000 concurrent connections |

### Auto-Scaling Strategies

| Strategy | Metrics | Pros | Cons |
|----------|---------|------|------|
| CPU-based | CPU utilization > 70% | Simple | Slow, doesn't capture all bottlenecks |
| Request-based | Request queue depth | Fast | Can scale down too early |
| Custom metric | gRPC inflight requests | Accurate | Complex to set up |
| Predictive | ML-based traffic forecast | Proactive | Requires traffic history |
| Scheduled | Known peak times | Zero latency | Brittle to unexpected spikes |

### Cost Optimization

| Technique | Savings | Example |
|-----------|---------|---------|
| Reserved instances | 30-60% | 1yr/3yr commit for baseline capacity |
| Spot/preemptible | 60-90% | Batch processing, stateless workers |
| Right-sizing | 10-30% | Match instance type to resource usage |
| Auto-scaling | 20-40% | Scale down during low traffic |
| Storage tiering | 40-60% | Move cold data to cheaper storage |

**Lab Connection:** Lab 09 (disk space capacity incident) teaches capacity planning mistakes. Lab 04 (connection pool exhaustion) shows what happens when you don't size pools.

---

## Section 6: Release Engineering

### Blue-Green Deployment

```
Traffic → Router → Blue (current)
               → Green (new) — 0% traffic initially
               → After validation: swap 100% → Green
               → Blue = rollback target
```

| Aspect | Configuration |
|--------|--------------|
| Database | Both environments share DB (backward-compatible schema) |
| Rollback | DNS/cname flip back to Blue |
| Validation | Smoke tests, monitoring window (30 min) |
| Tooling | Kubernetes namespaces, AWS ECS services, Spinnaker |

### Canary Releases

```
1% (5 min) → 10% (10 min) → 25% (30 min) → 50% (1h) → 100%
```

**Canary analysis (automated gating):**
```yaml
canary:
  analysis:
    - metric: error_rate
      threshold: +0.5% vs baseline
      window: 5m
    - metric: p99_latency
      threshold: +20% vs baseline
      window: 5m
    - metric: cpu_utilization
      threshold: +15% vs baseline
      window: 5m
  rollback:
    condition: any_metric_breached
    action: route_traffic_to_baseline
```

### Feature Flags

```
if (featureFlags.isEnabled("new-checkout-flow", userId)) {
    // new flow
} else {
    // legacy flow
}
```

**Types:**
- **Release toggles:** Control feature rollout
- **Experiment toggles:** A/B testing
- **Ops toggles:** Kill switches for degradation
- **Permission toggles:** Beta/Early access programs

### Rollback Automation

```yaml
rollback_automation:
  triggers:
    - error_rate_breach: +1% for 2 minutes
    - p99_latency_breach: +100ms for 5 minutes
    - availability_drop: < 99.9% for 1 minute
  actions:
    - rollout_previous_version
    - disable_feature_flags
    - notify_pagerduty
    - send_slack_alert
    - log_incident
```

**Lab Connection:** Lab 06 (deployment rollback) provides a real-world scenario of failed deployment with rollback. Lab 07 (circuit breaker) shows feature flag patterns.

---

## Section 7: Observability Stack

### Logging (ELK / Loki)

| Component | ELK | Loki |
|-----------|-----|------|
| Storage | Elasticsearch (indexed) | Compressed chunks in object store |
| Query | Kibana (Lucene) | LogQL (label + filter) |
| Strengths | Full-text search, complex aggregations | Cost-effective, K8s-native |
| Weaknesses | Expensive at scale, high cardinality issues | Limited full-text search |

**Log volume reduction strategies:**
1. **Sampling:** Log 1% of debug events, 100% of errors
2. **Structured logging:** JSON format, searchable fields
3. **Log levels:** Adjust verbosity per service in production
4. **Aggregation:** Summarize high-frequency events (e.g., "rate_limit_exceeded: 1423 times in 5m")

### Metrics (Prometheus)

**Prometheus architecture:**
```
Service:9100/metrics → Prometheus Server → TSDB (local)
                  ↘ Alertmanager → PagerDuty
                  ↘ Grafana → Dashboards
                  → Thanos/Cortex (long-term + global view)
```

**Key metric types:**
- **Counter:** Only increases (request count, errors)
- **Gauge:** Goes up and down (CPU, memory, queue depth)
- **Histogram:** Bucketed latency distribution (P50, P95, P99)
- **Summary:** Quantile estimation with sliding window

### Tracing (Jaeger / OpenTelemetry)

```yaml
tracing_config:
  sampler:
    type: probabilistic
    rate: 0.01  # 1% sampling — cost vs visibility tradeoff
  backend: jaeger
  storage: elasticsearch
  ui_queries:
    - "Find all traces with error status"
    - "Find slowest 1% of traces by service"
    - "Trace waterfall for request-id X"
```

### Profiling (pprof / async-profiler)

| Profiler | Language | Use Case |
|----------|----------|----------|
| async-profiler | Java | CPU + allocation flame graphs |
| pprof | Go | CPU, heap, goroutine, mutex |
| perf | Linux | Kernel-level profiling |
| py-spy | Python | Sampling profiler for Python |

**Lab Connection:** Every lab's `MONITORING.md` covers the observability for that specific incident type. Lab 03 (high CPU) teaches CPU profiling. Lab 01 (memory leak) teaches heap analysis.

---

## Section 8: Security Operations

### Incident Response for Security

```
Detection (GuardDuty, SIEM alert, user report)
  ↓
Triage (Is it a real security threat?)
  ↓
Contain (Isolate affected systems, preserve evidence)
  ↓
Eradicate (Remove threat, patch vulnerability)
  ↓
Recover (Restore from clean backup, verify integrity)
  ↓
Post-Incident (Root cause, regulatory reporting, improvements)
```

### Threat Detection

| Detection Type | Tools | Example |
|---------------|-------|---------|
| Anomaly-based | Statistical models | Unusual outbound traffic volume |
| Signature-based | WAF rules, IDS | SQL injection pattern match |
| Behavior-based | UEBA | User accessing unusual resources |
| Rule-based | SIEM correlations | Multiple failed logins + success |

### Vulnerability Management

```yaml
vulnerability_management:
  scanning:
    frequency: daily (critical), weekly (high), monthly (medium)
    types: [SAST, DAST, dependency, container, infrastructure]
  remediation_slas:
    critical: 24 hours
    high: 7 days
    medium: 30 days
    low: 90 days
```

### Compliance Automation

| Compliance | Automation |
|------------|-----------|
| SOC 2 | Automated evidence collection, continuous monitoring |
| PCI DSS | Network segmentation, encryption, access controls |
| HIPAA | Audit logging, access reviews, BAA verification |
| GDPR | Data classification, consent management, DSR automation |
| ISO 27001 | Policy as code, automated controls, quarterly reviews |

**Lab Connection:** Lab 10 (security incident response) covers the full security IR lifecycle from detection to post-incident analysis.

---

## Section 9: Infrastructure Design

### Kubernetes Architecture

```
Control Plane:
  API Server → etcd
  Scheduler
  Controller Manager
Worker Nodes:
  kubelet → containerd
  kube-proxy → iptables/ipvs
  CNI (Calico, Cilium) → Pod networking
```

**Key SRE considerations:**
- **Pod Disruption Budgets:** Ensure minimum available pods during voluntary disruptions
- **Vertical Pod Autoscaler:** Adjust resource requests based on usage
- **Cluster Autoscaler:** Scale node pools based on unschedulable pods
- **Priority Classes:** Preemptible pods can be evicted for critical workloads

### Service Mesh (Istio)

```
Service A → Sidecar (Envoy) → Service B (Envoy)
            mTLS encryption
            Circuit breaking
            Traffic splitting
            Observability
```

**Service mesh value for SRE:**
- **Traffic management:** Canary, blue-green without code changes
- **Security:** mTLS between all services, authorization policies
- **Observability:** Automatic metrics, traces for all traffic
- **Resilience:** Circuit breakers, retries, timeouts configurable centrally

### CNI / CSI / Ingress/Egress

| Component | Purpose | Examples |
|-----------|---------|----------|
| CNI | Pod networking | Calico, Cilium, Flannel, Weave |
| CSI | Storage volume management | EBS CSI, Azure Disk CSI, CSI Hostpath |
| Ingress | North-south traffic | nginx-ingress, Traefik, HAProxy |
| Egress | External outbound traffic | Egress gateway, network policies |

**Lab Connection:** Lab 12 (Kubernetes crashloop) teaches pod health, probes, and resource limits. Lab 07 (circuit breaker) teaches service mesh resilience patterns.

---

## Section 10: Design for Distributed Systems

### Failure Mode Design

| Component | Failure Mode | Mitigation |
|-----------|-------------|------------|
| Database | Connection pool exhaustion | Pool sizing, connection timeout, circuit breaker (Lab 04, 07) |
| Cache | Stampede on miss | Cache jitter, request coalescing, stale-while-revalidate (Lab 08) |
| Queue | Consumer lag | Partition count, consumer parallelism, alert on lag (Lab 13) |
| API | Rate limit breach | Token bucket, sliding window, throttling response (Lab 14) |
| Disk | Space exhaustion | Log rotation, alert at 80/90%, capacity planning (Lab 09) |
| TLS | Certificate expiry | Auto-renewal, monitoring 30/14/7 days before expiry (Lab 11) |

### Data Consistency Patterns

| Pattern | Description | Use Case |
|---------|-------------|----------|
| Strong consistency | All reads see latest write | Financial transactions |
| Eventual consistency | Reads eventually see latest | User profiles, social feeds |
| Read-your-writes | User sees own writes immediately | User settings |
| Monotonic reads | Never see older version | Session data |
| Bounded staleness | Staleness guaranteed within time | Analytics |

---

## Interview Response Templates

### Template 1: "Design an Incident Response System for a Global E-Commerce Platform"

**Framework:**
1. **Requirements:**
   - Support 10M+ users globally (5 regions)
   - 99.99% uptime SLO
   - < 5 min MTTA, < 30 min MTTR for SEV1
   - Follow-the-sun on-call with 15 min escalation

2. **On-Call Architecture:**
   ```yaml
   regions: [US-EAST, EU-WEST, AP-SOUTHEAST]
   rotations:
     - primary: 1 week on, 3 weeks off
     - secondary: same schedule, offset
     - escalation: 15 → 30 → 60 → 120 min
   ```

3. **Alerting:**
   - Multi-window multi-burn-rate SLO alerts
   - Synthetic monitoring (external health checks)
   - Anomaly detection (deviation from baseline)

4. **Incident Workflow:**
   - Detection → PagerDuty → Acknowledge (2 min)
   - Triage → Severity classification
   - War room → IC, Ops Lead, Comms Lead, Scribe
   - Mitigation → Rollback/feature flag/scale up
   - Resolution → Verify metrics
   - Post-mortem → Within 72 hours

5. **Tools:**
   - Monitoring: Prometheus + Grafana
   - Alerting: Alertmanager → PagerDuty
   - Communication: Slack + Zoom
   - Tracking: Jira/ServiceNow
   - Status page: Statuspage.io

6. **Testing:**
   - Monthly tabletop exercises
   - Quarterly gamedays
   - Automated chaos experiments

**Lab Connection:** This design directly uses patterns from Lab 04 (alerts), Lab 06 (rollback), Lab 10 (security escalation), and Lab 15 (DR failover).

### Template 2: "Design a Monitoring System for 10K Microservices"

**Framework:**
1. **Requirements:**
   - 10,000+ services with varying criticality
   - 1M+ time series metrics
   - 10TB+ logs per day
   - 100K+ traces per second

2. **Metrics Pipeline:**
   ```
   Service (expose /metrics) → Prometheus (sharded by service team)
     → Thanos (global view + long-term retention)
     → Grafana (dashboards for each team)
     → Alertmanager (team-specific alerts)
   ```

3. **Log Pipeline:**
   ```
   Service (stdout JSON) → Fluentd (DaemonSet) → Kafka → Loki → Grafana
     ↩ S3 archive after 30 days
   ```

4. **Trace Pipeline:**
   ```
   Service → OpenTelemetry Agent → Jaeger Collector → Cassandra/Elasticsearch
     → Sampling: 1% probability (head-based)
     → Tail-based sampling for error traces
   ```

5. **Alerting Philosophy:**
   - SLO-based with error budget burn rate
   - Per-service SLOs tied to service tiers (critical = 99.99%, standard = 99.9%)
   - Alert fatigue prevention: multi-window, grouping, silencing during maintenance
   - Auto-remediation runbooks for known patterns

6. **Dashboard Design:**
   - Service-level dashboard (RED metrics)
   - Infrastructure dashboard (USE metrics)
   - Business dashboard (SLO compliance, error budget)
   - Team dashboard (incident count, MTTR, toil)

7. **Cost Optimization:**
   - Metrics: Reduce cardinality, aggregate less-important dimensions
   - Logs: Sample debug logs, keep errors 100%, archive after 30 days
   - Traces: Adaptive sampling based on traffic volume

**Lab Connection:** Lab 03 (monitoring CPU), Lab 04 (connection pool metrics), Lab 08 (cache hit ratio monitoring), and Lab 13 (consumer lag monitoring) all inform this design.

### Template 3: "Design a Disaster Recovery Strategy for a Payment Platform"

**Framework:**
1. **Requirements:**
   - RTO: 15 minutes
   - RPO: < 1 second (no data loss)
   - Multi-region (US-East, US-West, EU-West)
   - Payment provider integrations (no single point of failure)

2. **Architecture:**
   - Active-Active across 3 regions
   - Global load balancer (Route53 + CloudFront)
   - Local writes + async cross-region replication
   - Idempotency keys for payment processing

3. **Failover process:**
   - Health checks on service, DB, queue in each region
   - Automated failover when health checks fail in 2/3 regions
   - DNS TTL reduction before planned failover (30s instead of 300s)
   - Canary failover (1% traffic to standby before cutover)

4. **Data replication:**
   - Database: Synchronous commit within region, async cross-region
   - Queue messages: Replicate to secondary region
   - Cache: Regional, rewarm on failover
   - File storage: Cross-region replication with versioning

5. **Testing:**
   - Monthly DR drills (region failover, load test)
   - Gameday: Simulate region outage, measure RTO/RPO
   - Chaos experiments: Network partition, DNS failure, DB failover

**Lab Connection:** Lab 15 (DR failover) provides hands-on practice with active-passive failover. Lab 07 (circuit breaker) shows what happens without proper failover. Lab 05 (database recovery) addresses data consistency.

### Template 4: "Design a Capacity Planning System for a Growing Platform"

**Framework:**
1. **Data collection:** Historical traffic, growth rate, seasonal patterns
2. **Modeling:** Linear regression for steady growth + seasonal adjustments
3. **Sizing:** CPU, memory, disk, network per user/request
4. **Buffer:** 2x growth headroom, 20% emergency buffer
5. **Automation:** Auto-scaling for short-term, provisioning pipeline for long-term
6. **Review:** Weekly capacity review, quarterly planning

---

## System Design Study Roadmap by Lab

| Lab | System Design Topic |
|-----|-------------------|
| 01 | Memory management, heap sizing, GC tuning |
| 02 | Concurrency design, lock-free data structures |
| 03 | CPU profiling, auto-scaling, resource sizing |
| 04 | Connection pool design, thread pool sizing |
| 05 | Database failover, read replicas, query optimization |
| 06 | CI/CD pipeline design, release automation |
| 07 | Circuit breaker patterns, bulkhead design, resilience |
| 08 | Cache hierarchy design, distributed caching |
| 09 | Capacity planning, disk management, log rotation |
| 10 | Security architecture, WAF, SIEM, incident response |
| 11 | PKI design, certificate management, mTLS |
| 12 | K8s architecture, pod lifecycle, service mesh |
| 13 | Kafka architecture, consumer group design, streaming |
| 14 | API gateway design, rate limiting strategies |
| 15 | DR architecture, multi-region design, RTO/RPO |

---

## Quick Reference: SRE System Design Evaluation Criteria

| Criterion | What Interviewers Evaluate |
|-----------|---------------------------|
| Requirements clarity | Did you ask about scale, SLO, constraints before designing? |
| Tradeoff awareness | Do you acknowledge pros/cons of each design choice? |
| Failure modes | Do you discuss what breaks and how it recovers? |
| Operationalization | Can your design be operated by an on-call engineer? |
| Monitoring | How will you know if this system is healthy? |
| Capacity | How will this system scale? What's the bottleneck? |
| Security | Where are the attack surfaces? How do you protect data? |
| Cost | What's the cost implication of design decisions? |
