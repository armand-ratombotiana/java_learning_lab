# TDD Interview Guide — Wave 6

> Target: 300+ lines covering TDD principles, testing in Spring Boot, company-specific approaches, interview questions with code

---

## 1. TDD Principles

### Q: What is the Red-Green-Refactor cycle?

**Answer:**
The core TDD loop:
```
🔴 RED: Write a failing test first (defines expected behavior)
🟢 GREEN: Write minimal code to make the test pass
🔵 REFACTOR: Improve code without changing behavior
🔴 RED: Write next failing test...
```

```java
// Phase 1 — RED: Write failing test
class ShoppingCartTest {
    @Test
    void shouldCalculateTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.addItem(new Item("Book", 10.0));
        cart.addItem(new Item("Pen", 2.0));
        assertThat(cart.getTotal()).isEqualTo(12.0);
    }
}

// Phase 2 — GREEN: Minimal implementation
public class ShoppingCart {
    private double total = 0;
    public void addItem(Item item) { this.total += item.price(); }
    public double getTotal() { return total; }
}

// Phase 3 — REFACTOR: Improve without changing behavior
public class ShoppingCart {
    private final List<Item> items = new ArrayList<>();
    public void addItem(Item item) { items.add(item); }
    public double getTotal() {
        return items.stream().mapToDouble(Item::price).sum();
    }
}
```

**Company Frequency:** All companies (universal principle)

**Follow-ups:**
- What if the pass phase leads to fragile tests?
- How much refactoring is too much before writing the next test?

---

### Q: FIRST principles of unit testing

**Answer:**

| Letter | Principle | Meaning | Violation Example |
|--------|-----------|---------|-------------------|
| **F** | Fast | Tests run quickly (ms) | Tests that hit database or network |
| **I** | Independent | No test depends on another | Shared static state, test order dependencies |
| **R** | Repeatable | Same result every run | Random data, time-dependent, flaky network |
| **S** | Self-validating | Boolean pass/fail, no manual check | Tests that print output for human review |
| **T** | Timely | Written before/with code | Tests written weeks after production code |

```java
// BAD — violates FIRST
class BadTest {
    private static int counter = 0; // I: shared state

    @Test
    void testOne() {
        String result = callExternalAPI(); // F: slow, R: flaky
        System.out.println("Result: " + result); // S: manual check
        counter++;
    }
}

// GOOD — follows FIRST
class GoodTest {
    @Test
    void shouldReturnDiscountedPrice() {
        Product product = new Product("Widget", 100.0);
        double price = product.applyDiscount(0.1);
        assertThat(price).isEqualTo(90.0);
    }
}
```

**Company Frequency:** Google (high), Amazon (high), all companies

---

### Q: Test pyramid

**Answer:**

```
         ╱╲
        ╱  ╲         E2E (few)
       ╱    ╲
      ╱______╲
     ╱        ╲      Integration (some)
    ╱          ╲
   ╱____________╲
  ╱              ╲
 ╱                ╲  Unit (many)
╱__________________╲
```

| Layer | Count | Speed | Scope | Example |
|-------|-------|-------|-------|---------|
| **Unit** | Many (1000s) | ms | Single class/method | `ShoppingCartTest` |
| **Integration** | Some (100s) | s | Component + infrastructure | `@DataJpaTest`, `@WebMvcTest` |
| **Contract** | Few (10s) | s | API contract between services | Pact, Spring Cloud Contract |
| **E2E** | Few (10s) | min | Full system | `@SpringBootTest` + browser |

**Spring Boot testing pyramid mapping:**
```
Unit: plain JUnit 5 + Mockito (no Spring context)
Contract: @JsonTest, @RestClientTest
Integration: @WebMvcTest, @DataJpaTest, @SpringBootTest
```

**Company Frequency:** Google (they popularized), Amazon (adaptation), all companies

**Follow-ups:**
- When to break the pyramid? (More E2E for critical paths, more contract tests for distributed systems)
- What is the "ice cream cone" anti-pattern?

---

## 2. Testing in Spring Boot

### Q: @SpringBootTest with sliced contexts

**Answer:**

```java
// Full context — loads everything
@SpringBootTest
class OrderServiceIntegrationTest {
    @Autowired
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        Order order = orderService.createOrder(new CreateOrderRequest("customer-1"));
        assertThat(order.getId()).isNotNull();
    }
}

// Sliced: Web layer only
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;

    @Test
    void shouldReturnOrder() throws Exception {
        given(orderService.findById(1L)).willReturn(new Order(1L, "customer-1"));

        mockMvc.perform(get("/orders/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.customerId").value("customer-1"));
    }
}

// Sliced: JPA only
@DataJpaTest
class OrderRepositoryTest {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private TestEntityManager em;

    @Test
    void shouldFindByCustomer() {
        em.persist(new Order("customer-1"));
        List<Order> orders = orderRepository.findByCustomerId("customer-1");
        assertThat(orders).hasSize(1);
    }
}

// Sliced: JSON serialization only
@JsonTest
class OrderJsonTest {
    @Autowired
    private JacksonTester<Order> json;

    @Test
    void shouldSerialize() throws Exception {
        Order order = new Order(1L, "customer-1");
        assertThat(json.write(order)).isEqualToJson("expected-order.json");
    }
}
```

**What each slice loads:**

| Slice | Beans Loaded |
|-------|-------------|
| `@WebMvcTest` | Controllers, filters, Jackson, validation |
| `@DataJpaTest` | JPA repos, EntityManager, DataSource |
| `@JsonTest` | Jackson/Gson ObjectMapper |
| `@RestClientTest` | RestTemplate builder, Jackson |
| `@JdbcTest` | JdbcTemplate, DataSource |

**Company Frequency:** All companies (universal)

---

### Q: @MockBean, @SpyBean, @MockitoBean (Spring Boot 3.4+)

**Answer:**

```java
// Spring Boot 3.4 introduces @MockitoBean and @MockitoSpyBean
// replacing @MockBean and @SpyBean

// Pre-3.4
@SpringBootTest
class OrderServiceTest {
    @MockBean
    private PaymentClient paymentClient;

    @MockBean
    private InventoryClient inventoryClient;
}

// 3.4+ (uses Mockito's stricter mocking)
@SpringBootTest
class OrderServiceTest {
    @MockitoBean
    private PaymentClient paymentClient;

    @MockitoBean
    private InventoryClient inventoryClient;

    @Test
    void shouldCreateOrder() {
        given(paymentClient.authorize(any())).willReturn(true);
        given(inventoryClient.reserve(any())).willReturn(true);

        Order order = orderService.createOrder(new OrderRequest());

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(paymentClient).authorize(any());
        verify(inventoryClient).reserve(any());
    }
}

// @SpyBean / @MockitoSpyBean — partial mock
@SpringBootTest
class AuditServiceTest {
    @SpyBean
    private AuditService auditService;

    @Test
    void shouldAuditOrderCreation() {
        doNothing().when(auditService).sendToExternalSystem(any());

        orderService.createOrder(new OrderRequest());

        verify(auditService).logEvent("ORDER_CREATED");
    }
}
```

**Difference @MockBean vs @MockitoBean:**
- `@MockBean` registers a Mockito mock in the ApplicationContext (may conflict with `@Primary`)
- `@MockitoBean` (3.4+) uses Mockito's `@Mock` annotation with stricter stubbing, better error messages
- Both replace existing beans in context

**Company Frequency:** All companies (universal)

---

### Q: MockMvc vs WebTestClient vs TestRestTemplate

**Answer:**

```java
// MockMvc — server-side mock (fastest, no real HTTP)
@WebMvcTest(OrderController.class)
class OrderControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldGetOrder() throws Exception {
        // Do NOT test with @SpringBootTest — MockMvc cannot run on random port
        mockMvc.perform(get("/orders/1"))
               .andExpect(status().isOk())
               .andExpect(content().contentType(MediaType.APPLICATION_JSON))
               .andDo(print());
    }
}

// TestRestTemplate — real HTTP client, real server
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderControllerRestTemplateTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldGetOrder() {
        ResponseEntity<Order> response = restTemplate
            .getForEntity("/orders/1", Order.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

// WebTestClient — reactive, works with both mock and real servers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class OrderControllerWebTestClientTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void shouldGetOrder() {
        webClient.get().uri("/orders/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.customerId").isEqualTo("customer-1");
    }
}

// WebTestClient can also bind to controller (like MockMvc)
@WebFluxTest(OrderController.class)
class OrderControllerWebfluxTest {
    @Autowired
    private WebTestClient webClient;

    @Test
    void shouldGetOrder() {
        webClient.get().uri("/orders/1")
            .exchange()
            .expectStatus().isOk();
    }
}
```

**Company Frequency:** All companies (universal)

---

### Q: Testcontainers for integration testing

**Answer:**

```java
@SpringBootTest
@Testcontainers
class OrderRepositoryTest {

    // Single container
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    // Multiple containers — use @Testcontainers + static containers
    @Container
    static KafkaContainer kafka = new KafkaContainer(
        DockerImageName.parse("confluentinc/cp-kafka:7.5"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPersistAndRetrieveOrder() {
        Order order = orderRepository.save(new Order("customer-1", 100.0));
        assertThat(order.getId()).isNotNull();

        Optional<Order> found = orderRepository.findById(order.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo("customer-1");
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldQueryCustomNativeQuery() {
        orderRepository.save(new Order("customer-1", 50.0));
        orderRepository.save(new Order("customer-1", 150.0));

        List<Order> bigOrders = orderRepository
            .findByCustomerIdAndAmountGreaterThan("customer-1", 100.0);
        assertThat(bigOrders).hasSize(1);
    }
}
```

**Testcontainers modules:**
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>kafka</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

**Company Frequency:** Netflix (high), Google (high), all modern teams

**Follow-ups:**
- Testcontainers vs H2 for database testing?
- How to share containers across test classes?

---

## 3. Company-Specific Testing Approaches

### Q: [Google] Mocks vs Fakes vs In-memory implementations

**Answer:**
Google distinguishes between:

| Type | What It Is | When to Use | Example |
|------|-----------|-------------|---------|
| **Mock** | Verifies behavior (interactions) | Testing interactions between objects | `Mockito.mock()` |
| **Fake** | Lightweight working implementation | Testing business logic without real infrastructure | In-memory database, fake payment gateway |
| **Stub** | Returns canned answers | Simple test setup | `given(repo.find(id)).willReturn(user)` |
| **In-memory** | Full implementation in memory | Integration testing without heavy dependencies | H2, FakeS3, LocalStack |

**Google's preference:**
- **Fakes over mocks** for most testing (less brittle, more behavior-focused)
- **In-memory implementations** for infrastructure (FakeClock, InMemoryBlobStore)
- **Real implementations** with Testcontainers for integration tests
- **Mocks** only at system boundaries (network calls)

```java
// Fake implementation — lightweight, testable
public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong();

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idGen.incrementAndGet());
        }
        store.put(order.getId(), order);
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        return store.values().stream()
            .filter(o -> o.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
}

// Test with fake — fast, no Spring context needed
class OrderServiceTest {
    private final InMemoryOrderRepository repo = new InMemoryOrderRepository();
    private final OrderService service = new OrderService(repo);

    @Test
    void shouldCreateOrder() {
        Order order = service.createOrder(new CreateOrderRequest("customer-1"));
        assertThat(repo.findById(order.getId())).isPresent();
    }
}
```

---

### Q: [Amazon] "You build it, you test it"

**Answer:**
Amazon's testing philosophy:

1. **Every service has tests before deployment** — no exceptions
2. **Developers write tests, not QA** (QA focuses on exploratory/integration)
3. **Testing in production** (Canary deployments, metrics-driven validation)
4. **Chaos engineering** (Chaos Monkey inspired by Amazon's early practices)

**Amazon-specific patterns:**
```java
// Testing fault tolerance — every service must handle failure
@SpringBootTest
class OrderServiceFaultToleranceTest {
    @MockBean
    private PaymentClient paymentClient;

    @Test
    void shouldHandlePaymentTimeout() {
        given(paymentClient.authorize(any()))
            .willThrow(new TimeoutException("Payment timed out"));

        OrderResult result = orderService.createOrder(new OrderRequest());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        assertThat(result.getCompensationActions())
            .contains(CompensationAction.RELEASE_INVENTORY);
    }

    @Test
    void shouldHandlePaymentRetrySuccess() {
        given(paymentClient.authorize(any()))
            .willThrow(new ServiceUnavailableException())
            .willThrow(new ServiceUnavailableException())
            .willReturn(true);

        OrderResult result = orderService.createOrder(new OrderRequest());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
```

---

### Q: [Netflix] Chaos testing

**Answer:**
Netflix pioneered chaos engineering with **Chaos Monkey** (randomly kills production instances) and **Chaos Kong** (simulates AWS region failure).

**Testing resilience:**
```java
@SpringBootTest
@Testcontainers
class OrderServiceChaosTest {

    @Container
    static ToxiproxisContainer toxiproxy = new ToxiproxisContainer("ghcr.io/shopify/toxiproxy:2.5");

    @DynamicPropertySource
    static void config(DynamicPropertyRegistry reg) {
        reg.add("payment.service.url", () ->
            "http://" + toxiproxy.getHost() + ":" + toxiproxy.getMappedPort(8666));
    }

    @Test
    void shouldToleratePaymentLatency() {
        // Use Toxiproxy to inject latency
        Proxy paymentProxy = toxiproxy.getProxy("payment-service");
        Toxic latency = paymentProxy.toxics().latency("latency", ToxicDirection.DOWNSTREAM, 5000);

        OrderResult result = orderService.createOrder(new OrderRequest());

        // Service should degrade gracefully, not crash
        assertThat(result.getStatus()).isIn(
            OrderStatus.PAYMENT_FAILED, OrderStatus.PENDING_RETRY);

        latency.remove();
    }
}
```

**Netflix testing pyramid:**
```
Production Testing (canary)
├── Automated Canary Analysis (Kayenta)
├── Chaos Experiments (Monkey, Kong)
├── Integration Tests (real infra via Testcontainers)
├── Contract Tests (Pact)
└── Unit Tests (core logic)
```

---

## 4. TDD Interview Questions

### Q: What is TDD and when should you NOT use it?

**Answer:**
TDD (Test-Driven Development): Write tests before production code following Red-Green-Refactor.

**When NOT to use TDD:**
1. **Prototyping/exploratory code** — unknown requirements, high churn
2. **One-time scripts** — data migration, cleanup jobs
3. **UI-heavy applications** — testing complex UI interactions is expensive (but use for business logic)
4. **Legacy code without tests** — add tests gradually, not via TDD rewrite
5. **When requirements are completely unknown** — validate concept first, then TDD

**When ALWAYS use TDD:**
- Business logic / domain rules
- Financial calculations
- Data transformations
- API endpoints (spec-first)

---

### Q: How to test code that depends on external services?

**Answer:**

```java
// Layer 1: Test with mock
@WebMvcTest(OrderController.class)
class OrderControllerTest {
    @MockBean
    private OrderService orderService;

    @Test
    void shouldCreateOrder() {
        given(orderService.createOrder(any()))
            .willReturn(new Order(1L, "customer-1"));

        mockMvc.perform(post("/orders")
                .contentType(JSON)
                .content("{\"customerId\": \"customer-1\"}"))
               .andExpect(status().isCreated());
    }
}

// Layer 2: Test with WireMock (HTTP client test)
@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
class OrderServiceWireMockTest {
    @Test
    void shouldHandlePaymentService() {
        stubFor(post(urlEqualTo("/payment/authorize"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("{\"authorized\": true}")));

        Order result = orderService.createOrder(new OrderRequest());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}

// Layer 3: Test with Testcontainers
@SpringBootTest
@Testcontainers
class OrderServiceIntegrationTest {
    @Container
    static GenericContainer<?> paymentService = new GenericContainer<>("payment-service:latest")
        .withExposedPorts(8080);

    @Test
    void shouldWorkWithRealPaymentService() {
        // Hit real payment service in container
        Order result = orderService.createOrder(new OrderRequest());
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
```

---

### Q: How to test asynchronous/reactive code?

**Answer:**

```java
// Testing reactive code with StepVerifier (Project Reactor)
class ReactiveOrderServiceTest {
    private final ReactiveOrderService service = new ReactiveOrderService();

    @Test
    void shouldCreateOrder() {
        Mono<Order> result = service.createOrder(new CreateOrderRequest("customer-1"));

        StepVerifier.create(result)
            .assertNext(order -> {
                assertThat(order.getId()).isNotNull();
                assertThat(order.getCustomerId()).isEqualTo("customer-1");
            })
            .verifyComplete();
    }

    @Test
    void shouldHandleError() {
        Mono<Order> result = service.createOrder(new CreateOrderRequest(null));

        StepVerifier.create(result)
            .expectError(ValidationException.class)
            .verify();
    }

    @Test
    void shouldEmitMultipleEvents() {
        Flux<String> events = service.streamOrderEvents("order-1");

        StepVerifier.create(events)
            .expectNext("ORDER_CREATED")
            .expectNext("PAYMENT_PROCESSED")
            .expectNext("ORDER_CONFIRMED")
            .thenCancel()
            .verify();
    }

    @Test
    void shouldTimeoutOnSlowService() {
        Mono<Order> slow = service.createOrder(new CreateOrderRequest("customer-1"));

        StepVerifier.create(slow)
            .expectTimeout(Duration.ofSeconds(5))
            .verify();
    }
}

// Testing async with Awaitility (CompletableFuture/Kafka)
class AsyncOrderServiceTest {
    @Test
    void shouldProcessAsyncOrder() {
        CompletableFuture<Order> future = orderService.createOrderAsync(new OrderRequest());

        await().atMost(5, TimeUnit.SECONDS)
            .until(future::isDone);

        assertThat(future.get().getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void shouldHandleKafkaEvent() {
        // Send event
        kafkaTemplate.send("orders", new OrderCreatedEvent("order-1"));

        // Wait for async processing
        await().atMost(10, TimeUnit.SECONDS)
            .until(() -> orderRepository.findById("order-1").isPresent());

        Order processed = orderRepository.findById("order-1").get();
        assertThat(processed.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
```

---

### Q: How to test database interactions?

**Answer:**

```java
// 1. @DataJpaTest + H2 (fast, limited)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class OrderRepositoryH2Test {
    @Autowired
    private OrderRepository repository;

    @Test
    void shouldSaveAndFind() {
        Order saved = repository.save(new Order("customer-1", 100.0));
        assertThat(repository.findById(saved.getId())).isPresent();
    }
}

// 2. @DataJpaTest + Testcontainers (real PostgreSQL)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class OrderRepositoryPostgresTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void config(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", postgres::getJdbcUrl);
        reg.add("spring.datasource.username", postgres::getUsername);
        reg.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderRepository repository;

    @Test
    void shouldExecuteCustomQuery() {
        repository.save(new Order("customer-1", 50.0));
        repository.save(new Order("customer-1", 200.0));

        List<Order> bigOrders = repository
            .findByCustomerIdAndAmountGreaterThan("customer-1", 100.0);
        assertThat(bigOrders).hasSize(1);
        assertThat(bigOrders.get(0).getAmount()).isEqualTo(200.0);
    }
}

// 3. @SpringBootTest + @Transactional (rollback after each test)
@SpringBootTest
@Transactional
class OrderServiceDatabaseTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldCreateOrderInDatabase() {
        orderService.createOrder(new CreateOrderRequest("customer-1"));

        List<Order> orders = orderRepository.findByCustomerId("customer-1");
        assertThat(orders).hasSize(1);
    }
}
```

---

### Q: How to test microservices communication?

**Answer:**

```java
// 1. Contract testing with Spring Cloud Contract
// Producer side (Order Service)
// src/test/resources/contracts/shouldReturnOrder.groovy
Contract.make {
    description "should return order by ID"
    request {
        method GET()
        url "/orders/1"
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
            id: 1,
            customerId: "customer-1",
            status: "CONFIRMED"
        ])
    }
}

// Consumer side (Payment Service) — stub generated from contract
// @AutoConfigureStubRunner downloads and starts stub

// 2. Pact contract testing
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "OrderService", port = "8080")
class PaymentServicePactTest {

    @Pact(consumer = "PaymentService")
    public V4Pact createPact(PactDslWithProvider builder) {
        return builder
            .given("order 1 exists")
            .uponReceiving("a request for order 1")
                .path("/orders/1")
                .method("GET")
            .willRespondWith()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new PactDslJsonBody()
                    .integerType("id", 1)
                    .stringType("customerId", "customer-1")
                    .stringType("status", "CONFIRMED"))
            .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void shouldProcessPaymentForValidOrder() {
        Order order = restTemplate.getForObject("/orders/1", Order.class);
        assertThat(order.getStatus()).isEqualTo("CONFIRMED");

        PaymentResult result = paymentService.process(order);
        assertThat(result.isApproved()).isTrue();
    }
}

// 3. WireMock for provider testing
@SpringBootTest
@WireMockTest(httpPort = 8089)
class OrderServiceWireMockTest {

    @Test
    void shouldHandlePaymentServiceDown() {
        stubFor(post(urlEqualTo("/payment/authorize"))
            .willReturn(aResponse()
                .withStatus(503)));

        OrderResult result = orderService.createOrder(new OrderRequest());

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        verify(exactly(3), postRequestedFor(urlEqualTo("/payment/authorize")));
    }
}
```

---

## 5. Advanced Testing Topics

### Q: @Sql and @SqlGroup — database test setup

**Answer:**

```java
@SpringBootTest
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SqlGroup({
    @Sql(scripts = "/sql/setup-orders.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class OrderServiceSqlTest {

    @Autowired
    private OrderService orderService;

    @Test
    void shouldProcessExistingOrders() {
        // /sql/setup-orders.sql inserts test orders
        List<Order> processed = orderService.processPendingOrders();
        assertThat(processed).hasSize(3);
    }

    @Test
    @Sql("/sql/special-setup.sql") // additional per-test setup
    void shouldHandleSpecialCase() {
        // Custom setup just for this test
    }
}
```

**SQL script features:**
```sql
-- /sql/setup-orders.sql
INSERT INTO orders (id, customer_id, status, amount)
VALUES
    (100, 'customer-1', 'PENDING', 50.00),
    (101, 'customer-2', 'PENDING', 150.00),
    (102, 'customer-1', 'CONFIRMED', 200.00);

ALTER SEQUENCE orders_seq RESTART WITH 200;
```

---

### Q: TestTransaction — programmatic transaction control in tests

**Answer:**

```java
@SpringBootTest
class TestTransactionDemo {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldDemonstrateTestTransaction() {
        // TestTransaction.withinOrFail() — ensure test runs in transaction

        // Start transaction
        TestTransaction.start();

        orderRepository.save(new Order("customer-1", 100.0));
        assertThat(orderRepository.findByCustomerId("customer-1")).hasSize(1);

        // Rollback — data not persisted
        TestTransaction.rollback();

        assertThat(orderRepository.findByCustomerId("customer-1")).isEmpty();

        // Start a new transaction
        TestTransaction.start();
        orderRepository.save(new Order("customer-2", 200.0));
        TestTransaction.end();
    }

    @Test
    @Transactional
    void shouldUseTransactionBoundary() {
        orderRepository.save(new Order("customer-1", 100.0));

        // Flag for rollback manually
        TestTransaction.flagForRollback();
        // At method end, transaction rolls back automatically
    }
}
```

---

> **End of TDD_INTERVIEW_GUIDE.md**
> Total content: ~350+ lines covering principles, Spring Boot testing, company-specific approaches, interview questions
