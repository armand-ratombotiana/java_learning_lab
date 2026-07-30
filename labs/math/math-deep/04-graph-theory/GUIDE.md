# Graph Theory — Study Guide

## Core Concepts

### Graph Representations
- **Adjacency Matrix**: O(V²) space, O(1) edge lookup, dense graphs
- **Adjacency List**: O(V+E) space, O(deg(v)) edge lookup, sparse graphs
- **Edge List**: simple, good for algorithms sorting edges (Kruskal)

### Eulerian vs Hamiltonian
- Eulerian path: visits every edge exactly once (0 or 2 odd-degree vertices)
- Hamiltonian path: visits every vertex exactly once (NP-complete to find)

### Graph Coloring
- Vertex coloring: adjacent vertices get different colors
- Chromatic number χ(G): minimum colors needed
- Greedy algorithm: O(V+E), uses at most Δ+1 colors
- Four Color Theorem: every planar graph is 4-colorable

## Implementation Checklist
1. Choose representation based on graph density
2. Use degree sequence as first isomorphism check
3. For Eulerian: check degree parity before searching
4. For coloring: order vertices by degree (largest first) for better greedy results

## Common Pitfalls
- Assuming isomorphism can be checked by degree sequence alone
- Confusing Eulerian (edges) with Hamiltonian (vertices)
- Forgetting that greedy coloring can use more than χ(G) colors
