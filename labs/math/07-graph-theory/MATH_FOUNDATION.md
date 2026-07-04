# Math Foundation of Graph Theory

## Prerequisites

- Set theory: vertices $V$, edges $E$
- Relations: a graph is a binary relation on $V$
- Basic combinatorics: $\binom{V}{2}$ possible edges

## Formal Definition

A graph $G = (V, E)$ consists of:
- A set $V$ of vertices (nodes)
- A set $E \subseteq \{\{u, v\} \mid u, v \in V\}$ of edges (undirected)
- Or $E \subseteq V \times V$ for directed graphs

## Fundamental Theorems

### Handshaking Lemma
$$
\sum_{v \in V} \deg(v) = 2|E|
$$

### Euler's Formula (Planar Graphs)
$$
V - E + F = 2
$$

### Mantel's Theorem
Maximum edges in triangle-free graph on $n$ vertices: $\lfloor n^2/4 \rfloor$

### Cayley's Formula
Number of labeled trees on $n$ vertices: $n^{n-2}$

### Four Color Theorem
Every planar graph is 4-colorable.

## Graph Properties

| Property | Meaning |
|----------|---------|
| Connected | Path exists between any two vertices |
| Bipartite | Vertices can be split into 2 sets, edges go between sets |
| Planar | Drawable without edge crossings |
| Eulerian | Trail using every edge exactly once |
| Hamiltonian | Cycle visiting every vertex exactly once |
