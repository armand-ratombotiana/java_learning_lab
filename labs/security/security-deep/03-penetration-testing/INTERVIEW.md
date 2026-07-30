# Interview: Penetration Testing

## Q1: Conceptual Understanding
**Q**: Explain the difference between vulnerability assessment and penetration testing.
**A**: Vulnerability assessment identifies and catalogs vulnerabilities (passive). Penetration testing actively exploits them to determine real-world impact (active). Pentesting answers "what can an attacker actually achieve?"

## Q2: Implementation
**Q**: Walk through your methodology for a web application pentest.
**A**: 1) Recon: enumerate subdomains, endpoints, technologies. 2) Scan: Nikto, Burp spider, directory brute-force. 3) Manual testing: SQLi, XSS, CSRF, SSRF, IDOR. 4) Exploit: chain vulnerabilities for impact. 5) Report: findings with CVSS scores and remediation.

## Q3: System Design
**Q**: Design a continuous penetration testing pipeline.
**A**: Integrate DAST (Burp/ZAP) into CI/CD pipeline. Run scheduled external scans (Nessus Tenable.io). Use breach and attack simulation tools (Atomic Red Team). Trigger manual pentest for major releases.

## Coding Challenge
Write a simple port scanner that checks if specific ports are open on a target host.
