# Internals: Graph Representation in Java

## Adjacency List Memory

```java
// For a graph with V vertices and E edges:
List<Integer>[] adj = new List[V];
// Array of V references: 4V bytes (compressed OOPs)
// Per vertex: ArrayList object (~40 bytes) + backing array
// Per edge: reference in backing array (4 bytes)
// Total: O(V + E) memory

// Node-based adjacency list:
class Node { int vertex; Node next; }
// Each edge: ~24 bytes (object header + vertex + next + padding)
// Compared to ArrayList: 4 bytes per edge
```

## Weighted Graph

```java
class Edge {
    int to;
    int weight;
    Edge(int to, int weight) { this.to = to; this.weight = weight; }
}

// Adjacency list for weighted graph
List<Edge>[] adj = new List[V];
adj[u].add(new Edge(v, weight));
```

## Graph as HashMap (Sparse/Infinite Graphs)

```java
// For very large or infinite graphs (e.g., web crawler):
Map<Integer, Set<Integer>> graph = new HashMap<>();
graph.computeIfAbsent(u, k -> new HashSet<>()).add(v);

// Or use Guava's Graph:
// MutableGraph<Integer> graph = GraphBuilder.undirected().build();
```

## JGraphT Library (Production)

```java
// Production-quality graph library
Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
graph.addVertex(1);
graph.addVertex(2);
graph.addEdge(1, 2);  // connects 1-2
```

## Java No Standard Graph

Unlike `List`, `Map`, `Set`, Java does not have a standard `Graph` interface. Each application implements its own or uses a third-party library like **JGraphT**, **Guava Graphs**, or **Apache Commons Graph**.

## Serialization

Graph serialization formats for Java:
- **Adjacency list** (JSON): `{"1": [2, 3], "2": [1], "3": [1]}`
- **Edge list** (CSV): `1,2`, `1,3`, `2,3`
- **Adjacency matrix** (2D array): `[[0,1,1],[1,0,0],[1,0,0]]`
- **GraphML**: XML-based standard for exchanging graph data
- **DOT**: text format used by GraphViz visualization
