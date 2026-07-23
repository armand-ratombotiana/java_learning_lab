# Lab 10 — Security Incident: Communication Templates

## Initial Alert

```
Title: [SEV1] Security Breach — Unauthorized API Key — Customer Data Accessed
Service: Customer API
Severity: SEV1 (Security Incident)

Detection: CloudTrail anomaly — API key used from unexpected geo
Scope: 50,000 customer records exported in last 30 minutes
Source IP: 185.x.x.x (not in allowlist)
API Key: user-123 (developer's personal key)
Status: Key revoked, source IP blocked at WAF
```

## Status Updates

```
STATUS #1 — Breach Contained

Containment actions completed:
1. ✅ API key revoked
2. ✅ Source IP blocked at WAF
3. ✅ All access tokens for affected user rotated
4. ✅ CloudTrail analysis in progress

Investigation:
- Key was exposed in public GitHub commit 2 days ago
- Key had read access to customer database
- 50,000 records of PII exported (names, emails, phone numbers)

Notification:
- Security team notified
- Legal team notified (GDPR breach)
- Customer notification in progress (within 72 hours per regulation)
```

```
STATUS #2 — Post-Incident Actions

Remediation:
1. git-secrets pre-commit hook added to all repos
2. All developer keys rotated
3. IAM policy reduced to least-privilege
4. GitHub secret scanning enabled
5. CloudTrail alerting for anomalous API key usage

Post-mortem scheduled for Friday.
```
