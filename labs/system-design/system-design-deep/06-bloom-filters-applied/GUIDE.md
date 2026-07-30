# Implementation Guide: Bloom Filters

## 1. Standard Bloom Filter

### Concept
A bit array of size `m` with `k` hash functions. To add an element, hash it with all `k` functions and set the corresponding bits. To check membership, verify all `k` bits are set.

### False Positive Rate
```java
double fpp = Math.pow(1 - Math.exp(-k * n / m), k);
// Optimal k = (m / n) * ln(2)
```

### Implementation
```java
public class BloomFilter<T> {
    private final BitSet bits;
    private final int k; // hash functions
    private final int m; // bit array size

    public void add(T element) {
        int[] hashes = hash(element, k);
        for (int h : hashes) bits.set(h % m);
    }

    public boolean mightContain(T element) {
        int[] hashes = hash(element, k);
        for (int h : hashes) {
            if (!bits.get(h % m)) return false;
        }
        return true;
    }
}
```

## 2. Counting Bloom Filter

### Concept
Replace each bit with a counter (typically 4 bits). Increment on add, decrement on remove. Enables deletion.

### Overflow Handling
Counters can overflow. Mitigations:
- Use 4-bit counters (max 15)
- Cascade to overflow table
- Periodically scrub stale entries

## 3. Scalable Bloom Filter

### Concept
When a Bloom filter reaches capacity, create a new one with larger size. Check all filters for membership. New elements always added to the newest filter.

### false positive bounding
Each filter has a tightening false positive rate:
```java
fpp[i] = fpp_0 * r^i  where r < 1
```
Total false positive rate is bounded by `fpp_0 / (1 - r)`.

## 4. Cuckoo Filter

### Concept
Each element has two candidate buckets (like cuckoo hashing). If both are full, kick out an existing element and reinsert it (cuckooing).

### Benefits over Bloom
- Lower false positive rate for same space
- Supports deletion naturally
- Better cache performance (smaller footprint)

### Limitations
- Insertion can fail (relocation limit)
- Not as simple as Bloom filter
- Maximum load factor ~95%

## 5. System Design Applications

### Cache Optimization (CDN)
Before checking the cache, check a Bloom filter for key existence. If the filter says "no", skip the cache lookup entirely. Reduces cache miss overhead by 30-50%.

### Deduplication (Web Crawler)
Maintain a Bloom filter of visited URLs. Before crawling, check the filter. False positives mean some URLs are skipped (acceptable for crawling).

### Malicious URL Detection
Browser checks URLs against a local Bloom filter of known malicious URLs. False positives trigger a full remote check.
