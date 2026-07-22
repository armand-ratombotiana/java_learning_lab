# Interview Questions: HashMap Internals

## Company-Specific Focus

### Google
- HashMap internal structure: array of Node<K,V> bins, treeification threshold (8 -> 64)
- Hash function: how key.hashCode() is transformed into bucket index (hash ^ (hash >>> 16))
- Capacity and load factor: why default capacity is 16, load factor 0.75

### Microsoft
- HashMap vs Dictionary<K,V> in C#: design differences in collision handling
- Tree bins: why Java 8 converted linked lists to trees when collisions exceed threshold 8

### Amazon
- HashMap performance in high-scale caches: pre-sized to avoid resizing
- Resize operation: doubles capacity, rehashes all entries — O(n) cost
- Initial capacity estimation: capacity = expected size / load factor + 1

### Meta
- hashCode contract: equals consistency, distribution quality
- Mutable keys: changing hash code while in map causes lookup failures
- Custom key classes: overriding equals/hashCode properly

### Apple
- HashMap vs IdentityHashMap: reference equality vs object equality
- WeakHashMap: keys with weak references for caching
- LinkedHashMap: insertion order vs access order iteration

### Oracle
- HashMap spec: JCF documentation for contract and concurrency guarantees
- Hash table in Java 7 vs 8: linked list migration to tree bins
- Internal modCount for fail-fast iteration

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1 Two Sum | Easy | Google, Amazon, Meta | HashMap for O(1) lookups |
| 49 Group Anagrams | Medium | Amazon, Google, Apple | HashMap with sorted char key |
| 128 Longest Consecutive Sequence | Medium | Google, Amazon | HashSet for O(1) membership |
| 560 Subarray Sum Equals K | Medium | Amazon, Facebook | HashMap of prefix sums |
| 380 Insert Delete GetRandom O(1) | Medium | Google, Amazon, Apple | HashMap + ArrayList combination |
| 146 LRU Cache | Medium | Google, Amazon, Apple | LinkedHashMap access order |

## Real Production Scenarios
- **Amazon**: HashMap with initial capacity too low caused 30 resizes during warm-up — 500ms added to startup
- **LinkedIn**: Mutable key in HashMap — employee object's hashCode changed after promotion (getNewSalary), making it unfindable
- **Uber**: HashMap<Integer, X> with int autoboxing — cached Integer values (-128 to 127) vs heap allocated caused reference equality bugs

## Interview Patterns & Tips
- **Always override hashCode when overriding equals**: contract guarantees
- **HashMap resize is expensive**: estimate initial capacity for known data sizes
- **mutable keys break maps**: use immutable keys (records, String, Integer)
- **null key**: HashMap allows one null key; stored in table[0]

## Deep Dive Questions
- **Treeify threshold**: Why is treeification threshold 8 and de-treeification threshold 6?
- **Hash distribution**: How does HashMap spread hash bits evenly?
- **Capacity alignment**: Why is capacity always a power of 2? (bit masking instead of modulo)
- **Resize**: How does resize transfer entries from old table to new table?
- **ConcurrentModification**: How does the iterator detect concurrent modification?