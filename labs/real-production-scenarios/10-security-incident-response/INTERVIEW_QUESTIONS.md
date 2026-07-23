# Lab 10 — Security Incident Response: Interview Questions

**Q1: What are the phases of the security incident response lifecycle?**

**Answer:** NIST framework: 1) Preparation — tools, runbooks, training. 2) Detection and Analysis — identify incident, determine scope. 3) Containment, Eradication, Recovery — stop the bleeding, remove threat, restore normal operations. 4) Post-Incident Activity — post-mortem, lessons learned, improvements. For P0 security incidents (breach, data exfiltration): containment is the absolute priority — even over root cause investigation.

**Q2: You find an API endpoint that exposes customer PII without authentication. What do you do?**

**Answer:** 1) IMMEDIATE: Disable the endpoint (feature flag, WAF block, or deploy fix). 2) Verify the endpoint was not accessed by unauthorized parties (check access logs). 3) If accessed: determine scope (how many customers affected, what data exposed). 4) Notify security team + legal (data breach notification requirements). 5) Notify affected customers (per regulations — GDPR, CCPA). 6) Root cause: why wasn't auth required? Fix CI/CD to catch missing auth. 7) Add security scanning to CI/CD (SAST/DAST).

**Q3: What is the difference between SAST, DAST, and penetration testing?**

**Answer:** SAST (Static Application Security Testing): white-box testing — analyzes source code for vulnerabilities (SQL injection, XSS, hardcoded secrets). Done during development, in CI/CD. DAST (Dynamic Application Security Testing): black-box testing — tests running application for vulnerabilities (OWASP Top 10). Done in staging/production. Penetration testing: manual testing by security experts — simulates real attacker, comprehensive assessment (social engineering, business logic flaws). Done periodically (annual or quarterly).

**Q4: How do you detect a security breach in production?**

**Answer:** 1) Anomalous traffic patterns — unexpected IPs, geographies, request rates. 2) Authentication anomalies — failed login spike, impossible travel, credential stuffing patterns. 3) Data access anomalies — large data exports, unusual query patterns. 4) Alerting: SIEM (Splunk ES, Sentinel, ELK Security) correlation rules. 5) WAF/IDS/IPS alerts. 6) File integrity monitoring — unexpected file changes. 7) Endpoint detection and response (EDR) — suspicious process execution. 8) Cloud provider security tools — GuardDuty, Security Hub, Azure Defender.

**Q5: What is the principle of least privilege and how do you implement it?**

**Answer:** Every user/service should have only the permissions necessary to perform their function. Implementation: 1) IAM roles instead of shared credentials. 2) Service-specific service accounts (not "admin for everything"). 3) Database: per-service database users with only the required permissions (SELECT for read-only services). 4) Network: security groups with specific source/destination/port rules. 5) File system: application runs as non-root user. 6) Periodic access reviews — remove unused permissions. 7) Automated IAM policy validation in CI/CD.

**Q6: A developer accidentally commits AWS access keys to a public GitHub repo. What do you do?**

**Answer:** 1) IMMEDIATE: Rotate the keys in AWS IAM (invalidate them). 2) Check AWS CloudTrail for any unauthorized use of the keys. 3) If keys were used: assume breach — investigate what was accessed. 4) Remove the keys from git history (BFG Repo-Cleaner or git filter-branch). 5) Add pre-commit hook to prevent future credential commits (git-secrets, truffleHog). 6) Add scanning in CI/CD for leaked credentials. 7) Post-mortem: why did the developer have production AWS keys in their development environment?

**Q7: What is the shared responsibility model in cloud security?**

**Answer:** Cloud provider is responsible for security OF the cloud (physical security, hardware, networking, hypervisor). Customer is responsible for security IN the cloud (data, configuration, IAM, network ACLs, OS patching, application security). For SaaS: provider handles more. For IaaS: customer handles more. Common customer failures: misconfigured S3 buckets, overly permissive IAM roles, unpatched OS, open security groups. Always verify your responsibilities under each service model.

**Q8: How would you respond to a DDoS attack on your application?**

**Answer:** 1) Detection: traffic spike, high CPU/network, elevated error rates, user reports of slowness. 2) Mitigation: route traffic through DDoS protection service (AWS Shield, Cloudflare, Akamai). 3) Rate limiting: enforce per-IP and per-user rate limits. 4) WAF rules: block known attack patterns, geographic blocks if appropriate. 5) Auto-scaling: scale out to absorb traffic while mitigation kicks in. 6) If application-layer DDoS: identify the targeted endpoint and implement CAPTCHA or authentication. 7) Post-attack: analyze attack patterns, update WAF rules, improve detection.

**Q9: Tell me about a security incident you handled. (STAR)**

**Answer:** Situation: Our monitoring detected an unusual pattern of API calls from an unexpected geographic region, followed by a data export of 50,000 customer records. Task: As security on-call, I needed to contain the breach. Action: I immediately revoked the compromised API key, blocked the source IP at the WAF, and verified no ongoing access. I analyzed CloudTrail logs to determine scope: the key was a developer's personal key that had been exposed in a public code repository. I rotated all developer keys and implemented git-secrets pre-commit hook. Result: Breach contained within 10 minutes. All affected customers notified per GDPR requirements. No further unauthorized access detected.

**Q10: How do you balance security with developer velocity?**

**Answer:** 1) Shift left: security checks in CI/CD (SAST, dependency scanning) that run in minutes, not days. 2) Pre-approved libraries: maintain a catalog of approved open-source libraries. 3) Security as code: automated policies (not manual review gates). 4) Developer training: security awareness, common vulnerability patterns. 5) Emergency process: security exceptions with automated expiry and review. 6) Blameless culture: security incidents drive system improvements, not blame. 7) Measure: time to fix security issues, security issue density, false positive rate.
