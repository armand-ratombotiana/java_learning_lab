# Security Interview Guide — Company Types

> Comparing interview processes for security roles at product companies, security vendors, and consulting firms.
> Understand what to expect, how to prepare, and what each type values.

---

## Table of Contents

1. [Product Companies](#product-companies)
2. [Security Vendors](#security-vendors)
3. [Consulting Firms](#consulting-firms)
4. [Comparison Matrix](#comparison-matrix)
5. [By Company Type — Deep Dives](#by-company-type)
6. [Preparation Strategy](#preparation-strategy)

---

## Product Companies

### Characteristics

| Aspect | Description |
|--------|-------------|
| **Primary Focus** | Build and sell a product (SaaS, platform, or software) |
| **Security Team** | Supports engineering; may be centralized or embedded |
| **Security Priorities** | Product security, infrastructure security, user trust |
| **Examples** | Google, Meta, Apple, Microsoft, Amazon, Shopify, Stripe, Uber |

### Interview Process Structure

```
1. Recruiter screen (30 min)         — Background, role expectations
2. Technical phone screen (45-60 min) — Security + coding fundamentals
3. On-site (4-6 rounds):
   ├── Coding (1-2 rounds)           — LeetCode medium/hard
   ├── System Design (1 round)       — Distributed systems + security
   ├── Security Design (1 round)     — Threat modeling, architecture
   ├── Behavioral (1-2 rounds)       — Past experience, leadership
   └── Lunch / Cross-functional      — Culture fit, collaboration
```

### What They Test

| Area | Weight | Typical Questions |
|------|--------|-------------------|
| **Coding** | 30-40% | Data structures, algorithms, language proficiency |
| **System Design** | 20-30% | Design secure distributed systems, scale considerations |
| **Security Knowledge** | 20-30% | Threat modeling, secure design, cryptography basics |
| **Behavioral** | 10-20% | Conflict resolution, security decision-making, ownership |

### Preparation Strategy

- **Deep coding practice**: LeetCode medium is the baseline
- **System design**: Focus on distributed systems with security constraints
- **Product context**: Understand how security decisions impact the product
- **Trade-offs**: Be ready to discuss security vs user experience vs time-to-market
- **Ownership**: Product companies value engineers who take ownership of security

### Example Scenarios

```
Q: "How do you balance shipping a feature quickly vs ensuring it is secure?"

A framework:
1. Identify the specific security risks
2. Classify by severity and likelihood
3. Implement mitigations proportional to risk
4. Use feature flags for gradual rollout
5. Establish a rollback plan if security issues surface
6. Document residual risk and get sign-off

Key point: You don't have to say "no" — you need to manage risk.
```

```
Q: "A critical vulnerability is found in production. How do you handle it?"

Response structure:
1. CONTAIN — Stop the bleeding (rate limit, disable feature, rollback)
2. ASSESS — Determine scope, affected users, data exposure
3. FIX — Develop and deploy patch with minimal blast radius
4. COMMUNICATE — Internal notification, customer disclosure if needed
5. POST-MORTEM — Root cause analysis, prevention measures
6. FOLLOW-UP — Check for similar patterns across codebase
```

---

## Security Vendors

### Characteristics

| Aspect | Description |
|--------|-------------|
| **Primary Focus** | Build security products (tools, platforms, services) |
| **Security Team** | Deep security expertise, often includes research teams |
| **Security Priorities** | Detection accuracy, threat research, platform security |
| **Examples** | CrowdStrike, Palo Alto Networks, Cloudflare, Okta, HashiCorp, Datadog, SentinelOne, Zscaler |

### Interview Process Structure

```
1. Recruiter screen (30 min)         — Background check
2. Technical screen (45-60 min)      — Deep security domain knowledge
3. On-site (4-6 rounds):
   ├── Security Deep Dive (1-2 rds)  — Domain expertise, product knowledge
   ├── System Design (1 round)       — Security architecture at scale
   ├── Scenario/Case Study (1 rd)    — Customer problem or detection design
   ├── Coding/Implementation (1 rd)  — Language proficiency, secure coding
   └── Behavioral (1 round)          — Passion for security, team culture
```

### What They Test

| Area | Weight | Typical Questions |
|------|--------|-------------------|
| **Security Domain** | 40-50% | Deep knowledge of their security domain |
| **System Design** | 20-25% | Security-specific architecture |
| **Coding** | 15-20% | Practical implementation, not abstract algorithms |
| **Case Studies** | 10-15% | Customer-facing scenarios, incident response |
| **Behavioral** | 5-10% | Industry passion, continuous learning |

### Domain-Specific Focus

#### Network Security Vendors (Palo Alto, Cloudflare, Zscaler)
```
- Deep TCP/IP, DNS, BGP, HTTP knowledge
- Understanding of TLS, mTLS, certificate management
- Firewall architectures (stateful, deep packet inspection)
- DDoS mitigation strategies
- Zero trust networking principles
- SD-WAN and SASE architectures
```

#### Endpoint/EDR Vendors (CrowdStrike, SentinelOne, Microsoft Defender)
```
- OS internals (Windows, Linux, macOS)
- Process injection techniques, persistence mechanisms
- Kernel vs user mode security
- Memory forensics
- Malware analysis and reverse engineering
- MITRE ATT&CK framework mastery
```

#### Identity Vendors (Okta, Ping Identity, ForgeRock)
```
- OAuth 2.0, OIDC, SAML protocol internals
- Federation, SSO, SCIM
- Passwordless authentication, WebAuthn/FIDO2
- Adaptive MFA, risk-based authentication
- Directory services (LDAP, Active Directory)
- Identity governance and administration (IGA)
```

#### Cloud Security Vendors (Wiz, Orca, Palo Alto Prisma Cloud)
```
- CSPM, CWPP, CIEM, CNAPP concepts
- Cloud provider APIs (AWS, Azure, GCP)
- IaC security (Terraform, CloudFormation)
- Container/K8s security
- Compliance frameworks (CIS, NIST, SOC2)
- Agent-based vs agentless scanning
```

#### Infrastructure Security Vendors (HashiCorp)
```
- Secrets management (Vault)
- Infrastructure as Code (Terraform)
- Service mesh (Consul)
- Policy as Code (Sentinel)
- Certificate management (PKI secrets engine)
- Encryption key lifecycle
```

### Preparation Strategy

- **Deep domain expertise**: Go beyond surface-level knowledge
- **Product knowledge**: Use the product, understand its architecture
- **Competitive landscape**: Know how the product compares to alternatives
- **Threat landscape**: Stay current on latest threats and attack techniques
- **Hands-on labs**: Set up the product in a lab environment
- **White papers**: Read the company's technical publications

### Example Scenarios

```
Q: "Design a detection rule for an adversary using WMI for lateral movement."

Approach:
1. Understand WMI lateral movement: wmic.exe /node:TARGET process call create
2. Identify log sources: Windows Event Log 4688 (process creation), 4104 (PowerShell script blocks)
3. Detection logic:
   - Unusual wmic.exe process creation with remote targets
   - Correlation with network connections to admin shares
   - Anomaly detection on WMI activity by non-admin users
4. Tuning considerations: Exclude legitimate IT automation tools
5. Response actions: Alert severity, automated containment playbook
```

```
Q: "A customer wants to migrate from on-prem identity to the cloud. Design the architecture."

Considerations:
1. Hybrid identity strategy (sync vs federated)
2. Password hash sync vs pass-through auth vs federation
3. MFA migration strategy
4. Legacy application compatibility
5. GPO to Conditional Access policy mapping
6. Phasing approach (pilot groups, app migration waves)
7. Rollback plan
8. User communication and training
```

---

## Consulting Firms

### Characteristics

| Aspect | Description |
|--------|-------------|
| **Primary Focus** | Client engagements (audits, assessments, IR, implementation) |
| **Security Team** | Consultants working across multiple clients |
| **Security Priorities** | Incident response, compliance, risk management, architecture |
| **Examples** | Mandiant (Google), CrowdStrike Professional Services, Deloitte Cyber, PwC Cyber, KPMG, Accenture Security |

### Interview Process Structure

```
1. Recruiter screen (30 min)         — Experience, certifications
2. Manager screen (30-45 min)        — Client-facing skills, domain expertise
3. Technical screen (60 min)         — Hands-on scenario, methodology
4. On-site / Final rounds (3-4 rds):
   ├── Case Study (1-2 rounds)       — Full engagement simulation
   ├── Technical (1 round)           — Deep domain expertise
   ├── Presentation (1 round)        — Communicate findings to stakeholders
   └── Behavioral (1 round)          — Client management, travel, teamwork
```

### What They Test

| Area | Weight | Typical Questions |
|------|--------|-------------------|
| **Domain Expertise** | 35-45% | Deep knowledge in their area |
| **Methodology** | 20-25% | How you approach problems (structured process) |
| **Communication** | 15-20% | Explain technical concepts to non-technical audience |
| **Client Management** | 10-15% | Handling difficult clients, scope management |
| **Certifications** | 5-10% | CISSP, CISM, OSCP, SANS GIAC |

### Consulting Role Types

#### Incident Response
```
- Forensics acquisition and analysis
- Malware reverse engineering
- Containment and eradication strategies
- Ransomware negotiation experience (if applicable)
- Legal holds and chain of custody
- Reporting and executive communication
```

#### Penetration Testing
```
- Web app, mobile, network, cloud, social engineering
- OWASP Top 10, PTES, OSSTMM methodologies
- Tool proficiency (Burp Suite, Metasploit, Cobalt Strike)
- Report writing
- Remediation verification
```

#### Governance, Risk & Compliance (GRC)
```
- Regulatory frameworks (PCI-DSS, HIPAA, SOC2, FedRAMP, GDPR)
- Risk assessment methodologies
- Policy development
- Third-party vendor risk management
- Audit management
- Board-level communication
```

#### Security Architecture
```
- Enterprise security architecture (SABSA, TOGAF)
- Zero trust architecture design
- Cloud security architecture
- Network segmentation design
- Identity and access management architecture
```

### Preparation Strategy

- **Certifications**: CISSP is must-have; OSCP for technical roles; CISM for GRC
- **Case studies**: Prepare detailed walkthroughs of past engagements
- **Methodology**: Demonstrate a structured approach to every problem
- **Soft skills**: Client communication, presentation, conflict resolution
- **Industry knowledge**: Regulations, compliance standards, threat landscape
- **Business acumen**: Understand how security drives business value
- **Sales awareness**: Scoping, SOWs, change orders, upselling

### Consulting Case Study Pattern

```
CLIENT SITUATION:
A mid-size fintech company suffered a ransomware attack.
Production databases encrypted, backup systems also affected.
CEO wants to know if they should pay the ransom.

YOUR TASK (as lead IR consultant):

Phase 1 — Triage (first 4 hours)
- Determine scope of compromise
- Identify initial access vector
- Containment strategy
- Preserve evidence

Phase 2 — Investigation (days 1-3)
- Forensic analysis of affected systems
- Determine data exfiltration
- Identify persistence mechanisms
- Root cause analysis

Phase 3 — Remediation (days 4-7)
- Cleanup and eradication plan
- Recovery strategy
- Rebuild guidance

Phase 4 — Reporting (day 7+)
- Executive summary
- Technical findings
- Recommendations
- Timeline

Assessment criteria:
1. Technical rigor — Did you identify the right artifacts?
2. Communication — Can you explain risks to the CEO?
3. Decision-making — How do you approach the ransom decision?
4. Process — Do you follow a structured IR framework?
5. Adaptability — How do you handle incomplete information?
```

---

## Comparison Matrix

| Dimension | Product Companies | Security Vendors | Consulting Firms |
|-----------|-----------------|-----------------|------------------|
| **Coding Skill** | High (LeetCode) | Medium (practical) | Low-Medium |
| **Security Depth** | Medium | High | High |
| **System Design** | High | High | Medium |
| **Communication** | Medium | Medium | High (client-facing) |
| **Certifications** | Low priority | Low-Medium | High (CISSP, OSCP) |
| **Domain Breadth** | Company-specific | Security domain-specific | Client-dependent |
| **Work Pace** | Product release-driven | Release cycles + research | Billable hour-driven |
| **Travel** | Rare | Occasionally | Often (client site) |
| **Career Growth** | Engineering ladder | Security-specific ladder | Partner track |

---

## By Company Type — Deep Dives

### Big Tech (Google, Meta, Apple, Microsoft, Amazon)

| Aspect | Detail |
|--------|--------|
| **Interview Difficulty** | Very High |
| **Coding Bar** | LeetCode Hard for some (Google, Meta) |
| **System Design Bar** | Distributed systems at planet scale |
| **Security Bar** | Deep but not vendor-level deep |
| **Compensation** | Top of market (300k-600k+ total comp) |
| **Typical Background** | CS degree, 5+ YoE, previous security roles |
| **Hiring Volume** | Selective, but large teams |

### Security Unicorns (CrowdStrike, Wiz, Datadog, Cloudflare)

| Aspect | Detail |
|--------|--------|
| **Interview Difficulty** | High |
| **Coding Bar** | Practical Go/Python coding |
| **System Design Bar** | Security-focused distributed systems |
| **Security Bar** | Very High (you must know their domain) |
| **Compensation** | 200k-500k total comp |
| **Typical Background** | Security specialty, previous security vendor experience |
| **Hiring Volume** | Growing rapidly |

### Traditional Security (Cisco, Palo Alto, McAfee, Trend Micro)

| Aspect | Detail |
|--------|--------|
| **Interview Difficulty** | Medium-High |
| **Coding Bar** | Moderate, often product-specific |
| **System Design Bar** | Network/product security architecture |
| **Security Bar** | High (certifications valued) |
| **Compensation** | 150k-350k total comp |
| **Typical Background** | Network engineering, security operations, 10+ YoE |
| **Hiring Volume** | Stable, replacement-driven |

### Identity & Access (Okta, Ping, ForgeRock, Auth0)

| Aspect | Detail |
|--------|--------|
| **Interview Difficulty** | High |
| **Coding Bar** | Java/Go coding, identity protocol implementation |
| **System Design Bar** | Identity services at scale |
| **Security Bar** | Very High (OAuth, OIDC, SAML mastery expected) |
| **Compensation** | 180k-400k total comp |
| **Typical Background** | Identity engineer, authentication specialist |
| **Hiring Volume** | Growing |

### Cloud & Infrastructure (HashiCorp, Confluent, Elastic)

| Aspect | Detail |
|--------|--------|
| **Interview Difficulty** | High |
| **Coding Bar** | Go coding, infrastructure tooling |
| **System Design Bar** | Distributed infrastructure security |
| **Security Bar** | Infrastructure security, cloud IAM |
| **Compensation** | 175k-400k total comp |
| **Typical Background** | Cloud/SRE background with security focus |
| **Hiring Volume** | Growing |

### Consulting (Big 4, Mandiant, CrowdStrike Services)

| Aspect | Detail |
|--------|--------|
| **Interview Difficulty** | Medium |
| **Coding Bar** | Low (unless technical role) |
| **System Design Bar** | Medium (architecture assessments) |
| **Security Bar** | High (CISSP expected) |
| **Compensation** | 100k-250k total comp (Partner: 300k+) |
| **Typical Background** | Former SOC, military, or internal security team |
| **Hiring Volume** | High (constant demand) |

---

## Preparation Strategy

### 6-Week Interview Prep Plan

| Week | Focus | Activities |
|------|-------|------------|
| **1** | **Self-assessment** | Identify target companies, assess gaps, gather resources |
| **2** | **Coding basics** | LeetCode Easy/Medium, language-specific security patterns |
| **3** | **Security fundamentals** | OWASP Top 10, MITRE ATT&CK, cryptography basics |
| **4** | **System design** | Practice 2-3 security system designs, review patterns |
| **5** | **Company-specific** | Deep dive into target company products and architecture |
| **6** | **Mock interviews** | Full mock rounds, behavioral preparation, whiteboard practice |

### Resource Recommendations

#### General Security
- OWASP Top 10 (2021) — Web security essentials
- MITRE ATT&CK — Adversary tactics and techniques
- NIST SP 800-53 — Security controls catalog
- SANS Reading Room — Security whitepapers

#### Coding
- LeetCode — Practice medium difficulty
- HackerRank — Language-specific challenges
- Cracking the Coding Interview — General prep
- Codility — Many companies use this platform

#### System Design
- "System Design Interview" — Alex Xu
- "Designing Data-Intensive Applications" — Martin Kleppmann
- Security architecture patterns (Zero Trust, SASE, SIEM)

#### Company Research
- Company engineering blogs
- Conference talks (re:Invent, Google I/O, RSA, BlackHat)
- Quarterly security transparency reports
- CVE databases for the company's products

### Red Flags to Avoid

```
❌ Claiming expertise in areas you don't know
❌ Ignoring the business context of security decisions
❌ Presenting only problems without solutions
❌ Being inflexible on technology choices
❌ Not asking questions about the team or role
❌ Neglecting behavioral preparation
```

---

## Post-Interview

### Follow-up Strategy

```
1. Send thank-you notes within 24 hours
2. Reference specific topics discussed
3. Add a brief follow-up answering any open questions
4. Reiterate enthusiasm for the role
5. Ask about next steps and timeline
```

### Negotiation Considerations

| Factor | Product | Vendor | Consulting |
|--------|---------|--------|------------|
| **Base Salary** | High | High | Medium |
| **Equity/RSUs** | Significant | Significant | Minimal |
| **Bonus** | 10-20% | 10-20% | 10-30% (billable) |
| **Sign-on** | Common | Common | Rare |
| **Remote** | Often hybrid | Often remote | Client-site dependent |
| **Certification Bonus** | Rare | Sometimes | Common |

### Evaluating Offers

```
Key factors beyond compensation:
1. Team culture and security maturity
2. Impact scope (product vs infrastructure vs research)
3. Career growth trajectory
4. Learning opportunities
5. Management quality
6. Work-life balance
7. Technology stack
8. Company security maturity (are they eating their own dogfood?)
```

---

*Last updated: July 2026*
