# Security Academy — Company Interview Guide

> Comprehensive interview preparation for security roles at top technology companies.
> Covers role types, interview processes, and specific knowledge tested per company.

---

## Table of Contents

1. [Google (Project Zero, Cloud Security)](#1-google)
2. [Amazon (AWS Security, GuardDuty)](#2-amazon)
3. [Meta (Security Engineer)](#3-meta)
4. [Microsoft (Azure Security, Defender)](#4-microsoft)
5. [Apple (Privacy, Device Security)](#5-apple)
6. [CrowdStrike](#6-crowdstrike)
7. [Palo Alto Networks](#7-palo-alto)
8. [Cloudflare](#8-cloudflare)
9. [Datadog](#9-datadog)
10. [Okta](#10-okta)
11. [HashiCorp](#11-hashicorp)

---

## 1. Google

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Information Security Engineer** | Infrastructure security, network security, access management | CorpEng, Cloud Security |
| **Application Security Engineer** | Secure development, code review, fuzzing | Project Zero, Chrome Security |
| **Security Engineer (Red Team)** | Offensive security, adversary simulation | Google Red Team |
| **Security Engineer (Detection & Response)** | Monitoring, forensics, incident response | Threat Analysis Group (TAG) |
| **Site Reliability Engineer (Security)** | Security infrastructure, automation | Google Cloud SRE |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Phone Screen** | Technical phone call | 45 min | General security knowledge, past experience |
| **Technical Phone** | Coding + Security | 60 min | Algorithms + security scenario |
| **Onsite — Security Deep Dive** | Whiteboard | 45 min | Security architecture, threat modeling |
| **Onsite — Coding** | Whiteboard | 45 min | Data structures, algorithms (LeetCode medium/hard) |
| **Onsite — System Design** | Whiteboard | 45 min | Design a secure system |
| **Onsite — Googliness** | Behavioral | 45 min | Leadership, ambiguity handling |
| **Onsite — Cross-functional** | Panel | 45 min | Collaboration, stakeholder management |

### Specific Knowledge Tested

- **Google Cloud Security**: IAM, VPC Service Controls, Access Transparency, CMEK, CSEK, Cloud Armor, Security Command Center
- **BeyondCorp / Zero Trust**: Google's implementation of zero trust networking
- **Project Zero**: Vulnerability research, exploit mitigation, fuzzing (libFuzzer, AFL)
- **TLS/SSL**: Google's mTLS implementations, BoringSSL
- **Kubernetes Security**: Binary Authorization, GKE Security, Pod Security Policies
- **Internal Security Infrastructure**: ALES (Alice), Google's internal encryption infrastructure
- **Cryptography**: AEAD, Key Transparency, Certificate Transparency
- **Sawyer (Security Review)**: Google's internal security review platform
- **Buganizer**: Issue tracking for security bugs

### Common Interview Questions

```
1. Design a secure key management system for a distributed storage service.
2. How would you detect and respond to a data exfiltration via DNS tunneling?
3. Implement a secure password hashing scheme. What algorithm would you choose?
4. Design a certificate transparency log system.
5. How would you secure a multi-tenant Kubernetes cluster?
6. Describe the threat model for a cloud-native application.
7. How does Google's BeyondCorp architecture work?
8. Design a system to detect account takeover at scale.
```

### Preparation Resources

- Google Cloud Security Foundations Guide
- BeyondCorp whitepapers (Google Research)
- Project Zero blog posts
- Google Online Security Blog
- Tink (Google's cryptography library) documentation

---

## 2. Amazon

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Incident response, security operations | AWS Security, Amazon CSO |
| **Application Security Engineer** | Secure development lifecycle, penetration testing | Amazon AppSec, Alexa Security |
| **Security Assurance Engineer** | Compliance, auditing, risk management | AWS ProServe Security |
| **Security Solutions Architect** | Customer-facing security architecture | AWS Solutions Architecture |
| **SDE (Security)** | Building security tools and services | GuardDuty, Inspector, Detective |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Phone Screen** | Technical + Behavioral | 60 min | Leadership Principles + Security basics |
| **Technical Phone** | Security deep dive | 60 min | Previous experience, whiteboard security |
| **Onsite — Security Design** | Whiteboard | 60 min | Design a secure system |
| **Onsite — Coding** | Whiteboard | 60 min | Data structures, algorithms |
| **Onsite — Bar Raiser** | Behavioral | 60 min | Leadership Principles deep dive |
| **Onsite — Manager** | Career conversation | 45 min | Role alignment, growth |

### Specific Knowledge Tested

- **AWS Security Services**: GuardDuty, Inspector, Detective, Security Hub, Macie, WAF, Shield, CloudTrail, Config
- **IAM Deep Dive**: Policy evaluation logic, permission boundaries, service control policies, resource-based policies
- **KMS & CloudHSM**: Key rotation, HSM-backed keys, envelope encryption
- **VPC Security**: Security groups vs NACLs, VPC endpoints, privatelink, network ACLs
- **S3 Security**: Bucket policies, ACLs, block public access, object lock, encryption in transit/at rest
- **Lambda Security**: Execution roles, Lambda@Edge security, cold start attacks
- **Amazon's Internal Security**: Cell-based architecture, two-pizza team model security
- **Leadership Principles**: Customer Obsession, Ownership, Dive Deep, Have Backbone, Insist on Highest Standards

### Common Interview Questions

```
1. Design a secure identity and access management system for a cloud object store.
2. How would you architect GuardDuty's threat detection pipeline?
3. A customer reports a potential S3 data breach. Walk through your response.
4. Design a least-privilege IAM policy for a serverless application.
5. How would you secure a multi-account AWS organization?
6. Describe how certificate revocation works in AWS Private CA.
7. Design a system to detect crypto-mining in EC2 instances.
8. How do you enforce encryption at rest across all AWS services?
```

### Preparation Resources

- AWS Well-Architected Framework (Security Pillar)
- AWS Security Best Practices whitepaper
- AWS re:Invent security sessions
- AWS IAM documentation
- GuardDuty threat detection findings

---

## 3. Meta

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Product security, infrastructure security | Facebook App Security, WhatsApp Security |
| **Detection & Response Engineer** | Threat detection, IR | Meta Threat Response |
| **Red Team Engineer** | Adversarial simulation | Meta Red Team |
| **Privacy Engineer** | Privacy engineering, data governance | Privacy Infrastructure |
| **Security Software Engineer** | Building security platforms | Security Tools, Threat Prevention |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Recruiter Screen** | Non-technical | 30 min | Experience, role alignment |
| **Technical Screen** | Coding + Security | 45 min | Algorithms basics, security scenario |
| **Onsite — Coding** | Whiteboard/Code | 45 min | Data structures (2 rounds) |
| **Onsite — Security** | Whiteboard | 45 min | Security architecture, threat modeling |
| **Onsite — System Design** | Whiteboard | 45 min | Design a secure large-scale system |
| **Onsite — Behavioral** | Behavioral | 45 min | Meta's values, security decision-making |

### Specific Knowledge Tested

- **Osquery**: Meta's SQL-based OS instrumentation tool (open source)
- **End-to-End Encryption**: WhatsApp's Signal Protocol integration
- **Secure Enclave**: Trusted execution environments
- **Account Security**: Login approvals, two-factor, suspicious login detection
- **Content Security**: Malware detection, phishing detection at scale
- **Infrastructure Security**: Server fleet management, container security
- **GraphQL Security**: Depth limiting, query whitelisting, authentication
- **Hack (PHP) Security**: Meta's PHP variant, security aspects
- **Bug Bounty Program**: Meta's industry-leading bug bounty structure

### Common Interview Questions

```
1. Design an end-to-end encrypted messaging system for billions of users.
2. How would you detect and block account takeover attacks at scale?
3. Design a secure storage system for user biometric data.
4. How would you implement a cross-platform secure credential store?
5. Describe the threat model for a social media platform.
6. Design a system to enforce data retention policies globally.
7. How would you secure GraphQL APIs against malicious queries?
8. Design a phishing detection system for a messaging platform.
```

---

## 4. Microsoft

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Azure security, identity security | Azure Security Engineering |
| **Offensive Security Engineer** | Red teaming, penetration testing | Microsoft Red Team |
| **Defensive Security Engineer** | Detection, response, threat hunting | Microsoft Defender |
| **Security Researcher** | Vulnerability research, exploit development | Microsoft Security Response Center |
| **Security Architect** | Enterprise security architecture | Azure Architecture |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Phone Screen** | Technical + Behavioral | 45 min | Security fundamentals, experience |
| **Technical Phone** | Security + Coding | 60 min | Security scenarios, basic coding |
| **Onsite — Security Design** | Whiteboard | 45 min | Design secure Azure services |
| **Onsite — Coding** | Whiteboard | 45 min | Algorithms (LeetCode medium) |
| **Onsite — System Design** | Whiteboard | 45 min | Large-scale distributed security |
| **Onsite — Growth & Leadership** | Behavioral | 45 min | Microsoft culture fit |

### Specific Knowledge Tested

- **Azure Security**: Azure AD (Entra ID), Defender for Cloud, Sentinel, Key Vault, Policy, Blueprints
- **Active Directory**: Entra ID, Kerberos, NTLM, LDAP security, group policy security
- **Windows Security**: Windows Defender, Credential Guard, Device Guard, BitLocker, Secure Boot
- **Office 365 Security**: Exchange Online Protection, Defender for Office 365, DLP
- **Microsoft Defender Suite**: Defender for Endpoint, Identity, Cloud Apps, IoT
- **Azure Sentinel**: SIEM/SOAR, KQL (Kusto Query Language)
- **Secure Development Lifecycle (SDL)**: Microsoft's SDL, threat modeling (STRIDE)
- **Identity Security**: On-prem to cloud identity, hybrid identity, conditional access
- **PowerShell Security**: Secure scripting, constrained endpoints, JEA

### Common Interview Questions

```
1. Design a system to manage security policies across millions of devices.
2. How would you detect a pass-the-hash attack in a hybrid Active Directory environment?
3. Design a conditional access policy framework for a global enterprise.
4. How does Windows Defender use machine learning for malware detection?
5. Design a secure CI/CD pipeline for deploying to Azure Kubernetes Service.
6. Describe the STRIDE threat modeling methodology with an example.
7. How would you architect a DLP solution for Office 365?
8. Design a secure domain join process for Azure AD-joined devices.
```

---

## 5. Apple

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Platform security, device security | iOS Security, macOS Security |
| **Application Security Engineer** | Third-party review, internal tools | App Review, Security Engineering |
| **Cryptography Engineer** | Cryptographic implementation | CoreCrypto, Secure Enclave |
| **Privacy Engineer** | Privacy-by-design, data minimization | Privacy Infrastructure |
| **Security Researcher** | Vulnerability discovery | Apple Security Research |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Phone Screen** | Technical + Experience | 45 min | Security expertise, projects |
| **Technical Phone** | Security deep dive | 60 min | Platform-specific security |
| **Onsite — Architecture** | Whiteboard | 45 min | Secure system design |
| **Onsite — Implementation** | Coding | 45 min | Systems programming, C/C++ |
| **Onsite — Security** | Whiteboard | 45 min | Cryptography, threat models |
| **Onsite — Behavioral** | Cross-functional | 45 min | Collaboration, product thinking |

### Specific Knowledge Tested

- **iOS Security**: Secure Enclave, Keychain, Data Protection, App Sandbox, Code Signing
- **macOS Security**: System Integrity Protection (SIP), FileVault, Gatekeeper, Notarization
- **Secure Enclave**: Hardware security module, biometric data processing
- **Apple Silicon Security**: M1/M2/M3 Secure Boot, SEP, hardware-accelerated crypto
- **Cryptography**: CoreCrypto, CommonCrypto, Apple's cryptographic frameworks
- **Privacy Features**: App Tracking Transparency, Privacy Labels, On-device processing
- **Sign in with Apple**: Private email relay, cross-platform SSO
- **iCloud Security**: End-to-end encryption, CloudKit security, data protection classes
- **Find My Network**: Crowd-sourced location, end-to-end encrypted
- **AirTag Security**: Unwanted tracking detection, privacy safeguards

### Common Interview Questions

```
1. Design a secure enclave for a mobile device (hardware + software).
2. How does Apple implement end-to-end encryption for iCloud backups?
3. Design a system that prevents a compromised kernel from accessing user data.
4. How would you implement a privacy-preserving analytics system?
5. Describe the threat model for a mobile payment system.
6. Design a code signing system for a mobile app store.
7. How does Secure Boot work from hardware root of trust to OS launch?
8. Design a system to detect and prevent sideloaded malware on mobile devices.
```

---

## 6. CrowdStrike

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Detection engineering, Falcon platform | Falcon OverWatch |
| **Incident Response Consultant** | Breach response, forensics | Falcon Complete |
| **Threat Intelligence Analyst** | Threat research, adversary profiling | CrowdStrike Intelligence |
| **Security Architect** | Customer security architecture | Sales Engineering |
| **Software Engineer (Security)** | Platform development | Falcon Platform |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Recruiter Screen** | Non-technical | 30 min | Background, availability |
| **Technical Screen** | Security scenario | 60 min | Incident response, EDR concepts |
| **Onsite — Technical** | Whiteboard | 45 min | Malware analysis, detection patterns |
| **Onsite — Scenario** | Case study | 60 min | Full IR scenario walk-through |
| **Onsite — Behavioral** | Leadership | 45 min | Team collaboration, handling pressure |

### Specific Knowledge Tested

- **CrowdStrike Falcon**: EDR architecture, sensor deployment, detection logic
- **Incident Response**: NIST IR framework, containment strategies, forensic imaging
- **Malware Analysis**: Reverse engineering, packer detection, memory forensics
- **Windows Internals**: Processes, threads, memory management, registry, NTFS
- **MITRE ATT&CK**: Tactics, techniques, procedure mapping, detection gaps
- **Threat Intelligence**: Adversary attribution, IoC extraction, TTP analysis
- **Cloud Security**: AWS, Azure, GCP security, container forensics
- **Endpoint Detection**: Process injection detection, persistence mechanisms, privilege escalation

### Common Interview Questions

```
1. Walk through a complete incident response for a ransomware attack.
2. How does CrowdStrike Falcon detect process injection?
3. Design a detection rule for an adversary using LOLBins for lateral movement.
4. How would you handle a false positive that is impacting critical business systems?
5. Describe the forensic artifacts associated with a hands-on-keyboard attack.
6. How would you detect credential dumping via LSASS?
7. Design a threat hunting hypothesis for a supply chain compromise.
8. How do you measure detection coverage against MITRE ATT&CK?
```

---

## 7. Palo Alto Networks

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Network security, NGFW | Prisma Access |
| **Cloud Security Architect** | Cloud security posture | Prisma Cloud |
| **Security Researcher** | Threat research, signature dev | Unit 42 |
| **Solutions Engineer** | Pre-sales technical | Field Sales |
| **SOC Engineer** | Managed security services | Cortex XSIAM |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Recruiter Screen** | Non-technical | 30 min | Role alignment |
| **Technical Phone** | Network security | 60 min | Firewall, IPS/IDS, cloud security |
| **Onsite — Technical** | Whiteboard | 45 min | Security architecture |
| **Onsite — Scenario** | Case study | 60 min | Customer deployment scenario |
| **Onsite — Behavioral** | Cultural | 45 min | Team fit |

### Specific Knowledge Tested

- **PAN-OS**: Next-gen firewall features, App-ID, User-ID, Content-ID
- **Prisma Cloud**: CNAPP, CSPM, CWPP, IaC scanning, CIEM
- **Cortex XDR/XSIAM**: Extended detection, SOAR, data lake
- **Zero Trust**: PAN's Zero Trust framework, ZTNA 2.0
- **Network Security**: TCP/IP, SSL/TLS inspection, DNSSEC, BGP security
- **Automation**: Pan-os-ansible, terraform provider, REST APIs
- **Cloud Security**: AWS/Azure/GCP security group management

### Common Interview Questions

```
1. How does a next-generation firewall differ from a traditional firewall?
2. Design a zero-trust network architecture for a hybrid workforce.
3. How would you detect encrypted malware traffic without decryption?
4. Design a cloud security posture management system.
5. How does App-ID identify applications regardless of port/protocol?
6. Describe how to secure a multi-cloud deployment with Prisma Cloud.
7. How would you handle a DDoS attack targeting a customer's web application?
8. Design a logging and alerting strategy for a global distributed enterprise.
```

---

## 8. Cloudflare

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Platform security, infrastructure | Infrastructure Security |
| **Security Researcher** | Vulnerability research, threat intel | Cloudflare Threat Research |
| **Application Security Engineer** | Product security, bug bounty | Product Security |
| **Network Security Engineer** | DDoS mitigation, WAF | Edge Security |
| **Security Systems Engineer** | Building internal tools | Authentication, Secrets |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Recruiter Call** | Non-technical | 30 min | Experience, interest |
| **Technical Screen** | Security fundamentals | 60 min | Web security, networking |
| **Onsite — System Design** | Whiteboard | 45 min | Distributed security systems |
| **Onsite — Incident** | Case study | 45 min | Crisis management, breach response |
| **Onsite — Coding** | Code review | 45 min | Security code review, Go/Rust |
| **Onsite — Product** | Cross-team | 45 min | Collaboration, user empathy |

### Specific Knowledge Tested

- **CDN Security**: DDoS mitigation, WAF, bot management, rate limiting
- **Cloudflare Workers**: Serverless security, edge computing, V8 isolation
- **SSL/TLS**: Universal SSL, custom certificates, mTLS, Certificate Transparency
- **DNS Security**: DNSSEC, DNS-over-HTTPS, DNS-over-TLS, authoritative DNS
- **Zero Trust (Cloudflare One)**: Access, Gateway, Browser Isolation
- **Web Security**: OWASP Top 10, CORS, CSP, HSTS, HPKP
- **Network Security**: BGP security, RPKI, anycast networking, DDoS protection
- **Cryptography**: Cloudflare's crypto libraries (CIRCL), TLS 1.3, post-quantum
- **Security Headers**: Cloudflare's approach to enforcing security headers
- **go-cve-search**: Internal vulnerability management

### Common Interview Questions

```
1. Design a DDoS mitigation system that can handle 10 Tbps attacks.
2. How does Cloudflare implement TLS 1.3 at the edge?
3. Design a WAF rule engine that operates on every HTTP request globally.
4. How would you build a secure WebSocket proxy that inspects traffic?
5. Describe how Certificate Transparency works and why it matters.
6. Design a bot detection system using only edge data.
7. How would you secure a Workers-based application processing sensitive data?
8. Design a key rotation system for thousands of edge locations.
```

### Preparation Resources

- Cloudflare Learning Center
- Cloudflare Blog (engineering and security)
- CIRCL (Go cryptographic library)
- Cloudflare Workers documentation
- Argo Tunnel and Zero Trust docs

---

## 9. Datadog

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Platform security, product security | Security Engineering |
| **Security Research Engineer** | Threat detection research | Security Research |
| **Detection & Response Engineer** | Monitoring, alerting, IR | Detection & Response |
| **Security Software Engineer** | Building security features | Agent Security, Cloud SIEM |
| **SRE (Security)** | Infrastructure security | Security SRE |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Technical Screen** | Phone | 45 min | Security + basic coding |
| **Security Screen** | Phone | 60 min | Detection engineering, monitoring |
| **Onsite — Coding** | Pair programming | 45 min | Python/Go, algorithm design |
| **Onsite — System Design** | Whiteboard | 45 min | Observability at scale |
| **Onsite — Security** | Scenario | 45 min | Threat detection, case studies |
| **Onsite — Behavioral** | Manager | 45 min | Team collaboration, growth |

### Specific Knowledge Tested

- **Datadog Security Products**: Cloud SIEM, CSPM, CWS (Cloud Workload Security), Application Security
- **Detection Engineering**: Sigma rules, correlation logic, anomaly detection
- **Agent Security**: Datadog Agent architecture, secure communication, privilege separation
- **Observability Security**: Log protection, sensitive data scrubbing, RBAC for observability
- **Cloud Security**: Agent-based vs API-based CSPM, runtime threat detection
- **Kubernetes Security**: Admission controller, OPA integration, pod security standards
- **Signal Sciences (Cloudflare WAF)**: Datadog's WAF capabilities
- **Go and Python**: Primary languages, security code review

### Common Interview Questions

```
1. Design a cloud security posture management system that monitors multiple clouds.
2. How would you detect a data exfiltration using DNS in a monitored environment?
3. Design a correlation rule that detects crypto-mining across multiple hosts.
4. How does the Datadog Agent securely communicate with the backend?
5. Design a system to automatically remediate a misconfigured S3 bucket.
6. How would you detect and alert on a Kubernetes privilege escalation?
7. Design a sensitive data scanning pipeline for log data.
8. How do you test detection rules to minimize false positives?
```

---

## 10. Okta

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Identity security, authentication | Security Engineering |
| **Product Security Engineer** | Secure development, code review | Product Security |
| **Security Architect** | Architecture review, threat modeling | Architecture Review Board |
| **Security Researcher** | Identity protocols, vulnerability discovery | Security Research |
| **Customer Trust Engineer** | Customer security engagements | Customer Trust |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Recruiter Screen** | Non-technical | 30 min | Background check |
| **Technical Screen** | Identity protocols | 60 min | OAuth, OIDC, SAML |
| **Onsite — Security** | Whiteboard | 45 min | Identity architecture |
| **Onsite — System Design** | Whiteboard | 45 min | Scaling identity services |
| **Onsite — Coding** | Implementation | 45 min | Secure coding in Java/Go |
| **Onsite — Behavioral** | Leadership | 45 min | Customer focus, ownership |

### Specific Knowledge Tested

- **OAuth 2.0**: Authorization code flow, PKCE, implicit flow, client credentials, refresh tokens
- **OpenID Connect**: ID tokens, UserInfo endpoint, discovery, dynamic client registration
- **SAML 2.0**: SAML requests/responses, assertions, metadata, SSO flows
- **Okta APIs**: Okta API authentication, rate limiting, webhooks, event hooks
- **Workforce Identity**: Universal Directory, lifecycle management, app integration
- **Customer Identity (Auth0)**: CIAM, social login, MFA, passwordless
- **Session Management**: Session tokens, cookie security, session revocation
- **MFA Methods**: TOTP, SMS, push notification, WebAuthn, biometric
- **Threat Detection**: Okta ThreatInsight, suspicious login detection, anomaly detection
- **Zero Trust**: Okta's zero trust architecture, device trust, adaptive MFA

### Common Interview Questions

```
1. Design an OAuth 2.0 authorization server handling 100k+ RPS.
2. How would you implement single sign-out across multiple applications?
3. Design a secure session management system for a workforce identity platform.
4. Describe the security considerations of the OAuth 2.0 authorization code flow with PKCE.
5. Design a system to detect and block credential stuffing attacks.
6. How would you architect a multi-tenant identity platform with tenant isolation?
7. Design a privilege escalation attack path in a poorly configured Okta org.
8. How does WebAuthn prevent phishing compared to traditional passwords?
```

---

## 11. HashiCorp

### Role Types

| Role | Focus Areas | Team Examples |
|------|------------|---------------|
| **Security Engineer** | Infrastructure security, platform sec | Vault Security, Consul Security |
| **Application Security Engineer** | Product security, code review | Product Security |
| **Security Researcher** | Vulnerability research | Security Research |
| **Security Solutions Engineer** | Customer deployments | Field Engineering |
| **Cloud Security Engineer** | HCP (HashiCorp Cloud Platform) | Cloud Platform Security |

### Interview Process

| Round | Format | Duration | Focus |
|-------|--------|----------|-------|
| **Recruiter Screen** | Non-technical | 30 min | Role fit |
| **Technical Screen** | Infrastructure security | 60 min | Vault, Terraform security |
| **Onsite — Technical** | Whiteboard | 45 min | Security architecture |
| **Onsite — Implementation** | Coding | 45 min | Go, secure infrastructure |
| **Onsite — Design** | Whiteboard | 45 min | Distributed systems security |
| **Onsite — Behavioral** | Cultural | 45 min | HashiCorp principles |

### Specific Knowledge Tested

- **Vault**: Secret engines, auth methods, dynamic secrets, encryption-as-a-service, replication
- **Terraform Security**: IaC security, state management, provider security, `terraform plan` security review
- **Consul Security**: Service mesh, mTLS, intentions, gossip encryption
- **Nomad Security**: Workload isolation, ACLs, Vault integration
- **Boundary**: Identity-based access management for infrastructure
- **Waypoint**: Build/deploy pipeline security
- **HCP Security**: Multi-tenant isolation, key management, compliance
- **Consul-Template & Vault Agent**: Secure secret injection
- **Sentinel**: Policy-as-code for compliance enforcement
- **Go Security**: Secure Go coding, memory safety, concurrency

### Common Interview Questions

```
1. Design a dynamic secrets engine that generates short-lived database credentials.
2. How would you secure a multi-tenant Terraform state storage backend?
3. Design a service mesh mTLS system with automatic certificate rotation.
4. How do you implement policy-as-code for cloud infrastructure deployment?
5. Design a system to securely distribute encryption keys to thousands of services.
6. How would you architect Vault in a disaster recovery configuration?
7. Describe the security model of HashiCorp Consul's gossip protocol.
8. Design an audit logging system that captures all infrastructure changes.
```

### Preparation Resources

- HashiCorp Security Model whitepapers
- Vault documentation (production hardening)
- Terraform Security Best Practices
- Consul Service Mesh documentation
- HashiCorp Learn platform
- HashiCorp Security Advisories

---

## Interview Preparation Checklist

### For All Companies

- [ ] Review OWASP Top 10 (OWASP Top 10:2021)
- [ ] Understand MITRE ATT&CK framework
- [ ] Practice threat modeling (STRIDE, PASTA, LINDDUN)
- [ ] Review NIST Cybersecurity Framework
- [ ] Prepare security architecture examples from past projects
- [ ] Practice coding with security focus (input validation, authentication, authorization)
- [ ] Understand secure design principles (least privilege, defense in depth, fail secure)
- [ ] Review the company's security blog, whitepapers, and public security incidents

### Behavioral Preparation

- **Breach Experience**: Have a story ready about a security incident you handled
- **Vulnerability Disclosure**: Your stance on responsible disclosure
- **Security vs Velocity**: How you balance shipping fast vs shipping securely
- **Compliance**: Experience with SOC2, PCI-DSS, HIPAA, FedRAMP
- **Cross-team Collaboration**: Working with engineering, product, legal on security

### Technical Preparation

- **Coding**: LeetCode medium difficulty (arrays, strings, trees, graphs, DP)
- **System Design**: Practice designing secure systems (auth, KMS, logging, monitoring)
- **Languages**: Python, Go, or Java for most companies; C/C++ for Apple, Microsoft
- **Cloud Security**: AWS, Azure, GCP security services
- **Network Security**: TCP/IP, DNS, HTTP, TLS, BGP, DNSSEC
- **Cryptography**: RSA, AES, ECC, AEAD, key exchange, signatures, hashing
- **Identity**: OAuth 2.0, OIDC, SAML, LDAP, Active Directory
- **OS Security**: Windows internals, Linux security, container security, macOS security

### Whiteboard Tips

```
1. CLARIFY requirements before jumping to solution
2. SCOPE the problem — what's in and out of scope
3. ASSUMPTIONS — state them explicitly
4. THREAT MODEL — identify what you're protecting against
5. DESIGN — draw boxes and arrows, explain data flow
6. EVALUATE — discuss trade-offs, alternatives, failure modes
7. DEFENSE IN DEPTH — suggest multiple layers of security
8. MONITORING — how would you detect attacks on your design
```

---

*Last updated: July 2026*
