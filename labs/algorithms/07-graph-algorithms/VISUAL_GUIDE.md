# Visual Guide — Graph Algorithms

## BFS vs DFS Tree
`
Graph:     BFS from A:     DFS from A:
  A        A (level 0)      A
 / \       |                |
B   C      B-C (level 1)    B
|   |      |                |
D   E      D-E (level 2)    D
                            C
                            E
`

## MST Example
`
Graph:         MST (Prim/Kruskal):
 A--6--B        A--1--B
 |     |        |     |
 5     3        5     3
 |     |        |     |
 D--2--C        D--2--C
`
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Graph Algorithms

## Bellman-Ford (Negative Edge Detection)
`java
public int[] bellmanFord(List<Edge> edges, int n, int src) {
    int[] dist = new int[n];
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[src] = 0;
    for (int i = 1; i < n; i++) {
        for (Edge e : edges) {
            if (dist[e.u] != Integer.MAX_VALUE && dist[e.u] + e.w < dist[e.v])
                dist[e.v] = dist[e.u] + e.w;
        }
    }
    for (Edge e : edges) {
        if (dist[e.u] != Integer.MAX_VALUE && dist[e.u] + e.w < dist[e.v])
            throw new RuntimeException("Negative cycle detected");
    }
    return dist;
}
`

## Union-Find for Kruskal
`java
class UnionFind {
    int[] parent, rank;
    UnionFind(int n) { parent = IntStream.range(0, n).toArray(); rank = new int[n]; }
    int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }
    boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (rank[px] < rank[py]) parent[px] = py;
        else if (rank[px] > rank[py]) parent[py] = px;
        else { parent[py] = px; rank[px]++; }
        return true;
    }
}
`
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Graph Algorithms

## BFS
1. Initialize queue with start node, mark visited
2. While queue not empty:
3.   Poll node, process it
4.   For each unvisited neighbor: mark visited, enqueue

## DFS (Recursive)
1. Mark current node visited, process it
2. For each unvisited neighbor: recursively call DFS

## Dijkstra
1. Initialize dist[src]=0, others=∞
2. Add src to priority queue
3. While queue not empty:
4.   Poll node with minimum distance
5.   If stale (dist > recorded), skip
6.   For each edge: if newDist < dist[neighbor], update and push
