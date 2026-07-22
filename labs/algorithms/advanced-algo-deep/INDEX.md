# Module 4: Advanced Algorithms Deep Dive

<div align="center">

**8 Micro-Labs | Duration: 24-32 hours | Difficulty: Expert**

</div>

---

## Overview

This module delivers an atomic deep-dive into advanced algorithmic domains — from bit manipulation and number theory to parallel, randomized, and approximation algorithms. Each micro-lab provides rigorous theory, real Java 21+ implementations, and JUnit 5 verified correctness.

---

## Micro-Labs

### [01 — Bit Manipulation Advanced](./01-bit-manipulation-advanced/)
Bit DP (Hamiltonian path, TSP DP over subsets), subset generation (SOS DP, Gray code), bit tricks (lowbit, popcount, leading/trailing zeros), XOR basis (linear basis over GF(2)).

### [02 — Number Theory Advanced](./02-number-theory-advanced/)
Extended Euclid, modular inverse (Fermat, extended Euclid), CRT (Chinese Remainder Theorem), Euler's totient, Fermat's little theorem, Miller-Rabin (deterministic bases), Pollard Rho, Mobius function, FFT/NTT.

### [03 — String Algorithms Advanced](./03-string-algorithms-advanced/)
Suffix array (doubling, SA-IS), LCP array (Kasai), suffix automaton (SAM, right contexts), suffix tree (Ukkonen, implicit/active nodes), Aho-Corasick (trie, failure links, automaton).

### [04 — Geometry Algorithms](./04-geometry-algorithms/)
Point/line operations, cross/dot product, convex hull (Graham scan, Andrew monotone chain, Jarvis march), closest pair (divide-and-conquer), line sweep (intersections, skyline), rotating calipers.

### [05 — Combinatorial Algorithms](./05-combinatorial-algorithms/)
Permutations (next/prev lexicographic, rank/unrank), combinations (Gray code order, Gosper's hack), partitions (restricted, Ferrers diagrams), Catalan numbers, Stirling numbers, Bell numbers.

### [06 — Parallel Algorithms](./06-parallel-algorithms/)
ForkJoin framework (work-stealing, RecursiveTask/RecursiveAction), parallel prefix sum (Blelloch), parallel sort (merge, quick), parallel search, work-span analysis, Amdahl's law.

### [07 — Randomized Algorithms](./07-randomized-algorithms/)
Las Vegas vs Monte Carlo, Miller-Rabin primality, Quickselect (random pivot analysis), hash-based approximation (Bloom filter, Count-Min Sketch, HyperLogLog), reservoir sampling, Karger's min cut.

### [08 — Approximation Algorithms](./08-approximation-algorithms/)
Vertex cover (2-approx via maximal matching), Set cover (log n greedy), TSP (MST-based 2-approx, Christofides), knapsack FPTAS (DP scaling, rounding), bin packing (first-fit decreasing, best-fit decreasing).

---

## Prerequisites

- Java 21+ SDK
- Strong algorithms background (Modules 1-3 or equivalent)
- Mathematical maturity (discrete math, linear algebra, number theory)
- Familiarity with concurrency (ForkJoin, threads)

## How to Use

1. Navigate to a micro-lab directory
2. Read `README.md` for learning objectives
3. Study `THEORY.md` and `MATH_FOUNDATION.md`
4. Review `CODE_DEEP_DIVE.md` for implementation walkthroughs
5. Complete `EXERCISES.md` and verify with `TESTS/`
6. Benchmark your solutions with `BENCHMARK/`
7. Test your knowledge with `QUIZ.md` and `FLASHCARDS.md`
