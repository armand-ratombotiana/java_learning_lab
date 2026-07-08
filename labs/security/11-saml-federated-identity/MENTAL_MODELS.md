# MENTAL MODELS: 11-saml-federated-identity

## Mental Models

### Core Mental Models

### Defense in Depth
Think of security like layers of an onion. If one layer is breached, the next layer still protects. Each security control is independent and overlapping.

**Application**: Never rely on a single security mechanism. Combine authentication, authorization, encryption, input validation, and monitoring.

### Least Privilege
Every user, service, and component should have exactly the permissions needed and nothing more.

**Application**: Start with zero permissions and grant only what's necessary. Review and revoke unused permissions regularly.

### Assume Breach
Design your system assuming an attacker has already compromised some components.

**Application**: Segment networks, encrypt sensitive data, implement monitoring, have incident response plans ready.

### Fail Secure
When a security control fails, it should deny access rather than allow it.

**Application**: Default-deny policy for access control. If authentication service is down, reject requests rather than allowing anonymous access.

### Security vs Usability Trade-off
Stronger security often means more friction for users. Find the right balance.

**Application**: Use adaptive authentication - simple auth for low-risk actions, MFA for sensitive operations.

### Trust But Verify
Even trusted components should validate inputs and verify their assumptions.

**Application**: Validate data between microservices even within the same network.

### Security by Design
Security cannot be bolted on after development. It must be part of the architecture.

**Application**: Include security requirements in design documents, threat model during architecture review.

### The Human Element
Most security breaches involve human error. Design systems that are secure by default.

**Application**: Make the secure path the easy path. Use sensible defaults that are secure.

### Defense in Depth Diagram

`
Layer 1: Perimeter (Firewall, WAF)
Layer 2: Network (Segmentation, mTLS)
Layer 3: Application (Auth, Authz, Validation)
Layer 4: Data (Encryption, Masking)
Layer 5: Monitoring (SIEM, Alerting)
Layer 6: Response (IR, Forensics)
`

### Trade-off Analysis

| Security Measure | User Impact | Security Benefit | Implementation Cost |
|-----------------|-------------|------------------|-------------------|
| MFA Required | High | Very High | Medium |
| Session Timeout | Low | Medium | Low |
| Rate Limiting | Low | High | Low |
| Audit Logging | None | High | Medium |
| Encryption | None | Very High | Low |

### Mental Models for Decision Making

### The Castle and the Moat (Old Model)
Traditional security focused on building a strong perimeter with deep moats. Everything inside was trusted. This model fails with cloud, mobile, and remote work.

### The Airport Model (Zero Trust)
Every person is verified at every access point. Trust is never inherited from location. Access is granular and time-limited.

### The Swiss Cheese Model
Each security control has holes (imperfections). Multiple controls stacked together reduce the probability of all holes aligning.

### The CISO Framework
- **Prevent**: Stop attacks before they succeed
- **Detect**: Identify attacks in progress
- **Respond**: Take action against active threats
- **Recover**: Restore normal operations after incidents

### The 80/20 Rule in Security
80% of security value comes from 20% of controls:
1. Multi-factor authentication
2. Regular patching and updates
3. Input validation and sanitization
4. Least privilege access control
5. Security awareness training

### The Security Pyramid

`
        /\         Incident Response
       /  \        Monitoring & Detection
      /    \       Access Control
     /      \      Data Protection
    /        \     Application Security
   /          \    Network Security
  /            \   Physical Security
 /______________\  Security Governance
`

Each level builds on the level below. Strong foundations enable effective upper-layer controls.

### Decision Framework

When making security decisions, consider:
1. **What is the threat?** - Understand the attack vector
2. **What is the impact?** - Data sensitivity, system criticality
3. **What is the probability?** - Likelihood of exploitation
4. **What is the cost?** - Implementation and operational cost
5. **What is the user impact?** - Friction and usability

### Risk Assessment Matrix

| Likelihood | Negligible | Minor | Moderate | Major | Critical |
|------------|-----------|-------|----------|-------|----------|
| Almost Certain | Medium | High | High | Critical | Critical |
| Likely | Medium | Medium | High | High | Critical |
| Possible | Low | Medium | Medium | High | High |
| Unlikely | Low | Low | Medium | Medium | High |
| Rare | Low | Low | Low | Medium | Medium |

### Threat Modeling Approach (STRIDE)

- **S**poofing: Impersonating users or systems
- **T**ampering: Modifying data or code
- **R**epudiation: Denying actions taken
- **I**nformation Disclosure: Exposing sensitive data
- **D**enial of Service: Disrupting service availability
- **E**levation of Privilege: Gaining unauthorized access

Each threat type has corresponding security controls. STRIDE is applied per component during architecture review to identify and mitigate potential threats.
