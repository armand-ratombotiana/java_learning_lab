# Bellman-Ford Theory & Intuition

## 💡 The Problem with Dijkstra's
Dijkstra's algorithm is fast and efficient, but it has one fatal flaw: it relies on a greedy assumption. It assumes that once a node is visited, its shortest path is finalized.
This assumption breaks if the graph has **Negative Edge Weights**.

Imagine a graph where edge A->B has weight 5, but edge B->C has weight -10. 
- Dijkstra might visit B and decide the path is 5.
- It doesn't account for the fact that going *further* into the graph (to C) actually reduces the total cost.

## 🚀 The Solution: Bellman-Ford
Published in 1958, Bellman-Ford is a Dynamic Programming algorithm that solves the shortest path problem even in the presence of negative weights.

### The Strategy: Systematic Relaxation
Instead of greedily picking the "best" node, Bellman-Ford uses brute force relaxation.
1. It initializes all distances to $\infty$.
2. It relaxes **every single edge** in the graph.
3. It repeats this process exactly $V-1$ times (where $V$ is the number of vertices).

**Why $V-1$?** Because the longest possible shortest path in a graph with $V$ nodes (without cycles) can have at most $V-1$ edges. By relaxing all edges $V-1$ times, we guarantee that the shortest path information propagates from the source to every other node, regardless of the order in which edges are processed.

## 🛑 Negative Cycles
A **Negative Cycle** is a loop in a graph where the sum of edge weights is less than zero. 
In such a graph, a "shortest path" doesn't exist because you could loop forever and reduce the cost to $-\infty$.

Bellman-Ford has a built-in "superpower": it can detect negative cycles. After $V-1$ iterations, it runs one final relaxation. If any distance *still* decreases, it means a negative cycle must exist.