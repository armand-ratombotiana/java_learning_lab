# Why Redis Exists

Redis was created by Salvatore Sanfilippo in 2009 to solve a specific problem: traditional databases were too slow for real-time web applications.

## Problems Redis Solves

### Database Bottleneck
RDBMS disk I/O creates latency. Redis serves all data from RAM, achieving sub-millisecond response times for read and write operations.

### Limited Data Structures
RDBMS and key-value stores (Memcached) provide only basic get/set. Redis offers lists, sets, sorted sets, hashes, streams — enabling complex operations server-side without round-trips.

### Cache + Database Gap
Applications needed a cache layer to reduce database load, but Memcached was too primitive. Redis evolved from a cache into a primary database with persistence, replication, and clustering.

## Design Philosophy
- **Simplicity**: Single-threaded event loop eliminates concurrency complexity
- **Performance**: All data in RAM, optimized C implementation
- **Versatility**: One tool for caching, messaging, sessions, queues, and real-time analytics

## Inspiration
Redis was influenced by Memcached (caching) but expanded far beyond it. The data structure server concept was novel — no other database offered operations directly on lists, sets, and sorted sets.
