# Architecture: Bloom Filters in System Design

## Cache Architecture (Bloom Filter as First Line of Defense)

```
Request → Bloom Filter (in memory)
    ├── "definitely not" → return null (avoid lookup)
    └── "maybe yes" → Cache (HashSet/HashMap)
                        ├── hit → return value
                        └── miss → Database → populate cache
```

This pattern saves expensive cache lookups for non-existent keys.

## Distributed Bloom Filter

```
Request → Coordinator
              │
              ├── Shard 1 Bloom Filter
              ├── Shard 2 Bloom Filter
              └── Shard 3 Bloom Filter
                    ↓
              Merge results (OR all bit arrays)
              ↓
              If "definitely not" → skip shard lookup
```

## Database Index Architecture (Apache Cassandra)

```
Read Request
    ↓
Bloom Filter (check if key might exist)
    ↓ "maybe yes"
Memtable (in-memory sorted structure)
    ↓ not found
SSTable index (sparse index of SSTable files)
    ↓
SSTable (compressed data file)
```

Cassandra's Bloom filters avoid reading SSTables that definitely do not contain the key.

## Web Crawler Architecture

```
URL Frontier
    ↓
Bloom Filter (visited URLs)  ← memory-efficient dedup
    ↓
Small HashSet (confirmed visited)  ← exact verification
    ↓
Crawler Worker
    ↓
Extract Links → Bloom Filter → URL Frontier
```

## Spell Check Architecture

```
Input Word
    ↓
Bloom Filter (dictionary membership)
    ├── "definitely not in dictionary" → suggest corrections
    └── "maybe in dictionary" → exact check
                                ├── in dictionary → accept
                                └── not in dictionary → false positive → suggest
```

## Bitcoin SPV Wallet

```
Lightweight Wallet
    ↓
Create Bloom Filter from wallet addresses
    ↓
Send to Full Node
    ↓
Full Node returns matching transactions
    ↓
Wallet filters results (some false positives)
```

## System Design Patterns

### Write-Through Bloom Filter

```
Write: data → database (always)
            → Bloom filter (if successful)

Read: Bloom filter → "maybe" → database → return
                     "no" → return null immediately
```

### Replicated Bloom Filter

```java
// Periodically merge multiple Bloom filters:
BloomFilter combined = BloomFilter.create(funnel, totalN, 0.01);
for (BloomFilter shard : shards) {
    combined.putAll(shard);  // OR the bit arrays
}
```

## Java Ecosystem

- **Guava**: `BloomFilter<T>` — the standard Java implementation
- **Apache Cassandra**: uses Guava's BloomFilter internally
- **Apache HBase**: Java-based, uses Bloom filters for region lookups
- **Elasticsearch**: uses Bloom filters for field membership tests
- **Redisson**: Redis-backed distributed Bloom filter for Java
- **JRedisBloom**: Java client for RedisBloom module
