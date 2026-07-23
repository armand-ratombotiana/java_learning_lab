# Lab 06 — Deployment Rollback: Communication Templates

## Initial Alert

```
Title: [SEV1] Deployment Failed — Error Rate Spike on Payment Service
Service: Payment Processing
Severity: SEV1

Deployment details:
- Version: v2.4.1-rc3 deployed at 14:22 UTC
- Error rate: 35% (baseline: 0.1%)
- P99 latency: 5s (baseline: 200ms)
- Deployment: 50% rolled out (canary)

Impact: Payment failures for 50% of traffic in us-east-1
Action: Rollback initiated
```

## Status Updates

### Rollback Initiated

```
STATUS #1 — Rollback Initiated

Deployment v2.4.1-rc3 rolled back to v2.4.0.
Canary instances replaced at 14:25 UTC.
Full rollback expected: 5 minutes.

Root cause (preliminary): Database schema mismatch — new column not nullable
but existing rows have NULL values.
```

### Rollback Complete

```
STATUS #2 — Rollback Complete

v2.4.0 deployed to 100% of instances.
Error rate: 0.1% (baseline restored)
P99 latency: 200ms (normal)
All payment processing functional.

Post-mortem actions:
1. Fix migration to handle NULL values
2. Add pre-deployment schema validation
3. Expand canary testing to include edge cases
```
