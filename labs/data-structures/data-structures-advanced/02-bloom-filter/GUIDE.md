# Guide: Bloom Filter

## Overview

A **Bloom filter** is a space-efficient probabilistic data structure used to test whether an element is a member of a set. It can produce **false positives** (says element is in set when it isn't) but **never false negatives** (if it says element is not in set, it definitely isn't).

This property makes Bloom filters ideal for caching, deduplication, and any "definitely not" check that avoids expensive lookups.

### Why Not Use a HashSet?

| Aspect | HashSet | Bloom Filter |
|--------|---------|-------------|
| Memory (1M strings) | ~50 MB | ~1.2 MB (1% FP) |
| False positives | None | Yes (configurable) |
| False negatives | None | None |
| Delete | O(1) | Not supported (counting BF variant exists) |
| Enumeration | All elements | Not possible |

**Key Insight**: When you can tolerate a small false positive rate in exchange for massive memory savings, Bloom filter wins.

---

## ASCII Diagram

```
Element: "apple"
                    Hash Functions
                   /    |    \
                  k1    k2    k3
                  |     |     |
                  v     v     v
   Bit Array: [0 1 0 1 1 0 0 0 1 0 1 0 0 1 0 0]
               ^     ^         ^
               |     |         |
              set   set       set
             bit 3  bit 4     bit 8
```

Bloom filter bit array with k=3 hash functions. Each hash independently selects a bit position.

### Membership Test

```
Test "apple":   k1 → bit 3 = 1 ✓
                k2 → bit 4 = 1 ✓
                k3 → bit 8 = 1 ✓ → "Probably in set"

Test "banana":  k1 → bit 7 = 0 ✗ → "Definitely NOT in set"
```

---

## Source Code Walkthrough

The implementation follows the standard Bloom filter pattern.

### Bit Array (line ~5)

```java
private final BitSet bitset;
private final int size;        // m bits
private final int hashCount;   // k hash functions
```

`BitSet` from `java.util` provides a compact bit array with O(1) get/set operations.

### add(String element) — lines ~12-18

```java
public void add(String element) {
    int[] hashes = getHashes(element);
    for (int hash : hashes) {
        bitset.set(Math.abs(hash % size));
    }
}
```

**Walkthrough with `add("apple")`, m=16, k=3:**
```
hash1("apple") →  345 → |345 % 16| = 9 → set bit 9
hash2("apple") → 1234 → |1234 % 16| = 2 → set bit 2
hash3("apple") → 5678 → |5678 % 16| = 6 → set bit 6
```

**Complexity**: O(k) time, where k = number of hash functions. O(1) space (just setting bits).

### mightContain(String element) — lines ~20-26

```java
public boolean mightContain(String element) {
    int[] hashes = getHashes(element);
    for (int hash : hashes) {
        if (!bitset.get(Math.abs(hash % size))) return false;
    }
    return true;
}
```

**Logic**: If ANY bit is 0, the element is definitely not present. If ALL bits are 1, the element *might* be present.

### Hash Functions

```java
private int[] getHashes(String element) {
    int[] hashes = new int[hashCount];
    for (int i = 0; i < hashCount; i++) {
        // Double hashing technique: h1 + i * h2
        hashes[i] = h1(element) + i * h2(element);
    }
    return hashes;
}
```

Using the **double hashing** technique (Kirsch-Mitzenmacher optimisation): instead of k independent hash functions, compute two (`h1`, `h2`) and derive k as `h1 + i·h2`. This is provably equivalent for Bloom filters.

### Parameters

```java
public BloomFilter(int expectedElements, double falsePositiveRate) {
    this.size = (int) (-expectedElements * Math.log(falsePositiveRate)
                       / (Math.log(2) * Math.log(2)));
    this.hashCount = (int) (size / (double) expectedElements * Math.log(2));
    this.bitset = new BitSet(size);
}
```

- **m = -n·ln(P) / ln²(2)**: Optimal bit count
- **k = (m/n) · ln(2)**: Optimal hash count (~0.7 × m/n)

---

## Complexity Table

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| add | O(k) | — | k hash computations |
| mightContain | O(k) | — | k bit checks |
| | | | |

**k**: number of hash functions (typically 3-15)
**m**: bit array size (typically 8-16 bits per element)

### False Positive Probability

For n elements, m bits, k hash functions:
```
P = (1 - (1 - 1/m)^(k·n))^k ≈ (1 - e^(-k·n/m))^k
```

**Examples (n = 1M elements):**

| m (bits) | bits/element | k | FP Rate |
|----------|-------------|---|---------|
| 8 MB | 8 | 5 | 2.17% |
| 12 MB | 12 | 8 | 0.42% |
| 16 MB | 16 | 11 | 0.01% |
| 1.2 MB | 1 | 1 | ~39% |

### Space Comparison at 1% FP Rate

| Structure | Memory (1M elements) |
|-----------|---------------------|
| HashSet<String> | ~50 MB |
| Bloom Filter | ~1.2 MB |
| Cuckoo Filter | ~1.0 MB |
| XOR Filter | ~0.8 MB |

---

## Comparison with Alternatives

| Feature | Bloom Filter | Cuckoo Filter | HashSet | BitSet (exact) |
|---------|-------------|---------------|---------|----------------|
| False positives | Yes (tunable) | Yes (low) | No | No |
| Deletion | No (counting BF: yes) | Yes | Yes | Yes |
| Memory (1M, 1% FP) | ~1.2 MB | ~1.0 MB | ~50 MB | ~12.5 MB |
| Add speed | O(k) | O(1) avg | O(1) | O(1) |
| Lookup speed | O(k) | O(1) avg | O(1) | O(1) |
| Enumeration | No | No | Yes | No |
| Union | Yes (OR bits) | No | Yes | Yes |

**When NOT to use a Bloom filter:**
- Need exact membership (use HashSet)
- Need to delete elements (use Cuckoo filter or Counting Bloom)
- Very small n (<1000): HashSet overhead is negligible
- Need to enumerate set members (not possible)

---

## Use Cases

### 1. Chrome Safe Browsing
**System**: Check URLs against known malicious list on-device
**Why Bloom**: 10M URLs → ~1MB filter on mobile device
**Flow**: Bloom on device → if "maybe bad", send hash prefix to server for verification

### 2. Database Indexing (Cassandra, HBase, RocksDB)
**System**: LSM-tree based databases
**Why Bloom**: Per-SSTable Bloom filter checks if a key might exist before I/O
**Impact**: Avoids 99% of unnecessary disk reads for non-existent keys

### 3. Cache Stampede Prevention
**System**: Memcached / Redis caching layer
**Why Bloom**: Before querying DB on cache miss, check Bloom. If "not in DB", skip DB entirely
**Result**: 90% reduction in DB load during cache warmup

### 4. Web Crawler Deduplication
**System**: Search engine crawlers (Googlebot)
**Why Bloom**: Track which URLs have been visited — 1B URLs → <1GB memory
**Challenge**: Need resettable Bloom for crawl frontier

### 5. Weak Password Detection
**System**: Signup/registration systems
**Why Bloom**: Check if password is in top 1B breached passwords
**Space**: 1B passwords → ~1.5GB Bloom (vs ~50GB HashSet)

### 6. Bitcoin SPV (Simplified Payment Verification)
**System**: Lightweight Bitcoin client
**Why Bloom**: Client creates Bloom filter of interest, full node returns matching transactions
**Privacy**: Filter gives probabilistic privacy — full node can't know exact interests

### 7. DNA Sequence Membership
**System**: Bioinformatics (k-mer presence)
**Why Bloom**: Check if a k-mer exists in reference genome
**Scale**: Human genome ~3B bases → Bloom filter in ~500MB

---

## Common Pitfalls

### 1. Using Poor Hash Functions
Java's `hashCode()` is insufficient. Use MurmurHash3, FNV-1a, or SHA-256 (truncated).

### 2. Wrong Size Calculation
If m is too small, all bits become 1 and every query returns "maybe". Always calculate `m = -n·ln(P) / ln²(2)`.

### 3. Ignoring k Impact
Too few hashes → high FP rate. Too many → slow, high FP rate (too many bits set). Use optimal k.

### 4. Thread Safety
BitSet is not thread-safe. Use `AtomicIntegerArray` or synchronise.

### 5. Serialisation
BitSet supports `toByteArray()`/`valueOf()` for compact serialisation.

---

## Advanced Variants

### Counting Bloom Filter
Each position is a small counter (2-4 bits) instead of a single bit. Supports delete. Used in network flow tracking.

### Cuckoo Filter
Uses cuckoo hashing with fingerprints. Supports deletion, lower FP rate than Bloom, but not as simple. Used in DPDK/Suricata.

### XOR Filter
More compact than Bloom for static sets. Build once, query many times. ~0.23 bits per entry at 1% FP (vs ~1.44 for Bloom). Used in Rust's cargo index.

### Scalable Bloom Filter
Series of Bloom filters of increasing size. New filter added when current one fills. Used in streaming applications.

### Bloomier Filter
Associates a value (not just boolean) with each element. Used in membership + value lookup.

### Blocked Bloom Filter
Cache-friendly variant: divides bit array into cache-line-sized blocks, hash selects block then bit. 4-8x faster due to cache efficiency.

---

## Testing the Implementation

```java
BloomFilter bf = new BloomFilter(1000, 0.01);
bf.add("apple");
bf.add("banana");
bf.add("orange");

assert bf.mightContain("apple") == true;
assert bf.mightContain("grape") == false; // probably (FP possible)

// FP rate check
int fp = 0;
for (int i = 0; i < 10000; i++) {
    if (bf.mightContain("test" + i)) fp++;
}
System.out.println("FP rate: " + fp / 10000.0); // ~0.01
```

### Edge Case Tests
```java
// Empty filter
BloomFilter empty = new BloomFilter(100, 0.01);
assert empty.mightContain("anything") == false;

// Single element
BloomFilter single = new BloomFilter(100, 0.01);
single.add("only");
assert single.mightContain("only") == true;

// Duplicate add (should be idempotent)
BloomFilter dup = new BloomFilter(100, 0.01);
dup.add("dup");
dup.add("dup");
assert dup.mightContain("dup") == true;
```

---

## Key Interview Takeaways

1. **Bloom filter = space vs accuracy trade-off**. Always mention FP rate.

2. **Zero false negatives** is the most important property. Never says "not in set" when it is.

3. **Optimal parameters**: m = -n·ln(P) / ln²(2), k = (m/n)·ln(2). Memorise these formulas.

4. **No delete**: Counting Bloom solves this with extra memory.

5. **Real systems**: Chrome Safe Browsing, Cassandra, Bitcoin SPV, Redis, CDN caching.

6. **Hands-on**: Implement a Bloom filter from scratch — it's one of the easier advanced DS to code, but the parameter tuning is the hard part.