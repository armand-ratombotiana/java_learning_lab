# Backend Academy — System Design Cheatsheet

<div align="center">

![System Design](https://img.shields.io/badge/System_Design-4285F4?style=for-the-badge)
![Cheatsheet](https://img.shields.io/badge/Cheatsheet-FF6F00?style=for-the-badge)

**Quick reference for system design interviews with backend focus**

</div>

---

## Load Balancing Patterns

### Algorithms
| Algorithm | How It Works | Best For | Caveats |
|-----------|-------------|----------|---------|
| Round Robin | Requests distributed sequentially | Equal-capacity servers | Server stickiness issues |
| Least Connections | Route to connection with fewest active | Long-lived connections | Metrics overhead |
| Least Response Time | Weight by response time | Heterogeneous servers | Response time overhead |
| IP Hash | Hash client IP to server | Session persistence | Uneven distribution |
| Consistent Hash | Hash ring with virtual nodes | Cache clusters, CDNs | Complexity, rebalancing |

### Load Balancer Types
| Type | Layer | Example | Use Case |
|------|-------|---------|----------|
| DNS Load Balancing | Application | Route53, CloudDNS | Global traffic distribution |
| Hardware LB | Network/Transport | F5, Citrix ADC | On-premise, high throughput |
| Software LB | Network/Transport | HAProxy, Nginx | Cloud-native, flexible |
| Cloud LB | Various | ALB, NLB, GCLB | AWS, GCP, Azure |

### Key Concepts
- **Health checks:** TCP, HTTP, or gRPC probes to detect unhealthy instances
- **Connection draining:** Gracefully remove instance from rotation before termination
- **Sticky sessions:** Route same client to same instance (problematic for fault tolerance)
- **Auto-scaling:** Scale in/out based on metrics (CPU, memory, request count, custom metrics)

---

## Caching Strategies

### HTTP Caching
| Header | Purpose | Example |
|--------|---------|---------|
| Cache-Control | Directives for caching behavior | `public, max-age=3600, must-revalidate` |
| ETag | Version identifier for resource | `"33a64df551425fcc55e4d42a148795d9f25f89d4"` |
| Last-Modified | Timestamp of last modification | `Wed, 21 Oct 2024 07:28:00 GMT` |
| Expires | Absolute expiry time | `Expires: Thu, 01 Dec 2024 16:00:00 GMT` |

### CDN Caching
| Concept | Description |
|---------|-------------|
| Origin Pull | CDN fetches from origin server on cache miss |
| Push | Content proactively pushed to CDN |
| Geo-distribution | Edge locations near users |
| Cache Invalidation | Purge by URL, tag, or wildcard |
| TTL | Time-to-live before re-fetch from origin |

### Application Caching
| Cache | Latency | Capacity | Use Case |
|-------|---------|----------|----------|
| In-Memory (local) | < 0.1ms | RAM-limited | Hot data per instance |
| Redis | < 1ms | RAM (cluster: TB+) | Distributed cache, leaderboards |
| Memcached | < 1ms | RAM | Simple key-value, ephemeral |
| CDN | < 50ms | Elastic | Static assets, API responses |

### Cache Invalidation Strategies
| Strategy | Mechanism | Pros | Cons |
|----------|-----------|------|------|
| TTL-based | Time expiration | Simple, predictable | Stale data until TTL |
| Event-driven | Message queue invalidation | Near real-time | Complexity, message loss |
| Write-through | Update cache on write | Strong consistency | Higher write latency |
| Version-based | Cache key includes version | Clean invalidation | Version management |

---

## Database Sharding and Replication

### Sharding Strategies
| Strategy | Algorithm | Best For | Trade-offs |
|----------|-----------|----------|------------|
| Key-based (Hash) | SHA/DJB hash of partition key | Even distribution | Rehashing on resharding |
| Range-based | Partition key range (e.g., A-F) | Range queries | Hot spots, data skew |
| Directory-based | Lookup table maps key to shard | Flexibility | Single point of failure |
| Geographic | Region-based partitioning | Geo-distributed apps | Cross-region queries |

### Sharding Challenges
| Challenge | Description | Solution |
|-----------|-------------|----------|
| Cross-shard queries | JOINs across shards | Denormalization, application-level join |
| Distributed transactions | ACID across shards | 2PC, Saga pattern |
| Resharding | Adding/removing shards | Consistent hashing, virtual nodes |
| Hot spots | Uneven load distribution | Rebalance, split hot shards |
| Global secondary indexes | Index across shards | Scatter-gather, materialized views |

### Replication Strategies
| Strategy | Write | Read | Consistency |
|----------|-------|------|-------------|
| Single-leader | One node | Any node | Strong (read-after-write from leader) |
| Multi-leader | Multiple nodes | Any node | Eventual (conflict resolution needed) |
| Leaderless | Any node (quorum) | Any node (quorum) | Tunable (R + W > N) |

### Replication Topologies
```
Single-Leader:          Multi-Leader:          Leaderless:
    [Leader]             [L1]───[L2]             [N1]───[N2]
       |                    |      |                |      |
    [F1] [F2] [F3]       [F1]   [F2]            [N3]───[N4]
```

---

## Message Queues (Kafka, RabbitMQ)

### Kafka vs RabbitMQ
| Feature | Kafka | RabbitMQ |
|---------|-------|----------|
| Message Model | Log-based (append-only) | Queue-based (push/pull) |
| Retention | By time/size (persistent) | By acknowledgment (deleted) |
| Ordering | Per-partition guaranteed | Per-queue guaranteed |
| Throughput | Millions/sec | Tens of thousands/sec |
| Routing | Topic-based (publish-subscribe) | Exchange types (direct, topic, fanout, headers) |
| Consumer Groups | Yes (partition-level) | Yes (competing consumers) |
| Message Replay | Yes (seek to offset) | No (acknowledged) |
| Exactly-once | Idempotent + transactional | Confirms + transactions |

### Kafka Core Concepts
```
Topic ──→ Partition ──→ Segment ──→ Log
  │           │
  │     [Leader/Follower]
  │
  ├── Producer (acks=all, idempotent)
  └── Consumer Group (one consumer per partition)
```

| Concept | Description | Configuration |
|---------|-------------|---------------|
| acks=0 | Fire and forget | Highest throughput, data loss |
| acks=1 | Leader acknowledged | Moderate durability |
| acks=all | All replicas ack | Strongest durability |
| min.insync.replicas | Minimum ISR count | Availability vs consistency trade-off |

### RabbitMQ Exchange Types
| Exchange | Routing | Use Case |
|----------|---------|----------|
| Direct | Exact routing key | Point-to-point |
| Topic | Pattern matching (routing_key.*) | Pub-sub with topics |
| Fanout | Broadcast to all queues | Event broadcasting |
| Headers | Match headers (key-value) | Complex routing |

---

## Microservices Communication (Sync vs Async)

### Synchronous Communication
| Protocol | Pros | Cons |
|----------|------|------|
| REST (HTTP/1.1) | Simple, universal | Head-of-line blocking |
| gRPC (HTTP/2) | Binary, streaming, typed | Limited browser support |
| GraphQL | Flexible queries, batching | Caching complexity, N+1 risk |

### Asynchronous Communication
| Method | Pros | Cons |
|--------|------|------|
| Message Queue (Kafka/RabbitMQ) | Decoupled, buffered, replayable | Eventual consistency, complexity |
| Event Bus | Loose coupling, event-driven | Event schema evolution |
| Pub/Sub | Broadcast to many consumers | Ordering guarantee complexity |

### Communication Trade-offs
| Aspect | Sync | Async |
|--------|------|-------|
| Latency | Low (RTT) | Higher (queuing) |
| Coupling | Tight | Loose |
| Availability | Lower (chain dependency) | Higher (buffer, retry) |
| Complexity | Simple | Higher (schema, errors) |
| Debugging | Easy (request trace) | Hard (eventual) |
| Consistency | Strong | Eventual |

---

## Monitoring and Observability

### Three Pillars of Observability
| Pillar | Purpose | Tool |
|--------|---------|------|
| Logging | Discrete events | ELK, Loki, Splunk |
| Metrics | Aggregated measurements | Prometheus, Grafana |
| Tracing | Request flow across services | Jaeger, Zipkin, OpenTelemetry |

### RED Method (Microservices)
| Metric | What | Example |
|--------|------|---------|
| Rate | Requests per second | Total HTTP requests/sec |
| Errors | Failed requests | HTTP 5xx, exceptions |
| Duration | Latency distribution | p50, p95, p99 response time |

### Four Golden Signals (Google SRE)
| Signal | Description |
|--------|-------------|
| Latency | Time to serve a request |
| Traffic | Demand on the system |
| Errors | Rate of failed requests |
| Saturation | How "full" the service is |

### Spring Boot Actuator Metrics
```properties
# Expose all endpoints
management.endpoints.web.exposure.include=health,info,prometheus,metrics,env

# Custom metrics tags
management.metrics.tags.application=${spring.application.name}
management.metrics.tags.instance=${HOSTNAME:localhost}

# Micrometer + Prometheus
management.metrics.export.prometheus.enabled=true
```

---

## Deployment Strategies

### Deployment Types
| Strategy | Description | Downtime | Rollback | Cost |
|----------|-------------|----------|----------|------|
| Rolling | Gradual instance replacement | None | Slow (re-replace) | Low |
| Blue-Green | Two identical environments | None | Instant (DNS switch) | High (2x infra) |
| Canary | Small subset gets new version | None | Fast (route all to old) | Medium |
| A/B | Canary + feature flags | None | Fast | Medium |
| Shadow | Mirror traffic to new version | None | No impact (mirror) | High (2x traffic) |
| Feature Flags | Toggle features at runtime | None | Instant (toggle off) | Low |

### Blue-Green Deployment
```
[Load Balancer]
     |
     ├── BLUE (v1) [Active] ──→ Production traffic
     │
     └── GREEN (v2) [Staging] ──→ Testing traffic
     
Switch: Update DNS/LB to point to GREEN
Rollback: Switch back to BLUE
```

### Canary Deployment
```
[Load Balancer]
     |
     ├── 90% Traffic ──→ Stable (v1)
     │
     └── 10% Traffic ──→ Canary (v2)
     
Gradual: 10% → 25% → 50% → 100% (or rollback)
```

### Kubernetes Deployment Strategies
```yaml
# Rolling update (default)
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 25%
    maxUnavailable: 25%

# Blue-Green via Service selector
# Canary via traffic splitting (service mesh)
```

### Health Check Patterns
| Probe | Endpoint | Failure Action |
|-------|----------|----------------|
| Liveness | /actuator/health/liveness | Restart container |
| Readiness | /actuator/health/readiness | Remove from service |
| Startup | /actuator/health/startup | Delay liveness checks |

---

## Back-of-the-Envelope Reference

### Latency Numbers
| Operation | Latency |
|-----------|---------|
| L1 cache reference | 0.5 ns |
| L2 cache reference | 7 ns |
| RAM access | 100 ns |
| SSD random read | 150 μs |
| Network packet (same DC) | 500 μs |
| Network packet (cross-region) | 50-100 ms |
| HDD random seek | 10 ms |
| Database write (single row) | 5-50 ms |
| HTTP request (API call) | 50-500 ms |

### Throughput Estimates
| Component | Max Throughput |
|-----------|---------------|
| Single web server (nginx) | ~50K RPS |
| Single PostgreSQL instance | ~5K TPS (writes) |
| Redis single instance | ~100K OPS |
| Kafka single partition | ~100K msg/s |
| Kafka cluster | Millions msg/s |
| DynamoDB single partition | ~1K WCU / ~3K RCU |

---

<div align="center">

**"Design for failure, optimize for speed, measure everything."**

---

[Back to Top](#backend-academy--system-design-cheatsheet)

</div>
