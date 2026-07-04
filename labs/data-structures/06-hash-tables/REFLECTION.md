# Reflection: Hash Tables

## What I Learned

- Hash tables provide O(1) average lookup by distributing keys across buckets via a hash function
- Good hash functions are deterministic, uniform, and fast — Java's `hashCode()` contract is critical
- Collision resolution (chaining vs open addressing) involves memory vs performance trade-offs
- Load factor and resizing determine space-time trade-offs
- Java's HashMap evolved significantly: linked chains → treeified chains for HashDoS protection
- Mutable keys break hash tables — always use immutable keys

## Questions to Consider

1. Why does Java use 0.75 as the default load factor? (Pareto-optimal trade-off)
2. When would open addressing be preferable to separate chaining? (Cache efficiency, no pointer overhead)
3. How does ConcurrentHashMap achieve thread safety without global locking?
4. What makes a good hash function for strings? (Polynomial rolling hash with odd prime)
5. How does consistent hashing differ from regular hashing in distributed systems?

## Connections

Hash tables connect to:
- **Arrays** (bucket array is the backing structure)
- **Linked lists** (chaining uses linked lists; Java 8+ trees for long chains)
- **Trees** (Java 8 HashMap treeification for collision chains)
- **Bloom filters** (use hash functions but with bit arrays)
- **Caches** (HashMap is the foundation of most caches)
- **Databases** (hash indexes for equality lookups)

## Self-Assessment

- [ ] Can implement hash table with separate chaining from scratch
- [ ] Can implement hash table with linear probing from scratch
- [ ] Can write a proper hashCode() for any class
- [ ] Understand load factor and resizing mechanics
- [ ] Understand Java HashMap internals (hash spreading, treeify, resize)
- [ ] Know when to use HashMap vs TreeMap vs LinkedHashMap
