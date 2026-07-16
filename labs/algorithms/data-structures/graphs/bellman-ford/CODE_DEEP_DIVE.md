# Bellman-Ford Code Deep Dive

This lab provides a pure Java implementation of the Bellman-Ford algorithm, including the logic to detect negative cycles.

## 💻 Pure Java Implementation

```java file="labs/algorithms/data-structures/graphs/bellman-ford/SOLUTION/BellmanFord.java"
package algorithms.graphs;

import java.util.Arrays;

/**
 * A fundamental implementation of the Bellman-Ford shortest path algorithm.
 */
public class BellmanFord {

    static class Edge {
        int source, target, weight;
        Edge(int s, int t, int w) {
            this.source = s;
            this.target = t;
            this.weight = w;
        }
    }

    public static void findShortestPath(int V, Edge[] edges, int source) {
        double[] dist = new double[V];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        dist[source] = 0;

        // 1. Relax all edges V-1 times
        for (int i = 1; i < V; i++) {
            for (Edge edge : edges) {
                if (dist[edge.source] != Double.POSITIVE_INFINITY && 
                    dist[edge.source] + edge.weight < dist[edge.target]) {
                    dist[edge.target] = dist[edge.source] + edge.weight;
                }
            }
        }

        // 2. Check for negative-weight cycles
        for (Edge edge : edges) {
            if (dist[edge.source] != Double.POSITIVE_INFINITY && 
                dist[edge.source] + edge.weight < dist[edge.target]) {
                System.err.println("🚨 ERROR: Graph contains a negative-weight cycle!");
                return;
            }
        }

        // 3. Print results
        System.out.println("Vertex Distance from Source (" + source + "):");
        for (int i = 0; i < V; i++) {
            System.out.println(i + "\t\t" + (dist[i] == Double.POSITIVE_INFINITY ? "INF" : dist[i]));
        }
    }

    public static void main(String[] args) {
        int V = 5;
        Edge[] edges = {
            new Edge(0, 1, -1),
            new Edge(0, 2, 4),
            new Edge(1, 2, 3),
            new Edge(1, 3, 2),
            new Edge(1, 4, 2),
            new Edge(3, 2, 5),
            new Edge(3, 1, 1),
            new Edge(4, 3, -3)
        };

        findShortestPath(V, edges, 0);
    }
}
```

## 🔍 Key Takeaways
1. **The Double Loop**: Look at the nested loops. The outer loop runs $V-1$ times. The inner loop iterates over every edge. This guaranteed repetition is what makes the algorithm robust but slow.
2. **Double.POSITIVE_INFINITY**: We use `Double` to avoid the integer overflow issues seen in Dijkstra's. If you use `Integer.MAX_VALUE` and add a negative weight, it will overflow to a very small negative number, causing the algorithm to fail.
3. **The Cycle Check**: The second loop over edges is the most important part of Bellman-Ford. If a path can still be shortened after $V-1$ steps, it is the definitive proof of a negative cycle.