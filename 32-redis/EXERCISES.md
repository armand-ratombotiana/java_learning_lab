# Exercises - Redis

## Exercise 1: Basic Operations
Implement core Redis operations with Spring Data Redis.

1. Store and retrieve a user session as JSON string
2. Use Redis Hash to store user profile fields
3. Implement a simple page view counter using INCR

## Exercise 2: Cache-Aside Pattern
Build a caching layer for a service:

1. Create a `@Cacheable` method on a repository
2. Configure TTL (time-to-live) for cache entries
3. Add cache eviction with `@CacheEvict`
4. Measure response time improvement with/without cache

## Exercise 3: Distributed Locking
Implement distributed locking for critical sections:

1. Use Redisson to acquire a lock before processing
2. Implement a "reserve stock" operation with lock
3. Handle lock timeout and release
4. Test concurrent access scenarios

## Exercise 4: Rate Limiting
Build a rate limiter using Redis:

1. Implement sliding window algorithm with ZSET
2. Create a `@RateLimiter` annotation
3. Test limits with concurrent requests
4. Add rate limit headers to responses

## Exercise 5: Pub/Sub Messaging
Implement a simple notification system:

1. Set up Redis Pub/Sub for real-time updates
2. Create a message publisher service
3. Implement a subscriber listener
4. Test multi-subscriber message delivery

## Bonus Challenge
Build a real-time leaderboard system using Sorted Sets. Support: adding scores, getting top N users, getting user rank, and removing users.