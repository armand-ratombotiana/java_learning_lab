# Service Discovery — Mock Interview Questions

## Fundamentals (3 questions)

**Q1**: What is service discovery? Why is it needed in distributed systems?

**Expected coverage**: Mechanism for services to find each other's network locations (IP:port), needed because containers/VMs start/stop/dynamically scale, IPs are ephemeral, clients need up-to-date endpoints. Two main approaches: client-side (client queries registry directly) and server-side (load balancer / proxy handles discovery).

**Q2**: Compare client-side and server-side service discovery patterns.

**Expected coverage**: Client-side discovery (client queries registry → gets list → load balances, e.g., Netflix Eureka + Ribbon, Consul + client libraries), server-side discovery (client hits LB/proxy → proxy handles discovery, e.g., AWS ALB + target group, Kubernetes Service + kube-proxy). Client-side: fewer network hops but client complexity. Server-side: centralized but adds hop.

**Q3**: What are the key components of a service discovery system?

**Expected coverage**: Service registry (storage of service instances, key: service name, value: list of endpoints), registration (self-registration on startup, third-party registration via sidecar), health checking (heartbeat/TTL probes, passive/unhealthy detection), deregistration (on graceful shutdown or health check failure), discovery (DNS, API, polling, watch/subscribe for changes).

## Intermediate (3 questions)

**Q4**: Compare major service discovery tools: Consul, etcd, ZooKeeper, Eureka, and Kubernetes DNS.

**Expected coverage**: Consul (DNS + HTTP API + health checks + KV store, raft-based, gossip for membership, multi-datacenter), etcd (KV store, raft-based, used by Kubernetes, no built-in DNS), ZooKeeper (hierarchical znodes, Zab consensus, ephemeral nodes for health), Eureka (AP system, eventual consistency, self-preservation mode, used in Netflix/Spring Cloud), K8s DNS (SRV records for headless services, DNS-based, service updates polled by kube-dns/CoreDNS).

**Q5**: How does Kubernetes service discovery work? Explain DNS + endpoints + kube-proxy.

**Expected coverage**: Service creates DNS entry (<svc>.<ns>.svc.cluster.local), kube-proxy on each node watches endpoints (via API server) and configures iptables or IPVS rules, controlling traffic to ready pods. Headless services (clusterIP: None) return pod IPs via DNS. EndpointSlice for large clusters (scalable endpoint management).

**Q6**: What happens when a service instance goes down? Walk through the service discovery lifecycle.

**Expected coverage**: Health check fails → registry marks unhealthy (grace period) → removes from registry → watchers receive update → clients/LBs update their endpoint list → new requests no longer routed to dead instance → existing connections (if any) eventually fail or drain. Self-registering services deregister on SIGTERM before shutdown.

## Advanced (2 questions)

**Q7**: Design service discovery for a multi-datacenter deployment with 5000 services.

**Expected coverage**: Consul with federation (WAN gossip, separate AC datacenters), or Consul with prepared queries (failover across DCs), gRPC resolver with xDS for traffic routing across DCs, latency-aware routing (prefer local DC, failover to remote), health check cross-DC bandwidth limits, preventing cascading failover (circuit breakers, rate limiters), observability (grafana dashboards for registry health, endpoint propagation delay).

**Q8**: Your service discovery is slow to propagate changes. Clients hit dead endpoints. How do you fix it?

**Expected coverage**: Reduce health check intervals (but increase load), implement passive health checking (circuit breaking: client marks as unhealthy after N failures), increase connection retries with exponential backoff, reduce DNS TTL (but increase query volume), use watch/subscribe instead of polling, verify no DNS caching at intermediate resolvers, implement client-side caching with TTL and background refresh.
