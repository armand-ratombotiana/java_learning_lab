# Redis Module - PROJECTS.md

---

# Mini-Project: Caching and Pub/Sub

## Project Overview

**Duration**: 3-4 hours  
**Concepts Used**: Redis Cache, Pub/Sub, Distributed Locks, Session Management

This mini-project demonstrates Redis caching, Pub/Sub messaging, and distributed locks.

---

## Project Structure

```
24-redis/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   └── RedisConfig.java
│   ├── service/
│   │   ├── CacheService.java
│   │   └── PubSubService.java
│   └── controller/
│       └── ProductController.java
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
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
    </dependencies>
</project>
```

---

## Implementation

```java
// config/RedisConfig.java
package com.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
    
    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}
```

```java
// service/CacheService.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
public class CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void set(String key, Object value, long ttlSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }
    
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public void delete(String key) {
        redisTemplate.delete(key);
    }
    
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}
```

```java
// Main.java
package com.learning;

import com.learning.service.CacheService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    
    private final CacheService cacheService;
    
    public Main(CacheService cacheService) {
        this.cacheService = cacheService;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            cacheService.set("product:1", "Laptop", 60);
            System.out.println("Cached: " + cacheService.get("product:1"));
        };
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
docker run -p 6379:6379 redis
cd 24-redis
mvn spring-boot:run
```

---

# Real-World Project: Advanced Redis Patterns

```java
// Distributed Lock Implementation
@Service
public class DistributedLockService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public boolean acquireLock(String lockKey, String requestId, long expirationSeconds) {
        Boolean result = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, requestId, Duration.ofSeconds(expirationSeconds));
        return Boolean.TRUE.equals(result);
    }
    
    public void releaseLock(String lockKey, String requestId) {
        String value = (String) redisTemplate.opsForValue().get(lockKey);
        if (requestId.equals(value)) {
            redisTemplate.delete(lockKey);
        }
    }
}
```

---

## Build Instructions

```bash
cd 24-redis
mvn clean compile
mvn spring-boot:run
```