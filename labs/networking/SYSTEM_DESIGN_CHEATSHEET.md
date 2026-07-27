# Network Architecture — System Design Cheatsheet

> 300+ lines covering TCP/IP, HTTP/2 vs HTTP/3 (QUIC), DNS, CDN, load balancing, anycast, BGP, VPC design, and service mesh.

---

## 1. TCP/IP Protocol Stack

```
Application   ← HTTP, DNS, TLS, QUIC
Transport     ← TCP, UDP
Internet      ← IP, ICMP, ARP
Link          ← Ethernet, Wi-Fi, PPP
```

### TCP Key Concepts

| Concept | Details |
|---------|---------|
| 3-Way Handshake | SYN → SYN-ACK → ACK (adds RTT latency) |
| Congestion Control | Slow start → congestion avoidance → fast recovery |
| Flow Control | Sliding window via TCP window size |
| Selective ACK (SACK) | ACK non-contiguous segments for better loss recovery |
| TCP Timestamps | RTT estimation, PAWS (Protection Against Wrapped Sequences) |
| Nagle's Algorithm | Coalesce small packets to reduce overhead |
| TCP Fast Open (TFO) | Data in SYN packet (saves 1 RTT) |
| Window Scaling | Allows windows > 64KB for high-latency/high-BW links |

### Congestion Control Algorithms

| Algorithm | Key Feature | Use Case |
|-----------|-------------|----------|
| Cubic | Cubic window growth, Linux default | Long-fat pipes, general internet |
| BBR | Model-based (not loss-based) | High BDP paths, avoids bufferbloat |
| New Reno | Classic AIMD | Legacy compatibility |
| BIC | Binary search increase | Discontinued (replaced by Cubic) |
| DCTCP | Data center optimized | Low-latency DC environments |
| BBRv3 | Enhanced fairness, loss response | Google production networks |

### Interview Question: "What happens when you type google.com in a browser?"

1. DNS resolution (stale → resolver → root → TLD → authoritative)
2. TCP 3-way handshake (or QUIC 0-RTT)
3. TLS handshake (if HTTPS, adds 1-2 RTT)
4. HTTP request (GET /, headers, cookies)
5. Server processing (load balancer, cache, app, DB)
6. HTTP response (status, headers, body, caching headers)
7. Browser renders (parse HTML, fetch subresources, render)

---

## 2. HTTP/2 vs HTTP/3 (QUIC)

| Feature | HTTP/2 | HTTP/3 |
|---------|--------|--------|
| Transport | TCP (with TLS 1.2+) | QUIC (UDP-based) |
| Multiplexing | Streams over single TCP | Native QUIC streams |
| Head-of-Line Blocking | TCP-level HOL blocking | No HOL (independent streams) |
| Connection Establishment | 2+ RTT (TCP + TLS) | 0-RTT or 1-RTT |
| Migration | Not supported (TCP reset drops connection) | Connection migration (IP/port change) |
| Flow Control | Stream + connection level | Stream + connection level (QUIC) |
| Encryption | TLS layer (TLS 1.3 recommended) | Built-in (QUIC encrypts most of packet) |
| Header Compression | HPACK | QPACK (adapted for out-of-order) |
| Server Push | Supported | Deprecated / rarely used |
| Adoption | ~40% of websites | ~30%+ and growing (Google, Meta, Cloudflare) |

### QUIC Key Features

- **0-RTT**: Send data immediately if you've connected before
- **Connection Migration**: Survives network changes (Wi-Fi to cellular)
- **Stream Multiplexing**: No head-of-line blocking between streams
- **Built-in Encryption**: QUIC packets are mostly encrypted
- **Flow Control**: Per-stream and per-connection with credit-based flow control

### Interview Question: "Why does HTTP/3 use UDP?"

Because QUIC runs over UDP to avoid OS kernel dependency. HTTP/2's TCP-level head-of-line blocking occurs because TCP is a reliable, in-order byte stream. If one packet is lost, all streams block until retransmission. QUIC solves this by implementing reliability per-stream over UDP, so the protocol lives in userspace for rapid iteration.

---

## 3. DNS Resolution

### Resolution Flow

```
stale/ISP cache → Recursive Resolver → Root (.) → TLD (.com) → Authoritative (google.com)
```

| Step | Server | Lookup | Details |
|------|--------|--------|---------|
| 1 | Browser Cache | A/AAAA records | Checks local memory cache |
| 2 | OS Cache | A/AAAA records | Checks system resolver cache |
| 3 | Router/ISP Cache | A/AAAA records | Caching DNS server |
| 4 | Recursive Resolver | — | Set recurses for you |
| 5 | Root Server | NS for .com | Delegates to TLD |
| 6 | TLD Server | NS for google.com | GSLB-aware delegation |
| 7 | Authoritative | A/AAAA for www | Returns final answer |

### DNS Record Types

| Record | Purpose |
|--------|---------|
| A | IPv4 address |
| AAAA | IPv6 address |
| CNAME | Canonical name (alias) |
| MX | Mail exchange |
| NS | Name server |
| TXT | Arbitrary text (SPF, DKIM, DMARC) |
| SOA | Start of authority (admin info, serial, refresh) |
| SRV | Service location |
| PTR | Reverse DNS (IP → hostname) |

### Anycast DNS

- Multiple DNS servers share one IP address
- BGP routes users to closest/healthiest server
- Provides DDoS resilience, latency reduction, high availability
- Used by Cloudflare (1.1.1.1), Google (8.8.8.8), AWS Route53

---

## 4. CDN Design

### Architecture

```
User → Edge Server (PoP) → Mid-tier Cache → Origin Shield → Origin Server
```

| Component | Role |
|-----------|------|
| Edge Server | Closest to user, serves cached content (POP worldwide) |
| Mid-tier Cache | Reduces load on origin, aggregates edge misses |
| Origin Shield | Single point of contact, prevents origin overload |
| Origin | Your server (may be on-prem or cloud) |

### Caching Strategies

| Strategy | Description | Use Case |
|----------|-------------|----------|
| Cache Aside | App checks cache first, misses fallback to origin | API responses |
| Read-through | Cache sits between app and data store | Database queries |
| Write-through | Write to cache + store simultaneously | Consistency critical |
| Write-behind | Write to cache, async write to store | High write throughput |
| TTL-based | Content expired after time-to-live | Static assets, images |
| Invalidation | Purge specific URLs when content changes | Dynamic content |

### Cache Control Headers

```
Cache-Control: public, max-age=31536000, immutable
Cache-Control: private, no-cache, no-store, must-revalidate
Surrogate-Control: max-age=86400    (CDN-specific)
```

- `public` vs `private`: Can intermediate caches cache it?
- `max-age`: Seconds until stale
- `s-maxage`: CDN-specific max-age (overrides max-age for shared caches)
- `stale-while-revalidate`: Serve stale while fetching fresh in background
- `stale-if-error`: Serve stale if origin returns 5xx

---

## 5. Load Balancing Algorithms

| Algorithm | How It Works | Best For | Caveat |
|-----------|-------------|----------|--------|
| Round Robin | Sequentially assigns requests | Equal capacity servers | Doesn't account for load |
| Weighted RR | Based on assigned weight | Heterogeneous servers | Manual weight tuning |
| Least Connections | Send to fewest active connections | Variable request duration | Can overload with many short-lived |
| Least Response Time | Lowest latency + active connections | Latency-sensitive apps | Needs accurate latency data |
| IP Hash | Hash(client IP) → server | Sticky sessions | Uneven distribution with small pools |
| Consistent Hash | Hash ring distribution | Large, dynamic pools | Minimal reshuffling on add/remove |
| Maglev (Google) | TCAM-based consistent hash | Very high throughput (10M+ cps) | More complex to implement |
| Weighted Least Cost | Multiple metrics (CPU, mem, net) | Advanced workloads | Requires agent on servers |
| Random | Random selection | Simple, large pools | Can be very balanced statistically |

### L4 vs L7 Load Balancing

| Aspect | L4 (Transport) | L7 (Application) |
|--------|---------------|------------------|
| Layer | TCP/UDP | HTTP, gRPC, WebSocket |
| Visibility | IP + port only | Full headers, cookies, path |
| Features | NAT, DSR | Rewriting, rate limiting, auth |
| Performance | Higher throughput (DPDK) | Lower (needs to parse content) |
| Use Case | UDP gaming, VPN | Web APIs, microservices |
| Examples | AWS NLB, HAProxy (TCP mode) | AWS ALB, Envoy, NGINX |

---

## 6. Anycast Routing

### How It Works

- Same IP prefix advertised from multiple locations
- BGP's shortest path selection routes users to nearest location
- If a location fails, BGP withdraws route and traffic shifts

### Benefits

| Benefit | Explanation |
|---------|-------------|
| Lower Latency | Users reach nearest PoP automatically |
| High Availability | Automatic failover via BGP |
| DDoS Mitigation | Traffic distributed across multiple sites |
| Simple DNS | Single IP for all regions |

### Trade-offs

- BGP convergence time (30s-60s for full table propagation)
- Session affinity must be handled at application layer
- Not all TCP connections survive reroutes cleanly
- Difficult with stateful protocols

### Use Cases

- DNS (Cloudflare 1.1.1.1, Google 8.8.8.8)
- CDN (Cloudflare, Fastly, CloudFront)
- HTTP(S) frontends (Google, Facebook)

---

## 7. BGP Routing

### BGP Path Selection Algorithm

BGP selects a single best path (in order of importance):

1. **Highest Weight** (Cisco proprietary, local)
2. **Highest Local Preference** (AS-wide)
3. **Prefer locally originated** (network/aggregate)
4. **Shortest AS_PATH**
5. **Lowest Origin type** (IGP < EGP < incomplete)
6. **Lowest MED** (inter-AS metric)
7. **Prefer eBGP over iBGP**
8. **Lowest IGP metric to next-hop**
9. **If both are eBGP, oldest path wins** (stability)
10. **Lowest Router-ID**
11. **Lowest Peer IP Address**

### BGP Attributes Summary

| Attribute | Type | Scope | Description |
|-----------|------|-------|-------------|
| Weight | Cisco | Local | Higher is better, not transitive |
| Local Preference | Well-known | AS | Higher is better, advertised within AS |
| AS_PATH | Well-known | Global | Shorter is better, loop prevention |
| Origin | Well-known | Global | IGP < EGP < incomplete |
| MED | Optional | Between ASes | Lower is better, not transitive |
| Next-Hop | Well-known | — | Where to forward packets |
| Community | Optional | Flexible | Tag-based route manipulation |

### BGP Migrations and Hijacks

- **Hijack detection**: RPKI, BGP monitoring (BGPStream, BGPMon)
- **Prefix filtering**: Don't accept prefixes you don't own
- **Max-prefix**: Limit number of prefixes from peer
- **TTL Security**: GTSM (General TTL Security Mechanism)
- **Routing registries**: IRR (Internet Routing Registry) filtering

---

## 8. VPC Design

### VPC Components (AWS)

```
Internet Gateway ─┬── Public Subnet ─── NAT Gateway ─┬── Private Subnet
                  │                                   │
                  └── Public Subnet ──── Load Balancer └── Private Subnet
```

| Component | Purpose |
|-----------|---------|
| VPC | Virtual private cloud, isolated network segment |
| Subnet | AZ-scoped IP range (public/private) |
| Route Table | Determines where traffic goes based on destination |
| Internet Gateway | Enables public internet access |
| NAT Gateway | Private → internet (initiated from private) |
| Security Group | Stateful instance-level firewall |
| NACL | Stateless subnet-level firewall |
| VPC Peering | Direct connection between VPCs |
| Transit Gateway | Hub for many VPCs and on-prem |
| VPC Endpoint | Private access to AWS services |
| VPN / Direct Connect | Hybrid connectivity |

### Multi-AZ Architecture

```
┌─────────────────────────── AZ-1 ───────────────────────────┐
│  Public Subnet 1   ──   App Subnet 1   ──   DB Subnet 1   │
└────────────────────────────────────────────────────────────┘
┌─────────────────────────── AZ-2 ───────────────────────────┐
│  Public Subnet 2   ──   App Subnet 2   ──   DB Subnet 2   │
└────────────────────────────────────────────────────────────┘
```

- Deploy load balancer across both AZs
- Application instances in each AZ
- Database with cross-AZ replication (RDS Multi-AZ/Aurora)
- NAT Gateway per AZ (avoid cross-AZ NAT traffic)

### Subnetting Quick Reference

| Prefix | Subnet Mask | Usable IPs | Use Case |
|--------|-------------|------------|----------|
| /16 | 255.255.0.0 | 65,531 | Entire VPC |
| /20 | 255.255.240.0 | 4,091 | Large subnet |
| /24 | 255.255.255.0 | 251 | Standard subnet |
| /26 | 255.255.255.192 | 59 | Small subnet |
| /28 | 255.255.255.240 | 11 | Very small (e.g., for interfaces) |

---

## 9. Service Mesh Networking

### Architecture

```
┌─── Pod A (Mesh Sidecar Envoy) ─── mTLS ─── Pod B (Mesh Sidecar Envoy) ───┐
│                                                                           │
│  Control Plane (Istiod) — Discovery, Config, Certificates                │
└───────────────────────────────────────────────────────────────────────────┘
```

### Key Functions

| Function | Description |
|----------|-------------|
| Traffic Routing | Canary, blue/green, circuit breaking, retries |
| Security | mTLS between every service, certificate rotation |
| Observability | Metrics (prometheus), tracing (Jaeger), access logs |
| Policy | Rate limiting, RBAC, quota enforcement |

### Service Mesh Comparison

| Feature | Istio | Linkerd | Consul Connect |
|---------|-------|---------|----------------|
| Proxy | Envoy | Linkerd-proxy | Envoy + built-in |
| Control Plane | Istiod | Controller | Consul Server |
| Performance | Moderate (Envoy is fast) | Lower latency | Moderate |
| Complexity | High | Low | Medium |
| mTLS | Auto (mutual) | Auto (mutual) | Auto (mutual) |
| Traffic Split | Yes (weighted) | Yes | Yes |
| Multiclustor | Yes (SPIFFE) | (via mesh gateway) | Yes (WAN gossip) |

### eBPF for Networking

- **Cilium**: Uses eBPF to replace kube-proxy, provide network policies, observability
- **Benefits**: No sidecar needed, kernel-level efficiency, transparent encryption (WireGuard)
- **Use cases**: Network policies, load balancing, traffic visibility, security observability
- **Comparison**: Cilium (eBPF) can replace CNI + service mesh components in some architectures

---

## Quick Reference: TCP State Machine

```
CLOSED
  └─→ SYN_SENT ──→ ESTABLISHED
CLOSED
  └─→ LISTEN ──→ SYN_RCVD ──→ ESTABLISHED
ESTABLISHED
  └─→ FIN_WAIT_1 ──→ FIN_WAIT_2 ──→ TIME_WAIT ──→ CLOSED
ESTABLISHED
  └─→ CLOSE_WAIT ──→ LAST_ACK ──→ CLOSED
```

---

*"Networks are just systems of negotiated agreements."*
