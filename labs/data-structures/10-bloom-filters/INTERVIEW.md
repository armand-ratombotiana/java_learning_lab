# Interview Questions: Bloom Filters

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| (No standard LeetCode problems — implement from scratch) | — | Google, Meta, Amazon, Microsoft, Oracle | System design / implementation |

## NeetCode Reference
Bloom filters are not in LeetCode but are essential for system design interviews (Design YouTube, Design Web Crawler, Design URL Shortener).

## Company-Specific Questions

### Google
- Design a web crawler — how would you avoid revisiting billions of URLs when the set doesn't fit in memory?
- How does Bigtable use Bloom filters to reduce disk reads? Explain the trade-off (filter size vs false positive rate vs saved I/O)
- Design a system to detect new URLs for indexing — millions of URLs/day against a set of 100B already-indexed URLs
- Explain how Chrome uses Bloom filters to detect malicious URLs

### Microsoft
- Design a spell checker for 1M+ words — how would a Bloom filter help with memory-constrained (mobile) devices?
- How would you use Bloom filters in a distributed cache to avoid cache stampede?
- Compare Bloom filter vs HashSet for membership testing — memory, time, and accuracy trade-offs

### Meta
- Design a social graph deduplication service — how to check if a user ID has been processed before?
- How would you detect duplicate photo uploads using perceptual hashing + Bloom filter?
- Design Instagram's feed deduplication — avoid showing the same post twice

### Amazon
- Design DynamoDB's distributed system — how do Bloom filters reduce disk reads in SSTables?
- How would you implement a Bloom filter for cache key membership in front of a database?
- Design a recommendation system that doesn't recommend already-purchased items (Bloom filter of purchased product IDs)

### Apple
- How would you use a Bloom filter in iCloud to efficiently check file existence across devices?
- Design a contact deduplication system when importing contacts from multiple sources
- How does APNs (Apple Push Notification service) use probabilistic structures for device token management?

### Oracle
- How could Bloom filters be used in Oracle's database for anti-join optimization?
- What is a Counting Bloom filter and how does it support deletion?
- How does a Scalable Bloom filter handle unknown element counts?
- Compare Bloom filter memory vs HashSet for 100M elements at 1% false positive rate

## Real Production Scenarios

- **Scenario 1: Web Crawler URL Deduplication** — A search engine crawler processes 1B+ URLs. A Bloom filter with 200MB of memory (~32 bits per element) provides a compact membership test. URLs that pass the Bloom filter are checked against an on-disk database for exact deduplication. False positives waste some I/O but are much cheaper than storing the full URL set in memory.

- **Scenario 2: Cache Miss Optimization** — A database-backed service uses a Bloom filter in front of the cache to check if a key exists. If the Bloom filter says "no", the service avoids both cache lookup and database query. For key-range queries (e.g., "user:123:*"), a counting Bloom filter tracks set members.

- **Scenario 3: Bitcoin Simplified Payment Verification (SPV)** — A lightweight Bitcoin wallet uses Bloom filters to request relevant transactions from full nodes. The wallet creates a Bloom filter of its addresses and sends it to peers. Peers return matching transactions. The approach preserves privacy (partial address disclosure) while reducing bandwidth.

## Interview Tips

- Time: O(k) for add/check (k = number of hash functions, typically 3-15)
- Space: ~1.44·log₂(1/P) bits per element for false positive rate P. For 1% FPR: ~9.6 bits/element
- Common edge cases: empty filter, adding the same element multiple times, filter with no elements
- Key formulas: m = -n·ln(P) / (ln(2))² bits, k = (m/n)·ln(2) hash functions
- Bloom filters have NO false negatives — if it says "no", the element was definitely not added
- Trade-off: lower P requires more bits per element and more hash functions

## Java-Specific Considerations

- `com.google.common.hash.BloomFilter<T>` (Guava) — `create(Funnel<T>, expectedInsertions, falseProbability)`
- Guava's `BloomFilter` is serializable and supports `put()` and `mightContain()`
- `Funnels` — `Funnels.stringFunnel(Charset)`, `Funnels.integerFunnel()`, `Funnels.byteArrayFunnel()`
- Custom `Funnel` for user objects — override `funnel(T, PrimitiveSink)` to map fields to bytes
- `java.util.BitSet` — used for Bloom filter bit array, `set()` and `get()` are O(1)
- `BloomFilter.copy()` creates a copy; `BloomFilter.expectedFpp()` returns estimated false positive rate
- From scratch: use `BitSet` or `long[]` for bit array, `MessageDigest` (MD5/SHA-256) for hashing, split hash into k sub-hashes via double hashing
- Thread safety: Guava's `BloomFilter` is not thread-safe — use `synchronized` or `ReentrantReadWriteLock`
- For distributed use: serialize bit array + hashCount; merge via bitwise OR (same params required)
