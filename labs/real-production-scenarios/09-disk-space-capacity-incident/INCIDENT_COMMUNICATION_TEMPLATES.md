# Lab 09 — Disk Space: Communication Templates

## Initial Alert

```
Title: [SEV1] Disk Full — Database Server /var at 100%
Service: Database Server (MySQL)
Severity: SEV1

Metrics:
- /var: 100% full (0 bytes free)
- /data: 65% full
- Database: ALL transactions failing (no WAL space)
- Application: All write operations failing — read-only mode

Impact: No new orders, no updates to existing orders
Cause: Application log file grew to 60GB (debug logging enabled accidentally)
```

## Status Updates

```
STATUS #1 — Emergency Cleanup In Progress

/var cleared: truncated 60GB log file, rotated logs.
Freed: 72GB (restored to 28% utilization).
Database resumed writes at 14:35 UTC.

Fix deployed: log config reverted, centralized log shipping enabled,
log rate limiting added.

Post-mortem: Debug logging was enabled in the deployment config.
Added: deployment config validation, log size monitoring per file.
```
