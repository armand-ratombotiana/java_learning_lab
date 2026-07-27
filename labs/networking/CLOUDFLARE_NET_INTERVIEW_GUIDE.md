# Cloudflare — Networking Interview Guide

---

## Role Overview

Cloudflare runs a global anycast network across 310+ data centers. Roles focus on edge networking, DNS, DDoS mitigation, HTTP/3/QUIC, and building infrastructure at massive global scale.

### Key Systems You Must Know

| System | Purpose | Why It Matters |
|--------|---------|----------------|
| Global Anycast Network | CDN + DDoS + DNS | Foundation of all Cloudflare services |
| Unimog / NNG | Load balancer | Maglev-like consistent hashing |
| Pingora | HTTP proxy (replaces NGINX) | Cloudflare's custom proxy |
| 1.1.1.1 | Public DNS resolver | World's fastest DNS (and most private?) |
| Argo Smart Routing | Intelligent routing | Optimized path selection, reduced latency |
| Cloudflare Workers | Edge compute | Serverless at the edge |
| Spectrum | TCP/UDP proxy | Non-HTTP traffic through Cloudflare |
| Magic Transit / Magic WAN | IP transit / SD-WAN | Enterprise networking |

---

## Interview Rounds (5 total)

### 1. Phone Screen (45 min)
**Focus**: Networking fundamentals, anycast, DNS, DDoS.

Common questions:
- How does anycast work and why does Cloudflare use it?
- Walk through DNS resolution using 1.1.1.1.
- What happens during a DDoS attack and how does Cloudflare mitigate it?
- TCP congestion control basics.

### 2. Technical (60 min)
**Focus**: Protocol depth, system design at edge.

Topics:
- QUIC handshake in detail. How does 0-RTT work?
- TLS 1.3 handshake. What is Encrypted Client Hello (ECH)?
- How would you design a global rate limiting system?
- Cache strategy for an API with TTL-varying content.

### 3. Protocol Deep Dive (60 min)
**Focus**: HTTP/2, HTTP/3, TLS, QUIC.

Questions:
- Compare HTTP/2 and HTTP/3 in detail.
- How does QUIC handle connection migration?
- Describe head-of-line blocking in HTTP/2 and how QUIC fixes it.
- How does HPACK compare to QPACK?
- What are the security implications of 0-RTT data?

### 4. Systems Design (60 min)
**Focus**: Building at Cloudflare scale (edge, global).

Design questions:
- Design a DDoS protection system for a new protocol.
- Design a CDN caching system handling 50 TB/s of traffic.
- Design a global DNS system with sub-10ms response time.
- Design a load balancer for Cloudflare's edge proxy (Pingora).

### 5. Team Fit / Behavioral (45 min)
**Focus**: Dogfooding, customer-first, edge mindset.

Questions:
- "Tell me about a time you improved performance at scale."
- "How do you think about trade-offs when designing a protocol?"
- "You discover a vulnerability in one of our edge components. What do you do?"
- "How would you explain anycast to a non-technical customer?"

---

## Must-Know Cloudflare Concepts

### Anycast at Cloudflare

```
User in Tokyo ──┐
                ├── BGP shortest path ──► Tokyo PoP (ashi.nrt1)
User in London ─┘                    └──► If Tokyo down → route to other PoP

All PoPs announce same IPs (/24 prefixes). BGP routes users to nearest.
```

**Benefits:**
- Automatic load distribution
- Instant failover
- DDoS absorption (traffic spread across 310+ data centers)
- Single IP for global service

### How Cloudflare Handles DDoS

| Layer | Protection | Tools |
|-------|-----------|-------|
| L3/L4 | Network-level | Flow tracking, rate limiting, BGP blackhole |
| L7 | Application-level | WAF, rate limiting, challenge page, bot management |
| DNS | DNS amplification | Rate limit per source, disable recursion, DNS firewall |

**Unimog / NNG Load Balancer:**
- Consistent hashing for connection persistence
- Flow-based accounting
- Packet filtering at line rate
- Direct Server Return (DSR) for outbound traffic

### Cloudflare's Proxy (Pingora)

- Built in Rust (memory safe, high performance)
- Replaced NGINX (better CPU/memory utilization)
- Graded request handling (different priority levels)
- Connection reuse with upstream servers
- Supports HTTP/1, HTTP/2, HTTP/3
- Handles 25M+ requests per second per data center

---

## Protocol Knowledge Required

| Protocol | Depth | Key Details |
|----------|-------|-------------|
| TCP/IP | Expert | Congestion control (BBR, Cubic), window scaling, fast open |
| HTTP/1.1 | Expert | Keep-alive, chunked transfer, pipelining |
| HTTP/2 | Expert | Stream multiplexing, HPACK, server push (deprecated), priority tree |
| HTTP/3 | Expert | QUIC transport, QPACK, 0-RTT, connection migration |
| TLS 1.3 | Expert | Handshake, 0-RTT, ECH, certificate transparency |
| DNS | Expert | DNSSEC, CNAME flattening, anycast, stealth DNS |
| QUIC | Expert | Frame types, stream IDs, flow control, migration |

---

## Sample Technical Questions

### Question 1: "How does Cloudflare's anycast help with DDoS?"

**Answer:**
1. Traffic is distributed across 310+ data centers globally — no single point to overwhelm.
2. If one PoP is attacked, BGP withdraws the route, and traffic shifts.
3. At each PoP, Unimog load balancer filters before reaching proxy.
4. Attack traffic is absorbed​​​​​​​​​​​​​​​​, legitimate traffic passed through with minimal latency.

### Question 2: "Design a global rate limiter for the edge."

**Key Considerations:**
- Distributed counters (Sliding window log + atomic operations)
- BGP anycast means an IP may hit different PoPs per minute — session affinity needed
- Use consistent hashing to route same IP/bucket to same counter
- Store counters in-memory at PoP, batch sync to central DB
- Apply limits hierarchically: per IP / per ASN / per datacenter / global

---

## Key Tips

> "Know the QUIC specification (RFC 9000) inside out — Cloudflare helped write it."
> "Understand Unimog/NNG — Cloudflare's load balancer is a key differentiator."
> "DDoS is a first-class feature here, not an afterthought."
> "They care deeply about latency — every millisecond counts."
> "Open source contributions (rustls, quiche, http3-client) are a conversation starter."
> "Be ready to discuss trade-offs in protocol design (not just what, but WHY)."

---

## Recommended Reading

- Cloudflare Blog (tech deep dives, infrastructure)
- RFC 9000 (QUIC), RFC 9001 (TLS over QUIC), RFC 9002 (QUIC loss detection)
- The Cloudflare Architecture blog series
- How Cloudflare handles DDoS attacks (blog whitepapers)
- Pingora: How Cloudflare replaced NGINX (blog)
- Cloudflare Open Source: rustls, pingora, quiche, wrangler

---

*"Every millisecond counts at Cloudflare. Every request is ninety-nine nine."*
