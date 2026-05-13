# Redis Module - PROJECTS.md

---

# Mini-Project 1: String Operations (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: SET/GET, INCR/DECR, SETNX, EXPIRE, TTL, MGET/MSET, String operations

This mini-project focuses on implementing Redis string operations for caching and counters.

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
│   │   ├── StringService.java
│   │   └── CounterService.java
│   └── model/
│       └── Product.java
└── src/main/resources/
    └── application.yml
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
    <artifactId>redis-string-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.30</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Redis Configuration

```java
// config/RedisConfig.java
package com.learning.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
```

---

## Step 3: String Service

```java
// service/StringService.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class StringService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public StringService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public void setStringWithExpiration(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, Duration.of(timeout, unit.toChronoUnit()));
    }
    
    public String getString(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }
    
    public boolean setIfAbsent(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value));
    }
    
    public boolean setIfPresent(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfPresent(key, value));
    }
    
    public void setWithNX(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().setIfAbsent(key, value, Duration.of(timeout, unit.toChronoUnit()));
    }
    
    public String getAndSet(String key, String value) {
        return (String) redisTemplate.opsForValue().getAndSet(key, value);
    }
    
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }
    
    public Long incrementBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    
    public Long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }
    
    public Long decrementBy(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
    
    public Double incrementByFloat(String key, double delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }
    
    public Long append(String key, String value) {
        return redisTemplate.opsForValue().append(key, value);
    }
    
    public String getRange(String key, long start, long end) {
        return redisTemplate.opsForValue().get(key, start, end);
    }
    
    public void setRange(String key, long offset, String value) {
        redisTemplate.opsForValue().set(key, offset, value);
    }
    
    public Long getBit(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }
    
    public boolean setBit(String key, long offset, boolean value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setBit(key, offset, value));
    }
    
    public Long bitCount(String key) {
        return redisTemplate.opsForValue().bitCount(key);
    }
    
    public void multiSet(Map<String, String> map) {
        redisTemplate.opsForValue().multiSet(map);
    }
    
    public Map<String, String> multiGet(Set<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }
    
    public boolean setBitCount(String key, long offset, int value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setBit(key, offset, value));
    }
    
    public Long getSize(String key) {
        return redisTemplate.opsForValue().size(key);
    }
}
```

---

## Step 4: Counter Service

```java
// service/CounterService.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class CounterService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public CounterService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public Long incrementCounter(String counterName) {
        return redisTemplate.opsForValue().increment("counter:" + counterName);
    }
    
    public Long incrementCounter(String counterName, long delta) {
        return redisTemplate.opsForValue().increment("counter:" + counterName, delta);
    }
    
    public Long decrementCounter(String counterName) {
        return redisTemplate.opsForValue().decrement("counter:" + counterName);
    }
    
    public Long getCounter(String counterName) {
        Long value = (Long) redisTemplate.opsForValue().get("counter:" + counterName);
        return value != null ? value : 0L;
    }
    
    public void resetCounter(String counterName) {
        redisTemplate.delete("counter:" + counterName);
    }
    
    public void initializeCounter(String counterName, long initialValue, long ttlSeconds) {
        String key = "counter:" + counterName;
        redisTemplate.opsForValue().set(key, initialValue, Duration.ofSeconds(ttlSeconds));
    }
    
    public Long incrementDailyCounter(String metricName) {
        String key = "counter:daily:" + metricName + ":" + getCurrentDate();
        Long newValue = redisTemplate.opsForValue().increment(key);
        
        if (newValue != null && newValue == 1) {
            redisTemplate.expire(key, Duration.ofDays(2));
        }
        
        return newValue;
    }
    
    public Long incrementHourlyCounter(String metricName) {
        String key = "counter:hourly:" + metricName + ":" + getCurrentHour();
        Long newValue = redisTemplate.opsForValue().increment(key);
        
        if (newValue != null && newValue == 1) {
            redisTemplate.expire(key, Duration.ofHours(25));
        }
        
        return newValue;
    }
    
    public Long incrementUniqueUser(String action) {
        String date = getCurrentDate();
        String key = "unique_users:" + action + ":" + date;
        return redisTemplate.opsForValue().increment(key);
    }
    
    public void incrementPageView(String pagePath) {
        String key = "page_views:" + pagePath + ":" + getCurrentDate();
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofDays(7));
    }
    
    public Long getPageViewCount(String pagePath) {
        String key = "page_views:" + pagePath + ":" + getCurrentDate();
        Long count = (Long) redisTemplate.opsForValue().get(key);
        return count != null ? count : 0L;
    }
    
    private String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }
    
    private String getCurrentHour() {
        return java.time.LocalDateTime.now().toString().substring(0, 13);
    }
}
```

---

## Step 5: Main Application

```java
// Main.java
package com.learning;

import com.learning.service.StringService;
import com.learning.service.CounterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    
    private final StringService stringService;
    private final CounterService counterService;
    
    public Main(StringService stringService, CounterService counterService) {
        this.stringService = stringService;
        this.counterService = counterService;
    }
    
    @Bean
    public CommandLineRunner run() {
        return args -> {
            System.out.println("=== Redis String Operations Demo ===\n");
            
            System.out.println("--- Basic String Operations ---");
            stringService.setString("user:1:name", "John Doe");
            stringService.setString("user:1:email", "john@example.com");
            System.out.println("Name: " + stringService.getString("user:1:name"));
            System.out.println("Email: " + stringService.getString("user:1:email"));
            
            System.out.println("\n--- String with Expiration ---");
            stringService.setStringWithExpiration("session:abc123", "active", 60, java.util.concurrent.TimeUnit.SECONDS);
            System.out.println("Session set with 60 second TTL");
            
            System.out.println("\n--- Set If Absent (Lock) ---");
            boolean lockAcquired = stringService.setIfAbsent("lock:resource", "locked");
            System.out.println("Lock acquired: " + lockAcquired);
            if (lockAcquired) {
                stringService.deleteKey("lock:resource");
            }
            
            System.out.println("\n--- Increment/Decrement ---");
            stringService.setString("counter:visits", "0");
            System.out.println("Initial: " + stringService.getString("counter:visits"));
            stringService.incrementCounter("visits");
            System.out.println("After increment: " + stringService.getString("counter:visits"));
            stringService.incrementBy("visits", 5);
            System.out.println("After +5: " + stringService.getString("counter:visits"));
            stringService.decrementCounter("visits");
            System.out.println("After decrement: " + stringService.getString("counter:visits"));
            
            System.out.println("\n--- Multi-Get/Multi-Set ---");
            java.util.Map<String, String> userData = new java.util.HashMap<>();
            userData.put("user:2:name", "Jane Smith");
            userData.put("user:2:email", "jane@example.com");
            userData.put("user:2:role", "admin");
            stringService.multiSet(userData);
            
            java.util.Set<String> keys = java.util.Set.of("user:2:name", "user:2:email", "user:2:role");
            java.util.Map<String, String> retrieved = stringService.multiGet(keys);
            retrieved.forEach((k, v) -> System.out.println(k + ": " + v));
            
            System.out.println("\n--- Counter Examples ---");
            Long pageViews = counterService.incrementDailyCounter("page_views");
            System.out.println("Page views today: " + pageViews);
            
            Long uniqueUsers = counterService.incrementUniqueUser("login");
            System.out.println("Unique users today: " + uniqueUsers);
            
            counterService.incrementPageView("/home");
            System.out.println("Home page views: " + counterService.getPageViewCount("/home"));
            
            System.out.println("\n=== String Operations Demo Complete ===");
        };
    }
    
    private void deleteKey(String key) {
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Step 6: Application Properties

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: redis-string-demo
  data:
    redis:
      host: localhost
      port: 6379
```

---

## Build Instructions

```bash
# Start Redis
docker run -p 6379:6379 redis

# Access Redis CLI
docker exec -it <container_id> redis-cli

cd 24-redis
mvn clean compile
mvn spring-boot:run
```

---

# Mini-Project 2: List/Set Operations (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Beginner  
**Concepts Used**: LPUSH/RPUSH, LPOP/RPOP, LRANGE, SADD/SMEMBERS, SINTER/SUNION, Sorted Sets

This mini-project focuses on implementing Redis list and set operations for data structures.

---

## Project Structure

```
24-redis/
├── src/main/java/com/learning/
│   ├── service/
│   │   ├── ListService.java
│   │   └── SetService.java
│   └── model/
│       └── RecentActivity.java
```

---

## Step 1: List Service

```java
// service/ListService.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ListService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public ListService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void pushLeft(String key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }
    
    public void pushRight(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }
    
    public Object popLeft(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    public Object popRight(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    public Object peekLeft(String key) {
        return redisTemplate.opsForList().leftPeek(key);
    }
    
    public Object peekRight(String key) {
        return redisTemplate.opsForList().rightPeek(key);
    }
    
    public Long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }
    
    public List<Object> getRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    public void setByIndex(String key, long index, Object value) {
        redisTemplate.opsForList().set(key, index, value);
    }
    
    public void removeByCount(String key, long count, Object value) {
        redisTemplate.opsForList().remove(key, count, value);
    }
    
    public void trim(String key, long start, long end) {
        redisTemplate.opsForList().trim(key, start, end);
    }
    
    public void pushIfExists(String key, Object value) {
        redisTemplate.opsForList().rightPushIfPresent(key, value);
    }
    
    public void pushAll(String key, List<Object> values) {
        redisTemplate.opsForList().rightPushAll(key, values);
    }
    
    public List<Object> getAll(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }
    
    public void addToRecentItems(String userId, String itemId, int maxSize) {
        String key = "recent:" + userId;
        redisTemplate.opsForList().remove(key, 0, itemId);
        redisTemplate.opsForList().leftPush(key, itemId);
        redisTemplate.opsForList().trim(key, 0, maxSize - 1);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
    
    public List<Object> getRecentItems(String userId, int count) {
        return redisTemplate.opsForList().range("recent:" + userId, 0, count - 1);
    }
    
    public void addToActivityLog(String activity, int maxSize) {
        String key = "activity:log";
        redisTemplate.opsForList().leftPush(key, activity);
        redisTemplate.opsForList().trim(key, 0, maxSize - 1);
    }
    
    public List<Object> getActivityLog(int count) {
        return redisTemplate.opsForList().range("activity:log", 0, count - 1);
    }
    
    public void addToTaskQueue(String queueName, String task) {
        redisTemplate.opsForList().rightPush("queue:" + queueName, task);
    }
    
    public String pollFromTaskQueue(String queueName) {
        String task = (String) redisTemplate.opsForList().leftPop("queue:" + queueName);
        return task;
    }
    
    public Long getQueueSize(String queueName) {
        return redisTemplate.opsForList().size("queue:" + queueName);
    }
}
```

---

## Step 2: Set Service

```java
// service/SetService.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SetService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public SetService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public Long addToSet(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }
    
    public Long removeFromSet(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }
    
    public Set<Object> getSetMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    public Boolean isMemberOfSet(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    
    public Long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }
    
    public Object popRandomMember(String key) {
        return redisTemplate.opsForSet().pop(key);
    }
    
    public Object getRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }
    
    public Set<Object> getRandomMembers(String key, long count) {
        return redisTemplate.opsForSet().distinctRandomMembers(key, count);
    }
    
    public Set<Object> intersectSets(String... keys) {
        return redisTemplate.opsForSet().intersect(keys);
    }
    
    public void intersectAndStore(String destination, String... keys) {
        redisTemplate.opsForSet().intersectAndStore(keys, destination);
    }
    
    public Set<Object> unionSets(String... keys) {
        return redisTemplate.opsForSet().union(keys);
    }
    
    public void unionAndStore(String destination, String... keys) {
        redisTemplate.opsForSet().unionAndStore(keys, destination);
    }
    
    public Set<Object> differenceSets(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }
    
    public void differenceAndStore(String destination, String key1, String key2) {
        redisTemplate.opsForSet().differenceAndStore(key1, key2, destination);
    }
    
    public void addTag(String itemId, String tag) {
        redisTemplate.opsForSet().add("tags:" + tag, itemId);
        redisTemplate.opsForSet().add("item:" + itemId + ":tags", tag);
    }
    
    public void removeTag(String itemId, String tag) {
        redisTemplate.opsForSet().remove("tags:" + tag, itemId);
        redisTemplate.opsForSet().remove("item:" + itemId + ":tags", tag);
    }
    
    public Set<Object> getItemsByTag(String tag) {
        return redisTemplate.opsForSet().members("tags:" + tag);
    }
    
    public Set<Object> getItemTags(String itemId) {
        return redisTemplate.opsForSet().members("item:" + itemId + ":tags");
    }
    
    public Set<Object> getItemsWithAllTags(String... tags) {
        if (tags.length == 0) return Set.of();
        if (tags.length == 1) {
            return getItemsByTag(tags[0]);
        }
        
        String firstKey = "tags:" + tags[0];
        String[] remainingKeys = new String[tags.length - 1];
        for (int i = 1; i < tags.length; i++) {
            remainingKeys[i - 1] = "tags:" + tags[i];
        }
        
        return redisTemplate.opsForSet().intersect(firstKey, remainingKeys);
    }
    
    public void trackUserInterest(String userId, String interest) {
        redisTemplate.opsForSet().add("interests:" + interest, userId);
        redisTemplate.opsForSet().add("user:" + userId + ":interests", interest);
    }
    
    public Set<Object> findUsersWithInterests(String... interests) {
        if (interests.length == 0) return Set.of();
        if (interests.length == 1) {
            return redisTemplate.opsForSet().members("interests:" + interests[0]);
        }
        
        String firstKey = "interests:" + interests[0];
        String[] remainingKeys = new String[interests.length - 1];
        for (int i = 1; i < interests.length; i++) {
            remainingKeys[i - 1] = "interests:" + interests[i];
        }
        
        return redisTemplate.opsForSet().intersect(firstKey, remainingKeys);
    }
}
```

---

## Step 3: Sorted Set Service

```java
// service/SortedSetService.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SortedSetService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public SortedSetService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public Boolean addToSortedSet(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }
    
    public Long addMultipleToSortedSet(String key, Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> tuples) {
        return redisTemplate.opsForZSet().add(key, tuples);
    }
    
    public Long removeFromSortedSet(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }
    
    public Double getScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }
    
    public Long getRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }
    
    public Long getReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }
    
    public Set<Object> getRangeByRank(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }
    
    public Set<Object> getReverseRangeByRank(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }
    
    public Set<Object> getRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }
    
    public Set<Object> getReverseRangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().reverseRangeByScore(key, min, max);
    }
    
    public Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> getRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    }
    
    public Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> getReverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }
    
    public Long getSortedSetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
    
    public Long countByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().count(key, min, max);
    }
    
    public void incrementScore(String key, Object value, double delta) {
        redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }
    
    public void removeByRank(String key, long start, long end) {
        redisTemplate.opsForZSet().removeRange(key, start, end);
    }
    
    public void removeByScore(String key, double min, double max) {
        redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }
    
    public void addProductRating(String productId, String userId, double rating) {
        redisTemplate.opsForZSet().add("product:" + productId + ":ratings", userId, rating);
    }
    
    public Double getProductRating(String productId, String userId) {
        return redisTemplate.opsForZSet().score("product:" + productId + ":ratings", userId);
    }
    
    public Double getAverageRating(String productId) {
        Set<org.springframework.data.redis.core.ZSetOperations.TypedTuple<Object>> ratings = 
            redisTemplate.opsForZSet().rangeWithScores("product:" + productId + ":ratings", 0, -1);
        
        if (ratings == null || ratings.isEmpty()) return 0.0;
        
        double sum = 0;
        int count = 0;
        for (var tuple : ratings) {
            if (tuple.getScore() != null) {
                sum += tuple.getScore();
                count++;
            }
        }
        
        return count > 0 ? sum / count : 0.0;
    }
    
    public void addLeaderboardEntry(String leaderboard, String playerId, double score) {
        redisTemplate.opsForZSet().add("leaderboard:" + leaderboard, playerId, score);
    }
    
    public Long getPlayerRank(String leaderboard, String playerId) {
        return redisTemplate.opsForZSet().reverseRank("leaderboard:" + leaderboard, playerId);
    }
    
    public Set<Object> getTopPlayers(String leaderboard, int count) {
        return redisTemplate.opsForZSet().reverseRange("leaderboard:" + leaderboard, 0, count - 1);
    }
    
    public void addPriceAlert(String productId, double targetPrice) {
        String key = "price_alerts:" + productId;
        long timestamp = System.currentTimeMillis();
        redisTemplate.opsForZSet().add(key, targetPrice + ":" + timestamp, targetPrice);
    }
    
    public Set<Object> getPriceAlertsForProduct(String productId, double currentPrice) {
        return redisTemplate.opsForZSet().rangeByScore("price_alerts:" + productId, 0, currentPrice);
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

---

# Mini-Project 3: Caching Patterns (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Cache-Aside, Write-Through, Write-Behind, Cache Eviction, Cache Stampede Prevention

This mini-project focuses on implementing caching patterns with Redis.

---

## Project Structure

```
24-redis/
├── src/main/java/com/learning/
│   ├── service/
│   │   ├── CacheService.java
│   │   ├── ProductCacheService.java
│   │   └── DistributedCacheLock.java
│   └── model/
│       └── Product.java
```

---

## Step 1: Cache Service

```java
// service/CacheService.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class CacheService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void put(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public void putWithExpiration(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, Duration.of(timeout, unit.toChronoUnit()));
    }
    
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }
    
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    public Long deleteMultiple(String... keys) {
        return redisTemplate.delete(java.util.Arrays.asList(keys));
    }
    
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    
    public Long getExpiration(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
    
    public Boolean setExpiration(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
    
    public Boolean persist(String key) {
        return redisTemplate.persist(key);
    }
    
    public String getLock(String lockKey, String requestId, long expirationSeconds) {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
            lockKey, requestId, Duration.ofSeconds(expirationSeconds));
        
        if (Boolean.TRUE.equals(acquired)) {
            return requestId;
        }
        return null;
    }
    
    public Boolean releaseLock(String lockKey, String requestId) {
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
        if (requestId.equals(currentValue)) {
            return redisTemplate.delete(lockKey);
        }
        return false;
    }
    
    public Object getAndDelete(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }
    
    public Object getAndExpire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.opsForValue().getAndExpire(key, timeout, unit);
    }
}
```

---

## Step 2: Product Cache Service (Cache-Aside Pattern)

```java
// service/ProductCacheService.java
package com.learning.service;

import com.learning.model.Product;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ProductCacheService {
    
    private static final String PRODUCT_KEY_PREFIX = "product:";
    private static final String PRODUCT_LIST_KEY = "products:all";
    private static final long CACHE_TTL_SECONDS = 3600;
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ProductRepository productRepository;
    
    public ProductCacheService(RedisTemplate<String, Object> redisTemplate, 
                               ProductRepository productRepository) {
        this.redisTemplate = redisTemplate;
        this.productRepository = productRepository;
    }
    
    public Optional<Product> getProduct(String productId) {
        String key = PRODUCT_KEY_PREFIX + productId;
        
        Product cached = (Product) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            System.out.println("Cache HIT for product: " + productId);
            return Optional.of(cached);
        }
        
        System.out.println("Cache MISS for product: " + productId);
        Optional<Product> product = productRepository.findById(productId);
        
        product.ifPresent(p -> {
            redisTemplate.opsForValue().set(key, p, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        });
        
        return product;
    }
    
    public void saveProduct(Product product) {
        String key = PRODUCT_KEY_PREFIX + product.getId();
        redisTemplate.opsForValue().set(key, product, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        invalidateProductList();
    }
    
    public void deleteProduct(String productId) {
        String key = PRODUCT_KEY_PREFIX + productId;
        redisTemplate.delete(key);
        invalidateProductList();
    }
    
    public List<Product> getAllProducts() {
        @SuppressWarnings("unchecked")
        List<Product> cached = (List<Product>) redisTemplate.opsForValue().get(PRODUCT_LIST_KEY);
        if (cached != null) {
            System.out.println("Cache HIT for product list");
            return cached;
        }
        
        System.out.println("Cache MISS for product list");
        List<Product> products = productRepository.findAll();
        redisTemplate.opsForValue().set(PRODUCT_LIST_KEY, products, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        return products;
    }
    
    public void invalidateProductList() {
        redisTemplate.delete(PRODUCT_LIST_KEY);
    }
    
    public void invalidateAllProductCaches() {
        java.util.Set<String> keys = redisTemplate.keys(PRODUCT_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        invalidateProductList();
    }
    
    public <T> T getOrLoad(String key, java.util.function.Supplier<T> loader, long ttl, TimeUnit unit) {
        @SuppressWarnings("unchecked")
        T cached = (T) redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }
        
        T loaded = loader.get();
        if (loaded != null) {
            redisTemplate.opsForValue().set(key, loaded, ttl, unit);
        }
        
        return loaded;
    }
    
    public void warmUpCache() {
        System.out.println("Warming up product cache...");
        List<Product> products = productRepository.findAll();
        
        for (Product product : products) {
            String key = PRODUCT_KEY_PREFIX + product.getId();
            redisTemplate.opsForValue().set(key, product, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        }
        
        redisTemplate.opsForValue().set(PRODUCT_LIST_KEY, products, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        System.out.println("Cache warmed up with " + products.size() + " products");
    }
}
```

---

## Step 3: Distributed Cache Lock

```java
// service/DistributedCacheLock.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class DistributedCacheLock {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public DistributedCacheLock(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public String acquireLock(String resource, long expirationSeconds) {
        String lockKey = "lock:" + resource;
        String lockValue = UUID.randomUUID().toString();
        
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(
            lockKey, lockValue, Duration.ofSeconds(expirationSeconds));
        
        if (Boolean.TRUE.equals(acquired)) {
            return lockValue;
        }
        return null;
    }
    
    public boolean releaseLock(String resource, String lockValue) {
        String lockKey = "lock:" + resource;
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
        
        if (lockValue.equals(currentValue)) {
            return Boolean.TRUE.equals(redisTemplate.delete(lockKey));
        }
        return false;
    }
    
    public boolean extendLock(String resource, String lockValue, long additionalSeconds) {
        String lockKey = "lock:" + resource;
        String currentValue = (String) redisTemplate.opsForValue().get(lockKey);
        
        if (lockValue.equals(currentValue)) {
            return Boolean.TRUE.equals(
                redisTemplate.expire(lockKey, additionalSeconds, TimeUnit.SECONDS));
        }
        return false;
    }
    
    public <T> T executeWithLock(String resource, long lockExpirationSeconds, 
                                  Supplier<T> action) {
        String lockValue = acquireLock(resource, lockExpirationSeconds);
        if (lockValue == null) {
            throw new RuntimeException("Could not acquire lock for resource: " + resource);
        }
        
        try {
            return action.get();
        } finally {
            releaseLock(resource, lockValue);
        }
    }
    
    public <T> T getOrLoadWithLock(String resource, String cacheKey, 
                                   Supplier<T> loader, long lockExpirationSeconds,
                                   long cacheExpirationSeconds, TimeUnit cacheExpirationUnit) {
        @SuppressWarnings("unchecked")
        T cached = (T) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        String lockValue = acquireLock(resource, lockExpirationSeconds);
        if (lockValue == null) {
            Thread.sleep(100);
            cached = (T) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cached = (T) redisTemplate.opsForValue().get(cacheKey);
            return cached != null ? cached : loader.get();
        }
        
        try {
            cached = (T) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
            
            T loaded = loader.get();
            if (loaded != null) {
                redisTemplate.opsForValue().set(cacheKey, loaded, cacheExpirationSeconds, cacheExpirationUnit);
            }
            return loaded;
        } finally {
            releaseLock(resource, lockValue);
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

---

# Mini-Project 4: Pub/Sub (2 hours)

## Project Overview

**Duration**: 2 hours  
**Difficulty**: Intermediate  
**Concepts Used**: Publish/Subscribe, Channel subscription, Message handling, Pattern matching

This mini-project focuses on implementing Redis Pub/Sub for messaging patterns.

---

## Project Structure

```
24-redis/
├── src/main/java/com/learning/
│   ├── config/
│   │   └── RedisPubSubConfig.java
│   ├── publisher/
│   │   └── MessagePublisher.java
│   └── subscriber/
│       ├── RedisMessageSubscriber.java
│       └── NotificationSubscriber.java
```

---

## Step 1: Redis Pub/Sub Configuration

```java
// config/RedisPubSubConfig.java
package com.learning.config;

import com.learning.subscriber.NotificationSubscriber;
import com.learning.subscriber.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisPubSubConfig {
    
    public static final String NOTIFICATIONS_CHANNEL = "notifications";
    public static final String USER_ACTIVITIES_CHANNEL = "user:activities";
    public static final String STOCK_UPDATES_CHANNEL = "stock:updates";
    public static final String NOTIFICATIONS_PATTERN = "notifications:*";
    
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListenerAdapter notificationListenerAdapter,
            MessageListenerAdapter patternListenerAdapter) {
        
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        container.addMessageListener(notificationListenerAdapter, 
            new ChannelTopic(NOTIFICATIONS_CHANNEL));
        
        container.addMessageListener(patternListenerAdapter, 
            new PatternTopic(NOTIFICATIONS_PATTERN));
        
        container.addMessageListener(notificationListenerAdapter, 
            new ChannelTopic(USER_ACTIVITIES_CHANNEL));
        
        return container;
    }
    
    @Bean
    public MessageListenerAdapter notificationListenerAdapter(
            NotificationSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "handleMessage");
    }
    
    @Bean
    public MessageListenerAdapter patternListenerAdapter(
            RedisMessageSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "handlePatternMessage");
    }
    
    @Bean
    public RedisTemplate<String, Object> redisPubSubTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setChannelTopicSerializer(new StringRedisSerializer());
        return template;
    }
}
```

---

## Step 2: Message Publisher

```java
// publisher/MessagePublisher.java
package com.learning.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessagePublisher {
    
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    
    public MessagePublisher(RedisTemplate<String, Object> redisTemplate, 
                            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void publishToChannel(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
        System.out.println("Published to " + channel + ": " + message);
    }
    
    public void publishNotification(String userId, String type, String content) {
        String channel = "notifications:" + userId;
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("type", type);
        notification.put("content", content);
        notification.put("timestamp", Instant.now().toString());
        
        try {
            String message = objectMapper.writeValueAsString(notification);
            redisTemplate.convertAndSend(channel, message);
            System.out.println("Notification published to " + channel);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize notification", e);
        }
    }
    
    public void publishUserActivity(String userId, String activity) {
        String channel = "user:activities:" + userId;
        
        Map<String, Object> activityData = new HashMap<>();
        activityData.put("userId", userId);
        activityData.put("activity", activity);
        activityData.put("timestamp", Instant.now().toString());
        
        try {
            String message = objectMapper.writeValueAsString(activityData);
            redisTemplate.convertAndSend("user:activities", message);
            System.out.println("User activity published: " + activity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize activity", e);
        }
    }
    
    public void publishStockUpdate(String productId, int newQuantity) {
        Map<String, Object> update = new HashMap<>();
        update.put("productId", productId);
        update.put("newQuantity", newQuantity);
        update.put("timestamp", Instant.now().toString());
        
        try {
            String message = objectMapper.writeValueAsString(update);
            redisTemplate.convertAndSend("stock:updates", message);
            System.out.println("Stock update published for product: " + productId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize stock update", e);
        }
    }
    
    public void publishPriceAlert(String productId, double oldPrice, double newPrice) {
        Map<String, Object> alert = new HashMap<>();
        alert.put("productId", productId);
        alert.put("oldPrice", oldPrice);
        alert.put("newPrice", newPrice);
        alert.put("timestamp", Instant.now().toString());
        
        try {
            String message = objectMapper.writeValueAsString(alert);
            redisTemplate.convertAndSend("price:alerts", message);
            System.out.println("Price alert published for product: " + productId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize price alert", e);
        }
    }
    
    public void publishOrderEvent(String orderId, String status, Map<String, Object> details) {
        Map<String, Object> event = new HashMap<>();
        event.put("orderId", orderId);
        event.put("status", status);
        event.put("details", details);
        event.put("timestamp", Instant.now().toString());
        
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend("order:events", message);
            System.out.println("Order event published: " + orderId + " - " + status);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize order event", e);
        }
    }
}
```

---

## Step 3: Redis Message Subscriber

```java
// subscriber/RedisMessageSubscriber.java
package com.learning.subscriber;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        String patternStr = pattern != null ? new String(pattern) : "none";
        
        System.out.println("=== Message Received ===");
        System.out.println("Channel: " + channel);
        System.out.println("Pattern: " + patternStr);
        System.out.println("Body: " + body);
        System.out.println("========================");
    }
    
    public void handlePatternMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        
        System.out.println("=== Pattern Matched Message ===");
        System.out.println("Channel: " + channel);
        System.out.println("Body: " + body);
        System.out.println("=============================");
    }
}
```

---

## Step 4: Notification Subscriber

```java
// subscriber/NotificationSubscriber.java
package com.learning.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationSubscriber {
    
    private final ObjectMapper objectMapper;
    private final Map<String, java.util.concurrent.ConcurrentLinkedQueue<String>> userInboxes;
    
    public NotificationSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.userInboxes = new java.util.concurrent.ConcurrentHashMap<>();
    }
    
    public void handleMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        
        try {
            if (channel.startsWith("notifications:")) {
                handleNotificationMessage(body);
            } else {
                handleGenericMessage(channel, body);
            }
        } catch (Exception e) {
            System.err.println("Failed to process message: " + e.getMessage());
        }
    }
    
    private void handleNotificationMessage(String body) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> notification = objectMapper.readValue(body, Map.class);
            
            String userId = (String) notification.get("userId");
            String type = (String) notification.get("type");
            String content = (String) notification.get("content");
            
            System.out.println("=== Notification Received ===");
            System.out.println("User: " + userId);
            System.out.println("Type: " + type);
            System.out.println("Content: " + content);
            System.out.println("============================");
            
            userInboxes.computeIfAbsent(userId, k -> new java.util.concurrent.ConcurrentLinkedQueue<>())
                .add(body);
            
        } catch (Exception e) {
            System.err.println("Failed to parse notification: " + e.getMessage());
        }
    }
    
    private void handleGenericMessage(String channel, String body) {
        System.out.println("=== Generic Message ===");
        System.out.println("Channel: " + channel);
        System.out.println("Body: " + body);
        System.out.println("=======================");
    }
    
    public java.util.Queue<String> getUserInbox(String userId) {
        return userInboxes.getOrDefault(userId, new java.util.concurrent.ConcurrentLinkedQueue<>());
    }
    
    public int getUnreadCount(String userId) {
        return userInboxes.getOrDefault(userId, new java.util.concurrent.ConcurrentLinkedQueue<>()).size();
    }
}
```

---

## Step 5: Activity Subscriber

```java
// subscriber/ActivitySubscriber.java
package com.learning.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActivitySubscriber extends MessageListenerAdapter {
    
    private final ObjectMapper objectMapper;
    private final Map<String, java.util.List<Map>> userActivities;
    
    public ActivitySubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.userActivities = new ConcurrentHashMap<>();
    }
    
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> activity = objectMapper.readValue(body, Map.class);
            
            String userId = (String) activity.get("userId");
            String activityType = (String) activity.get("activity");
            
            System.out.println("=== User Activity ===");
            System.out.println("User: " + userId);
            System.out.println("Activity: " + activityType);
            System.out.println("====================");
            
            userActivities.computeIfAbsent(userId, k -> new java.util.concurrent.CopyOnWriteArrayList<>())
                .add(activity);
            
        } catch (Exception e) {
            System.err.println("Failed to process activity: " + e.getMessage());
        }
    }
    
    public java.util.List<Map> getUserActivities(String userId) {
        return userActivities.getOrDefault(userId, new java.util.concurrent.CopyOnWriteArrayList<>());
    }
    
    public void clearUserActivities(String userId) {
        userActivities.remove(userId);
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

---

# Real-World Project: Session Store

## Project Overview

**Duration**: 10+ hours  
**Difficulty**: Advanced  
**Concepts Used**: Distributed sessions, Session clustering, User authentication, Session persistence

This comprehensive project implements a distributed session store using Redis.

---

## Project Structure

```
24-redis/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   └── SessionConfig.java
│   ├── model/
│   │   ├── UserSession.java
│   │   └── SessionData.java
│   ├── service/
│   │   ├── SessionService.java
│   │   ├── SessionRegistry.java
│   │   └── SessionCleanupService.java
│   ├── controller/
│   │   └── SessionController.java
│   └── filter/
│       └── SessionAuthenticationFilter.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: Session Configuration

```java
// config/SessionConfig.java
package com.learning.config;

import com.learning.model.UserSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class SessionConfig {
    
    public static final String SESSION_KEY_PREFIX = "session:";
    public static final long SESSION_TTL_SECONDS = 1800;
    public static final long SESSION_EXTENDED_TTL_SECONDS = 604800;
    
    @Bean
    public RedisTemplate<String, UserSession> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, UserSession> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public RedisTemplate<String, Object> attributeRedisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.afterPropertiesSet();
        return template;
    }
}
```

---

## Step 2: User Session Model

```java
// model/UserSession.java
package com.learning.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class UserSession implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String userId;
    private String username;
    private Map<String, Object> attributes;
    private Instant createdAt;
    private Instant lastAccessedAt;
    private Instant expiresAt;
    private String ipAddress;
    private String userAgent;
    private Boolean active;
    
    public UserSession() {
        this.createdAt = Instant.now();
        this.lastAccessedAt = Instant.now();
        this.attributes = new HashMap<>();
        this.active = true;
    }
    
    public UserSession(String sessionId, String userId, String username) {
        this();
        this.sessionId = sessionId;
        this.userId = userId;
        this.username = username;
        this.expiresAt = Instant.now().plusSeconds(1800);
    }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public Map<String, Object> getAttributes() { return attributes; }
    public void setAttributes(Map<String, Object> attributes) { this.attributes = attributes; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(Instant lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }
    
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
        this.lastAccessedAt = Instant.now();
    }
    
    public Object getAttribute(String key) {
        this.lastAccessedAt = Instant.now();
        return this.attributes.get(key);
    }
    
    public void removeAttribute(String key) {
        this.attributes.remove(key);
        this.lastAccessedAt = Instant.now();
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    public void touch() {
        this.lastAccessedAt = Instant.now();
    }
    
    public long getIdleTimeSeconds() {
        return java.time.Duration.between(lastAccessedAt, Instant.now()).getSeconds();
    }
}
```

---

## Step 3: Session Service

```java
// service/SessionService.java
package com.learning.service;

import com.learning.config.SessionConfig;
import com.learning.model.UserSession;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class SessionService {
    
    private final RedisTemplate<String, UserSession> sessionRedisTemplate;
    private final RedisTemplate<String, Object> attributeRedisTemplate;
    
    public SessionService(
            RedisTemplate<String, UserSession> sessionRedisTemplate,
            RedisTemplate<String, Object> attributeRedisTemplate) {
        this.sessionRedisTemplate = sessionRedisTemplate;
        this.attributeRedisTemplate = attributeRedisTemplate;
    }
    
    public UserSession createSession(String userId, String username, 
                                      String ipAddress, String userAgent) {
        String sessionId = UUID.randomUUID().toString();
        
        UserSession session = new UserSession(sessionId, userId, username);
        session.setIpAddress(ipAddress);
        session.setUserAgent(userAgent);
        
        saveSession(session);
        
        addToUserSessions(userId, sessionId);
        
        return session;
    }
    
    public void saveSession(UserSession session) {
        String key = SessionConfig.SESSION_KEY_PREFIX + session.getSessionId();
        sessionRedisTemplate.opsForValue().set(key, session, 
            Duration.ofSeconds(SessionConfig.SESSION_TTL_SECONDS));
    }
    
    public Optional<UserSession> getSession(String sessionId) {
        String key = SessionConfig.SESSION_KEY_PREFIX + sessionId;
        UserSession session = sessionRedisTemplate.opsForValue().get(key);
        
        if (session != null) {
            if (session.isExpired()) {
                invalidateSession(sessionId);
                return Optional.empty();
            }
            session.touch();
            saveSession(session);
        }
        
        return Optional.ofNullable(session);
    }
    
    public void invalidateSession(String sessionId) {
        Optional<UserSession> session = getSession(sessionId);
        
        String key = SessionConfig.SESSION_KEY_PREFIX + sessionId;
        sessionRedisTemplate.delete(key);
        
        removeFromUserSessions(session.map(UserSession::getUserId).orElse(null), sessionId);
    }
    
    public void extendSession(String sessionId) {
        Optional<UserSession> sessionOpt = getSession(sessionId);
        
        sessionOpt.ifPresent(session -> {
            session.setExpiresAt(Instant.now().plusSeconds(SessionConfig.SESSION_EXTENDED_TTL_SECONDS));
            session.touch();
            saveSession(session);
        });
    }
    
    public void setSessionAttribute(String sessionId, String key, Object value) {
        Optional<UserSession> sessionOpt = getSession(sessionId);
        
        sessionOpt.ifPresent(session -> {
            session.setAttribute(key, value);
            saveSession(session);
        });
    }
    
    public Object getSessionAttribute(String sessionId, String key) {
        Optional<UserSession> sessionOpt = getSession(sessionId);
        
        if (sessionOpt.isEmpty()) {
            return null;
        }
        
        sessionOpt.get().touch();
        saveSession(sessionOpt.get());
        
        return sessionOpt.get().getAttribute(key);
    }
    
    public void removeSessionAttribute(String sessionId, String key) {
        Optional<UserSession> sessionOpt = getSession(sessionId);
        
        sessionOpt.ifPresent(session -> {
            session.removeAttribute(key);
            saveSession(session);
        });
    }
    
    private void addToUserSessions(String userId, String sessionId) {
        String key = "user:sessions:" + userId;
        attributeRedisTemplate.opsForSet().add(key, sessionId);
        attributeRedisTemplate.expire(key, Duration.ofSeconds(SessionConfig.SESSION_TTL_SECONDS * 2));
    }
    
    private void removeFromUserSessions(String userId, String sessionId) {
        if (userId == null) return;
        
        String key = "user:sessions:" + userId;
        attributeRedisTemplate.opsForSet().remove(key, sessionId);
    }
    
    public Set<Object> getUserSessionIds(String userId) {
        String key = "user:sessions:" + userId;
        return attributeRedisTemplate.opsForSet().members(key);
    }
    
    public void invalidateAllUserSessions(String userId) {
        Set<Object> sessionIds = getUserSessionIds(userId);
        
        for (Object sessionId : sessionIds) {
            invalidateSession((String) sessionId);
        }
    }
    
    public Set<String> getAllActiveSessions() {
        return sessionRedisTemplate.keys(SessionConfig.SESSION_KEY_PREFIX + "*");
    }
    
    public Map<String, UserSession> getActiveSessions(int limit) {
        Set<String> keys = getAllActiveSessions();
        java.util.Map<String, UserSession> sessions = new java.util.HashMap<>();
        
        int count = 0;
        for (String key : keys) {
            if (count >= limit) break;
            UserSession session = sessionRedisTemplate.opsForValue().get(key);
            if (session != null && session.getActive()) {
                sessions.put(session.getSessionId(), session);
                count++;
            }
        }
        
        return sessions;
    }
    
    public long getActiveSessionCount() {
        Set<String> keys = getAllActiveSessions();
        return keys != null ? keys.size() : 0;
    }
}
```

---

## Step 4: Session Registry

```java
// service/SessionRegistry.java
package com.learning.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SessionRegistry {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    public SessionRegistry(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    
    public void registerOnlineUser(String userId, String sessionId) {
        String key = "online:users:" + userId;
        redisTemplate.opsForValue().set(key, sessionId, Duration.ofMinutes(30));
        
        redisTemplate.opsForSet().add("online:users", userId);
    }
    
    public void unregisterOnlineUser(String userId) {
        redisTemplate.delete("online:users:" + userId);
        redisTemplate.opsForSet().remove("online:users", userId);
    }
    
    public boolean isUserOnline(String userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("online:users:" + userId));
    }
    
    public Set<Object> getOnlineUsers() {
        return redisTemplate.opsForSet().members("online:users");
    }
    
    public long getOnlineUserCount() {
        Long count = redisTemplate.opsForSet().size("online:users");
        return count != null ? count : 0;
    }
    
    public void recordUserActivity(String userId) {
        String key = "user:last-activity:" + userId;
        redisTemplate.opsForValue().set(key, System.currentTimeMillis(), 
            Duration.ofMinutes(30));
    }
    
    public long getLastActivity(String userId) {
        String key = "user:last-activity:" + userId;
        Long timestamp = (Long) redisTemplate.opsForValue().get(key);
        return timestamp != null ? timestamp : 0;
    }
    
    public void trackUserPageView(String userId, String pagePath) {
        String key = "user:pageviews:" + userId;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofDays(1));
    }
    
    public long getPageViewCount(String userId) {
        String key = "user:pageviews:" + userId;
        Long count = (Long) redisTemplate.opsForValue().get(key);
        return count != null ? count : 0;
    }
}
```

---

## Step 5: Session Cleanup Service

```java
// service/SessionCleanupService.java
package com.learning.service;

import com.learning.config.SessionConfig;
import com.learning.model.UserSession;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SessionCleanupService {
    
    private final RedisTemplate<String, UserSession> sessionRedisTemplate;
    
    public SessionCleanupService(RedisTemplate<String, UserSession> sessionRedisTemplate) {
        this.sessionRedisTemplate = sessionRedisTemplate;
    }
    
    @Scheduled(fixedRate = 300000)
    public void cleanupExpiredSessions() {
        System.out.println("Starting expired session cleanup...");
        
        Set<String> keys = sessionRedisTemplate.keys(SessionConfig.SESSION_KEY_PREFIX + "*");
        int cleanedCount = 0;
        
        if (keys != null) {
            for (String key : keys) {
                UserSession session = sessionRedisTemplate.opsForValue().get(key);
                
                if (session == null || session.isExpired()) {
                    sessionRedisTemplate.delete(key);
                    cleanedCount++;
                }
            }
        }
        
        System.out.println("Cleaned up " + cleanedCount + " expired sessions");
    }
    
    @Scheduled(fixedRate = 900000)
    public void cleanupInactiveSessions() {
        System.out.println("Starting inactive session cleanup...");
        
        Set<String> keys = sessionRedisTemplate.keys(SessionConfig.SESSION_KEY_PREFIX + "*");
        int cleanedCount = 0;
        
        if (keys != null) {
            for (String key : keys) {
                UserSession session = sessionRedisTemplate.opsForValue().get(key);
                
                if (session != null && session.getIdleTimeSeconds() > 1800) {
                    session.setActive(false);
                    sessionRedisTemplate.delete(key);
                    cleanedCount++;
                }
            }
        }
        
        System.out.println("Cleaned up " + cleanedCount + " inactive sessions");
    }
    
    public void forceCleanup(String sessionId) {
        String key = SessionConfig.SESSION_KEY_PREFIX + sessionId;
        sessionRedisTemplate.delete(key);
        System.out.println("Force cleaned session: " + sessionId);
    }
    
    public void forceCleanupUser(String userId) {
        Set<Object> sessionIds = sessionRedisTemplate.opsForSet()
            .members("user:sessions:" + userId);
        
        if (sessionIds != null) {
            for (Object sessionId : sessionIds) {
                forceCleanup((String) sessionId);
            }
        }
        
        sessionRedisTemplate.delete("user:sessions:" + userId);
        System.out.println("Force cleaned all sessions for user: " + userId);
    }
}
```

---

## Build Instructions

```bash
# Start Redis
docker run -p 6379:6379 redis

# Access Redis CLI
docker exec -it <container_id> redis-cli

cd 24-redis
mvn clean compile
mvn spring-boot:run
```