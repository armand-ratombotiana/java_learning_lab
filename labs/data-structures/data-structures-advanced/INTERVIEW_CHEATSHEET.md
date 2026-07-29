# Interview Cheatsheet: Advanced Data Structures

## Quick Complexity Reference

### 01 Trie (Prefix Tree)
| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(L) | O(L·A) |
| Search | O(L) | O(1) |
| Delete | O(L) | O(1) |
| Prefix match | O(L) | O(1) |
| Auto-complete | O(L + R) | O(R) |
| **Key property**: prefix sharing saves space over HashSet of strings | | |
| **Compressed (radix tree)**: O(L) time, O(N) space for N nodes | | |

### 02 Bloom Filter
| Operation | Time | Space |
|-----------|------|-------|
| Add | O(k) | — |
| Contains | O(k) | — |
| **k**: number of hash functions, **m**: bit array size | | |
| False positive rate: (1 - e^(-kn/m))^k | | |
| Optimal k = (m/n) * ln(2) | | |
| **Cannot delete** (without counting variant) | | |

### 03 Suffix Array
| Operation | Time | Space |
|-----------|------|-------|
| Build (sort) | O(n log n) | O(n) |
| Build (SA-IS) | O(n) | O(n) |
| Pattern search | O(m log n) | O(1) |
| LCP array (Kasai) | O(n) | O(n) |
| Longest repeated substr | O(n) | O(n) |
| **Key**: suffix array + LCP = suffix tree alternative | | |

### 04 Fenwick Tree (Binary Indexed Tree)
| Operation | Time | Space |
|-----------|------|-------|
| Prefix sum | O(log n) | O(1) |
| Point update | O(log n) | O(1) |
| Range sum | O(log n) | O(1) |
| Range update + point query | O(log n) | O(1) |
| **Cannot do**: range min/max query | | |
| **2D BIT**: O(log² n) per operation, O(n²) space | | |

### 05 Segment Tree
| Operation | Time | Space |
|-----------|------|-------|
| Build | O(n) | O(4n) |
| Range query | O(log n) | O(1) |
| Point update | O(log n) | O(1) |
| Range update (lazy) | O(log n) | O(1) |
| **Lazy propagation**: O(4n) extra space for lazy array | | |
| **Iterative**: O(2n) space, no recursion | | |

### 06 Skip List
| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(log n) avg | O(log n) expected |
| Search | O(log n) avg | O(1) |
| Delete | O(log n) avg | O(1) |
| Range scan | O(log n + k) | O(1) |
| **Worst case**: O(n) with poor RNG | | |
| **Concurrent**: lock-free variants exist | | |

### 07 Red-Black Tree
| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(log n) | O(1) |
| Search | O(log n) | O(1) |
| Delete | O(log n) | O(1) |
| **Properties**: root = black, red has black children, same black height | | |
| **Height**: ≤ 2 log₂(n+1) guaranteed | | |
| **Rotation**: O(1) per insert/delete (max 2 rotations) | | |

### 08 Treap (Randomised BST)
| Operation | Time | Space |
|-----------|------|-------|
| Insert | O(log n) avg | O(1) |
| Search | O(log n) avg | O(1) |
| Delete | O(log n) avg | O(1) |
| Split/merge | O(log n) avg | O(1) |
| **Key property**: BST key + heap priority = Cartesian tree | | |
| **Implicit treap**: array-like operations by subtree size | | |

### 09 Union-Find (Disjoint Set Union)
| Operation | Time | Space |
|-----------|------|-------|
| Find | O(α(n)) | O(1) |
| Union | O(α(n)) | O(1) |
| Connected | O(α(n)) | O(1) |
| **α(n)** = inverse Ackermann (< 5 for all practical n) | | |
| **Path compression**: flattens tree on find | | |
| **Union by rank**: attaches smaller to larger tree | | |

### 10 Merkle Tree (Hash Tree)
| Operation | Time | Space |
|-----------|------|-------|
| Build | O(n) | O(n) |
| Root hash | O(1) | O(1) |
| Proof generation | O(log n) | O(log n) |
| Verification | O(log n) | O(1) |
| **Hash function**: SHA-256 (or any collision-resistant) | | |
| **Consistency proof**: O(log n) for append-only logs | | |

## When to Use What

| Scenario | Best Structure |
|----------|---------------|
| Prefix search / autocomplete | Trie |
| Membership test with memory constraint | Bloom Filter |
| Substring search / repeated patterns | Suffix Array |
| Point updates, prefix/range sums | Fenwick Tree |
| Range queries with updates (min/sum/GCD) | Segment Tree |
| Concurrent sorted set | Skip List |
| Always-balanced BST with range ops | Red-Black Tree |
| Order statistics + dynamic array | Treap (implicit) |
| Dynamic connectivity, graph components | Union-Find |
| Tamper-proof data verification | Merkle Tree |

## Space Trade-offs at Scale (1M elements)

| Structure | Approx Space | Use Case |
|-----------|-------------|----------|
| HashSet<String> | ~50 MB | Baseline |
| Trie | ~15 MB | Prefix queries |
| Bloom Filter (1% FP) | ~1.2 MB | Membership |
| Suffix Array | ~8 MB | String search |
| Fenwick Tree | ~8 MB | Range sums |
| Segment Tree | ~32 MB | Range queries |
| Skip List | ~40 MB | Concurrent set |
| Red-Black Tree | ~48 MB | Ordered map |
| Treap | ~48 MB | Order stats |
| Union-Find | ~16 MB | Connectivity |
| Merkle Tree | ~128 MB | Integrity |

## Common Pitfalls

1. **Trie**: forgetting `isEndOfWord` flag
2. **Bloom Filter**: using poor hash functions (use MurmurHash/FNV)
3. **Suffix Array**: 0-index vs 1-index confusion in LCP
4. **Fenwick Tree**: index must start at 1
5. **Segment Tree**: array size off-by-one (need 4n)
6. **Skip List**: missing update of previous pointers during insert
7. **Red-Black Tree**: uncle colour check during insert fixup
8. **Treap**: priority collisions (use tuple: priority, tie-breaker)
9. **Union-Find**: forgetting path compression in find
10. **Merkle Tree**: empty data, odd leaf count, hash collisions

## Java-Specific Notes

- `TreeMap<K,V>` = Red-Black Tree implementation
- `ConcurrentSkipListMap<K,V>` = Skip List (CAS-based)
- `BitSet` for Bloom Filter bit array
- Use `Arrays.binarySearch` for suffix array search
- Use `LongAdder` for concurrent counters in Fenwick
- `MessageDigest.getInstance("SHA-256")` for Merkle hashing
- `PriorityQueue` for heap operations in Treap context
- `HashMap<K,V>` for union-find with arbitrary keys (not just ints)

## Interview Script Template

> Let's think about this problem. We need [operation] with [constraint].
> A naive approach would be [brute force] at O([complexity]).
> We can improve using [DS name] because it supports [key property].
> The [DS name] gives us O([complexity]) for [operation] and O([complexity]) for [operation].
> Let me explain the algorithm: [brief steps].
> Edge cases: empty input, [specific edge case].
> Space complexity is O([space]). Trade-off is [trade-off].
> A variant could use [alternative] if [different constraint].