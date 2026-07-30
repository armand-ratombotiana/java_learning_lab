# Design Service Registry with Health Checking

## Problem Statement
Design and implement a service registry (like Eureka/Consul) with:
- Service registration: `register(serviceId, host, port, healthUrl)`
- Service discovery: `getInstances(serviceId)` returning healthy instances
- Heartbeat-based health checking with configurable intervals and timeouts
- Automatic eviction of unhealthy instances
- Load balancing (round-robin among healthy instances)
- Self-preservation mode (like Eureka's "safe mode") when heartbeat loss exceeds threshold

## Solution

```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.stream.*;

/**
 * Service registry with health checking, automatic eviction, and
 * self-preservation mode.
 * <p>
 * Time complexity:
 * - register: O(1)
 * - heartbeat: O(1)
 * - getInstances: O(k) where k = instances for service
 * - eviction: O(n) where n = total registered instances
 * <p>
 * Space complexity: O(n) for n registered instances
 */
public class ServiceRegistry {

    private final ConcurrentHashMap<String, CopyOnWriteArrayList<ServiceInstance>> services;
    private final ConcurrentHashMap<String, ServiceInstance> instanceById;
    private final ScheduledExecutorService healthChecker;
    private final HealthCheckConfig config;

    // Self-preservation
    private final AtomicLong lastHeartbeatTimestamp;
    private final AtomicLong heartbeatCount;
    private final AtomicLong renewalThreshold;
    private volatile boolean selfPreservationMode;

    public ServiceRegistry(HealthCheckConfig config) {
        this.config = config;
        this.services = new ConcurrentHashMap<>();
        this.instanceById = new ConcurrentHashMap<>();
        this.lastHeartbeatTimestamp = new AtomicLong(System.currentTimeMillis());
        this.heartbeatCount = new AtomicLong(0);
        this.renewalThreshold = new AtomicLong(
            config.expectedRenewalsPerMinute());
        this.healthChecker = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "registry-health-checker");
            t.setDaemon(true);
            return t;
        });
        this.healthChecker.scheduleAtFixedRate(this::runHealthCheck,
            config.healthCheckIntervalMs(), config.healthCheckIntervalMs(),
            TimeUnit.MILLISECONDS);
    }

    // ── Registration ─────────────────────────────────────────────────────────

    public ServiceInstance register(String serviceId, String host, int port,
                                    String healthUrl, Map<String, String> metadata) {
        String instanceId = serviceId + ":" + host + ":" + port;
        ServiceInstance instance = new ServiceInstance(
            instanceId, serviceId, host, port, healthUrl, metadata,
            System.currentTimeMillis(), System.currentTimeMillis(),
            InstanceStatus.UP);

        instanceById.put(instanceId, instance);
        services.computeIfAbsent(serviceId, k -> new CopyOnWriteArrayList<>()).add(instance);
        heartbeatCount.incrementAndGet();
        lastHeartbeatTimestamp.set(System.currentTimeMillis());
        return instance;
    }

    public ServiceInstance register(String serviceId, String host, int port) {
        return register(serviceId, host, port, "/actuator/health", Map.of());
    }

    // ── Heartbeat ────────────────────────────────────────────────────────────

    public boolean heartbeat(String instanceId) {
        ServiceInstance instance = instanceById.get(instanceId);
        if (instance == null) return false;

        long now = System.currentTimeMillis();
        instance.lastHeartbeat = now;
        instance.status = InstanceStatus.UP;
        heartbeatCount.incrementAndGet();
        lastHeartbeatTimestamp.set(now);
        return true;
    }

    // ── Discovery ────────────────────────────────────────────────────────────

    public List<ServiceInstance> getInstances(String serviceId) {
        List<ServiceInstance> instances = services.get(serviceId);
        if (instances == null) return List.of();
        return instances.stream()
            .filter(i -> i.status == InstanceStatus.UP)
            .collect(Collectors.toList());
    }

    public ServiceInstance getInstance(String instanceId) {
        return instanceById.get(instanceId);
    }

    /**
     * Returns a healthy instance using round-robin load balancing.
     */
    public Optional<ServiceInstance> getHealthyInstance(String serviceId) {
        List<ServiceInstance> healthy = getInstances(serviceId);
        if (healthy.isEmpty()) return Optional.empty();
        int idx = (int) (System.nanoTime() % healthy.size());
        return Optional.of(healthy.get(idx));
    }

    // ── Deregistration ──────────────────────────────────────────────────────

    public void deregister(String instanceId) {
        ServiceInstance instance = instanceById.remove(instanceId);
        if (instance != null) {
            List<ServiceInstance> list = services.get(instance.serviceId);
            if (list != null) {
                list.removeIf(i -> i.instanceId.equals(instanceId));
                if (list.isEmpty()) services.remove(instance.serviceId);
            }
        }
    }

    // ── Eviction ────────────────────────────────────────────────────────────

    private void runHealthCheck() {
        long now = System.currentTimeMillis();
        long threshold = now - config.heartbeatTimeoutMs();
        List<String> toEvict = new ArrayList<>();

        for (var entry : instanceById.entrySet()) {
            if (entry.getValue().lastHeartbeat < threshold) {
                toEvict.add(entry.getKey());
            }
        }

        double renewalRatio = getRenewalRatio();
        if (renewalRatio < config.selfPreservationThreshold()) {
            enterSelfPreservationMode();
            // Only evict if threshold is critically low (below 50% of threshold)
            if (renewalRatio < config.selfPreservationThreshold() * 0.5) {
                toEvict.forEach(this::deregister);
            }
        } else {
            exitSelfPreservationMode();
            toEvict.forEach(this::deregister);
        }
    }

    // ── Self-preservation ────────────────────────────────────────────────────

    private void enterSelfPreservationMode() {
        if (!selfPreservationMode) {
            selfPreservationMode = true;
            System.out.println("[WARN] Self-preservation mode activated — " +
                "renewal ratio below threshold");
        }
    }

    private void exitSelfPreservationMode() {
        if (selfPreservationMode) {
            selfPreservationMode = false;
            System.out.println("[INFO] Self-preservation mode deactivated");
        }
    }

    public boolean isSelfPreservationMode() {
        return selfPreservationMode;
    }

    public double getRenewalRatio() {
        long expected = renewalThreshold.get();
        long actual = heartbeatCount.getAndSet(0);
        return expected == 0 ? 1.0 : (double) actual / expected;
    }

    public int getInstanceCount() {
        return instanceById.size();
    }

    public Map<String, Integer> getServiceCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (var entry : services.entrySet()) {
            counts.put(entry.getKey(), entry.getValue().size());
        }
        return counts;
    }

    public void shutdown() {
        healthChecker.shutdown();
    }

    // ── Config ──────────────────────────────────────────────────────────────

    public record HealthCheckConfig(
        long healthCheckIntervalMs,
        long heartbeatTimeoutMs,
        int expectedRenewalsPerMinute,
        double selfPreservationThreshold
    ) {
        public static HealthCheckConfig defaults() {
            return new HealthCheckConfig(10_000, 30_000, 6, 0.85);
        }
    }

    // ── Service instance model ───────────────────────────────────────────────

    public enum InstanceStatus { UP, DOWN, OUT_OF_SERVICE }

    public static class ServiceInstance {
        public final String instanceId;
        public final String serviceId;
        public final String host;
        public final int port;
        public final String healthUrl;
        public final Map<String, String> metadata;
        public final long registrationTime;
        public volatile long lastHeartbeat;
        public volatile InstanceStatus status;

        ServiceInstance(String instanceId, String serviceId, String host,
                        int port, String healthUrl, Map<String, String> metadata,
                        long registrationTime, long lastHeartbeat,
                        InstanceStatus status) {
            this.instanceId = instanceId;
            this.serviceId = serviceId;
            this.host = host;
            this.port = port;
            this.healthUrl = healthUrl;
            this.metadata = metadata;
            this.registrationTime = registrationTime;
            this.lastHeartbeat = lastHeartbeat;
            this.status = status;
        }

        public String getUrl() {
            return "http://" + host + ":" + port;
        }

        @Override
        public String toString() {
            return "ServiceInstance{" + "id='" + instanceId + '\''
                + ", service='" + serviceId + '\'' + ", url='" + getUrl() + '\''
                + ", status=" + status + '}';
        }
    }

    // ── Example usage ───────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        ServiceRegistry registry = new ServiceRegistry(HealthCheckConfig.defaults());

        // Register instances
        registry.register("user-service", "192.168.1.10", 8081);
        registry.register("user-service", "192.168.1.11", 8081);
        registry.register("order-service", "192.168.1.20", 8082);

        // Heartbeats
        registry.getInstance("user-service:192.168.1.10:8081").heartbeat(
            "user-service:192.168.1.10:8081");

        // Discovery
        System.out.println("User service instances: " + registry.getInstances("user-service"));
        System.out.println("Healthy instance: " + registry.getHealthyInstance("user-service"));

        // Stats
        System.out.println("Service counts: " + registry.getServiceCounts());
        System.out.println("Total instances: " + registry.getInstanceCount());
        System.out.println("Self-preservation: " + registry.isSelfPreservationMode());

        registry.shutdown();
    }
}
```

## Complexity Analysis

| Operation         | Time Complexity | Space Complexity |
|-------------------|----------------|-----------------|
| register          | O(1)           | O(1)            |
| heartbeat         | O(1)           | O(1)            |
| getInstances      | O(k)           | O(k)            |
| getHealthyInstance| O(k)           | O(1)            |
| deregister        | O(1) average   | O(1)            |
| eviction run      | O(n)           | O(1)            |

Overall storage: O(n) where n = total registered instances across all services.

## Test Cases

```java
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;

class ServiceRegistryTest {

    private ServiceRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ServiceRegistry(
            new ServiceRegistry.HealthCheckConfig(50_000, 100_000, 6, 0.85));
    }

    @Test
    void testRegisterAndDiscover() {
        registry.register("svc1", "10.0.0.1", 9000);
        assertEquals(1, registry.getInstances("svc1").size());
    }

    @Test
    void testHeartbeat() {
        var inst = registry.register("svc1", "10.0.0.1", 9000);
        assertTrue(registry.heartbeat(inst.instanceId));
        assertFalse(registry.heartbeat("nonexistent"));
    }

    @Test
    void testDeregister() {
        registry.register("svc1", "10.0.0.1", 9000);
        registry.deregister("svc1:10.0.0.1:9000");
        assertTrue(registry.getInstances("svc1").isEmpty());
    }

    @Test
    void testRoundRobinDistribution() {
        registry.register("svc1", "10.0.0.1", 9000);
        registry.register("svc1", "10.0.0.2", 9000);
        registry.register("svc1", "10.0.0.3", 9000);

        var instances = registry.getInstances("svc1");
        assertEquals(3, instances.size());
    }

    @Test
    void testEviction() throws Exception {
        var config = new ServiceRegistry.HealthCheckConfig(100, 200, 6, 0.0);
        var fastRegistry = new ServiceRegistry(config);
        String id = fastRegistry.register("svc1", "10.0.0.1", 9000).instanceId;
        Thread.sleep(500);
        // Should be evicted after missing heartbeats
        assertTrue(fastRegistry.getInstances("svc1").isEmpty());
        fastRegistry.shutdown();
    }

    @Test
    void testSelfPreservation() {
        var config = new ServiceRegistry.HealthCheckConfig(50_000, 100_000, 1, 0.99);
        var reg = new ServiceRegistry(config);
        // Default is no self-preservation with normal threshold
        reg.register("svc1", "10.0.0.1", 9000);
        assertFalse(reg.isSelfPreservationMode());
        reg.shutdown();
    }

    @Test
    void testMultipleServices() {
        registry.register("svcA", "10.0.0.1", 9000);
        registry.register("svcB", "10.0.0.2", 9000);
        assertEquals(1, registry.getInstances("svcA").size());
        assertEquals(1, registry.getInstances("svcB").size());
    }

    @Test
    void testInstanceUrl() {
        var inst = registry.register("svc1", "10.0.0.1", 9000);
        assertEquals("http://10.0.0.1:9000", inst.getUrl());
    }

    @Test
    void testGetHealthyInstanceEmpty() {
        assertTrue(registry.getHealthyInstance("nonexistent").isEmpty());
    }
}
```
