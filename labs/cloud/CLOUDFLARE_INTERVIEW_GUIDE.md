# Cloudflare Interview Guide

## Overview
Comprehensive preparation guide for Cloudflare technical interviews — Solutions Engineer, Systems Engineer, Network Engineer, and Security Engineer roles.

## Role Types

| Role | Level | Focus |
|------|-------|-------|
| Solutions Engineer | IC3-IC5 | Customer-facing demos, POCs, onboarding, technical sales |
| Systems Engineer | IC4-IC6 | Edge network architecture, DDoS mitigation, CDN, Workers |
| Network Engineer | IC4-IC6 | Anycast, BGP, peering, edge data center operations, protocol optimization |
| Security Engineer | IC4-IC6 | WAF rules, DDoS, bot management, zero trust, API Shield |

## Interview Process

| Stage | Duration | Focus |
|-------|----------|-------|
| Recruiter Screen | 30 min | Background, interest in Cloudflare |
| Technical Screen | 60 min | Web performance, DNS, DDoS, CDN, HTTP protocols |
| Onsite Loop | 4 x 45 min | System design, networking deep dive, security, behavioral/culture |

## Core Technical Topics

### Cloudflare Architecture
- **Anycast Network**: Single IP advertised from all 310+ locations, BGP route optimization, auto-failover
- **Edge Data Centers**: 310+ locations, direct peering with major ISPs, sub-50ms response time global
- **Network Map**: Tiered caching, Argo Smart Routing, optimizes routes between edge and origin
- **Unimog**: Cloudflare's L4 load balancer at every edge location

### DNS
- **Authoritative DNS**: Fastest DNS resolver globally (1.1.1.1), DNSSEC, custom nameservers, secondary DNS
- **DNS Record Types**: A, AAAA, CNAME, MX, TXT, SRV, CAA, DS, DNSKEY, NS records
- **DNSSEC**: DNS record signing, automatic DNSSEC, DS record management
- **DNS CNAME Flattening**: Resolves CNAME at apex without an ALIAS record

### CDN & Performance
- **Caching**: Static and dynamic content, cache rules (by file extension, cookie, header, status code), cache key, tiered cache
- **Argo Smart Routing**: Optimized routing minimizing latency (avg 30% improvement), additional cost
- **HTTP/2 & HTTP/3 (QUIC)**: Multiplexing, server push, 0-RTT, forward error correction
- **Brotli Compression**: Better compression ratio than gzip (avg 20% smaller)
- **Image Optimization**: Polish (lossless/lossy), Mirage (mobile optimization), image resizing
- **Early Hints**: Send early response with Link headers to preload resources
- **Orange-to-Cloud vs Orange-to-Orange** (Argo)

### Security
- **DDoS Protection**: L3/L4/L7, unthrottled, always-on, infinite mitigation capacity, include/exclude rules
- **WAF**: Core Rules (OWASP), managed rulesets (Cloudflare, OWASP), custom rules, rate limiting rules, WAF scoring
- **Bot Management**: Automated detection (AI/ML), verified bots, bot score headers (CF-Bot-Score), challenge vs block
- **Rate Limiting**: Classic vs Advanced rate limiting, burst sensitivity, mitigation action
- **SSL/TLS**: Modes (Flexible, Full, Full Strict), SSL/TLS Recommender, Certificate Packs (universal, advanced, custom), OCSP Stapling, TLS 1.3
- **API Shield**: Schema validation, endpoint protection, mTLS, API discovery
- **Zero Trust**: Cloudflare Access (ZTNA), Gateway (secure web gateway), Browser Isolation (remote browser), Cloudflare One

### Edge Compute (Workers)
- **Cloudflare Workers**: V8 isolates, cold start < 5ms, 128MB memory, 50-100ms CPU time, 900+ locations
- **Workers KV**: Global key-value store, eventual consistency, reads at edge, writes in primary
- **Durable Objects**: Strong consistency, stateful, actor model, transactional storage
- **R2**: Object storage (S3-compatible), zero egress fees, S3 API compatibility
- **Queues**: Async message queuing, at-least-once delivery, configurable max retries
- **D1**: SQLite-compatible serverless database, running at the edge
- **Workers AI**: Run AI inference at the edge
- **Pages**: Full-stack edge hosting, integration with Workers, fast deployments

### Developer Platform
- **Cloudflare Pages**: Static site hosting, serverless functions (Pages Functions), preview deployments
- **Cloudflare Tunnel**: Secure origin connectivity without public IP, no open ports, automatic failover
- **Speed**: Browser Insights, Observatory (performance testing), Web Analytics

## Key Concepts to Understand

| Concept | Importance | Detail |
|---------|-----------|--------|
| Anycast | Critical | Single IP announced globally, traffic routes to nearest location |
| Reverse Proxy | Critical | Cloudflare sits between visitor and origin, terminates connections |
| Edge vs Origin | Critical | Edge = Cloudflare, Origin = your server |
| Orange Cloud | High | Proxy mode (Cloudflare acts as reverse proxy) |
| Gray Cloud | High | DNS-only mode (Cloudflare not proxied) |
| WAF Scoring | Medium | Each request gets threat score 1-99, score = risk level |
| Argo Smart Routing | Medium | Finds optimal route between Cloudflare edge and origin |
| Tiered Cache | Medium | Reduces origin load by using upper-tier edge for cache fills |

## Sample Interview Questions

### Technical Screen Questions
1. **Describe what happens when you type a URL in a browser** (DNS lookup, TCP handshake, TLS, HTTP request/response)
2. **Explain how anycast routing works and why Cloudflare uses it**
3. **How would you reduce TTFB (Time to First Byte) for a global website?**
4. **Compare HTTP/1.1, HTTP/2, and HTTP/3 (QUIC)** — multiplexing, head-of-line blocking
5. **How does a CDN cache work?** Cache hit/miss, TTL, cache invalidation
6. **How would you stop a L7 DDoS attack?** Rate limiting, WAF rules, IP reputation
7. **Explain mTLS and when you'd use it** (Zero Trust, API Shield)
8. **How does Workers KV work vs Durable Objects?** Consistency, performance, use cases

### System Design Questions
1. **Design a global video streaming delivery system using Cloudflare**
2. **Design a zero-trust corporate network access solution**
3. **Design an e-commerce platform with edge caching and WAF protection**
4. **Design a global API gateway with authentication and rate limiting**

### Behavioral Questions
1. **Tell me about a time you handled a DDoS attack or security incident**
2. **How do you prioritize between performance and security trade-offs?**
3. **Describe your experience with edge computing or serverless platforms**
4. **How do you troubleshoot a slow-loading website across global regions?**

## Performance Optimization Checklist

- Enable HTTP/2, HTTP/3, Brotli compression
- Enable Argo Smart Routing
- Configure Tiered Cache (Smart mode)
- Enable Early Hints
- Optimize images (Polish, Mirage, Image Resizing)
- Minify JS/CSS/HTML on-the-fly
- Enable Rocket Loader (JS optimization)
- Configure cache rules: longer TTL for static, shorter for dynamic
- Use signed URLs/cookies for private content
- Enable WebSockets, gRPC if needed

## Security Best Practices

- Set SSL/TLS to Full (Strict)
- Enable Always Use HTTPS
- Enable HSTS (Strict-Transport-Security)
- Enable WAF with Core Rules (OWASP)
- Set rate limiting rules per endpoint
- Enable Bot Fight Mode or Bot Management
- Enable DDoS managed rules
- Enable DNSSEC
- Set up automatic HTTPS rewrites
- Use Cloudflare Tunnel instead of public IP for origin
- Enable Email Security (Area 1 email filtering)

## Cert Path

| Certification | Focus | Time |
|--------------|-------|------|
| Cloudflare Fundamentals | Core concepts, DNS, CDN, security basics | 1-2 weeks |
| Cloudflare Network Professional | Advanced routing, Argo, Magic Transit, Spectrum | 4-6 weeks |
| Cloudflare Security Professional | WAF, DDoS, zero trust, bot management, API Shield | 6-8 weeks |
| Cloudflare Developer Professional | Workers, KV, Durable Objects, R2, Pages | 4-6 weeks |

---

*Last updated: July 2026*
