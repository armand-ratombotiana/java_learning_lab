# Incident Report: SSL/TLS Certificate Expiry

## Incident ID: INC-2026-0711-SSL
## Date: July 11, 2026
## Severity: SEV1 (Critical)

## Timeline (All times UTC)

### 09:14 — Certificate Expiry
- The Let's Encrypt wildcard certificate `*.acmecorp.com` reaches its `notAfter` date: 2026-07-11T09:14:00Z
- All Cloudflare edge PoPs begin rejecting HTTPS connections with `CERTIFICATE_EXPIRED` errors
- Mobile apps using certificate pinning immediately fail with `SSLHandshakeException`
- Internal monitoring systems show no alerts — the `blackbox_exporter` SSL expiry check was never configured

### 09:18 — First User Reports
- Customer support receives first 3 tickets: "Website showing 'Your connection is not private'"
- Support team escalates to Level 2 (no automated alert routing exists for SSL errors)

### 09:27 — Internal Detection
- SRE team member manually checks the website after a Slack message from a colleague
- `curl -v https://api.acmecorp.com` returns: `curl: (60) SSL certificate problem: certificate has expired`
- SRE checks the certificate: `openssl s_client -connect api.acmecorp.com:443 -servername api.acmecorp.com 2>&1 | openssl x509 -noout -dates`
- Output confirms: `notAfter=Jul 11 09:14:00 2026 GMT`

### 09:34 — Incident Declaration
- Incident declared as SEV1 via PagerDuty
- Primary and secondary on-call SREs engaged
- War room established in Slack channel #inc-ssl-expiry
- Cloudflare engineering team notified via partner support channel

### 09:41 — Attempted Renewal (FAILED)
- SRE attempts to issue new certificate via certbot:
  ```
  certbot certonly --dns-cloudflare --dns-cloudflare-credentials ~/.cloudflare/credentials -d *.acmecorp.com -d acmecorp.com
  ```
- Let's Encrypt returns: `too many certificates already issued for acmecorp.com`
- Rate limit check: `https://acme-v02.api.letsencrypt.org/acme/rate-limit-headers` shows 50/50 certificates issued this week
- Root cause of rate limit exhaustion: 3 monitoring engineers had issued test certificates during load testing earlier in the week

### 10:02 — Alternative Path Identified
- Cloudflare support recommends using Cloudflare Origin CA certificate (not subject to Let's Encrypt rate limits)
- Cloudflare Origin CA root is cross-signed by Google Trust Services and is trusted by all major browsers
- SRE generates Origin CA certificate via Cloudflare Dashboard

### 10:11 — Origin Certificate Issued
- Cloudflare Origin CA certificate issued for `*.acmecorp.com` and `acmecorp.com`
- Certificate validity: 15 years (Cloudflare Origin CA)
- Private key generated and stored securely

### 10:23 — Certificate Installation
- Origin certificate and private key installed on internal NGINX ingress controller
- Configuration updated in `/etc/nginx/sites-enabled/acmecorp-ssl.conf`
- NGINX reloaded: `nginx -s reload`
- Verification:
  ```
  openssl s_client -connect localhost:443 -servername api.acmecorp.com 2>&1 | grep "verify error"
  ```

### 10:35 — Cloudflare SSL Mode Issue
- Cloudflare is configured with "Full (Strict)" SSL mode
- Origin CA certificate does not match the private key used by Cloudflare for the edge certificate
- Cloudflare edge rejects the origin connection: `526 Invalid SSL certificate`
- Root cause: Cloudflare edge uses a Cloudflare-managed edge certificate; the origin pull requires a separate Cloudflare Origin CA certificate
- SRE temporarily downgrades Cloudflare SSL mode to "Full" (accepts any origin cert, including self-signed)

### 10:56 — HTTPS Restored (Partial)
- HTTPS traffic resumes through Cloudflare
- Cloudflare edge uses its own Universal SSL certificate for client-facing connections
- Internal origin connections use "Full" mode (encrypted but not validated)
- Traffic restored for 87% of users; remaining 13% have DNS caching issues

### 11:14 — DNS Cache TTL Issues
- Users with aggressive DNS caching still see expired certificate from cached DNS records
- Cloudflare PoPs with stale DNS cache serve old edge certificate
- Mobile apps with certificate pinning still failing (pinned to Let's Encrypt ISRG Root X1)
- Workaround: Users instructed to clear DNS cache and app data

### 11:42 — Certificate Pinning Resolution
- Engineering team identifies 4 mobile apps with certificate pinning
- Emergency app update triggered via app store expedited review process
- Apps pinned to Let's Encrypt ISRG Root X1 — Cloudflare edge uses different CA
- Hotfix release submitted to Apple App Store and Google Play Store

### 12:18 — Monitoring Configured
- Prometheus `blackbox_exporter` configured with SSL certificate expiry checks:
  ```yaml
  modules:
    http_2xx:
      prober: http
      http:
        valid_ssl: true
        fail_if_not_ssl: true
  ```
- Alert rule created:
  ```yaml
  - alert: SSLCertExpiringSoon
    expr: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 2592000  # 30 days
    severity: critical
  ```
- PagerDuty integration tested with intentionally expired certificate

### 12:31 — Automated Renewal Setup
- certbot systemd timer configured for automatic renewal:
  ```
  [Timer]
  OnCalendar=daily
  Persistent=true
  ```
- Renewal hook configured for NGINX reload
- Test renewal executed: `certbot renew --dry-run`

### 12:56 — Incident Resolved
- All production domains serving valid HTTPS certificates
- Monitoring alerts configured and active
- Mobile app updates submitted (pending review)
- Certificate pinning update planned for next sprint
- War room de-escalated to post-mortem

## Post-Incident Actions

1. Configure Prometheus blackbox_exporter SSL alerts for all 47 production domains
2. Implement automated certbot renewal with systemd timer
3. Create secondary certificates for critical subdomains (api, www, app)
4. Add SSL certificate expiry check to on-call runbook
5. Update Cloudflare SSL mode to "Full (Strict)" with valid Origin CA
6. Review and update certificate pinning strategy for mobile apps
7. Implement Certificate Transparency log monitoring
8. Create rate limit awareness documentation for engineering team

## Detailed Timeline Analysis

### Phase 1: Latent Failure (Months Leading Up to Incident)

| Date | Event | Responsible Party | Notes |
|------|-------|-------------------|-------|
| January 2026 | Ticket OPS-4189 opened: "Configure blackbox_exporter SSL checks" | Monitoring Team | Deprioritized |
| February 2026 | blackbox_exporter deployed without SSL module | DevOps | Known gap |
| March 2026 | Certificate renewal succeeded (manual — engineer checked email) | On-call SRE | Lucky catch |
| April 12, 2026 | Current certificate issued (valid 90 days) | DevOps | No auto-renewal |
| May 2026 | Renewal process removed from onboarding checklist | SRE Lead | Process gap |
| June 2026 | Monitoring team reorg — SSL ticket reassigned | Management | Lost context |
| July 4-8, 2026 | Holiday week — shared inbox not monitored | Team | No coverage |
| July 8-9, 2026 | Load testing — 3 engineers issued test certs | QA Team | Rate limit exhaustion |
| July 10, 2026 | Last day certificate was valid without alert | N/A | No monitoring |

### Phase 2: Active Failure (Incident Duration 09:14 — 12:56)

| Time | Elapsed | Event | Actor | System |
|------|---------|-------|-------|--------|
| 09:14 | 0:00 | Certificate `notAfter` date reached | Let's Encrypt | Certificate expires |
| 09:18 | 0:04 | First 3 support tickets received | Users | Support system |
| 09:22 | 0:08 | Social media mentions begin (17 tweets) | Users | Twitter/X |
| 09:27 | 0:13 | SRE manually detects via curl | SRE | CLI tool |
| 09:34 | 0:20 | SEV1 incident declared | SRE Lead | PagerDuty |
| 09:41 | 0:27 | certbot renewal fails (rate limit) | SRE | Let's Encrypt ACME |
| 09:45 | 0:31 | Cloudflare support contacted | SRE | Partner channel |
| 09:50 | 0:36 | Rate limit exhaustion confirmed | SRE | ACME headers |
| 10:02 | 0:48 | Origin CA solution identified | Cloudflare | Documentation |
| 10:11 | 0:57 | Origin CA certificate generated | SRE | Cloudflare API |
| 10:23 | 1:09 | Certificate installed on NGINX | SRE | SSH/NGINX |
| 10:35 | 1:21 | SSL mode issue discovered | SRE | Testing |
| 10:56 | 1:42 | SSL mode downgraded to "Full" | SRE | Cloudflare config |
| 11:14 | 2:00 | DNS cache issues identified | SRE | Analysis |
| 11:42 | 2:28 | Mobile pinning workaround | Mobile Team | App stores |
| 12:18 | 3:04 | blackbox_exporter configured | SRE | Prometheus |
| 12:31 | 3:17 | certbot timer configured | SRE | systemd |
| 12:56 | 3:42 | Incident resolved | All | Verification |

### Phase 3: Recovery and Post-Incident (After 12:56)

| Time | Event | Actor |
|------|-------|-------|
| 13:15 | PagerDuty incident acknowledged as resolved | SRE |
| 14:00 | Initial incident report drafted | Incident Commander |
| 15:30 | War room de-escalated | All |
| Day+1 | Full post-mortem meeting scheduled | SRE Lead |
| Day+2 | Root cause analysis completed | SRE Team |
| Day+3 | Preventive measures implementation begins | DevOps |
| Week+1 | All blackbox_exporter checks deployed | Monitoring Team |
| Week+2 | certbot renewal verified operational | SRE |
| Month+1 | Multi-CA strategy approved | Engineering Leadership |

## Verified Metrics

### Certificate Validation Chain

The expired certificate had the following chain:
```
Leaf: CN = *.acmecorp.com
  Issuer: R3 (Let's Encrypt)
  Serial: 04:AB:12:CD:34:EF:56:78:90
  Validity: Apr 12 09:14:00 2026 GMT — Jul 11 09:14:00 2026 GMT
  Subject: CN = *.acmecorp.com
  SAN: *.acmecorp.com, acmecorp.com
  Key: ECDSA P-256
  Fingerprint SHA256: AB:CD:EF:12:34:56:78:90:AB:CD:EF:12:34:56:78:90

Intermediate: CN = R3 (Let's Encrypt)
  Issuer: ISRG Root X1

Root: CN = ISRG Root X1
  Issuer: ISRG Root X1 (Self-signed)
```

### OpenSSL Verification Output

```
$ openssl s_client -connect api.acmecorp.com:443 -servername api.acmecorp.com 2>&1 | \
  openssl x509 -noout -subject -issuer -dates -checkend 0

subject=CN = *.acmecorp.com
issuer=C = US, O = Let's Encrypt, CN = R3
notBefore=Apr 12 09:14:00 2026 GMT
notAfter=Jul 11 09:14:00 2026 GMT
Certificate will expire
```

### Let's Encrypt Rate Limit Headers

```
$ curl -sI https://acme-v02.api.letsencrypt.org/acme/new-order
HTTP/2 405
Rephrase-RateLimit-Limit: 50
Rephrase-RateLimit-Reset: 2026-07-14T00:00:00Z
Rephrase-Certificates-Issued: 50
Boulder-Requester: 12345678
```

### Prometheus Alert Rule Testing

After configuration, the following alert was successfully tested by creating an intentionally expired certificate:

```
Alert: SSLCertExpired
Expression: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 0
Value: -86400 (24 hours expired)
Severity: critical
Status: FIRING
```

## Incident Cost Breakdown

| Cost Category | Amount | Description |
|-------------|--------|-------------|
| Lost transactions | $471,000 | 3h 42m at $127k/hour |
| SLA penalties | $45,000 | 3 enterprise clients |
| Customer support surge | $28,000 | 15 additional agents |
| Engineering overtime | $18,000 | 14 engineers × 4h × $320/h |
| PR/communications | $15,000 | Crisis PR firm engagement |
| Compliance review | $12,000 | PCI DSS audit costs |
| Software/tooling | $5,000 | Additional monitoring licenses |
| **Total** | **$594,000** | |

## Impact on Other Teams

| Team | Impact | Recovery Action |
|------|--------|----------------|
| Customer Support | 2,847 tickets received | Surge staffing, auto-responses |
| Marketing | SEO traffic dropped 40% | Google Search Console re-verification |
| Legal | Compliance notification required | Regulatory filing |
| Finance | Revenue reconciliation | Manual transaction matching |
| Mobile | App store ratings dropped (3.8→2.1) | Emergency update + response |

## Blameless Post-Mortem Culture

This incident was caused by systemic gaps in monitoring and process automation, not individual negligence. The team has committed to implementing the preventive measures documented in PREVENTION.md and MONITORING.md.
