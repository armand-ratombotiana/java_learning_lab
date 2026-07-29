# Advanced Data Structures — Complete Index

## Overview

This repository contains 10 advanced data structure labs covering non-linear, probabilistic, and tree-based structures essential for system design, competitive programming, and FAANG interviews. Each lab includes a compilable Java 21+ implementation, a comprehensive GUIDE.md, interview preparation materials, and mock interview transcripts.

## Lab Directory

| # | Data Structure | Category | Key Operations | Java File |
|---|---------------|----------|---------------|-----------|
| 01 | **Trie (Prefix Tree)** | Tree / String | insert, search, delete, startsWith, autoComplete | Trie.java |
| 02 | **Bloom Filter** | Probabilistic | add, mightContain, union, intersect | BloomFilter.java |
| 03 | **Suffix Array** | String | build, search (LCP), longestRepeatedSubstring | SuffixArray.java |
| 04 | **Fenwick Tree (BIT)** | Tree / Range | pointUpdate, prefixSum, rangeSum, rangeUpdate | FenwickTree.java |
| 05 | **Segment Tree** | Tree / Range | rangeQuery, pointUpdate, rangeUpdate (lazy), RMQ | SegmentTree.java |
| 06 | **Skip List** | Linked List / Probabilistic | insert, search, delete, rangeScan, levelIterator | SkipList.java |
| 07 | **Red-Black Tree** | BST / Self-Balancing | insert, delete, search, rotate, verify (RB properties) | RedBlackTree.java |
| 08 | **Treap (Cartesian Tree)** | BST / Heap | insert (priority-based), delete, split, merge, orderStatistic | Treap.java |
| 09 | **Union-Find (DSU)** | Graph / Disjoint Set | find (path compression), union (rank), connected, count | UnionFind.java |
| 10 | **Merkle Tree** | Tree / Cryptographic | build, rootHash, verify, getProof, checkConsistency | MerkleTree.java |

## Complexity Summary

| Structure | Insert | Search | Delete | Space |
|-----------|--------|--------|--------|-------|
| Trie | O(L) | O(L) | O(L) | O(A·L) |
| Bloom Filter | O(k) | O(k) | — | O(m) |
| Suffix Array | O(n log n) | O(m log n) | — | O(n) |
| Fenwick Tree | O(log n) | O(log n) | O(log n) | O(n) |
| Segment Tree | O(log n) | O(log n) | O(log n) | O(4n) |
| Skip List | O(log n) avg | O(log n) avg | O(log n) avg | O(n log n) |
| Red-Black Tree | O(log n) | O(log n) | O(log n) | O(n) |
| Treap | O(log n) avg | O(log n) avg | O(log n) avg | O(n) |
| Union-Find | α(n)* | α(n)* | — | O(n) |
| Merkle Tree | O(n) build | O(log n) proof | — | O(n) |

*α(n) = inverse Ackermann function, effectively constant. L = string length, A = alphabet size, k = hash count, m = filter bits, n = element count.

## Root-Level Reference Files

| File | Purpose |
|------|---------|
| INTERVIEW_CHEATSHEET.md | Quick-reference complexity and use-case table for interview prep |
| CRACKING_THE_INTERVIEW_GUIDE.md | Comprehensive study plan covering all 10 structures |
| NEETCODE_ROADMAP.md | Mapping to NeetCode 150 / Blind 75 problem patterns |
| COMPANY_INTERVIEW_GUIDE.md | FAANG-specific question patterns per structure |
| COMPANY_BEHAVIORAL_GUIDE.md | Behavioral question frameworks and STAR responses |
| SYSTEM_DESIGN_CHEATSHEET.md | System design roles for each DS with architecture sketches |
| ACADEMY_INTERVIEW_GUIDE.md | General interview prep, timing, and strategy guide |

## Per-Lab Documentation

Each lab (01-trie through 10-merkle-tree) contains four documentation files:

### GUIDE.md (400–600 lines)
- ASCII diagram of the data structure
- Step-by-step code walkthrough referencing the Java implementation
- Operation-level complexity table with amortised/worst-case breakdowns
- Comparison with alternative structures (3-way table)
- Real-world use cases (databases, compilers, blockchain, etc.)
- Common pitfalls and debugging strategies
- Advanced extensions (persistent, concurrent, compressed variants)

### INTERVIEW.md (200–400 lines)
- 15–20 FAANG interview questions with detailed answers
- LeetCode problem references with solution approach outline
- Time/space complexity for each solution
- Edge case handling: nulls, duplicates, large inputs
- Variation questions and follow-ups

### PROBLEM_WALKTHROUGH.md (400–600 lines)
- Full interview problem statement mimicking FAANG style
- Complete Java 21+ compilable solution (single file, no dependencies)
- Step-by-step walkthrough: brute force → optimal → further optimization
- Multiple test cases covering edge cases
- Complexity analysis and correctness argument

### MOCK_INTERVIEW.md (200–400 lines)
- Simulated transcript between interviewer and candidate
- 3-4 rounds: warm-up, core problem, follow-up, system design tie-in
- Senior-level Q&A with time-pressure reasoning
- Common mistakes and interviewer "hint" moments
- Post-interview debrief

## How to Use This Repository

1. **Learning path**: Start with GUIDE.md for each structure, read the source code, then move to problems
2. **Interview prep**: Use INTERVIEW.md and MOCK_INTERVIEW.md for structured practice
3. **Deep dive**: Run PROBLEM_WALKTHROUGH.md solutions, modify test cases, experiment with edge cases
4. **System design**: Cross-reference with SYSTEM_DESIGN_CHEATSHEET.md for architecture discussions
5. **Company targeting**: Use COMPANY_INTERVIEW_GUIDE.md and COMPANY_BEHAVIORAL_GUIDE.md for role-specific prep

## Build and Run

Each lab's solution compiles with Java 21+:

```
javac --release 21 src/LabFile.java
java --source 21 src/LabFile.java
```

Or using Maven from the project root:
```
mvn compile && mvn test
```