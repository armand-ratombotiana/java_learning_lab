$base = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms"

function Write-JavaFile($labDir, $relPath, $content) {
    $fullPath = Join-Path $labDir $relPath
    $dir = Split-Path $fullPath -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    Set-Content -Path $fullPath -Value $content -NoNewline
    Write-Host "  Created: $relPath"
}

# ============================================================
# LAB 06: Greedy Algorithms
# ============================================================
$lab = "$base\06-greedy-algorithms"

Write-JavaFile $lab "src/main/java/com/algo/lab06/GreedyAlgorithms.java" @'
package com.algo.lab06;

import java.util.*;

/**
 * Greedy algorithms.
 *
 * Activity Selection: O(n log n) time, O(1) space (excluding sort)
 * Coin Change (greedy): O(n) time, O(1) space (may not be optimal for all coin systems)
 * Fractional Knapsack: O(n log n) time, O(1) space
 * Huffman Coding: O(n log n) time, O(n) space
 */
public class GreedyAlgorithms {

    private GreedyAlgorithms() {}

    public static List<int[]> activitySelection(int[] start, int[] finish) {
        int n = start.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, Comparator.comparingInt(i -> finish[i]));

        List<int[]> selected = new ArrayList<>();
        selected.add(new int[]{start[indices[0]], finish[indices[0]]});
        int lastFinish = finish[indices[0]];

        for (int k = 1; k < n; k++) {
            int i = indices[k];
            if (start[i] >= lastFinish) {
                selected.add(new int[]{start[i], finish[i]});
                lastFinish = finish[i];
            }
        }
        return selected;
    }

    public static List<Integer> coinChangeGreedy(int[] coins, int amount) {
        Arrays.sort(coins);
        List<Integer> result = new ArrayList<>();
        for (int i = coins.length - 1; i >= 0 && amount > 0; i--) {
            while (amount >= coins[i]) {
                amount -= coins[i];
                result.add(coins[i]);
            }
        }
        return amount == 0 ? result : List.of();
    }

    public static double fractionalKnapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            items[i] = new Item(weights[i], values[i]);
        }
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio, a.ratio));

        double totalValue = 0.0;
        int remaining = capacity;

        for (Item item : items) {
            if (remaining <= 0) break;
            if (item.weight <= remaining) {
                totalValue += item.value;
                remaining -= item.weight;
            } else {
                totalValue += item.ratio * remaining;
                break;
            }
        }
        return totalValue;
    }

    private static class Item {
        int weight, value;
        double ratio;
        Item(int w, int v) { this.weight = w; this.value = v; this.ratio = (double) v / w; }
    }

    public static HuffmanResult huffmanCoding(Map<Character, Integer> frequencies) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(
            Comparator.comparingInt(n -> n.freq));
        for (var entry : frequencies.entrySet()) {
            pq.offer(new HuffmanNode(entry.getKey(), entry.getValue()));
        }
        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode merged = new HuffmanNode('\0', left.freq + right.freq);
            merged.left = left;
            merged.right = right;
            pq.offer(merged);
        }
        HuffmanNode root = pq.poll();
        Map<Character, String> codes = new HashMap<>();
        buildCodes(root, "", codes);
        return new HuffmanResult(root, codes);
    }

    private static void buildCodes(HuffmanNode node, String code, Map<Character, String> codes) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            codes.put(node.ch, code.isEmpty() ? "0" : code);
            return;
        }
        buildCodes(node.left, code + "0", codes);
        buildCodes(node.right, code + "1", codes);
    }

    private static class HuffmanNode {
        char ch;
        int freq;
        HuffmanNode left, right;
        HuffmanNode(char ch, int freq) { this.ch = ch; this.freq = freq; }
    }

    public record HuffmanResult(HuffmanNode root, Map<Character, String> codes) {}
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab06/GreedyExample.java" @'
package com.algo.lab06;

import java.util.*;

public class GreedyExample {
    public static void main(String[] args) {
        System.out.println("=== Greedy Algorithms Demo ===\n");

        System.out.println("--- Activity Selection ---");
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};
        List<int[]> selected = GreedyAlgorithms.activitySelection(start, finish);
        System.out.println("Selected activities:");
        for (int[] act : selected) {
            System.out.printf("  [%d, %d]%n", act[0], act[1]);
        }

        System.out.println("\n--- Coin Change (Greedy) ---");
        int[] coins = {1, 5, 10, 25};
        int amount = 63;
        List<Integer> change = GreedyAlgorithms.coinChangeGreedy(coins, amount);
        System.out.printf("Amount %d -> coins: %s%n", amount, change);

        System.out.println("\n--- Fractional Knapsack ---");
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        double maxVal = GreedyAlgorithms.fractionalKnapsack(weights, values, 50);
        System.out.printf("Max value: %.2f%n", maxVal);

        System.out.println("\n--- Huffman Coding ---");
        Map<Character, Integer> freq = new HashMap<>();
        freq.put('a', 5); freq.put('b', 9); freq.put('c', 12);
        freq.put('d', 13); freq.put('e', 16); freq.put('f', 45);
        var result = GreedyAlgorithms.huffmanCoding(freq);
        result.codes().forEach((ch, code) ->
            System.out.printf("'%c': %s%n", ch, code));
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab06/GreedyAlgorithmsTest.java" @'
package com.algo.lab06;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GreedyAlgorithmsTest {

    @Test
    void testActivitySelection() {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] finish = {2, 4, 6, 7, 9, 9};
        List<int[]> selected = GreedyAlgorithms.activitySelection(start, finish);
        assertEquals(4, selected.size());
    }

    @Test
    void testActivitySelectionSingle() {
        List<int[]> selected = GreedyAlgorithms.activitySelection(
            new int[]{1}, new int[]{2});
        assertEquals(1, selected.size());
    }

    @Test
    void testActivitySelectionAllOverlapping() {
        List<int[]> selected = GreedyAlgorithms.activitySelection(
            new int[]{1, 2, 3}, new int[]{5, 5, 5});
        assertEquals(1, selected.size());
    }

    @Test
    void testCoinChangeGreedy() {
        int[] coins = {1, 5, 10, 25};
        List<Integer> change = GreedyAlgorithms.coinChangeGreedy(coins, 63);
        assertEquals(6, change.size());
        assertTrue(change.contains(25));
        assertTrue(change.contains(10));
        assertTrue(change.contains(1));
    }

    @Test
    void testCoinChangeExact() {
        int[] coins = {1, 2, 5};
        List<Integer> change = GreedyAlgorithms.coinChangeGreedy(coins, 5);
        assertEquals(1, change.size());
        assertEquals(5, change.get(0).intValue());
    }

    @Test
    void testCoinChangeImpossible() {
        int[] coins = {2, 4};
        List<Integer> change = GreedyAlgorithms.coinChangeGreedy(coins, 3);
        assertTrue(change.isEmpty());
    }

    @Test
    void testFractionalKnapsack() {
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        double result = GreedyAlgorithms.fractionalKnapsack(weights, values, 50);
        assertEquals(240.0, result, 0.001);
    }

    @Test
    void testFractionalKnapsackZeroCapacity() {
        double result = GreedyAlgorithms.fractionalKnapsack(
            new int[]{10, 20}, new int[]{60, 100}, 0);
        assertEquals(0.0, result, 0.001);
    }

    @Test
    void testHuffmanCoding() {
        Map<Character, Integer> freq = new HashMap<>();
        freq.put('a', 5); freq.put('b', 9); freq.put('c', 12);
        freq.put('d', 13); freq.put('e', 16); freq.put('f', 45);
        var result = GreedyAlgorithms.huffmanCoding(freq);
        assertEquals(6, result.codes().size());
    }

    @Test
    void testHuffmanCodingSingleChar() {
        Map<Character, Integer> freq = new HashMap<>();
        freq.put('a', 10);
        var result = GreedyAlgorithms.huffmanCoding(freq);
        assertEquals("0", result.codes().get('a'));
    }

    @Test
    void testHuffmanCodingPrefixFree() {
        Map<Character, Integer> freq = new HashMap<>();
        freq.put('a', 5); freq.put('b', 9); freq.put('c', 12);
        var result = GreedyAlgorithms.huffmanCoding(freq);
        List<String> codes = new ArrayList<>(result.codes().values());
        for (int i = 0; i < codes.size(); i++) {
            for (int j = i + 1; j < codes.size(); j++) {
                assertFalse(codes.get(i).startsWith(codes.get(j)));
                assertFalse(codes.get(j).startsWith(codes.get(i)));
            }
        }
    }
}
'@

# ============================================================
# LAB 07: Graph Algorithms
# ============================================================
$lab = "$base\07-graph-algorithms"

Write-JavaFile $lab "src/main/java/com/algo/lab07/GraphAlgorithms.java" @'
package com.algo.lab07;

import java.util.*;

/**
 * Graph algorithms.
 *
 * BFS: O(V + E) time, O(V) space
 * DFS: O(V + E) time, O(V) space
 * Dijkstra: O((V + E) log V) time, O(V) space
 * Bellman-Ford: O(V * E) time, O(V) space
 * Floyd-Warshall: O(V^3) time, O(V^2) space
 * Prim's MST: O((V + E) log V) time, O(V) space
 * Kruskal's MST: O(E log E) time, O(V) space
 */
public class GraphAlgorithms {

    private GraphAlgorithms() {}

    public static List<Integer> bfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        visited.add(start);
        queue.offer(start);
        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);
            for (int neighbor : graph.getOrDefault(v, List.of())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
        }
        return order;
    }

    public static List<Integer> dfs(Map<Integer, List<Integer>> graph, int start) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        dfs(graph, start, visited, order);
        return order;
    }

    private static void dfs(Map<Integer, List<Integer>> graph, int v, Set<Integer> visited, List<Integer> order) {
        visited.add(v);
        order.add(v);
        for (int neighbor : graph.getOrDefault(v, List.of())) {
            if (!visited.contains(neighbor)) {
                dfs(graph, neighbor, visited, order);
            }
        }
    }

    public static Map<Integer, Integer> dijkstra(Map<Integer, List<Edge>> graph, int start) {
        Map<Integer, Integer> dist = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.dist));
        pq.offer(new Node(start, 0));
        dist.put(start, 0);
        while (!pq.isEmpty()) {
            Node curr = pq.poll();
            int u = curr.vertex;
            if (curr.dist > dist.getOrDefault(u, Integer.MAX_VALUE)) continue;
            for (Edge e : graph.getOrDefault(u, List.of())) {
                int newDist = dist.get(u) + e.weight;
                if (newDist < dist.getOrDefault(e.to, Integer.MAX_VALUE)) {
                    dist.put(e.to, newDist);
                    pq.offer(new Node(e.to, newDist));
                }
            }
        }
        return dist;
    }

    public static Map<Integer, Integer> bellmanFord(List<Edge> edges, int V, int start) {
        Map<Integer, Integer> dist = new HashMap<>();
        for (int i = 0; i < V; i++) dist.put(i, Integer.MAX_VALUE);
        dist.put(start, 0);

        for (int i = 1; i < V; i++) {
            for (Edge e : edges) {
                if (dist.get(e.from) != Integer.MAX_VALUE &&
                    dist.get(e.from) + e.weight < dist.get(e.to)) {
                    dist.put(e.to, dist.get(e.from) + e.weight);
                }
            }
        }
        for (Edge e : edges) {
            if (dist.get(e.from) != Integer.MAX_VALUE &&
                dist.get(e.from) + e.weight < dist.get(e.to)) {
                throw new IllegalStateException("Graph contains negative weight cycle");
            }
        }
        return dist;
    }

    public static int[][] floydWarshall(int[][] graph) {
        int V = graph.length;
        int[][] dist = new int[V][V];
        for (int i = 0; i < V; i++) System.arraycopy(graph[i], 0, dist[i], 0, V);
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (dist[i][k] != Integer.MAX_VALUE && dist[k][j] != Integer.MAX_VALUE
                        && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
        return dist;
    }

    public static List<Edge> primMST(Map<Integer, List<Edge>> graph, int V) {
        List<Edge> mst = new ArrayList<>();
        boolean[] inMST = new boolean[V];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        inMST[0] = true;
        for (Edge e : graph.getOrDefault(0, List.of())) pq.offer(e);
        while (!pq.isEmpty() && mst.size() < V - 1) {
            Edge e = pq.poll();
            if (inMST[e.to]) continue;
            inMST[e.to] = true;
            mst.add(e);
            for (Edge next : graph.getOrDefault(e.to, List.of())) {
                if (!inMST[next.to]) pq.offer(next);
            }
        }
        return mst;
    }

    public static List<Edge> kruskalMST(List<Edge> edges, int V) {
        List<Edge> mst = new ArrayList<>();
        edges.sort(Comparator.comparingInt(e -> e.weight));
        int[] parent = new int[V];
        for (int i = 0; i < V; i++) parent[i] = i;
        for (Edge e : edges) {
            int rootFrom = find(parent, e.from);
            int rootTo = find(parent, e.to);
            if (rootFrom != rootTo) {
                mst.add(e);
                union(parent, rootFrom, rootTo);
            }
        }
        return mst;
    }

    private static int find(int[] parent, int x) {
        if (parent[x] != x) parent[x] = find(parent, parent[x]);
        return parent[x];
    }

    private static void union(int[] parent, int x, int y) {
        parent[x] = y;
    }

    public record Edge(int from, int to, int weight) {}
    private record Node(int vertex, int dist) {}
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab07/GraphExample.java" @'
package com.algo.lab07;

import java.util.*;

public class GraphExample {
    public static void main(String[] args) {
        System.out.println("=== Graph Algorithms Demo ===\n");

        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of(1, 2));
        graph.put(1, List.of(0, 3, 4));
        graph.put(2, List.of(0, 4));
        graph.put(3, List.of(1, 4, 5));
        graph.put(4, List.of(1, 2, 3, 5));
        graph.put(5, List.of(3, 4));

        System.out.println("BFS from 0: " + GraphAlgorithms.bfs(graph, 0));
        System.out.println("DFS from 0: " + GraphAlgorithms.dfs(graph, 0));

        Map<Integer, List<GraphAlgorithms.Edge>> weightedGraph = new HashMap<>();
        weightedGraph.put(0, List.of(new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3)));
        weightedGraph.put(1, List.of(new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2)));
        weightedGraph.put(2, List.of(new GraphAlgorithms.Edge(2, 1, 1), new GraphAlgorithms.Edge(2, 3, 5)));
        weightedGraph.put(3, List.of());

        System.out.println("\nDijkstra from 0: " + GraphAlgorithms.dijkstra(weightedGraph, 0));

        List<GraphAlgorithms.Edge> edges = List.of(
            new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3),
            new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2),
            new GraphAlgorithms.Edge(2, 3, 5));
        System.out.println("Bellman-Ford from 0: " + GraphAlgorithms.bellmanFord(edges, 4, 0));

        int INF = Integer.MAX_VALUE;
        int[][] fwGraph = {
            {0, 3, INF, 5}, {2, 0, INF, 4}, {INF, 1, 0, INF}, {INF, INF, 2, 0}
        };
        int[][] fwResult = GraphAlgorithms.floydWarshall(fwGraph);
        System.out.println("\nFloyd-Warshall result:");
        for (int[] row : fwResult) System.out.println(Arrays.toString(row));

        System.out.println("\nPrim's MST: " + GraphAlgorithms.primMST(weightedGraph, 4));
        System.out.println("Kruskal's MST: " + GraphAlgorithms.kruskalMST(edges, 4));
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab07/GraphAlgorithmsTest.java" @'
package com.algo.lab07;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GraphAlgorithmsTest {

    @Test
    void testBFS() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of(1, 2));
        graph.put(1, List.of(2));
        graph.put(2, List.of());
        List<Integer> bfs = GraphAlgorithms.bfs(graph, 0);
        assertEquals(3, bfs.size());
        assertTrue(bfs.containsAll(List.of(0, 1, 2)));
    }

    @Test
    void testBFSDisconnected() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of());
        graph.put(1, List.of());
        List<Integer> bfs = GraphAlgorithms.bfs(graph, 0);
        assertEquals(1, bfs.size());
    }

    @Test
    void testDFS() {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(0, List.of(1, 2));
        graph.put(1, List.of(2));
        graph.put(2, List.of());
        List<Integer> dfs = GraphAlgorithms.dfs(graph, 0);
        assertEquals(3, dfs.size());
    }

    @Test
    void testDijkstra() {
        Map<Integer, List<GraphAlgorithms.Edge>> graph = new HashMap<>();
        graph.put(0, List.of(new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3)));
        graph.put(1, List.of(new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2)));
        graph.put(2, List.of(new GraphAlgorithms.Edge(2, 1, 1), new GraphAlgorithms.Edge(2, 3, 5)));
        graph.put(3, List.of());
        var dist = GraphAlgorithms.dijkstra(graph, 0);
        assertEquals(0, dist.get(0).intValue());
        assertEquals(3, dist.get(2).intValue());
        assertEquals(4, dist.get(1).intValue());
        assertEquals(6, dist.get(3).intValue());
    }

    @Test
    void testBellmanFord() {
        List<GraphAlgorithms.Edge> edges = List.of(
            new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3),
            new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2),
            new GraphAlgorithms.Edge(2, 3, 5));
        var dist = GraphAlgorithms.bellmanFord(edges, 4, 0);
        assertEquals(0, dist.get(0).intValue());
        assertEquals(4, dist.get(1).intValue());
        assertEquals(3, dist.get(2).intValue());
        assertEquals(6, dist.get(3).intValue());
    }

    @Test
    void testBellmanFordNegativeCycle() {
        List<GraphAlgorithms.Edge> edges = List.of(
            new GraphAlgorithms.Edge(0, 1, 1), new GraphAlgorithms.Edge(1, 2, -1),
            new GraphAlgorithms.Edge(2, 0, -2));
        assertThrows(IllegalStateException.class,
            () -> GraphAlgorithms.bellmanFord(edges, 3, 0));
    }

    @Test
    void testFloydWarshall() {
        int INF = Integer.MAX_VALUE;
        int[][] graph = {{0, 3, INF, 5}, {2, 0, INF, 4}, {INF, 1, 0, INF}, {INF, INF, 2, 0}};
        int[][] result = GraphAlgorithms.floydWarshall(graph);
        assertEquals(0, result[0][0]);
        assertEquals(3, result[0][1]);
        assertTrue(result[0][2] < INF);
    }

    @Test
    void testPrimMST() {
        Map<Integer, List<GraphAlgorithms.Edge>> graph = new HashMap<>();
        graph.put(0, List.of(new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3)));
        graph.put(1, List.of(new GraphAlgorithms.Edge(1, 0, 4), new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2)));
        graph.put(2, List.of(new GraphAlgorithms.Edge(2, 0, 3), new GraphAlgorithms.Edge(2, 1, 1), new GraphAlgorithms.Edge(2, 3, 5)));
        graph.put(3, List.of(new GraphAlgorithms.Edge(3, 1, 2), new GraphAlgorithms.Edge(3, 2, 5)));
        List<GraphAlgorithms.Edge> mst = GraphAlgorithms.primMST(graph, 4);
        assertEquals(3, mst.size());
    }

    @Test
    void testKruskalMST() {
        List<GraphAlgorithms.Edge> edges = List.of(
            new GraphAlgorithms.Edge(0, 1, 4), new GraphAlgorithms.Edge(0, 2, 3),
            new GraphAlgorithms.Edge(1, 2, 1), new GraphAlgorithms.Edge(1, 3, 2),
            new GraphAlgorithms.Edge(2, 3, 5));
        List<GraphAlgorithms.Edge> mst = GraphAlgorithms.kruskalMST(edges, 4);
        assertEquals(3, mst.size());
        int totalWeight = mst.stream().mapToInt(GraphAlgorithms.Edge::weight).sum();
        assertEquals(6, totalWeight);
    }
}
'@

# ============================================================
# LAB 08: String Algorithms
# ============================================================
$lab = "$base\08-string-algorithms"

Write-JavaFile $lab "src/main/java/com/algo/lab08/StringAlgorithms.java" @'
package com.algo.lab08;

import java.util.*;

/**
 * String matching and manipulation algorithms.
 *
 * KMP: O(n + m) time, O(m) space
 * Rabin-Karp: O(n + m) avg, O(n*m) worst, O(1) space
 * Z-Algorithm: O(n + m) time, O(n + m) space
 * Longest Palindromic Substring: O(n^2) time, O(1) space (expand around center)
 */
public class StringAlgorithms {

    private StringAlgorithms() {}

    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) return matches;
        int[] lps = computeLPS(pattern);
        int i = 0, j = 0;
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++; j++;
                if (j == m) {
                    matches.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return matches;
    }

    private static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0, i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i] = 0;
                i++;
            }
        }
        return lps;
    }

    public static List<Integer> rabinKarp(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0 || m > n) return matches;
        int d = 256, q = 101;
        int h = 1, pHash = 0, tHash = 0;
        for (int i = 0; i < m - 1; i++) h = (h * d) % q;
        for (int i = 0; i < m; i++) {
            pHash = (d * pHash + pattern.charAt(i)) % q;
            tHash = (d * tHash + text.charAt(i)) % q;
        }
        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) matches.add(i);
            }
            if (i < n - m) {
                tHash = (d * (tHash - text.charAt(i) * h) + text.charAt(i + m)) % q;
                if (tHash < 0) tHash += q;
            }
        }
        return matches;
    }

    public static int[] zAlgorithm(String s) {
        int n = s.length();
        int[] z = new int[n];
        int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) z[i] = Math.min(r - i + 1, z[i - l]);
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
        }
        return z;
    }

    public static List<Integer> zSearch(String text, String pattern) {
        String concat = pattern + "$" + text;
        int[] z = zAlgorithm(concat);
        List<Integer> matches = new ArrayList<>();
        int m = pattern.length();
        for (int i = m + 1; i < z.length; i++) {
            if (z[i] >= m) matches.add(i - m - 1);
        }
        return matches;
    }

    public static String longestPalindromicSubstring(String s) {
        int n = s.length();
        if (n < 2) return s;
        int start = 0, maxLen = 1;
        for (int i = 0; i < n; i++) {
            int len1 = expand(s, i, i);
            int len2 = expand(s, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }
        return s.substring(start, start + maxLen);
    }

    private static int expand(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--; right++;
        }
        return right - left - 1;
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab08/StringExample.java" @'
package com.algo.lab08;

import java.util.Arrays;

public class StringExample {
    public static void main(String[] args) {
        System.out.println("=== String Algorithms Demo ===\n");

        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";

        System.out.println("Text:    " + text);
        System.out.println("Pattern: " + pattern);

        System.out.println("\nKMP matches: " + StringAlgorithms.kmpSearch(text, pattern));
        System.out.println("Rabin-Karp matches: " + StringAlgorithms.rabinKarp(text, pattern));
        System.out.println("Z-algorithm matches: " + StringAlgorithms.zSearch(text, pattern));

        System.out.println("\nZ-array of \"aaabaaab\": " +
            Arrays.toString(StringAlgorithms.zAlgorithm("aaabaaab")));

        String s = "babad";
        System.out.println("Longest palindromic substring of \"" + s + "\": \""
            + StringAlgorithms.longestPalindromicSubstring(s) + "\"");

        String s2 = "cbbd";
        System.out.println("Longest palindromic substring of \"" + s2 + "\": \""
            + StringAlgorithms.longestPalindromicSubstring(s2) + "\"");

        String s3 = "forgeeksskeegfor";
        System.out.println("Longest palindromic substring of \"" + s3 + "\": \""
            + StringAlgorithms.longestPalindromicSubstring(s3) + "\"");
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab08/StringAlgorithmsTest.java" @'
package com.algo.lab08;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class StringAlgorithmsTest {

    @Test
    void testKMPFound() {
        List<Integer> matches = StringAlgorithms.kmpSearch("ABABDABACDABABCABAB", "ABABCABAB");
        assertEquals(List.of(10), matches);
    }

    @Test
    void testKMPMultiple() {
        List<Integer> matches = StringAlgorithms.kmpSearch("AAAAA", "AA");
        assertEquals(List.of(0, 1, 2, 3), matches);
    }

    @Test
    void testKMPNotFound() {
        List<Integer> matches = StringAlgorithms.kmpSearch("ABCDEF", "XYZ");
        assertTrue(matches.isEmpty());
    }

    @Test
    void testKMPEmptyPattern() {
        List<Integer> matches = StringAlgorithms.kmpSearch("ABC", "");
        assertTrue(matches.isEmpty());
    }

    @Test
    void testRabinKarpFound() {
        List<Integer> matches = StringAlgorithms.rabinKarp("ABABDABACDABABCABAB", "ABABCABAB");
        assertEquals(List.of(10), matches);
    }

    @Test
    void testRabinKarpMultiple() {
        List<Integer> matches = StringAlgorithms.rabinKarp("AAAAA", "AA");
        assertEquals(List.of(0, 1, 2, 3), matches);
    }

    @Test
    void testRabinKarpNotFound() {
        List<Integer> matches = StringAlgorithms.rabinKarp("ABCDEF", "XYZ");
        assertTrue(matches.isEmpty());
    }

    @Test
    void testZAlgorithm() {
        int[] z = StringAlgorithms.zAlgorithm("aaabaaab");
        assertArrayEquals(new int[]{0, 2, 1, 0, 2, 3, 1, 0}, z);
    }

    @Test
    void testZSearchFound() {
        List<Integer> matches = StringAlgorithms.zSearch("ABABDABACDABABCABAB", "ABABCABAB");
        assertEquals(List.of(10), matches);
    }

    @Test
    void testLongestPalindromicSubstring() {
        String result = StringAlgorithms.longestPalindromicSubstring("babad");
        assertTrue(result.equals("bab") || result.equals("aba"));
    }

    @Test
    void testLongestPalindromicSubstringEven() {
        assertEquals("bb", StringAlgorithms.longestPalindromicSubstring("cbbd"));
    }

    @Test
    void testLongestPalindromicSubstringSingle() {
        assertEquals("a", StringAlgorithms.longestPalindromicSubstring("a"));
    }

    @Test
    void testLongestPalindromicSubstringEmpty() {
        assertEquals("", StringAlgorithms.longestPalindromicSubstring(""));
    }

    @Test
    void testLongestPalindromicSubstringAllSame() {
        assertEquals("aaaa", StringAlgorithms.longestPalindromicSubstring("aaaa"));
    }

    @Test
    void testLongestPalindromicSubstringComplex() {
        String result = StringAlgorithms.longestPalindromicSubstring("forgeeksskeegfor");
        assertEquals("geeksskeeg", result);
    }

    @Test
    void testAllSearchMethodsAgree() {
        String text = "ABABDABACDABABCABAB";
        String pattern = "ABABCABAB";
        var kmp = StringAlgorithms.kmpSearch(text, pattern);
        var rk = StringAlgorithms.rabinKarp(text, pattern);
        var z = StringAlgorithms.zSearch(text, pattern);
        assertEquals(kmp, rk);
        assertEquals(kmp, z);
    }
}
'@

# ============================================================
# LAB 09: Divide and Conquer
# ============================================================
$lab = "$base\09-divide-and-conquer"

Write-JavaFile $lab "src/main/java/com/algo/lab09/DivideAndConquer.java" @'
package com.algo.lab09;

import java.util.*;

/**
 * Divide and Conquer algorithms.
 *
 * MergeSort: O(n log n) time, O(n) space
 * QuickSort: O(n log n) avg, O(n^2) worst, O(log n) space
 * Closest Pair: O(n log n) time, O(n) space
 * Max Subarray (Kadane): O(n) time, O(1) space
 */
public class DivideAndConquer {

    private DivideAndConquer() {}

    public static <T extends Comparable<T>> void mergeSort(T[] arr) {
        if (arr.length < 2) return;
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Comparable[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> void mergeSort(T[] arr, T[] temp, int left, int right) {
        if (left >= right) return;
        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, left, mid);
        mergeSort(arr, temp, mid + 1, right);
        merge(arr, temp, left, mid, right);
    }

    private static <T extends Comparable<T>> void merge(T[] arr, T[] temp, int left, int mid, int right) {
        System.arraycopy(arr, left, temp, left, right - left + 1);
        int i = left, j = mid + 1, k = left;
        while (i <= mid && j <= right) {
            if (temp[i].compareTo(temp[j]) <= 0) arr[k++] = temp[i++];
            else arr[k++] = temp[j++];
        }
        while (i <= mid) arr[k++] = temp[i++];
        while (j <= right) arr[k++] = temp[j++];
    }

    public static <T extends Comparable<T>> void quickSort(T[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private static <T extends Comparable<T>> void quickSort(T[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static <T extends Comparable<T>> int partition(T[] arr, int low, int high) {
        T pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j].compareTo(pivot) <= 0) {
                i++;
                T temp = arr[i]; arr[i] = arr[j]; arr[j] = temp;
            }
        }
        T temp = arr[i + 1]; arr[i + 1] = arr[high]; arr[high] = temp;
        return i + 1;
    }

    public static double closestPair(Point[] points) {
        Point[] sortedByX = points.clone();
        Point[] sortedByY = points.clone();
        Arrays.sort(sortedByX, Comparator.comparingDouble(p -> p.x));
        Arrays.sort(sortedByY, Comparator.comparingDouble(p -> p.y));
        return closestPair(sortedByX, sortedByY, 0, points.length - 1);
    }

    private static double closestPair(Point[] px, Point[] py, int left, int right) {
        if (right - left <= 3) {
            double min = Double.MAX_VALUE;
            for (int i = left; i <= right; i++) {
                for (int j = i + 1; j <= right; j++) {
                    min = Math.min(min, dist(px[i], px[j]));
                }
            }
            return min;
        }
        int mid = left + (right - left) / 2;
        double midX = px[mid].x;
        Point[] pyLeft = Arrays.stream(py).filter(p -> p.x <= midX).toArray(Point[]::new);
        Point[] pyRight = Arrays.stream(py).filter(p -> p.x > midX).toArray(Point[]::new);
        double dl = closestPair(px, pyLeft, left, mid);
        double dr = closestPair(px, pyRight, mid + 1, right);
        double d = Math.min(dl, dr);
        List<Point> strip = new ArrayList<>();
        for (Point p : py) {
            if (Math.abs(p.x - midX) < d) strip.add(p);
        }
        for (int i = 0; i < strip.size(); i++) {
            for (int j = i + 1; j < strip.size() && (strip.get(j).y - strip.get(i).y) < d; j++) {
                d = Math.min(d, dist(strip.get(i), strip.get(j)));
            }
        }
        return d;
    }

    private static double dist(Point a, Point b) {
        return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
    }

    public record Point(double x, double y) {}

    public static int maxSubarraySum(int[] arr) {
        int maxSoFar = arr[0], maxEndingHere = arr[0];
        for (int i = 1; i < arr.length; i++) {
            maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab09/DivideAndConquerExample.java" @'
package com.algo.lab09;

import java.util.Arrays;
import java.util.Random;

public class DivideAndConquerExample {
    public static void main(String[] args) {
        System.out.println("=== Divide and Conquer Demo ===\n");

        System.out.println("--- Merge Sort ---");
        Integer[] arr1 = {38, 27, 43, 3, 9, 82, 10};
        DivideAndConquer.mergeSort(arr1);
        System.out.println("Result: " + Arrays.toString(arr1));

        System.out.println("\n--- Quick Sort ---");
        Integer[] arr2 = {38, 27, 43, 3, 9, 82, 10};
        DivideAndConquer.quickSort(arr2);
        System.out.println("Result: " + Arrays.toString(arr2));

        System.out.println("\n--- Closest Pair ---");
        Point[] points = {
            new Point(2, 3), new Point(12, 30), new Point(40, 50),
            new Point(5, 1), new Point(12, 10), new Point(3, 4)
        };
        double minDist = DivideAndConquer.closestPair(points);
        System.out.printf("Minimum distance: %.4f%n", minDist);

        System.out.println("\n--- Max Subarray (Kadane) ---");
        int[] arr3 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Array: " + Arrays.toString(arr3));
        System.out.println("Maximum subarray sum: " + DivideAndConquer.maxSubarraySum(arr3));
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab09/DivideAndConquerTest.java" @'
package com.algo.lab09;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DivideAndConquerTest {

    @Test
    void testMergeSort() {
        Integer[] arr = {38, 27, 43, 3, 9, 82, 10};
        DivideAndConquer.mergeSort(arr);
        assertArrayEquals(new Integer[]{3, 9, 10, 27, 38, 43, 82}, arr);
    }

    @Test
    void testMergeSortEmpty() {
        Integer[] arr = {};
        DivideAndConquer.mergeSort(arr);
        assertArrayEquals(new Integer[]{}, arr);
    }

    @Test
    void testQuickSort() {
        Integer[] arr = {10, 7, 8, 9, 1, 5};
        DivideAndConquer.quickSort(arr);
        assertArrayEquals(new Integer[]{1, 5, 7, 8, 9, 10}, arr);
    }

    @Test
    void testQuickSortWithDuplicates() {
        Integer[] arr = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        DivideAndConquer.quickSort(arr);
        assertArrayEquals(new Integer[]{1, 1, 2, 3, 4, 5, 5, 6, 9}, arr);
    }

    @Test
    void testClosestPair() {
        Point[] points = {
            new Point(2, 3), new Point(12, 30), new Point(40, 50),
            new Point(5, 1), new Point(12, 10), new Point(3, 4)
        };
        double dist = DivideAndConquer.closestPair(points);
        assertTrue(dist > 0);
        assertEquals(Math.sqrt(2), dist, 0.0001);
    }

    @Test
    void testClosestPairTwoPoints() {
        Point[] points = {new Point(0, 0), new Point(3, 4)};
        assertEquals(5.0, DivideAndConquer.closestPair(points), 0.0001);
    }

    @Test
    void testMaxSubarraySum() {
        assertEquals(6, DivideAndConquer.maxSubarraySum(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
    }

    @Test
    void testMaxSubarraySumAllNegative() {
        assertEquals(-1, DivideAndConquer.maxSubarraySum(new int[]{-5, -3, -1, -7}));
    }

    @Test
    void testMaxSubarraySumAllPositive() {
        assertEquals(15, DivideAndConquer.maxSubarraySum(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    void testMaxSubarraySumSingle() {
        assertEquals(7, DivideAndConquer.maxSubarraySum(new int[]{7}));
    }

    @Test
    void testMaxSubarraySumEmpty() {
        assertEquals(0, DivideAndConquer.maxSubarraySum(new int[]{}));
    }
}
'@

# ============================================================
# LAB 10: Backtracking
# ============================================================
$lab = "$base\10-backtracking"

Write-JavaFile $lab "src/main/java/com/algo/lab10/BacktrackingAlgorithms.java" @'
package com.algo.lab10;

import java.util.*;

/**
 * Backtracking algorithms.
 *
 * N-Queens: O(n!) time, O(n) space
 * Sudoku Solver: O(9^(n*n)) time, O(n*n) space
 * Subset Sum: O(2^n) time, O(n) space
 * Hamiltonian Cycle: O(n!) time, O(n) space
 */
public class BacktrackingAlgorithms {

    private BacktrackingAlgorithms() {}

    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> results = new ArrayList<>();
        char[][] board = new char[n][n];
        for (char[] row : board) Arrays.fill(row, '.');
        solveNQueens(board, 0, results);
        return results;
    }

    private static void solveNQueens(char[][] board, int col, List<List<String>> results) {
        int n = board.length;
        if (col == n) {
            List<String> solution = new ArrayList<>();
            for (char[] row : board) solution.add(new String(row));
            results.add(solution);
            return;
        }
        for (int row = 0; row < n; row++) {
            if (isSafe(board, row, col)) {
                board[row][col] = 'Q';
                solveNQueens(board, col + 1, results);
                board[row][col] = '.';
            }
        }
    }

    private static boolean isSafe(char[][] board, int row, int col) {
        int n = board.length;
        for (int i = 0; i < col; i++) if (board[row][i] == 'Q') return false;
        for (int i = row, j = col; i >= 0 && j >= 0; i--, j--) if (board[i][j] == 'Q') return false;
        for (int i = row, j = col; i < n && j >= 0; i++, j--) if (board[i][j] == 'Q') return false;
        return true;
    }

    public static boolean solveSudoku(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isSudokuSafe(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku(board)) return true;
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSudokuSafe(int[][] board, int row, int col, int num) {
        for (int x = 0; x < 9; x++) {
            if (board[row][x] == num) return false;
            if (board[x][col] == num) return false;
        }
        int boxRow = row - row % 3, boxCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[boxRow + i][boxCol + j] == num) return false;
            }
        }
        return true;
    }

    public static List<List<Integer>> subsetSum(int[] arr, int target) {
        List<List<Integer>> results = new ArrayList<>();
        subsetSum(arr, target, 0, new ArrayList<>(), results);
        return results;
    }

    private static void subsetSum(int[] arr, int remaining, int idx, List<Integer> current, List<List<Integer>> results) {
        if (remaining == 0) {
            results.add(new ArrayList<>(current));
            return;
        }
        if (idx >= arr.length || remaining < 0) return;
        subsetSum(arr, remaining, idx + 1, current, results);
        current.add(arr[idx]);
        subsetSum(arr, remaining - arr[idx], idx + 1, current, results);
        current.remove(current.size() - 1);
    }

    public static List<Integer> hamiltonianCycle(int[][] graph) {
        int n = graph.length;
        List<Integer> path = new ArrayList<>(Collections.nCopies(n, -1));
        path.set(0, 0);
        if (hamiltonianUtil(graph, path, 1)) {
            return path;
        }
        return List.of();
    }

    private static boolean hamiltonianUtil(int[][] graph, List<Integer> path, int pos) {
        int n = graph.length;
        if (pos == n) {
            return graph[path.get(pos - 1)][path.get(0)] == 1;
        }
        for (int v = 1; v < n; v++) {
            if (isSafe(v, graph, path, pos)) {
                path.set(pos, v);
                if (hamiltonianUtil(graph, path, pos + 1)) return true;
                path.set(pos, -1);
            }
        }
        return false;
    }

    private static boolean isSafe(int v, int[][] graph, List<Integer> path, int pos) {
        if (graph[path.get(pos - 1)][v] == 0) return false;
        for (int i = 0; i < pos; i++) if (path.get(i) == v) return false;
        return true;
    }
}
'@

Write-JavaFile $lab "src/main/java/com/algo/lab10/BacktrackingExample.java" @'
package com.algo.lab10;

import java.util.List;

public class BacktrackingExample {
    public static void main(String[] args) {
        System.out.println("=== Backtracking Demo ===\n");

        System.out.println("--- N-Queens (4x4) ---");
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(4);
        System.out.println("Number of solutions: " + solutions.size());
        for (List<String> solution : solutions) {
            solution.forEach(System.out::println);
            System.out.println();
        }

        System.out.println("--- Sudoku Solver ---");
        int[][] board = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        boolean solved = BacktrackingAlgorithms.solveSudoku(board);
        System.out.println("Solved: " + solved);
        if (solved) {
            for (int[] row : board) {
                for (int v : row) System.out.print(v + " ");
                System.out.println();
            }
        }

        System.out.println("\n--- Subset Sum (target=10) ---");
        int[] arr = {1, 2, 3, 4, 5, 6, 7};
        List<List<Integer>> subsets = BacktrackingAlgorithms.subsetSum(arr, 10);
        System.out.println("Subsets that sum to 10:");
        subsets.forEach(System.out::println);

        System.out.println("\n--- Hamiltonian Cycle ---");
        int[][] graph = {
            {0, 1, 0, 1, 0},
            {1, 0, 1, 1, 1},
            {0, 1, 0, 0, 1},
            {1, 1, 0, 0, 1},
            {0, 1, 1, 1, 0}
        };
        List<Integer> path = BacktrackingAlgorithms.hamiltonianCycle(graph);
        System.out.println("Hamiltonian cycle: " + (path.isEmpty() ? "None" : path));
    }
}
'@

Write-JavaFile $lab "src/test/java/com/algo/lab10/BacktrackingAlgorithmsTest.java" @'
package com.algo.lab10;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class BacktrackingAlgorithmsTest {

    @Test
    void testNQueens4() {
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(4);
        assertEquals(2, solutions.size());
    }

    @Test
    void testNQueens1() {
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(1);
        assertEquals(1, solutions.size());
        assertEquals("Q", solutions.get(0).get(0));
    }

    @Test
    void testNQueens8() {
        List<List<String>> solutions = BacktrackingAlgorithms.solveNQueens(8);
        assertEquals(92, solutions.size());
    }

    @Test
    void testSudokuSolver() {
        int[][] board = {
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
        assertTrue(BacktrackingAlgorithms.solveSudoku(board));
        for (int[] row : board) {
            for (int v : row) assertTrue(v >= 1 && v <= 9);
        }
    }

    @Test
    void testSudokuAlreadySolved() {
        int[][] board = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };
        assertTrue(BacktrackingAlgorithms.solveSudoku(board));
    }

    @Test
    void testSubsetSum() {
        List<List<Integer>> results = BacktrackingAlgorithms.subsetSum(
            new int[]{1, 2, 3, 4, 5, 6, 7}, 10);
        assertFalse(results.isEmpty());
        for (List<Integer> subset : results) {
            assertEquals(10, subset.stream().mapToInt(Integer::intValue).sum());
        }
    }

    @Test
    void testSubsetSumNoSolution() {
        List<List<Integer>> results = BacktrackingAlgorithms.subsetSum(
            new int[]{2, 4, 6, 8}, 5);
        assertTrue(results.isEmpty());
    }

    @Test
    void testSubsetSumEmpty() {
        List<List<Integer>> results = BacktrackingAlgorithms.subsetSum(new int[]{}, 0);
        assertEquals(1, results.size());
        assertTrue(results.get(0).isEmpty());
    }

    @Test
    void testHamiltonianCycle() {
        int[][] graph = {
            {0, 1, 0, 1, 0},
            {1, 0, 1, 1, 1},
            {0, 1, 0, 0, 1},
            {1, 1, 0, 0, 1},
            {0, 1, 1, 1, 0}
        };
        List<Integer> path = BacktrackingAlgorithms.hamiltonianCycle(graph);
        assertFalse(path.isEmpty());
        assertEquals(5, path.size());
        assertTrue(path.stream().allMatch(v -> v >= 0 && v < 5));
    }

    @Test
    void testHamiltonianCycleNoSolution() {
        int[][] graph = {
            {0, 1, 0, 0},
            {1, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0}
        };
        List<Integer> path = BacktrackingAlgorithms.hamiltonianCycle(graph);
        assertTrue(path.isEmpty());
    }
}
'@

Write-Host "=== Script 2 (Labs 06-10) completed ==="
