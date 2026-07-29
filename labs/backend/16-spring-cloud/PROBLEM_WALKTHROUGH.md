# PROBLEM WALKTHROUGH: Design a Service Discovery and Load Balancing System

## Problem Statement

Design and implement a lightweight service discovery and load balancing system from scratch. The system should:

- Allow services to register themselves with metadata (host, port, health endpoint)
- Provide heartbeat-based health monitoring with automatic eviction
- Support client-side load balancing with configurable strategies (round-robin, random, least-connections, weighted)
- Support server-side load balancing via a simple reverse proxy
- Provide a REST API for registration, deregistration, and discovery
- Cache registry state for performance
- Handle network partitions gracefully

**Constraints:**
- Pure Java 21+ with embedded HTTP server (no Spring Cloud/Eureka dependency)
- Concurrent, thread-safe registry
- Sub-second registration propagation
- Configurable health check intervals

---

## Step-by-Step Solution

### Step 1: Service Instance Model

```java
public record ServiceInstance(
    String serviceId,
    String instanceId,
    String host,
    int port,
    String healthEndpoint,
    Map<String, String> metadata,
    Instant registeredAt,
    Instant lastHeartbeat,
    ServiceStatus status
) {
    public String getAddress() {
        return host + ":" + port;
    }

    public URI toUri() {
        try {
            return new URI("http://" + getAddress());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public ServiceInstance withStatus(ServiceStatus newStatus) {
        return new ServiceInstance(serviceId, instanceId, host, port,
            healthEndpoint, metadata, registeredAt, lastHeartbeat, newStatus);
    }

    public ServiceInstance withHeartbeat(Instant now) {
        return new ServiceInstance(serviceId, instanceId, host, port,
            healthEndpoint, metadata, registeredAt, now, status);
    }
}

public enum ServiceStatus {
    UP, DOWN, UNKNOWN
}

public record ServiceRegistrationRequest(
    String serviceId,
    String instanceId,
    String host,
    int port,
    String healthEndpoint,
    Map<String, String> metadata
) {}
```

### Step 2: Service Registry (Thread-Safe)

```java
public class ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ServiceRegistry.class);
    private static final Duration HEARTBEAT_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration EVICTION_INTERVAL = Duration.ofSeconds(15);

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, ServiceInstance>> registry = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ServiceInstance> instancesById = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ServiceRegistry() {
        scheduler.scheduleAtFixedRate(this::evictDeadInstances,
            EVICTION_INTERVAL.toMillis(), EVICTION_INTERVAL.toMillis(),
            TimeUnit.MILLISECONDS);
    }

    public ServiceInstance register(ServiceRegistrationRequest request) {
        Instant now = Instant.now();
        ServiceInstance instance = new ServiceInstance(
            request.serviceId(),
            request.instanceId(),
            request.host(),
            request.port(),
            request.healthEndpoint() != null ? request.healthEndpoint() : "/actuator/health",
            request.metadata() != null ? request.metadata() : Map.of(),
            now, now, ServiceStatus.UP
        );
        registry.computeIfAbsent(request.serviceId(), k -> new ConcurrentHashMap<>())
            .put(request.instanceId(), instance);
        instancesById.put(request.instanceId(), instance);
        log.info("Registered: {} ({}:{})", request.serviceId(), request.host(), request.port());
        return instance;
    }

    public Optional<ServiceInstance> deregister(String serviceId, String instanceId) {
        Optional<ServiceInstance> removed = Optional.ofNullable(
            registry.getOrDefault(serviceId, new ConcurrentHashMap<>())
                .remove(instanceId));
        instancesById.remove(instanceId);
        removed.ifPresent(inst ->
            log.info("Deregistered: {} ({})", serviceId, instanceId));
        return removed;
    }

    public boolean heartbeat(String serviceId, String instanceId) {
        ConcurrentHashMap<String, ServiceInstance> services = registry.get(serviceId);
        if (services == null) return false;
        services.computeIfPresent(instanceId, (id, inst) -> {
            ServiceInstance updated = inst.withHeartbeat(Instant.now())
                .withStatus(ServiceStatus.UP);
            instancesById.put(instanceId, updated);
            return updated;
        });
        return services.containsKey(instanceId);
    }

    public List<ServiceInstance> getInstances(String serviceId) {
        return List.copyOf(
            registry.getOrDefault(serviceId, new ConcurrentHashMap<>()).values());
    }

    public List<ServiceInstance> getHealthyInstances(String serviceId) {
        return getInstances(serviceId).stream()
            .filter(inst -> inst.status() == ServiceStatus.UP)
            .toList();
    }

    public Set<String> getServices() {
        return Set.copyOf(registry.keySet());
    }

    public Optional<ServiceInstance> getInstance(String instanceId) {
        return Optional.ofNullable(instancesById.get(instanceId));
    }

    boolean isHealthy(ServiceInstance instance) {
        return Duration.between(instance.lastHeartbeat(), Instant.now())
            .compareTo(HEARTBEAT_TIMEOUT) < 0;
    }

    void evictDeadInstances() {
        Instant now = Instant.now();
        registry.forEach((serviceId, instances) -> {
            instances.forEach((instanceId, instance) -> {
                if (Duration.between(instance.lastHeartbeat(), now)
                        .compareTo(HEARTBEAT_TIMEOUT) > 0) {
                    ServiceInstance markedDown = instance.withStatus(ServiceStatus.DOWN);
                    instances.put(instanceId, markedDown);
                    instancesById.put(instanceId, markedDown);
                    log.warn("Marked DOWN: {} ({}) - no heartbeat for {}ms",
                        serviceId, instanceId, HEARTBEAT_TIMEOUT.toMillis());

                    if (Duration.between(instance.lastHeartbeat(), now)
                            .compareTo(HEARTBEAT_TIMEOUT.multipliedBy(2)) > 0) {
                        instances.remove(instanceId);
                        instancesById.remove(instanceId);
                        log.warn("Evicted: {} ({}) - heartbeat timeout exceeded", serviceId, instanceId);
                    }
                }
            });
        });
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
```

### Step 3: Load Balancing Strategies

```java
@FunctionalInterface
public interface LoadBalanceStrategy {
    Optional<ServiceInstance> select(String serviceId, List<ServiceInstance> instances);
}

public class RoundRobinStrategy implements LoadBalanceStrategy {
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    @Override
    public Optional<ServiceInstance> select(String serviceId, List<ServiceInstance> instances) {
        if (instances.isEmpty()) return Optional.empty();
        int index = counters.computeIfAbsent(serviceId, k -> new AtomicInteger(-1))
            .updateAndGet(i -> (i + 1) % instances.size());
        return Optional.of(instances.get(index));
    }
}

public class RandomStrategy implements LoadBalanceStrategy {
    private final Random random = new Random();

    @Override
    public Optional<ServiceInstance> select(String serviceId, List<ServiceInstance> instances) {
        if (instances.isEmpty()) return Optional.empty();
        return Optional.of(instances.get(random.nextInt(instances.size())));
    }
}

public class LeastConnectionsStrategy implements LoadBalanceStrategy {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> connections = new ConcurrentHashMap<>();

    public void acquireConnection(String serviceId, String instanceId) {
        connections.computeIfAbsent(serviceId, k -> new ConcurrentHashMap<>())
            .computeIfAbsent(instanceId, k -> new AtomicInteger(0))
            .incrementAndGet();
    }

    public void releaseConnection(String serviceId, String instanceId) {
        connections.getOrDefault(serviceId, new ConcurrentHashMap<>())
            .getOrDefault(instanceId, new AtomicInteger(0))
            .updateAndGet(i -> Math.max(0, i - 1));
    }

    @Override
    public Optional<ServiceInstance> select(String serviceId, List<ServiceInstance> instances) {
        if (instances.isEmpty()) return Optional.empty();
        return instances.stream()
            .min(Comparator.comparingInt(inst -> {
                var connMap = connections.get(serviceId);
                if (connMap == null) return 0;
                return connMap.getOrDefault(inst.instanceId(), new AtomicInteger(0)).get();
            }));
    }
}

public class WeightedStrategy implements LoadBalanceStrategy {
    private final Random random = new Random();

    @Override
    public Optional<ServiceInstance> select(String serviceId, List<ServiceInstance> instances) {
        if (instances.isEmpty()) return Optional.empty();
        int totalWeight = instances.stream()
            .mapToInt(inst -> Integer.parseInt(
                inst.metadata().getOrDefault("weight", "1")))
            .sum();
        int randomWeight = random.nextInt(totalWeight);
        int cumulative = 0;
        for (ServiceInstance inst : instances) {
            cumulative += Integer.parseInt(
                inst.metadata().getOrDefault("weight", "1"));
            if (randomWeight < cumulative) return Optional.of(inst);
        }
        return Optional.of(instances.getLast());
    }
}
```

### Step 4: Load Balancer (Client-Side)

```java
public class LoadBalancer {

    private final ServiceRegistry registry;
    private final Map<String, LoadBalanceStrategy> strategies = new ConcurrentHashMap<>();
    private static final LoadBalanceStrategy DEFAULT_STRATEGY = new RoundRobinStrategy();

    public LoadBalancer(ServiceRegistry registry) {
        this.registry = registry;
    }

    public void setStrategy(String serviceId, LoadBalanceStrategy strategy) {
        strategies.put(serviceId, strategy);
    }

    public Optional<ServiceInstance> selectInstance(String serviceId) {
        List<ServiceInstance> healthyInstances = registry.getHealthyInstances(serviceId);
        if (healthyInstances.isEmpty()) {
            // Fallback to all instances (including DOWN) for resilience
            healthyInstances = registry.getInstances(serviceId);
            if (healthyInstances.isEmpty()) return Optional.empty();
        }
        LoadBalanceStrategy strategy = strategies.getOrDefault(serviceId, DEFAULT_STRATEGY);
        return strategy.select(serviceId, healthyInstances);
    }

    // HTTP call helper using Java HttpClient
    public <T> Optional<T> call(String serviceId, String path,
                                  Class<T> responseType) {
        return selectInstance(serviceId).flatMap(instance -> callInstance(instance, path, responseType));
    }

    <T> Optional<T> callInstance(ServiceInstance instance, String path,
                                   Class<T> responseType) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(instance.toUri() + path))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();
            HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return Optional.of(new ObjectMapper().readValue(response.body(), responseType));
            }
        } catch (Exception e) {
            // Mark instance as potentially down
            registry.heartbeat(instance.serviceId(), instance.instanceId());
        }
        return Optional.empty();
    }
}
```

### Step 5: Server-Side Proxy (Reverse Proxy)

```java
public class ServiceProxy {

    private final ServiceRegistry registry;
    private final LoadBalancer loadBalancer;
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    public ServiceProxy(ServiceRegistry registry, LoadBalancer loadBalancer) {
        this.registry = registry;
        this.loadBalancer = loadBalancer;
    }

    public HttpResponse<String> proxyRequest(HttpRequest incomingRequest,
                                               String serviceId) {
        return loadBalancer.selectInstance(serviceId)
            .map(instance -> forwardRequest(incomingRequest, instance))
            .orElseGet(() -> errorResponse(503, "Service unavailable: " + serviceId));
    }

    HttpResponse<String> forwardRequest(HttpRequest incoming, ServiceInstance instance) {
        try {
            String targetPath = incoming.uri().getPath()
                .replaceFirst("/api/" + instance.serviceId(), "");
            URI targetUri = URI.create(instance.toUri() + targetPath
                + (incoming.uri().getQuery() != null ? "?" + incoming.uri().getQuery() : ""));

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(targetUri)
                .timeout(Duration.ofSeconds(10));

            // Copy method and body
            incoming.headers().map().forEach((name, values) ->
                values.forEach(v -> builder.header(name, v)));

            if (incoming.bodyPublisher().isPresent()) {
                builder.method(incoming.method(),
                    HttpRequest.BodyPublishers.ofByteArray(
                        incoming.bodyPublisher().get().toCompletableFuture().get()));
            } else {
                builder.method(incoming.method(), HttpRequest.BodyPublishers.noBody());
            }

            HttpResponse<String> response = httpClient.send(
                builder.build(), HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (Exception e) {
            return errorResponse(502, "Bad gateway: " + e.getMessage());
        }
    }

    HttpResponse<String> errorResponse(int status, String message) {
        try {
            String body = new ObjectMapper().writeValueAsString(
                Map.of("error", message, "status", status));
            return new SimpleHttpResponse(status, body);
        } catch (Exception e) {
            return new SimpleHttpResponse(500, "{\"error\":\"Internal error\"}");
        }
    }

    record SimpleHttpResponse(int statusCode, String body) implements HttpResponse<String> {
        @Override public int statusCode() { return statusCode; }
        @Override public String body() { return body; }
        @Override public HttpHeaders headers() { return HttpHeaders.newBuilder().build(); }
        @Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
        @Override public HttpRequest request() { return null; }
        @Override public URI uri() { return null; }
        @Override public Version version() { return Version.HTTP_1_1; }
        @Override public SSLSession sslSession() { return null; }
    }
}
```

### Step 6: Health Checker

```java
public class HealthChecker {

    private static final Logger log = LoggerFactory.getLogger(HealthChecker.class);
    private final ServiceRegistry registry;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    public HealthChecker(ServiceRegistry registry) {
        this.registry = registry;
    }

    public void start(int intervalSeconds) {
        scheduler.scheduleAtFixedRate(this::checkAllServices,
            intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
    }

    void checkAllServices() {
        registry.getServices().forEach(serviceId ->
            registry.getInstances(serviceId).forEach(this::checkInstance));
    }

    void checkInstance(ServiceInstance instance) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(instance.toUri() + instance.healthEndpoint()))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            registry.heartbeat(instance.serviceId(), instance.instanceId());
        } catch (Exception e) {
            log.warn("Health check failed for {} ({}): {}",
                instance.serviceId(), instance.instanceId(), e.getMessage());
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
```

### Step 7: Registry REST API (HTTP Server)

```java
public class RegistryServer {

    private static final Logger log = LoggerFactory.getLogger(RegistryServer.class);
    private final ServiceRegistry registry;
    private final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule());
    private final int port;
    private HttpServer server;

    public RegistryServer(ServiceRegistry registry, int port) {
        this.registry = registry;
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/registry/register", this::handleRegister);
        server.createContext("/api/registry/deregister", this::handleDeregister);
        server.createContext("/api/registry/heartbeat", this::handleHeartbeat);
        server.createContext("/api/registry/services", this::handleListServices);
        server.createContext("/api/registry/instances", this::handleListInstances);
        server.createContext("/api/registry/health", this::handleHealth);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        server.start();
        log.info("Registry server started on port {}", port);
    }

    void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, Map.of("error", "Method not allowed"));
            return;
        }
        ServiceRegistrationRequest request = mapper.readValue(
            exchange.getRequestBody(), ServiceRegistrationRequest.class);
        ServiceInstance instance = registry.register(request);
        sendResponse(exchange, 201, instance);
    }

    void handleDeregister(HttpExchange exchange) throws IOException {
        if (!"DELETE".equals(exchange.getRequestMethod())
                && !"POST".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, Map.of("error", "Method not allowed"));
            return;
        }
        Map<String, String> params = queryParams(exchange);
        String serviceId = params.get("serviceId");
        String instanceId = params.get("instanceId");
        if (serviceId == null || instanceId == null) {
            sendResponse(exchange, 400, Map.of("error", "serviceId and instanceId required"));
            return;
        }
        Optional<ServiceInstance> removed = registry.deregister(serviceId, instanceId);
        if (removed.isPresent()) {
            sendResponse(exchange, 200, removed.get());
        } else {
            sendResponse(exchange, 404, Map.of("error", "Instance not found"));
        }
    }

    void handleHeartbeat(HttpExchange exchange) throws IOException {
        if (!"PUT".equals(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, Map.of("error", "Method not allowed"));
            return;
        }
        Map<String, String> params = queryParams(exchange);
        String serviceId = params.get("serviceId");
        String instanceId = params.get("instanceId");
        if (serviceId == null || instanceId == null) {
            sendResponse(exchange, 400, Map.of("error", "serviceId and instanceId required"));
            return;
        }
        boolean success = registry.heartbeat(serviceId, instanceId);
        sendResponse(exchange, success ? 200 : 404,
            Map.of("status", success ? "OK" : "NOT_FOUND"));
    }

    void handleListServices(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 200, registry.getServices());
    }

    void handleListInstances(HttpExchange exchange) throws IOException {
        Map<String, String> params = queryParams(exchange);
        String serviceId = params.get("serviceId");
        if (serviceId == null) {
            sendResponse(exchange, 400, Map.of("error", "serviceId required"));
            return;
        }
        boolean healthyOnly = "true".equals(params.get("healthy"));
        List<ServiceInstance> instances = healthyOnly
            ? registry.getHealthyInstances(serviceId)
            : registry.getInstances(serviceId);
        sendResponse(exchange, 200, instances);
    }

    void handleHealth(HttpExchange exchange) throws IOException {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("services", registry.getServices().size());
        health.put("instances", registry.getInstanceCount());
        sendResponse(exchange, 200, health);
    }

    void sendResponse(HttpExchange exchange, int status, Object body) throws IOException {
        byte[] json = mapper.writeValueAsBytes(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, json.length);
        exchange.getResponseBody().write(json);
        exchange.getResponseBody().close();
    }

    Map<String, String> queryParams(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) return Map.of();
        return Arrays.stream(query.split("&"))
            .map(p -> p.split("=", 2))
            .collect(Collectors.toMap(p -> p[0], p -> p.length > 1 ? p[1] : ""));
    }

    public void stop() {
        if (server != null) server.stop(0);
    }
}
```

### Step 8: Service Client (For Services to Register)

```java
public class ServiceClient implements AutoCloseable {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String registryUrl;
    private final ServiceRegistrationRequest registration;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public ServiceClient(String registryHost, int registryPort,
                          ServiceRegistrationRequest registration) {
        this.registryUrl = "http://" + registryHost + ":" + registryPort + "/api/registry";
        this.registration = registration;
    }

    public void registerAndHeartbeat() {
        register();
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, 10, 10, TimeUnit.SECONDS);
    }

    void register() {
        try {
            String body = new ObjectMapper().writeValueAsString(registration);
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(registryUrl + "/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
        }
    }

    void sendHeartbeat() {
        try {
            String url = registryUrl + "/heartbeat?serviceId="
                + registration.serviceId() + "&instanceId=" + registration.instanceId();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Heartbeat failed: " + e.getMessage());
        }
    }

    public void deregister() {
        try {
            String url = registryUrl + "/deregister?serviceId="
                + registration.serviceId() + "&instanceId=" + registration.instanceId();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Deregistration failed: " + e.getMessage());
        }
    }

    @Override
    public void close() {
        deregister();
        scheduler.shutdown();
    }
}
```

### Step 9: End-to-End Demo

```java
public class ServiceDiscoveryDemo {

    public static void main(String[] args) throws Exception {
        // Start registry server
        ServiceRegistry registry = new ServiceRegistry();
        RegistryServer registryServer = new RegistryServer(registry, 8761);
        registryServer.start();

        // Start health checker
        HealthChecker healthChecker = new HealthChecker(registry);
        healthChecker.start(15);

        // Start load balancer
        LoadBalancer loadBalancer = new LoadBalancer(registry);
        loadBalancer.setStrategy("order-service", new WeightedStrategy());
        loadBalancer.setStrategy("payment-service", new LeastConnectionsStrategy());

        // Service A registers
        ServiceClient orderService1 = new ServiceClient("localhost", 8761,
            new ServiceRegistrationRequest("order-service", "order-1",
                "192.168.1.10", 8081, "/health", Map.of("weight", "5")));
        orderService1.registerAndHeartbeat();

        // Service A instance 2 registers
        ServiceClient orderService2 = new ServiceClient("localhost", 8761,
            new ServiceRegistrationRequest("order-service", "order-2",
                "192.168.1.10", 8082, "/health", Map.of("weight", "3")));
        orderService2.registerAndHeartbeat();

        // Service B registers
        ServiceClient paymentService = new ServiceClient("localhost", 8761,
            new ServiceRegistrationRequest("payment-service", "payment-1",
                "192.168.1.20", 8090, "/health", Map.of()));
        paymentService.registerAndHeartbeat();

        // Client discovers and load balances
        System.out.println("Available services: " + registry.getServices());
        System.out.println("Order instances: " + registry.getHealthyInstances("order-service"));
        System.out.println("Payment instances: " + registry.getHealthyInstances("payment-service"));

        // Test load balancing
        for (int i = 0; i < 5; i++) {
            loadBalancer.selectInstance("order-service")
                .ifPresent(inst ->
                    System.out.println("Selected order instance: " + inst.instanceId()));
        }

        // Cleanup
        orderService1.close();
        orderService2.close();
        paymentService.close();
        healthChecker.shutdown();
        registryServer.stop();
        registry.shutdown();
    }
}
```

---

## Complexity Analysis

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|-----------------|
| Register | O(1) hash put | O(1) per instance |
| Deregister | O(1) hash remove | O(1) |
| Heartbeat | O(1) update | O(1) |
| Get instances | O(k) where k = instances per service | O(k) |
| Evict dead | O(n) total instances | O(1) |
| Round-robin select | O(1) | O(1) |
| Least-connections select | O(k) find min | O(k) |
| Weighted select | O(k) cumulative sum | O(1) |

---

## Follow-Up Questions

1. **How would you make the registry highly available?** — Run multiple registry instances with peer replication. Use gossip protocol (like Consul) or RAFT (like etcd) for consistency. Each registry instance can handle reads; writes require quorum.

2. **How do you handle split-brain scenarios?** — Use a consensus algorithm (RAFT/Paxos). Self-preservation mode (like Eureka) when cluster is partitioned — stop evictions, accept registrations, serve stale data until quorum returns.

3. **How would you implement zone/region-aware routing?** — Add `zone` and `region` to instance metadata. Load balancer prefers same-zone instances, falls back to same-region, then cross-region. Avoid cross-region calls for latency-sensitive operations.

4. **What's the difference between client-side and server-side discovery?** — Client-side: client queries registry and load-balances. Simpler, fewer hops. Server-side: proxy/router handles discovery and LB. Better for polyglot environments, central control.

5. **How do you integrate circuit breakers with service discovery?** — When a circuit breaker opens for an instance, mark it as DOWN in the registry. Load balancer skips unhealthy instances. When half-open probes succeed, mark it UP again.

---

## Test Cases

```java
class ServiceRegistryTest {

    private ServiceRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ServiceRegistry();
    }

    @Test
    void shouldRegisterService() {
        registry.register(new ServiceRegistrationRequest(
            "test-service", "inst-1", "localhost", 8080, "/health", Map.of()));
        assertThat(registry.getServices()).contains("test-service");
        assertThat(registry.getInstances("test-service")).hasSize(1);
    }

    @Test
    void shouldDeregisterService() {
        registry.register(new ServiceRegistrationRequest(
            "test-service", "inst-1", "localhost", 8080, "/health", Map.of()));
        registry.deregister("test-service", "inst-1");
        assertThat(registry.getInstances("test-service")).isEmpty();
    }

    @Test
    void shouldHandleHeartbeat() {
        registry.register(new ServiceRegistrationRequest(
            "test-service", "inst-1", "localhost", 8080, "/health", Map.of()));
        assertThat(registry.heartbeat("test-service", "inst-1")).isTrue();
        assertThat(registry.heartbeat("unknown", "inst-1")).isFalse();
    }

    @Test
    void shouldReturnOnlyHealthyInstances() {
        registry.register(new ServiceRegistrationRequest(
            "test-service", "inst-1", "localhost", 8080, "/health", Map.of()));
        registry.register(new ServiceRegistrationRequest(
            "test-service", "inst-2", "localhost", 8081, "/health", Map.of()));
        assertThat(registry.getHealthyInstances("test-service")).hasSize(2);
    }

    @Test
    void shouldEvictAfterHeartbeatTimeout() throws Exception {
        registry.register(new ServiceRegistrationRequest(
            "test-service", "inst-1", "localhost", 8080, "/health", Map.of()));
        Thread.sleep(100);
        registry.evictDeadInstances();
        assertThat(registry.getHealthyInstances("test-service")).isEmpty();
    }

    @Test
    void shouldLoadBalanceRoundRobin() {
        registry.register(new ServiceRegistrationRequest(
            "svc", "a", "host1", 8080, "/health", Map.of()));
        registry.register(new ServiceRegistrationRequest(
            "svc", "b", "host2", 8080, "/health", Map.of()));
        LoadBalancer lb = new LoadBalancer(registry);
        lb.setStrategy("svc", new RoundRobinStrategy());

        assertThat(lb.selectInstance("svc").get().instanceId()).isEqualTo("a");
        assertThat(lb.selectInstance("svc").get().instanceId()).isEqualTo("b");
        assertThat(lb.selectInstance("svc").get().instanceId()).isEqualTo("a");
    }

    @Test
    void shouldLoadBalanceWeighted() {
        registry.register(new ServiceRegistrationRequest(
            "svc", "heavy", "host1", 8080, "/health", Map.of("weight", "70")));
        registry.register(new ServiceRegistrationRequest(
            "svc", "light", "host2", 8080, "/health", Map.of("weight", "30")));
        LoadBalancer lb = new LoadBalancer(registry);
        lb.setStrategy("svc", new WeightedStrategy());

        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            lb.selectInstance("svc").ifPresent(inst ->
                counts.merge(inst.instanceId(), 1, Integer::sum));
        }
        assertThat(counts.get("heavy")).isGreaterThan(counts.get("light"));
    }

    @Test
    void shouldReturnEmptyForUnknownService() {
        LoadBalancer lb = new LoadBalancer(registry);
        assertThat(lb.selectInstance("unknown")).isEmpty();
    }
}
```

---

## Summary

This service discovery and load balancing system demonstrates:
- **Service registration** with metadata and health endpoints
- **Heartbeat-based health monitoring** with automatic eviction of dead instances
- **Four load balancing strategies**: round-robin, random, least-connections, weighted
- **Client-side load balancing**: clients select instances dynamically
- **Server-side proxy**: reverse proxy with service resolution
- **REST API**: full HTTP API for registry operations
- **Configurable strategies**: per-service load balancing strategy assignment
- **Thread-safe registry**: ConcurrentHashMap-based, handles concurrent registrations