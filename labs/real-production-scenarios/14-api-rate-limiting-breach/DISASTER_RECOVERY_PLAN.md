# Lab 14 — API Rate Limiting: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| RTO | 2 minutes (adjust rate limit configuration) |
| RPO | 0 (no data loss from rate limiting) |
| MTD | 5 minutes before legitimate users are impacted |

## Scenarios

### Scenario A: False Positives (Legitimate Users Blocked)

**Trigger:** Customer complaints of 429 errors on normal usage
**Recovery:**
1. Temporarily increase rate limit for affected client group (2x or 10x)
2. Switch from IP-based to API-key-based rate limiting
3. Investigate: is it a shared IP, burst traffic, or misconfigured limit?
4. Adjust limits permanently based on investigation
5. Add monitoring for false positive rate

### Scenario B: Rate Limiter Overloaded

**Trigger:** Rate limiter itself becomes bottleneck (high CPU, high latency)
**Recovery:**
1. Bypass rate limiter temporarily (emergency mode)
2. Use local (in-memory) rate limiting instead of distributed (Redis)
3. If still overloaded: use CDN-level rate limiting (Cloudflare, AWS WAF)
4. Scale up rate limiter infrastructure
5. Optimize rate limiting data structure

### Scenario C: Rate Limit Evasion (Attacker)

**Trigger:** Attacker rotates through many IPs/API keys to bypass limits
**Recovery:**
1. Identify evasion pattern (same user-agent, same behavior)
2. Add behavioral rate limits (per session, per action, per time)
3. Implement CAPTCHA for suspicious patterns
4. Add IP range blocking
5. Report to DDoS protection service

## Runbook

```yaml
symptoms:
  - "Legitimate users receiving 429"
  - "Rate limiter latency > 10ms"
  - "Attacker bypassing IP-based limits"

immediate:
  - "Increase limits temporarily (2-10x)"
  - "Switch from IP to API key based limits"
  - "Enable CDN/WAF rate limiting as backup"

diagnosis:
  - "Check rate limiter logs: blocked client IDs"
  - "Are blocked clients using shared IPs?"
  - "Are blocked clients exceeding legitimate usage?"
  - "Is rate limiter itself healthy (latency, throughput)?"

fix:
  - "Implement tiered rate limiting (per-IP, per-key, per-user)"
  - "Add burst allowance for legitimate traffic spikes"
  - "Add monitoring for false positive rate"
  - "CDN-level rate limiting as first line of defense"
```
