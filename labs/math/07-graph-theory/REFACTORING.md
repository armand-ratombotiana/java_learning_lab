# Refactoring Graph Theory Code

## Encapsulate Graph Representation

```java
// BEFORE: scattered Map<Integer, List<Integer>>
Map<Integer, List<Integer>> graph = new HashMap<>();
graph.computeIfAbsent(a, k -> new ArrayList<>()).add(b);

// AFTER
public class Graph {
    private final Map<Integer, List<Integer>> adj = new HashMap<>();
    public void addEdge(int u, int v) { ... }
    public List<Integer> neighbors(int v) { ... }
    public int vertexCount() { return adj.size(); }
    public List<Integer> bfs(int start) { ... }
    public List<Integer> dfs(int start) { ... }
}
```

## Use Generics for Type Safety

```java
public class Graph<V> {
    private final Map<V, List<V>> adj = new HashMap<>();
    public void addEdge(V u, V v) { ... }
}
```

## Separate Algorithm from Data Structure

```java
// Strategy pattern for graph traversal
interface TraversalStrategy<V> {
    List<V> traverse(Graph<V> g, V start);
}

class BFS<V> implements TraversalStrategy<V> { ... }
class DFS<V> implements TraversalStrategy<V> { ... }
```
