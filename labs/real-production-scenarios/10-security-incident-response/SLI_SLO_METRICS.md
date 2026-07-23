# Lab 10 — Security Incident: SLI/SLO/SLA Definitions

## Service: Security Monitoring

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Time to detect (TTD) | Incident start to detection | < 5 minutes |
| Time to contain (TTC) | Detection to containment | < 15 minutes |
| Vulnerability scan coverage | % of services scanned | > 95% |
| Vulnerability fix time | Critical vuln open to patched | < 7 days |
| Failed auth rate | Failed logins / total | < 5% |
| WAF block rate | Blocked requests / total | Monitor baseline |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| TTD < 5 min for critical alerts | 99.9% | ~43.8 min/month |
| TTC < 15 min for active breaches | 99.99% | ~4.38 min/month |
| Vulnerability patch within 7 days | 95% | ~36.5 hours/month |
| Zero data exposure incidents | 100% | 0 tolerance |

### Alerting

```yaml
groups:
  - name: security_alerts
    rules:
      - alert: BruteForceDetected
        expr: rate(failed_login_total[5m]) > 100
        for: 1m
        annotations:
          summary: "Possible brute force attack from {{ $labels.source_ip }}"

      - alert: DataExfiltrationAnomaly
        expr: rate(network_bytes_sent[5m]) > 1e9
        for: 5m
        annotations:
          summary: "Large outbound data transfer — possible exfiltration"

      - alert: CertificateExpiring
        expr: tls_certificate_days_until_expiry < 14
        annotations:
          summary: "Certificate expiring in < 14 days"
```
