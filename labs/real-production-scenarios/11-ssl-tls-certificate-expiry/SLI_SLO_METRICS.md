# Lab 11 — SSL/TLS Certificate: SLI/SLO/SLA

## Service: External-Facing APIs (TLS)

### SLIs

| Indicator | Definition | Target |
|-----------|-----------|--------|
| Certificate validity | Days until expiry per certificate | > 30 days |
| Certificate revocation | OCSP/CRL check success | > 99.9% |
| TLS handshake latency | Time to complete TLS 1.3 handshake | < 100ms |
| TLS protocol version | % connections using TLS 1.2+ | 100% |
| Cipher strength | % connections with AEAD ciphers | > 95% |

### SLOs

| SLI | Target | Error Budget |
|-----|--------|-------------|
| Certificates valid > 30 days | 99.99% | ~4.38 min/month |
| Zero expired certificate events | 100% | 0 tolerance |
| TLS 1.2+ enforced | 100% | 0 tolerance |

### Alerting

```yaml
groups:
  - name: tls_alerts
    rules:
      - alert: CertificateExpiringWarning
        expr: tls_certificate_days_until_expiry < 30
        annotations:
          summary: "Certificate for {{ $labels.host }} expires in {{ $value }} days"

      - alert: CertificateExpiringCritical
        expr: tls_certificate_days_until_expiry < 7
        annotations:
          summary: "Certificate for {{ $labels.host }} expires in < 7 days — RENEW NOW!"

      - alert: CertificateExpired
        expr: tls_certificate_expired == 1
        annotations:
          summary: "CERTIFICATE EXPIRED for {{ $labels.host }} — SERVICE IMPACT IMMINENT"
```
