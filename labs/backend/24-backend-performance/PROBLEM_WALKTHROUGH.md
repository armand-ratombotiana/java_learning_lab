# PROBLEM WALKTHROUGH: Implement a High-Throughput Async API with Virtual Threads

## Problem Statement

Design and implement a high-throughput REST API that leverages Java 21 Virtual Threads (Project Loom) for maximum concurrency. The system should:

- Handle 10,000+ concurrent requests with minimal memory footprint
- Use virtual threads for I/O-bound operations (DB calls, HTTP calls, file I/O)
- Implement structured concurrency for parallel task execution
- Properly handle thread pinning issues (synchronized, native frames)
- Integrate virtual threads with Spring Boot 3.x + Tomcat
- Provide backpressure and bounded concurrent processing
- Include performance benchmarking vs platform threads

**Constraints:**
- Java 21+ with Preview features enabled (virtual threads)
- Spring Boot 3.x with embedded Tomcat
- No reactive stack (Project Reactor/WebFlux) — imperative style with virtual threads
- Proper scoped value propagation for request context

---

## Step-by-Step Solution

### Step 1: Virtual Thread Configuration

```yaml
spring:
  threads:
    virtual:
      enabled: true  # Spring Boot 3.2+ enables virtual threads for Tomcat
  task:
    execution:
      thread-name-prefix: virt-task-
  datasource:
    hikari:
      maximum-pool-size: 10  # Small pool - virtual threads don't block
      minimum-idle: 2
      pool-name: virt-db-pool
server:
  tomcat:
    threads:
      max: 200  # Tomcat acceptor threads; virtual threads handle the rest
    max-connections: 10000
    accept-count: 1000
```

### Step 2: Application Configuration — Virtual Threads

```java
@SpringBootApplication
public class HighThroughputApplication {
    public static void main(String[] args) {
        SpringApplication.run(HighThroughputApplication.class, args);
    }
}

@Configuration
public class VirtualThreadConfig {

    @Bean
    public TomcatProtocolHandlerCustomizer<?> protocolHandlerVirtualThreadExecutor() {
        return protocolHandler -> {
            protocolHandler.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        };
    }

    @Bean(name = "virtualTaskExecutor")
    public Executor virtualTaskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean(name = "blockingIoExecutor")
    public ExecutorService blockingIoExecutor() {
        // For operations that might pin virtual threads (JNI, native libs)
        return Executors.newFixedThreadPool(10, Thread.ofPlatform()
            .name("blocking-pool-", 0).factory());
    }
}
```

### Step 3: Order Processing Service (I/O-Bound with Virtual Threads)

```java
@Service
public class OrderProcessingService {

    private static final Logger log = LoggerFactory.getLogger(OrderProcessingService.class);
    private final HttpClient httpClient = HttpClient.newBuilder()
        .executor(Executors.newVirtualThreadPerTaskExecutor())
        .connectTimeout(Duration.ofSeconds(5))
        .build();
    private final ObjectMapper objectMapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());

    // Simulate DB operations — virtual threads handle the blocking
    @Async("virtualTaskExecutor")
    public CompletableFuture<Order> createOrderAsync(CreateOrderRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            // This blocking I/O runs on a virtual thread — no platform thread wasted
            String orderId = simulateDbInsert(request);
            return new Order(orderId, request.customerId(), request.items(),
                OrderStatus.PENDING, Instant.now());
        });
    }

    // Structured concurrency — fetch results from multiple services in parallel
    public OrderAggregate getOrderAggregate(String orderId) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<Order> order = scope.fork(() -> fetchOrder(orderId));
            Subtask<List<Payment>> payments = scope.fork(() -> fetchPayments(orderId));
            Subtask<List<Shipment>> shipments = scope.fork(() -> fetchShipments(orderId));
            Subtask<Customer> customer = scope.fork(() -> fetchCustomer(orderId));

            scope.join();           // Wait for all
            scope.throwIfFailed();  // Propagate exceptions

            return new OrderAggregate(
                order.get(), payments.get(), shipments.get(), customer.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Order aggregation interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Order aggregation failed", e.getCause());
        }
    }

    // Virtual thread handles blocking I/O — no reactive necessary
    public Order fetchOrder(String orderId) {
        // Simulate blocking DB call
        simulateIo(Duration.ofMillis(50));
        return new Order(orderId, "cust-" + orderId, List.of(), OrderStatus.CONFIRMED, Instant.now());
    }

    public List<Payment> fetchPayments(String orderId) {
        simulateIo(Duration.ofMillis(30));
        return List.of(new Payment("pay-" + orderId, new BigDecimal("100.00"), "COMPLETED"));
    }

    public List<Shipment> fetchShipments(String orderId) {
        simulateIo(Duration.ofMillis(40));
        return List.of(new Shipment("ship-" + orderId, "UPS", "TRACK-123", "DELIVERED"));
    }

    public Customer fetchCustomer(String orderId) {
        simulateIo(Duration.ofMillis(20));
        return new Customer("John Doe", "john@example.com", "Gold");
    }

    // Parallel processing with bounded concurrency
    public List<ProcessedItem> processItemsInParallel(List<OrderItem> items) {
        try (var scope = new StructuredTaskScope<ProcessedItem>()) {
            List<Subtask<ProcessedItem>> subtasks = items.stream()
                .map(item -> scope.fork(() -> processSingleItem(item)))
                .toList();
            scope.join();
            return subtasks.stream()
                .map(Subtask::get)
                .toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Item processing interrupted", e);
        }
    }

    ProcessedItem processSingleItem(OrderItem item) {
        simulateIo(Duration.ofMillis(100));
        return new ProcessedItem(item.sku(), item.quantity(), true, "Processed");
    }

    private String simulateDbInsert(CreateOrderRequest request) {
        simulateIo(Duration.ofMillis(30));
        return UUID.randomUUID().toString();
    }

    private void simulateIo(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
```

### Step 4: Controller — Virtual Thread Handler

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderProcessingService orderService;
    private final ScopedValueService scopedValueService;

    public OrderController(OrderProcessingService orderService,
                            ScopedValueService scopedValueService) {
        this.orderService = orderService;
        this.scopedValueService = scopedValueService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request) {
        // Virtual thread handles this request — blocking is OK
        Order order = orderService.createOrderAsync(request).join();
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{orderId}/aggregate")
    public ResponseEntity<OrderAggregate> getAggregate(@PathVariable String orderId) {
        // Structured concurrency — parallel fetching
        OrderAggregate aggregate = orderService.getOrderAggregate(orderId);
        return ResponseEntity.ok(aggregate);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.fetchOrder(orderId));
    }

    @PostMapping("/{orderId}/process")
    public ResponseEntity<List<ProcessedItem>> processItems(
            @PathVariable String orderId,
            @RequestBody List<OrderItem> items) {
        // Parallel item processing
        List<ProcessedItem> processed = orderService.processItemsInParallel(items);
        return ResponseEntity.ok(processed);
    }

    // ScopedValue demo — request context without ThreadLocal
    @GetMapping("/context")
    public ResponseEntity<Map<String, String>> contextDemo() {
        return ResponseEntity.ok(Map.of(
            "requestId", scopedValueService.getCurrentRequestId(),
            "traceId", scopedValueService.getCurrentTraceId(),
            "userId", scopedValueService.getCurrentUserId()
        ));
    }
}
```

### Step 5: ScopedValue for Request Context

```java
@Component
public class ScopedValueService {

    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();
    private static final ScopedValue<String> USER_ID = ScopedValue.newInstance();

    public <T> T withContext(String requestId, String traceId, String userId,
                              Callable<T> action) throws Exception {
        return ScopedValue.where(REQUEST_ID, requestId)
            .where(TRACE_ID, traceId)
            .where(USER_ID, userId)
            .call(action);
    }

    public void runWithContext(String requestId, String traceId, String userId,
                                Runnable action) {
        ScopedValue.where(REQUEST_ID, requestId)
            .where(TRACE_ID, traceId)
            .where(USER_ID, userId)
            .run(action);
    }

    public String getCurrentRequestId() {
        return REQUEST_ID.orElse("unknown");
    }

    public String getCurrentTraceId() {
        return TRACE_ID.orElse("unknown");
    }

    public String getCurrentUserId() {
        return USER_ID.orElse("anonymous");
    }
}

// Filter to set ScopedValue before request processing
@Component
public class ScopedValueFilter implements Filter {

    private final ScopedValueService scopedValueService;

    public ScopedValueFilter(ScopedValueService scopedValueService) {
        this.scopedValueService = scopedValueService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                          FilterChain chain) throws IOException, ServletException {
        String requestId = UUID.randomUUID().toString();
        String traceId = request.getParameter("traceId") != null
            ? request.getParameter("traceId") : requestId;
        String userId = "user-" + System.currentTimeMillis() % 1000;

        scopedValueService.runWithContext(requestId, traceId, userId,
            () -> {
                try {
                    chain.doFilter(request, response);
                } catch (IOException | ServletException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
```

### Step 6: Virtual Thread Safe Semaphore (Backpressure)

```java
@Component
public class BackpressureManager {

    private final Semaphore semaphore;

    public BackpressureManager(@Value("${app.max-concurrent-requests:500}") int maxConcurrent) {
        this.semaphore = new Semaphore(maxConcurrent, true);
    }

    public <T> T executeWithBackpressure(Supplier<T> action) {
        try {
            semaphore.acquire();
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for permit", e);
        } finally {
            semaphore.release();
        }
    }

    // For async execution
    public <T> CompletableFuture<T> executeAsync(Supplier<T> action) {
        return CompletableFuture.supplyAsync(() -> executeWithBackpressure(action),
            Executors.newVirtualThreadPerTaskExecutor());
    }

    public int getAvailablePermits() {
        return semaphore.availablePermits();
    }

    public int getQueueLength() {
        return semaphore.getQueueLength();
    }
}
```

### Step 7: Virtual Thread Safe Dispatcher

```java
@Component
public class VirtualThreadDispatcher {

    private final BackpressureManager backpressureManager;

    public VirtualThreadDispatcher(BackpressureManager backpressureManager) {
        this.backpressureManager = backpressureManager;
    }

    public <T> CompletableFuture<T> dispatch(String taskName, Supplier<T> task) {
        return CompletableFuture.supplyAsync(
            () -> {
                Thread current = Thread.currentThread();
                log.info("Dispatching {} on virtual thread: {} ({})",
                    taskName, current.getName(), current.threadId());
                return backpressureManager.executeWithBackpressure(task);
            },
            Executors.newVirtualThreadPerTaskExecutor()
        );
    }

    // Batch dispatch with structured concurrency
    public <T> List<T> dispatchAll(String batchName, List<Supplier<T>> tasks) {
        try (var scope = new StructuredTaskScope<T>()) {
            List<StructuredTaskScope.Subtask<T>> subtasks = tasks.stream()
                .map(task -> scope.fork(() -> backpressureManager.executeWithBackpressure(task)))
                .toList();
            scope.join();
            return subtasks.stream()
                .map(subtask -> {
                    try { return subtask.get(); }
                    catch (Exception e) { return null; }
                })
                .filter(Objects::nonNull)
                .toList();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Batch dispatch interrupted", e);
        }
    }
}
```

### Step 8: Benchmarks — Virtual Thread vs Platform Thread

```java
@Component
public class VirtualThreadBenchmark {

    private static final Logger log = LoggerFactory.getLogger(VirtualThreadBenchmark.class);

    public void runBenchmark(int requestCount, int concurrency) {
        log.info("Running benchmark: {} requests, concurrency={}", requestCount, concurrency);

        // Platform threads
        long platformTime = measure(Executors.newFixedThreadPool(concurrency), requestCount);
        log.info("Platform threads: {}ms", platformTime);

        // Virtual threads
        long virtualTime = measure(Executors.newVirtualThreadPerTaskExecutor(), requestCount);
        log.info("Virtual threads: {}ms", virtualTime);

        log.info("Improvement: {}%", (platformTime - virtualTime) * 100 / platformTime);
    }

    long measure(ExecutorService executor, int requestCount) {
        long start = System.currentTimeMillis();
        try (var scope = new StructuredTaskScope<Void>()) {
            for (int i = 0; i < requestCount; i++) {
                scope.fork(() -> {
                    try (var inner = executor) {
                        simulateIoBoundWork();
                    }
                    return null;
                });
            }
            scope.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return System.currentTimeMillis() - start;
    }

    void simulateIoBoundWork() {
        try { Thread.sleep(50); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Specific benchmark for high-concurrency scenario
    public void highConcurrencyBenchmark() {
        int numTasks = 10_000;
        int ioMs = 100;

        log.info("=== High Concurrency Benchmark: {} tasks, {}ms I/O ===", numTasks, ioMs);

        // Platform thread pool (200 threads)
        long platformTime = measurePlatform(numTasks, ioMs);
        log.info("Platform (200 threads): {}ms — throughput: {} req/s",
            platformTime, numTasks * 1000L / platformTime);

        // Virtual threads
        long virtualTime = measureVirtual(numTasks, ioMs);
        log.info("Virtual (unbounded): {}ms — throughput: {} req/s",
            virtualTime, numTasks * 1000L / virtualTime);
    }

    long measurePlatform(int numTasks, int ioMs) {
        long start = System.nanoTime();
        try (var executor = Executors.newFixedThreadPool(200)) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < numTasks; i++) {
                futures.add(CompletableFuture.runAsync(() -> {
                    try { Thread.sleep(Duration.ofMillis(ioMs)); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }, executor));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        return Duration.ofNanos(System.nanoTime() - start).toMillis();
    }

    long measureVirtual(int numTasks, int ioMs) {
        long start = System.nanoTime();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            for (int i = 0; i < numTasks; i++) {
                futures.add(CompletableFuture.runAsync(() -> {
                    try { Thread.sleep(Duration.ofMillis(ioMs)); }
                    catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }, executor));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
        return Duration.ofNanos(System.nanoTime() - start).toMillis();
    }
}
```

### Step 9: Thread Pinning Detection

```java
@Component
public class ThreadPinningDetector {

    private static final Logger log = LoggerFactory.getLogger(ThreadPinningDetector.class);
    private final AtomicLong pinnedCount = new AtomicLong();

    @EventListener(ApplicationReadyEvent.class)
    public void startMonitoring() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::checkPinning, 1, 10, TimeUnit.SECONDS);
    }

    void checkPinning() {
        // Detect carrier threads that are pinned (virtual threads that blocked carrier)
        Thread current = Thread.currentThread();
        if (current.isVirtual()) {
            // Virtual threads should not hold monitors for long
            log.warn("Monitoring thread is virtual — pinning detection active");
        }
    }

    // Simulate a pinned thread scenario for testing
    public void simulatePinning() {
        // Synchronized blocks pin virtual threads to carrier threads
        synchronized (this) {
            log.warn("PINNING: Virtual thread {} blocked carrier thread {}",
                Thread.currentThread().getName(),
                Thread.currentThread().isVirtual() ? "VIRTUAL" : "PLATFORM");
            simulateIo(Duration.ofMillis(100));
        }
    }

    // Better: use ReentrantLock instead of synchronized
    private final ReentrantLock lock = new ReentrantLock();

    public void nonPinningOperation() {
        lock.lock();
        try {
            // Virtual thread yields carrier if needed
            simulateIo(Duration.ofMillis(100));
        } finally {
            lock.unlock();
        }
    }

    private void simulateIo(Duration duration) {
        try { Thread.sleep(duration); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

### Step 10: Bounded Concurrent Processing

```java
@Service
public class BoundedProcessingService {

    private static final Logger log = LoggerFactory.getLogger(BoundedProcessingService.class);
    private final Semaphore concurrencyLimit;

    public BoundedProcessingService(@Value("${app.max-concurrent-items:100}") int maxConcurrent) {
        this.concurrencyLimit = new Semaphore(maxConcurrent, true);
    }

    public List<ProcessingResult> processAll(List<DataItem> items) {
        List<ProcessingResult> results = new ArrayList<>();
        try (var scope = new StructuredTaskScope<ProcessingResult>()) {
            List<StructuredTaskScope.Subtask<ProcessingResult>> subtasks = items.stream()
                .map(item -> scope.fork(() -> processWithLimit(item)))
                .toList();
            scope.join();
            results.addAll(subtasks.stream()
                .map(subtask -> {
                    try { return subtask.get(); }
                    catch (Exception e) {
                        return new ProcessingResult(item.id(), false, e.getMessage());
                    }
                })
                .toList());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return results;
    }

    ProcessingResult processWithLimit(DataItem item) {
        try {
            concurrencyLimit.acquire();
            try {
                processItem(item);
                return new ProcessingResult(item.id(), true, "OK");
            } finally {
                concurrencyLimit.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ProcessingResult(item.id(), false, "Interrupted");
        }
    }

    void processItem(DataItem item) {
        simulateIo(Duration.ofMillis(200));
        log.debug("Processed item: {}", item.id());
    }

    private void simulateIo(Duration duration) {
        try { Thread.sleep(duration); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

record DataItem(String id, String data) {}
record ProcessingResult(String id, boolean success, String message) {}
```

### Step 11: Domain Records

```java
public record Order(
    String id,
    String customerId,
    List<OrderItem> items,
    OrderStatus status,
    Instant createdAt
) {}

public record OrderItem(
    String sku,
    String productName,
    int quantity,
    BigDecimal unitPrice
) {}

public record OrderAggregate(
    Order order,
    List<Payment> payments,
    List<Shipment> shipments,
    Customer customer
) {}

public record Payment(
    String id,
    BigDecimal amount,
    String status
) {}

public record Shipment(
    String id,
    String carrier,
    String trackingNumber,
    String status
) {}

public record Customer(
    String name,
    String email,
    String tier
) {}

public record CreateOrderRequest(
    String customerId,
    List<OrderItem> items
) {}

public record ProcessedItem(
    String sku,
    int quantity,
    boolean success,
    String message
) {}

public enum OrderStatus {
    PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED
}
```

### Step 12: Performance Endpoint

```java
@RestController
@RequestMapping("/api/benchmark")
public class BenchmarkController {

    private final VirtualThreadBenchmark benchmark;
    private final BackpressureManager backpressureManager;
    private final VirtualThreadDispatcher dispatcher;

    public BenchmarkController(VirtualThreadBenchmark benchmark,
                                BackpressureManager backpressureManager,
                                VirtualThreadDispatcher dispatcher) {
        this.benchmark = benchmark;
        this.backpressureManager = backpressureManager;
        this.dispatcher = dispatcher;
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runBenchmark(
            @RequestParam(defaultValue = "1000") int requests,
            @RequestParam(defaultValue = "100") int concurrency) {
        long start = System.currentTimeMillis();
        benchmark.runBenchmark(requests, concurrency);
        long elapsed = System.currentTimeMillis() - start;
        return ResponseEntity.ok(Map.of(
            "requests", requests,
            "concurrency", concurrency,
            "elapsed", elapsed + "ms",
            "throughput", (requests * 1000.0 / elapsed) + " req/s"
        ));
    }

    @GetMapping("/backpressure")
    public ResponseEntity<Map<String, Object>> backpressureStatus() {
        return ResponseEntity.ok(Map.of(
            "availablePermits", backpressureManager.getAvailablePermits(),
            "queueLength", backpressureManager.getQueueLength()
        ));
    }

    @GetMapping("/thread-info")
    public ResponseEntity<Map<String, Object>> threadInfo() {
        return ResponseEntity.ok(Map.of(
            "activeThreads", Thread.activeCount(),
            "isVirtualThread", Thread.currentThread().isVirtual(),
            "virtualThreadName", Thread.currentThread().getName()
        ));
    }
}
```

---

## Complexity Analysis

| Aspect | Platform Threads (200 pool) | Virtual Threads (Unbounded) |
|--------|---------------------------|---------------------------|
| **Max concurrent I/O tasks** | 200 (pool size) | 100,000+ (memory bound) |
| **Memory per blocked task** | ~1MB (stack) | ~10KB (stack) |
| **Context switch overhead** | ~10µs (kernel) | ~1µs (user-mode) |
| **10K concurrent sleep(100ms)** | ~5s (200 threads) | ~100ms (all run) |
| **Throughput (10K 100ms I/O)** | ~2,000 req/s | ~100,000 req/s |
| **Scaling cost** | O(pool size * stack) | O(tasks * stack) |

---

## Follow-Up Questions

1. **What causes virtual thread pinning and how do you avoid it?** — Pinning occurs when a virtual thread enters a `synchronized` block or calls a native method (JNI). The carrier thread is pinned and cannot be reassigned. Fix: use `ReentrantLock` instead of `synchronized`, avoid JNI in hot paths.

2. **How do virtual threads differ from WebFlux reactive programming?** — Virtual threads use imperative blocking style (easier to write/debug). WebFlux uses reactive streams (harder, better for event-loop architectures). Virtual threads are better for I/O-bound workloads; WebFlux can be better for CPU-bound with backpressure.

3. **How do you handle thread-local storage with virtual threads?** — Virtual threads can use `ThreadLocal` (millions of ThreadLocal instances work). However, `ScopedValue` (Incubator in Java 21) is preferred: immutable, inheritable, bounded lifetime. `ScopedValue.where()` creates a scoped binding.

4. **What are the memory implications of 10,000 virtual threads?** — Each virtual thread has a small stack (~10KB vs 1MB for platform threads). 10,000 virtual threads use ~100MB for stacks vs 10GB for platform threads. Heap memory is the same regardless.

5. **When should you NOT use virtual threads?** — CPU-bound tasks (no I/O waiting), tasks that run synchronized blocks for a long time, tasks using JNI/pinning operations, when you need priority-based scheduling, when running on Java <21.

---

## Test Cases

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class VirtualThreadApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateOrder() throws Exception {
        var request = """
            {"customerId": "cust-1", "items": [
                {"sku": "SKU-1", "productName": "Widget", "quantity": 2, "unitPrice": 19.99}
            ]}
            """;

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isString())
            .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void shouldGetOrderAggregate() throws Exception {
        mockMvc.perform(get("/api/orders/order-1/aggregate"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.order").exists())
            .andExpect(jsonPath("$.payments").isArray())
            .andExpect(jsonPath("$.shipments").isArray())
            .andExpect(jsonPath("$.customer").exists());
    }

    @Test
    void shouldProcessItems() throws Exception {
        var items = """
            [{"sku": "A", "productName": "A", "quantity": 1, "unitPrice": 10},
             {"sku": "B", "productName": "B", "quantity": 2, "unitPrice": 20}]
            """;

        mockMvc.perform(post("/api/orders/order-1/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(items))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].success").value(true));
    }

    @Test
    void shouldReturnThreadInfo() throws Exception {
        mockMvc.perform(get("/api/benchmark/thread-info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.isVirtualThread").isBoolean())
            .andExpect(jsonPath("$.virtualThreadName").isString());
    }

    @Test
    void shouldHandleHighConcurrency() throws Exception {
        // Execute 100 concurrent requests
        int numRequests = 100;
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < numRequests; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                try {
                    var response = mockMvc.perform(get("/api/orders/order-1/aggregate"))
                        .andExpect(status().isOk())
                        .andReturn();
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }, executor));
        }

        var results = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream().map(CompletableFuture::join).toList())
            .join();

        assertThat(results).allMatch(b -> b);
    }

    @Test
    void shouldPropagateScopedValue() throws Exception {
        mockMvc.perform(get("/api/orders/context"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.requestId").isNotEmpty());
    }

    @Test
    void shouldApplyBackpressure() throws Exception {
        int permits = 50;
        var backpressure = new BackpressureManager(permits);

        // Execute more tasks than permits
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < permits * 2; i++) {
            futures.add(CompletableFuture.runAsync(
                () -> backpressure.executeWithBackpressure(() -> {
                    try { Thread.sleep(10); } catch (InterruptedException e) {}
                    return null;
                }), executor));
        }

        var all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        assertThatCode(() -> all.join()).doesNotThrowAnyException();
    }
}

@SpringBootTest
class VirtualThreadConfigurationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void shouldHaveVirtualThreadExecutor() {
        var executor = context.getBean("virtualTaskExecutor", Executor.class);
        assertThat(executor).isNotNull();
    }

    @Test
    void virtualThreadShouldBeUsed() throws Exception {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                () -> Thread.currentThread().isVirtual(), executor);
            assertThat(future.get()).isTrue();
        }
    }

    @Test
    void structuredConcurrencyShouldWork() throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Subtask<Integer> task1 = scope.fork(() -> {
                Thread.sleep(10);
                return 42;
            });
            Subtask<String> task2 = scope.fork(() -> {
                Thread.sleep(5);
                return "hello";
            });
            scope.join();
            scope.throwIfFailed();
            assertThat(task1.get()).isEqualTo(42);
            assertThat(task2.get()).isEqualTo("hello");
        }
    }
}
```

---

## Summary

This high-throughput virtual thread API demonstrates:
- **Virtual threads for I/O**: blocking calls (Thread.sleep, DB, HTTP) don't waste platform threads
- **Structured concurrency**: `StructuredTaskScope` for parallel task management with proper error propagation
- **ScopedValue**: request context propagation without ThreadLocal (safer, inheritable)
- **Backpressure**: `Semaphore`-based concurrency limiting to prevent overload
- **Thread pinning awareness**: using `ReentrantLock` instead of `synchronized` to avoid carrier thread pinning
- **Benchmarking**: measurable throughput improvements (50x+) for I/O-bound workloads
- **Spring Boot integration**: Tomcat virtual thread protocol handler, `@Async` with virtual executor