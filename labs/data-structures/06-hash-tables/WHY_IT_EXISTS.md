# Why Hash Tables Exist

Hash tables exist to solve the problem of **fast key-based lookup** — finding a value by its key in O(1) average time.

## Problems with Other Structures

- **Arrays**: O(1) access by integer index, but cannot look up by arbitrary key
- **Linked lists**: O(n) search even with a known key
- **BSTs**: O(log n) search — good, but can we do better?
- **Sorted arrays**: O(log n) binary search — requires sorted data

## What Hash Tables Provide

| Need | Hash Table Solution |
|------|-------------------|
| O(1) average lookup | Direct index via hash function |
| Arbitrary keys | Hash function maps any object to integer |
| Insert/delete in O(1) | No shifting or rebalancing |
| Membership testing | Fast contains check |
| Caching | Key-value cache (LRU, session store) |
| Deduplication | Hash set for uniqueness testing |

## Real-World Impact

- **Databases**: hash indexes for fast equality lookups
- **Caches**: Memcached, Redis are distributed hash tables
- **Compilers**: symbol tables map variable names to types/addresses
- **Networking**: hash-based routing, load balancing
- **Cryptography**: password hashing, digital signatures
- **Java itself**: HashMap powers HashSet, LinkedHashMap, IdentityHashMap, WeakHashMap

Hash tables are the **most widely used non-trivial data structure** in software engineering.
