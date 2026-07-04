# Math Foundation: Graphs

## Graph Fundamentals

For a graph G = (V, E) with V vertices and E edges:
- **Undirected**: max edges = V(V-1)/2 (complete graph)
- **Directed**: max edges = V(V-1)
- **Dense graph**: E ≈ O(V²)
- **Sparse graph**: E ≈ O(V)

## Degree Relations

- **Sum of degrees** (undirected): Σ deg(v) = 2|E|
- **Handshaking lemma**: number of odd-degree vertices is even
- **Directed**: Σ in-deg(v) = Σ out-deg(v) = |E|

## BFS Distance Bounds

In an unweighted graph with V vertices:
- BFS explores in layers: L₀ = {s}, Lₖ = vertices at distance exactly k
- Each vertex belongs to exactly one layer
- Each edge connects vertices in same or adjacent layers
- Shortest path distance ≤ V - 1

## Dijkstra's Correctness

Dijkstra's algorithm relies on the property that once a vertex is dequeued from the priority queue, its distance is finalized (no shorter path can be found). This holds only for **non-negative edge weights**.

For negative weights, Bellman-Ford must be used:
- Run V-1 relaxations over all edges
- Each relaxation: if dist[u] + w(u,v) < dist[v], update dist[v]
- One additional pass detects negative cycles

## Graph Matrix Algebra

Adjacency matrix A:
- A^1 represents paths of length 1 (direct edges)
- A^k represents paths of length k
- (A^k)[i][j] = number of walks of length k from i to j

## Minimum Spanning Tree

For a connected weighted graph:
- A spanning tree has exactly V-1 edges
- **Cut property**: the minimum-weight edge crossing any cut belongs to some MST
- **Cycle property**: the maximum-weight edge in any cycle cannot be in any MST

## Planar Graph Formula (Euler)

For a connected planar graph:
- V - E + F = 2 (Euler's formula)
- F = number of faces (regions) in planar embedding
- For planar graphs: E ≤ 3V - 6 (for V ≥ 3)
