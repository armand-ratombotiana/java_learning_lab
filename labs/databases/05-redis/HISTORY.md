# History: Redis

## Timeline

- **2009**: Redis created by Salvatore Sanfilippo (antirez)
- **2010**: Redis 1.0 – basic data structures, persistence
- **2011**: Redis 2.2 – sorted sets, transactions (MULTI/EXEC)
- **2012**: Redis 2.4 – replication improvements, AOF persistence
- **2013**: Redis 2.6 – Lua scripting (EVAL), milliseconds expiration
- **2014**: Redis 2.8 – partial resynchronization, Sentinel
- **2015**: Redis 3.0 – Redis Cluster (native sharding)
- **2016**: Redis 3.2 – GEO API, BITFIELD, SPOP improvements
- **2017**: Redis 4.0 – modules API, PSYNC2, lazy freeing
- **2018**: Redis 5.0 – Streams (XADD, XREAD, consumer groups)
- **2019**: Redis 6.0 – SSL, ACLs, RESP3, threaded I/O
- **2020**: Redis 6.2 – BF.CARD, ZMSCORE, improved client eviction
- **2022**: Redis 7.0 – Redis Functions, sharded pub/sub, AOFv3
- **2023**: Redis 7.2 – auto-tuning, vector similarity search module
- **2024**: Redis 7.4 – performance improvements, enhanced search

## Key Milestones

- **Redis Cluster**: Automatic sharding across multiple nodes with no central coordinator
- **Streams**: Redis entered the streaming data space with consumer groups, message acknowledgment, and blocking reads
- **Modules**: Extension system (RediSearch, RedisJSON, RedisGraph, RedisTimeSeries)
- **RESP3**: New protocol with typed replies, push notifications (pub/sub)

## Java Client History
- **Jedis**: Oldest, lightweight, synchronous only
- **Lettuce**: Most popular, supports sync/async/reactive, netty-based
- **Redisson**: Distributed Java objects (lock, semaphore, map) on top of Redis
- **Spring Data Redis**: Abstraction layer supporting Jedis, Lettuce, Redisson
