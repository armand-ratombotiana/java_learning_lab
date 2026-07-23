# Lab 09 — Disk Space: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 5 minutes (emergency cleanup) |
| RPO | 0 (data preserved, only logs affected) |
| MTD | 15 minutes before data loss risk |

## Scenarios

### Scenario A: Log File Fills Disk

**Trigger:** Verbose logging fills /var, database cannot write
**Recovery:**
1. `truncate -s 0 /var/log/app.log` (immediate space recovery)
2. `logrotate --force /etc/logrotate.d/app` (force rotation)
3. Verify database writes resume
4. Fix logging configuration
5. Add disk monitoring with 80% warning alert

### Scenario B: Temp Tablespace Full (Oracle)

**Trigger:** Query spill fills temp tablespace
**Recovery:**
1. Add tempfile: `ALTER TABLESPACE temp ADD TEMPFILE`
2. Kill the offending query
3. Fix the query (add index, optimize joins)
4. Schedule temp tablespace growth monitoring

### Scenario C: Docker Disk Full

**Trigger:** Docker/overlay2 fills disk with unused images/containers
**Recovery:**
1. `docker system prune -a --volumes` (remove unused)
2. Clean up build cache
3. Set up Docker garbage collection schedule
4. Add Docker disk usage monitoring
