# Security Architecture — System Design Cheatsheet

> Ready-to-use architecture patterns and design considerations for security infrastructure.
> Use this as a reference during system design interviews and architecture discussions.

---

## Table of Contents

1. [Zero Trust Architecture](#zero-trust-architecture)
2. [SASE (Secure Access Service Edge)](#sase)
3. [SIEM Architecture](#siem-architecture)
4. [SOAR Architecture](#soar-architecture)
5. [IDS/IPS Design](#idsips-design)
6. [WAF (Web Application Firewall)](#waf-design)
7. [Secrets Management](#secrets-management)
8. [Key Management Service (KMS)](#key-management-service)
9. [Certificate Management](#certificate-management)
10. [Encryption Patterns](#encryption-patterns)
11. [HSM (Hardware Security Module)](#hsm)
12. [AuthN/AuthZ Architecture](#authn-authz)
13. [Logging & Monitoring Pipeline](#logging-monitoring)
14. [Network Security Architecture](#network-security)
15. [Cloud Security Architecture](#cloud-security)

---

## Zero Trust Architecture

### Core Principles

```
1. Verify explicitly       — Always authenticate and authorize based on all data points
2. Use least privilege     — Limit access with JIT/JEA (Just-In-Time / Just-Enough-Access)
3. Assume breach           — Segment access, encrypt everything, use analytics
```

### Reference Architecture

```
                    ┌────────────────────────────────────┐
                    │           Policy Engine            │
                    │  (Continuous verification)         │
                    └──────────┬─────────────────────────┘
                               │
    ┌──────────┐    ┌──────────▼──────────┐    ┌──────────┐
    │  User    │───▶│  Zero Trust Proxy   │───▶│ Resource │
    │  Device  │    │  (PEP — Policy       │    │  (App,   │
    │  Service │    │   Enforcement Point) │    │   Data)  │
    └──────────┘    └─────────────────────┘    └──────────┘
                               │
                    ┌──────────▼──────────┐
                    │    Log & Analytics  │
                    │    (Continuous      │
                    │     monitoring)     │
                    └─────────────────────┘
```

### Key Components

| Component | Function | Example Technologies |
|-----------|----------|---------------------|
| **Policy Engine (PE)** | Evaluates access requests against policy | Google BeyondCorp, Zscaler ZPA |
| **Policy Administrator (PA)** | Generates access tokens | Okta, Azure AD Conditional Access |
| **Policy Enforcement Point (PEP)** | Enforces access decisions | Envoy proxy, Cloudflare Access |
| **Data Sources** | Identity, device health, context | CrowdStrike, Jamf, Workspace ONE |

### Design Considerations

- **Identity as the new perimeter**: No implicit trust based on network location
- **Micro-segmentation**: East-west traffic between workloads also verified
- **Continuous validation**: Access is re-evaluated per request, not per session
- **Device trust**: Posture check (OS patch, AV running, disk encrypted)
- **Session security**: Short-lived sessions with periodic re-verification
- **Data protection**: Encryption, DLP, watermarking, DRM

### Interview Template

```
"Let's design a Zero Trust architecture for a global enterprise with
50,000 employees, hybrid cloud, and remote workforce."

Outline:
1. Identity foundation — SSO with MFA, device registration, user attributes
2. Access proxy — All traffic routes through policy enforcement point
3. Policy engine — Contextual policies (device, location, data sensitivity, time)
4. Micro-segmentation — Workload identity for east-west traffic
5. Monitoring — Continuous monitoring and anomaly detection
6. Gradual migration — Phased rollout from pilot to full deployment

Trade-offs:
- (+) Security: Eliminates network-based implicit trust
- (+) Agility: Users can work from anywhere securely
- (-) Complexity: Policy management at scale
- (-) Latency: Every request must go through enforcement
- (-) Cost: Specialized infrastructure investment
```

---

## SASE

### Architecture Framework

```
                     ┌──────────────────────────────┐
                     │    SASE Converged Platform    │
                     │                               │
    ┌──────────────┐ │  ┌──────────┐ ┌──────────┐   │
    │  Branch      │─┼─▶│ SD-WAN   │ │ SWG      │   │
    │  Users       │ │  │ (MPLS,   │ │ (URL     │   │
    ├──────────────┤ │  │  Broad-  │ │  Filter) │   │
    │  Remote      │─┼─▶│  band)   │ ├──────────┤   │
    │  Workers     │ │  └──────────┘ │ CASB     │   │
    ├──────────────┤ │               │ (Shadow  │   │
    │  DC/Cloud    │─┼─▶             │  IT)     │   │
    │  Resources   │ │               ├──────────┤   │
    └──────────────┘ │               │ ZTNA     │   │
                     │               │ (Access) │   │
                     │               ├──────────┤   │
                     │               │ FWaaS    │   │
                     │               └──────────┘   │
                     └──────────────────────────────┘
                                   │
                     ┌──────────────▼───────────────┐
                     │     Public Internet / Cloud   │
                     └──────────────────────────────┘
```

### Key Capabilities

| Capability | Description | Typical Vendors |
|-----------|-------------|-----------------|
| **SD-WAN** | Software-defined WAN, optimizes traffic routing | VeloCloud, Silver Peak |
| **SWG** | Secure Web Gateway, URL filtering, threat protection | Zscaler, Netskope |
| **CASB** | Cloud Access Security Broker, shadow IT discovery | Netskope, McAfee MVISION |
| **ZTNA** | Zero Trust Network Access, app-level access | Zscaler ZPA, Cloudflare Access |
| **FWaaS** | Firewall as a Service, NGFW capabilities | Palo Alto Prisma Access |

### Design Considerations

- **PoP placement**: Edge locations for low-latency policy enforcement
- **Traffic steering**: Tunnel all traffic through SASE edge (proxy-based)
- **Identity integration**: Federation with identity provider
- **DPI (Deep Packet Inspection)**: Decrypt, inspect, re-encrypt traffic
- **Logging**: Centralized logging across all edge locations

---

## SIEM Architecture

### Pipeline Architecture

```
                     COLLECTION LAYER
    ┌──────────┐  ┌──────────┐  ┌──────────┐
    │ AWS      │  │ Azure    │  │ On-Prem  │
    │ CloudTrail│  │ Activity │  │ Syslog   │
    │ GuardDuty│  │ Logs     │  │ WinEvt   │
    └────┬─────┘  └────┬─────┘  └────┬─────┘
         │             │             │
         └─────────────┼─────────────┘
                       │
                    ┌──▼───────────────────┐
                    │   Log Ingestion       │
                    │   (Kafka / Event Hub)  │
                    └──────────┬────────────┘
                               │
                     PROCESSING LAYER
                    ┌──────────▼───────────┐
                    │  Normalization &      │
                    │  Enrichment            │
                    │  (Parse, GeoIP,       │
                    │   Asset Context)       │
                    └──────────┬────────────┘
                               │
                      STORAGE LAYER
                    ┌──────────▼───────────┐
                    │  Hot / Warm / Cold   │
                    │  (Data Lake / Index) │
                    └──────────┬────────────┘
                               │
                    DETECTION LAYER
                    ┌──────────▼───────────┐
                    │  Correlation Rules   │
                    │  + ML Anomalies      │
                    │  + Threat Intel      │
                    └──────────┬────────────┘
                               │
                    RESPONSE LAYER
                    ┌──────────▼───────────┐
                    │   Alerting + SOAR    │
                    │   (Case Management,  │
                    │    Auto-Response)    │
                    └──────────────────────┘
```

### Key Components

| Component | Function | Scaling Consideration |
|-----------|----------|----------------------|
| **Log Shipper** | Collect logs from sources | Filebeat, Fluentd, Cribl |
| **Message Queue** | Buffer ingestion spikes | Kafka (partition by log type) |
| **Normalization** | Parse to common schema | ECS (Elastic Common Schema) |
| **Storage** | Hot/Warm/Cold tiers | S3/ADLS for cold, SSD for hot |
| **Detection Engine** | Rules + ML + Threat Intel | Snort rules, Sigma, custom ML |
| **Search & Visualization** | Ad-hoc query, dashboard | KQL (Kusto), SPL (Splunk) |
| **Case Management** | Track investigations | Built-in or integrated |

### Sizing Considerations

```
Log Volume: 100 GB/day per 1000 employees (typical enterprise)
     - 50-100 events per second per 1000 endpoints
     - Scale: Partition Kafka by source type
     - Index: Hot (7 days), Warm (30 days), Cold (1 year+)

Compliance Requirements:
     - PCI-DSS: 1 year retention
     - HIPAA: 6 years retention
     - SOC2: 1 year retention
     - GDPR: Right to deletion requires purge capability
```

---

## SOAR Architecture

### Playbook-Driven Response

```
                ┌──────────────────────────┐
                │      Trigger (Alert)     │
                │  (SIEM alert, ticket,    │
                │   email, API call)       │
                └──────────┬───────────────┘
                           │
                ┌──────────▼───────────────┐
                │    Playbook Engine        │
                │                           │
                │  1. Enrich (Get context)  │
                │  2. Analyze (Risk score)  │
                │  3. Decide (Automated?)   │
                │  4. Act (Contain/Remediate│
                │  5. Notify (Slack, Page)  │
                │  6. Document (Case note)  │
                └───────────────────────────┘
                           │
                ┌──────────▼───────────────┐
                │      Actions              │
                │  ┌───────────────────┐    │
                │  │ Block IP (FW)     │    │
                │  │ Disable User (IAM)│    │
                │  │ Isolate Host (EDR)│    │
                │  │ Create Ticket     │    │
                │  │ Send Alert        │    │
                │  └───────────────────┘    │
                └───────────────────────────┘
```

### Key Design Decisions

```
Playbook Types:
1. Automated (no-human) — Known bad IPs, well-defined containment
2. Semi-automated — Analyst approval required for destructive actions
3. Manual guidance — Step-by-step instructions for complex incidents

Integration Patterns:
- REST API integration with security tools
- Webhook trigggers from SIEM
- Email-to-case for human-reported incidents
- Chat bot interaction for analyst enrichment

Governance:
- Approval chains for high-risk actions
- Audit trail for every automated action
- Rollback capability for containment actions
- Periodic playbook review and testing
```

---

## IDS/IPS Design

### Network Detection Architecture

```
                    ┌──────────────────────┐
                    │    Internet / WAN     │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │    External Firewall  │
                    │    (Network ACL)      │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
           ┌───────▶│    IPS (Inline)      │───────┐
           │        │    (Block malicious) │       │
           │        └──────┬───────────────┘       │
           │               │                        │
           │        ┌──────▼───────────────┐       │
           │        │    Internal Firewall │       │
           │        │    (Segmentation)    │       │
           │        └──────┬───────────────┘       │
           │               │                        │
    ┌──────┴──────┐  ┌─────▼──────────┐  ┌─────────▼─────┐
    │  SPAN/TAP   │  │  Application   │  │   IDS (Passive)│
    │  (Network   │  │  Servers       │  │   (Alert only) │
    │   visibility)│  │               │  │                │
    └─────────────┘  └────────────────┘  └────────────────┘
```

### IDS vs IPS Decision

| Aspect | IDS (Passive) | IPS (Inline) |
|--------|--------------|--------------|
| **Position** | Out-of-band (SPAN port) | Inline in traffic path |
| **Latency Impact** | None | Adds latency |
| **Action** | Alert only | Block, drop, reset |
| **Risk** | May miss packets | Can impact production traffic |
| **Use Case** | Monitoring, forensics | Prevention, compliance |

### Signature Types

```
1. Content-based — Match specific byte patterns (e.g., known exploit payloads)
2. Behavioral — Detect deviation from baseline (e.g., unusual outbound traffic)
3. Anomaly — Statistical deviation (e.g., sudden spike in traffic)
4. Protocol — Protocol violation detection (e.g., malformed HTTP request)
5. Correlation — Combine multiple signals (e.g., port scan + brute force)

Rule maintenance:
- Daily signature updates from vendor
- Custom rules for internal applications
- False positive tuning per environment
- Regular rule cleanup (obsolete rules increase noise)
```

---

## WAF Design

### Architecture

```
                    ┌──────────────────────┐
                    │       Client          │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │    CDN / Reverse      │
                    │    Proxy (Cloudflare, │
                    │     CloudFront, Fastly)│
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │      WAF Layer        │
                    │                       │
                    │  ┌─────────────────┐  │
                    │  │ Rule Engine      │  │
                    │  │ - OWASP CRS     │  │
                    │  │ - Custom Rules  │  │
                    │  │ - Rate Limiting │  │
                    │  │ - Bot Detection │  │
                    │  │ - IP Reputation │  │
                    │  └─────────────────┘  │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │    Application Load   │
                    │    Balancer           │
                    └──────────┬───────────┘
                               │
                    ┌──────────▼───────────┐
                    │    Application        │
                    │    Servers            │
                    └──────────────────────┘
```

### Rule Categories

| Category | Examples | Action |
|----------|----------|--------|
| **Protocol Compliance** | HTTP protocol violations, malformed requests | Block |
| **Input Validation** | SQLi, XSS, command injection, path traversal | Block/Log |
| **Rate Limiting** | API rate limits, DDoS protection | Throttle/Block |
| **IP Reputation** | Known malicious IPs, TOR exit nodes, VPNs | Block |
| **Bot Detection** | Scrapers, crawlers, automated tools | Challenge/Captcha |
| **Geolocation** | Country-based allow/block lists | Block |
| **Session Protection** | Session hijacking, cookie poisoning | Block/Re-auth |
| **File Upload** | Malware scanning, size limits, type validation | Block/Quarantine |

### Deployment Models

```
Cloud WAF (e.g., Cloudflare WAF, AWS WAF, Azure WAF):
  + Managed, auto-scaling, global rule distribution
  + SSL termination at edge
  + Low operational overhead
  - Limited customization
  - Sensitive data leaves your VPC

Application WAF (e.g., ModSecurity, NAXSI):
  + Maximum customization
  + Data stays in your control
  + No external dependency
  - Performance overhead on app servers
  - You manage updates and scaling

Hybrid:
  + Cloud WAF for volumetric attacks (layer 3-4)
  + App WAF for application-specific rules (layer 7)
  + Defense in depth
```

---

## Secrets Management

### Architecture

```
                    ┌───────────────────────────────────┐
                    │     Secrets Management Platform    │
                    │  (Vault, AWS Secrets Manager,     │
                    │   Azure Key Vault, GCP Secret     │
                    │   Manager, CyberArk)              │
                    └──────┬──────┬──────────┬──────────┘
                           │      │          │
            ┌──────────────┘      │          └──────────────┐
            │                     │                         │
    ┌───────▼──────┐    ┌────────▼──────┐    ┌─────────────▼──┐
    │  Auth Method │    │ Secret Engine │    │  Audit Log     │
    │  - Token     │    │  - KV Store   │    │  - All access  │
    │  - K8s       │    │  - Database   │    │    logged      │
    │  - LDAP      │    │  - PKI        │    │  - Rotation    │
    │  - OIDC      │    │  - Transit    │    │    tracking    │
    │  - AWS IAM   │    │  - SSH        │    │                │
    └──────────────┘    └────────┬──────┘    └────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │     Policy Engine        │
                    │  (Who can read what?     │
                    │   Path-based ACLs,       │
                    │   Capabilities)          │
                    └─────────────────────────┘
```

### Key Concepts

```
Dynamic Secrets:
  - Secrets generated on-demand, not stored permanently
  - Auto-expire after TTL (lease)
  - Examples: Database credentials, cloud provider credentials

Encryption as a Service:
  - Applications send plaintext, get ciphertext
  - Never handle encryption keys directly
  - Key rotation is transparent to applications

Secret Rotation:
  - Automatic rotation based on schedule
  - Zero-downtime rotation (blue-green)
  - Compromise rotation (immediate)

Lease Management:
  - Every secret has a lease (TTL)
  - Clients must renew before expiry
  - Improve revocation on compromise
```

### Design Interview Template

```
"Design a secrets management system for a microservices architecture
with 500 services across 3 cloud regions."

Requirements:
1. Dynamic secrets for database and cloud credentials
2. Encryption as a service for data-in-transit and at-rest
3. Automatic rotation every 24 hours
4. Audit logging for compliance (SOC2)
5. Disaster recovery with multi-region replication

Architecture:
1. Primary cluster per region with auto-unseal (KMS-backed)
2. Performance standby nodes for read replicas
3. Kubernetes sidecar injector for pod identity
4. Vault Agent for secret renewal without restarting services
5. Cross-region replication via performance replication
6. Root key rotation every quarter

Trade-offs:
- Active-active vs active-passive for DR
- Consul vs integrated storage for backend
- In-cluster vs external Vault deployment
```

---

## Key Management Service

### Envelope Encryption

```
                     ┌─────────────────────────┐
                     │     Customer Master Key  │
                     │       (CMK — HSM/HSM)    │
                     │         AWS KMS /        │
                     │       Azure Key Vault    │
                     └───────────┬─────────────┘
                                 │ Generate DEK
                                 │ (Data Encryption Key)
                                 ▼
                    ┌──────────────────────────┐
                    │   Encrypted DEK (Envelope)│
                    │   ┌──────────────────┐    │
                    │   │ Plaintext DEK    │    │
                    │   │ (In memory only) │    │
                    │   └────────┬─────────┘    │
                    │            │               │
                    │            ▼               │
                    │    ┌──────────────┐       │
                    │    │ Encrypt Data  │       │
                    │    │ with DEK     │       │
                    │    └──────────────┘       │
                    └──────────────────────────┘

Storage:
┌──────────────────────────────────────────────┐
│  Stored Data                                  │
│  ┌──────────────────┬──────────────────────┐  │
│  │ Encrypted DEK    │ Encrypted Data       │  │
│  │ (wrapped by CMK) │ (encrypted by DEK)   │  │
│  └──────────────────┴──────────────────────┘  │
└──────────────────────────────────────────────┘
```

### KMS Key Hierarchy

```
Root of Trust (HSM)
    └── Customer Master Key (CMK)
            └── Key Encryption Key (KEK)
                    └── Data Encryption Keys (DEKs)
                            └── Encrypted Data (at rest)
```

### KMS Features

| Feature | Purpose | Implementation |
|---------|---------|---------------|
| **Key Generation** | Create cryptographic keys | FIPS 140-2 Level 3 HSM |
| **Key Rotation** | Rotate CMK periodically | Automatic (annual) or manual |
| **Envelope Encryption** | Encrypt with DEK, wrap DEK with CMK | AWS KMS, Azure Key Vault |
| **Key Import** | Import your own key material | BYOK (Bring Your Own Key) |
| **Key Policies** | IAM permissions on keys | Resource-based policies |
| **Audit** | Track key usage | CloudTrail, Azure Monitor |

### Design Considerations

```
Availability: 
  - KMS operations are on the critical path for decrypt
  - Use regional redundancy
  - Cache DEKs in application memory (with TTL)
  - Plan for KMS outage: degrade gracefully (no new encrypt, allow decrypt with cached DEKs)

Performance:
  - Envelope encryption reduces KMS API calls
  - Cache encrypted DEK with data, not on KMS
  - Batch encrypt/decrypt operations

Compliance:
  - FIPS 140-2/3 validated
  - Key material never leaves HSM boundary
  - Audit all key access
  - Support for key deletion with cooldown period
```

---

## Certificate Management

### Lifecycle

```
                 ┌─────────────────────────────┐
                 │   Certificate Authority      │
                 │   (Internal/Public CA)       │
                 └──────────┬──────────────────┘
                            │
          Request ──────────┤
          (CSR signed by   │
           private key)     │
                            ▼
                 ┌─────────────────────────────┐
                 │   Certificate Issuance       │
                 │   (CA issues signed cert)    │
                 └──────────┬──────────────────┘
                            │
         ┌──────────────────┼──────────────────┐
         │                  │                  │
         ▼                  ▼                  ▼
  ┌────────────┐   ┌──────────────┐   ┌──────────────┐
  │ Deployment │   │  Monitoring  │   │  Renewal     │
  │ (Install)  │   │  (Expiry,   │   │  (Before     │
  │            │   │   Revocation)│   │   Expiry)    │
  └────────────┘   └──────────────┘   └──────┬───────┘
                                             │
                                      ┌──────▼───────┐
                                      │  Revocation   │
                                      │  (CRL/OCSP)   │
                                      └──────────────┘
```

### Certificate Types

| Type | Use Case | Validity |
|------|----------|----------|
| **TLS Server** | HTTPS, mTLS server | 90 days to 2 years |
| **TLS Client** | mTLS client authentication | 90 days to 2 years |
| **Code Signing** | Sign binaries, containers | 1-3 years |
| **Email (S/MIME)** | Email encryption + signing | 2-5 years |
| **Root CA** | Sign intermediate CAs | 10-15 years |
| **Intermediate CA** | Sign end-entity certs | 5-10 years |

### Design for Certificate Management

```
Automated Certificate Management:
  - ACME protocol (Let's Encrypt, cert-manager)
  - Auto-renewal before expiry (30 days for 90-day certs)
  - Certificate Transparency logging
  - mTLS with short-lived certs (hourly rotation)

PKI Architecture:
  - Offline Root CA (air-gapped, in safe)
  - Online Intermediate CAs (per environment/region)
  - HSM-protected CA keys
  - OCSP responder for revocation checking

Kubernetes Certificates:
  - cert-manager for automated issuance
  - Let's Encrypt for public TLS
  - Vault PKI for internal mTLS
  - Istio/Linkerd for service mesh certs
```

---

## Encryption Patterns

### At Rest

```
Pattern: Each layer encrypts independently

Application Layer:
  - Field-level encryption (PII, secrets)
  - Database column encryption
  - Client-side encryption before sending to server

Storage Layer:
  - Disk encryption (BitLocker, LUKS)
  - Filesystem encryption (eCryptfs, fscrypt)
  - Object storage encryption (S3 SSE-S3/SSE-KMS/SSE-C)

Database Layer:
  - Transparent Data Encryption (TDE)
  - Tablespace encryption
  - Column-level encryption

Backup Layer:
  - Backup encryption
  - Offline backup encryption
  - Cross-region replication encryption
```

### In Transit

```
Network Layers:

Layer 2: MACsec (link encryption)
Layer 3: IPsec (network encryption)
Layer 4: TLS (transport encryption)
Layer 5+: Application-level encryption (end-to-end)

TLS Best Practices:
  - TLS 1.3 preferred (1.2 minimum)
  - Strong cipher suites (ECDHE + AES-GCM or ChaCha20)
  - HSTS for web services
  - Certificate pinning (with backup certs)
  - OCSP stapling
  - Mutual TLS for service-to-service
```

### Design Interview Template

```
"Design encryption for a global file storage service."

Requirements:
1. End-to-end encryption for sensitive files
2. Sharing between users
3. Server-side deduplication (without seeing plaintext)
4. Compliance with GDPR, HIPAA

Proposal:
1. Client-side encryption with user-managed key
2. Envelope encryption for sharing (wrap DEK with recipient's public key)
3. Convergent encryption for deduplication (hash-based)
4. Server-side re-encryption for key rotation

Key Components:
  - Key server for key management
  - Client SDK for encryption/decryption
  - Sharing protocol (re-wrap DEK)

Threat Model:
  - Server compromise: attacker gets encrypted blobs only
  - Key server compromise: attacker needs user's private key too
  - Client compromise: attacker can access in-clear data
```

---

## HSM

### Architecture

```
Physical HSM:
┌──────────────────────────────┐
│         Tamper-Proof         │
│         Enclosure            │
│  ┌────────────────────────┐  │
│  │   Cryptographic        │  │
│  │   Processor            │  │
│  │   ┌──────────┐         │  │
│  │   │ Key Store │         │  │
│  │   │ (Secure) │         │  │
│  │   └──────────┘         │  │
│  │   ┌──────────┐         │  │
│  │   │ RNG      │         │  │
│  │   │ (FIPS)   │         │  │
│  │   └──────────┘         │  │
│  └────────────────────────┘  │
│  Tamper Detection:           │
│  - Zeroization on tamper     │
│  - Mesh anti-tamper          │
│  - Temperature sensors       │
└──────────────────────────────┘

Cloud HSM:
┌──────────────────────────────┐
│  AWS CloudHSM / Azure        │
│  Dedicated HSM / GCP Cloud   │
│  HSM                          │
│                               │
│  - FIPS 140-2 Level 3        │
│  - Customer-managed           │
│  - Cluster for HA             │
│  - PKCS#11 / JCE / OpenSSL   │
└──────────────────────────────┘
```

### HSM Use Cases

| Use Case | Requirement | HSM Function |
|----------|-------------|--------------|
| **Root CA** | Private key must never leave HSM | Key generation + signing |
| **KMS Root Key** | CMK protection | Key wrapping |
| **Payment Processing** | PCI-PIN compliance | PIN generation |
| **Code Signing** | Secure build pipeline | Signing operations |
| **Database TDE** | Key protection | Key retrieval and caching |

---

## AuthN/AuthZ Architecture

```
                    ┌─────────────────────────────────────┐
                    │       Identity Provider (IdP)        │
                    │   (Okta, Azure AD, Keycloak, Auth0)  │
                    └──────┬────────────┬─────────────────┘
                           │            │
           Authentication  │            │ Authorization
                           │            │
                    ┌──────▼────┐  ┌────▼─────────────┐
                    │  Service  │  │  Policy Decision  │
                    │  (App)    │  │  Point (PDP)      │
                    │           │  │  (OPA, Cedar,     │
                    │           │  │   Authz Policy)   │
                    └───────────┘  └────────┬──────────┘
                                            │
                                      ┌─────▼──────────┐
                                      │  Policy Info    │
                                      │  Point (PIP)    │
                                      │  (User roles,   │
                                      │   permissions)  │
                                      └─────────────────┘

Token Types:
┌──────────────┬──────────────┬──────────────────┐
│ Token        │ Format       │ Use Case          │
├──────────────┼──────────────┼──────────────────┤
│ Access Token │ JWT/opaque   │ API authorization │
│ ID Token     │ JWT          │ User identity     │
│ Refresh Token│ opaque       │ Obtain new tokens │
│ Session Token│ opaque       │ Web session       │
│ API Key      │ opaque string│ Service-to-service│
└──────────────┴──────────────┴──────────────────┘
```

---

## Logging & Monitoring Pipeline

```
                    ┌─────────────────┐
                    │   Data Sources   │
                    │ (App, OS, Cloud, │
                    │  Network, EDR)   │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   Log Shipper   │
                    │ (Filebeat,      │
                    │  Fluentd, Cribl) │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │   Buffer/Queue  │
                    │   (Kafka)       │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
   ┌──────▼──────┐   ┌──────▼──────┐   ┌──────▼──────┐
   │  SIEM       │   │  Data Lake  │   │  Metrics    │
   │  (Elastic,  │   │  (S3, ADLS) │   │  (Datadog,  │
   │   Splunk,   │   │  (Archive)  │   │   Prometheus)│
   │   Sentinel) │   │             │   │             │
   └──────┬──────┘   └─────────────┘   └─────────────┘
          │
   ┌──────▼──────┐
   │  Alerting   │
   │  + Dashboards│
   └─────────────┘
```

---

## Network Security Architecture

### Segmentation

```
Internet ──▶ DMZ ──▶ App Tier ──▶ Data Tier
              │          │            │
              ▼          ▼            ▼
           WAF, LB    Firewall     Encryption
           Reverse    App-Sec      Database
           Proxy      Group        Firewall
```

### Security Groups

```
Multi-tier security groups:
  Web Tier SG:
    Inbound: 80, 443 from Internet/ALB
    Outbound: To App Tier SG

  App Tier SG:
    Inbound: From Web Tier SG (app port)
    Outbound: To Data Tier SG (DB port)

  Data Tier SG:
    Inbound: From App Tier SG (DB port)
    Outbound: None (locked down)

  Management SG:
    Inbound: From Bastion/Admin VPN
    SSH/RDP access only
```

---

## Cloud Security Architecture

### Multi-Account Strategy

```
Organization Root
├── Security Account (CloudTrail, GuardDuty, Security Hub)
├── Log Archive Account (S3 bucket for logs, immutable)
├── Infrastructure Account (Network, shared services)
├── Dev Account
├── Staging Account
└── Production Account

Service Control Policies (SCPs):
  - Deny: Delete CloudTrail trail
  - Deny: Disable KMS key rotation
  - Deny: Create public S3 buckets
  - Deny: Non-approved regions
  - Protect root user
```

### Cloud Security Controls

| Layer | Controls |
|-------|----------|
| **IAM** | Least privilege, permission boundaries, roles, condition keys |
| **Network** | VPC segmentation, security groups, NACLs, VPC endpoints |
| **Data** | Encryption at rest (KMS), encryption in transit (TLS) |
| **App** | WAF, API Gateway auth, Cognito for user pools |
| **Monitoring** | CloudTrail, Config, GuardDuty, Security Hub |
| **Compliance** | Config rules, audit manager, Artifact reports |

---

*Last updated: July 2026*
