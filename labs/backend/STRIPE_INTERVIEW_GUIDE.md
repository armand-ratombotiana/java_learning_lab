# Stripe Interview Guide — Backend Academy

## Interview Process for Backend Roles

Stripe's interview process typically spans 4-5 weeks with 5-6 rounds:

1. **Phone Screen (45 min)** — Technical screen with a backend engineer. Covers programming, system design fundamentals, and a discussion of past projects.
2. **Coding Round 1 (45 min)** — Algorithms and data structures. Stripe focuses on practical problems: parsing, API design, and working with time-series or financial data. Expect Java, Python, or Go.
3. **Coding Round 2 (45 min)** — Second coding round, often with a focus on concurrency, distributed systems, or API design. Stripe values clean, testable code.
4. **System Design Round (60 min)** — Design a distributed system. Stripe focuses on reliability, consistency, and operational excellence. Examples: design a payment system, a ledger, or a fraud detection pipeline.
5. **API Design Round (45 min)** — Stripe is unique in having a dedicated API design round. You'll design a REST API or SDK, focusing on developer experience, consistency, and extensibility.
6. **Behavioral / Cross-functional (45 min)** — Focus on Stripe's values: Think about the User, Move with Urgency, Care about the Details, Question Assumptions, Seek Diverse Perspectives.

Stripe's interview process is known for being rigorous but respectful. They value clear communication, attention to detail, and a deep understanding of the developer experience.

## Top Problems by Backend Topic

### Topic: API Design & Developer Experience

#### Problem: Design a REST API for a Payment Processing System
- **LC/System Design ref**: System Design: Payment API (Stripe-like)
- **Problem statement**: Design a REST API for processing payments that supports multiple payment methods, idempotent retries, webhooks, and paginated listing of transactions. The API must be intuitive and consistent.
- **Interview walkthrough**: Discuss resource-oriented design: PaymentIntent, PaymentMethod, Refund, Charge. Use idempotency keys for safe retries. For webhooks, use a signature header (HMAC-SHA256) for verification. For pagination, use cursor-based with `starting_after` and `ending_before`. Discuss error handling: return structured errors with types, codes, and params. For expansion, support `?expand[]=customer` to include related resources.
- **Solution**: RESTful API with consistent naming conventions (snake_case for fields, plural nouns for resources). Use idempotency keys stored in Redis with TTL. For webhooks, sign payloads with HMAC-SHA256 and support retries with exponential backoff. For pagination, use cursor-based with `has_more` flag. For expansion, use a parameter that specifies which related objects to include inline.
- **Java/Spring code**:
```java
@RestController
@RequestMapping("/v1/charges")
public class ChargeController {
    @PostMapping
    public ResponseEntity<Charge> createCharge(
            @RequestBody @Valid CreateChargeRequest request,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        Charge charge = chargeService.create(request, idempotencyKey);
        return ResponseEntity.status(201).body(charge);
    }

    @GetMapping
    public ResponseEntity<ChargeList> listCharges(
            @RequestParam(required = false) String startingAfter,
            @RequestParam(required = false) String endingBefore,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String customer) {
        ChargeList charges = chargeService.list(startingAfter, endingBefore, limit, customer);
        return ResponseEntity.ok(charges);
    }
}

@Service
public class ChargeService {
    @Transactional
    public Charge create(CreateChargeRequest request, String idempotencyKey) {
        if (idempotencyService.wasProcessed(idempotencyKey)) {
            return idempotencyService.getPreviousResponse(idempotencyKey);
        }
        Charge charge = new Charge();
        charge.setAmount(request.amount());
        charge.setCurrency(request.currency());
        charge.setSource(request.source());
        charge.setIdempotencyKey(idempotencyKey);
        charge.setStatus(ChargeStatus.PENDING);
        charge = chargeRepository.save(charge);
        idempotencyService.markProcessed(idempotencyKey, charge);
        kafka.send("charge-events", new ChargeCreatedEvent(charge.getId()));
        return charge;
    }
}
```

- **What Stripe evaluates**: API design sensibility, idempotency, error handling, and developer experience.
- **Follow-ups**: How would you design a webhook delivery system? How would you handle API versioning? How would you design an SDK for this API?

#### Problem: Design a Webhook Delivery System
- **LC/System Design ref**: System Design: Webhook Delivery
- **Problem statement**: Design a reliable webhook delivery system that delivers events to thousands of merchant endpoints with at-least-once guarantees, retries with exponential backoff, and a dashboard for delivery monitoring.
- **Interview walkthrough**: Discuss the webhook lifecycle: event occurs -> webhook is queued -> delivery attempted -> success logged or retry scheduled. For reliability, use a database-backed queue with status tracking. For retries, use exponential backoff with jitter (e.g., 10s, 30s, 2min, 10min, 30min, 1h, 6h). After max retries, mark as failed and alert. For signature verification, sign payloads with HMAC-SHA256 using a per-merchant secret. For idempotency, include an `Idempotency-Key` header in webhook requests.
- **Solution**: Use a webhook delivery service with a PostgreSQL queue. Each webhook endpoint has a configuration (URL, secret, events subscribed). On event, create a WebhookDelivery record. A scheduler picks up pending deliveries and sends HTTP POST requests. Track delivery status, response code, and latency. Implement exponential backoff with jitter. Provide a dashboard for merchants to view delivery logs and manually retry.
- **Java/Spring code**:
```java
@Service
public class WebhookDeliveryService {
    private final WebhookDeliveryRepository deliveryRepository;
    private final RestClient restClient;

    @Async
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void deliver(WebhookDelivery delivery) {
        String signature = computeHmac(delivery.getPayload(), delivery.getSecret());
        try {
            ResponseEntity<String> response = restClient.post()
                .uri(delivery.getEndpointUrl())
                .header("Stripe-Signature", signature)
                .body(delivery.getPayload())
                .retrieve()
                .toEntity(String.class);
            delivery.setStatus(DeliveryStatus.DELIVERED);
            delivery.setHttpStatus(response.getStatusCodeValue());
        } catch (HttpClientErrorException e) {
            delivery.setStatus(DeliveryStatus.FAILED);
            delivery.setError(e.getMessage());
            if (e.getStatusCode().is4xxClientError()) {
                delivery.setPermanentFailure(true); // don't retry 4xx
            }
        }
        deliveryRepository.save(delivery);
    }

    @Scheduled(fixedRate = 60000)
    public void retryFailedDeliveries() {
        List<WebhookDelivery> failed = deliveryRepository
            .findByStatusAndRetriesLessThan(DeliveryStatus.FAILED, 5);
        for (WebhookDelivery delivery : failed) {
            delivery.setRetries(delivery.getRetries() + 1);
            deliveryRepository.save(delivery);
            deliverAsync(delivery);
        }
    }
}
```

- **What Stripe evaluates**: Reliability, retry strategies, webhook security, and developer dashboard design.
- **Follow-ups**: How would you handle webhook endpoint throttling? How would you support webhook filtering (only send certain events)? How would you implement a webhook test mode?

### Topic: Distributed Transactions & Consistency

#### Problem: Design a Double-Entry Accounting Ledger
- **LC/System Design ref**: System Design: Accounting System
- **Problem statement**: Design a double-entry accounting ledger that records every financial transaction with debits and credits, supports balance queries, and provides audit trails. The system must never lose a transaction and must be auditable.
- **Interview walkthrough**: Discuss double-entry accounting principles: every transaction has a debit and a credit, total debits must equal total credits. Use a ledger table with columns: id, account_id, entry_type (DEBIT/CREDIT), amount, currency, description, created_at. For balance queries, use a materialized view or a running balance table updated via triggers. For audit, make all records immutable — use append-only logs. For high throughput, batch writes and use partitioning by account_id.
- **Solution**: Use an append-only ledger table. Each transaction is a row with a unique ID, account ID, entry type, amount, and running balance (computed at write time). Use PostgreSQL SERIALIZABLE isolation for balance transfers. For high-throughput accounts, use a separate balance table updated asynchronously with reconciliation. For audit, every entry has a unique ID and references the previous entry (blockchain-like chain).
- **Java/Spring code**:
```java
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    private UUID accountId;
    private UUID previousEntryId;
    @Enumerated(STRING)
    private EntryType entryType; // DEBIT, CREDIT
    private BigDecimal amount;
    private String currency;
    private BigDecimal runningBalance;
    private String description;
    private Instant createdAt;
}

@Service
public class LedgerService {
    @Transactional(isolation = SERIALIZABLE)
    public LedgerEntry recordEntry(UUID accountId, BigDecimal amount, String currency, String description) {
        Account account = accountRepository.findByIdForUpdate(accountId);
        LedgerEntry lastEntry = ledgerRepository.findTopByAccountIdOrderByCreatedAtDesc(accountId);
        BigDecimal newBalance = account.getBalance().add(amount);
        LedgerEntry entry = new LedgerEntry();
        entry.setAccountId(accountId);
        entry.setPreviousEntryId(lastEntry != null ? lastEntry.getId() : null);
        entry.setAmount(amount);
        entry.setRunningBalance(newBalance);
        entry.setDescription(description);
        entry = ledgerRepository.save(entry);
        account.setBalance(newBalance);
        accountRepository.save(account);
        return entry;
    }
}
```

- **What Stripe evaluates**: Financial system design, data integrity, auditability, and understanding of double-entry accounting.
- **Follow-ups**: How would you handle multi-currency ledgers? How would you implement real-time balance reporting? How would you detect and prevent double-spending?

### Topic: Distributed Systems & Reliability

#### Problem: Design a Distributed Lock Service
- **LC/System Design ref**: System Design: Distributed Lock
- **Problem statement**: Design a distributed lock service that provides mutual exclusion across services with automatic release on failure (lease-based), fairness, and deadlock detection.
- **Interview walkthrough**: Discuss Redis Redlock vs ZooKeeper vs etcd for distributed locking. For Stripe's use case, strong consistency is critical — prefer etcd or ZooKeeper over Redis. Discuss lease mechanism: lock is acquired with a TTL, the holder must renew periodically. If the holder crashes, the lock is automatically released after TTL. Discuss fencing tokens to prevent stale locks from causing data corruption.
- **Solution**: Use etcd for distributed locking with leases. Each lock acquisition creates a lease with a TTL. The holder must keep the lease alive via keepalive. Include a fencing token (monotonically increasing) in the lock response. The protected resource checks the fencing token and rejects operations with stale tokens. For high contention, use a sharded lock approach.
- **Java/Spring code**:
```java
@Component
public class DistributedLockService {
    private final EtcdClient etcdClient;

    public Lock acquire(String lockKey, Duration ttl) {
        String lockPath = "/locks/" + lockKey;
        String sessionId = UUID.randomUUID().toString();
        long leaseId = etcdClient.createLease(ttl);
        try {
            etcdClient.put(lockPath, sessionId, leaseId, PutOption.WithPrevKV());
            return new Lock(lockKey, sessionId, leaseId);
        } catch (EtcdException e) {
            etcdClient.revokeLease(leaseId);
            throw new LockAcquisitionException("Failed to acquire lock: " + lockKey);
        }
    }

    public void release(Lock lock) {
        String lockPath = "/locks/" + lock.key();
        etcdClient.delete(lockPath, DeleteOption.withPrevValue(lock.sessionId()));
        etcdClient.revokeLease(lock.leaseId());
    }
}
```

- **What Stripe evaluates**: Distributed consensus, lease management, fencing tokens, and failure handling.
- **Follow-ups**: How would you handle split-brain scenarios? How would you implement a read-write lock? How would you detect and recover from deadlocks?

#### Problem: Design a Global Payment Routing System
- **LC/System Design ref**: System Design: Payment Routing
- **Problem statement**: Design a system that routes each payment to the optimal payment processor based on cost, success rate, latency, and geographic region. Handle failover and A/B testing of processors.
- **Interview walkthrough**: Discuss the routing decision factors: processor cost per transaction, historical success rate (by card type, region, amount), latency, and current availability. Use a weighted random selection based on a score computed from these factors. For failover, use a circuit breaker pattern per processor. For A/B testing, support traffic splitting by percentage. Discuss how to collect and aggregate success metrics in real-time.
- **Solution**: Use a routing service that maintains a real-time view of processor health. Each processor has a score computed from weighted factors. Use a weighted random selection for routing. Circuit breakers trip when error rate exceeds threshold. Metrics are collected via a sidecar that monitors each processor response. Use a time-series database for historical success rate computation.
- **Java/Spring code**:
```java
@Service
public class PaymentRouter {
    private final List<PaymentProcessor> processors;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public PaymentResult route(Payment payment) {
        List<ProcessorScore> scored = processors.stream()
            .map(p -> new ProcessorScore(p, score(p, payment)))
            .sorted(Comparator.comparing(ProcessorScore::score).reversed())
            .toList();

        for (ProcessorScore ps : scored) {
            CircuitBreaker cb = circuitBreakerFactory.create(ps.processor().getName());
            try {
                return cb.run(() -> ps.processor().charge(payment), t -> fallback(payment));
            } catch (Exception e) {
                log.warn("Processor {} failed: {}", ps.processor().getName(), e.getMessage());
            }
        }
        throw new AllProcessorsFailedException("No available processor for payment");
    }

    private double score(PaymentProcessor processor, Payment payment) {
        double costScore = 1.0 / processor.cost(payment.amount(), payment.currency());
        double successScore = processor.successRate(payment.region());
        double latencyScore = 1.0 / (processor.avgLatencyMs(payment.region()) + 1);
        return costScore * 0.3 + successScore * 0.5 + latencyScore * 0.2;
    }
}
```

- **What Stripe evaluates**: Routing algorithm design, circuit breaker patterns, weighted decision making, and operational reliability.
- **Follow-ups**: How would you handle processor outages? How would you implement canary deployments for new processors? How would you detect and mitigate processor fraud?

### Topic: System Design

#### Design Stripe's Payment Processing Pipeline
- **Requirements**: Process millions of payments/day across 195+ countries, 135+ currencies, with 99.999% uptime. Handle declines, retries, 3D Secure, and fraud screening.
- **Framework**: Discuss the payment lifecycle: tokenization -> authorization -> capture -> settlement. For each step, discuss failure modes and retry strategies. For 3D Secure, discuss the redirect flow and webhook notification. For fraud, discuss integration with Stripe Radar (rule-based ML). For global payments, discuss the need for local acquiring, multi-currency settlement, and compliance with local regulations (PSD2, SCA).
- **Key trade-offs**: Authorization hold duration vs merchant cash flow. Synchronous vs async fraud screening. Local vs cross-border settlement.
- **Stripe-specific**: Discuss Stripe's use of a custom database (Vitess-based), the event sourcing system for payment state, and the webhook infrastructure. Emphasize developer experience — every API design decision should consider the developer using it.

#### Design a Fraud Detection System
- **Requirements**: Detect and block fraudulent transactions in real-time (<100ms), with <0.1% false positive rate. Handle 10M transactions/day.
- **Framework**: Discuss the two-stage approach: real-time rules engine (block known fraud patterns) and ML model (score transactions for risk). For the rules engine, use a configurable rules DSL (e.g., "if amount > $10K AND country != customer.country THEN score += 0.8"). For ML, use gradient-boosted trees or neural networks trained on historical fraud data. For real-time features, use a feature store (Redis) with features like: transaction velocity, average transaction amount, distance from last transaction.
- **Key trade-offs**: Latency vs model complexity. Recall vs precision. Real-time vs batch feature computation.
- **Stripe-specific**: Discuss Stripe Radar, the ML-based fraud prevention system. Talk about the use of signals (device fingerprint, IP geolocation, card fingerprint) and the rule engine that allows merchants to customize fraud rules.

## Behavioral Questions

### Tell me about a time you had to design for reliability
- **Situation**: Our payment API had a 0.5% error rate during peak hours due to database connection pool exhaustion. Each error meant a failed payment and a frustrated user.
- **Task**: Improve reliability to 99.99% without adding significant infrastructure cost.
- **Action**: Implemented a three-pronged approach: (1) Added connection pooling with HikariCP configured for max lifetime and leak detection. (2) Introduced a circuit breaker around the database call that falls back to a cached response for read-only queries. (3) Added a bulkhead pattern to isolate payment writes from reads. Also implemented a health check endpoint for the load balancer to drain unhealthy instances.
- **Result**: Error rate dropped to 0.01%. P99 latency improved from 2s to 150ms. The circuit breaker prevented cascading failures during a subsequent database migration.
- **What Stripe evaluates**: Reliability engineering, defense in depth, and operational excellence.

### Tell me about a time you improved a developer workflow
- **Situation**: Our API documentation was manually maintained and frequently out of sync with the implementation. Developers spent hours debugging incorrect documentation.
- **Task**: Automate API documentation generation and ensure it stays in sync with code.
- **Action**: Integrated springdoc-openapi to generate OpenAPI 3.0 specs from annotations. Added a CI step that validates the generated spec against a stored baseline and fails the build if there are breaking changes. Created a developer portal with interactive API explorer. Added examples and request/response schemas to all endpoints.
- **Result**: Documentation accuracy improved to 100%. Developer onboarding time reduced from 1 week to 1 day. The breaking change detection caught 3 accidental API changes before they reached production.
- **What Stripe evaluates**: Developer experience focus, automation mindset, and attention to detail.

## Study Plan

Priority labs for Stripe backend interviews:
1. **REST APIs** — API design is the most important skill for Stripe
2. **Security** — PCI compliance, encryption, authentication
3. **Security Deep** — Cryptography, key management, secure coding
4. **Transactions** — ACID, distributed transactions, sagas
5. **Messaging** — Kafka, event-driven architecture
6. **Caching** — Redis, distributed caching
7. **Spring Boot** — Core framework
8. **Testing** — Integration testing, contract testing
9. **Performance** — Profiling, optimization
10. **API Versioning** — Backward compatibility, deprecation

## Tips

- Stripe's interview process is unique because of the dedicated API design round. Practice designing APIs that are consistent, intuitive, and developer-friendly.
- Know Stripe's API conventions: snake_case fields, consistent error format, idempotency keys, expandable objects, cursor-based pagination.
- For system design, emphasize reliability and data integrity above all else. Stripe deals with money — every transaction must be accounted for.
- Stripe values writing. You may be asked to write a design doc as part of the interview. Practice writing clear, structured technical documents.
- Be prepared to discuss idempotency in depth: how to implement it, how to handle race conditions, and how to clean up stale idempotency keys.
- Know the payment ecosystem: card networks (Visa, Mastercard, Amex), ACH, SEPA, wire transfers, and digital wallets.
- Stripe's culture emphasizes "moving with urgency" but "caring about the details." Show that you can move fast without cutting corners.
- For behavioral questions, use the STAR format and quantify results. Stripe values impact and metrics.
- Understand the concept of "developer experience" (DX) — Stripe's product is APIs, so every design decision should consider the developer using it.

### Topic: Concurrency & Distributed Systems

#### Problem: Design a Distributed Counter Service
- **LC/System Design ref**: System Design: Distributed Counter
- **Problem statement**: Design a distributed counter service that tracks API usage per customer with high accuracy, supports increments and reads, and handles 1M+ increments/second.
- **Interview walkthrough**: Discuss the challenges of distributed counting: race conditions, network partitions, and clock skew. For Stripe's use case (API usage tracking), eventual consistency is acceptable. Use a Redis-backed counter with periodic flushing to a database. For higher accuracy, use a CRDT counter (G-Counter or PN-Counter) that can tolerate network partitions. Discuss read repair and hinted handoff for consistency.
- **Solution**: Use a Redis cluster with sharding by customer ID. Each counter is a Redis hash with fields for different API endpoints. Increments are batched and flushed to PostgreSQL every minute for durability. For cross-region, use a CRDT counter (PN-Counter) that can be merged across regions. For monitoring, track counter drift between Redis and PostgreSQL and alert on significant discrepancies.
- **Java/Spring code**:
```java
@Component
public class UsageCounterService {
    private final StringRedisTemplate redis;
    private final UsageRepository usageRepository;

    public void increment(String customerId, String apiEndpoint, long amount) {
        String key = "usage:" + customerId + ":" + apiEndpoint;
        redis.opsForValue().increment(key, amount);
        redis.expire(key, Duration.ofHours(1));
    }

    @Scheduled(fixedRate = 60000)
    public void flushCounters() {
        Set<String> keys = redis.keys("usage:*");
        for (String key : keys) {
            String value = redis.opsForValue().getAndDelete(key);
            if (value != null) {
                String[] parts = key.split(":");
                String customerId = parts[1];
                String endpoint = parts[2];
                long count = Long.parseLong(value);
                usageRepository.incrementUsage(customerId, endpoint, count);
            }
        }
    }
}
```

- **What Stripe evaluates**: Distributed counting, batching for performance, data durability, and accuracy.
- **Follow-ups**: How would you handle counter drift between Redis and the database? How would you implement multi-tenant rate limiting? How would you detect and prevent counter manipulation?

### Topic: System Design — Additional Questions

#### Design Stripe's Connect Platform (Marketplace Payments)
- **Requirements**: Support marketplace payments where a platform (e.g., Shopify) collects payments on behalf of sellers, handles split payments, and manages onboarding, compliance, and payouts.
- **Framework**: Discuss the Connect architecture: platform account, connected accounts (sellers), and the flow of funds. For split payments, use the PaymentIntent with `transfer_data` to split funds between the platform and the seller. For onboarding, use Stripe Connect's OAuth flow or API key creation. For compliance, handle KYC/AML verification for sellers. For payouts, schedule automatic or manual transfers to seller bank accounts.
- **Key trade-offs**: Platform vs direct payment flow. Immediate vs delayed payout. Standard vs express onboarding.
- **Stripe-specific**: Discuss Stripe Connect's account types (standard, express, custom), the use of `application_fee` for platform revenue, and the compliance requirements for marketplace platforms.

#### Design Stripe's Billing Platform (Subscriptions, Invoicing)
- **Requirements**: Support recurring billing with complex pricing models (flat, per-seat, usage-based, tiered), proration, coupons, dunning, and invoicing.
- **Framework**: Discuss the subscription lifecycle: creation -> trial -> active -> past_due -> canceled. For pricing, support a flexible pricing model with a rules engine. For proration, compute the difference between the old and new plan on change. For dunning, use a configurable retry schedule with smart retries (retry on different days/times). For invoicing, generate PDF invoices and send via email.
- **Key trade-offs**: Pricing flexibility vs system complexity. Real-time proration vs batch. Dunning aggressiveness vs customer retention.
- **Stripe-specific**: Discuss Stripe Billing's subscription API, the use of `subscription_schedule` for complex billing scenarios, and the dunning configuration for failed payments.

## Behavioral Questions — Additional

#### Tell me about a time you had to debug a difficult production issue
- **Situation**: A rare race condition caused duplicate charges for approximately 0.01% of payments. The issue was intermittent and hard to reproduce.
- **Task**: Find and fix the root cause without introducing new bugs.
- **Action**: Added detailed logging around the payment processing flow. Used distributed tracing to correlate events across services. Discovered that the idempotency check had a race condition: two concurrent requests with the same idempotency key could both pass the check before either was marked as processed. Fixed by using a database-level unique constraint on the idempotency key and a retry loop.
- **Result**: Duplicate charges were eliminated. The fix was verified with a chaos engineering experiment that injected duplicate requests. The team added a monitoring alert for idempotency key collisions.
- **What Stripe evaluates**: Deep debugging, understanding of race conditions, and systematic problem solving.

#### Tell me about a time you had to design for a new regulatory requirement
- **Situation**: A new regulation required that we store payment data in a specific geographic region and provide data deletion capabilities within 30 days of a user request.
- **Task**: Design a system that supports data residency and deletion without impacting existing functionality.
- **Action**: Implemented a data residency layer: each customer's data is tagged with a region, and all database queries include a region filter. For data deletion, implemented a soft-delete mechanism with a scheduled purge job. Added a compliance dashboard for customers to view and request data deletion. Wrote automated tests that verify data never leaves the designated region.
- **Result**: The system passed the compliance audit with zero findings. The data residency feature became a competitive advantage for winning enterprise customers.
- **What Stripe evaluates**: Compliance awareness, data governance, and ability to design for regulatory requirements.

## Study Plan — Expanded

Priority labs for Stripe backend interviews:
1. **REST APIs** — API design is the most important skill for Stripe
2. **Security** — PCI compliance, encryption, authentication
3. **Security Deep** — Cryptography, key management, secure coding
4. **Transactions** — ACID, distributed transactions, sagas
5. **Messaging** — Kafka, event-driven architecture
6. **Caching** — Redis, distributed caching
7. **Spring Boot** — Core framework
8. **Testing** — Integration testing, contract testing
9. **Performance** — Profiling, optimization
10. **API Versioning** — Backward compatibility, deprecation
11. **Spring Cloud** — Service discovery, configuration
12. **CQRS/Axon** — Event sourcing for audit trails
13. **Batch Processing** — Large-scale data processing
14. **Spring Boot Internals** — Deep framework understanding

## Tips — Additional

- Stripe's interview process is unique because of the dedicated API design round. Practice designing APIs that are consistent, intuitive, and developer-friendly.
- Know Stripe's API conventions: snake_case fields, consistent error format, idempotency keys, expandable objects, cursor-based pagination.
- For system design, emphasize reliability and data integrity above all else. Stripe deals with money — every transaction must be accounted for.
- Stripe values writing. You may be asked to write a design doc as part of the interview. Practice writing clear, structured technical documents.
- Be prepared to discuss idempotency in depth: how to implement it, how to handle race conditions, and how to clean up stale idempotency keys.
- Know the payment ecosystem: card networks (Visa, Mastercard, Amex), ACH, SEPA, wire transfers, and digital wallets.
- Stripe's culture emphasizes "moving with urgency" but "caring about the details." Show that you can move fast without cutting corners.
- For behavioral questions, use the STAR format and quantify results. Stripe values impact and metrics.
- Understand the concept of "developer experience" (DX) — Stripe's product is APIs, so every design decision should consider the developer using it.
- For system design, always discuss how you would handle a payment processor outage. Stripe's infrastructure is designed for multi-processor resilience.
- Know the difference between authorization and capture in payment processing, and how Stripe's API handles both.
- Be prepared to discuss how you would design a system that supports 135+ currencies with proper decimal handling and rounding.
