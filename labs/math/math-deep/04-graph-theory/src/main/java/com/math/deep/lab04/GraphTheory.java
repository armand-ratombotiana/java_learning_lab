package com.math.deep.lab04;

import java.util.*;

public class GraphTheory {

    public static boolean isEulerianCircuit(int[][] adjMatrix) {
        int n = adjMatrix.length;
        for (int i = 0; i < n; i++) {
            int degree = 0;
            for (int j = 0; j < n; j++) degree += adjMatrix[i][j];
            if (degree % 2 != 0) return false;
        }
        return isConnected(adjMatrix);
    }

    public static boolean isEulerianPath(int[][] adjMatrix) {
        int n = adjMatrix.length;
        int oddCount = 0;
        for (int i = 0; i < n; i++) {
            int degree = 0;
            for (int j = 0; j < n; j++) degree += adjMatrix[i][j];
            if (degree % 2 != 0) oddCount++;
        }
        return (oddCount == 0 || oddCount == 2) && isConnected(adjMatrix);
    }

    private static boolean isConnected(int[][] adjMatrix) {
        int n = adjMatrix.length;
        boolean[] visited = new boolean[n];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        visited[0] = true;
        while (!stack.isEmpty()) {
            int v = stack.pop();
            for (int u = 0; u < n; u++) {
                if (adjMatrix[v][u] > 0 && !visited[u]) {
                    visited[u] = true;
                    stack.push(u);
                }
            }
        }
        for (boolean v : visited) if (!v) return false;
        return true;
    }

    public static List<Integer> eulerianCircuit(int[][] adjMatrix) {
        if (!isEulerianCircuit(adjMatrix)) throw new IllegalArgumentException("No Eulerian circuit");
        int n = adjMatrix.length;
        int[][] graph = new int[n][n];
        for (int i = 0; i < n; i++) System.arraycopy(adjMatrix[i], 0, graph[i], 0, n);
        Deque<Integer> stack = new ArrayDeque<>();
        List<Integer> circuit = new ArrayList<>();
        stack.push(0);
        while (!stack.isEmpty()) {
            int v = stack.peek();
            boolean hasEdge = false;
            for (int u = 0; u < n; u++) {
                if (graph[v][u] > 0) {
                    graph[v][u]--;
                    graph[u][v]--;
                    stack.push(u);
                    hasEdge = true;
                    break;
                }
            }
            if (!hasEdge) circuit.add(stack.pop());
        }
        Collections.reverse(circuit);
        return circuit;
    }

    public static int greedyColoring(int[][] adjMatrix) {
        int n = adjMatrix.length;
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        colors[0] = 0;
        boolean[] used = new boolean[n];
        for (int v = 1; v < n; v++) {
            Arrays.fill(used, false);
            for (int u = 0; u < n; u++) {
                if (adjMatrix[v][u] > 0 && colors[u] != -1) used[colors[u]] = true;
            }
            int c;
            for (c = 0; c < n; c++) if (!used[c]) break;
            colors[v] = c;
        }
        int maxColor = 0;
        for (int c : colors) maxColor = Math.max(maxColor, c);
        return maxColor + 1;
    }

    public static boolean isBipartite(int[][] adjMatrix) {
        int n = adjMatrix.length;
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        Deque<Integer> queue = new ArrayDeque<>();
        for (int start = 0; start < n; start++) {
            if (colors[start] != -1) continue;
            colors[start] = 0;
            queue.push(start);
            while (!queue.isEmpty()) {
                int v = queue.pop();
                for (int u = 0; u < n; u++) {
                    if (adjMatrix[v][u] > 0) {
                        if (colors[u] == -1) {
                            colors[u] = 1 - colors[v];
                            queue.push(u);
                        } else if (colors[u] == colors[v]) return false;
                    }
                }
            }
        }
        return true;
    }

    public static int[] degreeSequence(int[][] adjMatrix) {
        int n = adjMatrix.length;
        int[] degrees = new int[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) degrees[i] += adjMatrix[i][j];
        }
        Arrays.sort(degrees);
        return degrees;
    }
}
