# Incident Report — Disk Space Capacity Incident

## Incident ID: INC-2026-0801-009

**Date**: August 1, 2026  
**Reported By**: SRE Team (James Wilson)  
**Status**: Resolved  
**Severity**: SEV1 / P0  

---

## Timeline (All Times UTC)

### Pre-Incident Conditions

| Time | Event |
|------|-------|
| 2026-07-15 | Application v3.1 deployed — includes verbose debug logging for new reporting feature |
| 2026-07-15 | Log volume increases from 2GB/day to 15GB/day per server |
| 2026-07-20 | Temp file cleanup bug introduced in report generation module — files not deleted after download |
| 2026-07-20 | Temp files begin accumulating at 2.5GB/hour during business hours |
| 2026-07-28 | Database audit_log table reaches 340GB (10 months of data, 1.2GB/day growth) |
| 2026-07-31 | Disk utilization reaches 70% on busiest database server |

### Incident Onset

| Time | Event |
|------|-------|
| 06:00 | Batch report generation runs — creates 30GB of temp files across 12 app servers |
| 07:00 | Production traffic begins — verbose logging generates 15GB/day per server |
| 08:30 | First application server reaches 80% disk — Prometheus alert fires (warning) |
| 09:00 | 3 app servers at >85% disk — alerts acknowledged but not escalated |
| 09:30 | 6 app servers at >90% disk — PagerDuty warning alert fires |
| 10:00 | Database server 01 reaches 85% disk — alerts firing, SRE investigating |
| 10:15 | Database server 01 reaches 92% — PagerDuty high-priority alert |
| 10:30 | Database server 01 at 97%, database servers 02-04 at >90% |
| 10:32 | Database server 01 reaches 100% — PostgreSQL refuses new writes |
| 10:33 | Application errors spike — 43% of transactions failing |
| 10:34 | PagerDuty SEV1 alert: "Multiple servers at 100% disk capacity" |
| 10:35 | SEV1 declared — SRE team bridge opened |

### Containment and Recovery

| Time | Event |
|------|-------|
| 10:40 | SRE team begins emergency disk recovery on affected servers |
| 10:45 | Database server 01: audit_log table identified as largest consumer (180GB) |
| 10:46 | Audit_log partition identified: 340GB total, growing 1.2GB/day |
| 10:50 | 6 months of old audit data truncated from audit_log (180GB freed) |
| 11:00 | Database server 01 disk drops from 100% to 55% |
| 11:05 | Remaining database servers: same audit table truncation applied |
| 11:15 | Application servers: temp files cleaned from {app-home}/temp directory |
| 11:20 | 15GB of temp files removed per app server — disk drops from 95% to 70% |
| 11:25 | Log rotation reviewed: `rotate 5` increased to `rotate 30` with compression |
| 11:30 | Old compressed logs purged — disk drops from 70% to 55% |
| 11:45 | All 20 servers below 60% disk |
| 11:50 | Application error rate returning to normal |
| 12:00 | All transactions processing normally |
| 12:30 | Post-incident review initiated |

### Post-Incident Actions

| Time | Event |
|------|-------|
| 12:30 | Logrotate configuration updated on all servers: `rotate 30`, `compress`, `delaycompress` |
| 12:35 | Temp file cleanup cron job created for {app-home}/temp (hourly cleanup) |
| 12:40 | Audit_log table partitioned by month — retention policy: 6 months |
| 12:45 | Automated partition maintenance cron job created |
| 12:50 | Disk monitoring alerts updated: 80% = warning, 90% = critical (was 85% and 95%) |
| 13:00 | Incident formally closed |

### Metrics Summary

| Metric | Pre-Incident | Peak During Incident | Post-Recovery |
|--------|-------------|---------------------|---------------|
| Database Server Disk | 70% | 100% | 55% |
| Application Server Disk | 65% | 97% | 50% |
| Transaction Error Rate | 0.1% | 43% | 0.1% |
| Affected Servers | 0 | 20 | 0 |
| Audit Log Size | 340GB | 340GB | 160GB |
| Log Volume/Day | 2GB | 15GB | 15GB (with compression) |
| Temp File Accumulation | 0 | 30GB/server | 0 |
| Recovery Time | — | 132 minutes | — |

### Root Cause Summary

The incident was caused by three concurrent issues:
1. **Logrotate misconfiguration**: `rotate 5` insufficient for 15GB/day log volume
2. **Temp file leak**: Report generation bug with no cleanup, plus tmpwatch not covering correct path
3. **Unbounded audit table**: No partition management or retention policy for audit_log (340GB)

### Action Items

| # | Action | Owner | Target | Status |
|---|--------|-------|--------|--------|
| 1 | Fix logrotate: rotate 30, compress, delaycompress | SRE Team | 08/01 | Done |
| 2 | Fix temp file cleanup: cron job for {app-home}/temp | SRE Team | 08/01 | Done |
| 3 | Partition audit_log by month, 6-month retention | DBA Team | 08/01 | Done |
| 4 | Fix report generation bug (delete temp files after download) | App Team | 08/05 | In Progress |
| 5 | Reduce log verbosity from DEBUG to INFO | App Team | 08/02 | In Progress |
| 6 | Update disk alert thresholds: 80%, 90% | SRE Team | 08/01 | Done |
| 7 | Add inode monitoring to disk alerts | SRE Team | 08/05 | Planned |
| 8 | Capacity planning review for all production servers | SRE Team | 08/15 | Planned |

## Expanded Incident Analysis

### Server-by-Server Impact Assessment

**Database Servers (8 servers)**:

| Server | Disk Size | Usage Before | Usage During | Usage After | Primary Consumer |
|--------|-----------|-------------|-------------|-------------|-----------------|
| db-01 (primary) | 500GB | 340GB (68%) | 500GB (100%) | 275GB (55%) | audit_log (340GB) |
| db-02 (replica) | 500GB | 350GB (70%) | 500GB (100%) | 280GB (56%) | audit_log (340GB) |
| db-03 (replica) | 500GB | 330GB (66%) | 500GB (100%) | 270GB (54%) | audit_log (340GB) |
| db-04 (primary) | 500GB | 360GB (72%) | 500GB (100%) | 290GB (58%) | audit_log (340GB) + logs (20GB) |
| db-05 (replica) | 500GB | 345GB (69%) | 500GB (100%) | 275GB (55%) | audit_log (340GB) |
| db-06 (replica) | 500GB | 355GB (71%) | 500GB (100%) | 285GB (57%) | audit_log (340GB) |
| db-07 (primary) | 500GB | 335GB (67%) | 500GB (100%) | 270GB (54%) | audit_log (340GB) |
| db-08 (replica) | 500GB | 350GB (70%) | 500GB (100%) | 280GB (56%) | audit_log (340GB) |

**Application Servers (12 servers)**:

| Server | Disk Size | Usage Before | Usage During | Usage After | Primary Consumer |
|--------|-----------|-------------|-------------|-------------|-----------------|
| app-01 | 200GB | 125GB (62%) | 190GB (95%) | 100GB (50%) | Logs (75GB) + Temp (30GB) |
| app-02 | 200GB | 130GB (65%) | 195GB (97%) | 105GB (52%) | Logs (75GB) + Temp (30GB) |
| app-03 | 200GB | 120GB (60%) | 185GB (92%) | 95GB (48%) | Logs (75GB) + Temp (30GB) |
| app-04 | 200GB | 128GB (64%) | 190GB (95%) | 102GB (51%) | Logs (75GB) + Temp (30GB) |
| app-05 | 200GB | 132GB (66%) | 192GB (96%) | 105GB (53%) | Logs (75GB) + Temp (30GB) |
| app-06 | 200GB | 118GB (59%) | 180GB (90%) | 92GB (46%) | Logs (75GB) + Temp (25GB) |
| app-07 | 200GB | 126GB (63%) | 188GB (94%) | 100GB (50%) | Logs (75GB) + Temp (28GB) |
| app-08 | 200GB | 135GB (67%) | 195GB (97%) | 108GB (54%) | Logs (75GB) + Temp (32GB) |
| app-09 | 200GB | 122GB (61%) | 185GB (92%) | 96GB (48%) | Logs (75GB) + Temp (28GB) |
| app-10 | 200GB | 130GB (65%) | 190GB (95%) | 104GB (52%) | Logs (75GB) + Temp (30GB) |
| app-11 | 200GB | 128GB (64%) | 188GB (94%) | 100GB (50%) | Logs (75GB) + Temp (29GB) |
| app-12 | 200GB | 134GB (67%) | 192GB (96%) | 106GB (53%) | Logs (75GB) + Temp (31GB) |

### Recovery Actions Detail

**Phase 1: Database Servers (10:40-11:15)**
1. Identified audit_log as largest consumer (340GB average per server)
2. Verified audit data retention requirements with legal team (6 months minimum)
3. Executed partition drop: `DROP TABLE audit_log_2025_* CASCADE;` — removed 180GB (6+ months of data)
4. Verified disk freed: `df -h` showed 55% utilization
5. Applied same fix to all 8 database servers (took 45 minutes total due to replication considerations)

**Phase 2: Application Servers (11:00-11:30)**
1. Identified temp files as primary offender (30GB average per server)
2. Executed temp file cleanup: `find /opt/app/temp -type f -mmin +60 -delete`
3. Verified tmpwatch configuration — added missing path to cron
4. Compressed old logs: `find /var/log/app -name "*.log" -mtime +2 -exec gzip {} \;`
5. Updated logrotate configuration in-place
6. Verified disk freed: `df -h` showed ~50% utilization

### Monitoring Gap Analysis

**Alert Timeline and Gaps**:

| Time | Event | Expected Response | Actual Response |
|------|-------|------------------|-----------------|
| 08:30 | Server app-03 hits 80% disk (warning) | Investigate within 1 hour | Acknowledged, no investigation |
| 09:00 | 3 servers > 85% (warning) | Escalate to SRE team | Acknowledged, no escalation |
| 10:00 | 5 servers > 90% (critical) | Page SRE team | PagerDuty alert fired at 10:00 |
| 10:15 | 8 DB servers > 95% (critical) | Page SRE + DBA + IC | PagerDuty fired, gap in response |
| 10:30 | DB-01 at 100% (emergency) | Immediate response | SEV1 declared at 10:35 |

**Gap Analysis**:
1. Warning alerts at 80% were not investigated — on-call engineer assumed "normal traffic increase"
2. No escalation was triggered for acknowledged but unresolved alerts
3. The jump from 85% to 100% happened faster than expected because the batch report generation job ran at 06:00 and temp files accumulated during the day
4. No trend-based alert existed — "disk projected to fill within 24 hours" would have caught this at 08:30

### Communication and Coordination

**Key Communication Failures**:
1. The DBA team was not initially paged — they joined 15 minutes after SEV1 declaration
2. The application team was not aware of the temp file issue until 30 minutes into the incident
3. There was no single view of disk utilization across all servers — the SRE team used individual SSH connections to check each server
4. The decision to truncate audit data required legal consultation, delaying recovery by 15 minutes

**Improved Protocols**:
Following this incident:
- A centralized disk utilization dashboard was created (Grafana with all servers)
- DBA team is automatically paged for any database disk alert
- Temp file cleanup is now part of the standard incident response checklist
- Legal retention policies are documented in the runbook to avoid consultation delays

### Expanded Technical Details

**PostgreSQL WAL Growth During Incident**:
When the database disk reached 100%, PostgreSQL could not write to the WAL (Write-Ahead Log). This caused:
- WAL archiver process to fail
- WAL sender processes (for replication) to stall
- Checkpoint process to fail
- Replication lag to increase from 50ms to 5,200ms
- Risk of data loss if a crash occurred during this window

The WAL directory consumed an additional 15GB during the incident (standard for the recovery window), which exacerbated the disk space shortage.

**Application Error Analysis**:
The 43% error rate was distributed as:
- Database connection errors (could not connect to database): 22%
- Query timeout errors (query waiting too long in queue): 15%
- Application errors (failed to write to temp directory): 4%
- Other errors: 2%

All errors were directly or indirectly caused by disk exhaustion.

**Log File Growth Rate Calculation**:
```
Normal conditions:
- Log rate: 2GB/day per server
- 12 servers × 2GB/day = 24GB/day total

With verbose debug logging:
- Log rate: 15GB/day per server
- 12 servers × 15GB/day = 180GB/day total
- With rotate 5: 5 × 15GB = 75GB per server = 900GB total (45% of aggregate disk capacity)

With proper logrotate (rotate 30, compress):
- 30 compressed logs × 1.5GB = 45GB per server (90% reduction)
- 540GB total (acceptable for 200GB disks with 30-day retention)
```

### Root Cause Summary (Technical)

The incident was caused by three converging disk consumption patterns:

**Pattern 1: Unbounded Growth (audit_log)**
- Type: Gradual, predictable
- Cause: No retention policy or partition management
- Duration: 10 months
- Space consumed: 340GB per database server

**Pattern 2: Increased Log Volume (v3.1 release)**
- Type: Step change, sudden
- Cause: Debug logging enabled without logrotate adjustment
- Duration: 2 weeks (since v3.1 deployment)
- Space consumed: 75GB per server

**Pattern 3: Temp File Leak (report generation bug)**
- Type: Accelerating, acute
- Cause: Bug in report generation code (no cleanup)
- Duration: 2 weeks (since v3.1 deployment)
- Space consumed: 30GB per server during business hours
