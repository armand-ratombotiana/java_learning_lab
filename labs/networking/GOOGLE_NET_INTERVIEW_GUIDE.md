# Google — Network Engineer Interview Guide

---

## Role Overview

Google Network Engineers design, build, and operate one of the largest networks in the world — serving billions of users across search, YouTube, Cloud, Gmail, and more.

### Key Systems You Must Know

| System | Purpose | Why It Matters |
|--------|---------|----------------|
| Jupiter | Data center fabric (Clos topology) | Foundational DC design |
| B4 | SDN-based WAN connecting data centers | Software-defined WAN |
| Espresso | Peering edge for external traffic | Global connectivity |
| Maglev | Consistent-hashing L4 load balancer | 10M+ connections/s |
| Andromeda | Virtual network stack for GCP | Cloud networking |
| Google Front End | TLS termination, routing, DDoS | Entry point for all services |

---

## Interview Rounds (5-6 total)

### 1. Phone Screen (45 min)
**Focus**: Networking fundamentals, TCP/IP, BGP, DNS.

Common questions:
- Walk through HTTP request flow from browser to server.
- Explain BGP path selection algorithm.
- TCP vs UDP and when to use each.
- How does DNS resolution work?

### 2. Coding Round (45 min)
**Focus**: Network-related algorithms. Languages: Python, Go, C++.

Topics:
- Socket programming (TCP/UDP echo server)
- IP subnetting, CIDR calculations
- Graph algorithms for routing
- Packet parsing/filtering

### 3. System Design (60 min)
**Focus**: Large-scale network architecture.

Common design questions:
- Design a global load balancer (like Google Front End / Maglev).
- Design a data center fabric (Clos topology).
- Design a CDN for YouTube (edge caching, origin shield).
- Design a DNS system handling 100B queries/day.

### 4. Deep Dive — Protocols (60 min)
**Focus**: Expert-level protocol knowledge.

Topics:
- QUIC handshake in detail
- TCP congestion control algorithms (BBR, CUBIC, pacing)
- BGP AS-path manipulation, communities, route reflectors
- MPLS, segment routing, traffic engineering

### 5. Network Operations / Scenario (60 min)
**Focus**: Troubleshooting at Google scale.

Scenario examples:
- A new data center BGP peer is causing traffic issues.
- Latency spikes between two global regions. Walk through debugging.
- A Jupiter fabric link is dropping packets. Root cause analysis.

### 6. Googleyness — Behavioral (45 min)
**Focus**: Leadership, ownership, collaboration.

Questions:
- Tell me about a time you disagreed with your manager.
- Describe a network outage you handled from start to finish.
- How do you influence without authority in a cross-team project?

---

## Must-Know Google Networking Papers

| Paper | Topic | Key Insight |
|-------|-------|-------------|
| Jupiter Rising | Data center topology | Clos fabric, 1000+ switch topology |
| B4: Google's SDN WAN | SDN WAN | Centralized traffic engineering |
| Maglev: A Fast Reliable SW LB | Load balancing | Consistent hashing, connection tracking |
| TCP BBR | Congestion control | Model-based, not loss-based |
| QUIC: A UDP-Based Secure Transport | Transport protocol | 0-RTT, connection migration |

---

## Protocol Depth Required

| Protocol | Required Knowledge Level |
|----------|------------------------|
| TCP/IP | Expert — congestion control, window management, timestamps, SACK, TFO |
| BGP | Expert — path selection, communities, route reflection, confederations |
| HTTP/2/3 | Advanced — HPACK, QPACK, stream prioritization, 0-RTT |
| DNS | Advanced — anycast, DNSSEC, stealth anycast |
| QUIC | Expert — handshake, 0-RTT, connection migration, stream IDs |
| MPLS | Intermediate — LDP, RSVP-TE, segment routing |
| VXLAN/EVPN | Intermediate — overlay/underlay, anycast gateway |

---

## Sample Study Plan

| Week | Focus | Resources |
|------|-------|-----------|
| 1 | TCP/IP deep dive | Stevens TCP/IP Illustrated Vol 1 |
| 2 | BGP + routing | BGP Design and Implementation |
| 3 | SDN + Google papers | SIGCOMM papers (Jupiter, B4, Maglev) |
| 4 | QUIC + HTTP/3 | RFC 9000, 9001, cloudflare quiche |
| 5 | System design practice | Design a CDN, LB, DNS system |
| 6 | Mock interviews | Practice out loud with timer |

---

## Key Tips

> "Know the details of QUIC's cryptographic handshake — they will ask."
> "Expect to be asked about a real Google outage and how you'd fix it."
> "Google values data-driven decisions — always support network designs with data."
> "Your Googleyness is as important as your protocol knowledge."

---

*"Google's network is the backbone of the internet for billions of users."*
