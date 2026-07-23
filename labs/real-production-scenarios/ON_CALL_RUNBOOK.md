# On-Call Runbook — Comprehensive On-Call Guide

## What to Do When Paged

### Step 0: Before You Go On-Call

- [ ] Verify your monitoring tools work (PagerDuty, Grafana, Datadog, Splunk)
- [ ] Test alert routing — do you receive test alerts?
- [ ] Review recent deployments and known issues
- [ ] Read all open incident tickets
- [ ] Verify SSH access to production systems
- [ ] Check that runbooks are up-to-date
- [ ] Confirm you have stakeholder contact list
- [ ] Set up your on-call workstation (laptop charged, dock, second monitor)
- [ ] Brief your backup / secondary on-call

### Step 1: Acknowledge the Alert

- **Goal:** Within 2 minutes of page
- **Action:** Acknowledge in PagerDuty / OpsGenie / your alerting tool
- **Check:** Is this a real incident or a false alarm?
- **If duplicate/known issue:** check if someone else is already handling it

### Step 2: Triage — Is This Real?

Quick assessment checklist:

| Signal | Real Incident | False Alarm |
|--------|---------------|-------------|
| Alert from multiple sources | ✅ Likely real | ❌ Unlikely |
| Users are complaining | ✅ Real | ❌ |
| Metrics show anomaly across 2+ sources | ✅ Real | ❌ |
| Single metric spike, no user impact | ❌ Investigate low | ✅ May be alert noise |
| Alert fires during maintenance | ❌ May be expected | ✅ |
| Alert fires right after deployment | ✅ Correlation = real | ❌ |
| Grafana dashboard confirms unusual patterns | ✅ Real | ❌ |

### Step 3: Classify Severity

See severity classification below. If SEV1/P0, immediately start the incident response process.

### Step 4: Declare Incident

- **SEV1/P0:** Immediately assemble incident response team, declare in your incident management tool
- **SEV2/P1:** Investigate, page additional engineers if needed
- **SEV3/P2:** Investigate during business hours, create ticket

### Step 5: Mitigate Before You Debug

**Immediate mitigation options (do first, ask questions later):**
- Rollback recent deployment
- Disable feature flag
- Scale up / scale out
- Traffic shift to healthy region
- Restart service (last resort)
- Kill specific slow queries
- Clear cache
- Degrade non-critical features

### Step 6: Debug with System

**Use the OSI model approach — narrow down layer by layer:**

| Layer | What to Check | Commands/Tools |
|-------|---------------|----------------|
| Application | Error logs, thread dumps, heap dumps | `journalctl`, `tail -f app.log`, `jstack` |
| Framework | Connection pools, thread pools, GC logs | HikariCP metrics, GC logs, JMX |
| Database | Slow queries, locks, connections, replication lag | `SHOW PROCESSLIST`, `pg_stat_activity`, AWR |
| Cache | Hit/miss ratio, eviction rate, latency | Redis `INFO`, Memcached stats |
| Network | Latency, packet loss, DNS resolution, TLS certs | `ping`, `traceroute`, `openssl s_client` |
| OS | CPU, memory, disk I/O, file descriptors | `top`, `iostat`, `df`, `ulimit` |
| Hardware | Disk failure, NIC failure, power | Cloud provider console, IPMI |

### Step 7: Communicate

Send status updates every 30 minutes (or as appropriate for severity):
- What is the impact? (who, what, how bad)
- What is the current state? (investigating, mitigating, recovered)
- What is the next action?
- When is the next update expected?

### Step 8: Resolve and Verify

- Confirm customer impact has ended
- Verify key metrics return to baseline
- Keep monitoring for 30+ minutes post-resolution
- Send final status update

### Step 9: Document

- Ensure incident timeline is recorded (scribe notes)
- Create ticket for post-mortem
- File follow-up tickets for any remaining work
- Update runbooks with lessons learned

### Step 10: Handoff

If your shift ends:
- Brief the incoming on-call on active incidents
- Document any ongoing investigation
- Transfer incident command if incident is still active
- Note any open tickets and their status

---

## Severity Classification

### Standard Severity Matrix

| Severity | Definition | Response Time | Update Frequency | Examples |
|----------|-----------|---------------|-----------------|----------|
| **SEV1 / P0** | Critical — complete service outage or data loss affecting all users | < 5 minutes | Every 30 minutes | Site down, data corruption, security breach |
| **SEV2 / P1** | Major — partial outage, significant degradation for subset of users | < 15 minutes | Every 60 minutes | Payment failures, slow performance, feature unavailability |
| **SEV3 / P2** | Minor — limited impact, workaround available | < 60 minutes | Daily | UI bug, non-critical feature broken, cosmetic issue |
| **SEV4 / P3** | Trivial — minimal impact, no user-facing effect | < 1 business day | Per ticket update | Internal tool issue, documentation error, cosmetic bug |
| **SEV5** | Informational — no user impact, potential issue | No response needed | N/A | Warning alerts, capacity nearing limits |

### Google SRE Severity Categories

| Level | Definition | Response |
|-------|-----------|----------|
| **P0** | Service is down or severely degraded for all users | Full incident response |
| **P1** | Service is partially down or degraded for a subset of users | Debug immediately |
| **P2** | Minor issue with acceptable workaround | Debug during business hours |
| **P3** | Cosmetic or informational | Track as regular bug |

### Amazon AWS Severity Levels

AWS Support severity definitions:

| Severity | Definition | Initial Response Time |
|----------|-----------|---------------------|
| **Critical** | Core production system is down | 15 minutes |
| **Urgent** | Production system severely degraded | 1 hour |
| **High** | Non-production system down, production has workaround | 4 hours |
| **Normal** | General guidance or questions | 12 hours |
| **Low** | Feature requests, documentation | 24 hours |

### Microsoft Azure Severity

| Severity | Definition | Response Time |
|----------|-----------|---------------|
| **A** | Production environment severely impacted, business stopped | < 1 hour |
| **B** | Production environment moderately impacted, business impaired | < 2 hours |
| **C** | Production environment minimally impacted, workaround exists | < 4 hours |

### Example Severity Classifications by Scenario

| Scenario | Severity | Why |
|----------|----------|-----|
| Payment gateway returning 500 errors on all transactions | SEV1 | Revenue loss, all users affected |
| Login page loads 3s slower than normal | SEV2 | Users affected but can still log in |
| Admin dashboard shows incorrect chart labels | SEV4 | No user impact, internal tool |
| Database connection pool at 80% utilization | SEV3/SEV2 | Capacity warning, not yet impacting users |
| Security breach — customer data accessed | SEV1 | Data compromise, legal/regulatory implications |
| Mobile app crashes on startup (iOS 17) | SEV1/SEV2 | Depends on % of users affected |
| CDN certificate expired, static assets not loading | SEV1 | Site appears broken to all users |

---

## Escalation Paths

### Standard Escalation Chain

```
On-Call Engineer (Tier 1)
    ↕ (if unresolved after 15 min)
Service Team Lead (Tier 2)
    ↕ (if SEV1 or unresolved after 30 min)
Engineering Manager / Director (Tier 3)
    ↕ (if major incident or cross-team)
VP of Engineering / CTO (Tier 4 — communication only)
```

### When to Escalate

| Condition | Escalate To |
|-----------|------------|
| Cannot identify root cause within 15 minutes | Tier 2 |
| Need expertise in a specific domain (DB, network, security) | Subject matter expert |
| Incident duration exceeds 1 hour | Tier 3 + Stakeholder management |
| Customer-facing impact > 30 minutes | Communications lead + Product manager |
| Security / data breach | Security team + Legal |
| Regulatory / compliance impact | Compliance officer + Legal |
| Need to involve vendors (cloud provider, SaaS) | Vendor support + Account manager |

### Escalation Communication Template

```
Subject: ESCALATION: [Severity] - [Service] - [Brief Description]

To: [Escalation Contact]

We are escalating this incident because:
- [Reason 1: root cause not identified]
- [Reason 2: need specialized expertise]
- [Reason 3: incident duration exceeded threshold]

Current Status:
- Impact: [what is affected, how many users]
- Mitigation: [what steps taken so far]
- Next Actions: [what we're doing now]

Please join the incident bridge:
- Bridge: [link]
- Slack: [#incident-channel]
- Incident ID: [INC-12345]
```

---

## Communication Templates

### Status Update Template

```
INCIDENT UPDATE #[NUMBER]

Status: [Investigating | Mitigating | Monitoring | Resolved]
Severity: [SEV1/SEV2/SEV3]
Date/Time: [UTC timestamp]

What is happening:
[1-2 sentence description of the issue and current state]

Impact:
- Services affected: [list]
- Users affected: [estimated count or percentage]
- Geographic regions: [list]

What we're doing:
- [Action item 1]
- [Action item 2]
- [Action item 3]

Next update: [time or "No further updates expected"]

Incident Commander: [Name]
Channel: [#incident-channel]
```

### Post-Incident Summary Template

```
POST-INCIDENT SUMMARY

Incident ID: INC-YYYY-NNN
Date: YYYY-MM-DD
Duration: [HH:MM]
Severity: [SEV1/SEV2]
Services affected: [list]

Summary:
[2-3 paragraph narrative of the incident]

Impact:
- Total downtime: [minutes]
- Users affected: [count or percentage]
- Error rate during incident: [%]
- Data loss: [Yes/No, details if yes]

Root Cause:
[1-2 sentences]

Timeline:
- [Time] - [Event]
- [Time] - [Event]
- [Time] - [Event]

Action Items:
| # | Action Item | Owner | Due Date | Status |
|---|------------|-------|----------|--------|
| 1 | | | | |
| 2 | | | | |
| 3 | | | | |

Lessons Learned:
[What went well, what could be improved]
```

### Incident Bridge Opening Script

"Hello everyone, this is [Name], I'm the Incident Commander for [Incident ID]. The current severity is [SEV1]. Here's what we know: [brief summary]. I'm assigning [Name] as Operations Lead, [Name] as Communications Lead, and [Name] as Scribe. Operations Lead, please start investigating [specific symptom]. Communications Lead, please send the initial status update to [stakeholder group]. Scribe, please start tracking the timeline. Everyone, please use #incident-channel for all communication. Any questions? Let's go."

---

## Tools Checklist

### Monitoring Tools

| Category | Tools | What to Check |
|----------|-------|---------------|
| Infrastructure | Prometheus, Datadog, Grafana, CloudWatch, Azure Monitor | CPU, memory, disk, network |
| Application | New Relic, AppDynamics, Dynatrace, OpenTelemetry | Request rate, latency, error rate |
| Logs | Splunk, ELK Stack (Elasticsearch/Logstash/Kibana), Loki | Error logs, exceptions, access logs |
| Tracing | Jaeger, Zipkin, AWS X-Ray, Datadog APM | Distributed traces, span latencies |
| Uptime | Pingdom, StatusCake, Checkly | External health checks |
| Synthetic | Catchpoint, ThousandEyes, mPulse | User experience simulation |
| Real User | RUM (Real User Monitoring), Google Analytics | Actual user experiences |
| Security | WAF logs, IDS/IPS, SIEM (Splunk ES, Sentinel) | Attack patterns, anomalies |

### Diagnostic Tools

| Tool | Use Case | Command |
|------|----------|---------|
| `top` / `htop` | CPU and memory per process | `top -H -p <pid>` |
| `iostat` | Disk I/O performance | `iostat -x 1` |
| `vmstat` | System memory, paging, CPU | `vmstat 1` |
| `netstat` / `ss` | Network connections | `ss -tuln` |
| `tcpdump` | Packet capture | `tcpdump -i eth0 port 80` |
| `curl` | HTTP endpoint testing | `curl -v https://service.example.com` |
| `dig` / `nslookup` | DNS resolution | `dig service.example.com` |
| `openssl` | TLS/SSL certificate check | `openssl s_client -connect host:443` |
| `jstack` | Java thread dump | `jstack <pid>` |
| `jcmd` | JVM diagnostics | `jcmd <pid> help` |
| `jstat` | JVM GC statistics | `jstat -gcutil <pid> 5s` |
| `df -h` | Disk space | `df -h` |
| `du -sh` | Directory size | `du -sh /var/log` |
| `journalctl` | System logs | `journalctl -u service-name -n 100` |

### Cloud-Specific Tools

| Cloud | Tools |
|-------|-------|
| AWS | CloudWatch, CloudTrail, AWS Config, Systems Manager, Trusted Advisor |
| Azure | Azure Monitor, Log Analytics, Application Insights, Azure Advisor |
| GCP | Cloud Monitoring, Cloud Logging, Cloud Trace, Cloud Profiler |

### Communication Tools

| Tool | Use |
|------|-----|
| Slack / Teams | Incident channel, status updates |
| Zoom / Google Meet / Teams | Incident bridge |
| PagerDuty / OpsGenie | On-call scheduling, alert routing |
| Jira / ServiceNow | Incident tracking |
| Confluence / Notion | Runbooks, documentation |
| Statuspage | External status page |

### On-Call Engineer's Home Lab Checklist

- Laptop with full charge + power adapter
- VPN client configured and tested
- SSH keys loaded and tested
- Monitoring tools open (Grafana, logs, tracing)
- Incident bridge number/link bookmarked
- Team contact list accessible
- Secondary communication method (phone) available
- Runbooks accessible offline (in case of VPN outage)

---

## Common Runbook Patterns

### Pattern 1: Service Unreachable

```
1. Confirm — can you reach the service from your machine?
   ├── Yes → check if specific users/regions
   └── No → continue
2. Check DNS resolution: `dig service.example.com`
3. Check load balancer health: cloud console → target group health
4. Check instance/container health: cloud console → instance state
5. Check service process: `systemctl status service-name`
6. Check application logs: `journalctl -u service-name -n 100 --no-pager`
7. Check resource utilization: CPU, memory, disk
8. If not resolved → restart service → if still down → escalate
```

### Pattern 2: Elevated Error Rates

```
1. Confirm error rate spike: check dashboard
2. Is error rate correlated with deployment? → roll back
3. Check error logs: what type of errors? (500, 403, 404, timeout?)
4. Check error distribution: all endpoints or specific?
5. Check upstream dependencies: database, cache, downstream APIs
6. Check recent configuration changes
7. Check traffic patterns: seasonal, DDoS, news event
8. Check resource limits: connection pools, thread pools, file descriptors
```

### Pattern 3: High Latency

```
1. Confirm latency spike: P50, P95, P99 — which percentile?
2. Is latency correlated with request type, endpoint, region?
3. Check thread pool utilization — are threads queuing?
4. Check database query performance — slow queries, locks
5. Check cache hit ratio — is cache warming?
6. Check GC logs — are there long GC pauses?
7. Check downstream service latency — are they slow?
8. Check network latency between services
9. Check for resource contention — CPU, memory, disk I/O
```

### Pattern 4: Database Issues

```
1. Check database connectivity
2. Check connection count vs. max_connections
3. Check for long-running queries: `SHOW FULL PROCESSLIST`
4. Check for locks: `SHOW OPEN TABLES`, `SELECT * FROM information_schema.innodb_locks`
5. Check replication lag
6. Check slow query log
7. Check disk space on database server
8. Check database error log
9. Check for plan regression (execution plan changed)
```

---

## How to Write Effective Runbooks

### Runbook Structure

```
# [Runbook Title]

## Overview
- Service/component this runbook covers
- Symptoms that trigger this runbook
- Severity level

## Prerequisites
- Required access/permissions
- Required tools
- Required credentials (and where to find them securely)

## Diagnosis Steps
1. Step-by-step commands with expected output
2. Decision trees for branching paths
3. Links to dashboards, logs, traces

## Mitigation Steps
1. Step-by-step actions to mitigate
2. Commands with exact syntax
3. Rollback procedure
4. Verification steps

## Resolution Steps
1. Permanent fix procedure
2. Verification procedure
3. Monitoring confirmation

## Post-Resolution
- Cleanup steps
- Documentation updates needed
- Who to notify

## Contact
- Service team
- Subject matter experts
- Vendor support contacts

## History
- Date | Author | Change
- YYYY-MM-DD | Name | Initial creation
```

### Runbook Quality Checklist

- [ ] Every command has exactly the right syntax (no placeholders)
- [ ] Expected output is shown for each command
- [ ] Decision trees cover all common paths
- [ ] Runbook is tested at least quarterly
- [ ] Runbook is accessible even if VPN is down
- [ ] Contact information is current
- [ ] All links are valid
- [ ] Single person can follow it end-to-end

---

## SLO/SLI/SLA Definitions and Management

### Service Level Indicator (SLI)

**Definition:** A quantifiable measure of a service's quality.

**Common SLIs:**

| Category | SLI | Measurement |
|----------|-----|-------------|
| Availability | Fraction of valid requests that succeed | `successful_requests / total_requests` |
| Latency | Fraction of requests that complete within a threshold | `requests < 200ms / total_requests` |
| Throughput | Number of requests processed per second | `total_requests / time_period` |
| Durability | Fraction of data that persists without loss | `data_not_lost / total_data` |
| Freshness | Age of data presented to user | `current_time - data_timestamp` |
| Correctness | Fraction of requests returning correct results | `correct_responses / total_responses` |

### Service Level Objective (SLO)

**Definition:** A target value or range for an SLI, representing the desired reliability.

**Example SLOs:**

| Service Type | SLI | SLO |
|-------------|-----|-----|
| Critical API | Availability | 99.99% (4.38 minutes downtime/year) |
| Web service | Availability | 99.9% (8.76 hours downtime/year) |
| Internal tool | Availability | 99% (3.65 days downtime/year) |
| Search API | Latency (P99 < 200ms) | 95% of requests < 200ms |
| Database | Availability | 99.95% |
| Batch job | Completion within window | 99.5% on-time completion |

**Error Budget:** 100% - SLO = error budget

| SLO | Error Budget (per year) | Error Budget (per month) |
|-----|----------------------|------------------------|
| 99.99% | 52.56 minutes | ~4.38 minutes |
| 99.9% | 8.76 hours | ~43.8 minutes |
| 99.5% | 43.8 hours | ~3.65 hours |
| 99% | 87.6 hours | ~7.3 hours |
| 95% | 438 hours | ~36.5 hours |

### Service Level Agreement (SLA)

**Definition:** A contractual commitment to meet SLOs, with penalties for non-compliance.

**Key SLA Concepts:**

| Concept | Description |
|---------|-------------|
| Service Credits | Financial penalty when SLA is breached (typically 10-30% of monthly fee) |
| SLA Exclusions | Events not counted against SLA (scheduled maintenance, force majeure, customer actions) |
| Measurement Period | Time window over which SLA is measured (monthly, quarterly, annually) |
| Reporting | How SLA compliance is measured and reported (provider or third party) |

**AWS SLA Examples:**

| Service | SLA |
|---------|-----|
| EC2 | 99.99% monthly uptime for multi-AZ deployments |
| RDS | 99.95% monthly uptime for multi-AZ |
| S3 | 99.9% monthly uptime (99.99% durability) |
| DynamoDB | 99.99% monthly uptime |
| Lambda | 99.95% monthly uptime |

### Burn Rate Alerting

**Burn Rate:** How fast the error budget is being consumed.

| Burn Rate | Meaning | Action |
|-----------|---------|--------|
| < 1x | Within budget | No action |
| 1x - 2x | Using budget faster than planned | Review, minor concern |
| 2x - 5x | Accelerated consumption | Alert on-call, investigate |
| 5x - 10x | Rapid consumption | Page on-call, potential SEV2 |
| > 10x | Very rapid consumption | Page on-call, declare incident |

### Multi-Window Multi-Burn-Rate Alerting

Google SRE's recommended alerting strategy uses two windows:

| Window | Burn Rate | SLO | Example (99.9% SLO) |
|--------|-----------|-----|-------------------|
| Short (1 hour) | 14.4x | Page if error rate > 14.4x SLO target | Alert if > 1.44% errors in 1 hour |
| Long (6 hours) | 6x | Page if error rate > 6x SLO target | Alert if > 0.6% errors in 6 hours |

This ensures:
- Fast response to severe error rate spikes
- Slower response to gradual degradation
- No false positives from short transient spikes
- Coverage for both acute and chronic issues

---

## Incident Management Frameworks

### Google SRE Framework

**Five Stages of Incident Response:**

1. **Detection:** Monitoring, alerting, or user reports
2. **Triage:** Determine severity, impact, and response needed
3. **Mitigation:** Stop the bleeding — rollback, feature flag, traffic shift
4. **Resolution:** Permanent fix deployed and verified
5. **Post-Mortem:** Blameless analysis, action items, improvements

**Key Principles:**
- Error budgets drive engineering priorities
- SLOs are the north star for reliability
- Blameless post-mortems improve culture and systems
- Automation reduces toil and human error
- Measure everything that matters

### ITIL Framework

**ITIL Incident Management Process:**

1. **Incident Logging:** Record all relevant information
2. **Categorization:** Assign category (hardware, software, network, security)
3. **Prioritization:** Based on impact and urgency
4. **Initial Diagnosis:** Tier 1 support attempts resolution
5. **Escalation:** Functional or hierarchical escalation if needed
6. **Investigation and Diagnosis:** Tier 2/3 engineers
7. **Resolution and Recovery:** Apply fix and verify
8. **Incident Closure:** Confirm user satisfaction, document

**ITIL Terminology:**

| Term | Definition |
|------|-----------|
| Incident | An unplanned interruption or reduction in quality |
| Problem | Root cause of one or more incidents |
| Known Error | A problem with a documented workaround |
| Workaround | A temporary fix that reduces or eliminates impact |
| Service Request | A standard change with low risk |
| Change | Any addition, modification, or removal of service |

### AWS Incident Management

**AWS Best Practices:**
- Use AWS Systems Manager for runbook automation
- CloudWatch for monitoring and alerting
- AWS Config for configuration change tracking
- CloudTrail for API activity logging
- AWS Shield for DDoS protection
- AWS WAF for web application firewall

**AWS Incident Response Steps:**
1. Detect using CloudWatch Alarms / GuardDuty
2. Respond using AWS Systems Manager Automation
3. Investigate using CloudTrail, Config, VPC Flow Logs
4. Recover using backups, snapshots, multi-AZ failover
5. Analyze using AWS Compute Optimizer, Trusted Advisor
6. Improve using Well-Architected reviews

### Comparison of Frameworks

| Aspect | Google SRE | ITIL | AWS |
|--------|-----------|------|-----|
| Focus | Reliability through engineering | Service management | Cloud operations |
| Key Metric | Error budget burn rate | MTTR, MTBF | Well-Architected pillars |
| Incident Model | Incident Commander | Service Desk | Incident response plan |
| Post-Incident | Blameless post-mortem | Problem management | Root cause analysis |
| Automation Priority | High (reduce toil) | Medium | High (Systems Manager) |
| Monitoring Philosophy | Four golden signals | Event management | CloudWatch + X-Ray |
| Scalability | Designed for Google-scale | Designed for enterprise IT | Designed for cloud |

---

## References

- Google SRE Book: https://sre.google/sre-book/table-of-contents/
- Google SRE Workbook: https://sre.google/workbook/table-of-contents/
- AWS Well-Architected Framework: https://aws.amazon.com/architecture/well-architected/
- ITIL 4 Foundation: https://www.axelos.com/certifications/itil-service-management/
- Understanding Error Budgets: https://sre.google/workbook/alerting-on-slos/
- Incident Management Process: https://www.atlassian.com/incident-management
- PagerDuty Incident Response: https://response.pagerduty.com/
- O'Reilly: "Incident Management for Operations"
