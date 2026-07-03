# Debugging — Graph Algorithms

## Print Graph
`java
private void printGraph(Map<Integer, List<Integer>> graph) {
    for (var entry : graph.entrySet())
        System.out.println(entry.getKey() + " → " + entry.getValue());
}
`

## Visualize Step by Step
`java
System.out.println("Visited: " + visited + " Queue: " + queue);
`

## Test Small Graphs
`java
Map<Integer, List<Integer>> g = Map.of(0, List.of(1), 1, List.of(2), 2, List.of(0));
assertTrue(hasCycle(g));  // triangle
`
"@

wf "REFACTORING.md" @"
# Refactoring — Graph Algorithms

## Graph Interface
`java
public interface Graph {
    void addEdge(int u, int v, int weight);
    List<Integer> bfs(int start);
    int[] shortestPath(int src);
}
`

## Visitor Pattern for Traversal
`java
interface NodeVisitor { void visit(int node); }
`
"@

wf "PERFORMANCE.md" @"
# Performance — Graph Algorithms

| Algorithm | Time | Space | Use Case |
|-----------|------|-------|----------|
| BFS | O(V+E) | O(V) | Unweighted shortest path |
| DFS | O(V+E) | O(V) | Connectivity, cycles |
| Dijkstra | O((V+E) log V) | O(V) | Non-negative weights |
| Bellman-Ford | O(VE) | O(V) | Negative edge detection |
| Floyd-Warshall | O(V³) | O(V²) | All-pairs |
| Prim | O(E log V) | O(V) | MST |
| Kruskal | O(E log E) | O(V) | MST |
