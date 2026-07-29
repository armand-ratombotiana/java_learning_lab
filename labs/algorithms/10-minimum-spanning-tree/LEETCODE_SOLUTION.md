# LeetCode 1584 — Min Cost to Connect All Points

## Problem

You are given an array `points` representing integer coordinates on a 2D plane. The **distance** between `points[i]` and `points[j]` is the **Manhattan distance**: `|xi - xj| + |yi - yj|`.

Return the **minimum cost** to connect all points (i.e., the weight of a Minimum Spanning Tree).

**Constraints:**
- `1 <= points.length <= 1000`

---

## Solution 1: Prim's Algorithm

### Intuition

Prim's grows a tree from a seed node, always adding the cheapest edge connecting a visited node to an unvisited node. Using a min-heap, each iteration picks the minimum-weight frontier edge.

```java
import java.util.*;

/**
 * LeetCode 1584 — Min Cost to Connect All Points
 *
 * Prim's algorithm with adjacency list on the fly (no explicit graph).
 *
 * Time: O(V^2) naive, O(V^2 log V) with heap — here O(V^2) as we build edges live
 * Space: O(V)
 */
public class MinCostConnectPointsPrim {

    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        boolean[] visited = new boolean[n];
        int[] minDist = new int[n];
        Arrays.fill(minDist, Integer.MAX_VALUE);
        minDist[0] = 0;

        int cost = 0, edgesUsed = 0;

        while (edgesUsed < n) {
            int u = -1;
            // Pick unvisited node with smallest minDist
            for (int i = 0; i < n; i++) {
                if (!visited[i] && (u == -1 || minDist[i] < minDist[u]))
                    u = i;
            }
            visited[u] = true;
            cost += minDist[u];
            edgesUsed++;

            // Update distances to unvisited neighbors
            for (int v = 0; v < n; v++) {
                if (!visited[v]) {
                    int d = Math.abs(points[u][0] - points[v][0])
                          + Math.abs(points[u][1] - points[v][1]);
                    minDist[v] = Math.min(minDist[v], d);
                }
            }
        }
        return cost;
    }

    public static void main(String[] args) {
        MinCostConnectPointsPrim s = new MinCostConnectPointsPrim();

        int[][] t1 = {{0,0},{2,2},{3,10},{5,2},{7,0}};
        System.out.println("Test 1: " + s.minCostConnectPoints(t1) + " (expected: 20)");

        int[][] t2 = {{0,0},{1,1},{1,0},{-1,1}};
        System.out.println("Test 2: " + s.minCostConnectPoints(t2) + " (expected: 4)");

        int[][] t3 = {{0,0}};
        System.out.println("Test 3: " + s.minCostConnectPoints(t3) + " (expected: 0)");

        int[][] t4 = {{0,0},{1,0},{2,0}};
        System.out.println("Test 4: " + s.minCostConnectPoints(t4) + " (expected: 2)");
    }
}
```

---

## Solution 2: Kruskal's Algorithm with Union-Find

```java
import java.util.*;

/**
 * LeetCode 1584 — Min Cost to Connect All Points
 *
 * Kruskal's algorithm: sort all edges by weight, union if no cycle.
 *
 * Time: O(V^2 log V) | Space: O(V^2)
 */
public class MinCostConnectPointsKruskal {

    public int minCostConnectPoints(int[][] points) {
        int n = points.length;
        List<int[]> edges = new ArrayList<>();

        // Generate all V*(V-1)/2 edges with Manhattan distances
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int d = Math.abs(points[i][0] - points[j][0])
                      + Math.abs(points[i][1] - points[j][1]);
                edges.add(new int[]{i, j, d});
            }
        }

        edges.sort((a, b) -> a[2] - b[2]);

        UnionFind uf = new UnionFind(n);
        int cost = 0, edgesUsed = 0;

        for (int[] e : edges) {
            if (uf.union(e[0], e[1])) {
                cost += e[2];
                edgesUsed++;
                if (edgesUsed == n - 1) break;
            }
        }
        return cost;
    }

    static class UnionFind {
        int[] parent, rank;
        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }
        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }
        boolean union(int x, int y) {
            int rx = find(x), ry = find(y);
            if (rx == ry) return false;
            if (rank[rx] < rank[ry]) parent[rx] = ry;
            else if (rank[rx] > rank[ry]) parent[ry] = rx;
            else { parent[ry] = rx; rank[rx]++; }
            return true;
        }
    }

    public static void main(String[] args) {
        MinCostConnectPointsKruskal s = new MinCostConnectPointsKruskal();
        int[][] t = {{0,0},{2,2},{3,10},{5,2},{7,0}};
        System.out.println("Test 1: " + s.minCostConnectPoints(t) + " (expected: 20)");
        int[][] t2 = {{0,0},{1,1},{1,0},{-1,1}};
        System.out.println("Test 2: " + s.minCostConnectPoints(t2) + " (expected: 4)");
    }
}
```

## Complexity Comparison

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Prim (naive) | O(V^2) | O(V) | Good for dense graphs (complete graph here) |
| Kruskal | O(V^2 log V) | O(V^2) | Generates all edges explicitly, simpler |

For `n ≤ 1000`, both are acceptable. Prim's O(V^2) is faster on a complete graph since generating all edges is O(V^2) anyway; Kruskal's sorting adds a log factor.