# DNS & Load Balancing — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Walk through DNS resolution from browser to authoritative server.

**Expected coverage**: Browser cache → OS resolver cache → local DNS resolver (router/ISP) → root server (.) → TLD server (.com) → authoritative nameserver (ns.example.com) → returns A/AAAA record. Each level caches with TTL. Recursive vs iterative resolution. EDNS0 for larger payloads.

**Q2**: What are the main DNS record types and their purposes?

**Expected coverage**: A (IPv4), AAAA (IPv6), CNAME (canonical name, alias), MX (mail exchanger, priority field), NS (nameserver delegation), TXT (text, SPF/DKIM/DMARC), SOA (start of authority), PTR (reverse DNS), SRV (service location, priority/weight/port), CAA (certification authority authorization).

**Q3**: Compare round-robin DNS, weighted round-robin, and DNS-based load balancing (e.g., Route53 latency/geolocation).

**Expected coverage**: Round-robin DNS (multiple A records returned, rotated order), weighted RR (WRR weight field), limitations (no health checking, clients may cache first result, no failover), Route53 latency (routes to lowest latency region based on latency probes), geolocation (based on client IP location), geoproximity (with bias), failover (primary + secondary with health checks).

## Intermediate (3 questions)

**Q4**: What is anycast DNS? How does Cloudflare's 1.1.1.1 use it?

**Expected coverage**: Same IP prefix announced from multiple locations via BGP, BGP shortest path routes user to nearest PoP, benefits (lower latency, automatic failover, DDoS distribution), stateless (connection migration handled by client retry), stealth anycast for authoritative DNS, anycast vs unicast trade-offs.

**Q5**: Explain L4 vs L7 load balancing. When would you use each?

**Expected coverage**: L4 (TCP/UDP, transport layer, NAT/DSN, faster, no content awareness), L7 (HTTP/HTTPS/gRPC, full content awareness, path/routing/rewriting, slower due to parsing), L4 examples (NLB, HAProxy TCP mode), L7 examples (ALB, Envoy, NGINX), use case: L4 for performance-critical TCP/UDP, L7 for smart routing to microservices.

**Q6**: How do health checks work in load balancers? What types are there?

**Expected coverage**: TCP health check (SYN → SYN-ACK or RST), HTTP health check (GET /health → 200 OK), gRPC health check (gRPC Health Checking Protocol), application-level health check (custom endpoint), passive health check (connection failure tracking), circuit breaking (Envoy: consecutive failures trigger ejection), graceful degradation (draining connections before removal).

## Advanced (2 questions)

**Q7**: Design a global DNS system for an e-commerce platform with active-active regions.

**Expected coverage**: Route53 latency-based routing directing to nearest region, health checks on each region's load balancer, TTL tuning (60s for fast failover), failover policy (primary + secondary), anycast for DNS (fast resolution globally), monitoring with Route53 health checks + CloudWatch, DNS caching strategy (stale-while-revalidate for resilience).

**Q8**: You notice increased latency after a DNS change. Walk through debugging.

**Expected coverage**: Check TTL (clients caching old records), dig +trace for resolution path, check for DNS blackholing, check anycast routing changes (BGP path change), compare latency from multiple probes (catchpoint, pingdom), verify authoritative server load (queries/sec, CPU), check for missing secondary DNS, verify EDNS0 support end-to-end.
