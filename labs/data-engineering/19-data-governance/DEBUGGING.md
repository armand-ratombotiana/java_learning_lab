# Debugging Data Governance

## PII Missed
Check if column naming differs from expected patterns; sample size may be too small; update regex patterns

## Masking Performance
Dynamic masking adds query latency; consider view-based masking for frequently accessed data

## RBAC Access Issues
Users can't access legitimate data for their role; check role membership, inheritance, row filters

## GDPR Erasure Failures
Data may exist in unexpected locations (logs, cache, backups); expand search scope
