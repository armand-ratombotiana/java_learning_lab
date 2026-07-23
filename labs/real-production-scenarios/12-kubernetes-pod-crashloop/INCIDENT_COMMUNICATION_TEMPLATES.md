# Lab 12 — Kubernetes Pod CrashLoop: Communication Templates

## Initial Alert

```
Title: [SEV2] Pod CrashLoopBackOff — Payment Service
Service: Payment Processing (Kubernetes)
Severity: SEV2

Metrics:
- 8/12 pods in CrashLoopBackOff
- Exit code: 137 (OOMKilled)
- Memory limit: 256Mi
- Memory usage: ~300Mi (exceeds limit)
- Restart rate: 12 restarts in 5 minutes

Impact: Payment processing degraded (4/12 pods available)
```

## Status Updates

```
STATUS #1 — CrashLoop Investigation

Exit code 137 = OOMKilled.
Memory limit 256Mi is too low for this service.
Memory usage growing from 150Mi → 300Mi before crash (possible leak).

Actions:
- Increased memory limit to 512Mi (temporary fix)
- Capturing heap dump before OOM for analysis
- Checking for memory leak (recent deployment?)

Pods recovering with increased limit.
```

```
STATUS #2 — Resolved

Memory limit increased to 512Mi.
All 12 pods healthy and running.
Root cause: New feature added in-memory cache without size limits.
Fix: Added cache size bound (max 1000 entries) + TTL-based eviction.
Memory usage stabilized at 180Mi.
Added: memory usage trend monitoring, OOM alert.
```
