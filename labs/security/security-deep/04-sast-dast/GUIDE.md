# SAST & DAST — Study Guide

## Core Concepts

### SAST (White-Box)
- Analyzes source code without executing it
- Finds vulnerabilities early in SDLC
- High false positive rate; needs triage
- Examples: SQL injection pattern matching, hardcoded secrets, XSS sinks

### DAST (Black-Box)
- Tests running application from outside
- Finds runtime vulnerabilities (configuration, business logic)
- No source code access needed
- Examples: parameter tampering, SQL injection fuzzing, auth bypass

### Key Metrics
- **True Positive**: correctly identified vulnerability
- **False Positive**: flagged but not exploitable
- **True Negative**: correctly identified as safe
- **False Negative**: missed vulnerability (worst case)

## Implementation Checklist
1. Run SAST during development (pre-commit or PR stage)
2. Run DAST against staging environments
3. Prioritize findings by severity (CVSS)
4. Configure baseline to reduce noise
5. Regular tool updates for new vulnerability signatures

## Common Pitfalls
- Relying solely on SAST (misses runtime issues)
- Relying solely on DAST (too late, expensive fixes)
- Not tuning tools → overwhelmed by false positives
- Ignoring dependency scanning (SCA)
