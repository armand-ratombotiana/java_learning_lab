# Interview Cheatsheet: Security Incident

## Key Diagnostic Commands
- `last` / `lastb` — login history
- `journalctl -u sshd -n 100` — SSH auth logs
- `netstat -tulanp` — current connections and listening ports
- `lsof -i` — process network connections
- CloudTrail / AWS CloudTrail — API activity
- `auditd` logs — Linux audit events
- WAF logs — blocked/allowed requests
- SIEM dashboards (Splunk, Sentinel)

## Common Metrics to Check
- Failed login attempts (rate and origin)
- API 401/403 rate spikes
- Unusual data transfer out (egress anomalies)
- Configuration changes (IAM, security groups, ACLs)
- New user/role creation
- Certificate issuance changes

## Typical Root Causes
- Exposed credentials (in code, config, logs)
- Missing authentication on internal endpoints
- Insufficient rate limiting (brute force)
- Certificate expiry or misconfiguration
- Default credentials unchanged
- Open S3 bucket / insecure storage
- Supply chain attack (dependency compromise)

## Interview Question Patterns
- "How do you respond to a security breach in production?"
- "Design a secure secret rotation system"
- "How do you implement rate limiting for login endpoints?"
- "What would you do if you found credentials in a git repo?"

## STAR Story Template
**S**: Alert of unusual API activity — 10K login attempts from unknown IPs
**T**: Investigate and mitigate brute force attack
**A**: Blocked IPs via WAF, added rate limiting to login endpoint, rotated all credentials
**R**: Attack blocked, added automatic IP blocking at 100 failed attempts/min
