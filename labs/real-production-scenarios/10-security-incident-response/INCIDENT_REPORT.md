# Incident Report — Security Incident: Compromised API Key

## Incident ID: SEC-2026-0805-010

**Date**: August 5, 2026  
**Reported By**: Security Engineering Team (Sarah Chen)  
**Status**: Resolved — Containment Achieved, Investigation Ongoing  
**Severity**: SEV1 / P0  

---

## Timeline (All Times UTC)

### Pre-Incident — Key Exposure

| Time | Event |
|------|-------|
| 07:45 | Developer Alice begins work on new feature requiring access to external API |
| 07:55 | Alice copies `.env` file from local development environment to project directory |
| 08:00 | Alice commits code: `git add . && git commit -m "Add external API integration"` |
| 08:00 | `.env` file containing production API key `sk-prod-user-service-v3` is committed to public GitHub repo `company/user-service` |
| 08:01 | GitHub Actions triggers — CI/CD runs. No secret scanning is configured in CI pipeline |
| 08:02 | Pre-commit hooks run — no `.env` or secret scanning hook configured |
| 08:03 | Commit pushed to GitHub — `.env` file is now publicly accessible |
| 08:47 | Automated GitHub secret scanner (e.g., GitGuardian, TruffleHog) discovers the `.env` file |
| 08:48 | Attacker downloads the `.env` file from public GitHub repository |
| 08:55 | Attacker extracts API key `sk-prod-user-service-v3`: begins reconnaissance |

### Incident Onset — Active Exploitation

| Time | Event |
|------|-------|
| 09:12 | First API call from attacker IP (185.34.72.100, registered in Eastern Europe) |
| 09:12-09:20 | Attacker calls `GET /api/v1/users/health` — probe for service availability |
| 09:20-09:30 | Attacker calls `GET /api/v1/users/{id}` — test data access (50 calls) |
| 09:30-10:00 | Attacker calls `GET /api/v1/users/export/bulk` — attempts bulk export of user data |
| 09:30-13:00 | Attacker successfully exports 840,000 user records (in batches of 10,000) via the bulk export endpoint |
| 10:00-12:00 | Attacker calls `POST /api/v1/cache/clear` — disrupts caching (5,000 calls) |
| 11:00-12:00 | Attacker calls `GET /api/v1/admin/reports/users` — accesses internal reporting (2,000 calls) |
| 12:00-12:30 | Attacker calls `GET /api/v1/users/{id}/details` — accesses detailed PII (5,000 calls) |

### Detection and Response

| Time | Event |
|------|-------|
| 12:00 | AWS CloudTrail reports: API call volume from API key `sk-prod-user-service-v3` increased 400% in last hour |
| 12:15 | CloudTrail anomaly detection ML model flags key as anomalous (unusual geographic pattern — all calls from Eastern Europe, none from normal US-based IPs) |
| 12:20 | AWS GuardDuty alert: "CredentialAccess:IAMUser/AnomalousBehavior" for the API key |
| 12:25 | ELK SIEM correlating logs: CloudTrail + Cloudflare WAF + application logs — confirms attacker pattern |
| 12:28 | Security engineer Sarah Chen paged via PagerDuty |
| 12:30 | SEV1 declared — security incident bridge opened |
| 12:32 | Attacker IP (185.34.72.100) blocked at Cloudflare WAF |
| 12:35 | API key `sk-prod-user-service-v3` revoked in all services |
| 12:38 | New API key generated and rotated into production |
| 12:40 | IP whitelisting deployed to all sensitive API endpoints |
| 12:45 | All attacker IPs blocked — traffic from compromised key stopped |
| 12:50 | Scans initiated on GitHub repository — `.env` file removed, but had been publicly accessible for 4h47m |
| 13:00 | Containment verified — no further unauthorized API calls detected |

### Post-Incident

| Time | Event |
|------|-------|
| 13:05 | GitHub secret scanning enabled on repository |
| 13:10 | All `.env` files removed from repository history using `git filter-branch` |
| 13:15 | Pre-commit hook for secret scanning deployed to all developer machines |
| 13:20 | AWS Secrets Manager integration began — migration of all API keys |
| 13:25 | Incident commander declares containment phase complete |
| 13:30 | Data exposure assessment initiated (which user records were accessed?) |
| 13:37 | Incident formally resolved — investigation and remediation continuing |
| 14:00 | Mandatory breach notification process initiated (GDPR/CCPA) |

### Metrics Summary

| Metric | Baseline | During Incident | Post-Resolution |
|--------|----------|----------------|-----------------|
| API Call Rate (compromised key) | ~100/day | ~18,000/hour | 0 |
| Unique IPs Calling Key | 3 (known) | 1 (attacker) | 3 (known) |
| Data Exported | — | 840,000 user records | — |
| Endpoints Accessed | 5 | 8 (3 unauthorized) | 5 |
| Time Key Was Exposed | — | 4h47m (on GitHub) | — |
| Time Key Actively Used | — | ~4 hours | — |
| Detection Time | — | T+4h30m from first attack | — |
| Containment Time | — | 30 min after detection | — |
| IPs Blocked | — | 5 (attacker + proxies) | — |

### Exposed Data Assessment

| Data Type | Records Exposed | Sensitivity |
|-----------|----------------|-------------|
| User Names | 840,000 | PII |
| Email Addresses | 840,000 | PII |
| Phone Numbers | 620,000 | PII |
| Physical Addresses | 450,000 | PII |
| Order History | 210,000 | Sensitive |
| Payment Methods (last 4 digits) | 180,000 | Sensitive |
| Full Credit Card Numbers | 0 (not stored) | — |

### Action Items

| # | Action | Owner | Target | Status |
|---|--------|-------|--------|--------|
| 1 | Remove `.env` from git history (filter-branch) | Security Team | 08/05 | Done |
| 2 | Rotate ALL API keys (not just compromised one) | Security Team | 08/06 | In Progress |
| 3 | Implement IP whitelisting on all sensitive endpoints | Platform Team | 08/06 | In Progress |
| 4 | Deploy pre-commit secret scanning hook | DevEx Team | 08/06 | Done |
| 5 | Enable GitHub secret scanning on all repos | Security Team | 08/05 | Done |
| 6 | Migrate all secrets to AWS Secrets Manager | Platform Team | 08/30 | Planned |
| 7 | Implement anomaly detection ML for API usage | Data Engineering | 09/15 | Planned |
| 8 | Rate limiting per API key (max 1000/hr) | Platform Team | 08/07 | In Progress |
| 9 | Notify affected users (data breach notification) | Legal/PR | 08/12 | Planned |

## Expanded Incident Analysis

### Attacker Behavior Analysis

**Reconnaissance Phase (08:47-09:12 — 25 minutes)**:
After discovering the key, the attacker conducted reconnaissance:
1. `08:47`: Downloaded .env file from public GitHub repository
2. `08:50`: Extracted API key from file
3. `08:55`: First API call from Tor exit node (IP obfuscation)
4. `09:00-09:10`: Probed service health endpoints (GET /health, GET /api/v1/users/health)
5. `09:10-09:12`: Tested read access with individual user lookups (50 calls)

**Key Observation**: The attacker tested the key's validity and scope before launching the data exfiltration. This reconnaissance phase represents a detection opportunity — unusual health endpoint calls from a new IP could have triggered an alert.

**Exploitation Phase (09:12-13:00 — ~4 hours)**:
Once the attacker confirmed the key worked and had access to the bulk export endpoint:
1. `09:12-09:20`: Tested bulk export with small batches (10 records)
2. `09:20-10:00`: First major batch (80,000 records in 8 calls)
3. `10:00-12:00`: Main data exfiltration (750,000 records in 75 calls)
4. `12:00-12:30`: Final data access (10,000 records + cache disruption)

**Data Exfiltration Pattern**:
```
Call pattern:
09:20 — GET /api/v1/users/export/bulk?limit=10000&offset=0
09:21 — GET /api/v1/users/export/bulk?limit=10000&offset=10000
09:22 — GET /api/v1/users/export/bulk?limit=10000&offset=20000
...
10:00 — GET /api/v1/users/export/bulk?limit=10000&offset=80000
...continues...
12:00 — GET /api/v1/users/export/bulk?limit=10000&offset=830000

Total: 84 API calls × 10,000 records = 840,000 records
```

The attacker used simple pagination to iterate through all user records. Each call returned 10,000 records in JSON format. At an average of 5KB per record, each response was approximately 50MB. Total data exfiltrated: approximately 4.2GB.

**Attacker IP Analysis**:
| IP Address | Location | Type | Calls Made |
|-----------|----------|------|------------|
| 185.34.72.100 | Eastern Europe | VPS (likely compromised) | 1,800,000 |
| 91.121.87.45 | France | Another VPS | 200,000 |
| 198.51.100.23 | US | Proxy | 50,000 |
| 203.0.113.45 | Asia | Proxy | 20,000 |

The attacker used multiple IP addresses to avoid rate limiting and detection. The primary IP (185.34.72.100) made the majority of calls. The secondary IPs were likely used as fallbacks.

### Detection Capability Assessment

**What Detected the Incident**:
- **AWS CloudTrail ML Anomaly Detection**: Flagged the key as anomalous at 12:00 — 3 hours after the first attacker call
- **AWS GuardDuty**: Alerted at 12:20 — "CredentialAccess:IAMUser/AnomalousBehavior"
- **ELK SIEM Correlation**: Confirmed the attack at 12:25 by correlating CloudTrail + WAF logs

**Why Detection Took 3 Hours**:
1. CloudTrail ML anomaly detection was configured to look for broad patterns (not per-key)
2. The key had no usage baseline — it had been inactive for months, so there was no "normal" to compare against
3. The attacker used a low-and-slow approach (84 calls over 4 hours = ~0.35 calls/minute average peak)
4. No real-time alert existed for "bulk export endpoint accessed from external IP"
5. The SIEM rules focused on known-bad IPs, not anomalous behavior from valid credentials

**Detection Gaps**:
| Gap | Impact | Fix |
|-----|--------|-----|
| No per-key usage baseline | Could not detect unusual per-key activity | Implement baseline learning for all API keys |
| No bulk export monitoring | Bulk export could be called without alerts | Add CloudWatch alarm for bulk export calls |
| No geo-anomaly detection | External IP was not flagged as anomalous | Implement geo-location checking on API keys |
| No inactive-key monitoring | Key was 18 months old, never used | Automatically revoke unused keys (90-day policy) |
| SIEM rules too narrow | Only checked known-bad IPs | Add behavioral anomaly rules (new IP for existing key) |

### Data Exposure Impact

**Records Exposed by Type**:
```
User Names:          840,000 records — All users in database
Email Addresses:     840,000 records — All users in database
Phone Numbers:       620,000 records — Users who provided phone numbers
Physical Addresses:  450,000 records — Users with shipping addresses
Order History:       210,000 records — Users with completed orders
Payment Method Info: 180,000 records — Last 4 digits of card, card type
Full SSN/TIN:       0 records — Not stored in this database
Full Credit Cards:  0 records — Not stored (tokenized via payment processor)
```

**Regulatory Impact**:
| Regulation | Triggered? | Requirement |
|-----------|-----------|-------------|
| GDPR (EU residents) | Yes | Notify within 72 hours of discovery |
| CCPA (California residents) | Yes | Notify affected California residents |
| PCI DSS (Payment data) | Partial | Notify acquirer (only last 4 digits exposed) |
| SOX (Public company) | Possible | Assess materiality for SEC disclosure |

**User Notification Plan**:
1. Identify affected users (840,000 unique users)
2. Determine regulatory notification requirements by jurisdiction
3. Draft notification letter (explaining what happened, what data was exposed, what we're doing)
4. Notify regulators (EU DPA, California AG) within required timeframe
5. Offer identity protection services (credit monitoring) to affected users
6. Provide FAQ and support contact

### Remediation Effectiveness

**Key Rotation Timeline**:
| Time | Action | Duration |
|------|--------|----------|
| 12:30 | SEV1 declared | T+0 |
| 12:32 | Attacker IP blocked at Cloudflare | T+2 min |
| 12:35 | Compromised key revoked | T+5 min |
| 12:38 | New key generated and deployed | T+8 min |
| 12:40 | IP whitelisting deployed | T+10 min |
| 12:45 | All attacker traffic stopped | T+15 min |
| 12:50 | GitHub .env file removed | T+20 min |
| 13:00 | Containment verified | T+30 min |

**Containment Effectiveness**: 30 minutes from SEV1 declaration to full containment. This is within the target of 60 minutes for security incidents but faster is needed (target: 15 minutes for confirmed data exfiltration).

**Delays in Containment**:
1. 5-minute delay: Generating new API key and distributing to all services
2. 3-minute delay: Updating Cloudflare WAF rules (API rate limit)
3. 2-minute delay: Verifying key revocation took effect (caching)
4. 5-minute delay: Removing .env file from git history (git filter-branch)

### Post-Incident Security Improvements

**Immediate Improvements (within 24 hours)**:
1. All production API keys rotated (not just the compromised one)
2. GitHub secret scanning enabled on all repositories
3. Pre-commit hooks deployed to all developer machines
4. .gitignore updated across all repositories
5. Initial security training session conducted (all-hands)

**Short-Term Improvements (within 2 weeks)**:
1. IP whitelisting deployed to all sensitive API endpoints
2. Rate limiting configured for all API keys (1000/hr default)
3. AWS Secrets Manager implementation started
4. Security incident response runbook created
5. CloudTrail anomaly detection updated for per-key monitoring

**Long-Term Improvements (within 3 months)**:
1. Full migration from .env files to AWS Secrets Manager
2. Anomaly detection ML model for API usage patterns
3. Security champion program established
4. Quarterly penetration testing scheduled
5. Developer security training curriculum developed
