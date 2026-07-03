# 24 - Redis Caching

Redis in-memory data store operations with Jedis client. Covers string operations (set, get, setex, incr), list operations (lpush, lindex, lrange), set operations (sadd, smembers, scard, sismember), hash operations (hmset, hget, hgetAll), sorted sets (zadd, zrank, zrevrange), pub/sub messaging, and transactions (multi/exec).

## Prerequisites

- Java 11+
- Maven 3.x
- Redis server running on localhost:6379

## Key Concepts

- Redis data types: strings, lists, sets, hashes, sorted sets
- Key expiration with SETEX
- Atomic operations: INCR
- Pub/Sub: publish to channels, subscribe for messages
- Transactions: MULTI/EXEC for atomic batch operations
- Jedis client API
- Connection management and pooling

## Module Structure

- `src/main/java/com/learning/lab/module24/Lab.java` - Redis operations lab
- `src/test/` - Redis test implementations
- `SOLUTION/` - Solution code

## Learning Objectives

- Perform CRUD operations on different Redis data types
- Implement caching patterns with time-to-live
- Use Redis pub/sub and transactions

## Estimated Time

- 1-2 hours

## How to Run

```bash
cd 24-redis
mvn compile exec:java -Dexec.mainClass="com.learning.lab.module24.Lab"
```
