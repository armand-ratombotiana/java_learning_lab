# Interview Cheatsheet: Disk Space Incident

## Key Diagnostic Commands
- `df -h` — filesystem disk space usage
- `du -sh /*` — find largest directories
- `du -sh /var/log/*` — check log sizes
- `find / -type f -size +100M -exec ls -lh {} \;` — large files
- `lsof | grep deleted` — files deleted but still held open
- `journalctl --disk-usage` — journal size
- Docker: `docker system df` — container disk usage

## Common Metrics to Check
- Disk usage % by partition
- Disk usage growth rate (GB/day)
- Inode utilization
- Log file rotation status
- Temp directory size
- Old backup size and age
- Heap dump file presence

## Typical Root Causes
- Log rotation not configured/misconfigured
- Unmonitored growth in /var/log
- Temp files not cleaned up
- Old heap dumps from prior OOMs
- Database WAL/transaction log growth
- Docker images/volumes not pruned
- Monitoring agent log accumulation

## Interview Question Patterns
- "How do you set up disk space monitoring with trend alerting?"
- "Design a log rotation and retention policy"
- "What happens when disk fills up? How do you respond?"
- "How do you prevent heap dumps from filling disk?"

## STAR Story Template
**S**: Kubernetes node disk pressure → pods evicted → service unavailable
**T**: Recover disk space and prevent recurrence
**A**: Found 50GB of heap dumps and 100GB of unrotated logs, added logrotate config, set limit on heap dump retention
**R**: Disk usage stabilized at 40%, added disk trend alerting at 80%
