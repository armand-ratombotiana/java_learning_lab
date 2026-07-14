# Dijkstra's Code Deep Dive

This lab provides a pure Java implementation of Dijkstra's algorithm using an Adjacency List and a Priority Queue for optimal performance.

## 💻 Pure Java Implementation

```java file="labs/algorithms/data-structures/graphs/dijkstra/SOLUTION/DijkstraAlgorithm.java"
package algorithms.graphs;

import java.util.*;

/**
 * A production-grade implementation of Dijkstra's Shortest Path algorithm.
 */
public class DijkstraAlgorithm {

    static class Edge {
        int target;
        int weight;
        Edge(int target, int weight) {
            this.target = target;
            this.weight = weight;
        }
    }

    static class Node implements Comparable<Node> {
        int id;
        int distance;
        Node(int id, int distance) {
            this.id = id;
            this.distance = distance;
        }
        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    public static int[] findShortestPaths(List<List<Edge>> graph, int source) {
        int n = graph.size();
        int[] distances = new int[n];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;

        PriorityQueue<Node> pq = new PriorityQueue<>();
        pq.add(new Node(source, 0));

        boolean[] visited = new boolean[n];

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            int u = current.id;

            if (visited[u]) continue;
            visited[u] = true;

            // Relax all outgoing edges from u
            for (Edge edge : graph.get(u)) {
                int v = edge.target;
                int weight = edge.weight;

                // If path through u is shorter than previously known path to v
                if (!visited[v] && distances[u] != Integer.MAX_VALUE && distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                    pq.add(new Node(v, distances[v]));
                }
            }
        }
        return distances;
    }

    public static void main(String[] args) {
        int numNodes = 5;
        List<List<Edge>> graph = new ArrayList<>();
        for (int i = 0; i < numNodes; i++) graph.add(new ArrayList<>());

        // Build a sample graph
        // 0 -> 1 (4), 0 -> 2 (1)
        // 2 -> 1 (2), 2 -> 3 (5)
        // 1 -> 4 (3), 3 -> 4 (1)
        graph.get(0).add(new Edge(1, 4));
        graph.get(0).add(new Edge(2, 1));
        graph.get(2).add(new Edge(1, 2));
        graph.get(2).add(new Edge(3, 5));
        graph.get(1).add(new Edge(4, 3));
        graph.get(3).add(new Edge(4, 1));

        int[] dists = findShortestPaths(graph, 0);

        System.out.println("Shortest distances from Node 0:");
        for (int i = 0; i < dists.length; i++) {
            System.out.println("To Node " + i + ": " + dists[i]);
        }
    }
}
```

## 🔍 Key Takeaways
1. **The Priority Queue**: Notice how we use `pq.poll()` to always get the node with the smallest distance. This is the heart of the greedy strategy.
2. **The `visited` Check**: We mark nodes as visited once they are polled from the PQ. If we see a node again (because it was added to the PQ multiple times with different distances), we skip it. This prevents redundant work.
3. **Integer.MAX_VALUE**: We initialize distances to "Infinity". We must check `distances[u] != Integer.MAX_VALUE` before adding weights to avoid integer overflow, which would turn the distance into a negative number and break the algorithm.