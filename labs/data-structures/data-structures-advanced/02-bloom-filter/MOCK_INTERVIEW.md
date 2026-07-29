# Mock Interview: Bloom Filter

## Setting

- **Round**: Onsite system design + coding
- **Duration**: 60 minutes
- **Focus**: Probabilistic data structures, system design

---

## Transcript

### Part 1: System Design Warm-up (10 min)

**Interviewer:** We're building a web crawler for a search engine. We need to crawl billions of URLs but can't store the visited set in memory. How do you track which URLs have been visited?

**Candidate:** I'd use a Bloom filter. With 1B URLs and 0.1% false positive rate, we'd need about 1.7GB of memory. This is feasible on a single server, and we'd avoid storing the full URLs which would be 50GB+.

**Interviewer:** What happens when the Bloom filter gets full? After 1B insertions, the false positive rate starts climbing.

**Candidate:** I'd use a Scalable Bloom Filter — when the current filter reaches capacity, I add a new larger filter. On check, I query all filters in sequence. Old filters can be archived to disk. Alternatively, I can partition: first n bits of URL hash determines which shard's Bloom filter to check.

**Interviewer:** Good. What about false positives — you'd miss URLs that you've already visited and skip them.

**Candidate:** Bloom filters only have false positives, never false negatives. So we'd never miss a URL. We'd only occasionally re-crawl a URL we've already visited, which is acceptable for a search engine (we re-crawl periodically anyway). The Bloom filter says "definitely not visited" or "maybe visited" — in the latter case we can check a secondary store for confirmation.

**Interviewer:** And URL normalisation?

**Candidate:** I'd normalise URLs before feeding to the Bloom: lowercase, sort query params, remove fragments, resolve relative paths. Also: separate Bloom for each subdomain for faster rebuild.

---

### Part 2: Core Problem — Cache Shield (30 min)

**Interviewer:** Now the coding part. We have a cache in front of a database. When the cache misses for a hot key, thousands of requests simultaneously hit the DB. Implement a shield using a Bloom filter.

**Candidate:** Let me restate: I need to ensure that only one request queries the DB per key on cache miss. The Bloom filter helps avoid DB queries for keys that definitely don't exist.

Let me clarify:
1. Do we know the set of valid keys ahead of time?
2. What's the cache consistency model?
3. Should I handle writes/updates?

**Interviewer:** Assume we know the set of valid DB keys — we can pre-populate the Bloom. Read-only workload for this problem.

**Candidate:** Great. My approach:

1. **Bloom filter** pre-populated with all valid keys
2. **ConcurrentHashMap** for cache
3. **Per-key mutex** using `synchronized` on a lock object per key
4. **Double-check locking** — check cache again after acquiring lock

```java
class CacheShield {
    ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();
    BloomFilter bloom; // pre-populated
    ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

    String get(String key) {
        String val = cache.get(key);
        if (val != null) return val;

        if (!bloom.mightContain(key)) return null;

        Object lock = locks.computeIfAbsent(key, k -> new Object());
        synchronized (lock) {
            val = cache.get(key);
            if (val != null) return val;
            val = db.query(key);
            if (val != null) cache.put(key, val);
            return val;
        }
    }
}
```

**Interviewer:** Walk me through the Bloom filter check. What guarantee does it give?

**Candidate:** When `mightContain` returns `false`, the key is **definitely not** in the DB. So we return null immediately, saving a DB query. When it returns `true`, the key **might** be in the DB — we still need to check the actual DB.

**Interviewer:** What's the impact of a false positive?

**Candidate:** A false positive causes one (and only one) DB query that returns null. This is acceptable because:
1. It's one query, not a stampede
2. The mutex ensures no concurrent queries for the same key
3. The DB query result is cached (as null) to prevent repeated false positives

Actually, I should cache null values too:

```java
if (val != null) cache.put(key, val);
else cache.put(key, "NULL_SENTINEL");
```

This way, subsequent requests for the same non-existent key hit the cache instead of the Bloom filter → DB path.

**Interviewer:** How do you handle the lock map growing unboundedly?

**Candidate:** I'd use a bounded cache for locks — or remove the lock entry after the DB query completes. Or use a striped lock with N buckets:

```java
Object[] locks = new Object[1024];
for (int i = 0; i < 1024; i++) locks[i] = new Object();

int bucket = Math.abs(key.hashCode() % 1024);
synchronized (locks[bucket]) { ... }
```

This bounds memory while still providing concurrency — two different keys in different buckets can proceed in parallel.

**Interviewer:** Good. Now let's talk about the Bloom filter implementation. Show me.

```java
class BloomFilter {
    BitSet bitset;
    int size, hashCount;

    BloomFilter(int expectedElements, double fpRate) {
        size = (int)(-expectedElements * Math.log(fpRate) / (Math.log(2) * Math.log(2)));
        hashCount = (int)((double)size / expectedElements * Math.log(2));
        bitset = new BitSet(size);
    }

    void add(String element) {
        int h1 = element.hashCode();
        int h2 = h1 >>> 16 | h1 << 16;
        for (int i = 0; i < hashCount; i++) {
            bitset.set(Math.abs((h1 + i * h2) % size));
        }
    }

    boolean mightContain(String element) {
        int h1 = element.hashCode();
        int h2 = h1 >>> 16 | h1 << 16;
        for (int i = 0; i < hashCount; i++) {
            if (!bitset.get(Math.abs((h1 + i * h2) % size))) return false;
        }
        return true;
    }
}
```

**Interviewer:** Explain the double hashing.

**Candidate:** Standard Bloom theory says we need k independent hash functions. Using k different hash functions is expensive. The Kirsch-Mitzenmacher trick shows that `h1 + i·h2 (mod m)` for i = 0..k-1 produces k hash values that are effectively independent. I use Java's `hashCode()` as h1 and a bitwise rotation as h2.

For production, I'd use MurmurHash3 for better distribution.

---

### Part 3: Follow-ups (15 min)

**Interviewer:** How do you handle writes? If a new key is added to the DB, the Bloom filter doesn't know about it yet.

**Candidate:** We need to update the Bloom filter on every DB write. This is cheap — O(k). We can batch updates and flush periodically. The worst case: a key is in the DB but not in Bloom — this causes a cache miss + DB query, which is correct but inefficient.

**Alternative**: Don't pre-populate Bloom. Instead, populate lazily — every time a DB query returns a value, add the key to Bloom. After sufficient queries, Bloom covers the hot set.

**Interviewer:** How do you handle the cache + Bloom update atomically?

**Candidate:** They don't need to be atomic. The write path is:
1. Write to DB
2. Invalidate cache key
3. Add key to Bloom (or rebuild Bloom on schedule)

Cache invalidation ensures stale values aren't served. Bloom being slightly stale is acceptable (FP increases slightly).

**Interviewer:** How would you test this system?

**Candidate:**
1. **Unit test**: Get existing key, get non-existing key, get key that was deleted
2. **Concurrency test**: 100 threads get same key on cache miss → verify 1 DB query
3. **Bloom FP test**: Query keys known not in DB → measure actual FP rate
4. **Performance test**: Compare DB QPS with/without shield
5. **Chaos test**: Kill cache, verify system recovers without DB overload

---

### Part 4: Summary (5 min)

**Interviewer:** Good. Any questions?

**Candidate:** Yes — how does your team currently handle cache stampede? And what's your typical latency budget for cache lookups?

**[Interview continues with candidate questions]**

---

## Debrief

### What Went Well
- Clear understanding of Bloom filter properties (no false negatives)
- Double-check locking pattern explained correctly
- Lock striping suggestion for bounded memory
- Proactive null-value caching suggestion
- Good follow-up handling of writes and lazy population

### Areas for Growth
- Could have mentioned Counting Bloom for delete support
- Bloom parameter derivation was smooth but could be faster
- Lock cleanup was an afterthought

### Score
| Category | Score (1-5) |
|----------|-------------|
| System Design Understanding | 5 |
| DS Choice Justification | 5 |
| Code Quality | 4 |
| Complexity Analysis | 4 |
| Concurrency Handling | 4 |
| Communication | 5 |
| **Overall** | **4.5 / 5** |