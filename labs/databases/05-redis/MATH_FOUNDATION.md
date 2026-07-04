# Math Foundation: Redis

## Time Complexity of Core Operations

| Data Structure | Operation | Complexity |
|---|---|---|
| String | GET/SET | O(1) |
| List | LPUSH/RPOP | O(1) |
| List | LRANGE(n) | O(n) |
| Set | SADD/SREM/SISMEMBER | O(1) |
| Set | SMEMBERS | O(N) |
| Set | SINTER | O(N × M) |
| Sorted Set | ZADD | O(log N) |
| Sorted Set | ZRANGE | O(log N + M) |
| Hash | HSET/HGET | O(1) |
| Hash | HGETALL | O(N) |
| Stream | XADD | O(1) |
| Stream | XRANGE(n) | O(log N + M) |

## Memory Overhead

- Empty Redis instance: ~3MB
- Redis object overhead: 16 bytes per key
- Dict entry overhead: ~56 bytes per key-value pair
- String overhead: 4-8 bytes + actual data
- Sorted set (skiplist): ~40 bytes per element + string storage

## Eviction

- LRU approximation (24-bit timestamp, samples 5 keys by default)
- LFU (4.0+): counter with logarithmic increase, decay over time

## Redis Cluster

- 16384 hash slots (CRC16)
- Cluster bus port: 10000 + redis port
- MOVED redirect on wrong node
- No multi-key operations across slots

## Replication

- Asynchronous by default
- Replication backlog size: `repl-backlog-size` (default 1MB)
- Partial resync: uses backlog + replication ID
- Full resync: RDB transfer (size = used_memory)

## Pub/Sub Scaling

- One channel per pattern is O(N) subscribers
- Message fan-out: 1 publisher → N subscribers = O(N) message copies
- For high-throughput, use consumer groups with Streams instead

## Persistence Overhead

- RDB save: fork() + write full dataset
- AOF rewrite: fork() + write compact log
- AOF fsync: `always` (~100 ops/sec), `everysec` (~10000 ops/sec)
