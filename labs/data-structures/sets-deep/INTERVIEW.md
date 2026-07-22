# Interview Questions: Sets (Deep Dive)

## LeetCode Problem Map
| Problem | Difficulty | Company Signal | Pattern |
|---------|-----------|----------------|---------|
| [LC 217 Contains Duplicate](https://leetcode.com/problems/contains-duplicate/) | Easy | Amazon, Google, Microsoft, Apple, Meta | HashSet lookup |
| [LC 136 Single Number](https://leetcode.com/problems/single-number/) | Easy | Google, Amazon, Microsoft, Apple | XOR set |
| [LC 349 Intersection of Two Arrays](https://leetcode.com/problems/intersection-of-two-arrays/) | Easy | Meta, Amazon, Google | HashSet intersect |
| [LC 350 Intersection of Two Arrays II](https://leetcode.com/problems/intersection-of-two-arrays-ii/) | Easy | Microsoft, Amazon | HashMap counting |
| [LC 128 Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | Medium | Google, Amazon, Meta, Microsoft | HashSet spanning |
| [LC 3 Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium | Amazon, Meta, Google, Microsoft | Sliding window + HashSet |
| [LC 36 Valid Sudoku](https://leetcode.com/problems/valid-sudoku/) | Medium | Amazon, Microsoft, Apple | HashSet rows/cols/boxes |
| [LC 202 Happy Number](https://leetcode.com/problems/happy-number/) | Easy | Amazon, Apple | HashSet cycle detection |
| [LC 705 Design HashSet](https://leetcode.com/problems/design-hashset/) | Easy | Microsoft, Amazon | Array of buckets |
| [LC 414 Third Maximum Number](https://leetcode.com/problems/third-maximum-number/) | Easy | Microsoft, Amazon | TreeSet of size 3 |
| [LC 220 Contains Duplicate III](https://leetcode.com/problems/contains-duplicate-iii/) | Hard | Google, Amazon, Meta | TreeSet sliding window |
| [LC 352 Data Stream as Disjoint Intervals](https://leetcode.com/problems/data-stream-as-disjoint-intervals/) | Hard | Google, Amazon | TreeSet of intervals |

## NeetCode Reference
NeetCode 150: Arrays & Hashing and Tree categories include set-adjacent problems. Longest Consecutive Sequence and Contains Duplicate are must-know.

## Company-Specific Questions

### Google
- Implement a probabilistic membership test (Bloom filter) with tunable false positive rate
- How does TreeSet maintain sorted order? What is the underlying data structure?
- Design a time-based versioned set (add at timestamp, query state at any point in time)
- Longest consecutive sequence in O(n) time — explain why HashSet enables this

### Microsoft
- HashSet vs TreeSet vs LinkedHashSet — when would you choose each?
- Design a set that supports add, remove, and getRandom in O(1) (HashMap + ArrayList)
- How does Java's BitSet work and when would you use it over HashSet of integers?

### Meta
- Intersection of two sorted arrays — solve with HashSet and then with two pointers (space-optimized)
- Valid Sudoku — how to validate a partially filled board using sets
- Group shifted strings — given ["abc", "bcd", "xyz"], group strings with the same shift pattern (set of normalized keys)

### Amazon
- Contains Duplicate III — sliding window with TreeSet for O(n log k) time
- Design a product deduplication system using a Bloom filter to avoid processing duplicate product listings
- How would you implement a thread-safe set with sorted iteration?

### Apple
- Implement a concurrent set using lock stripping (ConcurrentHashSet via ConcurrentHashMap)
- Deduplicate streaming events with a bounded memory set (LRU eviction + set semantics)
- How would you detect duplicate photos using perceptual hashing and a set of hashes?

### Oracle
- What is the difference between `HashSet`, `LinkedHashSet`, and `TreeSet` in terms of performance and ordering guarantees?
- How does `Collections.newSetFromMap()` work? When would you use it?
- BitSet performance — what is the memory footprint of a BitSet with 1M bits vs a HashSet of 1M integers?
- NavigableSet interface — what operations does it provide beyond Set?

## Real Production Scenarios

- **Scenario 1: Web Crawler URL Deduplication** — A web crawler must avoid revisiting URLs. A Bloom filter provides a memory-efficient first pass (200M URLs in ~200MB), but may have false positives. A secondary exact set (backed by disk or Redis) catches false positives.

- **Scenario 2: Rate Limiter** — An API rate limiter tracks unique user IDs that have accessed an endpoint in the last minute. A sliding window set uses a LinkedHashSet to maintain insertion order for efficient eviction of expired entries.

- **Scenario 3: Spell Checker** — A spell checker loads a dictionary into a HashSet for O(1) lookups. For fuzzy matching (Levenshtein distance of 1), it generates all possible variations and checks against the set. TreeSet could enable prefix-based suggestions.

## Interview Tips

- Time: O(1) average for HashSet add/remove/contains, O(log n) for TreeSet, O(1) for BitSet operations on word-sized chunks
- Space: HashSet has high per-element overhead (entry object + hash array slots); BitSet uses 1 bit per element for dense integer sets
- Common edge cases: null elements (HashSet allows one null; TreeSet does not), concurrent modification, hashCode() consistency
- HashSet performance degrades with poor hashCode() — if all elements hash to same bucket, O(n) operations
- For small sets (≤10 elements), TreeSet may be competitive with HashSet due to lower memory overhead and no resizing

## Java-Specific Considerations

- `HashSet<E>` → backed by `HashMap<E, PRESENT>`, O(1), unsynchronized, allows null
- `LinkedHashSet<E>` → extends `HashSet`, maintains doubly-linked list of insertion order, slightly more memory
- `TreeSet<E>` → backed by `TreeMap` (Red-Black tree), O(log n), sorted via `Comparable` or `Comparator`
- `EnumSet<E extends Enum<E>>` → ultra-fast, backed by bit vector or long[], all elements from same enum type
- `BitSet` → growable bit vector, `get()`, `set()`, `and()`, `or()`, `xor()`, cardinality
- `CopyOnWriteArraySet<E>` → thread-safe, backed by `CopyOnWriteArrayList`, best for small read-heavy sets
- `ConcurrentHashMap.newKeySet()` → thread-safe set view, uses `ConcurrentHashMap` under the hood
- `Collections.newSetFromMap(map)` → wraps any Map as a Set, useful for creating e.g. `ConcurrentHashSet`
- `SortedSet` / `NavigableSet` → `TreeSet` implements these with `first()`, `last()`, `headSet()`, `subSet()`, `tailSet()`, `ceiling()`, `floor()`, `higher()`, `lower()`
- Best practice: use `EnumSet` for enum members, `BitSet` for dense integer flags, `HashSet` for general purpose, `TreeSet` when sorted iteration needed
