package com.algo.lab11;

import java.util.*;

/**
 * Branch and Bound algorithms.
 *
 * TSP (Branch & Bound): O(n!) worst, O(n^2) space
 * 0/1 Knapsack (B&B): O(2^n) worst, O(n) space
 */
public class BranchAndBound {

    private BranchAndBound() {}

    public static int tspBranchAndBound(int[][] graph) {
        int n = graph.length;
        boolean[] visited = new boolean[n];
        int[] bestPath = new int[n + 1];
        int[] bestCost = {Integer.MAX_VALUE};
        visited[0] = true;
        tspUtil(graph, visited, 0, 0, 1, n, new int[n + 1], bestCost, bestPath);
        return bestCost[0];
    }

    private static void tspUtil(int[][] graph, boolean[] visited, int currPos, int cost,
                                 int count, int n, int[] path, int[] bestCost, int[] bestPath) {
        if (count == n && graph[currPos][0] > 0) {
            int totalCost = cost + graph[currPos][0];
            if (totalCost < bestCost[0]) {
                bestCost[0] = totalCost;
                System.arraycopy(path, 0, bestPath, 0, n);
                bestPath[n] = 0;
            }
            return;
        }
        for (int i = 0; i < n; i++) {
            if (!visited[i] && graph[currPos][i] > 0) {
                int bound = cost + graph[currPos][i];
                if (bound >= bestCost[0]) continue;
                visited[i] = true;
                path[count] = i;
                tspUtil(graph, visited, i, bound, count + 1, n, path, bestCost, bestPath);
                visited[i] = false;
            }
        }
    }

    public static int knapSackBranchBound(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            items[i] = new Item(weights[i], values[i], (double) values[i] / weights[i]);
        }
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio, a.ratio));
        return knapSackBB(items, capacity, 0, 0, 0, new int[]{0});
    }

    private static int knapSackBB(Item[] items, int capacity, int idx, int currentWeight,
                                   int currentValue, int[] bestValue) {
        if (currentWeight > capacity) return bestValue[0];
        if (currentValue > bestValue[0]) bestValue[0] = currentValue;
        if (idx >= items.length) return bestValue[0];
        double bound = bound(items, capacity, idx, currentWeight, currentValue);
        if (bound <= bestValue[0]) return bestValue[0];
        knapSackBB(items, capacity, idx + 1, currentWeight + items[idx].weight,
                    currentValue + items[idx].value, bestValue);
        knapSackBB(items, capacity, idx + 1, currentWeight, currentValue, bestValue);
        return bestValue[0];
    }

    private static double bound(Item[] items, int capacity, int idx, int weight, int value) {
        double totalValue = value;
        int remaining = capacity - weight;
        int j = idx;
        while (j < items.length && items[j].weight <= remaining) {
            remaining -= items[j].weight;
            totalValue += items[j].value;
            j++;
        }
        if (j < items.length) {
            totalValue += items[j].ratio * remaining;
        }
        return totalValue;
    }

    private record Item(int weight, int value, double ratio) {}
}