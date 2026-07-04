# Exercises: Redis

## Exercise 1: Basic Operations
```java
// Implement a CRUD repository using Redis hashes
// - Create a user hash with fields: name, email, createdAt
// - Read user fields individually and all at once
// - Update individual fields
// - Delete the user
```

## Exercise 2: Cache Layer
1. Configure Spring Cache with Redis
2. Annotate a slow repository method with `@Cacheable`
3. Test that the same request returns cached data
4. Implement cache eviction on updates with `@CacheEvict`
5. Set a TTL of 10 minutes

## Exercise 3: Rate Limiter
```java
// Implement a sliding window rate limiter:
// - Allow 100 requests per minute per user
// - Return 429 Too Many Requests when exceeded
// - Use atomic INCR with TTL
// - Optional: implement with Sorted Set for precise sliding window
```

## Exercise 4: Job Queue
```java
// Implement a task queue using Redis lists:
// - Producer adds jobs (JSON) to the queue
// - Consumer blocks waiting for jobs (BRPOP)
// - Handle job failure (re-queue with retry count)
// - Support multiple priority levels
```

## Exercise 5: Leaderboard
```java
// Implement a real-time game leaderboard:
// - Submit scores with ZADD
// - Get top N players
// - Get player's rank
// - Get scores around a player
// - Implement tie-breaking
```

## Exercise 6: Distributed Lock
```java
// Implement a distributed lock:
// - SETNX with TTL (prevent deadlock)
// - Lua-based safe unlock (only owner can release)
// - Automatic lock extension for long-running tasks
// - Test with concurrent threads
```

## Exercise 7: Session Store
```java
// Configure Spring Session with Redis:
// - Replace Tomcat HTTP session with Redis-backed session
// - Verify session persists across application restarts
// - Scale to 2 instances, verify session sharing
```

## Exercise 8: Pub/Sub vs Streams
1. Implement a notification system with Pub/Sub
2. Implement the same with Redis Streams
3. Compare: message persistence, replay capability, consumer groups
4. Implement a dead-letter queue for failed stream messages

## Exercise 9: Cluster Mode
1. Set up a 6-node Redis Cluster (3 masters, 3 replicas)
2. Connect with Lettuce cluster support
3. Insert and query data across slots
4. Test failover by killing a master
5. Verify automatic slot redistribution

## Exercise 10: Performance Benchmark
```java
// Benchmark in Java:
// 1. Insert 100K items individually vs pipelined
// 2. Read 100K keys individually vs pipelined
// 3. Compare serialization: JSON vs Protocol Buffers vs Java Serialization
// 4. Measure memory with MEMORY USAGE
```
