# Module 2: Dynamic Programming Deep Dive

<div align="center">

**8 Micro-Labs | Duration: 24-32 hours | Difficulty: Advanced**

</div>

---

## Overview

This module delivers an atomic deep-dive into dynamic programming paradigms — from classic DP formulations to advanced optimization techniques. Each micro-lab provides rigorous theory, real Java 21+ implementations, and JUnit 5 verified correctness.

---

## Micro-Labs

### [01 — DP Classics](./01-dp-classics/)
Fibonacci (top-down, bottom-up, space-optimized, matrix exponentiation), grid paths (unique paths, min path sum, obstacles), rod cutting, coin change (unbounded/0-1 knapsack).

### [02 — LCS & Edit Distance](./02-lcs-edit-distance/)
Longest Common Subsequence (DP table, space-optimized, Hirschberg), Edit Distance (Levenshtein, Damerau-Levenshtein), longest palindromic subsequence, shortest common supersequence.

### [03 — LIS & Kadane](./03-lis-kadane/)
Longest Increasing Subsequence (O(n²), O(n log n) patience sorting), maximum subarray (Kadane), maximum product subarray, MCS with divide-and-conquer.

### [04 — Knapsack Variants](./04-knapsack-variants/)
0/1 knapsack (DP table, space-optimized), unbounded knapsack, bounded knapsack (binary splitting), subset sum, partition equal subset sum, knapsack with DP on value.

### [05 — Matrix Chain & String DP](./05-matrix-chain-string-dp/)
Matrix chain multiplication (MCM, optimal parenthesization), palindrome partitioning (min cuts), word break, burst balloons, boolean parenthesization.

### [06 — Tree DP](./06-tree-dp/)
Diameter of tree (2-DFS vs DP), max path sum (any to any), tree coloring (max independent set), tree DP with rerooting, tree centroid decomposition.

### [07 — Digit DP](./07-digit-dp/)
Count numbers with digit constraints, sum of digits, divisible by K, count numbers in range [L,R] with property P, tight/loose states, magic numbers.

### [08 — DP Optimizations](./08-dp-optimizations/)
Divide and Conquer DP (Knuth optimization, Quadrangle inequality), Convex Hull Trick (CHT, Li Chao tree), monotone queue DP (sliding window max), SOS DP (subset DP), DP on profiles (bitmask DP).

---

## Prerequisites

- Java 21+ SDK
- Strong understanding of recursion and memoization
- Basic complexity analysis
- Familiarity with arrays, lists, and basic data structures

## How to Use

1. Navigate to a micro-lab directory
2. Read `README.md` for learning objectives
3. Study `THEORY.md` and `MATH_FOUNDATION.md`
4. Review `CODE_DEEP_DIVE.md` for implementation walkthroughs
5. Complete `EXERCISES.md` and verify with `TESTS/`
6. Benchmark your solutions with `BENCHMARK/`
7. Test your knowledge with `QUIZ.md` and `FLASHCARDS.md`
