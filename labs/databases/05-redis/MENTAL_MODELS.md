# Mental Models: Redis

## Redis = Remote Dictionary Server
Think of Redis as a giant, thread-safe `Map<String, Object>` shared across all application instances. Every operation is atomic — no race conditions, no locks needed in application code.

## Data Structures as Specialized Tools
- **Strings**: A notepad — simple key-to-value storage
- **Lists**: A pipe — push on one end, pop from the other
- **Sets**: A bag of unique items — check membership, find overlaps
- **Sorted Sets**: A leaderboard — items always in order by score
- **Hashes**: A mini-object — group related fields together
- **Streams**: An event log — append messages, consume from any point

## Single-Threaded Event Loop
Every command executes in a single thread. This means:
- No concurrency bugs (no two commands half-complete)
- Atomic by default (no need for explicit locks on single keys)
- O(N) commands on large collections block everything

## TTL = Self-Cleaning Storage
Every key can have a Time-To-Live. Redis automatically deletes expired keys. This makes it perfect for caches, sessions, and temporary data — no garbage collection code needed.

## Replication = Scale Reads
One primary handles writes, replicas replicate data asynchronously. Use replicas for read scaling. If primary fails, promote a replica (Sentinel does this automatically).

## Redis Cluster = Sharded Hash Slots
Keys are distributed across 16384 hash slots. Each Redis node owns a subset of slots. This allows linear scaling — add nodes, redistribute slots.
