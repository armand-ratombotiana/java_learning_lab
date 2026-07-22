# Prevention: SSL/TLS Certificate Expiry

## Strategic Prevention Framework

This document outlines the multi-layered prevention strategy to ensure SSL/TLS certificate expiry never causes a production outage again. Based on lessons from Google SRE (Chapter 13 — Emergency Response), Let's Encrypt best practices, and Cloudflare documentation.

## Layer 1: Automated Certificate Renewal

### certbot Systemd Timer

```bash
# Install certbot with Cloudflare DNS plugin
sudo apt-get install certbot python3-certbot-dns-cloudflare

# Configure renewal
cat > /etc/letsencrypt/cli.ini << 'EOF'
rsa-key-size = 2048
email = sre-team@acmecorp.com
agree-tos = true
non-interactive = true
EOF

# Set up systemd timer (runs daily)
sudo systemctl enable --now certbot-renewal.timer
sudo systemctl start --now certbot-renewal.timer
```

### Renewal Hooks

```bash
# Pre-hook: Check rate limits before renewal
cat > /etc/letsencrypt/renewal-hooks/pre/check-rate-limit.sh << 'EOF'
#!/bin/bash
RATE_LIMIT=$(curl -sI https://acme-v02.api.letsencrypt.org/acme/new-order \
  | grep -i "Rephrase-Rate-Limit" \
  | awk '{print $2}')
if [ "$RATE_LIMIT" -lt 5 ]; then
  echo "WARNING: Let's Encrypt rate limit low ($RATE_LIMIT remaining)"
  # Notify Slack
  curl -X POST -H "Content-type: application/json" \
    --data "{\"text\":\"WARNING: Let's Encrypt rate limit low ($RATE_LIMIT remaining)\"}" \
    $SLACK_WEBHOOK_URL
fi
EOF

# Post-hook: Reload web server
cat > /etc/letsencrypt/renewal-hooks/post/reload-nginx.sh << 'EOF'
#!/bin/bash
systemctl reload nginx
echo "NGINX reloaded after certificate renewal"
EOF

chmod +x /etc/letsencrypt/renewal-hooks/pre/check-rate-limit.sh
chmod +x /etc/letsencrypt/renewal-hooks/post/reload-nginx.sh
```

## Layer 2: Prometheus Monitoring (blackbox_exporter)

### Deployment

```yaml
# docker-compose.blackbox.yml
version: '3.8'
services:
  blackbox_exporter:
    image: prom/blackbox-exporter:v0.24.0
    container_name: blackbox_exporter
    ports:
      - "9115:9115"
    volumes:
      - ./blackbox.yml:/config/blackbox.yml
    command:
      - '--config.file=/config/blackbox.yml'
    restart: always
```

### Alert Configuration

```yaml
# Alert rules for Grafana Cloud / Alertmanager
- name: ssl_certificates
  rules:
    - alert: CertificateExpiryWarning
      expr: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 2592000
      for: 1h
      annotations:
        summary: "Certificate for {{ $labels.instance }} expires in {{ $value | humanizeDuration }}"
        
    - alert: CertificateExpiryCritical
      expr: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 604800
      for: 5m
      annotations:
        summary: "Certificate for {{ $labels.instance }} expires in less than 7 days"
        
    - alert: CertificateExpired
      expr: probe_ssl_earliest_cert_expiry{job="blackbox"} - time() < 0
      for: 0m
      annotations:
        summary: "Certificate for {{ $labels.instance }} has EXPIRED"
```

## Layer 3: Certificate Transparency Monitoring

```bash
# Monitor CT logs for upcoming expiries
#!/bin/bash
DOMAINS=("acmecorp.com" "api.acmecorp.com" "www.acmecorp.com")

for domain in "${DOMAINS[@]}"; do
  echo "Checking CT logs for $domain..."
  curl -s "https://crt.sh/?q=%25.$domain&output=json" | jq '.[] | select(.name_value | contains("'"$domain"'")) | {name: .name_value, not_after: .not_after}' | sort -t: -k2
  
  # Send to monitoring
  expiry_date=$(echo "$result" | jq -r '.not_after')
  days_left=$(( ($(date -d "$expiry_date" +%s) - $(date +%s)) / 86400 ))
  if [ "$days_left" -lt 30 ]; then
    # Alert
    echo "ALERT: Certificate for $domain expires in $days_left days"
  fi
done
```

## Layer 4: Multi-CA Fallback Strategy

### Certificate Hierarchy

```
Primary: Let's Encrypt (90-day validity, automated renewal via ACME)
  └── Fallback 1: Cloudflare Origin CA (15-year validity, manual renewal)
      └── Fallback 2: Google Trust Services (398-day validity, paid)
          └── Fallback 3: Self-signed (emergency only, browser warning)
```

### Implementation

```yaml
# Cloudflare SSL configuration
ssl:
  mode: full_strict  # Requires valid origin certificate
  origin_certificate: 
    source: cloudflare_origin_ca
    validity: 15_years
  universal_ssl: enabled
  tls_1_3: enabled
  certificate_transparency_monitoring: enabled
```

## Layer 5: Process and Governance

### Certificate Lifecycle Management

1. **Certificate Inventory**: Maintain CMDB of all certificates (internal, external, wildcard, SAN)
2. **Ownership**: Each certificate has a designated owner and backup owner
3. **Review Cadence**: Monthly review of all certificate expiry dates
4. **Approval Workflow**: Certificate renewal requires peer review
5. **Emergency Process**: Documented emergency renewal procedure with fallback CAs

### Runbook Integration

```markdown
## Certificate Renewal Runbook

### Automated (Normal Path)
1. certbot systemd timer runs daily at 03:00 UTC
2. Renewal only occurs if certificate < 30 days from expiry
3. Post-renewal hook reloads NGINX
4. Prometheus alert confirms renewal (certificate expiry resets to > 90 days)

### Semi-Automated (Manual Trigger)
1. SSH to bastion host
2. sudo certbot renew
3. Verify with openssl s_client

### Emergency (Rate Limit Exhausted)
1. Generate Cloudflare Origin CA certificate via API
2. Install on NGINX ingress
3. Update Cloudflare SSL mode if needed
4. Verify with curl -v https://domain.com
5. Create follow-up ticket to restore primary certificate
```

## Layer 6: Testing and Validation

### Monthly Certificate Drill

```bash
#!/bin/bash
# Monthly certificate renewal drill
echo "=== Certificate Renewal Drill ==="

# 1. Check all certificate expiry dates
for domain in api.acmecorp.com www.acmecorp.com; do
  echo "Checking $domain..."
  expiry=$(openssl s_client -connect $domain:443 -servername $domain \
    < /dev/null 2>/dev/null | openssl x509 -noout -enddate)
  echo "  $expiry"
done

# 2. Test renewal process (dry run)
sudo certbot renew --dry-run
if [ $? -eq 0 ]; then
  echo "PASS: certbot dry run successful"
else
  echo "FAIL: certbot dry run failed"
  exit 1
fi

# 3. Verify blackbox_exporter alerts
curl -s http://localhost:9115/metrics | grep probe_ssl_earliest_cert_expiry
if [ $? -eq 0 ]; then
  echo "PASS: blackbox_exporter SSL metrics available"
else
  echo "FAIL: blackbox_exporter SSL metrics not found"
  exit 1
fi

# 4. Test Cloudflare Origin CA fallback
echo "Cloudflare Origin CA drill: (manual verification)"
echo "  - Verify Origin CA cert is less than 15 years old"
echo "  - Verify Cloudflare SSL mode is 'Full (Strict)'"

echo "=== Drill Complete ==="
```

## Key Prevention Metrics

| Control | Metric | Target | Owner |
|---------|--------|--------|-------|
| Automated renewal | % of certs with automated renewal | 100% | DevOps |
| Monitoring coverage | % of domains monitored for expiry | 100% | SRE |
| Alert response time | Time to acknowledge expiry alert | < 5 min | On-call |
| Renewal success rate | % of automated renewals succeeding | > 99.9% | DevOps |
| Rate limit awareness | Rate limit remaining at renewal | > 10 | Engineering |
| Drill completion | Monthly renewal drill pass rate | 100% | SRE |
| Certificate inventory | % of certs in CMDB | 100% | Security |
| Multi-CA coverage | % of domains with > 1 CA | 100% | DevOps |

## References

- Google SRE Book: https://sre.google/sre-book/emergency-response/
- Let's Encrypt Rate Limits: https://letsencrypt.org/docs/rate-limits/
- Cloudflare Origin CA: https://developers.cloudflare.com/ssl/origin-configuration/origin-ca/
- Prometheus blackbox_exporter: https://github.com/prometheus/blackbox_exporter
- certbot documentation: https://certbot.eff.org/docs/
