# Incident Response Runbook Checklist: SSL/TLS Certificate Expiry

## Incident ID: ____________________
## Date: ____________________
## Responder: ____________________

## Severity Classification

- [ ] **P0/SEV1**: HTTPS completely down, revenue-impacting
- [ ] **P1/SEV2**: Partial HTTPS failure, non-critical subdomains
- [ ] **P2/SEV3**: Certificate expiring within 7 days (preventive)

## Immediate Response (First 5 Minutes)

### Detection
- [ ] Verify certificate expiry:
  ```bash
  openssl s_client -connect <domain>:443 -servername <domain> 2>&1 | openssl x509 -noout -dates
  ```
- [ ] Check all affected domains (www, api, app, admin)
- [ ] Verify Cloudflare edge status: https://www.cloudflarestatus.com/
- [ ] Check Let's Encrypt status: https://letsencrypt.status.io/
- [ ] Confirm incident scope — wildcard or specific subdomain?

### Declaration
- [ ] Declare incident in PagerDuty (SEV1 if revenue-impacting)
- [ ] Create Slack channel: #inc-<incident-short-name>
- [ ] Post initial situation report:
  ```
  INC-XXXX | SEV1 | SSL certificate expired
  Domains affected: [list]
  Started at: [time]
  Impact: [all HTTPS traffic / partial]
  ```
- [ ] Notify SRE team lead
- [ ] Notify security engineering

## Assessment (5-15 Minutes)

### Certificate Analysis
- [ ] Check certificate chain:
  ```bash
  openssl s_client -connect <domain>:443 -servername <domain> 2>&1
  ```
- [ ] Verify OCSP status:
  ```bash
  openssl ocsp -url <ocsp_url> -issuer /path/to/issuer.pem -cert /path/to/cert.pem
  ```
- [ ] Check Certificate Transparency logs:
  ```bash
  curl -s "https://crt.sh/?q=%25.<domain>&output=json"
  ```
- [ ] Identify certificate serial number and issuer
- [ ] Check Cloudflare SSL mode (Full / Full Strict / Flexible)
- [ ] Verify mobile app certificate pinning status

### Rate Limit Assessment
- [ ] Check Let's Encrypt rate limits:
  ```bash
  curl -I https://acme-v02.api.letsencrypt.org/acme/new-order
  # Check: Rephrase-Certificates-Issued, Rephrase-Certificates-Allowed
  ```
- [ ] Check other CA rate limits (Google Trust, Microsoft)
- [ ] Identify any rate limit exhaustion

## Remediation (15-60 Minutes)

### Path A: Automated Renewal Available (Normal)
- [ ] Run certbot renewal:
  ```bash
  sudo certbot renew
  ```
- [ ] Verify new certificate:
  ```bash
  sudo certbot certificates
  ```
- [ ] Reload web server:
  ```bash
  sudo nginx -s reload
  ```
- [ ] Verify HTTPS:
  ```bash
  curl -vI https://<domain> 2>&1 | grep "SSL connection"
  ```

### Path B: Rate Limited — Use Cloudflare Origin CA
- [ ] Generate Cloudflare Origin CA certificate via dashboard or API
- [ ] Install certificate on origin server
- [ ] Update Cloudflare SSL mode if needed
- [ ] Verify Cloudflare edge-to-origin connection
- [ ] Test with curl from external location

### Path C: Certificate Pinning Issue
- [ ] Identify all pinned certificates in mobile apps
- [ ] Determine if pinning is to leaf, intermediate, or root
- [ ] Submit emergency app update to stores
- [ ] Consider removing pinning temporarily
- [ ] Document cert pinning SHA-256 fingerprints

### Path D: Full Outage — Emergency Workaround
- [ ] Switch Cloudflare SSL to "Flexible" (last resort)
- [ ] Generate self-signed certificate as absolute emergency
- [ ] Add security exception for internal traffic
- [ ] Test HTTPS from 3+ geographic locations
- [ ] Monitor for client-side errors

## Recovery (60-120 Minutes)

### Verification
- [ ] All production domains returning valid HTTPS:
  ```bash
  for domain in api.acmecorp.com www.acmecorp.com; do
    echo "Checking $domain..."
    curl -sI https://$domain | head -1
  done
  ```
- [ ] Mobile apps connecting successfully
- [ ] Internal microservices mTLS restored
- [ ] Cloudflare SSL mode restored to "Full (Strict)"
- [ ] Certificate pinning updated in mobile apps
- [ ] Monitoring alerts firing correctly
- [ ] PagerDuty incident acknowledged by all responders

### Monitoring Setup
- [ ] Configure Prometheus blackbox_exporter SSL checks:
  ```yaml
  modules:
    ssl_certificate_expiry:
      prober: http
      http:
        valid_ssl: true
  ```
- [ ] Add alert rules (30-day warning, 14-day critical)
- [ ] Verify alerts in Alertmanager
- [ ] Create Grafana dashboard for SSL expiry
- [ ] Test alert with intentionally-failing check

### Automation Setup
- [ ] Configure certbot systemd timer:
  ```bash
  sudo systemctl enable certbot-renewal.timer
  ```
- [ ] Add renewal hooks for web server reload
- [ ] Test dry-run renewal:
  ```bash
  sudo certbot renew --dry-run
  ```
- [ ] Verify renewal logs:
  ```bash
  journalctl -u certbot-renewal.service
  ```

## Post-Incident (120+ Minutes)

### Documentation
- [ ] Update incident report with timeline
- [ ] Document root cause using 5 Whys
- [ ] Update runbook with any new findings
- [ ] Add new monitoring checks to master checklist
- [ ] Create/update certificate inventory

### Certificate Lifecycle
- [ ] Create secondary certificates for critical subdomains
- [ ] Configure multi-CA fallback (Let's Encrypt, Cloudflare, Google Trust)
- [ ] Implement Certificate Transparency log monitoring
- [ ] Set up automated daily certificate health checks
- [ ] Add certificate expiry to weekly SRE review

### Process Improvements
- [ ] Schedule monthly certificate renewal drill
- [ ] Add SSL expiry check to on-call handoff checklist
- [ ] Update employee onboarding to include cert awareness
- [ ] Create rate limit awareness documentation
- [ ] Implement certificate inventory CMDB

## Long-Term Preventive Actions

### Architecture
- [ ] Move to automated ACME protocol for all certificates
- [ ] Implement multi-region certificate management
- [ ] Consider Cloudflare Universal SSL as backup
- [ ] Review certificate pinning strategy
- [ ] Implement mTLS certificate lifecycle management

### Monitoring Maturity
- [ ] Add synthetic monitoring from 3+ geographic regions
- [ ] Implement Certificate Transparency log monitoring
- [ ] Create automated certificate renewal success dashboard
- [ ] Add SSL/TLS version monitoring (TLS 1.2, 1.3 compliance)
- [ ] Configure OCSP stapling monitoring

### Governance
- [ ] Define SLO for certificate validity (target: 99.99% uptime)
- [ ] Create certificate renewal SLA (24 hours for automated, 1 hour for manual)
- [ ] Establish quarterly certificate audit
- [ ] Define escalation matrix for certificate issues
- [ ] Create vendor SLA requirements for CA providers

## Escalation Contacts

| Role | Name | Phone | Email |
|------|------|-------|-------|
| SRE Lead | | | |
| Security Lead | | | |
| Cloudflare TAM | | | |
| Let's Encrypt Support | | | |
| VP Engineering | | | |
| Legal/Compliance | | | |

## Lessons Learned

### What went well:
- _________________________________________________________________
- _________________________________________________________________
- _________________________________________________________________

### What went wrong:
- _________________________________________________________________
- _________________________________________________________________
- _________________________________________________________________

### What we will improve:
- _________________________________________________________________
- _________________________________________________________________
- _________________________________________________________________

## Sign-Off

- [ ] Incident report completed: ____________________
- [ ] Root cause analysis completed: ____________________
- [ ] All action items assigned: ____________________
- [ ] Post-mortem scheduled: ____________________
- [ ] Final approval by SRE Director: ____________________

---

*This checklist is based on Google SRE practices and Let's Encrypt operational guidance.*
