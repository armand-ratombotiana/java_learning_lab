# LeetCode 787 — Cheapest Flights Within K Stops

## Problem

There are `n` cities connected by `m` flights. Each flight `(from, to, price)` is a one-way edge. Given a source `src` and destination `dst`, find the **cheapest price** from `src` to `dst` with **at most `k` stops** (i.e., `k + 1` flights max). Return `-1` if no such route exists.

**Constraints:**
- `1 <= n <= 100`
- `0 <= flights.length <= n * (n - 1) / 2`
- `0 <= k < n`

---

## Solution: Bellman-Ford (DP with K iterations)

### Intuition

Bellman-Ford relaxes all edges `V - 1` times to find shortest paths. We adapt it: run exactly `k + 1` iterations, tracking the minimum cost to reach each node using at most that many flights. This naturally enforces the stop constraint.

```java
import java.util.Arrays;

/**
 * LeetCode 787 — Cheapest Flights Within K Stops
 *
 * Bellman-Ford with K+1 iterations.
 * Each iteration finds the best price using at most that many flights.
 *
 * Time: O(K * E) | Space: O(V)
 */
public class CheapestFlights {

    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] prices = new int[n];
        Arrays.fill(prices, Integer.MAX_VALUE);
        prices[src] = 0;

        // Run k+1 iterations (k stops means k+1 flights)
        for (int i = 0; i <= k; i++) {
            int[] tmp = prices.clone();
            for (int[] f : flights) {
                int from = f[0], to = f[1], price = f[2];
                if (prices[from] != Integer.MAX_VALUE) {
                    int newPrice = prices[from] + price;
                    if (newPrice < tmp[to]) {
                        tmp[to] = newPrice;
                    }
                }
            }
            prices = tmp;
        }

        return prices[dst] == Integer.MAX_VALUE ? -1 : prices[dst];
    }

    public static void main(String[] args) {
        CheapestFlights s = new CheapestFlights();

        // Test 1: Standard case
        int[][] t1 = {{0,1,100},{1,2,100},{2,3,100},{0,2,500}};
        System.out.println("Test 1: " + s.findCheapestPrice(4, t1, 0, 3, 1)
            + " (expected: 500)");

        // Test 2: Not enough stops
        System.out.println("Test 2: " + s.findCheapestPrice(4, t1, 0, 3, 0)
            + " (expected: -1)");

        // Test 3: Direct + within stops
        int[][] t3 = {{0,1,10},{1,2,20},{0,2,50}};
        System.out.println("Test 3: " + s.findCheapestPrice(3, t3, 0, 2, 1)
            + " (expected: 30)");

        // Test 4: Self-loop
        System.out.println("Test 4: " + s.findCheapestPrice(3, t3, 0, 0, 0)
            + " (expected: 0)");
    }
}
```

---

## Alternative: Dijkstra with Stops as State

We can also run Dijkstra with a state `(node, stopsRemaining)`:

```java
import java.util.*;

/**
 * Dijkstra alternative for LeetCode 787.
 * Time: O(E * log(V * K)) | Space: O(V * K)
 */
public class CheapestFlightsDijkstra {

    public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        List<int[]>[] graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<>();
        for (int[] f : flights) graph[f[0]].add(new int[]{f[1], f[2]});

        int[][] cost = new int[n][k + 2];
        for (int[] row : cost) Arrays.fill(row, Integer.MAX_VALUE);
        cost[src][0] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]);
        pq.offer(new int[]{src, 0, 0}); // node, stops, cost

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int node = cur[0], stops = cur[1], price = cur[2];
            if (price > cost[node][stops]) continue;
            if (node == dst) return price;
            if (stops > k) continue;

            for (int[] edge : graph[node]) {
                int next = edge[0], p = edge[1];
                int ns = stops + 1;
                int np = price + p;
                if (ns <= k + 1 && np < cost[next][ns]) {
                    cost[next][ns] = np;
                    pq.offer(new int[]{next, ns, np});
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        CheapestFlightsDijkstra s = new CheapestFlightsDijkstra();
        int[][] t = {{0,1,100},{1,2,100},{2,3,100},{0,2,500}};
        System.out.println(s.findCheapestPrice(4, t, 0, 3, 1) + " (expected: 500)");
    }
}
```

## Complexity Comparison

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Bellman-Ford (K iterations) | O(K * E) | O(V) | Simple, fits constraints well |
| Dijkstra with state | O(E * log(V * K)) | O(V * K) | More complex, better for large K |

For `n ≤ 100`, Bellman-Ford is simpler and fast enough.