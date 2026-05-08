# Pedagogic Guide - Consul

## Learning Path

### Phase 1: Core Concepts
1. Service discovery architecture
2. Consul architecture (server, client, agents)
3. Service registration and deregistration
4. DNS-based discovery

### Phase 2: Configuration
1. Spring Cloud Consul setup
2. Service registration properties
3. Health check configuration
4. Metadata and tags

### Phase 3: Service Discovery
1. REST API discovery
2. DNS SRV record queries
3. Spring client implementations
4. Load balancing strategies

### Phase 4: Key-Value Store
1. KV operations and paths
2. Transactions and watches
3. Configuration integration
4. Environment-specific configs

### Phase 5: Advanced Features
1. Health checks (HTTP, TCP, TTL, Script)
2. ACL system and tokens
3. Multi-datacenter federation
4. Consul Connect (service mesh)

## Interview Topics

| Topic | Description |
|-------|-------------|
| Service Discovery | How services find each other |
| Health Checks | Ensuring service availability |
| Consistency | CAP theorem implications |
| ACLs | Security and access control |
| Federation | Multi-datacenter deployment |

## Comparison with Alternatives

| Feature | Consul | Eureka | Zookeeper |
|---------|--------|--------|------------|
| Protocol | HTTP/DNS | HTTP | ZAB |
| Consistency | Strong | Eventual | Strong |
| KV Store | Yes | No | Yes |
| Multi-DC | Native | Limited | Yes |
| Learning Curve | Medium | Low | High |

## Next Steps
- Explore Consul Connect for mTLS between services
- Learn Intentions for service-to-service authorization
- Study prepared queries for advanced discovery