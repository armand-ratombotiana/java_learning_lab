# Cracking the Security Interview — Complete Guide

> Comprehensive interview preparation covering OWASP Top 10, Web Security, Cloud Security, Cryptography, and Network Security. Each section includes interview questions with answers.

---

## Table of Contents

1. [OWASP Top 10 (2021)](#owasp-top-10)
2. [Web Security](#web-security)
3. [Cloud Security](#cloud-security)
4. [Cryptography](#cryptography)
5. [Network Security](#network-security)
6. [Identity & Access Management](#iam)
7. [Application Security](#appsec)
8. [Infrastructure Security](#infrastructure)
9. [Security Operations](#secops)
10. [Governance, Risk & Compliance](#grc)

---

## OWASP Top 10

### A01 — Broken Access Control

**What it is**: When users can act outside their intended permissions.

**Common examples**:
- IDOR (Insecure Direct Object Reference)
- Path traversal
- Missing function level access control
- CORS misconfiguration

**Interview Q**: *How do you prevent IDOR vulnerabilities?*

```
Answer:
1. Use indirect object references (UUIDs instead of auto-increment IDs)
2. Server-side authorization check on every access
3. Use access control lists (ACLs) or role-based checks
4. Verify ownership: does user X own resource Y?
5. Implement proper session management tied to user identity
6. Use authorization frameworks (Spring Security @PreAuthorize)
7. Penetration test for horizontal/vertical privilege escalation

Example check:
public Post getPost(String userId, String postId) {
    Post post = postRepository.findById(postId);
    if (!post.getOwnerId().equals(userId)) {
        throw new AccessDeniedException("Not your post");
    }
    return post;
}
```

### A02 — Cryptographic Failures

**What it is**: Weak or missing encryption leading to data exposure.

**Interview Q**: *How do you protect sensitive data at rest and in transit?*

```
Answer:
At Rest:
1. Encrypt all sensitive data using AES-256-GCM
2. Use envelope encryption with a KMS
3. Encrypt database columns containing PII
4. Encrypt backups and snapshots
5. Use TDE for full database encryption

In Transit:
1. TLS 1.3 for all communications
2. HSTS headers to enforce HTTPS
3. mTLS for service-to-service communication
4. Certificate pinning for critical services
5. Perfect Forward Secrecy cipher suites

Key Management:
1. Use a dedicated KMS (AWS KMS, Azure Key Vault, HashiCorp Vault)
2. Rotate keys on schedule and on compromise
3. Separate keys by environment (dev/staging/prod)
4. Audit all key access
```

### A03 — Injection

**What it is**: Untrusted data sent to an interpreter as part of a command.

**Interview Q**: *How do you prevent SQL injection in Java?*

```
Answer:
1. ALWAYS use PreparedStatement with parameterized queries
2. Use ORM frameworks (Hibernate, JPA) with parameter binding
3. Implement input validation and whitelisting
4. Use stored procedures (but still parameterized)
5. Apply least privilege to database accounts
6. Use WAF rules to detect injection attempts
7. Regular penetration testing and SAST scanning

Never:
String query = "SELECT * FROM users WHERE name = '" + input + "'";

Always:
PreparedStatement stmt = conn.prepareStatement(
    "SELECT * FROM users WHERE name = ?");
stmt.setString(1, input);
```

### A04 — Insecure Design

**What it is**: Risks from architecture and design flaws.

**Interview Q**: *What is threat modeling and how do you apply STRIDE?*

```
Answer:
Threat modeling is a structured approach to identify security risks
during the design phase.

STRIDE:
S — Spoofing: Can someone pretend to be someone else?
    Mitigation: Authentication, mTLS, digital signatures

T — Tampering: Can data be modified without detection?
    Mitigation: Integrity checks, versioning, audit logs

R — Repudiation: Can a user deny their actions?
    Mitigation: Audit trails, logging, digital signatures

I — Information Disclosure: Can data be accessed by unauthorized users?
    Mitigation: Encryption, access control, data classification

D — Denial of Service: Can the system be made unavailable?
    Mitigation: Rate limiting, auto-scaling, DDoS protection

E — Elevation of Privilege: Can a user gain higher privileges?
    Mitigation: RBAC, input validation, principle of least privilege

Process:
1. Draw data flow diagram
2. Identify trust boundaries
3. Apply STRIDE per component
4. Document threats and mitigations
5. Validate mitigations in code review
```

### A05 — Security Misconfiguration

**What it is**: Insecure default configurations or incomplete hardening.

**Interview Q**: *What security misconfigurations do you look for?*

```
Answer:
Common misconfigurations:
1. Default credentials unchanged
2. Directory listing enabled
3. Unnecessary services running
4. Debug/error messages with stack traces
5. CORS set to allow all origins
6. Security headers missing (HSTS, CSP, X-Frame-Options)
7. Cloud storage buckets publicly accessible
8. Overly permissive IAM policies
9. Unpatched software versions
10. Open ports that should be closed

Remediation:
- Automated configuration scanning (CIS benchmarks)
- Infrastructure as Code for consistent configurations
- Regular vulnerability scanning
- Configuration management tools
```

### A06 — Vulnerable and Outdated Components

**What it is**: Using components with known vulnerabilities.

**Interview Q**: *How do you manage supply chain security?*

```
Answer:
1. Maintain a Software Bill of Materials (SBOM)
2. Use dependency scanning tools (OWASP Dependency-Check, Snyk)
3. Automated dependency updates with Dependabot/Renovate
4. Pin dependency versions (avoid floating versions)
5. Review licenses for compliance
6. Verify package integrity (checksums, signature verification)
7. Use private registries with scanned and approved packages
8. Regular vulnerability scanning in CI/CD pipeline
9. Have a rapid patching process for critical CVEs
10. Monitor security advisories for your dependencies
```

### A07 — Identification and Authentication Failures

**What it is**: Weak authentication mechanisms.

**Interview Q**: *Design a secure authentication system.*

```
Answer:
1. Password Requirements: Minimum 12 characters, hashed with Argon2id
2. Rate Limiting: Max 5 attempts before CAPTCHA, lockout after 10
3. MFA: TOTP or WebAuthn as second factor
4. Session Management: Secure random tokens, HttpOnly/Secure cookies
5. Passwordless Option: WebAuthn/FIDO2 passkeys
6. Account Recovery: Out-of-band verification, no security questions
7. Monitoring: Detect credential stuffing, anomalous logins
8. Audit Log: Track all authentication attempts
```

### A08 — Software and Data Integrity Failures

**What it is**: Failures related to CI/CD pipelines and software updates.

**Interview Q**: *How do you secure a CI/CD pipeline?*

```
Answer:
1. Code security: SAST in PR checks, dependency scanning
2. Build security: Reproducible builds, signed artifacts
3. Deployment security: Immutable infrastructure, blue-green deployment
4. Pipeline security: Least privilege for CI/CD roles
5. Secret management: No secrets in code, use Vault/CI secrets
6. Artifact signing: Sign all build artifacts
7. Supply chain: Verify third-party action integrity
8. Audit: Log all build and deployment actions
9. Access control: Approvals for production deployments
10. Verify: Post-deployment security validation
```

### A09 — Security Logging and Monitoring Failures

**What it is**: Insufficient detection and response capabilities.

**Interview Q**: *Design a security monitoring system.*

```
Answer:
1. Log Sources: Applications, OS, network, cloud, identity systems
2. Log Format: Structured (JSON with schema), consistent timestamp
3. Log Collection: Centralized via agent or API, encrypted in transit
4. Storage: Hot (7d), warm (30d), cold (1yr+) with different performance
5. Detection: Correlation rules + ML-based anomaly detection
6. Alerting: Severity-based, on-call rotation, escalation paths
7. Response: SOAR integration for automated response
8. Compliance: Immutable logs, access controls, audit trail
9. Testing: Regular tabletop exercises, red team validation
10. Improvement: Metrics-driven (MTTD, MTTR), post-incident reviews
```

### A10 — Server-Side Request Forgery (SSRF)

**What it is**: Attacker makes server-side application fetch internal resources.

**Interview Q**: *How do you prevent SSRF vulnerabilities?*

```
Answer:
1. URL whitelist: Only allow requests to approved external endpoints
2. Block private IP ranges (127.0.0.0/8, 10.0.0.0/8, 172.16.0.0/12, 192.168.0.0/16)
3. Disable redirect following (or validate redirect target)
4. Use a dedicated HTTP client for outbound requests
5. DNS resolution validation (ensure IP is not internal)
6. Network segmentation: Services that make outbound requests should not
   have access to internal metadata services
7. Use a forward proxy with allowlisting
8. Validate URL scheme (only allow https://, reject file://, dict://, etc.)
9. Input validation: No raw user input in URL construction
10. Cloud-specific: Block instance metadata endpoint access

Example:
public String fetchUrl(String url) {
    URI uri = new URI(url);
    
    // Validate scheme
    if (!"https".equals(uri.getScheme())) {
        throw new IllegalArgumentException("Only HTTPS allowed");
    }
    
    // Resolve DNS and check IP
    InetAddress addr = InetAddress.getByName(uri.getHost());
    if (addr.isSiteLocalAddress() || addr.isLoopbackAddress()) {
        throw new SecurityException("Private IP blocked");
    }
    
    // Check against whitelist
    if (!allowedHosts.contains(uri.getHost())) {
        throw new SecurityException("Host not allowed");
    }
    
    // Execute with timeout
    return httpClient.send(request, Duration.ofSeconds(5));
}
```

---

## Web Security

### HTTP Security Headers

```
Header: Strict-Transport-Security (HSTS)
Value: max-age=31536000; includeSubDomains; preload
Purpose: Forces HTTPS, prevents SSL stripping

Header: Content-Security-Policy
Value: default-src 'self'; script-src 'self'; object-src 'none'
Purpose: Prevents XSS by controlling resource loading

Header: X-Frame-Options
Value: DENY (or SAMEORIGIN)
Purpose: Prevents clickjacking

Header: X-Content-Type-Options
Value: nosniff
Purpose: Prevents MIME type sniffing

Header: Referrer-Policy
Value: strict-origin-when-cross-origin
Purpose: Controls how much referrer info is sent

Header: Permissions-Policy
Value: camera=(), microphone=(), geolocation=()
Purpose: Restricts browser API access

Header: Set-Cookie (secure flags)
Value: HttpOnly; Secure; SameSite=Strict; Path=/
Purpose: Prevents cookie theft
```

**Interview Q**: *What is CSP and how does it prevent XSS?*

```
Answer:
Content Security Policy is a browser security mechanism that defines
which resources (scripts, styles, images, etc.) are allowed to load.

It prevents XSS by:
1. Blocking inline scripts (unless 'unsafe-inline' is set)
2. Blocking eval() (unless 'unsafe-eval' is set)
3. Restricting script sources to trusted domains
4. Blocking inline event handlers (onclick, onerror, etc.)

Implementation:
- Report-only mode first: Content-Security-Policy-Report-Only
- Monitor reports for violations before enforcing
- Use nonces or hashes for legitimate inline scripts
- Consider using 'strict-dynamic' for modern apps
```

### CORS

**Interview Q**: *How does CORS work and what security considerations exist?*

```
Answer:
CORS (Cross-Origin Resource Sharing) is a browser mechanism that
controls cross-origin requests.

Flow:
1. Browser sends preflight OPTIONS request
2. Server responds with allowed origins, methods, headers
3. If allowed, browser sends actual request

Security considerations:
1. Never use Access-Control-Allow-Origin: * with credentials
2. Validate Origin header server-side, don't echo blindly
3. Use specific origins, not wildcards in production
4. Restrict methods to only what is needed
5. Limit exposed headers
6. Set Vary: Origin header for caching
```

### CSRF

**Interview Q**: *How do you prevent Cross-Site Request Forgery?*

```
Answer:
CSRF prevention methods:

1. Synchronizer Token Pattern:
   - Generate unique token per session
   - Include in forms or headers
   - Validate on server-side

2. SameSite Cookies:
   - Set-Cookie: SameSite=Strict (for sensitive actions)
   - Set-Cookie: SameSite=Lax (for safe methods)

3. Custom Headers:
   - Require X-Requested-By or similar custom header
   - JavaScript must be used to add the header

4. Double Submit Cookie:
   - Send token as cookie AND request parameter
   - Server verifies they match

5. Origin/Referer Header Check:
   - Verify Origin/Referer matches expected value
```

### TLS/SSL

**Interview Q**: *Describe the TLS 1.3 handshake.*

```
Answer:
TLS 1.3 Handshake (simplified):

Client Hello:
- Supported cipher suites
- Key share (EC Diffie-Hellman public key)
- Supported versions

Server Hello:
- Selected cipher suite
- Server key share
- Certificate

Server Finished:
- Certificate verify (signs handshake transcript)
- Finished message (MAC of handshake)

Client Finished:
- Finished message (MAC of handshake)

Key improvements over TLS 1.2:
- 1-RTT handshake (down from 2-RTT)
- 0-RTT for resumed connections
- Removed weak ciphers (RC4, 3DES)
- Forward secrecy is mandatory
- Simplified cipher suite negotiation
```

---

## Cloud Security

### Shared Responsibility Model

```
┌──────────────────────────────────────┐
│    Customer                          │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ Customer Data                  │  │
│  ├────────────────────────────────┤  │
│  │ Application (IAM, Config,     │  │
│  │  Encryption, Firewall)        │  │
│  ├────────────────────────────────┤  │
│  │ Platform & IAM                │  │
│  ├────────────────────────────────┤  │
│  │ OS, Network, Firewall (if     │  │
│  │  customer-managed)            │  │
│  └────────────────────────────────┘  │
├──────────────────────────────────────┤
│    Cloud Provider                    │
│                                      │
│  ┌────────────────────────────────┐  │
│  │ Compute, Storage, Networking   │  │
│  ├────────────────────────────────┤  │
│  │ Physical Security              │  │
│  │ (DC access, cooling, power)    │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
```

**Interview Q**: *Explain the shared responsibility model and how it changes with different service models.*

```
Answer:
- IaaS: Customer manages everything above the hypervisor (OS, app, data)
- PaaS: Customer manages data and application configuration
- SaaS: Customer manages data and user access only

Key implications:
1. Just because it is in the cloud does not mean it is secure
2. Misconfiguration is the #1 cause of cloud breaches
3. Know where your responsibility ends and the provider's begins
4. Use managed services to reduce attack surface
```

### AWS Security

**Interview Q**: *How do you secure a multi-account AWS organization?*

```
Answer:
1. Organization Setup:
   - Separate accounts for: Security (audit), Log Archive, Network
   - Environment accounts: Dev, Staging, Prod
   - Workload accounts: Isolated per application

2. Preventive Controls (SCPs):
   - Deny: Leaving organization
   - Deny: Disabling CloudTrail
   - Deny: Making S3 buckets public
   - Deny: Non-approved regions
   - Deny: Removing KMS key rotation

3. Detective Controls:
   - Centralized CloudTrail to security account
   - GuardDuty in every account
   - Security Hub for aggregation
   - Config rules for compliance

4. Network:
   - Transit Gateway for networking
   - VPC endpoints for private access
   - Network Firewall for inspection

5. Identity:
   - SSO via IAM Identity Center
   - Role-based access from central IdP
   - Permission boundaries for developer roles

6. Data:
   - S3 bucket policies with conditions
   - KMS with CMK for encryption
   - S3 Object Lock for immutability
```

### Kubernetes Security

**Interview Q**: *How do you secure a Kubernetes cluster?*

```
Answer:
1. Cluster Setup:
   - Regular version updates
   - Enable audit logging
   - Restrict API server access
   - etcd encryption

2. Authentication & Authorization:
   - OIDC integration for user auth
   - RBAC with least privilege
   - Service accounts with specific roles
   - Pod identity for cloud access

3. Network Security:
   - Network policies for pod-to-pod
   - Ingress TLS termination
   - mTLS via service mesh (Istio, Linkerd)
   - Encrypt node-to-node traffic

4. Pod Security:
   - Pod Security Standards (restricted profile)
   - Security contexts (readOnlyRootFilesystem, runAsNonRoot)
   - Resource limits
   - Seccomp/AppArmor profiles

5. Container Security:
   - Image scanning (Trivy, Clair)
   - Minimal base images (distroless)
   - Image signing (Cosign)
   - No privileged containers

6. Secrets:
   - Encrypt secrets at rest
   - Use external secrets operator (Vault, AWS Secrets Manager)
   - No secrets in environment variables
   - Short-lived credentials

7. Admission Control:
   - OPA/Gatekeeper policies
   - Kyverno for Kubernetes-native policies
   - Validating/Mutating webhooks
```

---

## Cryptography

### Symmetric vs Asymmetric

**Interview Q**: *Compare symmetric and asymmetric encryption.*

```
Answer:
Symmetric:
- Same key for encrypt and decrypt
- Fast (hardware-accelerated)
- Key distribution problem
- Examples: AES, ChaCha20, 3DES
- Use case: Bulk data encryption

Asymmetric:
- Public/private key pair
- Slow (limited to small data)
- Solves key distribution
- Examples: RSA, ECC, Diffie-Hellman
- Use case: Key exchange, digital signatures

Hybrid approach:
- Use asymmetric to exchange a session key
- Use symmetric for bulk encryption (TLS does this)
```

### Hashing

**Interview Q**: *How should passwords be stored?*

```
Answer:
NEVER:
- Plaintext
- MD5/SHA-1 (fast hash, not designed for passwords)
- Simple SHA-256 (still too fast)

ALWAYS:
1. Argon2id (preferred — memory-hard, time-hard, resistant to GPU/ASIC)
2. BCrypt (if Argon2 unavailable, cost factor >= 12)
3. PBKDF2 with HMAC-SHA256 (FIPS compliant, high iterations)

Best practices:
- Unique salt per password (16+ bytes, CSPRNG)
- Cost factor high enough for ~100ms verification
- Pepper (application-wide secret) optionally
- Hash on server-side (never send raw password over network)
- Rate limit verification attempts

Example with Argon2:
Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(
    16,      // salt length
    32,      // hash length
    1,       // parallelism
    60000,   // memory in KB (~60MB)
    10);     // iterations
```

### JWT

**Interview Q**: *How do you securely implement JWT?*

```
Answer:
1. Signing: Use RS256 or ES256 (asymmetric), NOT HS256 (symmetric; if secret leaks,
   anyone can forge tokens). For internal services, HS256 with rotated secret is OK.

2. Short expiration: Access tokens: 15 min. Refresh tokens: 7 days (or less).

3. Claims:
   - iss (issuer)
   - sub (subject — user ID)
   - aud (audience — specific service)
   - exp (expiration)
   - iat (issued at)
   - jti (unique token ID — for revocation)

4. Security:
   - Validate all claims server-side
   - Never accept alg: none
   - Validate the signature before ANY other claim
   - Use jti for token revocation
   - Store refresh tokens securely (hash in DB)

5. Storage:
   - Access tokens: Memory (not localStorage/sessionStorage)
   - Refresh tokens: HttpOnly, Secure, SameSite cookie
```

### TLS

**Interview Q**: *Why is certificate pinning controversial?*

```
Answer:
Certificate pinning associates a host with a specific certificate or
public key.

Pros:
- Prevents MITM from rogue CAs
- Detects CA compromise
- Extra layer of trust validation

Cons:
- Certificate rotation requires app update
- If backup cert not properly managed, app breaks
- Pinning the wrong cert blocks legitimate traffic
- HTTP Public Key Pinning (HPKP) deprecated due to abuse potential

Modern approach:
- Use Certificate Transparency (CT) instead
- Monitor issuance via CT logs
- Use Expect-CT header (now deprecated in favor of CT built-in)
- For mobile apps: certificate transparency + CAs you trust
```

---

## Network Security

### Firewall Types

**Interview Q**: *Compare stateful firewall, NGFW, and WAF.*

```
Answer:
Stateful Firewall:
- Operates at OSI Layer 3-4
- Tracks connection state (SYN, SYN-ACK, ACK)
- Allows return traffic automatically
- No application-layer inspection
- Example: iptables, AWS Security Groups

Next-Generation Firewall (NGFW):
- Stateful firewall + application awareness
- Deep packet inspection (DPI) at Layer 7
- User and identity awareness
- IPS capabilities
- SSL/TLS inspection
- Example: Palo Alto, Fortinet

Web Application Firewall (WAF):
- Layer 7 only (HTTP/HTTPS)
- Inspects HTTP requests/responses
- Rules: SQLi, XSS, CSRF, etc.
- Rate limiting, bot detection
- Example: Cloudflare WAF, AWS WAF, ModSecurity
```

### DNS Security

**Interview Q**: *What DNS security mechanisms exist?*

```
Answer:
1. DNSSEC:
   - Digitally signs DNS records
   - Prevents cache poisoning
   - Chain of trust from root zone

2. DNS-over-HTTPS (DoH):
   - Encrypts DNS queries in HTTPS
   - Prevents eavesdropping on DNS
   - Hides DNS from network observers

3. DNS-over-TLS (DoT):
   - Encrypts DNS over TLS on port 853
   - More efficient than DoH
   - Standard port makes it blockable

4. Response Policy Zones (RPZ):
   - DNS firewall
   - Block known malicious domains
   - Can redirect to sinkhole

5. DNS Sinkhole:
   - Returns false IP for malicious domains
   - Used in security monitoring
   - Identifies infected hosts
```

### DDoS Mitigation

**Interview Q**: *Design a DDoS mitigation system.*

```
Answer:
Layers of defense:

1. Edge (Cloudflare, AWS Shield, Akamai):
   - Anycast network distributes traffic
   - Volumetric attack absorption
   - L3/L4 mitigation at edge

2. Network Layer:
   - Rate limiting per IP
   - SYN cookies/proxy
   - Connection table limits
   - BGP blackhole (RTBH) for extreme cases

3. Application Layer:
   - WAF rules for layer 7 attacks
   - Rate limiting per user/session
   - CAPTCHA challenges
   - Behavioral analysis

4. Infrastructure:
   - Auto-scaling to absorb traffic
   - Cached content delivery
   - Load balancer connection limits
   - Health check-based instance rotation

Detection mechanisms:
- Traffic baselining and anomaly detection
- NetFlow/sFlow analysis
- Application performance monitoring
- CPU/memory/network utilization spikes
```

---

## IAM

### OAuth 2.0

**Interview Q**: *Explain the OAuth 2.0 Authorization Code flow with PKCE.*

```
Flow:
1. Client initiates request to authorization server with:
   - response_type=code
   - client_id
   - redirect_uri
   - code_challenge (SHA-256 of code_verifier)
   - code_challenge_method=S256

2. User authenticates and authorizes

3. Authorization server redirects to client with authorization code

4. Client exchanges code for token:
   - POST to /token endpoint
   - grant_type=authorization_code
   - code
   - code_verifier (original random string)
   - client_id
   - redirect_uri

5. Authorization server verifies code_verifier against code_challenge

6. Returns access_token, refresh_token, id_token (if OIDC)

Why PKCE?
- Required for public clients (mobile, SPA) without client secret
- Prevents authorization code interception attack
- code_verifier is never sent in the initial request, only its hash
```

### SAML vs OIDC

**Interview Q**: *Compare SAML 2.0 and OpenID Connect.*

```
Answer:
SAML 2.0:
- XML-based (heavy)
- Uses browser redirects (HTTP POST/Redirect bindings)
- SAML assertions contain user attributes
- No built-in token expiration (usually session-based)
- Enterprise-focused (ADFS, Okta, Ping)
- Supports IdP-initiated SSO
- Signed assertions

OpenID Connect:
- JSON-based (lightweight, REST-friendly)
- Uses JWTs for ID tokens
- Built on OAuth 2.0
- Built-in expiration via JWT claims
- Mobile and web app friendly
- Discovery via .well-known/openid-configuration
- Simpler to implement

When to use:
- SAML: Enterprise SSO, legacy apps, government
- OIDC: Modern web apps, mobile apps, APIs, microservices
```

---

## AppSec

### SAST vs DAST

**Interview Q**: *Compare SAST, DAST, IAST, and RASP.*

```
Answer:
SAST (Static Analysis Security Testing):
- White-box (source code)
- Early in SDLC (shift left)
- Detects: SQLi, XSS, injection, hardcoded secrets
- High false positive rate
- Tools: SonarQube, Checkmarx, Fortify

DAST (Dynamic Analysis Security Testing):
- Black-box (running application)
- Late in SDLC (QA/testing stage)
- Detects: Runtime issues, config problems
- Low false positive rate
- Tools: OWASP ZAP, Burp Suite, Acunetix

IAST (Interactive Application Security Testing):
- Hybrid (agent running in application)
- During automated tests
- Combines SAST and DAST advantages
- Low false positive, code-level findings
- Tools: Contrast Security, Seeker

RASP (Runtime Application Self-Protection):
- Runtime protection (inside app or alongside)
- Protects in production
- Blocks attacks in real-time
- No signature updates needed
- Tools: Contrast Protect, Imperva RASP
```

### Secure SDLC

**Interview Q**: *Design a secure software development lifecycle.*

```
Answer:
Phase 1: Requirements
- Security requirements defined
- Threat modeling for new features
- Privacy impact assessment

Phase 2: Design
- Security design review
- Architecture risk analysis
- Data flow diagram with trust boundaries

Phase 3: Development
- Secure coding standards
- SAST scanning in IDE
- Dependency scanning
- Code review with security checklist

Phase 4: Testing
- DAST scanning
- Penetration testing
- Fuzz testing for input handling
- Security regression tests

Phase 5: Deployment
- Infrastructure scanning
- Container image scanning
- Configuration validation
- Secrets scanning

Phase 6: Operations
- Runtime monitoring
- Vulnerability management
- Incident response
- Continuous improvement
```

---

## Infrastructure Security

### Container Security

**Interview Q**: *How do you secure a container from build to runtime?*

```
Answer:
Build:
1. Use minimal base images (alpine, distroless)
2. Scan images for vulnerabilities (Trivy, Clair, Grype)
3. Sign images with Cosign
4. Use multi-stage builds to reduce attack surface
5. Never run as root (USER directive)

Registry:
1. Private registry with access control
2. Vulnerability scanning at push
3. Immutable tags
4. Approved base images only

Deploy:
1. Image policy webhook to enforce signing
2. Never run privileged containers
3. Read-only root filesystem
4. Resource limits to prevent DoS
5. Drop all capabilities, add only needed

Runtime:
1. Runtime security monitoring (Falco, Tracee)
2. Network policies for pod isolation
3. Seccomp/AppArmor profiles
4. Regular image updates and patching
5. Pod Security Standards
```

---

## SecOps

### Incident Response

**Interview Q**: *Walk through a complete incident response process.*

```
Answer:
Preparation:
- Documented IR plan
- Defined roles and contacts
- Tools ready (EDR, SIEM, forensic tools)
- Regular tabletop exercises

Identification:
- Alert from SIEM, EDR, or user report
- Initial triage: Is it real? Severity? Scope?
- Gather initial evidence

Containment:
- Short-term: Block IP, disable user, isolate host
- Long-term: Patch systems, rotate credentials
- Preserve evidence before actions

Eradication:
- Remove malware, backdoors, persistence
- Patch root cause
- Validate no remaining access

Recovery:
- Restore from clean backups
- Monitor for recurrence
- Gradual return to production

Lessons Learned:
- Root cause analysis
- Timeline review
- Control improvements
- Post-incident report
```

---

## GRC

### Compliance Frameworks

**Interview Q**: *What's the difference between a security framework, standard, and regulation?*

```
Answer:
Framework (NIST CSF):
- Guidance, not mandatory
- Flexible implementation
- For improving security posture
- Example: NIST Cybersecurity Framework

Standard (ISO 27001, PCI-DSS):
- Specific requirements
- Auditable and certifiable
- Mandatory for compliance
- Example: PCI-DSS for payment data

Regulation (GDPR, HIPAA):
- Legal requirement
- Has penalties for non-compliance
- Jurisdictional authority
- Example: GDPR for EU personal data
```

---

*Last updated: July 2026*
