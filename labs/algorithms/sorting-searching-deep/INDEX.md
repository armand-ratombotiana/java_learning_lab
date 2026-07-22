# Module 1: Sorting & Searching Deep Dive

<div align="center">

**8 Micro-Labs | Duration: 24-32 hours | Difficulty: Intermediate to Advanced**

</div>

---

## Overview

This module provides an atomic deep-dive into sorting and searching algorithms — from fundamental comparison sorts to advanced bit-level techniques. Each micro-lab focuses on a single algorithmic paradigm with rigorous theoretical coverage, real Java 21+ implementations, and JUnit 5 verified correctness.

---

## Micro-Labs

### [01 — Comparison Sorts](./01-comparison-sorts/)
Insertion sort analysis (best Ω(n), worst O(n²)), selection sort, bubble sort variants (cocktail shaker, comb sort), shell sort gap sequences (Shell, Hibbard, Sedgewick, Pratt).

### [02 — Divide and Conquer Sorts](./02-divide-conquer-sorts/)
Merge sort (top-down, bottom-up), quick sort (Lomuto/Hoare partitioning, 3-way, dual-pivot), hybrid sorts (Timsort, Introsort).

### [03 — Heap Sort](./03-heap-sort/)
Binary heap construction (bottom-up O(n)), heap sort (extract-max/min, sift-down), heap sort vs quick sort vs merge sort benchmarks, d-ary heap.

### [04 — Linear Sorts](./04-linear-sorts/)
Counting sort (key range, stability), radix sort (LSD/MSD, base selection), bucket sort (distribution, insertion per bucket), American flag sort.

### [05 — Binary Search Variants](./05-binary-search-variants/)
Lower/upper bound, sqrt/peak/mountain search, rotated array search, exponential search, interpolation search, ternary search.

### [06 — Order Statistics](./06-order-statistics/)
Quickselect (Hoare), median-of-medians (Blum-Floyd-Pratt-Rivest-Tarjan) O(n) worst-case, selection from two sorted arrays, kth smallest in matrix.

### [07 — String Searching](./07-string-searching/)
Brute force, KMP (prefix function, failure links), Boyer-Moore (bad character/good suffix), Rabin-Karp (rolling hash), Z-algorithm.

### [08 — Bit Sort/Search](./08-bit-sort-search/)
Bitonic sort, odd-even sort (Batcher), binary search on bit arrays, population count medians, SIMD sorting primitives.

---

## Prerequisites

- Java 21+ SDK
- Basic complexity analysis (Big O, Omega, Theta)
- Familiarity with arrays, recursion, and basic data structures

## How to Use

1. Navigate to a micro-lab directory
2. Read `README.md` for learning objectives
3. Study `THEORY.md` and `MATH_FOUNDATION.md`
4. Review `CODE_DEEP_DIVE.md` for implementation walkthroughs
5. Complete `EXERCISES.md` and verify with `TESTS/`
6. Benchmark your solutions with `BENCHMARK/`
7. Test your knowledge with `QUIZ.md` and `FLASHCARDS.md`
