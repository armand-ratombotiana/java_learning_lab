# LeetCode 743 — Network Delay Time

## Problem

You are given a network of `n` nodes labeled `1` to `n`, and a list of travel times as directed edges `times[i] = (u, v, w)` where `u` is source, `v` is target, and `w` is travel time.

You send a signal from node `k`. Return the **minimum time** for the signal to reach **all** nodes, or `-1` if not all nodes are reachable.

**Constraints:**
- `1 <= n <= 100`
- `1 <= times.length <= 6000`

---

## Solution: Dijkstra with Binary Heap

```java
import java.util.*;

/**
 * LeetCode 743 — Network Delay Time
 *
 * Dijkstra's algorithm with a binary heap (PriorityQueue).
 *
 * Time: O((V + E) log V) | Space: O(V + E)
 */
public class NetworkDelayTime {

    public int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list
        List<int[]>[] graph = new List[n + 1];
        for (int i = 1; i <= n; i++) graph[i] = new ArrayList<>();
        for (int[] edge : times) {
            graph[edge[0]].add(new int[]{edge[1], edge[2]});
        }

        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[]{k, 0});

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int node = cur[0], d = cur[1];
            if (d > dist[node]) continue;

            for (int[] neighbor : graph[node]) {
                int next = neighbor[0], w = neighbor[1];
                int nd = d + w;
                if (nd < dist[next]) {
                    dist[next] = nd;
                    pq.offer(new int[]{next, nd});
                }
            }
        }

        int maxDelay = 0;
        for (int i = 1; i <= n; i++) {
            if (dist[i] == Integer.MAX_VALUE) return -1;
            maxDelay = Math.max(maxDelay, dist[i]);
        }
        return maxDelay;
    }

    public static void main(String[] args) {
        NetworkDelayTime s = new NetworkDelayTime();

        // Test 1: Standard case
        int[][] t1 = {{2,1,1},{2,3,1},{3,4,1}};
        System.out.println("Test 1: " + s.networkDelayTime(t1, 4, 2) + " (expected: 2)");

        // Test 2: Unreachable node
        int[][] t2 = {{1,2,1}};
        System.out.println("Test 2: " + s.networkDelayTime(t2, 2, 2) + " (expected: -1)");

        // Test 3: Single node
        int[][] t3 = {};
        System.out.println("Test 3: " + s.networkDelayTime(t3, 1, 1) + " (expected: 0)");

        // Test 4: Multiple paths
        int[][] t4 = {{1,2,10},{1,3,5},{2,3,2},{3,2,1}};
        System.out.println("Test 4: " + s.networkDelayTime(t4, 3, 1) + " (expected: 6)");
    }
}
```

---

## Complexity Analysis

| Aspect | Value |
|--------|-------|
| Time Complexity | O((V + E) log V) — Each node extracted once (V log V), each edge relaxed once (E log V) |
| Space Complexity | O(V + E) — Adjacency list stores E edges, distance array stores V values, PQ holds at most V entries |

### Algorithm Walkthrough

1. Build adjacency list from `times` edges.
2. Initialize `dist[k] = 0`, all others to `INF`.
3. Use a min-heap keyed by distance, starting with `(k, 0)`.
4. For each extracted node, relax all outgoing edges.
5. After processing, the max finite distance is the answer.

### Why Dijkstra?

- All edge weights are non-negative (travel times).
- We need the shortest path from `k` to all nodes — Dijkstra solves single-source shortest paths optimally.
- A binary heap gives O(log V) extract-min and decrease-key operations.