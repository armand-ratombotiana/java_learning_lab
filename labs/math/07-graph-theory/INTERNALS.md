# Internals of Graph Theory

## Graph Representation Tradeoffs

| Property | Adjacency Matrix | Adjacency List |
|----------|-----------------|---------------|
| Space | $O(V^2)$ | $O(V + E)$ |
| Edge query | $O(1)$ | $O(\deg(v))$ |
| Iterate neighbors | $O(V)$ | $O(\deg(v))$ |
| Add edge | $O(1)$ | $O(1)$ |
| Remove edge | $O(1)$ | $O(\deg(v))$ |

## Union-Find (Disjoint Set Union)

```java
public class UnionFind {
    private final int[] parent, rank;
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
    }
    public int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]); // path compression
        return parent[x];
    }
    public boolean union(int x, int y) {
        int px = find(x), py = find(y);
        if (px == py) return false;
        if (rank[px] < rank[py]) parent[px] = py;
        else if (rank[px] > rank[py]) parent[py] = px;
        else { parent[py] = px; rank[px]++; }
        return true;
    }
}
```

## Kruskal's MST

Sort edges by weight, add edges that don't create cycles (using Union-Find).

## Topological Sort (Kahn's Algorithm)

```java
public static List<Integer> topologicalSort(Map<Integer, List<Integer>> graph, int n) {
    int[] indegree = new int[n];
    for (var entry : graph.entrySet())
        for (int v : entry.getValue()) indegree[v]++;
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < n; i++) if (indegree[i] == 0) queue.add(i);
    List<Integer> result = new ArrayList<>();
    while (!queue.isEmpty()) {
        int u = queue.poll();
        result.add(u);
        for (int v : graph.getOrDefault(u, List.of()))
            if (--indegree[v] == 0) queue.add(v);
    }
    return result.size() == n ? result : List.of(); // cycle detected
}
```
