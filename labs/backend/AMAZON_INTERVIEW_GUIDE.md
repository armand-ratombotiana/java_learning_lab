# Amazon Interview Guide — Backend Academy

## Interview Process for Backend Roles

Amazon's backend interview process typically spans 4-6 weeks with 5 rounds:

1. **Phone Screen (60 min)** — Technical screening with a backend engineer covering data structures, algorithms, and one system design question. Expect a live-coding session on a shared document.
2. **Coding Round 1 (45 min)** — Algorithmic problem solving with focus on scalability. Amazon often asks about arrays, strings, and trees with a backend twist (e.g., processing log files, merging intervals).
3. **Coding Round 2 (45 min)** — Object-oriented design or API design problem. May involve designing a class hierarchy for a payment system or a parking lot.
4. **System Design Round (60 min)** — Design a large-scale backend system. Amazon focuses on data modeling, consistency models, and cost optimization.
5. **Bar Raiser (60 min)** — Senior engineer or manager evaluates across all dimensions: coding, system design, leadership, and Amazon Leadership Principles.

Amazon's backend interviews heavily emphasize the Leadership Principles (LPs). Every answer should tie back to one or more LPs: Customer Obsession, Ownership, Invent and Simplify, Are Right, A Lot, Learn and Be Curious, Insist on the Highest Standards, Think Big, Bias for Action, Frugality, Earn Trust, Dive Deep, Have Backbone, Deliver Results.

## Top Problems by Backend Topic

### Topic: Scalable Web Services & API Design

#### Problem: Design a Shopping Cart Service
- **LC/System Design ref**: System Design: E-commerce Cart
- **Problem statement**: Design the backend for a shopping cart that supports add/remove/update items, persists across sessions, handles concurrent updates from multiple devices, and calculates real-time pricing with discounts.
- **Interview walkthrough**: Start with data model: cart_id, user_id, items (JSONB or separate table), created_at, updated_at. Discuss optimistic locking for concurrent cart updates. For real-time pricing, use a pricing service that caches product prices and applies discount rules. Discuss idempotency keys for add-to-cart to prevent duplicate charges. Talk about cart expiration for abandoned carts.
- **Solution**: Use a Cart service with a REST API. Store cart in PostgreSQL with a JSONB column for items (flexible schema). Use @Version for optimistic concurrency control. For pricing, use a separate PricingService that applies rules engine. For abandoned carts, use a scheduled job that marks carts as EXPIRED after 7 days. Use Redis for session-to-cart mapping.
- **Java/Spring code**:
```java
@Entity
public class Cart {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    private UUID userId;
    @Convert(converter = CartItemsConverter.class)
    @Column(columnDefinition = "JSONB")
    private List<CartItem> items;
    @Version
    private long version;
    private CartStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}

@Service
@Transactional
public class CartService {
    public Cart addItem(UUID cartId, CartItem newItem, String idempotencyKey) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException(cartId));
        if (idempotencyService.wasProcessed(idempotencyKey)) {
            return cart;
        }
        cart.getItems().add(newItem);
        cart.setUpdatedAt(Instant.now());
        Cart saved = cartRepository.save(cart);
        idempotencyService.markProcessed(idempotencyKey);
        return saved;
    }
}
```

- **What Amazon evaluates**: Idempotency, optimistic concurrency, data modeling flexibility, and API design.
- **Follow-ups**: How would you handle flash sales? How would you implement coupon stacking? How would you design for multi-currency?

#### Problem: Design a Payment Processing API
- **LC/System Design ref**: System Design: Payment System
- **Problem statement**: Design a REST API for processing payments that supports multiple payment methods, handles idempotency, and provides idempotent retry with exactly-once semantics.
- **Interview walkthrough**: Discuss the payment flow: authorize, capture, refund, void. For idempotency, require an idempotency key header. Store idempotency keys in a database with a unique constraint and TTL. Use two-phase commit for payment gateway calls or implement a saga pattern. Discuss PCI-DSS compliance considerations (tokenization, never store raw card numbers).
- **Solution**: REST API with idempotency key support. Use a transactional outbox pattern: write payment event to outbox table in the same transaction as the payment record. A separate relay publishes to the payment gateway. Use a state machine for payment status (PENDING, AUTHORIZED, CAPTURED, REFUNDED, FAILED). For idempotency, use a composite key (idempotency_key, merchant_id) with upsert semantics.
- **Java/Spring code**:
```java
@PostMapping("/payments")
public ResponseEntity<PaymentResponse> createPayment(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody @Valid PaymentRequest request) {
    Payment existing = paymentService.findByIdempotencyKey(idempotencyKey);
    if (existing != null) {
        return ResponseEntity.ok(PaymentResponse.from(existing));
    }
    Payment payment = paymentService.processPayment(request, idempotencyKey);
    return ResponseEntity.status(CREATED).body(PaymentResponse.from(payment));
}

@Service
public class PaymentService {
    @Transactional
    public Payment processPayment(PaymentRequest request, String idempotencyKey) {
        Payment payment = new Payment();
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setIdempotencyKey(idempotencyKey);
        payment.setStatus(PaymentStatus.PENDING);
        payment = paymentRepository.save(payment);

        outboxRepository.save(new OutboxEvent(
            payment.getId(), "PaymentCreated", payment.toJson()));
        return payment;
    }
}
```

- **What Amazon evaluates**: Idempotency, transactional integrity, API design for financial systems, and error handling.
- **Follow-ups**: How would you handle partial refunds? How would you detect duplicate payments? How would you design for multi-currency?

### Topic: Database & Data Modeling

#### Problem: Design a Product Catalog with Hierarchical Categories
- **LC/System Design ref**: System Design: E-commerce Database
- **Problem statement**: Design the database schema for a product catalog with hierarchical categories (up to 10 levels deep), where each product can belong to multiple categories and categories can have variable attributes.
- **Interview walkthrough**: Discuss the nested set model vs adjacency list vs materialized path for hierarchical categories. For PostgreSQL, use a recursive CTE with adjacency list. For attributes, use EAV (entity-attribute-value) or JSONB. Discuss trade-offs: EAV is flexible but query-heavy; JSONB is simpler but harder to index deeply. For Amazon scale, consider denormalizing category paths into the product table.
- **Solution**: Use adjacency list with a recursive CTE for category trees. Store product attributes as JSONB with a GIN index. For the category-product relationship, use a junction table with a composite index. For performance, cache the category tree in Redis and invalidate on category changes.
- **Java/Spring code**:
```java
@Query(value = """
    WITH RECURSIVE category_tree AS (
        SELECT id, name, parent_id, 0 AS depth
        FROM categories WHERE id = :rootId
        UNION ALL
        SELECT c.id, c.name, c.parent_id, ct.depth + 1
        FROM categories c
        INNER JOIN category_tree ct ON c.parent_id = ct.id
    )
    SELECT * FROM category_tree ORDER BY depth
    """, nativeQuery = true)
List<Category> findCategoryTree(@Param("rootId") UUID rootId);
```

- **What Amazon evaluates**: SQL proficiency, understanding of hierarchical data, indexing strategy, and trade-off analysis.
- **Follow-ups**: How would you handle multi-language attribute values? How would you implement category-based access control? How would you migrate from EAV to JSONB?

### Topic: Microservices & Service Decomposition

#### Problem: Decompose a Monolithic E-commerce Platform
- **LC/System Design ref**: System Design: Microservices Decomposition
- **Problem statement**: A monolithic e-commerce platform needs to be decomposed into microservices. Identify bounded contexts, define service boundaries, and design inter-service communication.
- **Interview walkthrough**: Use Domain-Driven Design to identify bounded contexts: Product Catalog, Order Management, Payment, Shipping, User, Recommendation. Discuss communication patterns: synchronous (REST/ gRPC) for real-time queries, asynchronous (Kafka/ SQS) for event-driven updates. Discuss the strangler fig pattern for incremental migration. Talk about shared data ownership and eventual consistency challenges.
- **Solution**: Decompose into 6 services. Use API Gateway (Spring Cloud Gateway) for routing. Synchronous calls for read operations (product details, user info). Asynchronous events for write operations (order placed -> payment -> shipping). Use Kafka for event streaming with Avro schema registry. Each service owns its database. Implement saga pattern for distributed transactions (order creation flow).
- **Java/Spring code**:
```java
@Service
public class OrderSagaOrchestrator {
    private final KafkaTemplate<String, OrderEvent> kafka;

    @Transactional
    public void createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        kafka.send("order-events", new OrderCreatedEvent(order.getId(), order.getUserId(), order.getTotal()));
    }

    @KafkaListener(topics = "payment-events")
    public void onPaymentResult(PaymentEvent event) {
        Order order = orderRepository.findById(event.orderId())
            .orElseThrow();
        if (event.success()) {
            order.setStatus(OrderStatus.CONFIRMED);
            kafka.send("order-events", new OrderConfirmedEvent(order.getId()));
        } else {
            order.setStatus(OrderStatus.FAILED);
            kafka.send("order-events", new OrderFailedEvent(order.getId(), event.reason()));
        }
        orderRepository.save(order);
    }
}
```

- **What Amazon evaluates**: Service decomposition, event-driven architecture, saga patterns, and API gateway design.
- **Follow-ups**: How would you handle service discovery? How would you manage schema evolution in event-driven communication? How would you implement canary deployments?

### Topic: Concurrency & Async Processing

#### Problem: Design an Async Order Processing Pipeline
- **LC/System Design ref**: System Design: Order Processing
- **Problem statement**: Design an asynchronous order processing pipeline that handles order validation, payment processing, inventory reservation, and shipping notification with guaranteed processing and dead-letter handling.
- **Interview walkthrough**: Discuss the choreography vs orchestration debate. For Amazon, orchestration with Step Functions or a saga orchestrator is preferred for complex workflows. Use SQS/Kafka for reliable message delivery. Discuss retry strategies with exponential backoff, dead-letter queues, and idempotent consumers. Talk about ordering guarantees — use a partition key (order_id) to keep related events in order.
- **Solution**: Use a saga orchestrator pattern. The orchestrator service receives order requests, publishes an OrderCreated event. A validation service consumes and validates, then publishes OrderValidated or OrderValidationFailed. The orchestrator listens and proceeds to payment, then inventory, then shipping. Each step has a compensating action for rollback. Use Kafka with compacted topics for state store.
- **Java/Spring code**:
```java
@Service
public class OrderOrchestrator {
    private final KafkaTemplate<String, OrderEvent> kafka;

    @Transactional
    public void startOrderSaga(CreateOrder command) {
        OrderSaga saga = new OrderSaga(command.orderId(), command.userId());
        saga.start();
        kafka.send("order-saga", command.orderId(), new OrderCreatedEvent(command));
    }

    @KafkaListener(topics = "order-saga")
    public void handleEvent(OrderEvent event) {
        SagaState state = sagaStateRepository.findById(event.sagaId()).orElseThrow();
        switch (event.type()) {
            case ORDER_CREATED -> kafka.send("payment-saga", new ProcessPaymentEvent(event));
            case PAYMENT_SUCCESS -> kafka.send("inventory-saga", new ReserveInventoryEvent(event));
            case PAYMENT_FAILED -> compensateOrder(event);
            case INVENTORY_RESERVED -> kafka.send("shipping-saga", new CreateShipmentEvent(event));
            case INVENTORY_FAILED -> compensatePayment(event);
        }
    }
}
```

- **What Amazon evaluates**: Async processing patterns, message reliability, idempotency, and error handling strategies.
- **Follow-ups**: How would you handle out-of-order messages? How would you implement exactly-once semantics? How would you monitor consumer lag?

### Topic: System Design — Storage & Databases

#### Problem: Design a Distributed Key-Value Store
- **LC/System Design ref**: System Design: Distributed Key-Value Store (DynamoDB-like)
- **Problem statement**: Design a distributed key-value store that is highly available, partition-tolerant, and provides configurable consistency levels.
- **Interview walkthrough**: Discuss CAP theorem — Amazon Dynamo chose AP (availability + partition tolerance) with eventual consistency. Talk about consistent hashing for partitioning, vector clocks for conflict resolution, and gossip protocol for membership. Discuss read repair, hinted handoff, and Merkle trees for anti-entropy. For consistency, support quorum-based reads/writes (ONE, QUORUM, ALL).
- **Solution**: Use consistent hashing with virtual nodes for even distribution. Each key is replicated to N nodes (configurable). Read and write operations use quorum: R + W > N for strong consistency. Use vector clocks to detect conflicts; on read, return all conflicting versions and let the application resolve. Use gossip protocol for node discovery and failure detection. Implement hinted handoff for availability during network partitions.
- **Java/Spring code**:
```java
public class ConsistentHashRouter {
    private final TreeMap<Integer, Node> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashRouter(List<Node> nodes, int virtualNodes) {
        this.virtualNodes = virtualNodes;
        for (Node node : nodes) {
            for (int i = 0; i < virtualNodes; i++) {
                int hash = hash(node.id() + ":" + i);
                ring.put(hash, node);
            }
        }
    }

    public Node getNode(String key) {
        int hash = hash(key);
        Map.Entry<Integer, Node> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry();
        return entry.getValue();
    }

    private int hash(String key) {
        return Hashing.murmur3_32().hashString(key, StandardCharsets.UTF_8).asInt();
    }
}
```

- **What Amazon evaluates**: Understanding of distributed systems theory, CAP theorem, consistency models, and Dynamo-style design.
- **Follow-ups**: How would you handle network partitions? How would you add security (encryption at rest, in transit)? How would you implement multi-region replication?

### Topic: Security & Authentication

#### Problem: Design a Secure API Gateway with Authentication
- **LC/System Design ref**: System Design: API Gateway
- **Problem statement**: Design an API gateway that authenticates requests, enforces rate limits, validates JWT tokens, and routes to backend services. The gateway must handle 100K requests/second.
- **Interview walkthrough**: Discuss JWT vs session-based auth. For JWT, validate signature, check expiry, extract claims, and forward to downstream services. For rate limiting, use a Redis-backed token bucket. Discuss TLS termination, request validation, and logging. Talk about gateway deployment — stateless, horizontally scalable behind a load balancer.
- **Solution**: Spring Cloud Gateway with a custom filter chain. JWT validation filter extracts and validates the token, populates security context. Rate limiting filter uses Redis token bucket. Routing filter forwards to downstream services based on path. Use Spring Security OAuth2 resource server for JWT validation. For high availability, deploy multiple gateway instances behind a load balancer.
- **Java/Spring code**:
```java
@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("product-service", r -> r.path("/api/products/**")
                .filters(f -> f
                    .requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter()))
                    .tokenRelay())
                .uri("lb://product-service"))
            .route("order-service", r -> r.path("/api/orders/**")
                .filters(f -> f.circuitBreaker(config -> config
                    .setName("orderServiceCB")
                    .setFallbackUri("forward:/fallback/orders")))
                .uri("lb://order-service"))
            .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(100, 200, 1);
    }
}
```

- **What Amazon evaluates**: Security-first mindset, understanding of OAuth2/JWT, rate limiting at scale, and gateway patterns.
- **Follow-ups**: How would you handle API key rotation? How would you implement mutual TLS? How would you design for GDPR data access requests?

### Topic: System Design — Large Scale

#### Design Amazon's Product Detail Page
- **Requirements**: Serve product details for 500M products with 99.99% availability, <100ms latency, handling 10M QPS.
- **Framework**: Discuss caching strategy: CDN for static assets, Redis for product metadata, local cache for hot products. Use a microservice architecture: Product Service, Pricing Service, Inventory Service, Review Service, Recommendation Service. Each service owns its data store. Use a BFF (Backend For Frontend) pattern to aggregate responses. For the product page, use parallel calls to all services with a timeout. Discuss database sharding by product_id.
- **Key trade-offs**: Cache freshness vs latency. Strong consistency for inventory vs eventual consistency for reviews. Cost of caching cold products vs cache hit ratio.
- **Amazon-specific**: Emphasize frugality — optimize cache costs. Discuss DynamoDB for product data, S3 for images, CloudFront for CDN. Talk about the "two-pizza team" structure for service ownership.

### Design Amazon's Recommendation Engine
- **Requirements**: Serve personalized product recommendations for 300M+ customers with <50ms latency, updating as users browse.
- **Framework**: Discuss offline vs online recommendation pipelines. Offline: batch processing with Spark/MapReduce to compute collaborative filtering (item-item, user-item) and store results in a key-value store. Online: real-time co-visitation matrix using streaming (Kinesis/Flink) to capture user behavior within a session. Use a hybrid approach: pre-computed recommendations for cold start, real-time updates for active sessions.
- **Key trade-offs**: Computation cost vs recommendation freshness. Diversity vs relevance. Exploration vs exploitation.
- **Amazon-specific**: Discuss Amazon's item-to-item collaborative filtering (published paper). Talk about personalization at scale using DynamoDB and ElastiCache. Mention the use of machine learning models for ranking.

### Design Amazon's Order Fulfillment System
- **Requirements**: Process millions of orders/day, assign to fulfillment centers, optimize shipping costs, provide real-time tracking.
- **Framework**: Discuss the order lifecycle: placed -> payment authorized -> inventory reserved -> fulfillment assigned -> picked -> packed -> shipped -> delivered. Use a workflow engine (or saga orchestrator) to manage state transitions. For inventory, use a distributed inventory service with optimistic locking. For fulfillment optimization, use a constraint solver to assign orders to fulfillment centers based on inventory, distance, and shipping cost.
- **Key trade-offs**: Speed vs cost in shipping. Inventory accuracy vs availability. Real-time tracking vs batch updates.
- **Amazon-specific**: Discuss Amazon's fulfillment center robotics, the use of Kiva robots, and how the backend coordinates with warehouse systems. Mention the use of DynamoDB for session state and S3 for order documents.

## Behavioral Questions

### Tell me about a time you delivered a feature under a tight deadline
- **Situation**: Our team was asked to build a same-day delivery checkout flow in 3 weeks for a major holiday sales event. The normal timeline was 8 weeks.
- **Task**: Deliver a production-ready feature that handles inventory availability, delivery slot selection, and order placement with 99.9% uptime.
- **Action**: Prioritized the MVP scope — cut advanced features like subscription scheduling and gift options. Used feature flags to deploy incrementally. Leveraged existing services (inventory, shipping) instead of building new ones. Wrote integration tests for critical paths. Coordinated with QA for parallel testing. Worked 3-day sprints with daily demos to stakeholders.
- **Result**: Launched on time with zero critical bugs. The feature generated $2M in revenue in the first month. Post-launch, we iterated on the cut features based on user feedback.
- **What Amazon evaluates**: Bias for Action, Deliver Results, Customer Obsession, and ability to prioritize under pressure.

### Tell me about a time you disagreed with your manager
- **Situation**: My manager wanted to use MongoDB for a new order management system because of faster development velocity. I believed PostgreSQL was a better fit due to the relational nature of order data and the need for transactional integrity.
- **Task**: Convince my manager without being confrontational, and find a solution that addressed both concerns.
- **Action**: Built a small proof-of-concept comparing both databases for our specific access patterns. Showed that PostgreSQL with JSONB columns could match MongoDB's flexibility while providing ACID transactions and better join performance. Proposed a hybrid approach: PostgreSQL for transactional data, MongoDB for session storage and analytics events. Presented data on operational costs, team expertise, and long-term maintenance.
- **Result**: The team adopted PostgreSQL for the core platform. The hybrid approach saved an estimated $50K/year in infrastructure costs. My manager appreciated the data-driven approach.
- **What Amazon evaluates**: Dive Deep, Have Backbone, Invent and Simplify, Frugality.

### Tell me about a time you invented a simple solution to a complex problem
- **Situation**: Our deployment pipeline required 12 manual steps, took 2 hours, and had a 15% failure rate. The team was afraid to deploy on Fridays.
- **Task**: Automate the deployment pipeline to reduce manual effort and increase deployment frequency.
- **Action**: Built a CI/CD pipeline using GitHub Actions, Docker, and a custom canary deployment script. Automated database migrations with Flyway. Added smoke tests and canary analysis (5% traffic for 10 minutes). Created a one-click rollback mechanism using previous Docker image tags. Documented the runbook and trained the team.
- **Result**: Deployment time reduced from 2 hours to 8 minutes. Deployment frequency increased from weekly to multiple times per day. Failure rate dropped from 15% to <1%.
- **What Amazon evaluates**: Invent and Simplify, Bias for Action, Deliver Results, Learn and Be Curious.

## Study Plan

Priority labs for Amazon backend interviews:
1. **Spring Boot** — Foundation for all backend work
2. **REST APIs** — API design is critical at Amazon
3. **Security** — Authentication, authorization, PCI compliance
4. **Messaging** — SQS/Kafka patterns for async processing
5. **Spring Cloud** — Service discovery, configuration, gateway
6. **Transactions** — ACID, distributed transactions, sagas
7. **Caching** — Multi-layer caching, Redis, CDN
8. **Performance** — Profiling, connection pooling, JVM tuning
9. **API Versioning** — Backward compatibility, deprecation strategies
10. **CQRS/Axon** — Event sourcing for audit trails

## Tips

- Amazon interviews are heavily LP-based. Prepare 2-3 STAR stories per leadership principle. Use the STAR method religiously.
- For system design, emphasize cost-consciousness. Amazon values "Frugality" — mention cost trade-offs in every design.
- Know DynamoDB deeply: partition keys, sort keys, GSIs, LSIs, adaptive capacity, and the 1MB query limit.
- Be ready to discuss "what would you do differently" — Amazon wants to see that you learn from mistakes.
- For coding rounds, optimize for readability and correctness first, then optimize. Amazon interviewers value clean, maintainable code.
- The "Bar Raiser" round is a separate evaluation — the interviewer does not have a hiring stake and evaluates you against the overall bar. Be prepared for a wildcard question.
- Understand Amazon's build vs buy philosophy. Be ready to discuss when to use a managed service vs building in-house.
- For system design, always discuss monitoring, alarming, and dashboards. Amazon uses CloudWatch extensively.
- Know the difference between DynamoDB's adaptive capacity, on-demand vs provisioned mode, and when to use each.

### Topic: Caching & Performance Optimization

#### Problem: Design a Product Recommendation Cache
- **LC/System Design ref**: System Design: Recommendation Cache
- **Problem statement**: Design a caching layer for product recommendations that serves personalized recommendations with <50ms latency, handles 100K QPS, and updates within 5 minutes of a user action.
- **Interview walkthrough**: Discuss the cache hierarchy: L1 (local Caffeine cache for hot users), L2 (Redis cluster for all users), L3 (database for cache misses). For personalization, cache user-specific recommendation lists keyed by userId. For invalidation, use a publish-subscribe pattern: when a user makes a purchase, publish an event that invalidates their recommendation cache. For cache warming, pre-compute recommendations for active users via a background job.
- **Solution**: Use a three-tier cache. L1: Caffeine cache per pod with 10K entries and 5-minute TTL. L2: Redis cluster with sharding by userId, 1-hour TTL. L3: PostgreSQL with materialized views for recommendation scores. A background job pre-warms the cache for users with high activity scores. Cache-aside pattern: on miss, compute recommendations and store in Redis.
- **Java/Spring code**:
```java
@Component
public class RecommendationCache {
    private final Cache<UUID, List<Recommendation>> localCache;
    private final StringRedisTemplate redis;
    private final RecommendationService recommendationService;

    public RecommendationCache() {
        this.localCache = Caffeine.newBuilder()
            .maximumSize(50_000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();
    }

    public List<Recommendation> getRecommendations(UUID userId) {
        List<Recommendation> local = localCache.getIfPresent(userId);
        if (local != null) return local;

        String key = "recs:" + userId;
        String cached = redis.opsForValue().get(key);
        if (cached != null) {
            List<Recommendation> recs = deserialize(cached);
            localCache.put(userId, recs);
            return recs;
        }

        List<Recommendation> recs = recommendationService.compute(userId);
        redis.opsForValue().set(key, serialize(recs), Duration.ofHours(1));
        localCache.put(userId, recs);
        return recs;
    }
}
```

- **What Amazon evaluates**: Multi-layer caching, cache invalidation, and performance optimization at scale.
- **Follow-ups**: How would you handle cache stampede for popular products? How would you implement write-through caching for inventory? How would you handle cache warming for new products?

### Topic: Security & Compliance

#### Problem: Design a PCI-DSS Compliant Payment Storage
- **LC/System Design ref**: System Design: Secure Data Storage
- **Problem statement**: Design a storage system for payment data that is PCI-DSS compliant. The system must store encrypted card data, support tokenization, and provide audit trails for all access.
- **Interview walkthrough**: Discuss PCI-DSS requirements: encrypt cardholder data at rest and in transit, restrict access on a need-to-know basis, maintain audit logs, and regularly test security. For tokenization, replace PAN with a random token. For encryption, use AES-256 with key rotation. For access control, use IAM roles with fine-grained permissions. For audit, log all access to sensitive data.
- **Solution**: Use a tokenization service. On ingest, encrypt the PAN with AES-256-GCM using a key from AWS KMS, store the encrypted PAN and a random token. Downstream services use the token. Only authorized services can detokenize. All detokenization requests are logged. Use a hardware security module (HSM) or cloud KMS for key management. Implement key rotation with versioned keys.
- **Java/Spring code**:
```java
@Service
public class TokenizationService {
    private final EncryptionService encryptionService;
    private final TokenRepository tokenRepository;
    private final AuditLogger auditLogger;

    @Transactional
    public TokenResponse tokenize(String pan, String merchantId) {
        if (!LuhnValidator.isValid(pan)) {
            throw new InvalidPanException("Invalid PAN");
        }
        String token = "tok_" + UUID.randomUUID().toString().replace("-", "");
        String encryptedPan = encryptionService.encrypt(pan);
        tokenRepository.save(new Token(token, encryptedPan, merchantId));
        return new TokenResponse(token, maskPan(pan));
    }

    @Transactional
    public String detokenize(String token, String requestorId) {
        TokenRecord record = tokenRepository.findById(token)
            .orElseThrow(() -> new TokenNotFoundException("Token not found"));
        auditLogger.logAccess(token, requestorId);
        return encryptionService.decrypt(record.getEncryptedPan());
    }
}
```

- **What Amazon evaluates**: Security-first design, PCI compliance, encryption key management, and audit logging.
- **Follow-ups**: How would you handle key rotation? How would you implement token vault replication across regions? How would you design for PCI SAQ compliance?

### Topic: Event-Driven Architecture

#### Problem: Design an Order Event Sourcing System
- **LC/System Design ref**: System Design: Event Sourcing
- **Problem statement**: Design an event-sourced order management system that records every state change as an immutable event, supports replay for audit, and provides current state via projection.
- **Interview walkthrough**: Discuss event sourcing vs traditional CRUD. Events are immutable facts: OrderCreated, OrderShipped, OrderDelivered, OrderCancelled. The current state is derived by replaying events. For querying, use a projection (materialized view) that is updated by event handlers. Discuss snapshotting to avoid replaying all events from the beginning. Discuss event versioning for schema evolution.
- **Solution**: Use an event store table: `events(aggregate_id, event_type, event_data, version, created_at)`. Each event is appended. A projection service reads events and updates a materialized view (current order state). For snapshots, periodically save the aggregate state to avoid replaying all events. Use Kafka for event publishing to downstream services.
- **Java/Spring code**:
```java
@Entity
@Table(name = "order_events")
public class OrderEvent {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    private UUID orderId;
    private String eventType;
    @Convert(converter = JsonConverter.class)
    @Column(columnDefinition = "JSONB")
    private Map<String, Object> eventData;
    private int version;
    private Instant createdAt;
}

@Service
public class OrderEventSourcingService {
    private final OrderEventRepository eventRepository;
    private final OrderProjectionRepository projectionRepository;

    @Transactional
    public void applyEvent(UUID orderId, OrderEvent event) {
        eventRepository.save(event);
        OrderProjection projection = projectionRepository.findById(orderId)
            .orElse(new OrderProjection(orderId));
        applyToProjection(projection, event);
        projectionRepository.save(projection);
    }

    public OrderProjection rebuildProjection(UUID orderId) {
        List<OrderEvent> events = eventRepository.findByOrderIdOrderByVersion(orderId);
        OrderProjection projection = new OrderProjection(orderId);
        for (OrderEvent event : events) {
            applyToProjection(projection, event);
        }
        return projection;
    }
}
```

- **What Amazon evaluates**: Event sourcing, CQRS, auditability, and data integrity.
- **Follow-ups**: How would you handle event schema evolution? How would you implement snapshotting? How would you handle event ordering across partitions?

### Topic: System Design — Additional Questions

#### Design Amazon's Inventory Management System
- **Requirements**: Track inventory across 100+ fulfillment centers, support real-time availability queries, handle 1M+ SKUs, and prevent overselling.
- **Framework**: Discuss the inventory service architecture: a central inventory service that maintains stock levels per SKU per fulfillment center. Use optimistic locking for inventory updates. For real-time availability, use Redis with inventory counts. For overselling prevention, use a two-phase reservation: reserve inventory when added to cart, confirm on payment, release on timeout. Discuss the reconciliation process for inventory drift.
- **Key trade-offs**: Real-time accuracy vs throughput. Reservation timeout duration vs user experience. Global inventory vs per-fulfillment-center inventory.
- **Amazon-specific**: Discuss Amazon's use of DynamoDB for inventory, the concept of "inventory health" dashboards, and the reconciliation process that runs nightly to correct drift.

#### Design Amazon's Recommendation Engine
- **Requirements**: Serve personalized product recommendations for 300M+ customers with <50ms latency, updating as users browse.
- **Framework**: Discuss offline vs online recommendation pipelines. Offline: batch processing with Spark/MapReduce to compute collaborative filtering (item-item, user-item) and store results in a key-value store. Online: real-time co-visitation matrix using streaming (Kinesis/Flink) to capture user behavior within a session. Use a hybrid approach: pre-computed recommendations for cold start, real-time updates for active sessions.
- **Key trade-offs**: Computation cost vs recommendation freshness. Diversity vs relevance. Exploration vs exploitation.
- **Amazon-specific**: Discuss Amazon's item-to-item collaborative filtering (published paper). Talk about personalization at scale using DynamoDB and ElastiCache. Mention the use of machine learning models for ranking.

### Behavioral Questions — Additional

#### Tell me about a time you had to learn a new domain quickly
- **Situation**: I was asked to lead the integration with a new payment processor that used a completely different API paradigm (SOAP/XML vs our REST/JSON stack).
- **Task**: Integrate the new processor within 4 weeks while maintaining our existing API contracts.
- **Action**: Created an adapter layer that translated our internal payment model to the processor's SOAP format. Used a facade pattern to abstract the processor differences. Wrote integration tests with WireMock to simulate the processor. Documented the integration patterns for future processor integrations.
- **Result**: Integration was completed in 3 weeks. The adapter pattern was reused for 3 subsequent processor integrations. The team estimated 60% time savings on each new integration.
- **What Amazon evaluates**: Adaptability, integration patterns, and pragmatic engineering.

#### Tell me about a time you improved a process
- **Situation**: Our on-call rotation had a 30-minute mean time to acknowledge (MTTA) because alerts were noisy and went to a shared email alias.
- **Task**: Reduce MTTA to under 5 minutes and reduce alert fatigue.
- **Action**: Implemented a tiered alerting system: P1 (page within 5 min), P2 (email within 30 min), P3 (dashboard). Added alert deduplication and grouping. Created runbooks for common alerts. Integrated with PagerDuty for on-call scheduling. Set up a weekly alert review to tune thresholds.
- **Result**: MTTA dropped from 30 minutes to 3 minutes. Alert volume reduced by 60%. On-call engineer satisfaction improved significantly.
- **What Amazon evaluates**: Operational excellence, process improvement, and systematic problem solving.

## Study Plan — Expanded

Priority labs for Amazon backend interviews:
1. **Spring Boot** — Foundation for all backend work
2. **REST APIs** — API design is critical at Amazon
3. **Security** — Authentication, authorization, PCI compliance
4. **Messaging** — SQS/Kafka patterns for async processing
5. **Spring Cloud** — Service discovery, configuration, gateway
6. **Transactions** — ACID, distributed transactions, sagas
7. **Caching** — Multi-layer caching, Redis, CDN
8. **Performance** — Profiling, connection pooling, JVM tuning
9. **API Versioning** — Backward compatibility, deprecation strategies
10. **CQRS/Axon** — Event sourcing for audit trails
11. **Testing** — Integration, contract, and chaos testing
12. **Security Deep** — Cryptography, PCI compliance, IAM

## Tips — Additional

- Amazon's leadership principles are not just for behavioral questions. Weave them into your system design answers too. For example, "I would use DynamoDB for this because it's highly available (Customer Obsession) and cost-effective at scale (Frugality)."
- Practice the "Amazon interview loop" — 5 back-to-back 45-minute interviews. Stamina matters.
- Know the difference between SQS (queue), SNS (pub/sub), and EventBridge (event bus) and when to use each.
- For system design, always discuss how you would handle traffic spikes (Prime Day, Black Friday). Amazon expects you to design for peak load.
- Be ready to discuss the "two-pizza team" concept and how it influences service boundaries.
- Amazon's "Working Backwards" process: start with the press release and FAQ, then work backwards to the architecture. Show this thinking in system design.
- Practice the "6-pager" writing style — Amazon uses narrative documents instead of slide decks. Be concise and structured in your explanations.
- Know the difference between DynamoDB's eventual consistent reads and strongly consistent reads, and when to use each.
- For system design, always discuss how you would handle a region failure. Amazon's infrastructure is designed for multi-region resilience.
