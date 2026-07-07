# Data Structures Academy — Complete Learning Path

<div align="center">

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-success?style=for-the-badge)
![Labs](https://img.shields.io/badge/Labs-30-blue?style=for-the-badge)
![Level](https://img.shields.io/badge/Level-Beginner_to_Expert-brightgreen?style=for-the-badge)

**Master 30 essential data structures — from arrays to cache-oblivious data structures — all examples in Java**

</div>

---

## Overview

The Data Structures Academy provides a comprehensive, hands-on curriculum covering essential data structures used in software engineering. Each lab includes theoretical foundations, Java implementations, complexity analysis, and practical exercises. This academy fills a gap not extensively covered by existing numbered modules, providing fresh lab directories with full implementations.

---

## Curriculum Map

### Level 1: Foundational Data Structures
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 01 | [Arrays](./01-arrays/) | Static arrays, dynamic arrays (ArrayList), multi-dimensional, operations | 2-3 hrs | Beginner |
| 02 | [Linked Lists](./02-linked-lists/) | Singly, doubly, circular linked lists, sentinel nodes | 3-4 hrs | Beginner |
| 03 | [Stacks & Queues](./03-stacks-queues/) | Stack, queue, deque, priority queue, circular buffer | 3-4 hrs | Beginner |

### Level 2: Trees & Graphs
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 04 | [Trees](./04-trees/) | Binary tree, tree traversals, N-ary trees, tree operations | 4-5 hrs | Intermediate |
| 05 | [Graphs](./05-graphs/) | Adjacency matrix/list, BFS, DFS, topological sort | 5-6 hrs | Intermediate |
| 06 | [Hash Tables](./06-hash-tables/) | Hash functions, collision resolution, load factor, rehashing | 3-4 hrs | Intermediate |

### Level 3: Advanced Data Structures
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 07 | [Heaps](./07-heaps/) | Min-heap, max-heap, heapify, heap sort, priority queue internals | 3-4 hrs | Intermediate |
| 08 | [Tries](./08-tries/) | Prefix tree, autocomplete, spell checker, compressed trie | 3-4 hrs | Intermediate |
| 09 | [Advanced Trees](./09-advanced-trees/) | BST, AVL trees, Red-Black trees, B-trees, self-balancing | 5-6 hrs | Advanced |
| 10 | [Bloom Filters](./10-bloom-filters/) | Probabilistic data structure, false positives, hash functions | 2-3 hrs | Advanced |

### Level 4: Expert Data Structures
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 11 | [Union-Find](./11-union-find/) | Disjoint Set Union, path compression, union by rank | 2-3 hrs | Advanced |
| 12 | [Segment Trees](./12-segment-trees/) | Range queries, point updates, lazy propagation, iterative segment tree | 4-5 hrs | Advanced |
| 13 | [Fenwick Tree](./13-fenwick-tree/) | Binary Indexed Tree, prefix sums, range updates, 2D BIT | 3-4 hrs | Advanced |
| 14 | [Skip Lists](./14-skip-lists/) | Randomized data structure, probabilistic balancing, search/insert/delete | 3-4 hrs | Advanced |
| 15 | [LRU Cache](./15-lru-cache/) | Eviction policies, HashMap + DLL, LinkedHashMap, concurrent LRU | 2-3 hrs | Advanced |
| 16 | [Concurrent Data Structures](./16-concurrent-data-structures/) | Lock-free, CAS, ConcurrentHashMap, non-blocking queues | 4-5 hrs | Expert |
| 17 | [Spatial Data Structures](./17-spatial-data-structures/) | Quadtrees, R-trees, k-d trees, spatial indexing, nearest neighbor | 4-5 hrs | Expert |
| 18 | [Circular Buffers](./18-circular-buffers/) | Ring buffers, producer-consumer, overwrite/blocking policies | 2-3 hrs | Intermediate |
| 19 | [Sparse Table & RMQ](./19-sparse-table-rmq/) | Range minimum/maximum queries, precomputation, O(1) queries | 2-3 hrs | Advanced |
| 20 | [Immutable & Persistent](./20-immutable-persistent/) | Immutability, structural sharing, persistent tree/list, functional | 3-4 hrs | Expert |

### Level 5: Frontier & Probabilistic Data Structures
| # | Lab | Topic | Duration | Difficulty |
|---|-----|-------|----------|------------|
| 21 | [Cuckoo & Robin Hood Hashing](./21-cuckoo-robin-hood/) | Cuckoo hashing, cycle detection, Robin Hood, backward shift deletion | 3-4 hrs | Expert |
| 22 | [Merkle Tree](./22-merkle-tree/) | Hash tree, proof of inclusion, verification, blockchain applications | 2-3 hrs | Advanced |
| 23 | [DSU with Rollbacks](./23-dsu-rollbacks/) | Persistent DSU, union by size with history, offline dynamic connectivity | 3-4 hrs | Expert |
| 24 | [XOR & Unrolled Linked Lists](./24-xor-unrolled-lists/) | Memory-efficient XOR list, block-based unrolled list, cache-friendly | 2-3 hrs | Advanced |
| 25 | [Space-Filling Curves](./25-space-filling-curves/) | Z-order, Hilbert curves, Morton codes, N-dim to 1D mapping | 3-4 hrs | Expert |
| 26 | [HyperLogLog](./26-hyperloglog/) | Cardinality estimation, register array, harmonic mean, HLL merging | 3-4 hrs | Expert |
| 27 | [Count-Min Sketch](./27-count-min-sketch/) | Frequency estimation, heavy hitters, conservative update | 2-3 hrs | Expert |
| 28 | [MinHash & SimHash](./28-minhash-simhash/) | Jaccard similarity, LSH, near-duplicate detection, banding | 3-4 hrs | Expert |
| 29 | [van Emde Boas Tree](./29-van-emde-boas/) | O(log log U) operations, predecessor/successor, recursive decomposition | 4-5 hrs | Expert |
| 30 | [Cache-Oblivious Structures](./30-cache-oblivious/) | Ideal cache model, CO B-tree, van Emde Boas layout, recursive matrix multiply | 4-5 hrs | Expert |

**Total estimated time: 100-130 hours**

---

## Learning Path

```
01─→02─→03─→04─→05─→06─→07─→08─→09─→10─→11─→12─→13─→14─→15─→16─→17─→18─→19─→20─→21─→22─→23─→24─→25─→26─→27─→28─→29─→30
Arr  LL  StQ  Tree Graph Hash Heap Trie AdvT Bloom UF   SegT FenW Skip LRU  Conc Spat Circ Spar Imm  Cuck Merk DSU  XOR  SFC  HLL  CMS  MinH vEB  CO
```

Labs 01–03: linear structures. Labs 04–05: trees & graphs. Labs 06–08: hash/prefix. Labs 09–10: advanced & probabilistic. Labs 11–15: specialized utilities. Labs 16–20: concurrent/spatial/functional. Labs 21–25: frontier hashing/lists/indexing. Labs 26–30: probabilistic sketches & advanced trees.

---

## Prerequisites

- Basic Java syntax (variables, loops, methods, classes)
- Understanding of recursion
- Familiarity with Big O notation
- Basic knowledge of object-oriented programming

---

## How to Use This Academy

### For Beginners
Start at Lab 01 and work through sequentially. Complete all exercises before moving to the next lab.

### For Intermediate Developers
Start at Lab 04 (Trees) or Lab 06 (Hash Tables). Use earlier labs as refresher material.

### For Interview Preparation
Focus on Labs 01–07 (core structures) and Labs 11–15 (specialized structures often asked in interviews).

### Lab Structure
Each lab contains 24 markdown files and 7 subdirectories:

| File | Purpose |
|------|---------|
| `README.md` | Overview, learning objectives, prerequisites |
| `THEORY.md` | Comprehensive theoretical foundation |
| `WHY_IT_EXISTS.md` | Historical context and motivation |
| `WHY_IT_MATTERS.md` | Practical importance in real-world development |
| `HISTORY.md` | Evolution of the data structure |
| `MENTAL_MODELS.md` | Analogies and mental frameworks |
| `HOW_IT_WORKS.md` | Step-by-step mechanical explanation |
| `INTERNALS.md` | Under-the-hood implementation details |
| `MATH_FOUNDATION.md` | Complexity analysis and mathematical prequisites |
| `VISUAL_GUIDE.md` | Diagrams and visual explanations |
| `CODE_DEEP_DIVE.md` | Detailed Java implementation walkthroughs |
| `STEP_BY_STEP.md` | Tutorial-style guided implementation |
| `COMMON_MISTAKES.md` | Pitfalls and anti-patterns |
| `DEBUGGING.md` | Debugging strategies and tools |
| `REFACTORING.md` | Improving existing code |
| `PERFORMANCE.md` | Time/space complexity analysis |
| `SECURITY.md` | Security implications and best practices |
| `ARCHITECTURE.md` | Architectural considerations |
| `EXERCISES.md` | Practice problems with solutions |
| `QUIZ.md` | Self-assessment questions |
| `FLASHCARDS.md` | Spaced-repetition learning cards |
| `INTERVIEW.md` | Common interview questions |
| `REFLECTION.md` | Guided self-reflection prompts |
| `REFERENCES.md` | Further reading and resources |
| `MINI_PROJECT/` | Small hands-on project |
| `REAL_WORLD_PROJECT/` | Production-scale project |
| `CHALLENGE/` | Advanced challenge problems |
| `TESTS/` | Unit and integration tests |
| `BENCHMARK/` | Performance benchmarks |
| `DIAGRAMS/` | Visual aids and architecture diagrams |
| `SOLUTION/` | Solutions to exercises and projects |

---

## Related Academies

- [Algorithms Academy](../algorithms/) — Sorting, searching, DP, graph algorithms
- [Java Academy](../java/) — Core Java fundamentals, collections framework
- [Backend Academy](../backend/) — Data structures in real-world services
- [System Design Academy](../system-design/) — When to use which data structure at scale

---

## Resources

### Books
- *Introduction to Algorithms* (CLRS) — Cormen, Leiserson, Rivest, Stein
- *Algorithms* — Robert Sedgewick & Kevin Wayne
- *Algorithm Design Manual* — Steven Skiena
- *Data Structures and Algorithms in Java* — Michael T. Goodrich

### Practice
- [LeetCode](https://leetcode.com/)
- [HackerRank Data Structures](https://www.hackerrank.com/domains/data-structures)
- [GeeksforGeeks](https://www.geeksforgeeks.org/data-structures/)
- [Visualgo](https://visualgo.net/) — Algorithm visualization

---

<div align="center">

**Master Structures. Build Everything.**

</div>
