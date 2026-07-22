# Root Cause Analysis: SSL/TLS Certificate Expiry

## RCA ID: RCA-2026-0711-001
## Severity: SEV1 — Critical Production Outage
## Duration: 3 hours 42 minutes
## Total Impact: ~$471,000 revenue loss + compliance penalties

## Executive Summary

On July 11, 2026, the Let's Encrypt wildcard TLS certificate for `*.acmecorp.com` expired at 09:14 UTC, causing a complete HTTPS outage affecting all production domains. The incident lasted 3 hours 42 minutes, impacted 2.3 million daily active users, and resulted in an estimated $471,000 in lost revenue. The root cause was a multi-layered failure of monitoring, process automation, and operational awareness.

## What Happened

The Let's Encrypt certificate `*.acmecorp.com` with serial number `04:AB:12:CD:34:EF:56:78:90` reached its `notAfter` date. Because no Prometheus `blackbox_exporter` alert was configured for SSL certificate expiry, the expiry was not detected until users reported HTTPS errors. When the SRE team attempted emergency renewal, Let's Encrypt rate limits had been exhausted by test certificates issued earlier in the week.

## Direct Cause

Let's Encrypt SSL/TLS certificate `*.acmecorp.com` expired at 09:14 UTC on July 11, 2026. The certificate had a validity period of 90 days and was issued on April 12, 2026. No automated renewal process was in place.

## The 5 Whys Analysis

### Why 1: Why did HTTPS traffic fail?
The Let's Encrypt certificate `*.acmecorp.com` expired at 09:14 UTC. All browsers and API clients rejected the expired certificate during TLS handshake. The `openssl s_client` command confirms:
```
depth=0 CN = *.acmecorp.com
verify error:num=10:certificate has expired
notAfter=Jul 11 09:14:00 2026 GMT
```

### Why 2: Why was the certificate not renewed before expiry?
The certificate renewal process was entirely manual. An engineer was expected to notice the Let's Encrypt expiry notification email and run `certbot renew` manually. The previous renewal (April 12, 2026) succeeded because the on-call engineer happened to check their email. This time, the expiry notification email was sent to a shared team inbox that was not monitored during the week of July 4 (US holiday week).

**Evidence**: Git log shows the last certificate renewal commit was April 12, 2026. No automated renewal cron job or systemd timer was configured on any of the 3 production NGINX hosts.

### Why 3: Why was there no automated monitoring to detect certificate expiry?
The Prometheus `blackbox_exporter` was deployed in the monitoring stack but was never configured to perform SSL certificate checks. The `blackbox_exporter` configuration file `/etc/prometheus/blackbox.yml` only contained HTTP health check modules — no SSL/TLS validation modules.

**Evidence**: 
```yaml
# Current blackbox_exporter config (missing SSL checks)
modules:
  http_2xx:
    prober: http
    http:
      valid_http_versions: ["HTTP/1.1", "HTTP/2"]
  # No ssl_certificate_expiry module configured
```

The monitoring team had a backlog item to configure SSL expiry checks (ticket OPS-4189, opened January 2026) but it was deprioritized for 6 months.

### Why 4: Why was the blackbox_exporter SSL verification not configured?

**Organizational Root Cause**: The SRE team had no defined standard for TLS certificate monitoring. There was no:
- Service Level Objective (SLO) for certificate validity
- Required monitoring configuration checklist for production domains
- Automated compliance check to verify certificates are monitored
- Regular audit of monitoring coverage gaps

The team operated under the assumption that "certificates last 90 days so there's plenty of time to notice." This assumption was incorrect.

**Second contributing factor**: When the `blackbox_exporter` was deployed in February 2026, the documentation provided by the monitoring team (vendor-managed) included only HTTP health check examples. The SSL certificate expiry feature was documented on the Prometheus website but was not included in the internal deployment guide.

### Why 5: Why did emergency renewal fail?

When the SRE team attempted to issue a new certificate at 09:41 UTC, Let's Encrypt returned a rate limit error:
```
too many certificates already issued for acmecorp.com
```

**Evidence**: 
```
$ curl -I https://acme-v02.api.letsencrypt.org/acme/new-order 2>&1 | grep -i rate
Rephrase-Rate-Limit: 50
Rephrase-Rate-Limit-Reset: 2026-07-14T00:00:00Z
```

The team had issued 50 certificates in the current 7-day window. Investigation revealed that 3 monitoring engineers had independently issued test certificates during a load testing exercise on July 8-9, 2026. Each engineer used their own certbot invocation, unaware that Let's Encrypt counts all certificates against the same registered domain limit. No one checked the rate limit status before starting.

**Systemic Root Cause**: The organization had:
1. No monitoring for certificate expiry dates (no blackbox_exporter SSL probes)
2. No automated renewal process (no certbot cron/systemd timer)
3. No awareness of Let's Encrypt rate limits (no documentation or training)
4. No fallback certificate strategy (single wildcard certificate, no Cloudflare Origin CA)
5. No tested emergency renewal procedure (no runbook for certificate renewal failure)

## Contributing Factors

1. **Holiday week**: The expiry occurred during US Independence Day holiday week when staffing was reduced
2. **No secondary DNS validation**: Certificate Transparency logs were not monitored for upcoming expiries
3. **Cloudflare "Full (Strict)" mode**: Required valid origin certificate, preventing graceful degradation
4. **Certificate pinning**: Mobile apps pinned to Let's Encrypt root CA, preventing automatic trust of Cloudflare edge certificates
5. **Rate limit unawareness**: No team member knew about Let's Encrypt's 50 certs/week/domain limit
6. **No certificate lifecycle management**: No CMDB or inventory tracking certificate expiry dates

## Verification

The root cause was verified by:
1. `openssl s_client` output showing certificate expiry
2. Let's Encrypt rate limit headers confirming exhaustion
3. Git log showing no automated renewal configuration
4. Prometheus configuration showing no SSL expiry alerts
5. Cloudflare dashboard showing "Full (Strict)" SSL mode requiring valid origin cert
6. Mobile app source code confirming certificate pinning configuration

## Organizational Failure Analysis

### Root Cause Category

This incident falls into the "Process Gap" and "Monitoring Gap" categories per the Google SRE incident taxonomy:

```
Primary Root Cause: Missing Monitoring Configuration
  └── No blackbox_exporter SSL expiry alerts configured (known gap, 6 months old)
Secondary Root Cause: Incomplete Process Automation
  └── No automated certificate renewal (manual process only)
Tertiary Root Cause: Knowledge Gap
  └── Let's Encrypt rate limits unknown to team
Quaternary Root Cause: No Redundancy
  └── Single certificate authority, no backup CA
Quinary Root Cause: Organizational Prioritization
  └── Certificate lifecycle management deprioritized for features
```

### Defect Prevention Analysis

Using the defect prevention framework, the following controls were missing or ineffective:

| Control Layer | Control | Status | Gap |
|--------------|---------|--------|-----|
| Prevention | Automated renewal | Missing | No systemd timer |
| Prevention | Rate limit monitoring | Missing | No pre-renewal check |
| Detection | SSL expiry monitoring | Missing | No blackbox_exporter |
| Detection | Certificate inventory | Missing | No CMDB tracking |
| Detection | CT log monitoring | Missing | No crt.sh scanning |
| Response | Emergency renewal runbook | Stale | 14 months old |
| Response | Multi-CA fallback | Missing | Single CA dependency |
| Recovery | CDN fallback | Missing | No Flexible SSL |
| Recovery | Graceful degradation | Missing | No partial HTTPS |

### Timeline of Missed Opportunities

| Date | Opportunity | Action Taken | Outcome |
|------|-------------|-------------|---------|
| Jan 2026 | blackbox_exporter SSL ticket created | Deprioritized | Gap persisted |
| Feb 2026 | blackbox_exporter deployed | SSL module not configured | Gap persisted |
| Mar 2026 | Previous renewal succeeded | Lucky catch, no action | No process change |
| Apr 2026 | New certificate issued | Manual renewal | No automation |
| May 2026 | Onboarding checklist updated | Renewal step removed | Process degraded |
| Jun 2026 | Team reorg | SSL ticket lost | Gap forgotten |
| Jul 4 | Holiday week | No coverage | Monitoring gap |
| Jul 8 | Load testing | 3 test certificates issued | Rate limit exhausted |
| Jul 11 09:14 | Certificate expires | No detection | Incident starts |

### Cost-Benefit Analysis of Prevention

| Prevention Measure | Estimated Cost | Cost if Implemented Earlier | Savings |
|-------------------|---------------|----------------------------|---------|
| blackbox_exporter SSL monitoring | 2 engineering hours ($640) | $640 | $593,360 |
| certbot systemd timer | 1 engineering hour ($320) | $320 | $593,680 |
| Cloudflare Origin CA backup | 30 minutes ($160) | $160 | $593,840 |
| Rate limit monitoring | 4 hours ($1,280) | $1,280 | $592,720 |
| Multi-CA strategy documentation | 8 hours ($2,560) | $2,560 | $591,440 |
| **Total prevention cost** | **~$5,000** | **~$5,000** | **$589,000+** |

The entire incident — costing $594,000 — could have been prevented by approximately $5,000 of proactive engineering work.

### Verification of Root Cause Analysis

The RCA was verified through the following methods:

1. **Direct observation**: OpenSSL s_client output confirmed certificate expiry
2. **Log analysis**: certbot logs showed no prior renewal attempts
3. **Configuration audit**: Prometheus config showed no SSL expiry module
4. **Process audit**: On-call runbook showed manual renewal task
5. **Rate limit audit**: Let's Encrypt ACME headers confirmed rate limit exhaustion
6. **Git log analysis**: No certificate renewal commits in 90 days
7. **Calendar review**: 3 cancelled DR drills, 6 deprioritized monitoring tickets
8. **Interview**: Team members confirmed awareness of the gap

### System Dynamics Analysis

The incident followed a classic "drift to failure" pattern:

```
Initial State: Manual renewal working (lucky catches)
    ↓
First Process Gap: Renewal step removed from checklist
    ↓
Second Process Gap: SSL monitoring deprioritized
    ↓
Third Process Gap: Certificate lifecycle not documented
    ↓
Trigger Event: Certificate expires
    ↓
First Barrier Failure: No monitoring detects expiry
    ↓
Second Barrier Failure: Manual renewal not performed
    ↓
Third Barrier Failure: Emergency renewal fails (rate limit)
    ↓
Fourth Barrier Failure: Single CA — no fallback
    ↓
Incident: 3h 42m HTTPS outage
```

### Comparison with Industry Best Practices

| Practice | Industry Standard | Our State (Before) | Our State (After) |
|----------|-----------------|-------------------|-------------------|
| Auto-renewal | Required | Not implemented | certbot systemd timer |
| Expiry monitoring | 30-day warning | Not configured | blackbox_exporter |
| Multi-CA | At least 2 CAs | Single CA | Let's Encrypt + Cloudflare |
| Rate limit awareness | Documented limits | Unknown | Rate limit monitoring |
| CT log monitoring | Weekly scan | Not configured | Daily crt.sh scan |
| Certificate inventory | CMDB tracked | Not tracked | PostgreSQL inventory |
| Monthly certificate review | Standard practice | Not performed | Automated reporting |

### Detailed 5 Whys with Evidence

Each Why is supported by specific evidence from the incident:

**Why 1: HTTPS traffic failed**
- Evidence: `openssl s_client -connect api.acmecorp.com:443` returned `verify error:num=10:certificate has expired`
- Console output captured at 09:27 UTC
- Certificate serial number 04:AB:12:CD:34:EF:56:78:90 had notAfter=Jul 11 09:14:00 2026 GMT

**Why 2: Certificate not renewed**
- Evidence: No certbot cron/systemd timer configured on any of 3 production NGINX hosts
- `ls -la /etc/cron.d/certbot` → file not found
- `systemctl list-timers | grep certbot` → no certbot timers
- Last renewal: April 12, 2026 (manual)
- Renewal email sent to shared team inbox, not monitored during holiday week

**Why 3: No monitoring**
- Evidence: Prometheus blackbox_exporter config at `/etc/prometheus/blackbox.yml` contained only HTTP health check modules
- No `ssl_certificate_expiry` or `valid_ssl: true` configuration
- Grafana dashboard query: `probe_ssl_earliest_cert_expiry` returned no data (metric did not exist)
- Ticket OPS-4189 (opened January 2026) labeled "SSL expiry monitoring" was in "Backlog" status

**Why 4: Why was monitoring not configured?**
- Evidence: No defined SLO for certificate validity
- No required monitoring configuration checklist for production domains
- Internal deployment guide (vendor-managed) included only HTTP health check examples
- SSL certificate expiry monitoring was documented on Prometheus website but not in internal docs

**Why 5: Why did emergency renewal fail?**
- Evidence: Let's Encrypt ACME API returned `too many certificates already issued`
- Rate limit headers confirmed: 50/50 certificates issued in current 7-day window
- Reset date: 2026-07-14T00:00:00Z (3 days from incident)
- 3 monitoring engineers issued 17 test certificates during July 8-9 load testing

## Recommendations Matrix

| Priority | Recommendation | Effort | Impact | Owner | Timeline |
|----------|---------------|--------|--------|-------|----------|
| P0 | Configure blackbox_exporter SSL alerts | 2h | Critical | SRE | Week 1 |
| P0 | Implement certbot systemd timer | 1h | Critical | DevOps | Week 1 |
| P0 | Create Cloudflare Origin CA backup | 1h | Critical | Cloud Infra | Week 1 |
| P1 | Document multi-CA strategy | 4h | High | Security | Week 2 |
| P1 | Review certificate pinning | 8h | High | Mobile Team | Week 3 |
| P1 | Create certificate inventory | 16h | High | DevOps | Week 4 |
| P2 | Implement rate limit monitoring | 8h | Medium | SRE | Month 2 |
| P2 | Automate CT log monitoring | 8h | Medium | SRE | Month 2 |
| P2 | Monthly certificate drill | 4h/month | Medium | SRE | Month 3 |
| P3 | Multi-region certificate management | 40h | Low | Cloud Infra | Quarter 3 |

## Lessons for the Industry

1. **SSL certificates are critical infrastructure**: Treat them like passwords — automate renewal, monitor expiry, and have backup plans.
2. **Rate limits are a real constraint**: Let's Encrypt's 50 certificates/week/domain limit can be exhausted. Test your emergency renewal process.
3. **Monitoring must cover all failure modes**: HTTP health checks alone are insufficient. You must explicitly monitor certificate expiry dates.
4. **Manual processes fail under pressure**: The holiday week, reduced staffing, and rushed emergency all contributed to the failure. Automate everything.
5. **One CA is not enough**: If your primary CA has an outage or rate limit, you need an alternative. Cloudflare Origin CA, Google Trust Services, or a commercial CA can serve as backup.
6. **Know your certificate blast radius**: A single wildcard certificate for *.acmecorp.com meant one expiry took down ALL subdomains. Consider separate certificates for critical subdomains.

## Recommendations Summary

1. **Immediate**: Configure blackbox_exporter SSL expiry alerts (30-day warning)
2. **Immediate**: Implement automated certbot renewal with systemd timer
3. **Short-term**: Create Cloudflare Origin CA backup certificates
4. **Short-term**: Review and update certificate pinning strategy
5. **Medium-term**: Implement certificate lifecycle management with expiry tracking
6. **Long-term**: Adopt ACME protocol for fully automated certificate management
7. **Long-term**: Implement multi-CA strategy (Let's Encrypt + Cloudflare + Google Trust Services)
