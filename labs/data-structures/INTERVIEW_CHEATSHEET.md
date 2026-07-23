# Interview Cheatsheet — Data Structures Academy

## The 5-Step Method
1. **Understand** — Restate, ask clarifying Qs, walk through examples
2. **Brute Force** — State naive approach, analyze complexity
3. **Optimize** — HashMap? Two-pointer? Sliding window? Binary search?
4. **Implement** — Clean code, edge cases first, talk through it
5. **Test** — Walk through examples, edge cases, complexity analysis

## Big O Reference
| DS | Access | Search | Insert | Delete | Space |
|----|--------|--------|--------|--------|-------|
| Array | O(1) | O(n) | O(n) | O(n) | O(n) |
| Stack | O(n) | O(n) | O(1) | O(1) | O(n) |
| Queue | O(n) | O(n) | O(1) | O(1) | O(n) |
| Singly LL | O(n) | O(n) | O(1) | O(1) | O(n) |
| Doubly LL | O(n) | O(n) | O(1) | O(1) | O(n) |
| Hash Table | N/A | O(1) avg | O(1) avg | O(1) avg | O(n) |
| BST | O(log n) | O(log n) | O(log n) | O(log n) | O(n) |
| Heap | O(1) min | O(n) | O(log n) | O(log n) | O(n) |
| Trie | O(k) | O(k) | O(k) | O(k) | O(n*k) |

## Top Patterns
| Pattern | When | Key Problems |
|---------|------|-------------|
| HashMap | Fast lookup, frequency, complements | LC 1, 49, 128, 560 |
| Two-Pointer | Sorted arrays, in-place | LC 11, 15, 42, 167 |
| Sliding Window | Subarrays/substrings, contiguous | LC 3, 76, 239, 424 |
| Fast & Slow | Cycle detection, middle | LC 141, 143, 234 |
| BFS | Shortest path, level order | LC 102, 127, 200, 286 |
| DFS | Path existence, connected components | LC 98, 124, 200, 207 |
| Recursion | Trees, divide & conquer | LC 104, 124, 236, 297 |

## Must-Know DS Implementations
- **HashMap**: Array of buckets, hash function, separate chaining, load factor, rehashing
- **LRU Cache**: HashMap<Key, Node> + Doubly Linked List — O(1) get/put
- **Trie**: Node with children[26] + isEnd flag — insert/search/prefixSearch
- **Union-Find**: parent[] + rank[] — find (path compression) + union (by rank) — O(alpha(n))
- **Segment Tree**: Build, query, point/range update — O(log n)
- **Fenwick Tree**: Binary indexed tree — prefix sum + point update — O(log n)
- **Heap**: Binary tree in array — siftUp/siftDown — O(log n) push/pop

## Edge Cases Checklist
- [ ] Empty/null input
- [ ] Single element
- [ ] Two elements
- [ ] All same values
- [ ] Already sorted
- [ ] Reverse sorted
- [ ] Negative numbers
- [ ] Mixed positive/negative
- [ ] Integer overflow
- [ ] Duplicates

## Complexity Analysis Phrases
- "We must examine each element, so Omega(n) is the lower bound"
- "Sorting is required, so O(n log n) is optimal"
- "We trade space for time with a HashMap"
- "The bottleneck is the sorting step"

## Company Focus
| Company | Top DS | Key Behavior |
|---------|--------|-------------|
| Google | Trees, Graphs, Hash Maps | Complexity analysis, optimization path |
| Meta | Arrays, Hash Maps, Recursion | Speed, 2 problems / 45 min |
| Amazon | LRU, Bloom Filters, Concurrent DS | Scalability, Leadership Principles |
| Microsoft | Linked Lists, Trees, Stacks | Production code, null-safe |
| Apple | Bit Arrays, Circular Buffers, Ropes | Memory efficiency, in-place |

## STAR for Behavioral
- **S**ituation: Context (project, team, timeline)
- **T**ask: Your responsibility
- **A**ction: What you actually did
- **R**esult: Quantified outcome

## Time Management (45 min)
| 0-5 min | 5-10 min | 10-30 min | 30-40 min | 40-45 min |
|---------|----------|-----------|-----------|-----------|
| Understand | Approach design | Code | Test | Complexity + Follow-up |

## Quick Ref — Common LC
| LC # | Problem | Pattern | Complexity |
|------|---------|---------|------------|
| 1 | Two Sum | HashMap | O(n) / O(n) |
| 20 | Valid Parentheses | Stack | O(n) / O(n) |
| 53 | Maximum Subarray | Kadane | O(n) / O(1) |
| 121 | Buy/Sell Stock | Single pass | O(n) / O(1) |
| 146 | LRU Cache | HM + DLL | O(1) / O(n) |
| 200 | Number of Islands | BFS/DFS/UF | O(M*N) / O(M*N) |
| 206 | Reverse Linked List | Iterative | O(n) / O(1) |
| 208 | Implement Trie | Trie | O(k) per op |
| 215 | Kth Largest | QuickSelect/Heap | O(n) avg |
| 238 | Product Except Self | Prefix/Suffix | O(n) / O(1) |
