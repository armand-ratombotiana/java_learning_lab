# Scalability - CODE DEEP DIVE

## Table of Contents
1. [Load Balancer Implementation](#lb-code)
2. [Caching Implementation](#cache-code)
3. [Horizontal Scaling Pattern](#scale-code)
4. [Database Scaling](#db-code)
5. [Auto-Scaling Configuration](#autoscale-code)

---

## 1. Load Balancer Implementation <a name="lb-code"></a>

### Simple HTTP Load Balancer

```java
package com.example.loadbalancer;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SimpleLoadBalancer {
    private final List<Server> servers;
    private int currentIndex = 0;
    private final LoadBalancingStrategy strategy;

    public enum LoadBalancingStrategy {
        ROUND_ROBIN, LEAST_CONNECTIONS, RANDOM
    }

    public SimpleLoadBalancer(List<String> serverAddresses, LoadBalancingStrategy strategy) {
        this.strategy = strategy;
        this.servers = serverAddresses.stream()
            .map(Server::new)
            .toList();
    }

    public String route(String request) throws IOException {
        Server server = selectServer();
        return server.forward(request);
    }

    private Server selectServer() {
        return switch (strategy) {
            case ROUND_ROBIN -> selectRoundRobin();
            case LEAST_CONNECTIONS -> selectLeastConnections();
            case RANDOM -> selectRandom();
        };
    }

    private Server selectRoundRobin() {
        Server server = servers.get(currentIndex);
        currentIndex = (currentIndex + 1) % servers.size();
        return server;
    }

    private Server selectLeastConnections() {
        return servers.stream()
            .min(Comparator.comparingInt(Server::getActiveConnections))
            .orElse(servers.get(0));
    }

    private Server selectRandom() {
        return servers.get(new Random().nextInt(servers.size()));
    }

    public static class Server {
        private final String address;
        private final int port;
        private final AtomicInteger activeConnections = new AtomicInteger(0);
        private boolean healthy = true;

        public Server(String address) {
            String[] parts = address.split(":");
            this.address = parts[0];
            this.port = parts.length > 1 ? Integer.parseInt(parts[1]) : 80;
        }

        public String forward(String request) throws IOException {
            activeConnections.incrementAndGet();
            try {
                Socket socket = new Socket(address, port);
                // Send request and receive response
                return "Response from " + address;
            } finally {
                activeConnections.decrementAndGet();
            }
        }

        public int getActiveConnections() {
            return activeConnections.get();
        }

        public boolean isHealthy() {
            return healthy;
        }

        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }
    }
}
```

### Health Check Implementation

```java
package com.example.loadbalancer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class HealthCheck {
    private final ExecutorService executor = Executors.newScheduledThreadPool(4);
    private final Map<String, ServerHealth> serverHealthMap = new ConcurrentHashMap<>();

    public void startMonitoring(List<String> servers, int intervalSeconds) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            servers.forEach(this::checkServerHealth);
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    private void checkServerHealth(String serverAddress) {
        String[] parts = serverAddress.split(":");
        String host = parts[0];
        int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 80;

        long startTime = System.currentTimeMillis();
        boolean healthy = performHealthCheck(host, port);
        long responseTime = System.currentTimeMillis() - startTime;

        ServerHealth health = serverHealthMap.computeIfAbsent(serverAddress, 
            k -> new ServerHealth());
        
        health.update(healthy, responseTime);
    }

    private boolean performHealthCheck(String host, int port) {
        try {
            URL url = new URL("http://" + host + ":" + port + "/health");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isServerHealthy(String serverAddress) {
        ServerHealth health = serverHealthMap.get(serverAddress);
        return health != null && health.isHealthy();
    }

    public static class ServerHealth {
        private final Queue<Long> responseTimes = new LinkedList<>();
        private int consecutiveFailures = 0;
        private int totalChecks = 0;
        private int successfulChecks = 0;

        private static final int MAX_RESPONSE_TIMES = 10;
        private static final int FAILURE_THRESHOLD = 3;

        public void update(boolean healthy, long responseTime) {
            totalChecks++;
            if (healthy) {
                consecutiveFailures = 0;
                successfulChecks++;
                responseTimes.add(responseTime);
                if (responseTimes.size() > MAX_RESPONSE_TIMES) {
                    responseTimes.poll();
                }
            } else {
                consecutiveFailures++;
            }
        }

        public boolean isHealthy() {
            return consecutiveFailures < FAILURE_THRESHOLD;
        }

        public double getAverageResponseTime() {
            return responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);
        }

        public double getSuccessRate() {
            return totalChecks > 0 ? (double) successfulChecks / totalChecks : 0;
        }
    }
}
```

---

## 2. Caching Implementation <a name="cache-code"></a>

### In-Memory Cache with TTL

```java
package com.example.cache;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TTLCache<K, V> {
    private final Map<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    private final int maxSize;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final ScheduledExecutorService cleanupExecutor;

    public TTLCache(long ttlMillis, int maxSize) {
        this.ttlMillis = ttlMillis;
        this.maxSize = maxSize;
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        startCleanupTask();
    }

    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (cache.size() >= maxSize) {
                evictOldest();
            }
            cache.put(key, new CacheEntry<>(value, System.currentTimeMillis() + ttlMillis));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<V> get(K key) {
        lock.readLock().lock();
        try {
            CacheEntry<V> entry = cache.get(key);
            if (entry == null) {
                return Optional.empty();
            }
            if (entry.isExpired()) {
                cache.remove(key);
                return Optional.empty();
            }
            entry.recordAccess();
            return Optional.of(entry.value);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void evictOldest() {
        cache.entrySet().stream()
            .min(Comparator.comparingLong(e -> e.getValue().lastAccessTime))
            .ifPresent(e -> cache.remove(e.getKey()));
    }

    private void startCleanupTask() {
        cleanupExecutor.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            cache.entrySet().removeIf(e -> e.getValue().isExpired());
        }, ttlMillis / 2, ttlMillis / 2, TimeUnit.MILLISECONDS);
    }

    private static class CacheEntry<V> {
        final V value;
        final long expiresAt;
        volatile long lastAccessTime;

        CacheEntry(V value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
            this.lastAccessTime = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiresAt;
        }

        void recordAccess() {
            this.lastAccessTime = System.currentTimeMillis();
        }
    }

    public void shutdown() {
        cleanupExecutor.shutdown();
    }
}
```

### Cache-Aside Pattern Implementation

```java
package com.example.cache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class CacheAsidePattern<K, V> {
    private final TTLCache<K, V> cache;
    private final Function<K, V> dataLoader;

    public CacheAsidePattern(long ttlMillis, int maxSize, Function<K, V> dataLoader) {
        this.cache = new TTLCache<>(ttlMillis, maxSize);
        this.dataLoader = dataLoader;
    }

    public V getOrLoad(K key) {
        Optional<V> cached = cache.get(key);
        if (cached.isPresent()) {
            return cached.get();
        }

        V value = dataLoader.apply(key);
        if (value != null) {
            cache.put(key, value);
        }
        return value;
    }

    public void invalidate(K key) {
        // Manual invalidation when data changes
        // Note: Not implemented - would need access to internal cache
    }

    public void invalidateAll() {
        // Clear all cache entries
        // Note: Would need to expose clear method on cache
    }
}

class ProductService {
    private final CacheAsidePattern<String, Product> productCache;
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
        this.productCache = new CacheAsidePattern<>(
            300_000, // 5 minutes TTL
            1000,    // Max 1000 items
            sku -> repository.findBySku(sku)
        );
    }

    public Product getProductBySku(String sku) {
        return productCache.getOrLoad(sku);
    }

    public void updateProduct(Product product) {
        repository.save(product);
        // Cache will be refreshed on next read
    }
}

interface ProductRepository {
    Product findBySku(String sku);
    void save(Product product);
}

class Product {}
```

### Write-Through Cache Implementation

```java
package com.example.cache;

import java.util.concurrent.*;

public class WriteThroughCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final Map<K, V> backingStore = new ConcurrentHashMap<>();

    public V write(K key, V value) {
        cache.put(key, value);
        backingStore.put(key, value);
        return value;
    }

    public V read(K key) {
        return cache.computeIfAbsent(key, backingStore::get);
    }

    public CompletableFuture<V> writeAsync(K key, V value) {
        return CompletableFuture.supplyAsync(() -> write(key, value));
    }
}
```

---

## 3. Horizontal Scaling Pattern <a name="scale-code"></a>

### Stateless Service Implementation

```java
package com.example.scaling;

import java.util.concurrent.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class StatelessProductService implements Servlet {
    private final ExecutorService executor = Executors.newFixedThreadPool(100);
    private ProductRepository repository;

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();
        
        switch (request.getMethod()) {
            case "GET" -> handleGet(request, response);
            case "POST" -> handlePost(request, response);
            case "PUT" -> handlePut(request, response);
            case "DELETE" -> handleDelete(request, response);
        }
    }

    private void handleGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String productId = req.getParameter("id");
        Product product = repository.findById(productId);
        
        res.setContentType("application/json");
        res.getWriter().write(toJson(product));
    }

    // No session storage - stateless!
    
    private ProductRepository getRepository() {
        return repository;
    }
}
```

### Session Management for Scaling

```java
package com.example.scaling;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class DistributedSessionManager {
    private final ConcurrentHashMap<String, SessionData> sessions = new ConcurrentHashMap<>();

    public String createSession(String userId) {
        String sessionId = UUID.randomUUID().toString();
        SessionData session = new SessionData(userId, System.currentTimeMillis());
        sessions.put(sessionId, session);
        return sessionId;
    }

    public SessionData getSession(String sessionId) {
        SessionData session = sessions.get(sessionId);
        if (session != null && !session.isExpired()) {
            session.updateLastAccess();
            return session;
        }
        return null;
    }

    public void invalidate(String sessionId) {
        sessions.remove(sessionId);
    }

    public static class SessionData {
        private final String userId;
        private final long createdAt;
        private volatile long lastAccessAt;
        private final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

        public SessionData(String userId, long createdAt) {
            this.userId = userId;
            this.createdAt = createdAt;
            this.lastAccessAt = createdAt;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - lastAccessAt > 3600000; // 1 hour
        }

        public void updateLastAccess() {
            this.lastAccessAt = System.currentTimeMillis();
        }

        public String getUserId() { return userId; }
        public Object getAttribute(String key) { return attributes.get(key); }
        public void setAttribute(String key, Object value) { attributes.put(key, value); }
    }
}
```

### Service Registry Implementation

```java
package com.example.scaling;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ServiceRegistry {
    private final Map<String, List<ServiceInstance>> services = new ConcurrentHashMap<>();
    private final ScheduledExecutorService healthCheckExecutor = Executors.newScheduledThreadPool(1);

    public void register(String serviceName, String host, int port) {
        ServiceInstance instance = new ServiceInstance(
            serviceName + "-" + UUID.randomUUID(),
            serviceName,
            host,
            port,
            true
        );
        
        services.computeIfAbsent(serviceName, k -> new CopyOnWriteArrayList<>())
            .add(instance);
        
        System.out.println("Registered: " + instance.getInstanceId());
    }

    public Optional<ServiceInstance> discover(String serviceName) {
        List<ServiceInstance> healthyInstances = services.getOrDefault(serviceName, Collections.emptyList())
            .stream()
            .filter(ServiceInstance::isHealthy)
            .collect(Collectors.toList());
        
        if (healthyInstances.isEmpty()) {
            return Optional.empty();
        }
        
        // Random selection for load distribution
        return healthyInstances.stream()
            .skip(new Random().nextInt(healthyInstances.size()))
            .findFirst();
    }

    public void deregister(String instanceId) {
        services.values().forEach(instances -> 
            instances.removeIf(i -> i.getInstanceId().equals(instanceId))
        );
    }

    public static class ServiceInstance {
        private final String instanceId;
        private final String serviceName;
        private final String host;
        private final int port;
        private volatile boolean healthy;

        public ServiceInstance(String instanceId, String serviceName, String host, int port, boolean healthy) {
            this.instanceId = instanceId;
            this.serviceName = serviceName;
            this.host = host;
            this.port = port;
            this.healthy = healthy;
        }

        public String getInstanceId() { return instanceId; }
        public String getServiceName() { return serviceName; }
        public String getHost() { return host; }
        public int getPort() { return port; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
    }
}
```

---

## 4. Database Scaling <a name="db-code"></a>

### Read Replica Implementation

```java
package com.example.db;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;

public class ReadReplicaRouter {
    private final String primaryUrl;
    private final List<String> replicaUrls;
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private int roundRobinIndex = 0;

    public ReadReplicaRouter(String primaryUrl, List<String> replicaUrls) {
        this.primaryUrl = primaryUrl;
        this.replicaUrls = replicaUrls;
    }

    public Connection getReadConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            return conn;
        }

        // Simple round-robin for replica selection
        synchronized (this) {
            String replicaUrl = replicaUrls.get(roundRobinIndex);
            roundRobinIndex = (roundRobinIndex + 1) % replicaUrls.size();
            conn = DriverManager.getConnection(replicaUrl);
        }

        connectionHolder.set(conn);
        return conn;
    }

    public Connection getWriteConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn != null) {
            return conn;
        }

        conn = DriverManager.getConnection(primaryUrl);
        connectionHolder.set(conn);
        return conn;
    }

    public <T> T executeRead(ReadOperation<T> operation) throws SQLException {
        try (Connection conn = getReadConnection()) {
            return operation.execute(conn);
        }
    }

    public <T> T executeWrite(WriteOperation<T> operation) throws SQLException {
        try (Connection conn = getWriteConnection()) {
            return operation.execute(conn);
        }
    }

    @FunctionalInterface
    interface ReadOperation<T> {
        T execute(Connection conn) throws SQLException;
    }

    @FunctionalInterface
    interface WriteOperation<T> {
        T execute(Connection conn) throws SQLException;
    }
}
```

### Database Sharding Implementation

```java
package com.example.db;

import java.util.*;

public class DatabaseSharding {
    private final int shardCount;
    private final ShardRouter router;

    public DatabaseSharding(int shardCount) {
        this.shardCount = shardCount;
        this.router = new HashShardRouter(shardCount);
    }

    public int getShardId(String key) {
        return router.getShardId(key);
    }

    public String getShardConnectionString(String key) {
        int shardId = getShardId(key);
        return "jdbc:postgresql://shard" + shardId + "-host:5432/ecommerce_shard" + shardId;
    }

    public static abstract class ShardRouter {
        protected final int shardCount;

        public ShardRouter(int shardCount) {
            this.shardCount = shardCount;
        }

        public abstract int getShardId(String key);
    }

    public static class HashShardRouter extends ShardRouter {
        public HashShardRouter(int shardCount) {
            super(shardCount);
        }

        @Override
        public int getShardId(String key) {
            int hash = Math.abs(key.hashCode());
            return hash % shardCount;
        }
    }

    public static class RangeShardRouter extends ShardRouter {
        public RangeShardRouter(int shardCount) {
            super(shardCount);
        }

        @Override
        public int getShardId(String key) {
            try {
                int userId = Integer.parseInt(key);
                return (userId / 100000) % shardCount;
            } catch (NumberFormatException e) {
                return Math.abs(key.hashCode()) % shardCount;
            }
        }
    }
}

class ShardedProductRepository {
    private final List<ProductRepository> shards;

    public ShardedProductRepository(int shardCount) {
        this.shards = new ArrayList<>();
        for (int i = 0; i < shardCount; i++) {
            shards.add(new ShardRepository(i));
        }
    }

    public void save(Product product) {
        int shardId = new DatabaseSharding(shards.size()).getShardId(product.getId());
        shards.get(shardId).save(product);
    }

    public Product findById(String id) {
        int shardId = new DatabaseSharding(shards.size()).getShardId(id);
        return shards.get(shardId).findById(id);
    }

    static class ShardRepository implements ProductRepository {
        private final int shardId;

        ShardRepository(int shardId) {
            this.shardId = shardId;
        }

        @Override
        public Product findById(String id) {
            return new Product("Shard-" + shardId + "-" + id);
        }

        @Override
        public void save(Product product) {
            System.out.println("Saved to shard: " + shardId);
        }
    }

    interface ProductRepository {
        Product findById(String id);
        void save(Product product);
    }
}

class Product {
    private String id;
    public Product(String id) { this.id = id; }
    public String getId() { return id; }
}
```

---

## 5. Auto-Scaling Configuration <a name="autoscale-code"></a>

### Kubernetes Auto-Scaling

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: product-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: product-service
  template:
    metadata:
      labels:
        app: product-service
    spec:
      containers:
      - name: product-service
        image: product-service:1.0
        ports:
        - containerPort: 8080
        resources:
          requests:
            cpu: "500m"
            memory: "512Mi"
          limits:
            cpu: "2000m"
            memory: "2Gi"
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: product-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: product-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
      - type: Percent
        value: 100
        periodSeconds: 15
      - type: Pods
        value: 4
        periodSeconds: 15
```

### Prometheus Metrics for Scaling

```yaml
global:
  scrape_interval: 15s

scrape_configs:
- job_name: 'product-service'
  kubernetes_sd_configs:
  - role: pod
  metrics_path: '/actuator/prometheus'
```

```java
package com.example.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}

class MetricsConfig {
    private final MeterRegistry registry;

    public MetricsConfig(MeterRegistry registry) {
        this.registry = registry;
        registerCustomMetrics();
    }

    private void registerCustomMetrics() {
        Counter requestsCounter = Counter.builder("http.requests.total")
            .tag("service", "product-service")
            .register(registry);

        Gauge.builder("http.requests.active", this, m -> m.getActiveRequests())
            .tag("service", "product-service")
            .register(registry);

        Timer requestTimer = Timer.builder("http.request.duration")
            .tag("service", "product-service")
            .register(registry);
    }

    private int getActiveRequests() {
        return 0; // Track active requests
    }
}
```

---

## Summary

This deep dive covered:

1. **Load Balancer**: Round-robin, least connections, and random strategies with health checks
2. **Caching**: TTL cache, cache-aside pattern, write-through pattern
3. **Horizontal Scaling**: Stateless services, session management, service registry
4. **Database Scaling**: Read replica routing, database sharding
5. **Auto-Scaling**: Kubernetes HPA configuration, Prometheus metrics

These implementations form the foundation of scalable distributed systems.