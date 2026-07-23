# Lab 10 — Security Incident: Disaster Recovery Plan

## Recovery Objectives

| Metric | Target |
|--------|--------|
| TTD (Time to Detect) | < 5 minutes |
| TTC (Time to Contain) | < 15 minutes |
| Data breach notification | Within 72 hours (GDPR) |
| Service restoration | < 1 hour |

## Scenarios

### Scenario A: API Key Leak

**Trigger:** Key exposed in public repo, unauthorized access detected
**Recovery:**
1. Revoke the compromised API key immediately
2. Block source IP at WAF
3. Audit CloudTrail for all actions by that key
4. Determine scope of accessed data
5. Rotate all keys in the same IAM group
6. Notify affected customers
7. Implement pre-commit secret scanning

### Scenario B: DDoS Attack

**Trigger:** Traffic spike from distributed sources, application slow/unavailable
**Recovery:**
1. Enable DDoS protection (AWS Shield, Cloudflare)
2. Rate limit at CDN/load balancer
3. Block attack patterns at WAF
4. Scale out to absorb traffic while mitigation engages
5. Implement Geo-blocking if attack is from unexpected regions
6. Post-attack: analyze patterns, update WAF rules

### Scenario C: Data Exfiltration

**Trigger:** Large outbound data transfer anomaly
**Recovery:**
1. Identify the source and method of exfiltration
2. Block the egress path
3. Revoke compromised credentials
4. Determine scope: what data was taken, how much, over what period
5. Notify security officer and legal
6. Notify affected customers
7. Implement egress monitoring and DLP controls
