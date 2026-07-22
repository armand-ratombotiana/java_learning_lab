# Interview Questions: Redis (Oracle Comparison)

## Oracle-Specific Questions
- Compare Redis in-memory caching with Oracle's Result Cache and Keep Pool — when would you use each?
- How does Redis persistence (RDB/AOF) compare to Oracle's REDO logs and UNDO?
- Compare Redis Sentinel vs Oracle Data Guard for high availability failover.
- How does Redis Cluster compare to Oracle RAC for distributed caching architecture?
- Explain Oracle's Coherence vs Redis for Java application caching.
- How would you use Redis with Oracle in a microservices architecture?
- Compare Redis transactions (MULTI/EXEC) with Oracle transactions (BEGIN/COMMIT).
- Oracle to Redis migration: when does it make sense to move session state to Redis?

## Google Cloud / Technical
- Cloud Memorystore for Redis vs self-managed Redis on Compute Engine
- Cloud CDN + Redis for global application caching
- Cloud Run + Redis for stateless application architecture

## Microsoft / Azure
- Azure Cache for Redis vs Redis on Azure VMs
- Azure Redis with Spring Boot for session caching
- Azure Container Apps with Redis sidecar pattern

## Amazon / AWS
- Amazon ElastiCache for Redis vs Redis on EC2
- DAX (DynamoDB Accelerator) vs Redis for caching
- AWS Global Datastore for Redis cross-region replication

## Apple
- Redis for Apple Push Notification caching
- Session management for Apple services with Redis

## LeetCode-Style Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| Cache Design | LRU Cache | Medium | Doubly Linked List |
| Rate Limiter | Sliding Window | Medium | Sorted Set |
| Distributed Lock | Redlock | Hard | SET NX EX |
| Leaderboard | Real-time Ranking | Medium | Sorted Set |
| Session Store | Session TTL | Easy | SETEX |
| Message Queue | Task Queue | Medium | LPUSH + BRPOP |
| Counter | Atomic Counter | Easy | INCR |
| Bloom Filter | Caching | Medium | BITFIELD |

## Production Scenarios
- Scenario 1: "Redis maxmemory-policy evicting hot keys causing cache storms"
- Scenario 2: "Redis replication lag causing stale cache reads after failover"
- Scenario 3: "Redis Cluster resharding causing MOVED redirect storm"
- Scenario 4: "AOF corruption after power failure — recovery strategy"

## Interview Patterns & Tips
- Oracle interviews value Redis knowledge for application performance optimization
- Expect caching strategy questions: how to use Redis with Oracle database
- Know Redis Cluster architecture and limitations
- Redis expertise complements Oracle skills for full-stack performance roles
- $110K-$170K for roles combining Oracle + Redis expertise
