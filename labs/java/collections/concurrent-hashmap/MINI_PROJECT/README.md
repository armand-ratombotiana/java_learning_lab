# Mini Project: Concurrent Cache

Build a thread-safe LRU cache backed by `StripedLockMap`.

Requirements:
- Fixed maximum capacity (evicts oldest entry when full)
- `computeIfAbsent` for atomic load-on-demand
- Configurable expiry (TTL per entry)
- Hit/miss statistics exposed via JMX

Deliverables:
- `ConcurrentLruCache.java` implementation
- `ConcurrentLruCacheTest.java` with concurrent access tests
- Performance comparison against `Caffeine` or `Guava Cache`
