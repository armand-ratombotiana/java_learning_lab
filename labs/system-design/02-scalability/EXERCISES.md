# Scalability - EXERCISES

## Exercise Set Overview
This exercise set contains 22 exercises covering vertical scaling, horizontal scaling, load balancing, caching, and database scaling.

---

## Part 1: Vertical vs Horizontal Scaling (1-5)

### Exercise 1: Identify Scaling Type
**Difficulty**: Easy

Classify the following scaling scenarios as vertical or horizontal:
- Adding 64GB RAM to your database server
- Deploying 5 identical application instances behind a load balancer
- Upgrading from 8-core to 32-core CPU
- Adding 3 new server nodes to a Kubernetes cluster

**Answer**:
- Adding RAM to DB server: Vertical
- Deploying 5 instances: Horizontal
- Upgrading CPU: Vertical
- Adding 3 nodes: Horizontal

---

### Exercise 2: Calculate Hardware Upgrade Cost
**Difficulty**: Medium

Calculate the monthly cost difference between vertical and horizontal scaling:
- Current: 4-core, 16GB RAM server at $100/month
- Vertical upgrade: 16-core, 64GB RAM at $400/month
- Horizontal: 4 servers with 4-core, 16GB each at $100 each

**Answer**:
- Vertical: $400/month (4x increase)
- Horizontal: $400/month (4x increase)
- At this scale, costs are equal. However, horizontal provides fault tolerance.

---

### Exercise 3: Identify Bottlenecks for Vertical Scaling
**Difficulty**: Medium

For a monolithic application, identify what cannot be improved by vertical scaling:
- Database CPU at 100%
- Network bandwidth limit
- Application memory usage
- Disk I/O throughput

**Answer**:
- All can potentially be improved by vertical scaling EXCEPT network bandwidth limit (depends on data center/ISP, not server hardware)

---

### Exercise 4: Design Horizontal Architecture
**Difficulty**: Hard

Design a horizontally scalable architecture for an e-commerce product catalog that handles 10,000 products and 1000 concurrent users.

**Answer**:
1. Stateless application servers behind load balancer
2. Read replicas for product catalog queries
3. CDN for product images
4. Redis cache for frequently accessed products
5. Database connection pooling

---

### Exercise 5: Calculate Scaling Requirements
**Difficulty**: Hard

Given:
- Current system handles 100 RPS with 4-core CPU at 80% utilization
- Expected peak is 500 RPS

Calculate:
- Minimum instances needed (horizontal)
- CPU cores needed (vertical)

**Answer**:
- Horizontal: 500/100 × 1.2 (buffer) = 6 instances
- Vertical: 500/100 × 4 = 20 cores

---

## Part 2: Load Balancing (6-12)

### Exercise 6: Choose Load Balancing Algorithm
**Difficulty**: Medium

For each scenario, select the best load balancing algorithm:
1. Video streaming service with varying content sizes
2. API with consistent request sizes
3. WebSocket connections with long-lived sessions

**Answer**:
1. Least Connections (handles varying sizes better)
2. Round Robin (consistent requests)
3. Source IP Hash (sticky sessions)

---

### Exercise 7: Implement Round Robin
**Difficulty**: Medium

Write code for a simple round-robin load balancer that distributes requests across 3 servers.

**Answer**:

```java
public class RoundRobinLoadBalancer {
    private final List<String> servers;
    private int currentIndex = 0;

    public RoundRobinLoadBalancer(List<String> servers) {
        this.servers = servers;
    }

    public String getNextServer() {
        String server = servers.get(currentIndex);
        currentIndex = (currentIndex + 1) % servers.size();
        return server;
    }
}
```

---

### Exercise 8: Add Health Checks
**Difficulty**: Medium

Add health check functionality to remove unhealthy servers from rotation.

**Answer**:

```java
public class LoadBalancerWithHealthCheck {
    private final Map<String, ServerStatus> servers = new ConcurrentHashMap<>();

    public void updateServerHealth(String server, boolean healthy) {
        servers.get(server).setHealthy(healthy);
    }

    public String getNextHealthyServer() {
        return servers.entrySet().stream()
            .filter(e -> e.getValue().isHealthy())
            .findFirst()
            .map(Map.Entry::getKey)
            .orElseThrow(() -> new RuntimeException("No healthy servers"));
    }

    static class ServerStatus {
        private volatile boolean healthy = true;
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }
    }
}
```

---

### Exercise 9: Configure Layer 4 vs Layer 7
**Difficulty**: Medium

Explain when to use Layer 4 vs Layer 7 load balancing.

**Answer**:
- Layer 4: TCP/UDP level, faster, for simple load distribution
- Layer 7: Application level, can inspect HTTP, for routing based on URL/headers/cookies

---

### Exercise 10: Design Session Affinity
**Difficulty**: Hard

Design a load balancer that maintains session affinity using consistent hashing.

**Answer**:

```java
public class ConsistentHashLoadBalancer {
    private final HashFunction hashFunction = Hashing.murmur3();
    private final TreeMap<Long, String> ring = new TreeMap<>();

    public void addServer(String server) {
        long hash = hashFunction.hashString(server, StandardCharsets.UTF_8).asLong();
        ring.put(hash, server);
    }

    public String getServer(String key) {
        long hash = hashFunction.hashString(key, StandardCharsets.UTF_8).asLong();
        Map.Entry<Long, String> entry = ring.higherEntry(hash);
        return entry != null ? entry.getValue() : ring.firstEntry().getValue();
    }
}
```

---

### Exercise 11: Calculate Load Distribution
**Difficulty**: Easy

With 3 servers and 9 requests, show request distribution for:
- Round Robin
- Least Connections (if connections are [3, 2, 1])

**Answer**:
- Round Robin: 3, 3, 3 requests each
- Least Connections: 3→server2, 2→server3, 1→server1

---

### Exercise 12: Configure SSL Termination
**Difficulty**: Medium

Configure load balancer for SSL termination.

**Answer**:

```yaml
# nginx.conf
upstream backend {
    server 10.0.0.1:8080;
    server 10.0.0.2:8080;
}

server {
    listen 443 ssl;
    ssl_certificate /etc/ssl/certs/server.crt;
    ssl_certificate_key /etc/ssl/private/server.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    
    location / {
        proxy_pass http://backend;
    }
}
```

---

## Part 3: Caching (13-17)

### Exercise 13: Choose Cache Strategy
**Difficulty**: Medium

For each scenario, choose the appropriate caching strategy:
1. User profile data (changes infrequently)
2. Shopping cart (changes frequently)
3. Product inventory (changes on purchase)

**Answer**:
1. Cache-aside with long TTL
2. Don't cache, or write-through with short TTL
3. Event-based invalidation

---

### Exercise 14: Calculate Cache Hit Rate
**Difficulty**: Medium

Given: 1000 requests, 800 cache hits
Calculate: Hit rate, miss rate, efficiency improvement

**Answer**:
- Hit rate: 80%
- Miss rate: 20%
- With 1ms cache vs 100ms DB: Avg = 800×1 + 200×100 = 20.8ms vs 100ms → 79% improvement

---

### Exercise 15: Implement Cache-Aside
**Difficulty**: Hard

Implement cache-aside pattern for a product service.

**Answer**:

```java
public class ProductService {
    private final Cache<String, Product> cache;
    private final ProductRepository repository;

    public Product getProduct(String id) {
        Product product = cache.get(id);
        if (product == null) {
            product = repository.findById(id);
            if (product != null) {
                cache.put(id, product);
            }
        }
        return product;
    }

    public void updateProduct(Product product) {
        repository.save(product);
        cache.invalidate(product.getId());
    }
}
```

---

### Exercise 16: Design Cache Invalidation
**Difficulty**: Hard

Design a cache invalidation strategy for a product price update that propagates to all cache nodes.

**Answer**:
1. Database triggers event on price update
2. Event published to cache invalidation topic
3. All cache nodes subscribe and invalidate product price
4. Use versioned keys for additional safety

---

### Exercise 17: Configure Redis Cache
**Difficulty**: Medium

Configure Redis for a session store with 30-minute TTL.

**Answer**:

```yaml
# application.yml
spring:
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

# Session config
session:
  store: redis
  timeout: 30m
```

---

## Part 4: Database Scaling (18-22)

### Exercise 18: Design Read Replica Strategy
**Difficulty**: Medium

Design a read replica strategy for a social media application.

**Answer**:
- Primary: Handle all writes
- 3 read replicas: For timeline, profile, and search queries
- Application-level routing based on query type
- Accept eventual consistency for reads

---

### Exercise 19: Choose Sharding Key
**Difficulty**: Hard

For an e-commerce system, evaluate these as potential sharding keys:
- User ID
- Order ID
- Product ID

**Answer**:
- User ID: Good for user-centric queries (orders, cart)
- Order ID: Good for order lookups, but not user queries
- Product ID: Good for product queries, not user queries

---

### Exercise 20: Implement Sharding Router
**Difficulty**: Hard

Implement a hash-based sharding router for user data.

**Answer**:

```java
public class UserShardRouter {
    private final int shardCount;

    public UserShardRouter(int shardCount) {
        this.shardCount = shardCount;
    }

    public int getShard(String userId) {
        return Math.abs(userId.hashCode()) % shardCount;
    }

    public String getConnectionString(String userId) {
        int shard = getShard(userId);
        return "jdbc:postgresql://shard-" + shard + ":5432/users";
    }
}
```

---

### Exercise 21: Calculate Replica Lag Impact
**Difficulty**: Medium

If replica lag is 2 seconds for an e-commerce order status:
- Is it acceptable for "Order Placed" confirmation?
- Is it acceptable for payment status?

**Answer**:
- Order Placed: Yes, 2 seconds acceptable
- Payment status: Critical - may need to read from primary

---

### Exercise 22: Configure Connection Pooling
**Difficulty**: Medium

Configure HikariCP for optimal performance with 10 read replicas.

**Answer**:

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      pool-name: ReadReplicas
```

---

## Summary

These 22 exercises covered:
- Vertical and horizontal scaling decisions
- Load balancing algorithms and health checks
- Caching strategies and invalidation
- Database scaling with replicas and sharding