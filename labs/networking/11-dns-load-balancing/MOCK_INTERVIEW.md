# DNS & Load Balancing (Advanced) — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: Explain how DNS resolvers handle DNSSEC validation.

**Expected coverage**: DNS Security Extensions (DNSSEC): RRSIG records (digital signature over record set), DNSKEY (public key for zone), DS (delegation signer, hash of child's DNSKEY, chain of trust from root), NSEC/NSEC3 (authenticated denial of existence). Resolver builds chain: root → TLD → authoritative, validates signatures. AD flag in response. Validation failure returns SERVFAIL.

**Q2**: What is Global Server Load Balancing (GSLB)? How does DNS-based GSLB work?

**Expected coverage**: Distributing traffic across multiple data centers/regions, DNS-based (returns different A records based on client location, latency, or availability), health check (monitors each site, removes failing sites from DNS response), TTL management (short TTL for failover, longer TTL for stability), route optimization (client subnet EDNS0 for geolocation accuracy).

**Q3**: Compare DNS-based load balancing with anycast routing.

**Expected coverage**: DNS-based (different IP per user/location, DNS resolver selects based on policy, users get different IPs, caching means slow failover), anycast (same IP everywhere, BGP routes to nearest PoP, fast failover via BGP withdrawal, stateless connections may break on reroute, ideal for stateless UDP services like DNS). Often combined: DNS for initial routing, anycast for distribution within region.

## Intermediate (3 questions)

**Q4**: Explain the different TCP load balancing modes: NAT, DSR (Direct Server Return), and proxy.

**Expected coverage**: NAT mode (LB rewrites dest IP:port to backend, backend replies through LB, asymmetric but simple, LB is bottleneck), DSR mode (LB rewrites dest MAC only, backend replies directly to client with VIP on loopback, good scalability, requires backend config), Proxy mode (full TCP termination at LB, new TCP to backend, full control but double TCP overhead, works for L7). AWS NLB uses NAT, DSR-like for Geneve encapsulation.

**Q5**: How does consistent hashing work in load balancers? Why is it used?

**Expected coverage**: Hash key (client IP or connection tuple) placed on ring, each server on ring, find first server clockwise from key. Adding/removing servers only affects neighbors (minimal reshuffling). Virtual nodes for better distribution when servers have unequal capacity. Used by Maglev, Amazon Dynamo, CDN cache routing. Reduces cache misses when server pool changes.

**Q6**: How does connection draining work in load balancers? Why is it important?

**Expected coverage**: When a backend server is deregistered, LB stops sending new connections but keeps existing connections alive for a configurable drain period (e.g., 300 seconds), allows in-flight requests to complete before fully removing server, prevents request failures during deployments, ALB has connection draining (default 300s, configurable), NLB has connection termination on deregistration. Key for zero-downtime deployments.

## Advanced (2 questions)

**Q7**: Design a global load balancing system that handles user session stickiness (no dropped sessions on reroute).

**Expected coverage**: Client-side solution: consistent hash on session ID → same backend even across regions (but adds cross-region latency), centralized session store (Redis across regions, GeoRedis for local reads), sticky cookies set by LB (identified backend), DNS-based: route by user location but maintain session via backend replication, connection draining on failover, client retry with session token in request header.

**Q8**: Your DNS-based load balancer is returning outdated records due to resolver caching. Traffic hits a failed region. How do you fix it?

**Expected coverage**: Reduce DNS TTL (trade-off: more queries), implement stale-while-revalidate (serve stale during refresh), use CDN with health checks (CloudFront can failover), DNSSEC-aware resolvers respect TTL, proactive monitoring (route53 health checks auto-remove unhealthy records), client-side fallback (if request fails, retry next IP), Anycast DNS for faster propagation, purge DNS caches at critical resolvers.
