# Interview: SAST & DAST

## Q1: Conceptual Understanding
**Q**: Compare SAST and DAST. When would you use each?
**A**: SAST finds vulnerabilities during development (shift-left) with source access — good for SQLi, XSS, hardcoded secrets. DAST finds runtime issues in deployed apps — good for auth bypass, config errors, business logic flaws. Use both in complementary fashion.

## Q2: Implementation
**Q**: How would you integrate SAST into a CI/CD pipeline?
**A**: Run SAST in CI on every pull request. Gate merges on critical/high findings. Publish results to developer dashboard. Use incremental analysis to only scan changed files. Break build on new critical findings only.

## Q3: System Design
**Q**: Design an application security testing platform.
**A**: Multi-engine approach: SAST (Semgrep/CodeQL) + DAST (ZAP) + SCA (OWASP Dependency Check) + Secrets scanning (TruffleHog). Unified findings database (DefectDojo). Jira integration. Slack notifications. SLSA provenance for supply chain.

## Coding Challenge
Write a simple SAST rule that detects SQL injection patterns in Java source code.
