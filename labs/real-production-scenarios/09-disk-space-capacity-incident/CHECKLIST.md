# Incident Response Runbook Checklist — Disk Space Capacity Incident

## Incident Type: SEV1/P0 — Disk Exhaustion / Data Corruption Risk

### Classification
- [ ] Incident severity: SEV1 (Critical) / P0 (Immediate)
- [ ] Incident type: Disk Space Exhaustion
- [ ] Number of affected servers: ____ out of ____
- [ ] Highest disk utilization: ____ %
- [ ] Inode utilization: ____ %
- [ ] Current incident commander: ________________________________

---

## Phase 1: Detection and Triage (0–5 minutes)

### Immediate Detection
- [ ] Confirm PagerDuty alert: disk > 90% or > 97%
- [ ] Run: `df -h` to check all mounted filesystems
- [ ] Run: `df -i` to check inode utilization
- [ ] Check: `dmesg | tail -20` for disk I/O errors
- [ ] Check: `tail /var/log/syslog | grep -i "disk\|space\|full"`

### Initial Assessment
- [ ] How many servers are affected?
- [ ] What is the highest utilization?
- [ ] Is the disk actively growing?
- [ ] Is this disk, inode, or both?
- [ ] What is the largest directory?

### Communication
- [ ] Declare SEV1 if multiple servers > 97% or any server at 100%
- [ ] Open bridge: SRE team, DBA team, application team
- [ ] Notify incident commander
- [ ] Post initial status: "Disk capacity critical on {{N}} servers"

---

## Phase 2: Containment (5–30 minutes)

### Emergency Disk Space Recovery
- [ ] Run `du -sh /* | sort -rh | head -20` to identify largest consumers
- [ ] Clear temp files: `find /opt/app/temp -type f -mmin +60 -delete`
- [ ] Clear /tmp: `find /tmp -type f -mmin +360 -delete`
- [ ] Compress old logs: `find /var/log/app -name "*.log" -mtime +1 -exec gzip {} \;`
- [ ] Drop old DB partitions: see database section below
- [ ] Verify freed space: `df -h`

### Stop Disk Growth
- [ ] Reduce log level: POST /admin/logging/root/INFO
- [ ] Stop report generation batch jobs
- [ ] Disable verbose logging temporarily
- [ ] If database: consider read-only mode for non-critical queries
- [ ] Kill processes writing to disk (if safe)

### Database-Specific Actions
- [ ] Check largest tables: `SELECT pg_size_pretty(pg_total_relation_size(...))`
- [ ] Drop old audit partitions: `DROP TABLE IF EXISTS audit_log_yyyy_mm;`
- [ ] Truncate temp tables: `TRUNCATE temp_data;`
- [ ] Run VACUUM: `VACUUM ANALYZE;`
- [ ] Check WAL growth: `SELECT * FROM pg_stat_wal;`

---

## Phase 3: Recovery (30–90 minutes)

### Fix Logrotate (All Servers)
- [ ] Update logrotate config: `rotate 30`, `compress`, `delaycompress`
- [ ] Test config: `logrotate -d /etc/logrotate.d/app`
- [ ] Compress existing old logs
- [ ] Verify logrotate is scheduled: `cat /etc/cron.daily/logrotate`
- [ ] Monitor log directory size

### Fix Temp File Cleanup (All Servers)
- [ ] Add tmpwatch cron: `*/30 * * * * root /usr/sbin/tmpwatch 1 /opt/app/temp`
- [ ] Fix application code (finally block cleanup)
- [ ] Add temp file monitoring
- [ ] Verify existing temp files cleaned

### Fix Database Retention (All DB Servers)
- [ ] Create partitioned audit_log table
- [ ] Migrate existing data to partitions
- [ ] Create partition maintenance job
- [ ] Set retention policy
- [ ] Test partition maintenance

### Service Verification
- [ ] Verify all servers below 80% disk
- [ ] Verify error rate returning to normal
- [ ] Check application health endpoints
- [ ] Verify database replication status
- [ ] Run end-to-end smoke tests

---

## Phase 4: Root Cause Investigation (90–180 minutes)

### Logrotate Investigation
- [ ] Check current logrotate config on all servers
- [ ] Verify rotate count (should be >= 30)
- [ ] Check compression is enabled
- [ ] Check maxage setting
- [ ] Review log volume per day

### Temp File Investigation
- [ ] Identify temp file creation pattern
- [ ] Review application code for cleanup gaps
- [ ] Check tmpwatch cron configuration
- [ ] Verify temp directory paths

### Database Investigation
- [ ] Check all table sizes
- [ ] Review partition configuration
- [ ] Check retention policies
- [ ] Review data growth rates

### Monitoring Investigation
- [ ] Review alert threshold configuration
- [ ] Check if trend analysis was available
- [ ] Review acknowledged alerts that were not escalated
- [ ] Check capacity planning documentation

---

## Phase 5: System-Wide Prevention (180–360 minutes)

### Immediate Prevention
- [ ] Update disk alert thresholds to 80/90/97%
- [ ] Add inode monitoring
- [ ] Add disk growth rate prediction
- [ ] Standardize logrotate across all servers
- [ ] Ensure all temp dirs have cleanup cron

### Short-Term Prevention
- [ ] Add capacity review to release process
- [ ] Implement automated partition maintenance
- [ ] Create emergency disk cleanup script
- [ ] Add log volume monitoring

### Long-Term Prevention
- [ ] Implement capacity planning process
- [ ] Schedule monthly capacity reviews
- [ ] Add disk space to deployment checklist
- [ ] Train SRE team on disk space incident response

---

## Phase 6: Post-Incident (After Resolution)

### Documentation
- [ ] Complete INCIDENT_REPORT.md with full timeline
- [ ] Complete ROOT_CAUSE.md with 5 Whys
- [ ] Update runbooks with disk space procedures
- [ ] Document all configuration changes made

### Action Items
- [ ] Create JIRA tickets for all prevention items
- [ ] Assign owners and target dates
- [ ] Schedule follow-up review in 2 weeks
- [ ] Share post-mortem with engineering team

### Prevention
- [ ] Add logrotate check to deployment pipeline
- [ ] Enforce database partitioning for time-series tables
- [ ] Add temp file cleanup to code review checklist
- [ ] Schedule quarterly capacity review

---

## Emergency Commands

```powershell
# Check disk usage
df -h

# Check inode usage
df -i

# Find largest directories
du -sh /* | sort -rh | Select-Object -First 20

# Find largest files
Get-ChildItem -Path C:\ -Recurse -ErrorAction SilentlyContinue |
    Sort-Object Length -Descending | Select-Object -First 20 FullName, Length

# Linux equivalent for temp cleanup
find /opt/app/temp -type f -mmin +60 -delete

# Compress old logs
find /var/log/app -name "*.log" -mtime +1 -exec gzip {} \;

# Check PostgreSQL table sizes
SELECT schemaname, tablename, pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename))
FROM pg_tables WHERE schemaname = 'public' ORDER BY pg_total_relation_size DESC;

# Check logrotate configuration
cat /etc/logrotate.d/app

# Run logrotate manually
logrotate -f /etc/logrotate.d/app

# Check tmpwatch configuration
crontab -l | Select-String "tmpwatch"
```

---

## Emergency Contacts

| Role | Name | Phone | Slack |
|------|------|-------|-------|
| SRE Primary | James Wilson | +1-555-0400 | @james.wilson |
| SRE Secondary | Emily Chen | +1-555-0401 | @emily.chen |
| DBA Lead | Rachel Green | +1-555-0402 | @rachel.green |
| App Team Lead | Tom Bradley | +1-555-0403 | @tom.bradley |
| Incident Commander | David Park | +1-555-0404 | @david.park |

---

## Checklist Version
- Version: 1.0
- Last Updated: 2026-08-02
- Approved By: SRE Lead
- Next Review Date: 2026-11-02
