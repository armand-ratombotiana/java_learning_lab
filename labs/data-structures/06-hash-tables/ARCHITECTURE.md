# Architecture: Hash Tables in System Design

## Caching Architecture

```
┌──────────┐     ┌──────────────┐     ┌──────────┐
│ Web      │────→│ Cache Layer  │────→│ Database │
│ Servers  │     │ (HashMap)    │     │          │
└──────────┘     └──────────────┘     └──────────┘
                     │
                     ▼
              ┌──────────────┐
              │ Eviction     │
              │ (LRU/TTL)    │
              └──────────────┘
```

Hash maps power in-memory caches. Cache-aside pattern: check map first, if miss, query DB and populate.

## Distributed Hash Tables (DHT)

```
Consistent Hash Ring:
    ┌─── Node A ───┐
    │              │
Node D             │ Node B
    │              │
    └─── Node C ───┘

Keys distributed across nodes using consistent hashing.
Minimal reorganization when nodes join/leave.
```

Used in: Cassandra, DynamoDB, Memcached, Chord protocol.

## Java Collections Hierarchy

```
Map (interface)
├── HashMap (general purpose)
├── LinkedHashMap (insertion/access order)
├── TreeMap (sorted, Red-Black tree)
├── EnumMap (enum keys, array-backed)
├── IdentityHashMap (reference equality)
├── WeakHashMap (weak refs for GC)
└── ConcurrentHashMap (thread-safe)
    └── ConcurrentSkipListMap (sorted, concurrent)
```

## Pattern: Two-Stage Lookup

```
Stage 1: HashMap lookup by key → O(1)
Stage 2: If found, return; else query database → populate map
Stage 3: Return value

This is the cache-aside pattern.
```

## Database Hash Index Architecture

```
Hash Index:
Table: hash(key) → page_id → offset_in_page

Used for: equality lookups only (not range queries)
Examples: InnoDB adaptive hash index, PostgreSQL hash index
Compared to B-tree: O(1) vs O(log n), no range scans
```

## Load Balancer Architecture

```
Consistent hash-based routing:
Hash(client_ip) → server_id

Benefits:
- Same client always goes to same server (session stickiness)
- Minimal remapping when servers change
- Used in: HAProxy, NGINX, Amazon ELB
```

## Java HashMap in the Ecosystem

- **Spring Framework**: `DefaultSingletonBeanRegistry` uses HashMap for singleton beans
- **Hibernate**: first-level cache (Session) uses HashMap; second-level cache uses distributed maps
- **Jackson**: serialization/deserialization metadata stored in HashMaps
- **Tomcat**: session storage uses ConcurrentHashMap
