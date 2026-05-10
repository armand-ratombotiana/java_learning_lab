# Hazelcast Projects

This directory contains hands-on projects using Hazelcast, a leading in-memory data grid platform for Java. Hazelcast provides distributed caching, data partitioning, and clustering capabilities.

## Project Overview

Hazelcast enables building high-performance, scalable applications with distributed data structures. This module covers two projects demonstrating different Hazelcast use cases.

---

# Mini-Project: Distributed Cache (2-4 Hours)

## Project Description

Build a distributed caching system using Hazelcast for session storage and data caching. This project demonstrates Hazelcast's IMap, distributed locking, and cluster management features.

## Technologies Used

- Hazelcast 5.3.0
- Java 21
- Maven
- Spring Boot

## Implementation Steps

### Step 1: Create Project Structure

```bash
mkdir hazelcast-cache
cd hazelcast-cache
mkdir -p src/main/java/com/learning/cache/{config,service,model}
mkdir -p src/main/resources
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>hazelcast-cache</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Hazelcast Cache</name>
    <description>Distributed caching with Hazelcast</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <hazelcast.version>5.3.0</hazelcast.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast</artifactId>
            <version>${hazelcast.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.2.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Cache Entry Model

```java
package com.learning.cache.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CacheEntry implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String key;
    private String value;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private long accessCount;
    private long maxIdleTime;
    
    public CacheEntry() {
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
    }
    
    public CacheEntry(String key, String value, long maxIdleTime) {
        this();
        this.key = key;
        this.value = value;
        this.maxIdleTime = maxIdleTime;
    }
    
    public void recordAccess() {
        this.accessCount++;
        this.lastAccessedAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        if (maxIdleTime <= 0) return false;
        return lastAccessedAt.plusSeconds(maxIdleTime).isBefore(LocalDateTime.now());
    }
    
    // Getters and Setters
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    
    public long getAccessCount() { return accessCount; }
    public void setAccessCount(long accessCount) { this.accessCount = accessCount; }
    
    public long getMaxIdleTime() { return maxIdleTime; }
    public void setMaxIdleTime(long maxIdleTime) { this.maxIdleTime = maxIdleTime; }
}
```

### Step 4: Create Cache Service

```java
package com.learning.cache.service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastRuntimeException;
import com.hazelcast.map.IMap;
import com.learning.cache.model.CacheEntry;
import com.hazelcast.collection.IList;
import com.hazelcast.topic.ITopic;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    
    private final HazelcastInstance hazelcastInstance;
    private final IMap<String, CacheEntry> cacheMap;
    private final IMap<String, String> sessionMap;
    private final ITopic<String> notificationTopic;
    
    private static final String CACHE_MAP_NAME = "distributed-cache";
    private static final String SESSION_MAP_NAME = "session-cache";
    private static final String NOTIFICATION_TOPIC = "cache-notifications";
    
    public CacheService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        this.cacheMap = hazelcastInstance.getMap(CACHE_MAP_NAME);
        this.sessionMap = hazelcastInstance.getMap(SESSION_MAP_NAME);
        this.notificationTopic = hazelcastInstance.getTopic(NOTIFICATION_TOPIC);
    }
    
    public void put(String key, String value) {
        put(key, value, 3600);
    }
    
    public void put(String key, String value, long ttlSeconds) {
        CacheEntry entry = new CacheEntry(key, value, ttlSeconds);
        cacheMap.set(key, entry, ttlSeconds, TimeUnit.SECONDS);
        
        notificationTopic.publish("PUT:" + key);
    }
    
    public Optional<String> get(String key) {
        CacheEntry entry = cacheMap.get(key);
        
        if (entry != null) {
            if (entry.isExpired()) {
                cacheMap.remove(key);
                notificationTopic.publish("EXPIRED:" + key);
                return Optional.empty();
            }
            
            entry.recordAccess();
            cacheMap.set(key, entry);
            return Optional.of(entry.getValue());
        }
        
        return Optional.empty();
    }
    
    public void remove(String key) {
        cacheMap.remove(key);
        notificationTopic.publish("REMOVED:" + key);
    }
    
    public boolean containsKey(String key) {
        return cacheMap.containsKey(key);
    }
    
    public Map<String, String> getAll(Collection<String> keys) {
        Map<String, CacheEntry> entries = cacheMap.getAll(keys);
        Map<String, String> result = new HashMap<>();
        
        entries.forEach((key, entry) -> {
            if (!entry.isExpired()) {
                entry.recordAccess();
                cacheMap.set(key, entry);
                result.put(key, entry.getValue());
            } else {
                cacheMap.remove(key);
            }
        });
        
        return result;
    }
    
    public void clear() {
        cacheMap.clear();
    }
    
    public int size() {
        return cacheMap.size();
    }
    
    public void putSession(String sessionId, String userId, long ttlSeconds) {
        sessionMap.set(sessionId, userId, ttlSeconds, TimeUnit.SECONDS);
    }
    
    public Optional<String> getSession(String sessionId) {
        return Optional.ofNullable(sessionMap.get(sessionId));
    }
    
    public void invalidateSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
    
    public boolean isSessionValid(String sessionId) {
        return sessionMap.containsKey(sessionId);
    }
    
    public void executePutTask(Runnable task) {
        hazelcastInstance.getExecutorService("cache-executor").submit(task);
    }
    
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cacheSize", cacheMap.size());
        stats.put("sessionSize", sessionMap.size());
        stats.put("clusterSize", hazelcastInstance.getCluster().getMembers().size());
        return stats;
    }
    
    public void addExpirationListener(Runnable listener) {
        notificationTopic.addMessageListener(message -> {
            String msg = message.getMessageObject();
            if (msg.startsWith("EXPIRED:")) {
                listener.run();
            }
        });
    }
}
```

### Step 5: Create Cache Controller

```java
package com.learning.cache.resource;

import com.learning.cache.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/cache")
public class CacheResource {
    
    private final CacheService cacheService;
    
    public CacheResource(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> put(
            @RequestParam String key,
            @RequestParam String value) {
        
        cacheService.put(key, value);
        
        Map<String, String> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        response.put("status", "cached");
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, String>> get(@PathVariable String key) {
        return cacheService.get(key)
            .map(value -> {
                Map<String, String> response = new HashMap<>();
                response.put("key", key);
                response.put("value", value);
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> remove(@PathVariable String key) {
        cacheService.remove(key);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(cacheService.getCacheStats());
    }
    
    @GetMapping("/keys")
    public ResponseEntity<Set<String>> getAllKeys() {
        return ResponseEntity.ok(cacheService.getCacheStats().keySet());
    }
    
    @PostMapping("/session")
    public ResponseEntity<Map<String, String>> createSession(
            @RequestParam String sessionId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "3600") long ttlSeconds) {
        
        cacheService.putSession(sessionId, userId, ttlSeconds);
        
        Map<String, String> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("userId", userId);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/session/invalidate")
    public ResponseEntity<Void> invalidateSession(@RequestParam String sessionId) {
        cacheService.invalidateSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
```

### Step 6: Configure Hazelcast

Create `src/main/resources/hazelcast.yaml`:

```yaml
hazelcast:
  cluster:
    name: cache-cluster
    password: dev-password
  network:
    port:
      port: 5701
      auto-increment: true
  map:
    distributed-cache:
      eviction:
        max-size-policy: PER_NODE
        size: 10000
        ttl: 3600
        when: OVERWRITE
    session-cache:
      eviction:
        max-size-policy: PER_NODE
        size: 50000
        ttl: 86400
  executor:
    pool-size: 16
```

### Step 7: Run and Test

```bash
# Build project
cd hazelcast-cache
mvn clean package

# Run first instance
java -jar target/hazelcast-cache-1.0.0.jar

# In another terminal, run second instance
java -jar target/hazelcast-cache-1.0.0.jar

# Test caching
curl -X POST "http://localhost:8080/api/cache?key=user:1&value=JohnDoe"
curl "http://localhost:8080/api/cache/user:1"
curl "http://localhost:8080/api/cache/stats"

# Test distributed session
curl -X POST "http://localhost:8080/api/cache/session?sessionId=sess-123&userId=user1"
curl -X POST "http://localhost:8080/api/cache/session/invalidate?sessionId=sess-123"
```

## Expected Output

- Distributed in-memory cache
- Session management
- Cluster-wide cache invalidation
- TTL-based expiration

---

# Real-World Project: Distributed Rate Limiter (8+ Hours)

## Project Description

Build a distributed rate limiting system using Hazelcast's data structures and distributed computing capabilities. Supports multiple rate limiting strategies including token bucket and sliding window.

## Architecture

```
┌──────────────┐     ┌──────────────┐
│ App Server 1 │     │ App Server 2 │
│  RateLimit   │◄───►│  RateLimit  │
│   Service   │     │   Service   │
└──────────────┘     └──────────────┘
         │                   │
         └─────────┬─────────┘
                   │
        ┌────────▼────────┐
        │Hazelcast Cluster│
        │  - IMap        │
        │  - ILock       │
        │  - IAtomic    │
        └───────────────┘
```

## Implementation Steps

### Step 1: Create Rate Limiter Project

```bash
mkdir hazelcast-rate-limiter
cd hazelcast-rate-limiter
mkdir -p src/main/java/com/learning/ratelimiter/{config,strategy,service}
mkdir -p src/main/resources
```

### Step 2: Create pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>hazelcast-rate-limiter</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    
    <name>Hazelcast Rate Limiter</name>
    <description>Distributed rate limiting with Hazelcast</description>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <hazelcast.version>5.3.0</hazelcast.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.hazelcast</groupId>
            <artifactId>hazelcast</artifactId>
            <version>${hazelcast.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>3.2.0</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 3: Create Rate Limiter Strategy

```java
package com.learning.ratelimiter.strategy;

public interface RateLimiterStrategy {
    
    boolean tryAcquire(String key, int permits, long timeoutMillis);
    
    long availablePermits(String key);
    
    void reset(String key);
    
    RateLimiterType getType();
    
    enum RateLimiterType {
        TOKEN_BUCKET,
        SLIDING_WINDOW,
        FIXED_WINDOW,
        LEAKY_BUCKET
    }
}
```

```java
package com.learning.ratelimiter.strategy;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.cp.lock.Lock;
import com.hazelcast.cp.lock.LockService;
import java.util.concurrent.TimeUnit;

public class TokenBucketRateLimiter implements RateLimiterStrategy {
    
    private final HazelcastInstance hazelcastInstance;
    private final int bucketSize;
    private final long refillIntervalMillis;
    private final String mapName;
    
    public TokenBucketRateLimiter(HazelcastInstance hazelcastInstance, 
                                 int bucketSize, long refillIntervalMillis) {
        this.hazelcastInstance = hazelcastInstance;
        this.bucketSize = bucketSize;
        this.refillIntervalMillis = refillIntervalMillis;
        this.mapName = "rate-limit-" + bucketSize + "-" + refillIntervalMillis;
    }
    
    @Override
    public boolean tryAcquire(String key, int permits, long timeoutMillis) {
        String lockKey = mapName + ":" + key;
        
        Lock lock = hazelcastInstance.getCPSubsystem().getLock(lockKey);
        
        try {
            if (lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS)) {
                try {
                    IAtomicLong tokens = hazelcastInstance.getAtomicLong(
                        mapName + ":tokens:" + key
                    );
                    IAtomicLong lastRefill = hazelcastInstance.getAtomicLong(
                        mapName + ":lastRefill:" + key
                    );
                    
                    long now = System.currentTimeMillis();
                    long lastRefillTime = lastRefill.get();
                    
                    if (now - lastRefillTime >= refillIntervalMillis) {
                        tokens.set(bucketSize);
                        lastRefill.set(now);
                    }
                    
                    long available = tokens.get();
                    
                    if (available >= permits) {
                        tokens.addAndGet(-permits);
                        return true;
                    }
                    
                    return false;
                } finally {
                    lock.unlock();
                }
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public long availablePermits(String key) {
        IAtomicLong tokens = hazelcastInstance.getAtomicLong(
            mapName + ":tokens:" + key
        );
        return tokens.get();
    }
    
    @Override
    public void reset(String key) {
        IAtomicLong tokens = hazelcastInstance.getAtomicLong(
            mapName + ":tokens:" + key
        );
        IAtomicLong lastRefill = hazelcastInstance.getAtomicLong(
            mapName + ":lastRefill:" + key
        );
        
        tokens.set(bucketSize);
        lastRefill.set(System.currentTimeMillis());
    }
    
    @Override
    public RateLimiterType getType() {
        return RateLimiterType.TOKEN_BUCKET;
    }
}
```

```java
package com.learning.ratelimiter.strategy;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.cp.lock.Lock;
import java.util.ArrayList;
import java.util.List;

public class SlidingWindowRateLimiter implements RateLimiterStrategy {
    
    private final HazelcastInstance hazelcastInstance;
    private final int maxRequests;
    private final long windowSizeMillis;
    private final String mapName;
    
    public SlidingWindowRateLimiter(HazelcastInstance hazelcastInstance,
                                    int maxRequests, long windowSizeMillis) {
        this.hazelcastInstance = hazelcastInstance;
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;
        this.mapName = "sliding-rate-limit-" + maxRequests + "-" + windowSizeMillis;
    }
    
    @Override
    public boolean tryAcquire(String key, int permits, long timeoutMillis) {
        String lockKey = mapName + ":" + key;
        Lock lock = hazelcastInstance.getCPSubsystem().getLock(lockKey);
        
        boolean acquired = lock.tryLock(timeoutMillis, TimeUnit.MILLISECONDS);
        
        try {
            if (acquired) {
                try {
                    IMap<String, List<Long>> requestTimes = 
                        hazelcastInstance.getMap(mapName + ":requests:" + key);
                    
                    long now = System.currentTimeMillis();
                    long windowStart = now - windowSizeMillis;
                    
                    List<Long> timestamps = requestTimes.getOrDefault(key, new ArrayList<>());
                    timestamps.removeIf(t -> t < windowStart);
                    
                    if (timestamps.size() + permits <= maxRequests) {
                        for (int i = 0; i < permits; i++) {
                            timestamps.add(now + i);
                        }
                        requestTimes.set(key, timestamps);
                        return true;
                    }
                    
                    return false;
                } finally {
                    lock.unlock();
                }
            }
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public long availablePermits(String key) {
        IMap<String, List<Long>> requestTimes = 
            hazelcastInstance.getMap(mapName + ":requests:" + key);
        
        List<Long> timestamps = requestTimes.getOrDefault(key, new ArrayList<>());
        long windowStart = System.currentTimeMillis() - windowSizeMillis;
        
        timestamps.removeIf(t -> t < windowStart);
        
        return maxRequests - timestamps.size();
    }
    
    @Override
    public void reset(String key) {
        IMap<String, List<Long>> requestTimes = 
            hazelcastInstance.getMap(mapName + ":requests:" + key);
        requestTimes.remove(key);
    }
    
    @Override
    public RateLimiterType getType() {
        return RateLimiterType.SLIDING_WINDOW;
    }
}
```

### Step 4: Create Rate Limiter Service

```java
package com.learning.ratelimiter.service;

import com.hazelcast.core.HazelcastInstance;
import com.learning.ratelimiter.strategy.*;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {
    
    private final Map<String, RateLimiterStrategy> limiters = new ConcurrentHashMap<>();
    private final HazelcastInstance hazelcastInstance;
    
    public RateLimiterService(HazelcastInstance hazelcastInstance) {
        this.hazelcastInstance = hazelcastInstance;
        
        limiters.put("default:token:100:60s", 
            new TokenBucketRateLimiter(hazelcastInstance, 100, 60000));
        limiters.put("default:sliding:100:60s",
            new SlidingWindowRateLimiter(hazelcastInstance, 100, 60000));
    }
    
    public boolean tryAcquire(String key) {
        return tryAcquire(key, 1, 1000);
    }
    
    public boolean tryAcquire(String key, int permits) {
        return tryAcquire(key, permits, 1000);
    }
    
    public boolean tryAcquire(String key, int permits, long timeoutMillis) {
        RateLimiterStrategy strategy = getOrCreateLimiter(key);
        return strategy.tryAcquire(key, permits, timeoutMillis);
    }
    
    public long availablePermits(String key) {
        RateLimiterStrategy strategy = limiters.getOrDefault(key, 
            limiters.get("default:token:100:60s"));
        return strategy.availablePermits(key);
    }
    
    public void reset(String key) {
        RateLimiterStrategy strategy = limiters.get(key);
        if (strategy != null) {
            strategy.reset(key);
        }
    }
    
    private RateLimiterStrategy getOrCreateLimiter(String key) {
        return limiters.computeIfAbsent(key, k -> {
            if (k.contains("sliding")) {
                return new SlidingWindowRateLimiter(
                    hazelcastInstance, 100, 60000);
            }
            return new TokenBucketRateLimiter(hazelcastInstance, 100, 60000);
        });
    }
    
    public Map<String, Object> getStats(String key) {
        RateLimiterStrategy strategy = getOrCreateLimiter(key);
        
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("availablePermits", availablePermits(key));
        stats.put("type", strategy.getType().name());
        
        return stats;
    }
}
```

### Step 5: Create REST Controller

```java
package com.learning.ratelimiter.resource;

import com.learning.ratelimiter.service.RateLimiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rate-limit")
public class RateLimitResource {
    
    private final RateLimiterService rateLimiterService;
    
    public RateLimitResource(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }
    
    @GetMapping("/{key}")
    public ResponseEntity<Map<String, Object>> check(
            @PathVariable String key,
            @RequestParam(defaultValue = "1") int permits) {
        
        boolean allowed = rateLimiterService.tryAcquire(key, permits, 0);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("allowed", allowed);
        response.put("permits", permits);
        response.put("available", rateLimiterService.availablePermits(key));
        
        if (allowed) {
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(429).body(response);
    }
    
    @PostMapping("/{key}")
    public ResponseEntity<Map<String, Object>> tryAcquire(
            @PathVariable String key,
            @RequestParam(defaultValue = "1") int permits,
            @RequestParam(defaultValue = "1000") long timeoutMillis) {
        
        boolean allowed = rateLimiterService.tryAcquire(key, permits, timeoutMillis);
        
        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("allowed", allowed);
        response.put("permits", permits);
        
        if (allowed) {
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(429).body(response);
    }
    
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> reset(@PathVariable String key) {
        rateLimiterService.reset(key);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{key}/stats")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String key) {
        return ResponseEntity.ok(rateLimiterService.getStats(key));
    }
}
```

### Step 6: Run and Test

```bash
# Build project
cd hazelcast-rate-limiter
mvn clean package

# Run multiple instances
java -jar target/hazelcast-rate-limiter-1.0.0.jar
java -jar target/hazelcast-rate-limiter-1.0.0.jar

# Test rate limiting
# First few requests should succeed
for i in {1..10}; do
  curl -X POST "http://localhost:8080/api/rate-limit/user:1?permits=1"
done

# Get statistics
curl "http://localhost:8080/api/rate-limit/user:1/stats"

# Reset limiter
curl -X DELETE "http://localhost:8080/api/rate-limit/user:1"
```

## Expected Output

- Token bucket rate limiter
- Sliding window rate limiter
- Distributed across cluster
- API key-based limits

## Build Instructions Summary

| Step | Command | Time |
|------|---------|------|
| Setup project | Maven | 15 min |
| Create strategies | Implementation | 2 hours |
| Create service | Business logic | 1.5 hours |
| Create API | REST endpoints | 1 hour |
| Testing | Integration | 3 hours |

Total estimated time: 8-10 hours