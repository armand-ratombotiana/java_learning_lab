# Backend Academy — Interview Cheatsheet

<div align="center">

![Cheatsheet](https://img.shields.io/badge/Cheatsheet-6DB33F?style=for-the-badge)
![Quick Reference](https://img.shields.io/badge/Quick_Reference-4285F4?style=for-the-badge)
![200+](https://img.shields.io/badge/Lines-200+-blue?style=for-the-badge)

**Quick reference for backend engineering interviews — annotations, patterns, optimization, and security**

</div>

---

## Spring Boot Annotations Cheat Sheet

### Core Annotations
| Annotation | Purpose | Example |
|------------|---------|---------|
| `@SpringBootApplication` | Main entry point — combines `@Configuration`, `@EnableAutoConfiguration`, `@ComponentScan` | `@SpringBootApplication public class Application {}` |
| `@RestController` | Controller where every method returns domain object (not view) | `@RestController @RequestMapping("/api/v1/users")` |
| `@RequestMapping` | Maps HTTP requests to handler methods | `@RequestMapping(method = RequestMethod.GET)` |
| `@GetMapping` | Shortcut for `@RequestMapping(method = GET)` | `@GetMapping("/{id}")` |
| `@PostMapping` | Shortcut for HTTP POST | `@PostMapping @ResponseStatus(HttpStatus.CREATED)` |
| `@PutMapping` | Shortcut for HTTP PUT | `@PutMapping("/{id}")` |
| `@DeleteMapping` | Shortcut for HTTP DELETE | `@DeleteMapping("/{id}")` |
| `@PatchMapping` | Shortcut for HTTP PATCH | `@PatchMapping("/{id}")` |

### Dependency Injection
| Annotation | Purpose | Details |
|------------|---------|---------|
| `@Autowired` | Injects dependencies | Constructor injection preferred over field injection |
| `@Qualifier` | Disambiguate beans when multiple candidates | `@Qualifier("userService") private UserService userService;` |
| `@Primary` | Primary bean when multiple candidates exist | `@Bean @Primary public DataSource dataSource() {}` |
| `@Scope` | Bean scope: singleton, prototype, request, session, application | `@Scope(value = "prototype")` |
| `@Value` | Injects values from property sources | `@Value("${app.max-connections:10}") private int maxConnections;` |
| `@ConfigurationProperties` | Maps properties to structured objects | `@ConfigurationProperties(prefix = "app.datasource")` |

### Data Access
| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Entity` | Marks class as JPA entity | `@Entity @Table(name = "users")` |
| `@Table` | Specifies the database table | `@Table(name = "users", schema = "public")` |
| `@Id` | Marks primary key field | `@Id @GeneratedValue(strategy = GenerationType.UUID)` |
| `@GeneratedValue` | Primary key generation strategy | `@GeneratedValue(strategy = GenerationType.SEQUENCE)` |
| `@Column` | Column mapping with constraints | `@Column(name = "email", nullable = false, unique = true)` |
| `@Transient` | Field not persisted | `@Transient private int computedValue;` |
| `@OneToMany` | One-to-many relationship | `@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)` |
| `@ManyToOne` | Many-to-one relationship | `@ManyToOne(fetch = FetchType.LAZY)` |
| `@JoinColumn` | Foreign key column | `@JoinColumn(name = "user_id", nullable = false)` |
| `@Query` | Custom JPQL/SQL query | `@Query("SELECT u FROM User u WHERE u.email = :email")` |
| `@Modifying` | Indicates modifying (DML) query | `@Modifying @Query("UPDATE User u SET u.status = :status")` |
| `@Transactional` | Declarative transaction management | `@Transactional(readOnly = true, timeout = 30)` |
| `@EntityGraph` | Fetch graph for query optimization | `@EntityGraph(attributePaths = {"orders"})` |

### Transaction Management
| Annotation | Purpose | Attributes |
|------------|---------|------------|
| `@Transactional` | Transaction boundary | `propagation`, `isolation`, `timeout`, `readOnly`, `rollbackFor` |
| `@Transactional(propagation = Propagation.REQUIRED)` | Join existing transaction or create new | Default |
| `@Transactional(propagation = Propagation.REQUIRES_NEW)` | Always create new transaction | Suspend current |
| `@Transactional(propagation = Propagation.NESTED)` | Nested transaction (savepoint) | Uses actual nested transactions |
| `@Transactional(isolation = Isolation.SERIALIZABLE)` | Highest isolation level | Strongest consistency, worst performance |

### Caching
| Annotation | Purpose | Example |
|------------|---------|---------|
| `@EnableCaching` | Enable caching support | `@EnableCaching @SpringBootApplication` |
| `@Cacheable` | Method result is cached | `@Cacheable(value = "users", key = "#id")` |
| `@CachePut` | Updates cache with method result | `@CachePut(value = "users", key = "#user.id")` |
| `@CacheEvict` | Removes entries from cache | `@CacheEvict(value = "users", allEntries = true)` |
| `@Caching` | Group multiple cache annotations | `@Caching(put = {...}, evict = {...})` |

### Validation
| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Valid` | Triggers validation on method parameter | `@Valid @RequestBody UserCreateRequest request` |
| `@Validated` | Group validation | `@Validated(CreateGroup.class)` |
| `@NotNull` | Value must not be null | `@NotNull(message = "Email is required")` |
| `@NotEmpty` | String/collection must not be empty | `@NotEmpty(message = "Name must not be empty")` |
| `@NotBlank` | String must not be blank | `@NotBlank` |
| `@Size` | Length/size constraints | `@Size(min = 3, max = 100)` |
| `@Min` / `@Max` | Numeric range | `@Min(0) @Max(1000)` |
| `@Pattern` | Regex pattern validation | `@Pattern(regexp = "^[A-Za-z0-9]+$")` |
| `@Email` | Email format validation | `@Email(message = "Invalid email format")` |
| `@Positive` / `@PositiveOrZero` | Positive number validation | `@PositiveOrZero` |

### Security
| Annotation | Purpose | Example |
|------------|---------|---------|
| `@EnableWebSecurity` | Enable Spring Security | `@Configuration @EnableWebSecurity` |
| `@EnableMethodSecurity` | Enable method-level security | `@EnableMethodSecurity(securedEnabled = true)` |
| `@PreAuthorize` | Method security with SpEL | `@PreAuthorize("hasRole('ADMIN')")` |
| `@PostAuthorize` | Check after method execution | `@PostAuthorize("returnObject.owner == authentication.name")` |
| `@Secured` | Role-based authorization | `@Secured({"ROLE_USER", "ROLE_ADMIN"})` |
| `@RolesAllowed` | JSR-250 role annotation | `@RolesAllowed("ADMIN")` |
| `@AuthenticationPrincipal` | Injects authenticated principal | `@AuthenticationPrincipal UserDetails user` |

### Scheduling and Async
| Annotation | Purpose | Example |
|------------|---------|---------|
| `@EnableScheduling` | Enable scheduled tasks | `@EnableScheduling @SpringBootApplication` |
| `@Scheduled` | Method runs on schedule | `@Scheduled(cron = "0 0 * * * *")` |
| `@EnableAsync` | Enable async method execution | `@EnableAsync @SpringBootApplication` |
| `@Async` | Method runs asynchronously | `@Async @EventListener` |
| `@EventListener` | Event-driven architecture | `@EventListener @Async` |

### Testing
| Annotation | Purpose | Example |
|------------|---------|---------|
| `@SpringBootTest` | Integration test with full context | `@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)` |
| `@WebMvcTest` | Web layer test only | `@WebMvcTest(UserController.class)` |
| `@DataJpaTest` | JPA repository test | `@DataJpaTest @AutoConfigureTestDatabase(replace = NONE)` |
| `@MockBean` | Mock bean in Spring context | `@MockBean private UserService userService;` |
| `@TestConfiguration` | Test-specific configuration | `@TestConfiguration static class TestConfig {}` |

---

## REST API Design Best Practices

### URL Structure
```
GET    /api/v1/users              — List users (paginated)
POST   /api/v1/users              — Create user
GET    /api/v1/users/{id}         — Get user by ID
PUT    /api/v1/users/{id}         — Full update (replace entire resource)
PATCH  /api/v1/users/{id}         — Partial update
DELETE /api/v1/users/{id}         — Delete user
```

### Query Parameters
| Parameter | Purpose | Example |
|-----------|---------|---------|
| `page` | Page number (0-indexed) | `?page=0` |
| `size` | Page size (default 20) | `?size=50` |
| `sort` | Sort by field | `?sort=createdAt,desc` |
| `filter` | Field-based filtering | `?filter=status:active` |
| `fields` | Sparse field selection | `?fields=id,name,email` |
| `search` | Full-text search | `?search=john+doe` |

### HTTP Status Codes
| Code | Meaning | When to Use |
|------|---------|-------------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | Created | Successful POST (new resource) |
| 204 | No Content | Successful DELETE |
| 400 | Bad Request | Validation errors, malformed request |
| 401 | Unauthorized | Missing or invalid authentication |
| 403 | Forbidden | Authenticated but not authorized |
| 404 | Not Found | Resource does not exist |
| 409 | Conflict | Resource state conflict (duplicate, stale) |
| 422 | Unprocessable Entity | Business rule violation |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Unexpected server error |
| 503 | Service Unavailable | Service temporarily unavailable |

### Pagination Response Format
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 1000,
  "totalPages": 50,
  "last": false,
  "first": true,
  "sort": { "sorted": true, "unsorted": false }
}
```

### Error Response Format
```json
{
  "type": "https://api.example.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "The request body contains invalid fields.",
  "instance": "/api/v1/users",
  "timestamp": "2025-01-01T12:00:00Z",
  "errors": [
    { "field": "email", "message": "must be a well-formed email address" },
    { "field": "age", "message": "must be a positive integer" }
  ]
}
```

---

## Microservice Patterns Quick Reference

### Communication Patterns
| Pattern | Protocol | Use Case | Pros | Cons |
|---------|----------|----------|------|------|
| REST | HTTP/1.1, HTTP/2 | Simple CRUD | Universal, simple | Over-fetching, chatty |
| gRPC | HTTP/2 | High-performance internal | Fast, typed, streaming | Limited browser support |
| GraphQL | HTTP/1.1, HTTP/2 | Complex data requirements | Precise queries, no over-fetching | Caching complexity |
| Message Queue | AMQP, Kafka | Async, event-driven | Decoupled, reliable, buffering | Added latency, complexity |
| WebSocket | WS, WSS | Real-time, bidirectional | Full-duplex, low-latency | Connection management |

### Resilience Patterns
| Pattern | Library | When to Use |
|---------|---------|-------------|
| Circuit Breaker | Resilience4J | Protect against cascading failures |
| Retry | Spring Retry, Resilience4J | Transient failures, network issues |
| Timeout | `@Transactional(timeout=...)`, Resilience4J | Prevent hung connections |
| Bulkhead | Resilience4J | Isolate failures by resource pool |
| Rate Limiter | Bucket4j, Resilience4J | Protect backend from overload |
| Cache-Aside | Spring Cache | Reduce database load |
| Fallback | Resilience4J @Fallback | Graceful degradation |

### Observability Patterns
| Component | Tool | Purpose |
|-----------|------|---------|
| Distributed Tracing | Micrometer Tracing, OpenTelemetry | Trace requests across services |
| Metrics | Micrometer + Prometheus | CPU, memory, latency, throughput |
| Logging | Logback/Log4j2 + ELK/Loki | Structured logging, aggregation |
| Health Checks | Spring Actuator | Readiness + liveness probes |

---

## Database Optimization Cheat Sheet

### Indexing Strategies
| Index Type | When to Use | Syntax |
|------------|-------------|--------|
| B-tree (default) | Equality + range queries | `CREATE INDEX idx_name ON table(col)` |
| Composite | Multi-column queries | `CREATE INDEX idx_a_b ON table(a, b)` |
| Covering | Index-only scans | Include extra columns in index |
| Partial | Sparse data queries | `CREATE INDEX idx_active ON table(status) WHERE status = 'ACTIVE'` |
| Hash | Equality only (PostgreSQL) | `CREATE INDEX idx_hash ON table USING HASH(col)` |
| GiST | Full-text, geometric (PG) | `CREATE INDEX idx_fts ON table USING GIST(to_tsvector('english', col))` |

### Query Optimization Quick Reference
| Problem | Symptom | Solution |
|---------|---------|----------|
| Full table scan | Sequential scan in plan | Add index, rewrite query |
| N+1 queries | Multiple SELECTs in loop | `@EntityGraph`, JOIN FETCH, batch size |
| Missing index | Sequential scan on WHERE | Analyze query, add index |
| Outdated statistics | Bad plan | ANALYZE / DBMS_STATS |
| Slow pagination | OFFSET with large value | Keyset/cursor pagination |
| Lock contention | Frequent deadlocks | Reduce transaction scope, index foreign keys |

### Connection Pooling (HikariCP)
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.leak-detection-threshold=60000
```

---

## Caching Strategies

| Strategy | Cache Hit Pattern | Staleness | Best For |
|----------|------------------|-----------|----------|
| Cache-Aside | Lazy load on miss | TTL-based | Read-heavy, moderate consistency |
| Read-Through | Cache handles DB load | TTL-based | Uniform data access |
| Write-Through | Cache + DB updated together | Strong consistency | Write-heavy, high consistency |
| Write-Behind | Cache updated, DB async | Eventual consistency | Write-heavy, tolerate lag |
| Refresh-Ahead | Proactive refresh before expiry | Predictable stale window | Predictable access patterns |

### Redis Data Types for Caching
| Data Type | Use Case | Example |
|-----------|----------|---------|
| String | Simple key-value, counters | Session, HTML fragments |
| Hash | Object representation | Cached DTOs, user profiles |
| List | Queue, timeline, recent items | Recent activity feed |
| Set | Unique items, tags | User permissions, categories |
| Sorted Set | Ranked data, leaderboards | Top products, trending topics |
| HyperLogLog | Cardinality estimation | Unique visitors count |

---

## Security Patterns (OAuth2, JWT, RBAC)

### OAuth2 Grant Types
| Grant Type | Use Case | Security |
|------------|----------|----------|
| Authorization Code | Server-side web apps | Most secure, uses PKCE |
| Client Credentials | Server-to-server | Service account authentication |
| Refresh Token | Long-lived access | Allows token rotation |
| Device Code | CLI, IoT devices | Limited input devices |

### JWT Structure
```
BASE64URL(Header) + "." + BASE64URL(Payload) + "." + BASE64URL(Signature)

Header: { "alg": "RS256", "typ": "JWT", "kid": "key-id-1" }
Payload: { "sub": "user123", "iss": "https://auth.example.com", 
           "aud": "api.example.com", "exp": 1735689600,
           "iat": 1735603200, "roles": ["admin", "user"] }
Signature: RSASHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload))
```

### Spring Security Filter Chain Order
1. SecurityContextPersistenceFilter
2. HeaderWriterFilter
3. CorsFilter
4. CsrfFilter
5. LogoutFilter
6. OAuth2AuthorizationRequestRedirectFilter
7. UsernamePasswordAuthenticationFilter
8. ConcurrentSessionFilter
9. RememberMeAuthenticationFilter
10. AnonymousAuthenticationFilter
11. ExceptionTranslationFilter
12. FilterSecurityInterceptor

### RBAC vs ABAC
| Aspect | RBAC | ABAC |
|--------|------|------|
| Model | Roles assigned to users | Attributes (user, resource, environment) |
| Flexibility | Low — role explosion risk | High — fine-grained policies |
| Performance | Fast (pre-computed roles) | Slower (policy evaluation) |
| Complexity | Simple to implement | Complex policy management |
| Example | `hasRole('ADMIN')` | `hasPermission(#document, 'EDIT')` |

---

## Testing Strategies for Backend

### Test Pyramid
```
         /\
        /  \
       / E2E\
      /------\
     /   API  \
    /  (Service)\
   /--------------\
  /   Integration   \
 /   (Repository)    \
/----------------------\
/     Unit (Service)     \
/------------------------\
/   Unit (Controller)     \
/--------------------------\
```

### Test Types Quick Reference
| Test | Scope | Speed | What to Mock | Framework |
|------|-------|-------|--------------|-----------|
| Unit (Service) | Single service method | Fast | All dependencies | JUnit 5 + Mockito |
| Unit (Controller) | Controller method | Fast | Service layer | `@WebMvcTest` |
| Integration (Repository) | DB interaction | Medium | External services | `@DataJpaTest`, Testcontainers |
| Integration (Service) | Full service with DB | Medium | External APIs | `@SpringBootTest` |
| E2E | Full request-response | Slow | Nothing | `@SpringBootTest(webEnvironment=RANDOM_PORT)` |
| Contract | Consumer-driven pacts | Fast | Provider stub | Spring Cloud Contract, Pact |

### Mockito Essentials
```java
// Setup
when(service.findById(any())).thenReturn(Optional.of(user));
when(service.findById(any())).thenThrow(new NotFoundException());

// Verification
verify(service, times(1)).save(any());
verify(service, never()).delete(any());
verifyNoMoreInteractions(service);

// Argument Matching
verify(service).save(argThat(u -> u.getEmail().equals("test@test.com")));

// Spy (partial mock)
@SpyBean private UserService userService;
```

### Testcontainers Quick Reference
```java
// PostgreSQL
@DynamicPropertySource
static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
}

// Kafka
@Container
static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

// Redis
@Container
static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);
```

---

<div align="center">

**"Interviews test how you think, not what you know. Show your process."**

---

[Back to Top](#backend-academy--interview-cheatsheet)

</div>
