# Backend Academy — System Design Cheatsheet

<div align="center">

![System Design](https://img.shields.io/badge/System_Design-4285F4?style=for-the-badge)
![Backend](https://img.shields.io/badge/Backend_Architecture-6DB33F?style=for-the-badge)
![Decision Tree](https://img.shields.io/badge/Decision_Tree-FF6F00?style=for-the-badge)

**Backend-focused system design — API design, databases, microservices, caching, queues, scalability, reliability, observability, security, cloud deployment**

</div>

---

## Table of Contents

1. [API Design](#1-api-design)
2. [Database Design](#2-database-design)
3. [Microservices Architecture](#3-microservices-architecture)
4. [Caching Strategies](#4-caching-strategies)
5. [Message Queues](#5-message-queues)
6. [Scalability](#6-scalability)
7. [Reliability](#7-reliability)
8. [Observability](#8-observability)
9. [Security](#9-security)
10. [Cloud Deployment](#10-cloud-deployment)
11. [Interview Templates](#11-interview-templates)
12. [Architecture Decision Tree](#12-architecture-decision-tree)

---

## 1. API Design

### REST vs GraphQL vs gRPC

| Aspect | REST | GraphQL | gRPC |
|--------|------|---------|------|
| **Protocol** | HTTP/1.1, HTTP/2 | HTTP/1.1, HTTP/2 | HTTP/2 |
| **Data format** | JSON, XML, YAML | JSON | Protocol Buffers (binary) |
| **Schema** | OpenAPI (Swagger) | Schema Definition Language (SDL) | Protobuf (.proto) |
| **Query flexibility** | Fixed endpoints | Client-defined queries | Predefined RPC methods |
| **Over-fetching** | Common (fixed response) | None (client selects fields) | Depends on method design |
| **Under-fetching** | Common (multiple endpoints) | None (nested queries) | Handled by RPC method granularity |
| **Caching** | HTTP caching (strong) | Limited (POST-based) | Not built-in (need proxy) |
| **Streaming** | SSE, WebSocket | Subscriptions (WebSocket) | Native (unary, server, client, bidirectional) |
| **Tooling** | Postman, Swagger UI | Apollo, GraphiQL | grpcurl, Bloom RPC |
| **Best for** | CRUD, public APIs | Complex UIs, mobile apps | Internal microservices, low-latency |

### Versioning Strategies

| Strategy | Mechanism | Backward Compatible | Example |
|----------|-----------|--------------------|---------|
| URI path | `/api/v1/users`, `/api/v2/users` | No (breaking change) | Google (some APIs), Stripe |
| Header | `Accept: application/vnd.company.v1+json` | Yes (default to latest) | GitHub, Dropbox |
| Query param | `/api/users?version=1` | Yes (default to latest) | Twilio, Netflix |
| No versioning | Additive changes only, never remove | Yes | Google AIP (gRPC best practice) |

### Pagination Patterns

| Pattern | How It Works | Best For | Example |
|---------|-------------|----------|---------|
| Offset | `?offset=20&limit=10` | Static datasets, jump-to-page | SQL `OFFSET/LIMIT` |
| Cursor | `?cursor=eyJsYXN0X2lkIjogMTB9` | Dynamic data, real-time feeds | GitHub, Stripe |
| Keyset | `?after_id=100&limit=10` | High-throughput, stable sort | LinkedIn (keyset pagination) |
| Page | `?page=1&size=10` | Simple UIs, admin panels | Most public APIs |

```java
// Cursor-based pagination with Base64 encoding
record CursorPagination(String cursor, int limit) {
    public String encodeCursor(long lastId, Instant lastCreatedAt) {
        String data = lastId + "|" + lastCreatedAt.toString();
        return Base64.getUrlEncoder().encodeToString(data.getBytes());
    }

    public long decodeLastId() {
        String decoded = new String(Base64.getUrlDecoder().decode(cursor));
        return Long.parseLong(decoded.split("\\|")[0]);
    }
}
```

### Rate Limiting Algorithms

| Algorithm | Memory | Accuracy | Burst Handling | Use Case |
|-----------|--------|----------|---------------|----------|
| Token Bucket | O(1) | Good | Yes (accumulate tokens) | Simple rate limiting |
| Leaky Bucket | O(1) | Good | No (smooths requests) | Traffic shaping |
| Fixed Window | O(1) | Low (edge burst) | Yes | Simple per-minute caps |
| Sliding Window Log | O(N) | High | Yes | Accurate, fine-grained |
| Sliding Window Counter | O(M) | High | Partial | Production-grade (Redis) |

**Backend Labs:** Lab 02 (REST APIs — full REST design), Lab 17 (API Versioning), Lab 22 (GraphQL DGS)

---

## 2. Database Design

### SQL vs NoSQL

| Decision Factor | SQL (PostgreSQL, MySQL) | NoSQL (DynamoDB, MongoDB, Cassandra) |
|----------------|------------------------|--------------------------------------|
| Schema | Fixed, enforced | Flexible, schema-on-read |
| Relationships | Rich (JOINs, FOREIGN KEYs) | Denormalized, application-level joins |
| ACID | Strong support | Varies (DynamoDB: single-item; Cassandra: tunable) |
| Scaling | Vertical (primary), read replicas | Horizontal (sharding natively) |
| Consistency | Strong by default | Eventual by default |
| Query complexity | Complex (JOINs, subqueries, aggregations) | Simple (key-value, document queries) |
| Use cases | Transactions, reporting, complex queries | High throughput, flexible schema, global scale |

### Sharding Strategies

| Strategy | How It Works | Pros | Cons |
|----------|-------------|------|------|
| Range-based | Shard by ID range (1-1M, 1M-2M) | Simple, range queries efficient | Hot spots, uneven distribution |
| Hash-based | Hash key → shard (consistent hashing) | Even distribution | Re-sharding expensive, range queries cross shards |
| Directory-based | Lookup table maps key → shard | Flexible, re-sharding easier | Single point of failure, lookup overhead |
| Geographic | Shard by region | Data locality, low latency | Cross-region queries expensive |
| Entity-based | Group related entities on same shard | Joins possible within shard | Complex mapping, uneven entity sizes |

### Replication Patterns

| Pattern | Leader Count | Consistency | Use Case |
|---------|-------------|-------------|----------|
| Single Leader | 1 | Strong (from leader) | Traditional apps, read replicas |
| Multi-Leader | 2+ | Eventual | Cross-region, offline-first |
| Leaderless | 0 | Quorum-based | Cassandra, ScyllaDB |

### Indexing Strategies

| Index Type | Best For | Trade-off |
|-----------|----------|-----------|
| B-Tree | Range queries, exact match | General-purpose, writes slower |
| Hash Index | Point lookups (equality) | No range queries |
| GIN (Generalized Inverted Index) | Full-text search, arrays | Build time, write overhead |
| GiST | Geospatial, full-text | Larger index size |
| Partial Index | Filtered queries (WHERE status = 'active') | Only useful for specific queries |
| Covering Index | All columns in query are indexed | Larger index, fast reads |

**Backend Labs:** Lab 04 (Spring Data JPA — ORM, queries, indexes), Lab 21 (Multi-Tenancy — database isolation strategies)

---

## 3. Microservices Architecture

### Service Decomposition Approaches

| Approach | How | Best For |
|----------|-----|----------|
| Domain-Driven Design (DDD) | Decompose by bounded contexts | Complex business domains |
| Business Capability | Decompose by business functions | Clear organizational structure |
| Subdomain | Decompose by DDD subdomains (core/supporting/generic) | Strategic domain alignment |
| Strangler Fig | Incrementally extract services from monolith | Migration projects |

### Inter-Service Communication

| Pattern | Protocol | Coupling | Use Case |
|---------|----------|----------|----------|
| REST | HTTP/1.1 | Synchronous, tight | CRUD operations, public APIs |
| gRPC | HTTP/2 | Synchronous, typed | Internal services, low-latency |
| GraphQL | HTTP/1.1, HTTP/2 | Synchronous, flexible | API gateway, aggregation |
| Async Messaging | Kafka/RabbitMQ/AMQP | Asynchronous, loose | Event-driven, decoupled |
| SSE | HTTP | Server→client streaming | Real-time updates, notifications |
| WebSocket | TCP | Bidirectional streaming | Real-time collaboration |

### API Gateway Patterns

| Pattern | Responsibility | Example |
|---------|---------------|---------|
| Gateway Routing | Route to appropriate service | Spring Cloud Gateway, Zuul |
| Gateway Aggregation | Combine multiple service responses | GraphQL Federation |
| Gateway Offloading | Auth, rate limiting, SSL termination | Kong, AWS API Gateway |
| Gateway BFF (Backend For Frontend) | Customized API per client type | Mobile BFF, Web BFF |

### Service Mesh

| Capability | Istio | Linkerd | Consul |
|------------|-------|---------|--------|
| Traffic management | Yes (circuit breakers, retries, timeouts) | Yes (basic) | Yes |
| Observability | Prometheus, Grafana, Jaeger | Prometheus, Grafana | Built-in UI |
| Security | mTLS, RBAC | mTLS | mTLS, ACL |
| Complexity | High | Medium | Medium |
| Performance overhead | ~5-15ms latency | ~1-5ms latency | ~2-10ms latency |

**Backend Labs:** Lab 16 (Spring Cloud — service discovery, gateway, circuit breakers), Lab 23 (CQRS/Axon)

---

## 4. Caching Strategies

### Cache Types

| Cache | Location | Latency | Capacity | Eviction |
|-------|----------|---------|----------|----------|
| Application (in-memory) | Process RAM | < 0.1ms | Instance RAM | TTL, LRU |
| Distributed (Redis/Memcached) | Separate servers | < 1ms | Cluster RAM | TTL, LRU, LFU |
| Content Delivery Network (CDN) | Edge locations | < 50ms | Elastic | TTL, purge |
| HTTP Cache (browser/proxy) | Client/browser | < 5ms | Client disk | Cache-Control headers |

### Cache Invalidation Patterns

| Pattern | Write Strategy | Read Strategy | Consistency |
|---------|---------------|--------------|-------------|
| Cache-Aside | Write to DB, invalidate cache | Read cache → miss → read DB → write cache | Eventually consistent |
| Write-Through | Write to DB + cache simultaneously | Read from cache | Strong |
| Write-Behind | Write to cache, async write to DB | Read from cache | Eventually (risk of loss) |
| Refresh-Ahead | Cache refreshes before TTL expires | Read from cache (always fresh) | Eventually |

### Cache Eviction Policies

| Policy | Description | Use Case |
|--------|-------------|----------|
| LRU (Least Recently Used) | Evict oldest accessed item | General purpose |
| LFU (Least Frequently Used) | Evict least accessed item | Content popularity |
| FIFO (First In First Out) | Evict oldest written item | Static data |
| TTL (Time To Live) | Evict after fixed time | Session data, tokens |
| Random | Evict random item | Simple, cache-resilient data |

**Backend Labs:** Lab 13 (Caching — Redis, JCache, cache annotations in Spring Boot)

---

## 5. Message Queues

### Kafka vs RabbitMQ vs SQS

| Feature | Apache Kafka | RabbitMQ | AWS SQS |
|---------|-------------|----------|---------|
| **Model** | Distributed log | Broker (exchange + queue) | Fully managed queue |
| **Ordering** | Per-partition | FIFO queues | Standard (best-effort), FIFO |
| **Delivery** | At-least-once, exactly-once | At-most-once, at-least-once | At-least-once |
| **Retention** | Configurable (days/weeks) | Ack-based (deleted after ack) | Configurable (max 14 days) |
| **Throughput** | Millions/sec | Thousands/sec | Unlimited (scaling) |
| **Consumer** | Pull-based (offset management) | Push-based (or pull) | Pull-based (long polling) |
| **Persistence** | Disk (configurable sync) | Disk (configurable) | Disk (multi-AZ) |
| **Best for** | Event sourcing, stream processing, logging | Task queues, RPC, complex routing | Simple queue, decoupling |

### Event-Driven Architecture Patterns

| Pattern | Description | Example |
|---------|-------------|---------|
| Event Notification | Service emits event, others react | Order placed → send email |
| Event-Carried State Transfer | Event includes full data to avoid callbacks | Order event includes product details |
| Event Sourcing | Event store as source of truth | Axon Framework, Kafka + Schema Registry |
| CQRS | Separate read/write models | Lab 23 (CQRS/Axon) |
| SAGA | Distributed transaction with compensating actions | Order → Payment → Inventory → Shipping |
| Outbox Pattern | Write event to DB table, CDC captures in Kafka | Debezium + Kafka Connect |

```java
// Outbox Pattern template
@Entity
@Table(name = "outbox_events")
class OutboxEvent {
    @Id @GeneratedValue private Long id;
    private String aggregateType; // e.g., "Order"
    private String aggregateId;   // e.g., "order-123"
    private String eventType;     // e.g., "OrderPlaced"
    private String payload;       // JSON
    private Instant createdAt;
}
```

**Backend Labs:** Lab 07 (Messaging — Kafka, RabbitMQ, Spring Cloud Stream), Lab 23 (CQRS/Axon)

---

## 6. Scalability

### Horizontal vs Vertical

| Aspect | Vertical Scaling | Horizontal Scaling |
|--------|----------------|-------------------|
| **Method** | Add more RAM/CPU to existing server | Add more servers to a pool |
| **Limit** | Hardware maximum (theoretical infinite in cloud) | Theoretically infinite |
| **Downtime** | Requires downtime (often) | No downtime (rolling) |
| **Cost** | Higher per-unit cost at scale | Linear cost with commodity hardware |
| **Application** | Requires no changes | Requires stateless design |
| **Caveat** | Memory-bound applications | Distributed systems complexity |

### Load Balancing Strategies

| Strategy | Algorithm | Use Case |
|----------|-----------|----------|
| Round Robin | Sequential distribution | Equal-capacity servers |
| Least Connections | Route to server with fewest connections | Long-lived connections |
| Least Response Time | Route to server with fastest response | Heterogeneous servers |
| IP Hash | Hash client IP to server | Session persistence |
| Consistent Hash | Hash ring with virtual nodes | Cache affinity |

### Auto-Scaling Strategies

| Strategy | Metric | Example |
|----------|--------|---------|
| CPU-based | CPU utilization > 70% | General compute workloads |
| Memory-based | Heap usage > 80% | Java applications (GC pressure) |
| Request-based | Requests per instance > threshold | API services |
| Custom metric | Queue depth, latency p99 | Async consumers |
| Predictive | ML-based traffic forecasting | Known traffic patterns (e.g., e-commerce) |

**Backend Labs:** Lab 16 (Spring Cloud — load balancing with Spring Cloud LoadBalancer), Lab 24 (Backend Performance)

---

## 7. Reliability

### Circuit Breaker

| State | Behavior | Transition |
|-------|----------|------------|
| CLOSED | Normal operation, requests pass through | → OPEN when failures exceed threshold |
| OPEN | Requests fail fast (without calling service) | → HALF_OPEN after timeout |
| HALF_OPEN | Limited requests pass through to test recovery | → CLOSED on success, → OPEN on failure |

### Retry Patterns

| Pattern | How It Works | Best For |
|---------|-------------|----------|
| Simple Retry | Retry N times immediately | Transient failures |
| Exponential Backoff | Retry with increasing delay (2^n) | Rate limiting, throttling |
| Jitter | Add randomness to backoff delay | Avoid thundering herd |
| Circuit Breaker + Retry | Retry while circuit is CLOSED | Resilience at scale |

### Timeouts

| Timeout Type | Where | Typical Value |
|-------------|-------|---------------|
| Connection timeout | TCP handshake | 500ms - 5s |
| Read timeout | Waiting for response body | 5s - 30s |
| Write timeout | Sending request body | 5s - 30s |
| Idle timeout | Connection pool | 30s - 5min |

### Bulkhead Pattern

| Approach | Isolation | Thread Pools | Use Case |
|----------|-----------|-------------|----------|
| Thread Pool Bulkhead | Per-service thread pool | Service A: 10, Service B: 5 | Real-time user-facing APIs |
| Semaphore Bulkhead | Per-service semaphore count | 20 concurrent calls | Fast, non-blocking operations |

### Health Checks

| Type | Endpoint | Returns |
|------|----------|---------|
| Liveness | `/actuator/health/liveness` | Up/Down (is JVM alive?) |
| Readiness | `/actuator/health/readiness` | Up/Down (can serve traffic?) |
| Startup | `/actuator/health/startup` | Started (initialization complete?) |
| Custom | `/actuator/health/db` | Yes/No (is DB reachable?) |

**Backend Labs:** Lab 11 (Testing — resilience testing), Lab 16 (Spring Cloud — Resilience4J)

---

## 8. Observability

### Three Pillars

| Pillar | What | Data | Tooling |
|--------|------|------|---------|
| Logging | Discrete events with timestamps | Text entries with severity | ELK, Loki, Splunk |
| Metrics | Aggregated numeric measurements | Counters, gauges, histograms | Prometheus, Micrometer |
| Tracing | Request lifecycle across services | Spans with parent-child relationships | Jaeger, Zipkin, OpenTelemetry |

### Spring Boot Observability

| Component | Technology | Configuration |
|-----------|------------|---------------|
| Metrics | Micrometer + Prometheus | `micrometer-registry-prometheus` |
| Tracing | Micrometer Tracing + Brave/OpenTelemetry | `micrometer-tracing-bridge-brave` |
| Logging | Logback + MDC | Logback-spring.xml |
| Health | Spring Boot Actuator | `/actuator/health` |
| Environment | Actuator Info endpoint | `/actuator/info` |

### Key Metrics to Monitor (Backend)

| Metric | What It Tells | Target |
|--------|--------------|--------|
| Request rate (RPS) | Traffic volume | System capacity tracking |
| Latency (p50, p95, p99) | Response time distribution | p99 < 500ms (API), p99 < 5ms (cache) |
| Error rate (5xx) | Failure percentage | < 0.1% |
| GC pause time | JVM health | < 50ms/min |
| Thread pool utilization | Saturation | < 80% |
| Connection pool usage | DB connection pressure | < 70% |
| Queue depth | Consumer lag | Near 0 |

**Backend Labs:** Lab 11 (Testing — performance testing), Lab 24 (Backend Performance — profiling, monitoring)

---

## 9. Security

### Authentication & Authorization

| Mechanism | How | Best For |
|-----------|-----|----------|
| Session + Cookie | Server-side session, cookie for session ID | Traditional web apps |
| JWT (JSON Web Token) | Self-contained token (stateless) | REST APIs, microservices |
| OAuth 2.0 | Authorization framework with scopes | Third-party access, API authorization |
| OpenID Connect | Identity layer on top of OAuth 2.0 | SSO, user authentication |
| mTLS | Mutual TLS certificate authentication | Service-to-service (zero trust) |
| API Key | Static token in header | Simple service identification |

### Rate Limiting

| Approach | Implementation | Scale |
|----------|---------------|-------|
| Nginx/Istio | Per-instance token bucket | Edge proxy level |
| Redis | Centralized sliding window | Distributed (2M+ RPS with Redis cluster) |
| Application | Spring Boot interceptor + local cache | Per-instance (simple) |
| API Gateway | AWS API Gateway / Kong | Managed, multi-tenant |

### DDoS Protection

| Layer | Attack Type | Defense |
|-------|-------------|---------|
| L3/L4 | SYN flood, UDP amplification | Kernel tuning, CDN scrubbing, AWS Shield |
| L7 | HTTP flood, slow loris | Rate limiting, WAF (Cloudflare, AWS WAF) |
| DNS | DNS amplification | Anycast DNS, rate limit authoritative queries |

### Data Protection

| In Transit | At Rest | In Use |
|-----------|---------|--------|
| TLS 1.3 | AES-256 encryption | Homomorphic encryption (niche) |
| mTLS | Envelope encryption | Secure enclaves (AWS Nitro, SGX) |
| HSTS header | Key rotation policies | Confidential computing |

**Backend Labs:** Lab 06 (Security — Spring Security, OAuth2, JWT), Lab 20 (Backend Security Deep)

---

## 10. Cloud Deployment

### AWS / Azure / GCP Services for Backend

| Capability | AWS | Azure | GCP |
|------------|-----|-------|-----|
| **Compute (containers)** | ECS, EKS | AKS | GKE |
| **Serverless** | Lambda + SnapStart | Functions | Cloud Functions, Cloud Run |
| **Managed Spring Boot** | ECS + Spring Cloud | Azure Spring Apps | Google Cloud Run |
| **SQL Database** | RDS (Aurora) | SQL Database | Cloud SQL (Spanner) |
| **NoSQL** | DynamoDB | Cosmos DB | Firestore, Bigtable |
| **Cache** | ElastiCache (Redis) | Azure Cache for Redis | Memorystore |
| **Message Queue** | SQS, SNS | Service Bus | Pub/Sub |
| **Object Storage** | S3 | Blob Storage | Cloud Storage |
| **Load Balancer** | ALB, NLB | Azure Load Balancer | Cloud Load Balancing |
| **API Gateway** | API Gateway | API Management | Apigee, Cloud Endpoints |
| **CDN** | CloudFront | Azure CDN | Cloud CDN |

### Containerization Best Practices

```dockerfile
# Multi-stage build for Java Spring Boot
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
USER app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseZGC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
```

### Orchestration: Kubernetes

| Component | Purpose | Backend Impact |
|-----------|---------|---------------|
| Pod | Smallest deployable unit | 1 container per pod (recommended) |
| Deployment | Declarative update strategy | Rolling updates, rollbacks |
| Service | Stable network endpoint | Load-balanced access to pods |
| ConfigMap | Configuration injection | Externalize Spring Boot config |
| Secret | Sensitive data | DB passwords, API keys |
| Ingress | External HTTP routing | API gateway alternative |
| HPA | Horizontal Pod Autoscaler | Auto-scale based on CPU/memory/custom metrics |

**Backend Labs:** Lab 25 (GraalVM Native — native images for cloud deployment), Lab 26 (Spring Boot Internals — embedded server optimization)

---

## 11. Interview Templates

### Template 1: Design a URL Shortener (Bit.ly)

```markdown
**1. Requirements**
- Functional: shorten URLs, redirect, track analytics, custom aliases
- Non-functional: low latency (<10ms redirect), high availability (99.99%), high read throughput

**2. Estimations**
- 100M URLs created/month, 10B redirects/month
- Write: ~38 URLs/sec peak, Read: ~3,800 redirects/sec peak (100:1 read:write)
- Storage: 500 bytes per URL → 50GB/month → 1.8TB/year

**3. API Design**
- POST /shorten { url, customAlias?, ttl? } → { shortUrl, expiresAt }
- GET /{shortCode} → 301 redirect

**4. Data Model**
- shortCode VARCHAR(7) PK | originalUrl TEXT | createdAt | expiresAt | clickCount
- Index on expiresAt for TTL cleanup

**5. Key Decisions**
- Short code: Base62 encoding of auto-increment ID (or hash + collision detection)
- NoSQL vs SQL: DynamoDB for high throughput, or PostgreSQL for JOINs with analytics
- Cache: Redis for hot URLs (LRU, 24h TTL) → 90% cache hit rate
- Redirect: 301 (permanent) for SEO, 302 (temporary) for analytics

**6. Scaling**
- Web tier: horizontal auto-scaling behind ALB (CPU-based)
- DB: read replicas for redirects, leader for writes
- Cache: Redis cluster with read replicas, consistent hashing
- Analytics: Kafka → Flink → ClickHouse for real-time click analytics

**7. Trade-offs**
- Consistency: eventually consistent cache (stale redirects possible for <1s)
- Short code generation: centralized ID gen (ticket server/ZooKeeper) vs distributed (Snowflake)
- Storage: DB per URL vs blob store for long-tail → hybrid approach
```

### Template 2: Design a Payment System (Stripe-like)

```markdown
**1. Key Backend Challenges**
- Idempotency: same request never charges twice (idempotency key → Redis, dedup before DB)
- Exactly-once processing: Kafka transactional producer, idempotent consumers
- Double-entry ledger: every credit has a corresponding debit (DB transactions across accounts)

**2. API Design Example**
- POST /v1/charges { amount, currency, source, idempotency_key, description }
- GET /v1/charges/{id}

**3. Critical Components**
- Idempotency layer: Redis SET NX with TTL (24h), dedup before processing
- Ledger: PostgreSQL with SERIALIZABLE isolation for balance operations
- Webhook delivery: outbox pattern → Kafka → Kafka Connect → HTTP delivery with retries
- Fraud detection: real-time rule engine + ML model on transaction stream

**4. Failure Scenarios**
- Double charge (idempotency key crash): webhook notification, auto-refund
- Payment provider down: circuit breaker → async retry → dead-letter queue → manual
- Lost webhook: replay from dead-letter queue, idempotent webhook processing

**5. Observability**
- Track: payment success rate, processing latency per provider, chargeback rate
- Alert: success rate drop > 1%, latency p99 > 5s, provider downtime
```

### Template 3: Design a Real-Time Chat/Messaging System

```markdown
**1. Backend Architecture**
- WebSocket gateway (HAProxy → Netty server → Spring WebFlux)
- Redis PUB/SUB for cross-instance message routing
- Kafka for message persistence (append-only log)
- Cassandra/DynamoDB for chat history (time-series model)

**2. Key Patterns**
- WebSocket cluster: sticky sessions via Redis session store
- Presence detection: heartbeats + Redis Set with TTL (last seen)
- Typing indicators: Redis PUB/SUB topic per conversation (ephemeral)
- Delivery receipts: at-least-once delivery with ACK protocol

**3. Data Model**
- Messages: conversationId, messageId (timeUUID), senderId, content, timestamp, type
- Secondary index: conversationId + messageId (time-ordered) for pagination
- TTL: messages archived to S3 after 90 days, deleted from primary after 180 days

**4. Trade-offs**
- Ordering: within a conversation, sorted by server timestamp (Kafka partition ordering)
- Consistency: eventually consistent for multi-device sync (last-write-wins)
- Attachment: S3 with presigned URLs, virus scan via Lambda
- Multi-tenancy: separate topic namespace per workspace (Kafka topics)
```

**Backend Labs:** All labs — use the SYSTEM_DESIGN_CHEATSHEET.md (existing) for additional patterns

---

## 12. Architecture Decision Tree

```
Q1: What type of system are you building?
├── Public-facing CRUD API
│   ├── → REST + Spring Boot + JPA + PostgreSQL
│   └── → If high read throughput → add Redis cache (Lab 13)
│
├── Real-time streaming / messaging
│   ├── → WebSocket + Kafka + Redis (Labs 07, 15, 19)
│   └── → SSE for simple push (Lab 19)
│
├── High-throughput event processing
│   ├── → Kafka + Apache Flink / Kafka Streams (Lab 07)
│   └── → CQRS/Axon for event sourcing (Lab 23)
│
├── Microservices with complex workflows
│   ├── → Spring Cloud stack: Gateway + Eureka + Config (Lab 16)
│   ├── → SAGA pattern for distributed transactions
│   └── → Service mesh (Istio) for observability & security
│
├── Batch data processing
│   ├── → Spring Batch (Lab 18) or Apache Spark
│   └── → S3 + Lambda for file-triggered processing (Lab 18)
│
├── Real-time collaboration / multiplayer
│   ├── → WebSocket cluster (Lab 15, 19) + Redis PUB/SUB
│   └── → Operational transforms / CRDTs for conflict resolution
│
└── Global multi-region system
    ├── → Multi-leader DB (Cosmos DB / Cassandra) for writes
    ├── → CDN for static & API caching
    ├── → Consistent hashing for region-agnostic routing
    └── → Multi-tenancy isolation strategies (Lab 21)

Q2: Choose database:
├── Strongly consistent, complex queries
│   └── PostgreSQL / MySQL (SQL with ACID)
├── High throughput, flexible schema, global scale
│   └── DynamoDB / Cassandra / MongoDB (NoSQL)
├── Time-series data
│   └── ClickHouse / TimescaleDB / InfluxDB
└── Full-text search
    └── Elasticsearch / OpenSearch

Q3: Caching strategy:
├── Read-heavy, mostly static data
│   └── Cache-aside with Redis + TTL
├── Read-write balanced, needs consistency
│   └── Write-through cache
├── Write-heavy, eventual consistency OK
│   └── Write-behind cache
└── Global audience
    └── CDN (CloudFront / Cloudflare / Fastly)

Q4: Message queue:
├── Event sourcing, stream processing
│   └── Apache Kafka
├── Task queues, complex routing
│   └── RabbitMQ
├── Simple decoupling, fully managed
│   └── AWS SQS / Azure Service Bus
└── Real-time pub/sub
    └── Redis PUB/SUB + Kafka for persistence

Q5: Deployment strategy:
├── Managed platform
│   ├── AWS → ECS / ExK + RDS + ElastiCache + SQS
│   ├── Azure → AKS + Azure SQL + Cache for Redis + Service Bus
│   └── GCP → GKE + Cloud SQL + Memorystore + Pub/Sub
├── Kubernetes + Helm
│   └── Istio + Prometheus + Grafana + Jaeger
└── Serverless
    └── AWS Lambda + DynamoDB + SQS + S3
```

---

<div align="center">

**System design is not about finding the "right" answer — it's about making explicit trade-offs.**

</div>
