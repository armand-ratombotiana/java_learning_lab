# Root Cause Analysis — Disk Space Capacity Incident

## Incident: INC-2026-0801-009

**Analysis Method**: 5 Whys  
**Analyst**: James Wilson, SRE Lead  
**Date**: August 2, 2026  

---

## Executive Summary

A 132-minute SEV1 incident caused by disk space exhaustion on 20 production servers. Three concurrent issues — log rotation misconfiguration, temp file leak, and unbounded database table growth — combined to consume 100% of available disk space. The root cause extends into capacity planning, monitoring thresholds, and operational maintenance practices.

---

## 5 Whys Analysis

### Why 1: Why did production servers run out of disk space?

**Answer**: Because three independent disk consumption mechanisms converged to fill all available space:
1. **Log Files**: Application v3.1 introduced verbose DEBUG logging, increasing log volume from 2GB/day to 15GB/day. Logrotate was configured with `rotate 5` (keep 5 rotated files), meaning each server retained up to 75GB of logs (37.5% of 200GB).
2. **Temp Files**: A bug in the report generation module caused temporary PDF files to accumulate in `{app-home}/temp` at 2.5GB/hour. The tmpwatch cron job only cleaned `/tmp`, not `{app-home}/temp`.
3. **Database Audit Table**: The `audit_log` table had grown to 340GB over 10 months with no partition management or retention policy.

On database servers (500GB), the audit table consumed 340GB (68%), logs 75GB (15%), and system overhead 40GB (8%), leaving only 45GB free — which was consumed by temp files and unexpected growth. On application servers (200GB), logs consumed 75GB (37.5%), temp files 30GB (15%), and system 20GB (10%), with 75GB remaining — but the temp file accumulation outpaced projections.

**Evidence**:
- `df -h` output: `/dev/sda1 500G 500G 0G 100%` (database server 01)
- `du -sh /var/log/app/`: 75GB per server
- `du -sh /opt/app/temp/`: 30GB per application server
- `SELECT pg_size_pretty(pg_total_relation_size('audit_log'))`: 340GB

### Why 2: Why were logrotate, temp file cleanup, and database retention all misconfigured simultaneously?

**Answer**: Because:

1. **Logrotate**: The configuration was set up during initial deployment 18 months ago when log volume was 2GB/day. When v3.1 increased logging to 15GB/day, no one updated the logrotate configuration. The `rotate 5` value was set arbitrarily without consideration of disk capacity or retention requirements.

2. **Temp files**: The application wrote temp files to `{app-home}/temp` (a standard pattern), but:
   - The tmpwatch cron job was configured for `/tmp` only — `{app-home}/temp` was not included
   - The report generation code had a bug: `finally` block did not delete temp files after download
   - No developer tested temp file cleanup behavior

3. **Database audit table**: The original schema design included `audit_log` with no partition key. As the table grew, it became increasingly difficult to partition (table lock concerns). The team deferred partition work multiple times. No retention policy was ever defined for audit data.

**Evidence**:
- `/etc/logrotate.d/app.conf`: `rotate 5` — unchanged since initial deployment (18 months ago)
- `/etc/cron.d/tmpwatch`: `/usr/sbin/tmpwatch 6 /tmp` — no mention of `/opt/app/temp`
- Sprint backlog: "Implement audit_log partitioning" — created 8 months ago, never started
- Code review: Report generation PR #3152 — `finally` block missing file.delete()

### Why 3: Why did monitoring not catch this before servers hit 100%?

**Answer**: Because the disk monitoring alerts were configured at thresholds that were too high:
- Warning alert: 85% (not 80%)
- Critical alert: 95% (not 90%)
- No staged escalation: the time between warning and critical was too short
- No inode monitoring existed
- No trend analysis or capacity forecasting was implemented — alerts were threshold-based only

Additionally, the warning alert at 85% was acknowledged but not escalated because the on-call engineer checked and saw disk was at 85% but not growing rapidly — not realizing that batch processing would start at 06:00 and push it over the edge.

**Evidence**:
- Prometheus alert rules: `disk_utilization > 85` (warning), `disk_utilization > 95` (critical)
- PagerDuty log: Warning alert at 08:30 acknowledged by on-call engineer at 08:35, no escalation
- No capacity trend dashboard existed — only current utilization
- `df -i` inode checks: not part of any monitoring configuration

### Why 4: Why was capacity planning not done for the v3.1 release?

**Answer**: Because the organization did not have a formal capacity planning process. Key missing practices:
1. No pre-release capacity review — the v3.1 release did not require capacity impact assessment
2. No log volume testing — the performance test suite measured response times and throughput but not log volume
3. No disk growth projections — the team had no process for forecasting disk usage growth
4. No resource budgeting — each release did not have a "resource budget" (expected increase in CPU, memory, disk, network)

**Evidence**:
- Release checklist for v3.1: 15 items covering functionality, security, performance — zero items covering disk, log, or storage impact
- No capacity planning document exists in the team wiki
- Performance test results: focused entirely on response times and throughput — no storage metrics
- Quarterly planning: no capacity review item in the agenda

### Why 5 (Root Cause): Why did the organization lack capacity planning and operational maintenance practices?

**Answer**: Because the organization had grown rapidly without investing in infrastructure engineering practices. Specific organizational gaps:

1. **Feature-First Culture**: Engineering velocity was prioritized over operational excellence. Releases shipped quickly without operational readiness reviews.

2. **No SRE Practice**: Despite having an "SRE team," the organization practiced DevOps (developers also handle operations) rather than SRE (dedicated reliability engineering with error budgets, capacity planning, and operational reviews).

3. **Rotating Neglect**: Logrotate configuration, tmpwatch setup, database partition management — all were "set up once and forget" tasks. No recurring maintenance review process existed.

4. **Monitoring Immaturity**: Monitoring was added reactively (after incidents) rather than proactively. The team had no capacity forecasting, trend analysis, or predictive alerting.

5. **Acknowledged Technical Debt**: The audit_log partitioning was known technical debt for 8 months. The sprint backlog had it deferred every sprint in favor of feature work. No mechanism existed to enforce operational work alongside feature work.

**Evidence**:
- Team structure: 15 developers, 3 SRE engineers — SRE team spent 70% of time on feature work
- Retrospective notes: "We need to invest in operational maintenance" — from 4 retrospectives ago, no action taken
- JIRA board: 80% of tickets labeled "feature", 5% labeled "tech debt", 15% labeled "bug"
- Manager interviews: "We knew audit_log was a problem but we needed to ship the reporting feature first"

---

## Causal Chain

```
No capacity planning for v3.1 release
    ↓
Log volume increases 7.5x (2GB→15GB/day)
Report generation bug (no file cleanup)
Audit_log grows unbounded (340GB)
    ↓
logrotate rotate=5 insufficient
tmpwatch not covering app-home/temp
No partition management for audit_log
    ↓
Disk consumption accelerates
    ↓
Monitoring thresholds too high (85%/95%)
No trend analysis or forecasting
    ↓
Warning alerts acknowledged but not escalated
    ↓
Batch job pushes disk to 100%
    ↓
20 servers full → 43% transaction failure rate
```

---

## Root Cause Statement

**The root cause of this incident is organizational: the absence of capacity planning processes, operational maintenance review, and proactive monitoring practices, combined with three specific technical failures (logrotate misconfiguration, temp file leak, unbounded database table growth), caused production disk exhaustion across 20 servers.**

### Contributing Factors

| Factor | Type | Impact |
|--------|------|--------|
| Logrotate `rotate=5` insufficient | Technical | Critical |
| Temp file bug (never deleted) | Technical | Critical |
| Audit_log unbounded (no partition) | Technical | Critical |
| No capacity planning process | Process | High |
| Monitoring thresholds too high | Process | High |
| No trend analysis / forecasting | Process | High |
| Operational tech debt deferred 8 months | Process | Medium |
| No inode monitoring | Technical | Medium |

### References
- Google SRE Book — Chapter 12: "Managing Disk Space"
- Google SRE Book — Chapter 6: "Monitoring Distributed Systems" (Four Golden Signals)
- Netflix Tech Blog: "Linux Performance Tools" — https://netflixtechblog.com/linux-performance-tools-7c9d2e6b8f2c
- PostgreSQL Documentation: "Table Partitioning" — https://www.postgresql.org/docs/15/ddl-partitioning.html
- Atlassian Engineering: "How We Fixed Disk Space Issues at Scale"

---

## Expanded Technical Root Cause Analysis

### Detailed Logrotate Failure Analysis

**Logrotate Configuration History**:
The logrotate configuration was created during initial deployment 18 months prior. At that time:
- Application log volume: 500MB/day per server
- Disk capacity: 200GB per server
- rotate 5 was deemed sufficient (5 × 500MB + 500MB active = 3GB max)
- No compression was needed (3GB was acceptable)

Over 18 months, the application evolved:
- New features added more logging
- The monitoring system was updated (more health check logging)
- Third-party library upgrades increased framework logging
- Log volume grew from 500MB/day to 2GB/day

At 2GB/day, rotate 5 meant:
- Active log: 2GB
- Rotated logs: 5 × 2GB = 10GB
- Total: 12GB (acceptable)

Then v3.1 introduced DEBUG-level logging:
- Log volume jumped to 15GB/day
- rotate 5 meant: 5 × 15GB + 15GB = 90GB (45% of disk)
- This level of consumption was catastrophic

**Why Logrotate Was Not Updated**:
1. The v3.1 release checklist did not include a logrotate review step
2. No automated monitoring alerted on log directory size as a percentage of disk
3. The developer who enabled debug logging did not know about logrotate
4. The SRE team was not informed about the log volume increase
5. No capacity review was triggered by the deployment

### Detailed Temp File Leak Analysis

**Root Cause in Application Code**:
The temp file leak was caused by a missing `finally` block in the report generation code:

```java
// Before fix — Temp file not cleaned up
public ResponseEntity<Resource> downloadReport(String type, String criteria) {
    File tempFile = reportService.generateReport(type, criteria);
    Resource resource = new FileSystemResource(tempFile);
    return ResponseEntity.ok()
        .header(CONTENT_DISPOSITION, "attachment; filename=\"" + tempFile.getName() + "\"")
        .body(resource);
    // tempFile is never deleted after response is sent
}

// After fix — Temp file cleaned up in finally block
public ResponseEntity<Resource> downloadReport(String type, String criteria) {
    File tempFile = null;
    try {
        tempFile = reportService.generateReport(type, criteria);
        Resource resource = new FileSystemResource(tempFile);
        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "attachment; filename=\"" + tempFile.getName() + "\"")
            .body(resource);
    } finally {
        if (tempFile != null) {
            tempFile.delete();
        }
    }
}
```

The bug was introduced by a junior developer who followed a tutorial that did not include cleanup logic. The code reviewer focused on the business logic (report generation) and missed the resource leak.

**Why tmpwatch Did Not Catch It**:
The tmpwatch cron job was configured as:
```
0 */6 * * * root /usr/sbin/tmpwatch 6 /tmp
```

This cleaned files in `/tmp` that were not accessed in 6 hours. The application wrote to `/opt/app/temp` instead of `/tmp`. The tmpwatch job did not cover this path.

**Why No One Noticed**:
- Temp files were cleaned up during development (developers manually clean their environments)
- The application was deployed to production on a Friday
- By Monday (48 hours later), the temp directory had accumulated 60GB
- Monday morning was when the disk hit 100%

### Detailed Database Audit Table Analysis

**Audit Table Growth History**:

| Month | Table Size | Cumulative | Notes |
|-------|-----------|------------|-------|
| Oct 2025 | 30GB | 30GB | Application launch |
| Nov 2025 | 35GB | 65GB | Traffic growth |
| Dec 2025 | 38GB | 103GB | Holiday traffic spike |
| Jan 2026 | 32GB | 135GB | Post-holiday normalization |
| Feb 2026 | 36GB | 171GB | Normal growth |
| Mar 2026 | 40GB | 211GB | Feature launch increased audit events |
| Apr 2026 | 38GB | 249GB | Normal growth |
| May 2026 | 42GB | 291GB | Traffic increase |
| Jun 2026 | 35GB | 326GB | Normal growth |
| Jul 2026 | 14GB | 340GB | Month not complete when incident occurred |

**Partitioning Design Decision**:
The original schema designer chose not to partition the audit_log table because:
- "It's just an audit table — it won't grow that fast" (incorrect projection)
- Partitioning adds complexity to the schema
- PostgreSQL partitioning was relatively new (v10+) and the team was unfamiliar
- The table was expected to have 6-month retention from day one, but the retention enforcement was never implemented

### Expanded Causal Chain Analysis

**Technical Causal Chain**:
```
v3.1 Release (2 weeks before incident)
    ↓
Debug logging + Report generation bug + Unbounded audit table
    ↓
Log volume: 2GB→15GB/day
Temp files: 0→30GB accumulation
Audit table: 340GB (10 months accumulation)
    ↓
Batch job at 06:00 → 30GB temp files per server
Traffic at 08:00 → 15GB logs per server
    ↓
80% disk alert at 08:30 (not escalated)
90% disk alert at 10:00 (paged)
100% disk at 10:30 (SEV1 declared)
```

**Organizational Causal Chain**:
```
No capacity planning process
    ↓
No pre-release capacity review
    ↓
Debug logging not reviewed for log volume impact
Report gen bug not caught in code review
Audit table partition work deferred 8 months
    ↓
Monitoring thresholds too high (85%/95%)
No trend-based forecasting
    ↓
Warning alerts at 80% not escalated
    ↓
Disk hits 100% → SEV1 incident
```

### Comparison with Google SRE Practices

**Google SRE Disk Management**:
Google's SRE teams follow these disk management practices:
1. **Disk quotas**: Each service has a disk quota and must stay within it
2. **Capacity planning**: Quarterly capacity reviews with 6-month projections
3. **Automated cleanup**: Standardized cleanup scripts run on all servers
4. **Unified monitoring**: All disk metrics are collected centrally, not per-server
5. **Predictive alerting**: Alerts fire when disk is projected to hit 100%, not when it happens

**Gap Analysis vs Google SRE**:

| Practice | Google SRE | Our Org | Gap |
|----------|-----------|---------|-----|
| Disk quotas | Per-service quotas | No quotas | Complete gap |
| Capacity planning | Quarterly, 6-month projections | None | Complete gap |
| Automated cleanup | Standard scripts | ad-hoc cron jobs | Partial |
| Unified monitoring | Centralized | Per-server | Complete gap |
| Predictive alerting | Trend-based | Threshold-only | Complete gap |

**Specific Google SRE Techniques We Implemented After Incident**:
1. **Disk usage trends**: Grafana panels showing 30-day disk usage trends with 7-day and 30-day projections
2. **Predictive alert**: `predict_linear(disk_utilization[7d], 86400) > 0.90` — alerts when disk predicted to exceed 90% within 24 hours
3. **Standard cleanup scripts**: Automated emergency cleanup script that can be triggered centrally
4. **Unified monitoring dashboard**: Single Grafana dashboard showing all server disk utilization
5. **Monthly capacity review**: First Monday of each month, 30-minute capacity review meeting
