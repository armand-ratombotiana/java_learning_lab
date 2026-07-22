# Interview Questions: Collections

## Company-Specific Focus

### Google
- HashMap internals: capacity, load factor, treeification threshold, Java 8+ improvements
- ConcurrentHashMap: lock striping, CAS operations, tree bins
- Collections that are immutable vs unmodifiable wrappers

### Microsoft
- Collection type selection: when to use lists vs sets vs maps vs queues
- Sorting in collections: Comparable vs Comparator, natural vs custom ordering
- Java streams vs LINQ in C#: which is better when

### Amazon
- LRU Cache design and implementation using LinkedHashMap
- ConcurrentHashMap for high-throughput caching in multi-threaded server-side applications
- Fighting GC pauses by pooling collection objects instead of continuously creating new ones

### Meta
- HashMap performance form and re-sizing cost in HPC and low-latency applications
- CopyOnWriteArrayList as a way to do lock-free iteration at high read rates
- ArrayList to LinkedList conversion when a DP solution requires many elements to be moved

### Apple
- Immutability using List.of(), Set.of(), Map.of() and how they differ from old implementations
- Unmodifiable collection views for defensive copying and read-only exposure
- Primitive collections using Eclipse Collections or Trove to reduce boxing

### Oracle
- Collection hierarchy and interface precedence in code design
- Collection.toArray() vs new T[0] / T[0] (for generics) and their runtime differences
- Fail-fast vs fail-safe iteration: ConcurrentModificationException mechanics
- New Java 21 collection methods: collectors, groupingBy, partitioningBy

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| 1 Two Sum | Easy | Google, Apple, Amazon, Meta, Microsoft | HashMap for O(1) lookup |
| 49 Group Anagrams | Medium | Amazon, Google, Apple | HashMap with string key, list values |
| 128 Longest Consecutive Sequence | Medium | Google, Amazon, Microsoft | HashSet for O(1) membership lookup |
| 560 Subarray Sum Equals K | Medium | Apple, Facebook, Amazon | Cumulative sum + HashMap |
| 347 Top K Frequent Elements | Medium | Google, Apple, Amazon | HashMap counting + HeaP |
| 15 3Sum | Medium | Amazon, Meta, Google, Microsoft | Sorting + two-pointer (related to collection of results) |
| 242 Valid Anagram | Easy | Amazon, Apple, Google | HashMap counting char occurrences |
| 350 Intersection of Two Arrays II | Easy | Apple, Google, Amazon | HashMap for frequency tracking |
| 692 Top K Frequent Words | Medium | Google, Apple, Amazon | HashMap counting + priority queue ordering |
| 697 Degree of an Array | Easy | Amazon, Google, Microsoft | HashMap storing first, last, and count |

## Real Production Scenarios
- **AWS**: An incorrect initial capacity for HashMap in a caching layer lead to a large number of resizes causing a mini-outage during launch.
- **Uber**: An array list of an accelerated size (defaulting to 10) was constantly expanded to 100+ for a 10k-long document causing large heap fragmentation.
- **Shopify**: WeakHashMap was used for caches; putAll caused OOM because referenced objects were never reclaimable by GC.

## Interview Patterns & Tips
- **Always use the interface, not the implementation**, e.g., List not ArrayList unless needed.
- **Iterator's fail-fast returns ConcurrentModificationException**: not guaranteed, just best-effort.
- **Set.add() and Map.put() can be used as a pseudo-implementation of a counter via compute()**
  e.g., map.merge(key, 1, Integer::sum) instead of both get and put.
- **For indexing or searching by a key, use Map**, but HashSet for uniqueness and small datasets as O(1) average.
- **Stream deep equals vs. List equals for array comparisons**: they differ (deep pair does a shallow memory comparison for arrays while the default does element equality.
- **TreeMap vs TreeSet vs each of the hybrid**.

## Deep Dive Questions
- **HashMap collisions and tree bin behavior**: When does the bucket from a linked list convert to a tree in memory? (threshold => 8)
- **Thread-safety for Collections**: What happens Under the Hood when mutating the collection? Explain how the temporary copy (K in CopyOnWriteArrayList works.
- **Removing an item during iteration**: How does ConcurrentHashMap let this happen in a safe, atomic way?
- **WeakHashMap -- weak forever?**: What reference types does it use, and what about performance?
- **Clearing an old Java memory bug sun.misc.Unsafe**: How is the tail for ConcurrentLinkedQueue handled to ensure non-blocking?
