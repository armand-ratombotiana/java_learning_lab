# Exercises: Hash Tables

## Basic

1. **Separate chaining** — Implement a hash table with separate chaining (linked lists). Include put, get, remove, size.

2. **Linear probing** — Implement a hash table with linear probing. Include put, get, remove, and resize.

3. **Custom hashCode** — Write a proper `hashCode()` for a `Point` class (x, y coordinates) and a `Student` class (id, name, grade).

4. **First non-repeating character** — Find the first character in a string that appears only once (use HashMap or array index).

## Intermediate

5. **Group anagrams** — Group strings into anagrams. Input: `["eat","tea","tan","ate","nat","bat"]` → `[["eat","tea","ate"],["tan","nat"],["bat"]]`.

6. **Subarray sum equals k** — Count subarrays whose sum equals k (use HashMap of prefix sums).

7. **LRU Cache** — Design an LRU cache with O(1) get and put (LinkedHashMap or HashMap + doubly linked list).

8. **Top K frequent elements** — Find the k most frequent elements (HashMap + bucket sort or heap).

9. **Longest consecutive sequence** — Find the longest consecutive element sequence in O(n) time.

## Advanced

10. **Design a HashMap** — Implement `MyHashMap` with put, get, remove using array of buckets and load factor resizing.

11. **Consistent hashing** — Implement a consistent hash ring with virtual nodes for even distribution.

12. **Distributed cache** — Design a simple distributed cache using consistent hashing across multiple nodes.

13. **Counting Bloom filter** — Combine hash table with counters to support membership and approximate frequency.
