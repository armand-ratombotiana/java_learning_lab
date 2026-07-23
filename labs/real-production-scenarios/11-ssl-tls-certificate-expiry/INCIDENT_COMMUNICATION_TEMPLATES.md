# Lab 11 — SSL/TLS Certificate: Communication Templates

## Initial Alert

```
Title: [SEV1] SSL Certificate Expired — api.example.com
Service: External API Gateway
Severity: SEV1

Certificate: api.example.com
Expired: 2024-06-15 00:00:00 UTC
Affected: All HTTPS traffic to api.example.com
Impact: 100% of API calls failing with SSL_ERROR_CERT_EXPIRED
Duration: 12 minutes since first failure detected
```

## Status Updates

```
STATUS #1 — Certificate Renewal In Progress

Actions:
- New CSR generated
- CA requested for renewal
- Certificate issued (waiting for download)
- Estimated installation: 10 minutes

Impact: API still down — all HTTPS clients rejecting connection
Manual override: Not possible — browser/app enforcement of expiry
```

```
STATUS #2 — Certificate Renewed

New certificate installed on load balancer.
HTTPS traffic restored.
Cause: Certificate expiry notification sent to DL with no action.
Fix: Automated renewal with ACME/Let's Encrypt.
Added: external monitoring (blackbox_exporter) with 30-day alert.

Post-mortem: Certificate inventory created, renewal automation implemented.
```
