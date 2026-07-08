# INTERVIEW: 17-blockchain-security

## Interview Questions

### Beginner Questions

**Q1**: What is this security mechanism and why is it important?
**A**: This security mechanism provides [core function]. It's important because it protects applications from [specific threats] and ensures [security goals].

**Q2**: How does authentication differ from authorization?
**A**: Authentication verifies who you are (identity), while authorization determines what you can do (permissions). Authentication happens before authorization.

**Q3**: What is the principle of least privilege?
**A**: Grant only the minimum permissions necessary for a user or service to perform their function. This limits potential damage from compromised accounts.

**Q4**: What is defense in depth?
**A**: Multiple layers of security controls so that if one layer is breached, others still provide protection.

### Intermediate Questions

**Q5**: Explain how token-based authentication works.
**A**: User authenticates with credentials, server validates and returns signed token. Client includes token in subsequent requests. Server validates token signature and extracts identity claims.

**Q6**: What is CSRF and how do you prevent it?
**A**: Cross-Site Request Forgery tricks authenticated users into executing unwanted actions. Prevention includes CSRF tokens, SameSite cookies, and origin header validation.

**Q7**: How do you handle session management securely?
**A**: Use secure random session IDs, HttpOnly/Secure/SameSite cookies, session timeout, concurrent session control, session fixation protection, and session regeneration on privilege escalation.

**Q8**: What are common JWT vulnerabilities?
**A**: None algorithm attack (accept unsigned tokens), weak secret keys, token theft via XSS, insufficient expiration, missing audience/issuer validation, and sensitive data in JWT payload.

### Advanced Questions

**Q9**: Design a secure microservices authentication architecture.
**A**: API gateway handles authentication, issues tokens. Services validate tokens locally using public keys. Service-to-service uses mTLS. Token includes scopes for fine-grained authorization.

**Q10**: How would you handle key rotation in production?
**A**: Support multiple active keys with versioning. New requests signed with new key. Old key retained for verification during transition period. Graceful expiration of old keys after transition.

**Q11**: Explain the trade-offs between stateless and stateful authentication.
**A**: Stateless (JWT) scales better but tokens can't be revoked. Stateful (sessions) allows revocation but requires shared session store. Hybrid approaches use short-lived JWT with refresh tokens.

**Q12**: How do you secure sensitive data at rest and in transit?
**A**: In transit: TLS 1.3 with strong cipher suites, certificate pinning. At rest: AES-256-GCM for data, separate key management, envelope encryption, field-level encryption for PII.

### Coding Questions

**Q13**: Write a method to validate a JWT token.
**A**: [Implementation demonstrating signature verification, expiration check, claim validation]

**Q14**: Implement rate limiting for an API endpoint.
**A**: [Implementation using token bucket or sliding window algorithm]

**Q15**: Create a security filter that logs all authentication attempts.
**A**: [Implementation extending OncePerRequestFilter with audit logging]

### System Design Questions

**Q16**: Design an authentication system for a multi-tenant SaaS application.
**Q17**: How would you implement RBAC for a large enterprise?
**Q18**: Design a secrets rotation system for 1000+ microservices.

### System Design Questions (Detailed)

**Q16**: Design an authentication system for a multi-tenant SaaS application.
**A**: Architecture includes tenant isolation via JWT claims (tenant_id), each tenant can configure their own IdP, tenant-specific authentication providers, shared session store with tenant prefix, global rate limiting per tenant, tenant admin console for security configuration, audit logs tagged by tenant, and data isolation at database level.

**Q17**: How would you implement RBAC for a large enterprise?
**A**: Implementation approach includes role hierarchy with inheritance, permission-based access (not role-based at code level), role mining from existing access patterns, role lifecycle management (create, modify, deprecate), periodic certification campaigns, emergency access (break-glass) procedures, separation of duties enforcement, and role analytics and reporting.

**Q18**: Design a secrets rotation system for 1000+ microservices.
**A**: System design includes central secrets management (HashiCorp Vault), sidecar pattern for secret injection, dynamic secrets with short TTL, automatic rotation without downtime, secret versioning and rollback, audit logging for all secret access, emergency secret rotation trigger, and health monitoring for secret expiry.

### Behavioral Questions

**Q19**: Describe a time you fixed a security vulnerability.
**A**: Follow STAR method: Situation (context of the vulnerability discovery), Task (responsibility and severity assessment), Action (steps taken to analyze, fix, and verify), Result (vulnerability resolved, improved processes).

**Q20**: How do you stay current with security threats?
**A**: Continuous learning approach includes following security blogs (KrebsOnSecurity, Schneier), monitoring CVE feeds and vendor advisories, participating in bug bounty programs, attending security conferences (Black Hat, DefCon), contributing to OWASP projects, and practicing on security labs (HackTheBox, TryHackMe).

**Q21**: How do you convince management to invest in security?
**A**: Business case approach: quantify risk using ALE (Annualized Loss Expectancy), reference industry breach costs, map to compliance requirements, show ROI of preventive controls, compare security insurance costs, and use peer company examples.

### Code Review Questions

**Q22**: What security issues do you look for in code reviews?
**A**: Security review checklist includes authentication/authorization checks present, input validation and output encoding, secure configuration (no hardcoded secrets), proper cryptography usage, error handling (no information leakage), logging (no sensitive data), session management, CSRF/XSS protection, SQL injection prevention, and secure deserialization.

**Q23**: How do you review a pull request for security?
**A**: Review process includes checking for security-related changes, reviewing diff for common vulnerability patterns, verifying security controls in new endpoints, checking configuration changes for weakening security, reviewing test coverage for security scenarios, verifying dependency changes for known vulnerabilities, running SAST scanner on the changes, deploying to review environment for DAST scan, documenting any security findings, and approving only after all findings addressed.

### Whiteboarding Questions

**Q24**: Whiteboard a secure authentication flow.
**A**: The flow includes user submits credentials via POST/login, server validates against UserDetailsService, AuthenticationManager processes authentication, SecurityContextHolder set with Authentication object, session created with JSESSIONID cookie, subsequent requests include session cookie, SessionRepositoryFilter validates session, and SecurityContextPersistenceFilter restores context.

**Q25**: Whiteboard a token revocation strategy.
**A**: Components include token blacklist (Redis with TTL matching token expiry), token version counter in database, JWT ID (jti) claim for unique identification, background job to clean expired entries, admin API for immediate revocation, webhook for external token revocation events, and audit log for all revocation actions.
