# Scalability - MINI PROJECT

## Project Overview

**Project Name**: Scalable Product API
**Time Estimate**: 5-7 hours
**Difficulty**: Intermediate

Build a scalable product API demonstrating load balancing, caching, and horizontal scaling patterns.

---

## Project Structure

```
scalable-product-api/
├── src/main/java/com/example/product/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── cache/
│   └── loadbalancer/
├── pom.xml
└── docker-compose.yml
```

---

## Step 1: Product Service Implementation

### 1.1 Create Product Model

```java
package com.example.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private String category;
    private int stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors, getters, setters
}
```

### 1.2 Create Product Repository

```java
package com.example.product.repository;

import com.example.product.model.Product;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProductRepository {
    private final Map<String, Product> products = new ConcurrentHashMap<>();

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        product.setUpdatedAt(LocalDateTime.now());
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public List<Product> findByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .toList();
    }

    public boolean existsById(String id) {
        return products.containsKey(id);
    }
}
```

### 1.3 Create Product Service

```java
package com.example.product.service;

import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import java.util.List;

public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product getProduct(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return repository.findByCategory(category);
    }
}
```

---

## Step 2: Implement In-Memory Cache

### 2.1 Create TTL Cache

```java
package com.example.product.cache;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ProductCache {
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;
    private final int maxSize;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ProductCache(long ttlMillis, int maxSize) {
        this.ttlMillis = ttlMillis;
        this.maxSize = maxSize;
    }

    public void put(String key, Product value) {
        lock.writeLock().lock();
        try {
            if (cache.size() >= maxSize) {
                evictOldest();
            }
            cache.put(key, new CacheEntry(value, System.currentTimeMillis() + ttlMillis));
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Product get(String key) {
        lock.readLock().lock();
        try {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                if (entry != null) {
                    cache.remove(key);
                }
                return null;
            }
            entry.recordAccess();
            return entry.product;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void invalidate(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }

    private void evictOldest() {
        cache.entrySet().stream()
            .min(Comparator.comparingLong(e -> e.getValue().lastAccessTime))
            .ifPresent(e -> cache.remove(e.getKey()));
    }

    private static class CacheEntry {
        final Product product;
        final long expiresAt;
        volatile long lastAccessTime;

        CacheEntry(Product product, long expiresAt) {
            this.product = product;
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
}
```

### 2.2 Create Cached Product Service

```java
package com.example.product.service;

import com.example.product.cache.ProductCache;
import com.example.product.model.Product;
import com.example.product.repository.ProductRepository;
import java.util.List;

public class CachedProductService {
    private final ProductService productService;
    private final ProductCache cache;
    private static final long CACHE_TTL_MS = 60_000; // 1 minute

    public CachedProductService(ProductRepository repository, ProductCache cache) {
        this.productService = new ProductService(repository);
        this.cache = cache;
    }

    public Product getProduct(String id) {
        Product cached = cache.get("product:" + id);
        if (cached != null) {
            return cached;
        }

        Product product = productService.getProduct(id);
        cache.put("product:" + id, product);
        return product;
    }

    public List<Product> getAllProducts() {
        List<Product> cached = cache.get("products:all");
        if (cached != null) {
            return cached;
        }

        List<Product> products = productService.getAllProducts();
        cache.put("products:all", products);
        return products;
    }

    public void invalidateProduct(String id) {
        cache.invalidate("product:" + id);
    }
}
```

---

## Step 3: Implement Load Balancer

### 3.1 Create Server Class

```java
package com.example.loadbalancer;

import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private final String id;
    private final String host;
    private final int port;
    private final AtomicInteger activeConnections = new AtomicInteger(0);
    private volatile boolean healthy = true;

    public Server(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public void incrementConnections() {
        activeConnections.incrementAndGet();
    }

    public void decrementConnections() {
        activeConnections.decrementAndGet();
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

    public String getUrl() {
        return host + ":" + port;
    }

    public String getId() { return id; }
}
```

### 3.2 Create Load Balancer

```java
package com.example.loadbalancer;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoadBalancer {
    private final List<Server> servers = new CopyOnWriteArrayList<>();
    private int roundRobinIndex = 0;
    private final LoadBalancingAlgorithm algorithm;

    public enum LoadBalancingAlgorithm {
        ROUND_ROBIN, LEAST_CONNECTIONS, RANDOM
    }

    public LoadBalancer(LoadBalancingAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public void addServer(Server server) {
        servers.add(server);
    }

    public void removeServer(String serverId) {
        servers.removeIf(s -> s.getId().equals(serverId));
    }

    public Server selectServer() {
        List<Server> healthyServers = new ArrayList<>();
        for (Server server : servers) {
            if (server.isHealthy()) {
                healthyServers.add(server);
            }
        }

        if (healthyServers.isEmpty()) {
            throw new RuntimeException("No healthy servers available");
        }

        return switch (algorithm) {
            case ROUND_ROBIN -> selectRoundRobin(healthyServers);
            case LEAST_CONNECTIONS -> selectLeastConnections(healthyServers);
            case RANDOM -> selectRandom(healthyServers);
        };
    }

    private Server selectRoundRobin(List<Server> servers) {
        Server server = servers.get(roundRobinIndex % servers.size());
        roundRobinIndex++;
        return server;
    }

    private Server selectLeastConnections(List<Server> servers) {
        return servers.stream()
            .min(Comparator.comparingInt(Server::getActiveConnections))
            .orElse(servers.get(0));
    }

    private Server selectRandom(List<Server> servers) {
        return servers.get(new Random().nextInt(servers.size()));
    }
}
```

### 3.3 Create Health Checker

```java
package com.example.loadbalancer;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;

public class HealthChecker {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<String, ServerHealth> healthMap = new ConcurrentHashMap<>();

    public void startMonitoring(LoadBalancer loadBalancer, int intervalSeconds) {
        scheduler.scheduleAtFixedRate(() -> {
            for (Server server : loadBalancer.getServers()) {
                checkAndUpdateHealth(server);
            }
        }, 0, intervalSeconds, TimeUnit.SECONDS);
    }

    private void checkAndUpdateHealth(Server server) {
        boolean healthy = performHealthCheck(server.getUrl());
        server.setHealthy(healthy);

        ServerHealth health = healthMap.computeIfAbsent(server.getId(),
            k -> new ServerHealth());
        health.updateHealth(healthy);
    }

    private boolean performHealthCheck(String url) {
        try {
            URL uri = new URL("http://" + url + "/health");
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setConnectTimeout(2000);
            connection.setReadTimeout(2000);
            return connection.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private static class ServerHealth {
        private int consecutiveFailures = 0;

        public void updateHealth(boolean healthy) {
            if (healthy) {
                consecutiveFailures = 0;
            } else {
                consecutiveFailures++;
            }
        }

        public boolean isHealthy() {
            return consecutiveFailures < 3;
        }
    }
}
```

---

## Step 4: Docker Configuration

### 4.1 Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/product-service-1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 4.2 Docker Compose for Scaling

```yaml
version: '3.8'

services:
  product-api-1:
    build: .
    ports:
      - "8081:8080"
    environment:
      - SERVER_PORT=8080
      - INSTANCE_ID=instance-1

  product-api-2:
    build: .
    ports:
      - "8082:8080"
    environment:
      - SERVER_PORT=8080
      - INSTANCE_ID=instance-2

  product-api-3:
    build: .
    ports:
      - "8083:8080"
    environment:
      - SERVER_PORT=8080
      - INSTANCE_ID=instance-3

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro

  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
```

### 4.3 NGINX Load Balancer Config

```nginx
events {
    worker_connections 1024;
}

http {
    upstream product_backend {
        least_conn;
        server product-api-1:8080;
        server product-api-2:8080;
        server product-api-3:8080;
    }

    server {
        listen 80;

        location / {
            proxy_pass http://product_backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        }

        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }
    }
}
```

---

## Step 5: Testing

### 5.1 Load Testing Script

```bash
#!/bin/bash

# Install hey (HTTP load generator)
go install github.com/rakyll/hey@latest

# Run load test
hey -n 10000 -c 100 http://localhost/api/products
```

### 5.2 Expected Results

| Instance | Requests | CPU Usage | Response Time |
|----------|----------|-----------|---------------|
| Instance 1 | ~3333 | ~60% | ~50ms |
| Instance 2 | ~3333 | ~60% | ~50ms |
| Instance 3 | ~3334 | ~60% | ~50ms |

---

## Project Deliverables

1. ✅ Product service with layered architecture
2. ✅ In-memory TTL cache implementation
3. ✅ Load balancer with multiple algorithms
4. ✅ Health checker for server monitoring
5. ✅ Docker setup for horizontal scaling
6. ✅ NGINX configuration for load balancing

---

## Extension Ideas

1. **Add Redis Cache**: Replace in-memory cache with Redis
2. **Add Database**: Add PostgreSQL with read replicas
3. **Add Auto-Scaling**: Configure Kubernetes HPA
4. **Add Metrics**: Add Prometheus metrics
5. **Add Distributed Tracing**: Add Zipkin integration