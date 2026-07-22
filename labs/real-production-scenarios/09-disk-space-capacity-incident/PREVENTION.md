# Prevention Guide — Avoiding Disk Space Capacity Incidents

## How to Prevent Recurrence of SEV1 Disk Exhaustion Incidents

This document outlines engineering practices, operational procedures, and monitoring standards required to prevent disk space capacity incidents. Drawing from Google SRE site reliability engineering principles, Netflix's operational practices, and industry standards.

---

## 1. Log Management Standards

### Logrotate Configuration Requirements

| Parameter | Standard | Enforcement |
|-----------|----------|-------------|
| `rotate` | 30 (minimum) | SRE review for deviations |
| `compress` | Required | Automated config checker |
| `delaycompress` | Required | Automated config checker |
| `maxage` | 30 days | Retention policy compliance |
| `dateext` | Required | Log file naming standard |
| `sharedscripts` | Recommended | Performance optimization |

### Logrotate Template

```
# /etc/logrotate.d/{service}
/var/log/{service}/*.log {
    daily
    rotate 30
    maxage 30
    compress
    delaycompress
    dateext
    dateformat -%Y%m%d-%s
    missingok
    notifempty
    sharedscripts
    postrotate
        systemctl reload {service} > /dev/null 2>&1 || true
    endscript
}
```

### Log Volume Governance

- All services MUST document expected log volume per day (in MB/GB)
- Log volume MUST be included in deployment checklist
- Any change that increases log volume by > 50% requires logrotate review
- DEBUG logging is prohibited in production (use INFO as minimum)
- Dynamic log level control must be available via Actuator endpoint

### Log Retention Policy

| Log Type | Retention | Action |
|----------|-----------|--------|
| Application logs | 30 days compressed | Logrotate |
| Access logs | 90 days compressed | Separate logrotate config |
| Audit logs | 6 months in DB | Partition drop |
| Debug logs | 7 days | Separate logrotate config |
| System logs | 30 days | logrotate (system default) |

---

## 2. Temp File Management Standards

### Temp File Cleanup Requirements

- All temp directories MUST be covered by tmpwatch or equivalent
- Temp files MUST be cleaned up in application code (finally block)
- Temp file accumulation MUST be monitored
- Maximum temp file age: 1 hour
- Maximum temp directory size: 5GB

### Temp Directory Configuration

| Directory | Cleanup Tool | Frequency | Max Age | Max Size |
|-----------|-------------|-----------|---------|----------|
| `/tmp` | tmpwatch | Every 6 hours | 6 hours | n/a |
| `/var/tmp` | tmpwatch | Every 24 hours | 24 hours | n/a |
| `{app-home}/temp` | tmpwatch | Every 30 min | 1 hour | 5GB |
| `{app-home}/uploads` | custom script | Every 1 hour | 2 hours | 10GB |

### Application Code Requirements

- All file creation must have a corresponding cleanup in `finally` or `try-with-resources`
- Temp files should use `java.nio.file.Files.createTempFile()` where possible
- File downloads must include response entity cleanup in finally block
- Log temp directory size on application startup

---

## 3. Database Table Management Standards

### Table Size Governance

| Table Type | Max Size | Retention | Partitioning |
|------------|----------|-----------|-------------|
| Audit tables | 50GB | 6 months | Monthly partitions required |
| Log tables | 100GB | 3 months | Monthly partitions required |
| Event tables | 200GB | 12 months | Monthly partitions required |
| Transactional tables | Based on data growth | Per business requirement | Partitioning recommended |

### Partition Management Requirements

- All time-series tables MUST be partitioned by date
- Partition maintenance job MUST create partitions 3 months in advance
- Partition maintenance job MUST drop partitions older than retention period
- Partition maintenance MUST run automatically (no manual intervention)
- Partition maintenance MUST be included in deployment checklist

### Database Capacity Monitoring

```sql
-- Regular monitoring queries
SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as total_size,
    (pg_total_relation_size(schemaname||'.'||tablename) * 100.0 /
        (SELECT sum(pg_total_relation_size(schemaname||'.'||tablename))
         FROM pg_tables WHERE schemaname = 'public')) as percentage
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC
LIMIT 20;
```

---

## 4. Monitoring Standards

### Threshold Configuration

| Metric | Warning | Critical | Emergency |
|--------|---------|----------|-----------|
| Disk Utilization | 80% | 90% | 97% |
| Inode Utilization | 80% | 90% | 95% |
| Log Directory Size | 50% of allocated | 75% of allocated | 90% of allocated |
| Temp Directory Size | 2GB | 4GB | 5GB |
| Database Table Size | 80% of budget | 95% of budget | 100% of budget |
| Disk Growth Rate | 90% predicted in 7 days | 90% predicted in 24h | — |

### Monitoring Requirements

- [ ] Disk utilization alert at 80% (warning, acknowledge within 1 hour)
- [ ] Disk utilization alert at 90% (critical, acknowledge within 15 minutes)
- [ ] Disk utilization alert at 97% (emergency, page immediately)
- [ ] Inode utilization monitoring (warning at 80%)
- [ ] Disk growth rate prediction (Prometheus predict_linear)
- [ ] Temp file accumulation monitoring
- [ ] Database table size monitoring
- [ ] Log volume monitoring (per service per day)

### Capacity Forecasting

- Weekly disk trend analysis using Prometheus predict_linear
- Monthly capacity review (all production servers)
- Quarterly capacity planning (next 6 months forecast)
- Pre-release capacity impact assessment for all deployments

---

## 5. Operational Procedures

### Pre-Release Capacity Assessment

Each release must answer:
1. What is the expected log volume increase (in MB/day)?
2. What is the expected temp file creation rate?
3. What is the expected database table growth rate?
4. Does this release change any retention policies?
5. Is a logrotate configuration update required?

### Monthly Maintenance Tasks

- [ ] Review logrotate configurations on all servers
- [ ] Verify temp file cleanup cron jobs are running
- [ ] Check database partition maintenance ran successfully
- [ ] Review disk growth trends
- [ ] Check for tables approaching size limits
- [ ] Verify backup disk space requirements

### Quarterly Capacity Review

- [ ] Review all production server disk utilization trends
- [ ] Project disk usage for next 6 months
- [ ] Plan disk upgrades if projected to exceed 80% within 6 months
- [ ] Review log retention policies
- [ ] Review database retention policies
- [ ] Update capacity forecasts

---

## 6. Automated Remediation

### Emergency Disk Cleanup Script

```bash
#!/bin/bash
# emergency_disk_cleanup.sh — Run when disk exceeds 95%

MOUNT=$1
THRESHOLD=${2:-95}

USAGE=$(df -h "$MOUNT" | tail -1 | awk '{print $5}' | sed 's/%//')

if [ "$USAGE" -lt "$THRESHOLD" ]; then
    echo "Disk usage $USAGE% below threshold $THRESHOLD%, no action needed"
    exit 0
fi

echo "EMERGENCY: Disk at ${USAGE}%, starting cleanup..."

# 1. Clean temp files
echo "Cleaning temp files..."
find /opt/app/temp -type f -mmin +60 -delete 2>/dev/null
find /tmp -type f -mmin +360 -delete 2>/dev/null

# 2. Compress old logs
echo "Compressing old logs..."
find /var/log/app -name "*.log" -mtime +1 -exec gzip {} \; 2>/dev/null

# 3. Drop old DB partitions (run via cron job or manually)
echo "Dropping old database partitions..."
psql -c "SELECT drop_old_partitions(6);" 2>/dev/null

# 4. Verify
NEW_USAGE=$(df -h "$MOUNT" | tail -1 | awk '{print $5}' | sed 's/%//')
echo "Cleanup complete. Disk went from ${USAGE}% to ${NEW_USAGE}%"
```

---

## 7. Testing Requirements

### Mandatory Disk-Space Tests
| Test | Frequency | Success Criteria |
|------|-----------|-----------------|
| Logrotate rotation test | Per deployment | Logs compressed, oldest rotated files deleted |
| Temp file cleanup test | Per deployment | Temp files deleted after download within 5 minutes |
| Partition maintenance test | Monthly | Partitions created/dropped as expected |
| Disk alert test | Monthly | Alert fires at correct threshold |
| Capacity planning review | Quarterly | No server projected to exceed 80% within 6 months |

---

## Summary of Prevention Measures

| # | Measure | Owner | Timeline | Impact |
|---|---------|-------|----------|--------|
| 1 | Standardize logrotate config across all servers | SRE Team | 1 week | Critical |
| 2 | Add temp file cleanup (cron + code fix) | App Team | 1 week | Critical |
| 3 | Implement DB partition management | DBA Team | 2 weeks | Critical |
| 4 | Update disk monitoring thresholds (80/90/97) | SRE Team | Immediate | Critical |
| 5 | Add capacity planning to release process | SRE Team | 1 month | High |
| 6 | Add disk growth rate prediction alerts | SRE Team | 2 weeks | High |
| 7 | Implement automated emergency cleanup | SRE Team | 1 month | Medium |
| 8 | Conduct quarterly capacity review | SRE Team | Ongoing | High |

### References
- Google SRE Book — Chapter 12: "Managing Disk Space"
- Google SRE Book — Chapter 6: "Monitoring Distributed Systems"
- Netflix Tech Blog: "Linux Performance Tools"
- PostgreSQL Documentation: "Table Partitioning"
- Atlassian Engineering: "How We Fixed Disk Space Issues at Scale"
