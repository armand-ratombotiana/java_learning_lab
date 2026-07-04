# Interview Questions: Hash Tables

## Easy

1. **Two Sum** — Return indices of two numbers that sum to target (HashMap).

2. **Contains Duplicate** — Check if any value appears at least twice (HashSet).

3. **Valid Anagram** — Check if two strings are anagrams (HashMap character count).

4. **First Unique Character** — Find first non-repeating character (HashMap of counts).

## Medium

5. **Group Anagrams** — Group anagrams together (HashMap of sorted strings → list).

6. **Top K Frequent Elements** — Return k most frequent elements (HashMap + bucket sort).

7. **Longest Consecutive Sequence** — O(n) time, find longest consecutive sequence.

8. **Subarray Sum Equals K** — Count subarrays summing to k (prefix sum HashMap).

9. **LRU Cache** — Design a least-recently-used cache (LinkedHashMap or HashMap + doubly linked list).

10. **Encode and Decode TinyURL** — Design a URL shortening service (two HashMaps).

## Hard

11. **Design HashMap** — Implement your own hash map with separate chaining, load factor, and resize.

12. **Minimum Window Substring** — Find smallest window containing all chars of target string (HashMap + sliding window).

13. **Max Points on a Line** — Given n points, find max points on a line (HashMap of slope → count).

14. **Insert Delete GetRandom O(1)** — Design a data structure supporting insert, delete, getRandom in O(1) (HashMap + ArrayList).

## Key Patterns

- **HashMap for lookup**: cache computed values, store indices for quick retrieval
- **HashMap for counting**: frequency maps are the most common pattern
- **Prefix sum + HashMap**: subarray sum problems
- **Sliding window + HashMap**: substring problems
- **HashMap + doubly linked list**: LRU cache

## Java-Specific Topics

- Difference between HashMap, LinkedHashMap, TreeMap, ConcurrentHashMap
- hashCode() and equals() contract — must override both
- HashMap vs HashSet (HashSet is wrapper around HashMap)
- HashMap capacity and load factor tuning
