# Interview Questions: Bloom Filter

## 17 FAANG-Style Interview Questions

### Question 1
> Implement a Bloom filter with add and mightContain methods.

**Answer:**

```java
class BloomFilter {
    BitSet bitset;
    int size, hashCount;

    BloomFilter(int expectedElements, double fpRate) {
        this.size = (int)(-expectedElements * Math.log(fpRate) / (Math.log(2) * Math.log(2)));
        this.hashCount = (int)((double)size / expectedElements * Math.log(2));
        this.bitset = new BitSet(size);
    }

    void add(String s) {
        int h1 = s.hashCode() & 0x7FFFFFFF;
        int h2 = (h1 >>> 16) | (h1 << 16);
        for (int i = 0; i < hashCount; i++) {
            bitset.set((h1 + i * h2) % size);
        }
    }

    boolean mightContain(String s) {
        int h1 = s.hashCode() & 0x7FFFFFFF;
        int h2 = (h1 >>> 16) | (h1 << 16);
        for (int i = 0; i < hashCount; i++) {
            if (!bitset.get((h1 + i * h2) % size)) return false;
        }
        return true;
    }
}
```

**Complexity**: O(k) time, O(m) space.

---

### Question 2
> Explain the false positive probability formula for a Bloom filter. How do you choose m and k?

**Answer:**
After inserting n elements into m bits with k hash functions, the probability a specific bit is still 0:
`P(bit=0) = (1 - 1/m)^(k·n) ≈ e^(-k·n/m)`

False positive: all k bits checked are 1:
`P(FP) = (1 - e^(-k·n/m))^k`

**Optimal k**: `k_opt = (m/n) · ln(2)` ≈ 0.693 · m/n

**Optimal m**: `m = -n · ln(P) / ln(2)^2`

---

### Question 3
> Your Bloom filter returns too many false positives. How do you debug?

**Answer:**
1. **Check bit array size**: m too small? Use formula `m = -n·ln(P)/ln²(2)`
2. **Check hash functions**: Are they well-distributed? Test uniformity
3. **Check k value**: Wrong k leads to more collisions
4. **Check actual n**: Did you insert more elements than expected? Bloom doesn't track count
5. **Check hash quality**: Java hashCode() is fast but not cryptographic. Use MurmurHash3

**Solution**: Track insert count. When approaching capacity, rebuild with larger filter.

---

### Question 4
> Design a web crawler deduplication system that can handle 10B URLs.

**Answer:**
Use a Bloom filter with:
- n = 10B, P = 0.001 (0.1% FP)
- m = -10B · ln(0.001) / ln²(2) ≈ 17.2 GB
- k = (17.2 GB · 8 / 10B) · ln(2) ≈ 9.5 → 10 hash functions

**Tiered approach**:
1. In-memory Bloom filter (hot URLs, most recent crawl)
2. On-disk Bloom filter (cold URLs)
3. LevelDB as fallback for FP resolution

**Crawl frontier** uses a separate "to-crawl" Bloom to avoid re-queueing.

---

### Question 5
> How does Chrome Safe Browsing use a Bloom filter?

**Answer:**
Chrome downloads a Bloom filter of known malicious URLs (~1MB for 10M entries). On each URL visit:
1. Check on-device Bloom filter
2. If "not in set" → safe (guaranteed)
3. If "maybe in set" → send hash prefix to Google's servers
4. Server responds with full verdict
5. Privacy: server only sees 32-bit hash prefix, not full URL

Updates: Chrome downloads a new filter every ~30 minutes.

---

### Question 6
> What's the difference between a Bloom filter and a Cuckoo filter?

**Answer:**
| Feature | Bloom Filter | Cuckoo Filter |
|---------|-------------|---------------|
| Deletion | No (Counting: yes) | Yes |
| FP rate | ~3% (optimal) | ~3% |
| Space | ~1.44 bits/item (1% FP) | ~1.05 bits/item |
| Lookup | O(k) | O(1) avg (2 probes) |
| Insert | O(k) | O(1) avg, O(n) worst (relocation) |

**Cuckoo filter**: Uses cuckoo hashing + fingerprints. Better space efficiency and supports deletion. Used in DPDK, Suricata IDS.

---

### Question 7
> Design a cache stampede prevention system using a Bloom filter.

**Answer:**
**Problem**: When a popular key expires, 1000s of requests hit the DB simultaneously.

**Solution**:
1. On cache miss, check Bloom filter before DB
2. Bloom filter indicates "does this key exist in DB?"
3. If "not in DB" → return empty (skip DB entirely)
4. If "maybe in DB" → allow ONE request to query DB, others wait

**Implementation**:
```java
String get(String key) {
    String val = cache.get(key);
    if (val != null) return val;

    if (!bloom.mightContain(key)) return null; // definitely not in DB

    synchronized(key.intern()) {
        val = cache.get(key); // double-check
        if (val != null) return val;
        val = db.query(key);
        if (val != null) {
            cache.put(key, val);
            bloom.add(key);
        }
        return val;
    }
}
```

---

### Question 8
> How would you implement a Counting Bloom filter?

**Answer:**
Replace each bit with a small counter (2-4 bits). Use an int array or byte array.

```java
class CountingBloomFilter {
    byte[] counters;  // 4-bit counters (0-15)
    int hashCount;

    void add(String s) {
        for (int h : getHashes(s)) {
            if (counters[h] < 15) counters[h]++;
        }
    }

    void remove(String s) {
        for (int h : getHashes(s)) {
            if (counters[h] > 0) counters[h]--;
        }
    }

    boolean mightContain(String s) {
        for (int h : getHashes(s)) {
            if (counters[h] == 0) return false;
        }
        return true;
    }
}
```

**Trade-off**: 4x memory for delete support. Counter overflow is possible (frozen at 15).

---

### Question 9
> Can a Bloom filter be used for set intersection? What about union?

**Answer:**
**Union**: Yes. Bitwise OR of two Bloom filters (same m, k) gives a filter representing the union of both sets.

**Intersection**: Approximate. Bitwise AND gives a lower bound on the intersection (false positives make it unreliable). The size of intersection can be estimated as:
`|A ∩ B| ≈ (|A| + |B| - |A ∪ B|)` where sizes are estimated from filter bit densities.

---

### Question 10
> How would you serialise a Bloom filter for storage/transmission?

**Answer:**
```java
byte[] serialize() { return bitset.toByteArray(); }

void deserialize(byte[] data) {
    bitset = BitSet.valueOf(data);
    size = bitset.length();
}
```

`BitSet.toByteArray()` gives compact representation. Store m and k as metadata. For transmission, prepend 4 bytes for m, 4 bytes for k, then the byte array.

---

### Question 11
> Explain the double hashing technique for Bloom filters. Why is it valid?

**Answer:**
Instead of k independent hash functions, compute two:
```
h1 = hash1(element)
h2 = hash2(element)
hi = (h1 + i * h2) % m
```

**Validity**: Kirsch and Mitzenmacher proved that this double-hashing scheme is equivalent to using k independent random hash functions — the false positive rate is the same. This saves computation and simplifies implementation.

---

### Question 12
> Design a URL shortener that prevents malicious URLs using a Bloom filter.

**Answer:**
1. Short URLs stored in DB
2. Bloom filter of known malicious URLs (~100K entries, tiny filter)
3. When creating short URL:
   - Check Bloom for original URL
   - If "maybe malicious" → flag for manual review
   - If "not in filter" → create short URL
4. When redirecting:
   - Check Bloom before redirect
   - If "maybe malicious" → show warning page
5. Update filter daily from threat intelligence feeds

---

### Question 13
> How would you implement a Bloom filter in a concurrent environment?

**Answer:**
Use `AtomicIntegerArray` instead of `BitSet`:
```java
AtomicIntegerArray bits = new AtomicIntegerArray(m);

void add(String s) {
    for (int h : getHashes(s)) {
        bits.set(h, 1); // atomic
    }
}
```

Or use `ReentrantReadWriteLock` around a regular BitSet — Bloom reads are more frequent than writes.

---

### Question 14
> Compare Bloom filters and HyperLogLog. When would you use each?

**Answer:**
| Feature | Bloom Filter | HyperLogLog |
|---------|-------------|-------------|
| Purpose | Membership test | Cardinality estimation |
| Answer | "Is X in set?" | "How many unique elements?" |
| Space | m bits = -n·ln(P)/ln²(2) | ~1.5 KB for 10^9 uniques |
| FP | Yes (tunable) | ~2% error on count |
| Complexity | O(k) add/check | O(1) add, O(1) count |

**Use Bloom** when you need membership queries. **Use HLL** when you only need element count (unique visitors, distinct terms).

---

### Question 15
> Design a password strength checker that rejects weak passwords from a 1B+ breached password list.

**Answer:**
1. Preprocess: extract all breached passwords → hash → build Bloom filter
2. n = 1B, P = 0.001: m ≈ 1.7 GB, k = 10
3. On signup:
   - Check password against Bloom filter
   - If "maybe in set" → require stronger password
   - If "not in set" → accept
4. Update filter monthly as new breaches occur
5. **Privacy**: Password never leaves client; Bloom filter is on-server

**Optimisation**: Tiered — first check against top 10K passwords (small exact set), then Bloom for full set.

---

### Question 16
> Explain the relationship between Bloom filter bit density and false positive rate.

**Answer:**
After n inserts into m bits with k hashes:
- **Bit density** d = 1 - (1 - 1/m)^(k·n) ≈ 1 - e^(-k·n/m)
- **FP rate** = d^k ≈ (1 - e^(-k·n/m))^k

At optimal k = (m/n)·ln(2), bit density d ≈ 0.5 (half of bits are 1). This is the "sweet spot" — maximum information per bit.

**Rule of thumb**: For optimal operation, ~50% of bits should be set. If density > 80%, the filter is overloaded.

---

### Question 17
> What's a Scalable Bloom Filter and when would you use it?

**Answer:**
A Scalable Bloom Filter is a series of Bloom filters with geometrically growing capacities. When one filter reaches capacity, a new larger filter is added. Elements are added to the latest filter. Membership check queries all filters (OR result).

**Advantages**: No need to know n in advance. Grows automatically. Good for streaming data.

**Disadvantages**: Slower lookups (check each filter). More complex maintenance.

**Use case**: Real-time event streams where element count is unknown (clickstream, log processing).