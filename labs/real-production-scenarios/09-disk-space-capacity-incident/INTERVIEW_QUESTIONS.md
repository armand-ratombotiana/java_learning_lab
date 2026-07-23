# Lab 09 — Disk Space / Capacity Incident: Interview Questions

**Q1: Your production server's disk is 100% full. What services are affected and how do you recover?**

**Answer:** Affected: 1) Application logging stops. 2) Database transactions fail (no space for WAL/redo logs). 3) Monitoring agents fail. 4) Cron jobs fail. 5) SSH login may fail (no space for .bash_history or PAM logs). 6) System may become unresponsive (no swap space). Recovery: 1) Identify largest directories: `du -sh /* | sort -rh | head -10`. 2) Clear logs: truncate log files, rotate aggressively. 3) Remove temp files: `tmpwatch`, clean /tmp. 4) Clean package manager cache. 5) If database is full: archive and purge old data, add disk space.

**Q2: How do you set up proactive disk space monitoring?**

**Answer:** 1) Monitor disk utilization per mount point: `df -h` at regular intervals. 2) Alert thresholds: warning at 80%, critical at 90%. 3) Monitor growth rate: predict when disk will fill (e.g., growing at 2GB/day → 100GB disk fills in 50 days). 4) Monitor inode usage (can have free space but no inodes). 5) Log file monitoring: largest log files, log rotation status. 6) Centralized logging: ship logs off-instance to prevent local disk fill. 7) Automated cleanup: cron job for log rotation, temp file cleanup.

**Q3: What is the difference between proactive capacity management and reactive disk space monitoring?**

**Answer:** Proactive: trend-based capacity planning — track growth rates, predict when disk will fill, order more capacity before it's needed. Reactive: responding to "disk full" alerts. Proactive examples: weekly capacity review, growth rate dashboards, automated capacity ordering (cloud auto-scaling). Reactive: emergency log cleanup, emergency disk extension. Mature operations spend 90% effort on proactive and 10% on reactive.

**Q4: A log file grows 50GB in one hour due to a logging bug. How do you handle it?**

**Answer:** 1) Immediately: truncate the log file (`> /var/log/app.log`), or move and compress. 2) Identify the log source — which class/module is generating excessive logs. 3) Fix the logging bug (log level too verbose, error loop). 4) Restart the application to release the file handle. 5) Add log rate limiting. 6) Add per-file log size limits with rotation. 7) Ship logs off-instance with compression. 8) Post-mortem: why wasn't log growth detected earlier?

**Q5: How do you calculate disk capacity requirements for a new service?**

**Answer:** Estimate per component: 1) Application logs: expected log volume per day × retention period. 2) Database: data size + indexes + WAL/redo logs (typically 2-3x data). 3) Caching: expected cache size × growth factor. 4) Temp space: max expected temp data per operation. 5) Monitoring and metrics. 6) OS and application binaries. Total × safety factor (1.5-2x). Plan for 6 months growth. In cloud: use auto-scaling storage (EBS, persistent disks) with growth alerts.

**Q6: What disk-related metrics should you monitor for a database server?**

**Answer:** 1) Disk utilization %. 2) Disk throughput (read/write IOPS). 3) Disk latency (I/O wait time). 4) Queue depth (pending I/O operations). 5) Disk space per database. 6) WAL/log file generation rate. 7) Temp tablespace growth (Oracle) / tempdb growth (SQL Server). 8) Replication lag — often caused by disk I/O. 9) Disk errors in system log. Alert on: utilization > 80%, latency > 20ms, queue depth > 2x number of disks, any disk errors.

**Q7: What happens when a Linux server runs out of inodes?**

**Answer:** Even with free disk space, no new files can be created. Symptoms: "No space left on device" error even when `df -h` shows free space. Cause: millions of small files (cached responses, temp files, Docker overlay layers). Fix: find directories with millions of files (`for d in /*; do echo "$d: $(ls $d | wc -l)"; done`), delete unnecessary files. Prevention: use tmpfs with inode limits, set per-directory file limits, monitor inode usage alongside disk space.

**Q8: How do auto-scaling storage and disk space monitoring interact?**

**Answer:** Cloud providers (AWS EBS, Azure managed disks, GCP persistent disks) support auto-scaling: grow disk when utilization exceeds threshold. However: 1) Growing disk doesn't help with inode exhaustion. 2) File system must be resized after volume grows (automate with startup script). 3) There's an upper limit per volume type. 4) Cost grows linearly. Monitoring should still track growth rates to plan for limits. Auto-scaling is a safety net, not a replacement for capacity planning.

**Q9: Tell me about a disk space incident you resolved. (STAR)**

**Answer:** Situation: Production database server ran out of disk space at 2 AM, causing all transactions to fail. Task: Restore database write capability. Action: I SSH'd in and found /var was 100% full. `du -sh /var/log/*` showed a 60GB application log file from a verbose debug logging config accidentally deployed. I truncated the log, rotated aggressively, and freed 70GB. The database resumed operations immediately. I then fixed the logging config, added log rate limiting, and set up disk monitoring with 80% warning and 90% critical alerts. Result: Database recovered in 5 minutes. No recurrence after adding centralized logging (logs shipped off-instance).

**Q10: How would you design a log management strategy that prevents disk space incidents?**

**Answer:** 1) Centralized logging: ship logs to ELK/Splunk/Datadog immediately. 2) Local log retention: max 1 day, rotated hourly. 3) Log rate limiting: per-source rate limits (max 1000 lines/sec per source). 4) Log size limits: max 100MB per file, max 1GB total per service. 5) Compression: gzip rotated logs. 6) Monitoring: track log generation rate per source, alert on anomalies. 7) Auto cleanup: cron job deletes logs > 24 hours. 8) Spill protection: if shipping fails, drop local logs rather than filling disk.
