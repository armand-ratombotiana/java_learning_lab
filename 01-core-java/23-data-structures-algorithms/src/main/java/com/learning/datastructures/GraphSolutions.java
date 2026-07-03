package com.learning.datastructures;

import java.util.*;

/**
 * Implementations of core Graph algorithms from the practice catalog.
 */
public class GraphSolutions {

    // =========================================================================
    // 1. Breadth-First Search (BFS)
    // =========================================================================
    public static List<Integer> bfs(int V, List<List<Integer>> adj, int startNode) {
        List<Integer> bfsTraversal = new ArrayList<>();
        boolean[] visited = new boolean[V];
        Queue<Integer> queue = new LinkedList<>();
        
        visited[startNode] = true;
        queue.offer(startNode);
        
        while (!queue.isEmpty()) {
            int curr = queue.poll();
            bfsTraversal.add(curr);
            
            for (int neighbor : adj.get(curr)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                }
            }
        }
        return bfsTraversal;
    }

    // =========================================================================
    // 2. Depth-First Search (DFS)
    // =========================================================================
    public static List<Integer> dfs(int V, List<List<Integer>> adj, int startNode) {
        List<Integer> dfsTraversal = new ArrayList<>();
        boolean[] visited = new boolean[V];
        dfsHelper(startNode, adj, visited, dfsTraversal);
        return dfsTraversal;
    }
    
    private static void dfsHelper(int node, List<List<Integer>> adj, boolean[] visited, List<Integer> result) {
        visited[node] = true;
        result.add(node);
        
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                dfsHelper(neighbor, adj, visited, result);
            }
        }
    }

    // =========================================================================
    // 3. Dijkstra's Algorithm (Shortest Path)
    // =========================================================================
    public static int[] dijkstra(int V, List<List<int[]>> adj, int source) {
        int[] distances = new int[V];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[source] = 0;
        
        // PriorityQueue stores int[]{node, distance}
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[]{source, 0});
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int u = current[0];
            int dist = current[1];
            
            if (dist > distances[u]) continue;
            
            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];
                
                if (distances[u] + weight < distances[v]) {
                    distances[v] = distances[u] + weight;
                    pq.offer(new int[]{v, distances[v]});
                }
            }
        }
        return distances;
    }
}