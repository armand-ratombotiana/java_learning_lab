# Google Security Engineer — Interview Guide

> Complete preparation guide for security engineering roles at Google.
> Covers Project Zero, Cloud Security, BeyondCorp, and Google-specific interview patterns.

---

## Role Overview

| Aspect | Detail |
|--------|--------|
| **Positions** | Information Security Engineer, Application Security Engineer, Red Team Engineer |
| **Levels** | L3 (Entry) to L8 (Director) |
| **Locations** | Mountain View, New York, Seattle, Zurich, Tokyo, London |
| **Interview Difficulty** | Very High |
| **Coding Bar** | LeetCode Medium-Hard |

## Interview Rounds

| Round | Focus | Duration | Key Topics |
|-------|-------|----------|------------|
| **Phone Screen** | General security + coding | 45 min | Security fundamentals, basic algorithms |
| **Technical Phone** | Security deep dive | 60 min | Threat modeling, crypto, network security |
| **Onsite Coding** | Algorithms | 45 min x2 | Data structures, problem-solving |
| **Onsite Security** | Security architecture | 45 min | Design, threat model, incident response |
| **Onsite System Design** | Distributed systems | 45 min | Scalable secure systems |
| **Onsite Googliness** | Behavioral | 45 min | Leadership, ambiguity, collaboration |

## Google-Specific Topics

### BeyondCorp (Zero Trust)
- No VPN required. Access based on device + user context
- Access Proxy evaluates every request
- Device inventory and trust scoring
- Continuous verification vs point-in-time auth

### Project Zero
- Vulnerability research and disclosure
- 90-day disclosure policy
- Focus on zero-day exploitation chains
- Bug classes: use-after-free, race conditions, type confusion

### Cloud Security
- IAM conditions and custom roles
- VPC Service Controls for data exfiltration prevention
- Access Transparency for provider access audit
- CMEK (Customer-Managed Encryption Keys) vs CSEK (Customer-Supplied)

### Cryptography
- Tink library (Google's crypto library)
- Keyczar framework
- AEAD, streaming AEAD, deterministic AEAD
- Key rotation and management

## Common Interview Questions

1. Design a secure key management system for a distributed storage service
2. How does BeyondCorp evaluate access requests?
3. Implement a rate limiter with token bucket algorithm
4. Design Certificate Transparency infrastructure
5. Threat model a GKE cluster with sensitive workloads
6. How would you detect DNS exfiltration?
7. Design a system for phishing-resistant authentication at scale
8. Explain the TLS 1.3 handshake

## Behavioral Questions

1. Tell me about a security vulnerability you discovered and how you handled it
2. How do you prioritize security work when everything seems urgent?
3. Describe a time you disagreed with an engineer about a security requirement
4. How do you handle ambiguity in security threat assessment?
5. What's the most complex security system you've designed?

## Recommended Preparation

- Read Google's security whitepapers (BeyondCorp series)
- Review Cloud Security Foundations
- Study Tink crypto library patterns
- Practice LeetCode medium/hard in Python or C++
- Understand Google SRE principles
- Follow Project Zero blog for vulnerability deep-dives
