# Microsoft Interview Guide — Backend Academy

## Interview Process for Backend Roles

Microsoft's backend interview process typically spans 4-6 weeks with 4-5 rounds:

1. **Phone Screen (45 min)** — Technical screen with a backend engineer covering algorithms, data structures, and basic system design. Often includes a live-coding problem.
2. **Coding Round 1 (45 min)** — Algorithmic problem solving. Microsoft focuses on problem-solving process, not just the final answer. Expect medium-difficulty LeetCode problems.
3. **Coding Round 2 (45 min)** — Second coding round, may include a design component or a more complex algorithmic problem. Microsoft often asks about string manipulation, trees, and dynamic programming.
4. **System Design Round (60 min)** — Design a backend system. Microsoft focuses on integration with Azure services, enterprise requirements, and reliability.
5. **Behavioral / "Fit" Round (45 min)** — Based on Microsoft's culture: growth mindset, customer obsession, diversity and inclusion, and collaboration. Expect questions about how you handle ambiguity, work with cross-functional teams, and deal with failure.

Microsoft's backend interviews emphasize engineering fundamentals, system design with Azure services, and the ability to work in a large organization with complex dependencies.

## Top Problems by Backend Topic

### Topic: Enterprise API Design

#### Problem: Design a Multi-Tenant SaaS API
- **LC/System Design ref**: System Design: Multi-Tenant SaaS
- **Problem statement**: Design a REST API for a multi-tenant SaaS platform where each tenant (organization) has isolated data, custom configurations, and usage quotas.
- **Interview walkthrough**: Discuss tenant isolation strategies: database per tenant, schema per tenant, or shared schema with tenant_id column. For Microsoft's scale, prefer shared schema with tenant_id for most cases, with database-per-tenant for enterprise customers. Discuss tenant-aware caching: partition cache keys by tenant. Discuss rate limiting per tenant, custom domain support, and tenant onboarding workflow.
- **Solution**: Use shared schema with tenant_id on every table. Use Spring's multi-tenancy support with a TenantContext (ThreadLocal) populated by a filter that extracts tenant from the JWT or subdomain. Use Flyway for schema migration with tenant-aware scripts. For rate limiting, use Redis with tenant-specific keys. For data isolation, add tenant_id to all queries via a Hibernate filter.
- **Java/Spring code**:
```java
@Component
public class TenantFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        String tenantId = extractTenant((HttpServletRequest) request);
        TenantContext.setTenantId(tenantId);
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}

@Entity
@Table(name = "orders")
@TenantFilter(column = "tenant_id")
public class Order {
    @Id @GeneratedValue(strategy = UUID)
    private UUID id;
    private String tenantId;
    // ...
}

@Component
public class TenantFilterProvider implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getTenantId();
    }
}
```

- **What Microsoft evaluates**: Multi-tenancy architecture, data isolation, configuration management, and enterprise-grade security.
- **Follow-ups**: How would you handle tenant-specific customizations? How would you implement tenant-level billing? How would you support on-premise deployment?

#### Problem: Design a File Storage Service (OneDrive-like)
- **LC/System Design ref**: System Design: Cloud File Storage
- **Problem statement**: Design a cloud file storage service that supports upload, download, sync, version history, and sharing. Handle 500M users and exabytes of data.
- **Interview walkthrough**: Discuss the architecture: frontend servers handle upload/download, metadata service stores file metadata in a database, blob store (Azure Blob Storage) stores file content. For sync, use a delta sync algorithm (rsync-like) to minimize data transfer. For version history, store only changed chunks using content-addressable storage. Discuss conflict resolution for concurrent edits — use last-writer-wins or version vectors.
- **Solution**: Use a microservice architecture: File Service (metadata), Blob Service (content), Sync Service (delta sync), Version Service (history). Store metadata in Azure Cosmos DB for global distribution. Store content in Azure Blob Storage with lifecycle policies. For versioning, use content-addressable storage: hash the file content, store once, reference by hash. For sync, use a chunk-based approach: split files into 4MB chunks, compute hashes, transfer only changed chunks.
- **Java/Spring code**:
```java
@Service
public class FileSyncService {
    private final BlobStorageClient blobClient;
    private final FileMetadataRepository metadataRepository;

    public SyncPlan computeSyncPlan(UUID userId, List<FileChunkHash> clientChunks) {
        List<FileChunk> serverChunks = metadataRepository.findChunksByUserId(userId);
        Set<String> serverHashes = serverChunks.stream()
            .map(FileChunk::getHash).collect(Collectors.toSet());
        Set<String> clientHashes = clientChunks.stream()
            .map(FileChunkHash::hash).collect(Collectors.toSet());

        List<String> neededHashes = clientHashes.stream()
            .filter(h -> !serverHashes.contains(h))
            .toList();
        List<String> missingHashes = serverHashes.stream()
            .filter(h -> !clientHashes.contains(h))
            .toList();

        return new SyncPlan(neededHashes, missingHashes);
    }
}
```

- **What Microsoft evaluates**: Enterprise-grade design, data isolation, multi-tenancy patterns, and Azure service integration.
- **Follow-ups**: How would you handle tenant migration? How would you implement cross-tenant analytics? How would you design for sovereign cloud requirements?

#### Problem: Design a Blob Storage System with Redundancy
- **LC/System Design ref**: System Design: Distributed File System / Blob Store
- **Problem statement**: Design a blob storage system that stores petabytes of data with 99.999999999% durability (11 nines), supports geo-redundancy, and provides REST API access.
- **Interview walkthrough**: Discuss data durability strategies: replication (3x) vs erasure coding. For Azure, erasure coding is used for cost efficiency. Discuss the write pipeline: client uploads to a frontend, which streams to multiple storage nodes in parallel. For reads, use a CDN for hot data and direct storage access for cold data. Discuss data lifecycle management: hot, cool, archive tiers. Discuss consistency models: strong consistency for writes, eventual for reads.
- **Solution**: Use erasure coding (12+4) for durability with 1.33x overhead vs 3x for replication. Data is striped across 16 fault domains. A metadata service tracks blob locations. For geo-redundancy, replicate asynchronously to a secondary region. Use Azure Blob Storage APIs for access. Implement lifecycle policies to move data between hot/cool/archive tiers.
- **Java/Spring code**:
```java
@Service
public class BlobStorageService {
    private final BlobStoreClient blobStore;
    private final MetadataService metadataService;

    public UploadResult upload(InputStream data, String blobName, String contentType) {
        String contentHash = digest(data);
        String storagePath = contentHash.substring(0, 2) + "/" + contentHash.substring(2, 4) + "/" + contentHash;
        BlobProperties props = blobStore.upload(storagePath, data, contentType);
        BlobMetadata metadata = new BlobMetadata(blobName, contentHash, props.size(), contentType);
        metadataService.save(metadata);
        return new UploadResult(metadata.getId(), contentHash);
    }

    public InputStream download(UUID blobId) {
        BlobMetadata metadata = metadataService.findById(blobId);
        String storagePath = metadata.contentHash().substring(0, 2) + "/"
            + metadata.contentHash().substring(2, 4) + "/" + metadata.contentHash();
        return blobStore.download(storagePath);
    }
}
```

- **What Microsoft evaluates**: Storage system design, data durability, lifecycle management, and Azure service integration.
- **Follow-ups**: How would you implement geo-redundancy? How would you handle large file uploads with resumable upload? How would you implement access control?

### Topic: System Design

#### Design Microsoft Teams (Real-Time Collaboration)
- **Requirements**: Real-time messaging, file sharing, video/audio calls, channel organization, 100M+ daily active users.
- **Framework**: Discuss the architecture: client connects to a regional frontend via WebSocket. The frontend authenticates and routes to a chat service. Chat messages are written to a distributed log (Kafka) and fanned out to recipients. For channels, use a publish-subscribe model. For file sharing, integrate with OneDrive/SharePoint. For calls, use a separate media server infrastructure (Azure Communication Services).
- **Key trade-offs**: Consistency vs availability for message ordering. Online vs offline message storage. Client-side vs server-side search indexing.
- **Microsoft-specific**: Discuss Azure SignalR Service for WebSocket management, Azure Cosmos DB for global distribution of chat history, and Azure Communication Services for calling. Mention the use of Fluid Framework for real-time collaboration.

#### Design Azure Blob Storage
- **Requirements**: Store exabytes of data, 99.999999999% durability, global access, REST API, tiered storage.
- **Framework**: Discuss the storage architecture: frontend layer (load balancer, authentication), partition layer (range-based partitioning by blob name), and storage layer (streams on NTFS). For durability, use Local Redundant Storage (LRS) with 3 replicas within a datacenter, plus Geo-Redundant Storage (GRS) for cross-region replication. Discuss the write pipeline: client uploads to frontend, which streams to multiple storage nodes in parallel, waits for acknowledgment from quorum, then confirms to client.
- **Key trade-offs**: Durability vs latency. Geo-redundancy vs write latency. Hot vs cool vs archive access tiers.
- **Microsoft-specific**: Discuss Azure Storage Stack, the Stream Layer, and the Partition Layer. Mention the use of NTFS for local storage and the Storage Stamp architecture.

#### Design an Enterprise SSO System
- **Requirements**: Support SAML, OAuth2, OpenID Connect. Handle 10M+ enterprise users. Provide single sign-on across thousands of applications.
- **Framework**: Discuss the authentication flow: user requests access to an app, is redirected to the identity provider (Azure AD), authenticates (password, MFA, Windows Hello), receives a token, and is redirected back. For OAuth2, discuss authorization code flow with PKCE. For SAML, discuss the SAML assertion flow. Discuss token caching, session management, and refresh token rotation.
- **Key trade-offs**: Security vs user experience. Token lifetime vs revocation latency. Centralized vs federated identity.
- **Microsoft-specific**: Discuss Azure AD, Active Directory Federation Services (AD FS), and the Microsoft identity platform. Mention support for legacy protocols (WS-Federation, NTLM) for enterprise customers.

## Behavioral Questions

### Tell me about a time you had to learn a new technology quickly
- **Situation**: I was assigned to lead the migration of a legacy .NET Framework monolith to a Spring Boot microservices architecture. I had no prior Spring Boot experience.
- **Task**: Deliver the first microservice within 6 weeks while learning Spring Boot, Docker, and Kubernetes.
- **Action**: Followed a structured learning plan: spent the first week on Spring Boot tutorials and building a small prototype. Used the Backend Academy labs for Spring Boot, REST APIs, and Spring Cloud. Paired with a senior engineer for code reviews. Built the first service (user management) with extensive tests. Documented patterns for the rest of the team.
- **Result**: The first service was delivered in 5 weeks. The team adopted the patterns I established for the remaining 12 services. I became the go-to person for Spring Boot questions.
- **What Microsoft evaluates**: Growth mindset, Learn It All, ability to ramp up quickly, and knowledge sharing.

### Tell me about a time you had to balance quality with speed
- **Situation**: A critical security vulnerability was discovered in our authentication library. The fix required a breaking API change, but we had a major release scheduled in 2 days.
- **Task**: Fix the vulnerability without delaying the release or breaking existing integrations.
- **Action**: Implemented a versioned API endpoint: the old endpoint was deprecated with a sunset header, the new endpoint was added. Used a feature flag to gradually roll out the new authentication flow. Ran both versions in parallel for 2 weeks, monitoring error rates. Wrote a migration guide for API consumers. Automated the migration for internal services.
- **Result**: The vulnerability was patched within 24 hours. The release shipped on time. 90% of API consumers migrated within the first week with zero downtime.
- **What Microsoft evaluates**: Growth mindset, customer focus, and ability to balance security with business needs.

### Tell me about a time you used data to make a decision
- **Situation**: Our team was debating whether to rewrite a legacy reporting service or incrementally improve it. The rewrite would take 6 months; incremental improvements would take 2 months but might not fully solve the problem.
- **Task**: Make a data-driven recommendation.
- **Action**: Analyzed production data: the service had 87% error rate on a specific report type that accounted for only 3% of usage. The remaining 97% of reports worked well. Proposed incremental improvements: fixed the broken report type, added caching for frequently accessed reports, and added a new API for the problematic use case. The rewrite would have been a poor ROI.
- **Result**: Fixed the 87% error rate in 2 weeks. User satisfaction improved. The rewrite was deprioritized, saving 4 months of engineering time.
- **What Microsoft evaluates**: Data-driven decision making, customer focus, and pragmatic engineering.

### Tell me about a time you worked across teams
- **Situation**: Our backend team needed to integrate with the data science team's ML model for fraud detection. The model was served as a Python microservice with high latency and no SLA.
- **Task**: Integrate the fraud detection model into the payment flow without increasing payment latency beyond 200ms.
- **Action**: Proposed an async integration: the payment service publishes a payment event to Kafka, the fraud detection service consumes it, scores it, and publishes the result. The payment service uses a callback or polls for the result. This decoupled the services and allowed the ML team to scale independently. We also added a fallback: if the fraud service doesn't respond within 100ms, proceed with a default risk score.
- **Result**: Payment latency remained under 200ms. Fraud detection rate improved by 40%. The async pattern became the standard for ML service integration.
- **What Microsoft evaluates**: Cross-team collaboration, pragmatic integration, and customer focus.

## Study Plan

Priority labs for Microsoft backend interviews:
1. **Spring Boot** — Core framework knowledge
2. **REST APIs** — API design fundamentals
3. **Security** — Authentication, authorization, Azure AD
4. **Security Deep** — Enterprise security patterns
5. **Multi-tenancy** — SaaS architecture patterns
6. **API Versioning** — Backward compatibility
7. **Messaging** — Event-driven architecture
8. **Caching** — Redis, CDN, multi-tier caching
9. **Testing** — Integration and contract testing
10. **Spring Cloud** — Service discovery, configuration, gateway

## Tips

- Microsoft interviews emphasize the "growth mindset." Show that you learn from failures and seek feedback.
- Be prepared to discuss Azure services: Azure AD, Cosmos DB, Blob Storage, Service Bus, API Management, and Functions.
- Microsoft values enterprise-grade design: security, compliance, multi-tenancy, and scalability are critical.
- For system design, discuss how you would integrate with existing Microsoft ecosystem (Active Directory, Office 365, Dynamics).
- Know the difference between Azure SQL Database, Cosmos DB, and SQL Server — when to use each.
- Microsoft interviewers appreciate candidates who ask clarifying questions and validate assumptions before diving into solutions.
- For coding rounds, focus on correctness and edge cases. Microsoft values thorough testing of your own code.
- Be prepared to discuss hybrid cloud scenarios (on-premise + cloud) — many Microsoft customers run hybrid deployments.
- Understand Azure's Well-Architected Framework: reliability, security, cost optimization, operational excellence, performance efficiency.

### Topic: Messaging & Event-Driven Architecture

#### Problem: Design an Event-Driven Order Processing Pipeline
- **LC/System Design ref**: System Design: Event-Driven Architecture
- **Problem statement**: Design an event-driven order processing system that handles order placement, payment processing, inventory reservation, and shipping notification using Azure Service Bus or Kafka.
- **Interview walkthrough**: Discuss the event flow: OrderPlaced -> PaymentProcessed -> InventoryReserved -> ShipmentCreated. Each service publishes events and subscribes to relevant topics. Use Azure Service Bus topics with subscriptions for fan-out. For reliability, use dead-letter queues and retry policies. For idempotency, include a message ID in each event. Discuss the saga pattern for distributed transactions.
- **Solution**: Use Azure Service Bus topics with subscriptions. Each service subscribes to relevant events. The order service publishes OrderPlaced. The payment service subscribes, processes payment, and publishes PaymentProcessed or PaymentFailed. The inventory service subscribes and reserves inventory. Use a saga orchestrator for the order flow. For idempotency, use a deduplication store (Redis) with message IDs.
- **Java/Spring code**:
```java
@Service
public class OrderSagaService {
    private final AzureServiceBusTemplate serviceBus;

    @Transactional
    public void placeOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        orderRepository.save(order);
        serviceBus.send("order-events", new OrderPlacedEvent(order.getId(), order.getUserId(), order.getTotal()));
    }

    @ServiceBusListener(topic = "payment-events", subscription = "order-service")
    public void onPaymentResult(PaymentEvent event) {
        Order order = orderRepository.findById(event.orderId()).orElseThrow();
        if (event.success()) {
            order.setStatus(OrderStatus.CONFIRMED);
            serviceBus.send("order-events", new OrderConfirmedEvent(order.getId()));
        } else {
            order.setStatus(OrderStatus.FAILED);
            order.setFailureReason(event.reason());
        }
        orderRepository.save(order);
    }
}
```

- **What Microsoft evaluates**: Event-driven architecture, Azure Service Bus, saga patterns, and reliability.
- **Follow-ups**: How would you handle duplicate events? How would you implement event replay? How would you handle schema evolution in events?

### Topic: Caching & Performance

#### Problem: Design a Distributed Cache for User Sessions
- **LC/System Design ref**: System Design: Distributed Session Store
- **Problem statement**: Design a distributed session store for a multi-region enterprise application. Sessions must be available across regions with <10ms read latency and survive region failures.
- **Interview walkthrough**: Discuss Redis vs Memcached vs Azure Redis Cache. For cross-region, use active geo-replication or a global distributed cache. For session affinity, use sticky sessions or a global session store. Discuss serialization (JSON vs Protocol Buffers vs Kryo). Discuss eviction policies: LRU with TTL. Discuss security: encrypt session data at rest and in transit.
- **Solution**: Use Azure Redis Cache with geo-replication for cross-region access. Store sessions as hashes with TTL. Use Protocol Buffers for serialization (compact, fast). For failover, use a passive replica in a secondary region. For session affinity, use a cookie with the session ID; the application reads from the nearest Redis replica.
- **Java/Spring code**:
```java
@Component
public class SessionStore {
    private final RedisTemplate<String, SessionData> redis;

    public void saveSession(String sessionId, SessionData data, Duration ttl) {
        redis.opsForValue().set("session:" + sessionId, data, ttl);
    }

    public Optional<SessionData> getSession(String sessionId) {
        SessionData data = redis.opsForValue().get("session:" + sessionId);
        return Optional.ofNullable(data);
    }

    public void refreshSession(String sessionId, Duration ttl) {
        redis.expire("session:" + sessionId, ttl);
    }
}
```

- **What Microsoft evaluates**: Distributed caching, session management, cross-region architecture, and enterprise reliability.
- **Follow-ups**: How would you handle session fixation attacks? How would you implement session logout across all devices? How would you handle GDPR data access requests for session data?

### Topic: System Design — Additional Questions

#### Design Microsoft's Azure DevOps Pipeline
- **Requirements**: Design a CI/CD pipeline service that supports 10M+ builds/month, integrates with GitHub, Azure Repos, and supports containers, VMs, and serverless deployments.
- **Framework**: Discuss the architecture: agent pool management, job scheduling, artifact storage, and pipeline orchestration. For agent pools, use a mix of Microsoft-hosted and self-hosted agents. For job scheduling, use a distributed queue with priority. For artifact storage, use Azure Blob Storage with CDN. For pipeline orchestration, use a DAG-based execution engine.
- **Key trade-offs**: Hosted vs self-hosted agents. Build speed vs cost. Parallelism vs resource contention.
- **Microsoft-specific**: Discuss Azure DevOps Pipelines, the use of Azure Container Instances for ephemeral build agents, and the integration with GitHub Actions.

#### Design Microsoft's Power Platform Backend
- **Requirements**: Support low-code app development with connectors to 200+ data sources, real-time collaboration, and enterprise-grade security.
- **Framework**: Discuss the architecture: connector service (integrates with external APIs), formula engine (evaluates Power Fx expressions), storage service (stores app definitions and data), and runtime service (executes apps). For connectors, use a plugin architecture with OAuth2 authentication. For the formula engine, use a tree-walking interpreter. For real-time collaboration, use WebSocket with CRDTs for conflict resolution.
- **Key trade-offs**: Connector flexibility vs security. Formula expressiveness vs performance. Real-time collaboration vs complexity.
- **Microsoft-specific**: Discuss Power Platform's use of Azure API Management for connectors, the Common Data Service for storage, and the Power Fx formula language.

## Behavioral Questions — Additional

#### Tell me about a time you had to deal with ambiguity
- **Situation**: Our team was asked to build a "customer 360" view, but the requirements were vague — "show everything we know about a customer."
- **Task**: Define the scope and deliver a useful product without clear requirements.
- **Action**: Interviewed 5 stakeholders (sales, support, marketing, product, engineering) to understand their needs. Prioritized the most common use cases: order history, support tickets, payment status, and account details. Built an MVP with these 4 views. Used a modular architecture so new views could be added. Presented the MVP to stakeholders and iterated based on feedback.
- **Result**: The MVP was adopted by the support team within the first week. Three more views were added in subsequent sprints. The project was recognized as a model for handling ambiguous requirements.
- **What Microsoft evaluates**: Growth mindset, customer focus, and ability to handle ambiguity.

#### Tell me about a time you mentored someone
- **Situation**: A junior engineer on my team was struggling with understanding distributed systems concepts and was blocked on a task involving Kafka.
- **Task**: Help them become productive while maintaining their autonomy.
- **Action**: Set up a weekly 1:1 mentoring session. Started with the fundamentals: Kafka topics, partitions, consumer groups, and offset management. Paired on the first task (writing a Kafka consumer) and gradually reduced involvement. Created a knowledge base document with common patterns and troubleshooting steps. Encouraged them to present their solution to the team.
- **Result**: The junior engineer became productive within 3 weeks and later led the migration of 3 services to event-driven architecture. The knowledge base document was used by 5 new team members.
- **What Microsoft evaluates**: Growth mindset, mentorship, and knowledge sharing.

## Study Plan — Expanded

Priority labs for Microsoft backend interviews:
1. **Spring Boot** — Core framework knowledge
2. **REST APIs** — API design fundamentals
3. **Security** — Authentication, authorization, Azure AD
4. **Security Deep** — Enterprise security patterns
5. **Multi-tenancy** — SaaS architecture patterns
6. **API Versioning** — Backward compatibility
7. **Messaging** — Event-driven architecture
8. **Caching** — Redis, CDN, multi-tier caching
9. **Testing** — Integration and contract testing
10. **Spring Cloud** — Service discovery, configuration, gateway
11. **Transactions** — ACID, distributed transactions
12. **Performance** — Profiling, optimization
13. **Batch Processing** — Large-scale data processing
14. **Spring Boot Internals** — Deep framework understanding

## Tips — Additional

- Microsoft values "growth mindset" above all. When discussing failures, focus on what you learned and how you improved.
- For system design, always consider enterprise requirements: compliance (SOC2, ISO 27001), accessibility, and localization.
- Know Azure's global infrastructure: regions, availability zones, and paired regions for disaster recovery.
- Be prepared to discuss how you would migrate a legacy on-premise system to the cloud (Azure Migrate, Database Migration Service).
- Microsoft interviewers appreciate candidates who can explain complex concepts simply. Practice teaching a technical concept.
- For coding rounds, consider accessibility and internationalization — Microsoft products serve a global audience.
- Understand the concept of "responsible AI" — Microsoft has strong principles around ethical AI development.
- For system design, always discuss cost optimization. Microsoft values building cost-effective solutions.
- Know the Azure Well-Architected Framework pillars: reliability, security, cost optimization, operational excellence, performance efficiency.

### Topic: API Versioning & Backward Compatibility

#### Problem: Design an API Versioning Strategy for a SaaS Platform
- **LC/System Design ref**: System Design: API Versioning
- **Problem statement**: Design an API versioning strategy for a SaaS platform with 1000+ API consumers. The strategy must support breaking changes, deprecation timelines, and multiple active versions.
- **Interview walkthrough**: Discuss versioning strategies: URL path versioning (`/v1/`, `/v2/`), header versioning (`Accept: application/vnd.company.v2+json`), and query parameter versioning. For enterprise APIs, URL path versioning is preferred for clarity. Discuss deprecation policy: announce 6 months in advance, maintain old version for 12 months, provide migration guides. Discuss how to handle versioning in the codebase: package-by-version or interceptor-based routing.
- **Solution**: Use URL path versioning (`/api/v1/orders`, `/api/v2/orders`). In the codebase, use separate controller packages for each version. Share common code (services, repositories) between versions. For deprecation, add a `Sunset` header to responses and a `Deprecation` header. Use a version resolver interceptor that routes requests to the appropriate controller based on the URL prefix.
- **Java/Spring code**:
```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderControllerV1 {
    @GetMapping
    public ResponseEntity<List<OrderV1>> listOrders() {
        return ResponseEntity.ok(orderService.findAll().stream()
            .map(OrderV1::from)
            .toList());
    }
}

@RestController
@RequestMapping("/api/v2/orders")
public class OrderControllerV2 {
    @GetMapping
    public ResponseEntity<List<OrderV2>> listOrders() {
        return ResponseEntity.ok(orderService.findAll().stream()
            .map(OrderV2::from)
            .toList());
    }
}
```

- **What Microsoft evaluates**: API versioning strategy, backward compatibility, and deprecation management.
- **Follow-ups**: How would you handle versioning of asynchronous APIs (events)? How would you communicate deprecation to API consumers? How would you handle versioning of error responses?

### Topic: Testing & Quality Assurance

#### Problem: Design a Contract Testing Framework for Microservices
- **LC/System Design ref**: System Design: Contract Testing
- **Problem statement**: Design a contract testing framework that ensures microservices communicate correctly, catches breaking changes before deployment, and provides clear feedback to developers.
- **Interview walkthrough**: Discuss consumer-driven contract testing (CDCT) vs provider-driven testing. Use Spring Cloud Contract or Pact for consumer-driven contracts. Each consumer defines expectations (e.g., "the order service returns a 200 with order ID when I POST to /orders"). The provider runs these contracts in its CI pipeline. Discuss how to handle versioning of contracts and how to detect breaking changes.
- **Solution**: Use Spring Cloud Contract for consumer-driven contracts. Each consumer service defines contracts in a shared repository. The provider service runs contract verification in its CI pipeline. Contracts are versioned and stored alongside the consumer service. Breaking changes are detected at build time, not runtime. For cross-team contracts, use a contract broker (Pact Broker) to share and version contracts.
- **Java/Spring code**:
```java
// Consumer contract (in consumer's test)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderServiceContractTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnOrderWhenOrderExists() {
        mockMvc.perform(get("/api/v1/orders/123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value("123"))
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}
```

- **What Microsoft evaluates**: Contract testing, API compatibility, and cross-team collaboration.
- **Follow-ups**: How would you handle breaking changes in contracts? How would you version contracts? How would you automate contract verification in CI/CD?

### Topic: Performance & Scalability

#### Problem: Design a Connection Pool for a High-Throughput Service
- **LC/System Design ref**: System Design: Connection Pooling
- **Problem statement**: Design a database connection pool for a service that handles 10K concurrent requests with sub-10ms database query latency. The pool must handle traffic spikes, prevent connection leaks, and provide metrics.
- **Interview walkthrough**: Discuss HikariCP configuration: maximum pool size, minimum idle, connection timeout, max lifetime, leak detection threshold. For high throughput, use a pool size equal to the number of CPU cores * 2 + effective spindle count. Discuss connection validation: test-on-borrow vs test-while-idle. Discuss monitoring: pool utilization, connection acquisition time, and timeout rate.
- **Solution**: Use HikariCP with a maximum pool size of 20-50 (depending on CPU cores). Configure max-lifetime to 30 minutes to handle connection rotation. Enable leak detection with a 30-second threshold. Use a health check that pings the database. For monitoring, expose HikariCP metrics via Micrometer to Prometheus. Set up alerts for pool exhaustion and slow connection acquisition.
- **Java/Spring code**:
```java
@Configuration
public class DatabaseConfig {
    @Bean
    public HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
        config.setMaximumPoolSize(50);
        config.setMinimumIdle(10);
        config.setConnectionTimeout(5000);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(30000);
        config.setConnectionTestQuery("SELECT 1");
        return new HikariDataSource(config);
    }
}
```

- **What Microsoft evaluates**: Connection pooling configuration, performance tuning, and operational monitoring.
- **Follow-ups**: How would you tune the pool for a read-heavy workload? How would you handle database failover? How would you monitor connection pool health?

### Topic: Security & Identity

#### Problem: Design an OAuth2 Authorization Server
- **LC/System Design ref**: System Design: OAuth2 Server
- **Problem statement**: Design an OAuth2 authorization server that supports authorization code flow, client credentials flow, and refresh tokens. The server must be highly available and secure.
- **Interview walkthrough**: Discuss OAuth2 grant types: authorization code (with PKCE), client credentials, and refresh token. For enterprise, also discuss SAML and WS-Federation. For security, use short-lived access tokens (15 min) and longer-lived refresh tokens (30 days). For token storage, use Redis with TTL. For revocation, maintain a token blacklist. Discuss the use of JWTs for access tokens and opaque tokens for refresh tokens.
- **Solution**: Use Spring Security OAuth2 Authorization Server. Authorization code flow with PKCE for public clients. Access tokens are JWTs signed with RS256. Refresh tokens are opaque tokens stored in Redis. For multi-tenancy, use a tenant-aware token issuer. For revocation, maintain a Redis set of revoked token IDs checked on each request.
- **Java/Spring code**:
```java
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig {
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        return new JdbcRegisteredClientRepository(dataSource());
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        return new JdbcOAuth2AuthorizationService(dataSource(), registeredClientRepository());
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if (context.getTokenType().equals(OAuth2TokenType.ACCESS_TOKEN)) {
                context.getClaims().claim("tenant_id", TenantContext.getTenantId());
                context.getClaims().claim("roles", extractRoles(context.getPrincipal()));
            }
        };
    }
}
```

- **What Microsoft evaluates**: OAuth2/OpenID Connect knowledge, token management, and enterprise security.
- **Follow-ups**: How would you implement token revocation? How would you handle cross-tenant authentication? How would you implement step-up authentication?

### Topic: Performance & Scalability

#### Problem: Design a Database Read Replica Strategy
- **LC/System Design ref**: System Design: Read Replicas
- **Problem statement**: Design a read replica strategy for a SaaS platform with a 10:1 read-to-write ratio. The system must handle 100K reads/second with <10ms latency and support read-after-write consistency.
- **Interview walkthrough**: Discuss read replica architecture: one primary for writes, multiple replicas for reads. For read-after-write consistency, use read-your-writes consistency: after a write, read from the primary for a configurable duration. For load balancing, use a round-robin or least-connections strategy across replicas. Discuss replication lag monitoring and alerting. For failover, use automatic promotion of a replica to primary.
- **Solution**: Use PostgreSQL with 3 read replicas. Use a custom routing datasource that directs writes to the primary and reads to replicas. For read-after-write consistency, use a request-scoped cache: after a write, subsequent reads from the same request go to the primary for 1 second. For replication lag, monitor `pg_stat_replication` and alert if lag exceeds 5 seconds.
- **Java/Spring code**:
```java
@Component
public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
    private final List<DataSource> readReplicas;
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected Object determineCurrentLookupKey() {
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            return "read";
        }
        return "write";
    }

    public DataSource getReadDataSource() {
        int index = counter.incrementAndGet() % readReplicas.size();
        return readReplicas.get(index);
    }
}
```

- **What Microsoft evaluates**: Database scaling, read replica management, and performance optimization.
- **Follow-ups**: How would you handle replication lag? How would you implement failover? How would you monitor replica health?

### Topic: Testing & Quality

#### Problem: Design a Test Strategy for a Microservices Architecture
- **LC/System Design ref**: System Design: Testing Strategy
- **Problem statement**: Design a testing strategy for a microservices architecture with 20+ services. The strategy must ensure quality without slowing down development velocity.
- **Interview walkthrough**: Discuss the test pyramid: unit tests (fast, isolated), integration tests (test boundaries), contract tests (service-to-service), end-to-end tests (critical paths). For microservices, emphasize contract tests to catch breaking changes early. Use testcontainers for integration tests with real dependencies. Use WireMock for external service mocking. For performance, use Gatling or JMeter for load testing.
- **Solution**: Use a multi-layered testing strategy. Unit tests for business logic (JUnit 5, Mockito). Integration tests with Testcontainers (PostgreSQL, Kafka, Redis). Contract tests with Spring Cloud Contract for service-to-service boundaries. End-to-end tests for critical user journeys (1-2 per service). Performance tests with Gatling for SLA verification. All tests run in CI with parallel execution.
- **Java/Spring code**:
```java
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
        .withDatabaseName("testdb");

    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OrderService orderService;

    @Test
    void shouldCreateOrderAndPublishEvent() {
        CreateOrderRequest request = new CreateOrderRequest("user-1", List.of("item-1"), BigDecimal.TEN);
        Order order = orderService.createOrder(request);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
        // Verify event was published to Kafka
        // ...
    }
}
```

- **What Microsoft evaluates**: Testing strategy, test infrastructure, and quality assurance.
- **Follow-ups**: How would you test asynchronous workflows? How would you implement performance testing in CI? How would you test database migrations?

### Topic: System Design — Additional Questions

#### Design Microsoft's Azure Cosmos DB
- **Requirements**: Design a globally distributed, multi-model database service with turnkey global distribution, elastic scalability, and multiple consistency levels.
- **Framework**: Discuss the Cosmos DB architecture: a resource governor that manages throughput (RU/s), a replication layer that handles global distribution, and a storage engine that supports multiple data models (document, key-value, graph, column-family). For consistency, offer 5 levels: strong, bounded staleness, session, consistent prefix, eventual. For partitioning, use a hash-based partition key.
- **Key trade-offs**: Consistency vs performance. Throughput vs cost. Multi-region writes vs conflict resolution.
- **Microsoft-specific**: Discuss Cosmos DB's use of a write-ahead log, the replication protocol for global distribution, and the resource governance model for predictable performance.

#### Design Microsoft's Azure API Management
- **Requirements**: Design an API management platform that provides API gateway, developer portal, analytics, and policy enforcement for enterprise APIs.
- **Framework**: Discuss the API Management architecture: API gateway (handles requests, enforces policies), developer portal (API documentation, key management), publisher portal (API configuration), and analytics pipeline. For policies, support rate limiting, IP filtering, JWT validation, and transformation. For caching, use a response cache with configurable TTL. For analytics, use a streaming pipeline to collect metrics.
- **Key trade-offs**: Gateway latency vs policy complexity. Managed vs self-hosted gateway. API key vs OAuth2 authentication.
- **Microsoft-specific**: Discuss Azure API Management tiers (consumption, developer, basic, standard, premium), the policy expression language, and the integration with Azure AD for authentication.

## Behavioral Questions — Additional

#### Tell me about a time you had to balance technical debt with feature delivery
- **Situation**: Our team was building a new feature that required significant refactoring of the existing codebase. The product team wanted the feature in 4 weeks; the refactoring alone would take 3 weeks.
- **Task**: Deliver the feature on time while addressing the technical debt.
- **Action**: Proposed a compromise: refactor the most critical 20% of the code that directly impacted the new feature, and defer the remaining refactoring. Used the strangler fig pattern: built the new feature alongside the old code, gradually migrated traffic. Documented the remaining technical debt in a backlog with estimated effort and impact.
- **Result**: The feature shipped on time. The partial refactoring improved test coverage and reduced bugs in the affected area. The documented technical debt was addressed in the next quarter.
- **What Microsoft evaluates**: Growth mindset, pragmatic decision-making, and ability to balance short-term and long-term goals.

## Study Plan — Expanded

Priority labs for Microsoft backend interviews:
1. **Spring Boot** — Core framework knowledge
2. **REST APIs** — API design fundamentals
3. **Security** — Authentication, authorization, Azure AD
4. **Security Deep** — Enterprise security patterns
5. **Multi-tenancy** — SaaS architecture patterns
6. **API Versioning** — Backward compatibility
7. **Messaging** — Event-driven architecture
8. **Caching** — Redis, CDN, multi-tier caching
9. **Testing** — Integration and contract testing
10. **Spring Cloud** — Service discovery, configuration, gateway
11. **Transactions** — ACID, distributed transactions
12. **Performance** — Profiling, optimization
13. **Batch Processing** — Large-scale data processing
14. **Spring Boot Internals** — Deep framework understanding
15. **CQRS/Axon** — Event sourcing, CQRS patterns
16. **GraalVM** — Native compilation, performance optimization

## Tips — Additional

- Microsoft values "growth mindset" above all. When discussing failures, focus on what you learned and how you improved.
- For system design, always consider enterprise requirements: compliance (SOC2, ISO 27001), accessibility, and localization.
- Know Azure's global infrastructure: regions, availability zones, and paired regions for disaster recovery.
- Be prepared to discuss how you would migrate a legacy on-premise system to the cloud (Azure Migrate, Database Migration Service).
- Microsoft interviewers appreciate candidates who can explain complex concepts simply. Practice teaching a technical concept.
- For coding rounds, consider accessibility and internationalization — Microsoft products serve a global audience.
- Understand the concept of "responsible AI" — Microsoft has strong principles around ethical AI development.
- For system design, always discuss cost optimization. Microsoft values building cost-effective solutions.
- Know the Azure Well-Architected Framework pillars: reliability, security, cost optimization, operational excellence, performance efficiency.
- Be prepared to discuss how you would handle a multi-cloud strategy. Microsoft supports hybrid and multi-cloud deployments.
- For system design, always discuss disaster recovery and business continuity. Microsoft's enterprise customers require this.
- Know the difference between Azure SQL Database, Cosmos DB, and SQL Server — when to use each.
- Microsoft interviewers appreciate candidates who ask clarifying questions and validate assumptions before diving into solutions.
- For coding rounds, focus on correctness and edge cases. Microsoft values thorough testing of your own code.
- Be prepared to discuss hybrid cloud scenarios (on-premise + cloud) — many Microsoft customers run hybrid deployments.
- Understand Azure's Well-Architected Framework: reliability, security, cost optimization, operational excellence, performance efficiency.
