# Lab 14 — API Rate Limiting: Communication Templates

## Initial Alert

```
Title: [SEV2] Rate Limiting False Positives — Legitimate Users Receiving 429
Service: Public API Gateway
Severity: SEV2

Metrics:
- 429 rate: 15% (normal: < 1%)
- Customer complaints: 50+ in last 10 min
- Affected: Users on shared IPs (NAT/office networks)
- Cause: Rate limit by IP with low threshold

Impact: Legitimate users blocked from API
```

## Status Updates

```
STATUS #1 — Rate Limit Investigation

Root cause: IP-based rate limiting at 100 req/min is too strict for
office networks where 50+ users share one public IP.

Actions:
- Increased IP rate limit from 100 to 1000 req/min (immediate fix)
- Prioritizing API key-based rate limiting over IP-based
- Adding authenticated user rate limits (per-key instead of per-IP)

429 rate dropping: 15% → 2% → targeted < 1%
```

```
STATUS #2 — Resolved

Rate limit strategy updated:
1. Authenticated users: per-API-key limits (10K req/min)
2. Unauthenticated: per-IP limits (100 req/min) with burst allowance
3. Shared IP detection: known office IPs get higher limits

429 rate: 0.5% (all legitimate — only actual abusive traffic blocked)
Post-mortem: rate limit testing added for shared IP scenarios.
```
