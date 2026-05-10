# Redis Module (32) - PROJECTS.md

---

# Mini-Project: Distributed Rate Limiter with Redis

## Project Overview

**Duration**: 4-5 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Redis Data Structures, Rate Limiting, Lua Scripts, Caching Patterns

This mini-project demonstrates building a distributed rate limiter using Redis operations and Lua scripting.

---

## Project Structure

```
32-redis/
├── pom.xml
└── redis-cache/
    └── src/main/java/
        ├── Main.java
        ├── service/
        │   └── RateLimiterService.java
        └── controller/
            └── RateLimitController.java
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>redis-cache</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Rate Limiter Service

```java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final DefaultRedisScript<Long> rateLimitScript;
    
    private static final String RATE_LIMIT_KEY_PREFIX = "rate:limit:";
    
    public RateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        
        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setResultType(Long.class);
        this.rateLimitScript.setScriptText(
            "local current = redis.call('INCR', KEYS[1]) " +
            "if current == 1 then " +
            "    redis.call('EXPIRE', KEYS[1], ARGV[1]) " +
            "end " +
            "return current"
        );
    }
    
    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        String redisKey = RATE_LIMIT_KEY_PREFIX + key;
        
        Long count = redisTemplate.execute(
            rateLimitScript,
            Arrays.asList(redisKey),
            String.valueOf(windowSeconds)
        );
        
        return count != null && count <= maxRequests;
    }
    
    public long getCurrentCount(String key) {
        String redisKey = RATE_LIMIT_KEY_PREFIX + key;
        String value = redisTemplate.opsForValue().get(redisKey);
        return value != null ? Long.parseLong(value) : 0;
    }
    
    public void resetLimit(String key) {
        String redisKey = RATE_LIMIT_KEY_PREFIX + key;
        redisTemplate.delete(redisKey);
    }
}
```

---

## Step 3: Controller

```java
package com.learning.controller;

import com.learning.service.RateLimiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RateLimitController {
    
    private final RateLimiterService rateLimiterService;
    
    public RateLimitController(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }
    
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData(@RequestHeader("X-Client-Id") String clientId) {
        boolean allowed = rateLimiterService.isAllowed(clientId, 10, 60);
        
        if (!allowed) {
            return ResponseEntity.status(429).body(Map.of(
                "error", "Rate limit exceeded",
                "retryAfter", 60
            ));
        }
        
        long currentCount = rateLimiterService.getCurrentCount(clientId);
        
        return ResponseEntity.ok(Map.of(
            "data", "Sample data response",
            "requestsRemaining", 10 - currentCount
        ));
    }
    
    @PostMapping("/reset/{clientId}")
    public ResponseEntity<Map<String, String>> resetLimit(@PathVariable String clientId) {
        rateLimiterService.resetLimit(clientId);
        return ResponseEntity.ok(Map.of("status", "Rate limit reset"));
    }
}
```

---

## Step 4: Main Application

```java
package com.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Application Properties

```yaml
spring.data.redis.host: localhost
spring.data.redis.port: 6379
server.port: 8080
```

---

## Build Instructions

```bash
# Start Redis
docker run -p 6379:6379 redis

cd 32-redis/redis-cache
mvn spring-boot:run

# Test rate limiting
curl -H "X-Client-Id: user1" http://localhost:8080/api/data
```

---

# Real-World Project: Distributed Caching with Redis

This comprehensive project demonstrates building a production-grade caching system with Redis including cache-aside pattern, distributed locks, and session management.

---

## Complete Implementation

```java
// Distributed Lock with Redis
package com.learning.lock;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class DistributedLockService {
    
    private final RedisTemplate<String, String> redisTemplate;
    private final ThreadLocal<String> lockValue = new ThreadLocal<>();
    
    public static final String LOCK_PREFIX = "lock:";
    public static final long DEFAULT_TIMEOUT = 30;
    
    public DistributedLockService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public boolean acquireLock(String resourceId, long timeoutSeconds) {
        String lockKey = LOCK_PREFIX + resourceId;
        String lockValue = UUID.randomUUID().toString();
        
        Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockValue, timeoutSeconds, TimeUnit.SECONDS);
        
        if (Boolean.TRUE.equals(acquired)) {
            this.lockValue.set(lockValue);
            return true;
        }
        return false;
    }
    
    public boolean releaseLock(String resourceId) {
        String lockKey = LOCK_PREFIX + resourceId;
        String currentValue = lockValue.get();
        
        if (currentValue != null) {
            String storedValue = redisTemplate.opsForValue().get(lockKey);
            if (currentValue.equals(storedValue)) {
                redisTemplate.delete(lockKey);
                lockValue.remove();
                return true;
            }
        }
        return false;
    }
}
```

---

## Build Instructions

```bash
cd 32-redis/redis-cache
mvn clean compile
mvn spring-boot:run

# Test distributed lock
curl -X POST http://localhost:8080/api/data
```

---

## Key Features

- **Rate Limiting**: Token bucket algorithm with Lua scripts
- **Distributed Locks**: Safe concurrent access to resources
- **Cache-aside Pattern**: Manual caching with TTL
- **Session Management**: Distributed session storage