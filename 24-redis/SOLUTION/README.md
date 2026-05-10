# Redis Solution

## Concepts Covered

### Caching
- Key-value storage with TTL (time-to-live)
- String operations with SETEX for expiration

### Pub/Sub
- Publish/subscribe messaging pattern
- Channel-based message distribution

### Distributed Locks
- Redis-based distributed locking using SET with NX (only if not exists) and EX (expiration)
- Safe release using Lua script

### Data Structures
- **Strings**: Simple key-value with expiration
- **Hashes**: Field-value pairs for objects
- **Sets**: Unique collections
- **Sorted Sets**: Leaderboards with scores
- **Bitmaps**: Space-efficient boolean arrays
- **HyperLogLog**: Probabilistic cardinality estimation

## Running Tests

```bash
docker run -d -p 6379:6379 redis:7-alpine
mvn test -Dtest=RedisSolutionTest
```