# Theory: Graphs

## Graph Definitions

A graph G = (V, E) consists of:
- **V**: a set of vertices (nodes)
- **E**: a set of edges (connections between vertices)

### Types

- **Undirected**: edges have no direction (u, v) = (v, u)
- **Directed (digraph)**: edges have direction u → v
- **Weighted**: edges have numeric weights
- **Unweighted**: edges have no weight (or unit weight)

### Properties

- **Degree**: number of edges incident to a vertex
- **In-degree**: (directed) edges entering a vertex
- **Out-degree**: (directed) edges leaving a vertex
- **Path**: sequence of vertices connected by edges
- **Cycle**: path where start = end, no repeated edges
- **Connected**: path exists between any two vertices
- **Complete**: every vertex connected to every other
- **Dense**: |E| ≈ |V|²
- **Sparse**: |E| ≈ |V|

## Representations

### Adjacency Matrix

```java
boolean[][] matrix = new boolean[n][n];
matrix[u][v] = true;  // edge from u to v
```

- O(V²) memory
- O(1) edge lookup
- Good for dense graphs

### Adjacency List

```java
List<Integer>[] adj = new List[n];
for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
adj[u].add(v);  // edge from u to v
```

- O(V + E) memory
- O(degree) edge lookup
- Good for sparse graphs

### Edge List

```java
List<Edge> edges = new ArrayList<>();
// Edge: {int from, int to, int weight}
```

- O(E) memory
- O(E) edge lookup
- Used in some algorithms (Kruskal's)

## Time Complexity

| Algorithm | Adjacency Matrix | Adjacency List |
|-----------|-----------------|---------------|
| BFS | O(V²) | O(V + E) |
| DFS | O(V²) | O(V + E) |
| Dijkstra | O(V²) | O((V+E) log V) |
| Detect cycle | O(V²) | O(V + E) |
| Topological sort | O(V²) | O(V + E) |
