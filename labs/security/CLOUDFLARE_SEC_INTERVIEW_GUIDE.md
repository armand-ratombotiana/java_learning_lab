# Cloudflare Security Engineer — Interview Guide

> Complete preparation guide for security engineering roles at Cloudflare.
> Covers edge security, DDoS mitigation, Workers, Zero Trust, and Cloudflare-specific interview patterns.

---

## Role Overview

| Aspect | Detail |
|--------|--------|
| **Positions** | Security Engineer, Security Researcher, Application Security Engineer |
| **Levels** | Associate to Principal |
| **Locations** | San Francisco, Austin, London, Lisbon, Singapore (remote-friendly) |
| **Interview Difficulty** | High |
| **Coding Bar** | Practical — Go/Rust coding, not abstract LeetCode |

## Interview Rounds

| Round | Focus | Duration | Key Topics |
|-------|-------|----------|------------|
| **Recruiter Call** | Background, experience | 30 min | Role alignment |
| **Technical Screen** | Security fundamentals | 60 min | Web security, networking, crypto |
| **Onsite System Design** | Distributed security systems | 45 min | DDoS, WAF, edge architecture |
| **Onsite Incident** | Crisis management | 45 min | Breach response, security operations |
| **Onsite Coding** | Code review | 45 min | Go/Rust, security code review |
| **Onsite Product** | Cross-team collaboration | 45 min | User empathy, product thinking |

## Cloudflare-Specific Topics

### Edge Security
- Anycast network architecture for DDoS absorption
- Magic Transit (L3 DDoS protection)
- Spectrum (TCP/UDP proxying)
- Unmetered DDoS protection

### WAF & Bot Management
- OWASP CRS (Core Rule Set) managed rules
- Rate limiting at edge (global distribution)
- Bot detection: machine learning + JS challenges
- Custom rules via Cloudflare WAF

### SSL/TLS
- Universal SSL (free, automated)
- Custom certificates (upload your own)
- mTLS for API security
- Certificate Transparency monitoring
- SSL/TLS modes: Flexible, Full, Full (Strict)
- Edge certificates with automatic renewal

### Cloudflare Workers
- Serverless at edge (V8 isolates)
- Workers KV for distributed key-value
- Durable Objects for stateful edge computing
- WebCrypto API within Workers
- Security considerations: not a sandbox escape vector

### Zero Trust (Cloudflare One)
- Access: Zero Trust network access (reverse proxy)
- Gateway: DNS filtering, web filtering
- Browser Isolation: Remote browser execution
- WARP: Client for device security
- Magic WAN: SD-WAN replacement

### DNS Security
- DNSSEC (free for all customers)
- DNS-over-HTTPS and DNS-over-TLS
- Authoritative DNS with anycast
- Secondary DNS for redundancy

## Common Interview Questions

1. Design a DDoS mitigation system that handles 10+ Tbps attacks
2. How would you implement TLS 1.3 at the edge across 300+ data centers?
3. Design a WAF rule engine operating on every HTTP request globally
4. How does Cloudflare Workers' security model prevent tenant escape?
5. Design Certificate Transparency at Cloudflare scale
6. How would you detect and block Layer 7 attacks at the edge?
7. Design a system for Bot Management using only edge data
8. How does Browser Isolation prevent data exfiltration?

## Behavioral Questions

1. Tell me about a time you responded to a large-scale security incident
2. How do you balance performance and security at the edge?
3. Describe a security architecture decision you had to defend to leadership
4. How do you approach zero-day vulnerability response?
5. What's your experience with open source security tools?

## Recommended Preparation

- Cloudflare Learning Center (free courses)
- Cloudflare Blog — engineering and security deep-dives
- CIRCL (Cloudflare's Go crypto library)
- Workers documentation and security model
- Understand how anycast networking works
- Study Cloudflare's history of major attacks (Mirai, etc.)
- Read about their post-quantum crypto initiatives
