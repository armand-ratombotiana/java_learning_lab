# System Design Cheatsheet — Architecture Patterns Deep Reference

> Comprehensive reference for architecture patterns tested in staff+ system design interviews.

---

## Table of Contents

1. [Integration Patterns](#1-integration-patterns)
2. [Decomposition Patterns](#2-decomposition-patterns)
3. [Database Patterns](#3-database-patterns)
4. [Resilience Patterns](#4-resilience-patterns)
5. [Observability](#5-observability)
6. [Security Patterns](#6-security-patterns)
7. [Deployment Patterns](#7-deployment-patterns)
8. [Communication Patterns](#8-communication-patterns)
9. [Data Flow Patterns](#9-data-flow-patterns)
10. [Architecture Decision Framework](#10-architecture-decision-framework)

---

## 1. Integration Patterns

### API Gateway Pattern

**Purpose**: Single entry point for all client requests, providing routing, composition, authentication, rate limiting, and protocol translation.

```
Client → API Gateway → [Auth | Rate Limiter | Router] → Service A
                                           ↘           → Service B
                                                        → Service C
```

**Key decisions:**
- **Technology**: NGINX, Kong, AWS API Gateway, Azure API Management, Zuul (Spring)
- **Routing**: Path-based, header-based, query-parameter-based
- **Authentication**: JWT validation, OAuth2 token exchange, API key verification
- **Rate limiting**: Token bucket, leaky bucket, sliding window (per client, per endpoint, per tier)
- **Caching**: Response caching at gateway level for idempotent GET requests

**Trade-offs:**
- **Pro**: Centralized security, client simplification, cross-cutting concerns
- **Con**: Single point of failure (mitigate with HA), performance bottleneck (mitigate with horizontal scaling), adds latency

**Interview tips:**
- For staff+: discuss gateway as a "fabric" or "mesh" rather than a single node
- Mention API gateway vs service mesh overlap: gateway handles north-south traffic, mesh handles east-west
- Discuss BFF pattern as specialization of API Gateway

### BFF (Backend for Frontend) Pattern

**Purpose**: Dedicated backend per client type (mobile, web, IoT, third-party API) to optimize for each client's unique requirements.

```
Mobile App → Mobile BFF → [auth, simple responses, reduced payload]
Web App    → Web BFF    → [auth, HTML SSR, larger responses]
IoT Device → IoT BFF    → [auth, binary protocols, reduced endpoints]
```

**Key decisions:**
- **Granularity**: One BFF per client type vs per feature domain
- **Co-location**: BFF in same deployment as gateway vs separate
- **Data shaping**: GraphQL ideal for BFF (field selection, reduced over-fetching)
- **Authentication**: BFF handles session management, token refresh, CSRF protection

**Trade-offs:**
- **Pro**: Optimized client experience, independent deployment, client-specific features
- **Con**: Duplication across BFFs, more services to maintain, inconsistent APIs

**Interview tips:**
- Discuss when NOT to use BFF (small team, simple API, single client type)
- Mention GraphQL as alternative that eliminates need for BFF in some cases
- Staff+ should discuss BFF decomposition boundary decisions

### Event Mesh

**Purpose**: Decoupled asynchronous communication between services through a distributed event broker fabric.

```
Service A → [Publish Event] → Event Mesh → [Subscribe] → Service B
                                          → [Subscribe] → Service C
                                          → [Subscribe] → Service D
```

**Key decisions:**
- **Broker**: Kafka (durable, ordered), RabbitMQ (routing, flexibility), Pulsar (multi-tenant, geo-replication)
- **Topology**: Centralized hub, distributed mesh, hybrid
- **Schema management**: Schema Registry, Avro/Protobuf, schema evolution compatibility
- **Partitioning**: Key-based, round-robin, custom partitioner

**Interview tips:**
- Distinguish between event mesh (logical) and message broker (physical)
- Discuss Kafka vs Pulsar trade-offs: Kafka excels at ordered processing, Pulsar at multi-tenant and geo-replication
- Staff+ should discuss event sourcing + event mesh combination for audit trails

### Message Broker

**Purpose**: Reliable message delivery between services with delivery guarantees.

**Key characteristics:**
- **Delivery guarantees**: At-most-once, at-least-once, exactly-once (via idempotency)
- **Ordering**: Partition-key ordering (Kafka), global ordering (rare, expensive)
- **Retention**: Time-based, size-based, compacted topics
- **Consumer groups**: Horizontal scaling of consumers, rebalancing strategies

**When to choose:**
- **Kafka**: High throughput, replay, ordered processing, event sourcing, stream processing
- **RabbitMQ**: Flexible routing, low latency, AMQP protocol, task queues
- **Pulsar**: Multi-tenant, geo-replication, tiered storage, low latency

### Service Mesh

**Purpose**: Dedicated infrastructure layer for handling service-to-service communication, offloading concerns from application code to a proxy sidecar.

```
Service A ↔ [Envoy Sidecar] ↔ Service Mesh Control Plane ↔ [Envoy Sidecar] ↔ Service B
                               ↑                           ↓
                           Discovery, mTLS, Observability, Traffic Management
```

**Key components:**
- **Data plane**: Envoy, Linkerd-proxy, NGINX — handles traffic, mTLS, load balancing
- **Control plane**: Istiod, Linkerd-controller — manages configuration, certificate issuance
- **Features**: Circuit breaking, retry, timeout, fault injection, traffic splitting, observability

**Trade-offs:**
- **Pro**: Consistent cross-cutting concerns, language-agnostic, enhanced observability
- **Con**: Complexity, latency overhead (1-3ms per hop), resource consumption, debugging difficulty

**Interview tips:**
- Discuss sidecar vs node-proxy vs CNI approaches
- When NOT to use service mesh (small deployment, low traffic, simple architecture)
- Staff+ should discuss eBPF as alternative to sidecar proxies

---

## 2. Decomposition Patterns

### Strangler Fig Pattern

**Purpose**: Incrementally migrate a monolithic application to microservices by gradually replacing specific functionality.

```
Phase 1: Monolith ──→ [Feature Toggle] ──→ New Service (parallel run)
Phase 2: Route traffic to new service, monitor
Phase 3: Remove old code from monolith
Phase 4: Repeat for next feature
```

**Implementation strategies:**
- **Branch by abstraction**: Create abstraction layer, implement both old and new, switch traffic
- **Feature toggles**: Decouple deployment from release, gradual rollout
- **Database strangling**: Shared database → database view/materialization → independent DB
- **API gateway routing**: Route specific endpoints to new services, rest to monolith

**Key decisions:**
- **Decomposition boundary**: Identify bounded contexts (DDD) for natural split points
- **Data ownership**: Which service owns which data; how to split shared data
- **Integration**: Synchronous (REST/gRPC) vs asynchronous (events) between monolith and new services
- **Rollback strategy**: Feature flags enable instant rollback without deployment

**Interview tips:**
- Staff+ should discuss strangling the monolith at the database layer (most challenging part)
- Discuss "inverse strangler" when you need to consolidate services back into monolith
- Mention Strangler Fig vs Parallel Run vs Toggle strategies

### Domain Events

**Purpose**: Events that capture something meaningful that happened in the domain, used for cross-service communication.

**Structure:**
```json
{
  "eventId": "uuid",
  "eventType": "OrderPlaced",
  "aggregateId": "order-123",
  "aggregateType": "Order",
  "version": 1,
  "timestamp": "2024-01-01T00:00:00Z",
  "data": {
    "orderId": "ord-456",
    "customerId": "cust-789",
    "total": 99.99,
    "items": ["item-1", "item-2"]
  }
}
```

**Key considerations:**
- **Idempotency**: Event processors must handle duplicate events
- **Ordering**: Events from same aggregate must be processed in order
- **Versioning**: Event schema evolves; backward/forward compatibility
- **Integration**: Domain events <→ Integration events (anti-corruption layer)

### Bounded Context

**Purpose**: Explicit boundary within which a particular domain model applies and is consistent.

**Bounded Context Relationships:**
- **Partnership**: Two contexts collaborate on shared goals
- **Shared kernel**: Shared subset of model between contexts
- **Customer-supplier**: Upstream context feeds downstream
- **Conformist**: Downstream conforms to upstream model
- **Anti-corruption layer**: Translation between contexts
- **Open-host service**: Well-defined protocol for integration
- **Separate ways**: No integration needed

**Interview tips:**
- Use bounded contexts to justify microservice boundaries
- Discuss how bounded contexts map to teams (Conway's Law)
- Staff+ should discuss context mapping in an interview design

### Aggregate

**Purpose**: Cluster of domain objects that can be treated as a single unit with a consistency boundary.

**Aggregate design rules:**
- **Consistency boundary**: All invariants must be satisfied within the aggregate
- **Root entity**: Only aggregate root has global identity; external references go through root
- **Transaction scope**: One aggregate = one transaction
- **Size**: Keep aggregates small; big aggregates cause contention
- **References**: Reference other aggregates by identity, not by object reference

**Interview tips:**
- Discuss aggregate design when designing data models in system design
- Common mistake: making aggregates too large (database-like modeling)
- Staff+ should discuss how aggregates map to event-sourced streams

---

## 3. Database Patterns

### CQRS (Command Query Responsibility Segregation)

**Purpose**: Separate read and write models to optimize for different access patterns.

```
Command Model (Writes)                  Query Model (Reads)
┌─────────────────────┐                 ┌─────────────────────┐
│ Commands → Handlers │                 │ Queries → Handlers  │
│ → Domain Logic      │                 │ → Read Models       │
│ → Write Database    │                 │ → Read Database     │
└─────────┬───────────┘                 └──────────┬──────────┘
          │                                        │
          └────────────── Sync ───────────────────┘
                     (Eventual consistency)
```

**When to use:**
- High read/write ratio disparity
- Different read and write data shapes
- Complex domain logic on writes, simple queries on reads
- Team specialization (write-side team, read-side team)

**When NOT to use:**
- Simple CRUD operations
- Strong consistency requirements
- Single database is sufficient

**Implementation approaches:**
- **Same database**: Different tables/indexes for read/write (easy but limited)
- **Separate databases**: Write DB (normalized) + Read DB (denormalized)
- **Event-sourced**: Write side as event store, read side as projections

**Interview tips:**
- Staff+ must discuss eventual consistency implications and how to handle stale reads
- Discuss CQRS vs database read replicas (different optimizations)
- Mention CQRS with event sourcing as a powerful combination

### Event Sourcing

**Purpose**: Store state changes as a sequence of events; current state is derived by replaying events.

```
Events Stream:
OrderCreated → OrderConfirmed → OrderShipped → OrderDelivered

Current State (replayed from events):
Order{status: DELIVERED, items: [...], total: 99.99}
```

**Key benefits:**
- Complete audit trail (every state change is recorded)
- Temporal query (what was the state at any point in time?)
- Event-driven architecture enabler (events can be consumed by multiple systems)
- Debugging (replay events to reproduce state)

**Challenges:**
- Event schema evolution (handling new events, deprecated events)
- Performance (replaying entire event stream — use snapshots)
- Storage (events never deleted, storage grows indefinitely)
- Consistency (reading uncommitted events, handling concurrent writes)

**Event store options:**
- **Dedicated**: EventStoreDB, Axon Server
- **Database**: PostgreSQL (events table), Kafka (topic as event log)

### Saga Pattern

**Purpose**: Manage distributed transactions across multiple services with compensating actions on failure.

**Choreography (Event-driven):**
```
Order Service → OrderCreated → Payment Service → PaymentProcessed → Shipping Service → Shipped
                  ✗ Payment Failed → Compensating Event → OrderCancelled
```

**Orchestration (Command-driven):**
```
                                → Payment Service
Orchestrator → OrderCreated →   → Inventory Service → CompleteOrder
                                → Shipping Service
                    On Failure → CancelOrder (compensation)
```

**Key decisions:**
- **Choreography vs Orchestration**: Decentralized (events) vs centralized (orchestrator)
- **Compensation**: Idempotent, reversible operations
- **Isolation**: Handling concurrent sagas (semantic locking, commutative updates)
- **Recovery**: Saga log, retries, manual intervention for unrecoverable failures

**Interview tips:**
- Staff+ must discuss saga failure modes and recovery strategies
- Discuss saga vs two-phase commit (XA) — sagas for long-running transactions
- Mention "saga as an aggregate" pattern for state management

### Outbox Pattern

**Purpose**: Ensure reliable message delivery by storing messages in the same database transaction as the business operation.

```
┌──────────────────────────────┐
│  Service Operation           │
│  1. Update business entity   │  Same transaction
│  2. Insert into outbox table │
└──────────┬───────────────────┘
           │
           ▼
   Outbox Poller / CDC
           │
           ▼
   Message Broker (Kafka/RabbitMQ)
           │
           ▼
   Downstream Services
```

**Implementation:**
- **Transactional outbox**: Write events to outbox table in same DB transaction
- **Message relay**: Poll outbox table or use CDC (Debezium)
- **Idempotency**: Each event has unique ID; consumers deduplicate

**CDC (Change Data Capture) variant:**
- Use Debezium/Kafka Connect to capture changes from database WAL
- No application-level outbox table needed
- Captures ALL changes, not just explicit business events

### Transactional Outbox

**Purpose**: Guarantee at-least-once delivery of messages by atomically persisting the message with the state change.

**Key considerations:**
- **Polling vs CDC**: Polling is simpler but can be slow; CDC captures instantly
- **Idempotency key**: Each outbox message has a unique ID for deduplication
- **Ordering**: Messages from same aggregate must maintain order
- **Cleanup**: Delete processed outbox records or use partitioned table

**Interview tips:**
- Frequently asked in distributed transaction questions
- Discuss outbox as alternative to distributed transactions
- Staff+ should discuss outbox + saga combination

---

## 4. Resilience Patterns

### Circuit Breaker

**Purpose**: Prevent cascading failures by failing fast when a downstream service is unhealthy.

**States:**
```
CLOSED (normal)
  → failures exceed threshold → OPEN
OPEN (failing fast)
  → timeout expires → HALF-OPEN
HALF-OPEN (trial request)
  → success → CLOSED
  → failure → OPEN
```

**Implementation:**
```
@CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
public PaymentResult processPayment(PaymentRequest request) {
    return paymentClient.charge(request);
}

public PaymentResult paymentFallback(PaymentRequest request, Throwable t) {
    return PaymentResult.failed("Payment service unavailable, please retry");
}
```

**Configuration:**
- **Failure threshold**: 50% failure rate over 100 calls (Resilience4J default)
- **Wait duration**: 60s in open state before half-open
- **Half-open call count**: 3 trial requests
- **Record exceptions vs ignore exceptions**

**Interview tips:**
- Discuss how to determine threshold and timeout values (based on SLOs)
- Staff+ should discuss circuit breaker at different layers (network, application, database)
- Mention circuit breaker + retry pattern — careful with timing (don't retry on open circuit)

### Bulkhead Pattern

**Purpose**: Isolate failures by partitioning resources into separate pools, preventing one failing component from taking down the entire system.

```
Bulkhead Types:
1. Thread pool isolation → Each service/operation gets its own thread pool
2. Semaphore isolation → Limited concurrent calls per service
3. Connection pool isolation → Separate connection pools per downstream
```

**Thread pool isolation example (Resilience4J):**
```yaml
resilience4j.bulkhead:
  configs:
    default:
      max-concurrent-calls: 10
      max-wait-duration: 500ms
  instances:
    paymentService:
      max-concurrent-calls: 5
      max-wait-duration: 1000ms
```

**When to use:**
- Critical vs non-critical downstream services
- Different SLAs for different operations
- Preventing noisy neighbors in multi-tenant systems

**Interview tips:**
- Discuss thread pool vs semaphore trade-offs (thread pool for async, semaphore for sync)
- Staff+ should discuss bulkhead at architectural level (separate clusters, shards, cells)

### Retry Pattern

**Purpose**: Transparently retry failed operations due to transient failures.

**Strategies:**
- **Immediate retry**: Single retry immediately
- **Fixed interval**: Retry every N seconds
- **Exponential backoff**: Retry with increasing intervals (1s, 2s, 4s, 8s...)
- **Jitter**: Add randomness to backoff to avoid thundering herd

```java
@Retry(name = "paymentService", fallbackMethod = "fallback")
public PaymentResult processPayment(PaymentRequest request) {
    // ...
}
```

**Key considerations:**
- **Idempotency**: Retries must be safe (use idempotency keys)
- **Max retries**: Limit retries to avoid prolonged failures
- **Circuit breaker**: Combine retry with circuit breaker (retry on closed, fail fast on open)

### Timeout Pattern

**Purpose**: Prevent a service from waiting indefinitely for a response.

**Timeout types:**
- **Connection timeout**: Time to establish connection (default: 500ms)
- **Read timeout**: Time to receive response after connection (default: 1000ms)
- **Write timeout**: Time to send request (default: 500ms)

**Configuration guidelines:**
- Set timeouts based on P99 latency + buffer
- Avoid identical timeouts across all services (cascading timeouts)
- Make timeouts configurable and SLA-aware

### Fallback Pattern

**Purpose**: Provide alternative behavior when a service call fails or returns an error.

**Fallback types:**
- **Cache fallback**: Serve stale cached data when service is unavailable
- **Default response**: Return sensible defaults
- **Degraded functionality**: Disable non-essential features
- **Alternative service**: Route to a secondary provider

```java
@CircuitBreaker(name = "productService", fallbackMethod = "getCachedProducts")
public List<Product> getProducts(String category) {
    return productClient.getProducts(category);
}

public List<Product> getCachedProducts(String category, Throwable t) {
    return cache.get(category, Collections.emptyList());
}
```

### Health Endpoint Pattern

**Purpose**: Expose service health for monitoring, load balancers, and orchestration platforms.

**Health check types:**
- **Liveness**: Is the application running? (pod restart indicator)
- **Readiness**: Is the application ready to serve traffic? (load balancer removal)
- **Startup**: Has the application completed initialization? (delayed probe)

```json
// GET /health
{
  "status": "UP",
  "components": {
    "database": { "status": "UP", "details": { "latency": "2ms" } },
    "kafka": { "status": "UP", "details": { "connectedSince": "2024-01-01T00:00:00Z" } },
    "diskSpace": { "status": "UP", "details": { "free": "10GB", "total": "100GB" } }
  }
}
```

---

## 5. Observability

### Distributed Tracing

**Purpose**: Trace requests across multiple services to understand end-to-end latency and identify bottlenecks.

**Trace structure:**
```
Trace ID: abc-123-def-456
  Span 1: API Gateway (10ms)
    Span 2: Auth Service (5ms)
    Span 3: Order Service (50ms)
      Span 4: Database Query (30ms)
      Span 5: Payment Service (200ms)
        Span 6: External Provider (180ms)
```

**Implementation:**
- **Instrumentation**: OpenTelemetry (vendor-neutral), Jaeger, Zipkin
- **Context propagation**: W3C Trace Context headers (`traceparent`, `tracestate`)
- **Sampling**: Head-based (consistent), tail-based (selective), probability sampling

**Key metrics from tracing:**
- **Service dependency graph**: Understand service topology
- **Latency breakdown**: Which service/operation contributes most to latency
- **Error propagation**: Which errors affect downstream services
- **Saturation**: Which services are approaching capacity

**Interview tips:**
- Staff+ must discuss sampling strategies (100% at low scale, probabilistic at high scale)
- Discuss OpenTelemetry as the unified standard replacing OpenTracing and OpenCensus
- Mention trace context propagation through message queues (a common challenge)

### Structured Logging

**Purpose**: Emit machine-parseable logs with consistent structure for aggregation, filtering, and alerting.

**Log format:**
```json
{
  "timestamp": "2024-01-01T00:00:00.123Z",
  "level": "WARN",
  "logger": "com.app.payment.PaymentService",
  "traceId": "abc-123-def-456",
  "spanId": "span-789",
  "message": "Payment retry attempt 2 of 3",
  "context": {
    "paymentId": "pay-456",
    "amount": 99.99,
    "currency": "USD",
    "attempt": 2,
    "maxRetries": 3
  },
  "error": {
    "type": "TimeoutException",
    "message": "Connection timed out after 500ms",
    "stackTrace": "com.app.payment.PaymentClient.charge(PaymentClient.java:42)"
  }
}
```

**Best practices:**
- **Correlation IDs**: Include trace ID in every log entry
- **Structured format**: JSON or key-value pairs, not free text
- **Log levels**: ERROR (action required), WARN (attention), INFO (normal), DEBUG (diagnostic)
- **Context**: Include relevant business context (entity ID, operation, duration)

### Metrics Aggregation

**Purpose**: Collect, aggregate, and analyze system metrics for monitoring and alerting.

**Metric types (the 4 golden signals):**
1. **Latency**: Time to service requests (P50, P95, P99, P999)
2. **Traffic**: Request rate (QPS/RPS), active connections
3. **Errors**: Error rate, error types (client 4xx, server 5xx, business errors)
4. **Saturation**: Resource utilization (CPU, memory, disk, connections)

**USE method (resources):**
- **Utilization**: Average time resource was busy
- **Saturation**: Degree of extra work resource can't handle
- **Errors**: Count of error events

**RED method (services):**
- **Rate**: Requests per second
- **Errors**: Failed requests per second
- **Duration**: Distribution of request latency

**Interview tips:**
- Discuss Prometheus (pull) vs Graphite/InfluxDB (push) trade-offs
- Staff+ should discuss cardinality explosion and how to manage metric labels
- Mention observability for event-driven systems (event throughput, consumer lag)

---

## 6. Security Patterns

### Zero Trust Architecture

**Purpose**: No implicit trust; verify every request regardless of network location.

**Principles:**
- **Verify explicitly**: Authenticate and authorize every request
- **Least privilege**: Minimum required access per identity
- **Assume breach**: Segment access, encrypt everything, monitor continuously

**Implementation:**
- **Identity**: Every request must have authenticated identity (user, service, device)
- **Device**: Device posture check before access
- **Network**: Micro-segmentation, encrypted tunnels
- **Application**: Application-level authorization (not just network-level)
- **Data**: Encryption at rest and in transit; data classification

### API Gateway with Auth

**Authentication flow:**
```
Client → Request → API Gateway
  → [Auth Plugin] → Validate JWT / Session Token
  → [Rate Limiter] → Per-client, per-endpoint quotas
  → [Routing] → Forward to service with identity context
```

**Auth strategies:**
- **JWT**: Stateless, contains claims, verify signature
- **OAuth2**: Authorization framework, token exchange (authorization code, client credentials)
- **API Keys**: Simple, good for programmatic access, rotate periodically
- **mTLS**: Mutual TLS certificate authentication (service-to-service)

**Gateway auth responsibilities:**
- Token validation (signature, expiration, issuer, audience)
- Token exchange (service-to-service tokens)
- Session management (cookie-based sessions)
- Rate limiting by authenticated identity

### Service Mesh mTLS

**Purpose**: Encrypt and authenticate all service-to-service traffic transparently.

```
Service A → [Envoy Sidecar] → TLS Connection → [Envoy Sidecar] → Service B
              ↑ Mutual Auth (certificate verification)
              ↑ Encrypted traffic (mTLS)
```

**mTLS flow:**
1. Service A's sidecar receives request
2. Sidecar initiates TLS handshake with Service B's sidecar
3. Both sides present certificates (mutual authentication)
4. Encrypted tunnel established
5. Request forwarded to Service B

**Certificate management:**
- **Issuer**: Istio Citadel, cert-manager, Vault
- **Rotation**: Automated, short-lived certificates (24h)
- **Spiffe**: SPIFFE IDs for workload identity (spiffe://cluster.local/ns/default/sa/payment-sa)

**Interview tips:**
- Discuss mTLS overhead (handshake latency, CPU for encryption)
- Staff+ should discuss mTLS vs application-level encryption trade-offs
- Mention service mesh integration with external certificate authorities

---

## 7. Deployment Patterns

### Blue-Green Deployment

**Purpose**: Zero-downtime deployment by maintaining two identical environments and switching traffic.

```
┌──────────────┐     Load      ┌──────────────────┐
│              │    Balancer    │                  │
│  Blue (v1)   │ ←─────────── → │   Green (v2)    │
│  [live]      │               │   [staging]      │
│              │               │                  │
└──────────────┘               └──────────────────┘
        ↓
Traffic switch to Green
        ↓
┌──────────────┐               ┌──────────────────┐
│  Blue (v1)   │               │   Green (v2)      │
│  [standby]   │               │   [live]          │
└──────────────┘               └──────────────────┘
```

**Key considerations:**
- **Database**: Schema changes must be backward compatible (additive only)
- **Traffic switch**: Gradual (10%, 50%, 100%) vs instant (risky)
- **Rollback**: Instant — switch traffic back to Blue
- **Cost**: Double infrastructure during deployment

### Canary Deployment

**Purpose**: Gradually roll out changes to a subset of users to validate before full rollout.

```
v1 (stable) → 90% of traffic
v2 (canary) → 10% of traffic
  → Monitor: errors, latency, business metrics
  → If healthy → increase to 25%, then 50%, then 100%
  → If degraded → rollback
```

**Canary strategies:**
- **Random**: X% of all requests
- **Geographic**: Specific region (e.g., US West first)
- **User segment**: Internal users, beta users, free tier users
- **Cookie-based**: Specific users based on cookie value

**Interview tips:**
- Discuss how to detect canary degradation (statistical significance, minimum sample size)
- Staff+ should discuss canary for stateful services (database changes, schema migrations)
- Mention canary vs A/B testing (canary = deployment strategy, A/B = feature experiment)

### Feature Flags

**Purpose**: Decouple deployment from release; control feature availability at runtime.

```java
if (featureFlagService.isEnabled("new-checkout-flow", userId)) {
    return newCheckoutFlow(order);
} else {
    return legacyCheckoutFlow(order);
}
```

**Use cases:**
- Gradual rollout
- Kill switch for problematic features
- A/B testing variants
- Per-user/per-tenant feature targeting
- Operational toggles (maintenance mode, throttling mode)

**Flag management:**
- **Static flags**: Compile-time, hard to change
- **Dynamic flags**: Runtime, centralized in flag management service (LaunchDarkly, Split)
- **Flag evaluation**: Server-side (secure, but request-latency concern) vs client-side (fast, but exposed)

### Dark Launch

**Purpose**: Deploy a feature to production without exposing it to users; validate in production-like environment.

**Approach:**
- Deploy the service
- Route real traffic to it ("dark" — response is discarded or compared)
- Monitor for errors, performance, correctness
- When confident, activate for real users

**Dark launch techniques:**
- **Traffic mirroring**: Copy (shadow) requests to new service, discard response
- **Replay**: Record real traffic, replay against new service
- **Comparison**: Run old and new in parallel, compare results (diff testing)

### A/B Testing

**Purpose**: Compare two versions of a feature with real users to determine which performs better.

**Statistical framework:**
- **Randomization**: Users randomly assigned to control (A) or treatment (B)
- **Metrics**: Primary (conversion, revenue), secondary (engagement, satisfaction), guardrail (latency, errors)
- **Sample size**: Calculate minimum sample for statistical significance
- **Duration**: Minimum 1 week (to capture weekly cycles)
- **Analysis**: Hypothesis testing, confidence intervals, segment analysis

**Interview tips:**
- Discuss A/B testing infrastructure (assignment service, metric pipeline, analysis tool)
- Staff+ should discuss A/B testing at scale (network effects, interference between experiments)

---

## 8. Communication Patterns

### Synchronous Communication

| Pattern | Protocol | Use Case | Latency | Coupling |
|---------|----------|----------|---------|----------|
| REST | HTTP/1.1, HTTP/2 | CRUD, simple queries | Moderate | Tight |
| gRPC | HTTP/2, Protocol Buffers | High-performance, streaming | Low | Tight |
| GraphQL | HTTP/1.1, HTTP/2 | Flexible queries, BFF | Moderate | Loose |
| WebSocket | WS, WSS | Real-time bidirectional | Low | Tight |

### Asynchronous Communication

| Pattern | Technology | Use Case | Delivery | Ordering |
|---------|-----------|----------|----------|----------|
| Event notification | Kafka, RabbitMQ | Fire-and-forget | At-least-once | Per partition |
| Event-carried state transfer | Kafka | State replication | At-least-once | Per partition |
| Command message | RabbitMQ, SQS | Task execution | At-most-once | Optional |
| Request-Reply | Kafka reply topic | Async request-response | At-least-once | Correlated |

### Idempotency Key Pattern

**Purpose**: Ensure operations can be safely retried without unintended side effects.

```json
POST /api/payments
Headers: Idempotency-Key: uuid-abc-123
Body: { "amount": 99.99, "currency": "USD" }

Response 200: { "status": "completed", "id": "pay-123" }

// Retry with same idempotency key
POST /api/payments
Headers: Idempotency-Key: uuid-abc-123  // Same key
Body: { "amount": 99.99, "currency": "USD" }

Response 200: { "status": "completed", "id": "pay-123" }  // Same response
```

**Key design:**
- Client generates UUID per operation
- Server stores key and response
- If key exists, return stored response without executing
- TTL for key storage (e.g., 24 hours)
- Include key versioning for different operation types

---

## 9. Data Flow Patterns

### Change Data Capture (CDC)

**Purpose**: Capture database changes as events for downstream consumption.

```
Database WAL → Debezium → Kafka → [Stream Processing]
                              → [Search Index Update]
                              → [Cache Invalidation]
                              → [Data Sync to other systems]
```

**CDC approaches:**
- **Log-based**: Read database transaction log (PostgreSQL WAL, MySQL binlog)
- **Polling-based**: Query `updated_at` column, detect changes
- **Trigger-based**: Database triggers write to change table

**Interview tips:**
- CDC vs outbox: CDC captures ALL changes (including system-level), outbox captures business events
- CDC enables data integration without application changes

### Event Carried State Transfer

**Purpose**: Transfer complete state in the event so consumers don't need to query the source.

```json
{
  "eventType": "OrderShipped",
  "orderId": "ord-456",
  "state": {                          // Full state included
    "status": "SHIPPED",
    "shippingAddress": {
      "street": "123 Main St",
      "city": "Springfield",
      "zip": "12345"
    },
    "items": [
      { "id": "item-1", "name": "Widget", "qty": 2 }
    ],
    "shippingMethod": "EXPRESS",
    "trackingNumber": "TRACK-789"
  }
}
```

**Trade-offs:**
- **Pro**: Consumers don't need to call source service; reduced coupling
- **Con**: Larger event payloads; state may be stale; data duplication

---

## 10. Architecture Decision Framework

### For Each Decision, Cover

1. **Context**: What problem are you solving?
2. **Options**: What alternatives did you consider?
3. **Decision**: Which option did you choose?
4. **Rationale**: Why this option over others?
5. **Consequences**: What trade-offs did you accept?
6. **Compliance**: How will you verify the decision was correct?

### Common Trade-Off Questions

| Trade-Off | Decision Factors |
|-----------|-----------------|
| Consistency vs Availability | Business requirements (financial vs social) |
| Monolith vs Microservices | Team size, product maturity, scale |
| Synchronous vs Async | Real-time needs, fault tolerance tolerance |
| SQL vs NoSQL | Data relationships, query patterns, scale |
| Event-driven vs Request-driven | Coupling tolerance, audit needs |
| Self-hosted vs Managed | Control vs operational overhead |
| Stateful vs Stateless | Session requirements, scalability needs |
| gRPC vs REST | Performance needs, browser clients, streaming |
| Kafka vs Pulsar | Multi-tenancy, geo-replication, throughput |
| Cache vs No Cache | Read/write ratio, staleness tolerance |

---

*This cheatsheet is designed for quick reference during system design interview preparation. Combine with per-company guides for targeted practice.*
