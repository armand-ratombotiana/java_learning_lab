# Refactoring — Network Flow

## Extract Dinic into Reusable Class

`java
class DinicMaxFlow {
    private List<Edge>[] graph;
    private int[] level, it;
    
    public DinicMaxFlow(int n) { ... }
    public void addEdge(int from, int to, long cap) { ... }
    public long maxFlow(int s, int t) { ... }
}
`

## Edge Data Record (Java 16+)

Use records for immutable edge data: record Edge(int to, int rev, long cap) {}

## Factory Pattern for Flow Algorithms

`java
interface MaxFlowAlgorithm {
    long computeMaxFlow(int n, int s, int t, List<EdgeDef> edges);
}
`

Allow easy switching between Ford-Fulkerson, Edmonds-Karp, Dinic, and Push-Relabel.

## Parallel BFS/DFS

For very large graphs, parallelize level computation and blocking flow search using thread pools.

## Memory Optimization

Use int[] instead of objects for edges: store to, rev, cap in parallel arrays.
