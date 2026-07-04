# Graph Theory

The study of graphs — mathematical structures modeling pairwise relations between objects.

## Scope

- Directed and undirected graphs
- Paths, cycles, connectivity
- Trees, spanning trees
- Shortest paths, network flow
- Graph coloring, matchings

## Java Implementation

```java
public class Graph {
    private final Map<Integer, List<Integer>> adj = new HashMap<>();

    public void addEdge(int u, int v) {
        adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
        adj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
    }

    public List<Integer> bfs(int start) {
        List<Integer> visited = new ArrayList<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> seen = new HashSet<>();
        queue.add(start);
        seen.add(start);
        while (!queue.isEmpty()) {
            int node = queue.poll();
            visited.add(node);
            for (int neighbor : adj.getOrDefault(node, List.of()))
                if (!seen.contains(neighbor)) {
                    seen.add(neighbor);
                    queue.add(neighbor);
                }
        }
        return visited;
    }
}
```
