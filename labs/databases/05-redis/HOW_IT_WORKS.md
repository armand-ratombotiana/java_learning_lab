# How Redis Works

## Single-Threaded Event Loop

```
Main thread: Read command → Execute → Write response
                  ↑                        |
                  └───── Event Loop ────────┘
```

Redis uses `epoll`/`kqueue`/`select` for I/O multiplexing. All commands execute in one thread. Since Redis 6.0, threaded I/O handles socket reads/writes while command execution remains single-threaded.

## Memory Management

- **jemalloc**: Default allocator, optimized for Redis patterns
- **Object sharing**: Small integers (0-9999) shared across instances
- **String encoding**: INT (8-byte long), EMBSTR (≤44 bytes), RAW
- **Lazy freeing**: UNLINK, FLUSHDB ASYNC — frees memory in background thread

## Persistence

### RDB (Redis Database File)
```
fork() → child writes snapshot to temp file → rename → done
```
- Blocking: fork() freezes parent for < 1ms typically
- Default: every 5 min if 100+ changes, every 60s if 10000+ changes

### AOF (Append-Only File)
```
Write command → AOF buffer → fsync (every sec or every write)
```
- Rewrite: fork + compact commands into minimal set
- Append-only: crash-safe with `appendfsync always`

## Eviction Policies

```
noeviction        → Error on writes when memory full
allkeys-lru       → Evict least recently used keys
volatile-lru      → Evict LRU keys with TTL set
allkeys-random    → Evict random keys
volatile-ttl      → Evict keys with shortest TTL
allkeys-lfu       → Evict least frequently used (4.0+)
```

## Replication Protocol

1. Replica connects to primary, sends SYNC/PSYNC
2. Primary forks, creates RDB snapshot, sends to replica
3. Primary buffers writes during RDB transfer
4. Replica loads RDB, applies buffered commands
5. Continuous partial replication via replication backlog

## Pub/Sub Model

```
Publisher → Channel → Subscriber(s)
              │
         Pattern Subscriber
```

- Fire-and-forget: no message persistence
- No delivery guarantees (subscriber must be connected)
- Pattern matching: PSUBSCRIBE "news.*"
- Sharded Pub/Sub (7.0+): works with cluster mode
