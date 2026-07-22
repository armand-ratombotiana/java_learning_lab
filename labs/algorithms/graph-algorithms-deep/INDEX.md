# Module 3: Graph Algorithms Deep Dive

<div align="center">

**8 Micro-Labs | Duration: 24-32 hours | Difficulty: Advanced to Expert**

</div>

---

## Overview

This module provides an atomic deep-dive into graph algorithms — from foundational spanning trees and shortest paths to advanced heavy-light decomposition. Each micro-lab focuses on a single algorithmic paradigm with rigorous theoretical coverage, real Java 21+ implementations, and JUnit 5 verified correctness.

---

## Micro-Labs

### [01 — Spanning Trees](./01-spanning-trees/)
Kruskal with DSU, Prim with binary/Fibonacci heap, Boruvka, MST verification (unique MST, second-best MST), dynamic MST, Steiner tree (DP approximation).

### [02 — Shortest Paths (All Pairs)](./02-shortest-paths-all-pairs/)
Floyd-Warshall (DP, transitive closure, min-max), Johnson's algorithm (reweighting, Bellman-Ford + Dijkstra), min-plus matrix multiplication, shortest paths in DAG.

### [03 — Max Flow & Min Cut](./03-max-flow-min-cut/)
Dinic (level graph, blocking flow, dead-end pruning), Push-Relabel (highest label, gap heuristic), min-cost max-flow (SSP, potentials), capacity scaling.

### [04 — Bipartite Matching](./04-bipartite-matching/)
Hungarian algorithm (assignment, O(n³), potentials), Hopcroft-Karp (max matching, O(E√V)), Kuhn-Munkres algorithm, stable marriage (Gale-Shapley), Hall's theorem.

### [05 — Graph Coloring](./05-graph-coloring/)
Greedy coloring (Welsh-Powell), Brooks' theorem, DSATUR (backtracking, Brelaz), chromatic number computation, edge coloring, Vizing's theorem.

### [06 — Topological Sort](./06-topological-sort/)
Kahn's algorithm (in-degree BFS), DFS-based topological sort, lexicographically smallest ordering, longest path in DAG, DP on DAG, parallel topological sort.

### [07 — Cycle Detection](./07-cycle-detection/)
Directed (DFS back edge, white/gray/black coloring, Kahn), undirected (DFS parent check, DSU), Eulerian/Hamiltonian cycles, Floyd cycle detection (tortoise-hare), Brent's algorithm.

### [08 — Heavy-Light Decomposition](./08-heavy-light/)
Heavy-Light Decomposition (HLD), chain decomposition, path queries (point/range), subtree queries, LCA with HLD, path updates with segment tree, path queries with Fenwick tree.

---

## Prerequisites

- Java 21+ SDK
- Strong understanding of graphs (BFS, DFS, adjacency lists/matrices)
- Basic complexity analysis
- Familiarity with priority queues, DSU, and segment trees

## How to Use

1. Navigate to a micro-lab directory
2. Read `README.md` for learning objectives
3. Study `THEORY.md` and `MATH_FOUNDATION.md`
4. Review `CODE_DEEP_DIVE.md` for implementation walkthroughs
5. Complete `EXERCISES.md` and verify with `TESTS/`
6. Benchmark your solutions with `BENCHMARK/`
7. Test your knowledge with `QUIZ.md` and `FLASHCARDS.md`
