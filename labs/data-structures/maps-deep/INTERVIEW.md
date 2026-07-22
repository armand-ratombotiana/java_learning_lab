# Interview Questions: Maps (Deep Dive)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 1 Two Sum](https://leetcode.com/problems/two-sum/) | Easy | Amazon, Google, Microsoft, Apple | HashMap lookup |
| [LC 49 Group Anagrams](https://leetcode.com/problems/group-anagrams/) | Medium | Amazon, Meta, Microsoft | HashMap keyed by sorted string |
| [LC 128 Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | Medium | Google, Amazon, Meta | HashMap spanning |
| [LC 560 Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/) | Medium | Meta, Amazon, Google | Prefix sum + HashMap |
| [LC 242 Valid Anagram](https://leetcode.com/problems/valid-anagram/) | Easy | Amazon, Microsoft, Apple | Character count map |
| [LC 290 Word Pattern](https://leetcode.com/problems/word-pattern/) | Easy | Google, Apple | Bijection map |
| [LC 359 Logger Rate Limiter](https://leetcode.com/problems/logger-rate-limiter/) | Easy | Google, Amazon | Timestamp map |
| [LC 146 LRU Cache](https://leetcode.com/problems/lru-cache/) | Medium | Amazon, Meta, Google, Microsoft | LinkedHashMap / HashMap + DLL |
| [LC 460 LFU Cache](https://leetcode.com/problems/lfu-cache/) | Hard | Google, Amazon | HashMap of frequency buckets |
| [LC 706 Design HashMap](https://leetcode.com/problems/design-hashmap/) | Easy | Microsoft, Amazon | Array of buckets with chaining |

## NeetCode Reference
NeetCode 150: Arrays & Hashing category — 9 problems. The Two Sum variants, Group Anagrams, and Longest Consecutive Sequence are essential.

## Company-Specific Questions

### Google
- Implement a hash map from scratch (handle collisions, resizing, load factor)
- Design a distributed key-value store (consistent hashing, replication)
- HashMap vs TreeMap vs LinkedHashMap — when would you override the natural ordering?
- How does Guava's `Table` (row-key → column-key → value) differ from nested maps?

### Microsoft
- LRU Cache implementation with LinkedHashMap (access-order vs insertion-order)
- Design a time-based key-value store (versioned keys with timestamps)
- How would you implement a concurrent hash map without Java's ConcurrentHashMap?

### Meta
- Group Anagrams — optimize for large input sets
- Design a cache keyed by (user, timestamp) — nested HashMap or custom key object?
- Implement a bijection (bidirectional map)

### Amazon
- Design a product catalog index (SKU → product details, category → list of SKUs)
- How does HashMap bucket treeification work (Java 8+, TREEIFY_THRESHOLD=8)?
- What happens when hashCode() returns the same value for all keys?

### Apple
- Implement a frequency map for streaming data (fixed-size map with eviction)
- Custom hashCode() for composite keys — what makes a good hash code?
- How would you store device event logs in a time-ordered map?

### Oracle
- How does ConcurrentHashMap achieve high concurrency (segmentation, CAS, read locks)?
- What is the `HashTable` class and why is it considered legacy?
- Compute the memory overhead of a HashMap with 1M entries (entry objects, arrays, buckets)

## Real Production Scenarios

- **Scenario 1: Session Management** — A web server uses a ConcurrentHashMap of session IDs to session data. The map must handle concurrent logins, expire stale sessions, and scale to millions of sessions without OOM.

- **Scenario 2: URL Shortener** — Design a service mapping short URLs to original URLs. Must handle 100M+ entries, support TTL expiration, and be thread-safe. Trade-off between HashMap (fast lookup) and TreeMap (range-based expiration).

- **Scenario 3: Stock Order Book** — A trading engine uses TreeMap for price levels (sorted by price), with each level containing a HashMap of order IDs to orders. Combining both maps enables both price-range queries and O(1) order lookup.

## Interview Tips

- Time: O(1) average for HashMap get/put, O(log n) for TreeMap, O(n) worst case for HashMap with bad hash distribution
- Space: HashMap has significant overhead per entry (32-48 bytes in HotSpot); TreeMap adds tree node overhead
- Common edge cases: null keys/values (HashMap allows one null key; TreeMap does not allow null keys), concurrent modification, hash collision storms
- Always override equals() AND hashCode() together; use Objects.hash() for composite keys

## Java-Specific Considerations

- `HashMap` → unsorted, O(1), allows null key, uses Node<K,V>[] table, treeified at threshold
- `LinkedHashMap` → insertion-order or access-order, slightly more memory, predictable iteration
- `TreeMap` → sorted via Comparable/Comparator, O(log n), Red-Black tree backed, `NavigableMap` interface
- `ConcurrentHashMap` → lock-free reads, CAS-based writes, `computeIfAbsent`, `merge`, `forEach` for atomic compound ops
- `Hashtable` → legacy, synchronized (contended single lock), no null keys/values
- `EnumMap` → ultra-fast for enum keys, uses ordinal-indexed array
- `IdentityHashMap` → reference equality, not `equals()`, used in serialization/proxies
- The `Map.of()` (Java 9+) creates immutable singleton/small maps with zero overhead
