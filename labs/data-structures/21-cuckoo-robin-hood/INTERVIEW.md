# Interview Questions: Cuckoo & Robin Hood Hashing

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 706 Design HashMap](https://leetcode.com/problems/design-hashmap/) | Easy | Microsoft, Amazon, Apple, Google | Array of buckets with chaining |
| [LC 705 Design HashSet](https://leetcode.com/problems/design-hashset/) | Easy | Microsoft, Amazon, Google | Hash set implementation |
| (System design focus) | — | Google, Amazon, Meta, Microsoft | Advanced hashing / cache-friendly |

## NeetCode Reference
Not in NeetCode 150. Advanced hashing techniques appear in system design for cache-efficient and space-efficient systems.

## Company-Specific Questions

### Google
- Explain how Cuckoo Hashing works — what happens during a cycle and how is it resolved?
- What is the expected insertion time for Cuckoo Hashing? How does the load factor threshold (~50%) compare to standard hash tables?
- Implement Cuckoo Hashing with two hash tables — handle cycles with rehashing to new hash functions
- Compare Cuckoo Hashing vs Robin Hood Hashing vs linear probing — which is more cache-friendly?

### Microsoft
- Explain Robin Hood Hashing — how does "stealing from the rich" (moving elements closer to their ideal position) improve variance?
- How does backward shift deletion work in Robin Hood Hashing?
- What is the empirical performance of Robin Hood Hashing vs Swiss Table (Google's Abseil flat_hash_map)?

### Meta
- Design a hash table optimized for read-heavy workloads (Cuckoo Hashing with 2-3 hash functions)
- How would you detect cycles in Cuckoo Hashing and recover?
- Compare open addressing (Cuckoo/Robin Hood) vs separate chaining — memory layout and cache miss rates

### Amazon
- Design a product inventory hash table that maximizes cache efficiency for lookups
- How would you implement a hash table for DynamoDB's internal key-value store?
- Compare Robin Hood Hashing vs Go's map implementation (Swiss Table / Ristretto)

### Apple
- How does the Swift standard library implement its Dictionary (open addressing with linear probing)?
- Design a hash table for a mobile device with limited memory (tight load factor, cache-line awareness)
- How would you tune hash table parameters for a real-time audio application (no allocation on insert)?

### Oracle
- What is the load factor trade-off for Cuckoo Hashing? Why is it typically limited to ~50%?
- Explain how Robin Hood Hashing reduces the variance of probe lengths — how does this improve worst-case performance?
- What is the Swiss Table (Google's Abseil) and how does it use SIMD for parallel bucket probing?
- Compare the memory overhead of open addressing (Cuckoo/Robin Hood) with Java's HashMap (chaining)

## Real Production Scenarios

- **Scenario 1: High-Performance Cache** — A database caching layer uses Cuckoo Hashing for its in-memory hash table. The cache demands predictable O(1) lookups with minimal cache misses. Cuckoo Hashing provides O(1) worst-case lookup (only 2 bucket checks) with excellent cache locality. The trade-off is lower load factor (~50%) and more complex insertion.

- **Scenario 2: Compiler Symbol Table** — A JIT compiler uses Robin Hood Hashing for its symbol table. The hash table is built once (during compilation) and queried millions of times during execution. Robin Hood Hashing's low probe-length variance ensures fast lookups with no pathological slowdowns.

- **Scenario 3: Network Packet Classifier** — A software-defined network switch uses a hash table (Cuckoo Hashing) to match packet headers against forwarding rules. The deterministic O(1) lookup is critical for line-rate processing. The low load factor trades memory for guaranteed performance.

## Interview Tips

- Time: O(1) worst-case lookup (Cuckoo — constant # of probes), O(1) average for Robin Hood, O(1) amortized insertion
- Space: load factor ~50% for Cuckoo (half the table may be empty), ~90%+ for Robin Hood with good hash functions
- Common edge cases: insertion cycle (Cuckoo — requires rehash with new hash functions), hash collision clustering (Robin Hood — move offending element)
- Cuckoo Hashing uses 2+ hash functions; when both positions for an element are occupied, evict the occupant and reinsert it
- Robin Hood Hashing: when probing, if the current element is farther from its ideal position than the inserting element, swap them
- Both are open addressing schemes — no linked list overhead, better cache locality than chaining

## Java-Specific Considerations

- No standard Cuckoo Hashing or Robin Hood Hashing class in Java — implement from scratch
- Cuckoo: `class CuckooMap<K,V> { Entry<K,V>[] t1, t2; HashFunction h1, h2; int cap; }` — two tables with separate hash functions
- Robin Hood: `class RobinHoodMap<K,V> { Entry<K,V>[] table; int cap; }` — single table with linear probing + displacement tracking
- `Objects.hash()` for initial hash; derive h1 and h2 via split: `h1 = hash ^ salt1`, `h2 = hash ^ salt2`
- For Cuckoo cycle detection: count attempts; if exceeds threshold (e.g., log n), rehash with new hash functions
- For Robin Hood DIB (Distance from Initial Bucket): store DIB in the entry or probe separately
- `ThreadLocalRandom` for generating new hash function salts during rehash
- `sun.misc.Unsafe` or `VarHandle` for atomic operations if thread-safety is needed
- Memory: open addressing stores entries directly in arrays (no node objects) — less GC pressure than chaining
- Performance: linear probing + Robin Hood is often fastest in practice due to sequential memory access patterns
