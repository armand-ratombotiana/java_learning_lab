# INCIDENT RESPONSE CHEATSHEET

## Severity Classification

| Severity | Definition | Response Time | Update Freq | Examples |
|----------|-----------|---------------|-------------|----------|
| **SEV1 / P0** | Complete service outage, all users affected, data loss | < 5 min | Every 30 min | Site down, data corruption, security breach |
| **SEV2 / P1** | Partial outage, significant degradation for subset | < 15 min | Every 60 min | Payment failures, high latency, feature down |
| **SEV3 / P2** | Minor impact, workaround available | < 60 min | Daily | UI bug, non-critical feature broken |
| **SEV4 / P3** | Minimal impact, no user-facing effect | Next business day | Per ticket | Internal tool issue, cosmetic bug |

## Incident Response Flow

```
ALERT RECEIVED
    │
    ▼
ACKNOWLEDGE (within 2 min)
    │
    ▼
TRIAGE ─── Is this real?
    │           │
    │       ┌───┴───┐
    │       YES     NO
    │       │       │
    │       ▼       ▼
    │   DECLARE  SILENCE ALERT
    │   SEVERITY (document false positive)
    │       │
    │       ▼
    │   ASSEMBLE TEAM
    │   ├── Assign Incident Commander (IC)
    │   ├── Assign Operations Lead
    │   ├── Assign Communications Lead
    │   └── Assign Scribe
    │       │
    │       ▼
    │   INITIAL ASSESSMENT
    │   ├── What's affected?
    │   ├── Blast radius?
    │   ├── Customer impact?
    │   └── Send initial status update
    │       │
    │       ▼
    │   MITIGATE (before deep debugging)
    │   ├── Rollback recent deployment
    │   ├── Disable feature flag
    │   ├── Scale up / scale out
    │   ├── Traffic shift to healthy region
    │   ├── Restart service (last resort)
    │   ├── Kill specific slow queries
    │   └── Clear cache
    │       │
    │       ▼
    │   INVESTIGATE ROOT CAUSE
    │   ├── Check dashboards (metrics, logs, traces)
    │   ├── Narrow by layer (app → infra → DB → network)
    │   ├── Look for correlations (deployment, config change)
    │   └── Document findings and timeline
    │       │
    │       ▼
    │   RESOLVE (permanent fix)
    │   ├── Apply fix
    │   ├── Verify fix (metrics return to baseline)
    │   └── Monitor for 30+ min
    │       │
    │       ▼
    │   CLOSURE
    │   ├── Send final status update
    │   ├── Schedule post-mortem within 72h
    │   ├── File follow-up tickets
    │   └── Update runbooks
```

## Communication Template Snippets

### Initial Notification
```
INCIDENT: [SEV1] - [Service] - [Brief Description]
Status: INVESTIGATING
Impact: [what's affected, % of users]
Started: [UTC]
Action: [what we're doing now]
Next update: [time]
IC: [Name]
Channel: [#incident-xxx]
```

### Status Update
```
UPDATE #[N]: [Service] — [Status]
Current: [what's happening now]
Impact: [current impact]
Next action: [what we'll do next]
Next update: [time or none]
```

### Resolution
```
RESOLVED: [Service] — [Brief Description]
Duration: [HH:MM]
Impact: [summary of impact]
Root cause: [1 sentence]
Action items: [follow-up tickets]
Post-mortem: [date/time]
```

## Common Command References

### Kubernetes (kubectl)
```bash
# Get pods with status
kubectl get pods -o wide -A

# Describe a specific pod (detailed events, conditions)
kubectl describe pod <name> -n <ns>

# Get logs
kubectl logs -f <pod> -n <ns> --tail=100
kubectl logs -f <pod> -n <ns> -c <container>

# Exec into container
kubectl exec -it <pod> -n <ns> -- /bin/sh

# Check events
kubectl get events -n <ns> --sort-by=.lastTimestamp

# Resource utilization
kubectl top pod -n <ns>
kubectl top node

# Restart deployment
kubectl rollout restart deployment/<name> -n <ns>

# Rollback
kubectl rollout undo deployment/<name> -n <ns>
kubectl rollout status deployment/<name> -n <ns>
```

### Docker
```bash
# List containers
docker ps -a

# Logs
docker logs -f <container> --tail=100

# Resource usage
docker stats <container>

# Inspect
docker inspect <container>

# Execute command
docker exec -it <container> /bin/bash

# Kill all stuck containers
docker kill $(docker ps -q)
```

### Git (Incident Debugging)
```bash
# Find what changed
git diff <deploy-tag>..<current-tag>

# Find who changed a file
git blame <file>

# Search commit messages
git log --grep="password\|secret\|connection\|memory"

# Show deployment tags
git tag -l "deploy-*"

# Cherry pick a fix
git cherry-pick <commit-hash>
```

### Linux / OS Commands
```bash
# CPU + memory by process
top -H -p <pid>

# Disk space
df -h
du -sh /var/log/*

# Disk I/O
iostat -x 1

# Network connections
ss -tuln
netstat -an | grep :8080

# DNS
dig <service>.example.com
nslookup <service>.example.com

# TLS check
openssl s_client -connect host:443 -servername host

# Load average, memory, swap
vmstat 1

# Find large files
find / -type f -size +100M -exec ls -lh {} \; 2>/dev/null
```

### Java JVM
```bash
# Thread dump
jstack <pid> > threaddump.txt

# Heap dump
jmap -dump:live,format=b,file=heap.hprof <pid>

# GC stats
jstat -gcutil <pid> 5s

# Metaspace
jcmd <pid> VM.metaspace

# Class loader stats
jcmd <pid> VM.classloader_stats

# JFR recording
jcmd <pid> JFR.start name=recording settings=profile duration=60s filename=recording.jfr
```

### Database
```bash
# MySQL: show running queries
mysql -e "SHOW FULL PROCESSLIST"

# MySQL: show locks
mysql -e "SHOW OPEN TABLES WHERE In_use > 0"

# PostgreSQL: running queries
psql -c "SELECT * FROM pg_stat_activity WHERE state != 'idle'"

# PostgreSQL: locks
psql -c "SELECT * FROM pg_locks WHERE granted = false"

# Check connection count
mysql -e "SHOW STATUS LIKE 'Threads_connected'"

# Kill query
mysql -e "KILL <thread_id>"

# Check replication lag
mysql -e "SHOW SLAVE STATUS\G"
psql -c "SELECT * FROM pg_stat_replication"
```

## Debugging Flow: Application → Infrastructure → Database → Network

### Layer 1: Application (start here if symptoms are user-facing)
```
ERRORS / LATENCY
    │
    ├── Check error logs (journalctl, app.log, structured logs)
    ├── Check recent deployments (git log, CI/CD pipeline)
    ├── Check configuration changes (feature flags, config files)
    ├── Check thread dumps (are threads stuck? blocked? waiting?)
    ├── Check heap dumps (memory leak? GC issues?)
    ├── Check API endpoints (all fail or specific ones?)
    └── Check dependencies (downstream services healthy?)
```

### Layer 2: Infrastructure (if app layer is clean)
```
RESOURCE ISSUE
    │
    ├── CPU: top, htop — is CPU maxed? which process?
    ├── Memory: free, vmstat — swap usage? OOM killer active?
    ├── Disk: df -h — space exhausted? inodes full?
    ├── Disk I/O: iostat — high await? IOPS maxed?
    ├── Network: ss, netstat — connection limits? TIME_WAIT flood?
    └── File descriptors: ulimit -a — limit hit? lsof count?
```

### Layer 3: Database (if infrastructure is healthy)
```
DATABASE ISSUE
    │
    ├── Connection pool: maxed out? leaked connections?
    ├── Slow queries: long-running? locked? blocked?
    ├── Locks: database locks? row locks? table locks?
    ├── Replication lag: replica behind primary?
    ├── Disk space on DB: WAL files? transaction log?
    └── Query plan: regression? stale statistics?
```

### Layer 4: Network (if everything else is clean)
```
NETWORK ISSUE
    │
    ├── DNS: dig/nslookup — resolving correctly? propagation delay?
    ├── TLS/SSL: certificate expiry? wrong SNI? cipher mismatch?
    ├── Load balancer: health checks passing? backend healthy?
    ├── Firewall: ports open? security group changed?
    ├── CDN: cache hit ratio? origin pull issues?
    └── DDoS: traffic pattern? WAF blocking valid requests?
```

## When to Escalate

| Condition | Escalate To |
|-----------|-------------|
| Cannot ID root cause within 15 min | Tier 2 / Service lead |
| Need DB expertise | DBA team |
| Need security expertise | Security team |
| Incident > 1 hour | Engineering manager + stakeholders |
| Customer impact > 30 min | Comms lead + product manager |
| Data breach / security incident | Security + Legal + Compliance |
| Cloud provider issue | Vendor support (AWS/Azure/GCP) |
| Multi-team incident | Program manager / director |
| Need to involve vendors | Vendor support + account manager |

## Post-Mortem Writing Guide

### Structure
1. **Title:** `[INCIDENT_ID] - [SEVERITY] - [BRIEF] - [DATE]`
2. **Executive Summary:** 1-2 paragraphs for executives
3. **Impact:** Metrics, users affected, revenue loss, SLA breach
4. **Timeline:** UTC times, key events (from logs, not memory)
5. **Root Cause:** Direct cause + contributing factors + trigger
6. **5 Whys Analysis:** Table format
7. **Action Items:** Immediate, short-term, medium-term, long-term
8. **Lessons Learned:** What went well, what could improve, surprises

### Post-Mortem Checklist
- [ ] Timeline is from logs/monitoring, not memory
- [ ] Root cause has supporting evidence
- [ ] All action items have assigned owners + due dates
- [ ] Customer impact is quantified
- [ ] Detection gaps identified
- [ ] Prevention gaps identified
- [ ] "What went well" section included
- [ ] Blameless language used throughout
- [ ] Post-mortem shared with affected teams
- [ ] Action items tracked in project management tool

### Blameless Language
| Avoid | Use Instead |
|-------|-------------|
| "Engineer forgot to..." | "The code path did not ensure..." |
| "Developer didn't test..." | "Tests did not cover..." |
| "Operations missed the alert" | "Alert routing did not reach on-call" |
| "Poor code quality" | "Code review did not flag the pattern" |
| "Someone should have caught this" | "System lacked automated checks for this" |
