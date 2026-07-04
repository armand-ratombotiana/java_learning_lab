# Debugging Graph Theory in Java

## Common Issues

### Graph Not Connected

Check if all expected nodes are reachable. BFS/DFS only covers the connected component containing the start vertex.

### Cycle Detection False Positive

Undirected graph: parent edge is not a cycle. Directed graph: a back edge is a cycle (different conditions!).

### Path Reconstruction

After Dijkstra/BFS, reconstruct path by storing `prev` for each node:

```java
// Store predecessor
Map<Integer, Integer> prev = new HashMap<>();
prev.put(neighbor, current);
// Reconstruct
List<Integer> path = new ArrayList<>();
for (Integer at = end; at != null; at = prev.get(at))
    path.add(at);
Collections.reverse(path);
```

## Debugging Checklist

- [ ] Directed vs undirected correct?
- [ ] Visited marking at the right time (enqueue vs dequeue)?
- [ ] Vertex indexing consistent (0-indexed vs 1-indexed)?
- [ ] Memory: adjacency matrix too large for $V > 10000$?
- [ ] Edge weights: negative values handled?
- [ ] Cycle detection logic matches graph type?
