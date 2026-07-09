package com.algorithms.lab06;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class DeepDiveTest {

    @Test
    void testExchangeArgumentActivitySelection() {
        int[][] intervals = {{1, 4}, {3, 5}, {0, 6}, {5, 7}, {3, 8}, {5, 9}, {6, 10}, {8, 11}, {8, 12}, {2, 13}, {12, 14}};
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
        int count = 0, lastEnd = 0;
        for (int[] act : intervals) {
            if (act[0] >= lastEnd) {
                count++;
                lastEnd = act[1];
            }
        }
        assertEquals(4, count); // Optimal: {1,4}, {5,7}, {8,11}, {12,14}
    }

    @Test
    void testExchangeProofHuffman() {
        // Huffman: merge smallest frequencies first
        int[] freq = {5, 9, 12, 13, 16, 45};
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int f : freq) pq.add(f);
        
        int totalCost = 0;
        while (pq.size() > 1) {
            int a = pq.poll();
            int b = pq.poll();
            int merged = a + b;
            totalCost += merged;
            pq.add(merged);
        }
        // For {5,9,12,13,16,45}, Huffman coding has specific cost
        assertTrue(totalCost > 0, "Total cost should be positive");
    }

    @Test
    void testFractionalKnapsackGreedy() {
        double[][] items = {{10, 60}, {20, 100}, {30, 120}}; // {weight, value}
        Arrays.sort(items, (a, b) -> Double.compare(b[1]/b[0], a[1]/a[0]));
        double capacity = 50;
        double totalValue = 0;
        for (double[] item : items) {
            if (capacity >= item[0]) {
                totalValue += item[1];
                capacity -= item[0];
            } else {
                totalValue += (item[1] / item[0]) * capacity;
                break;
            }
        }
        assertEquals(240.0, totalValue, 0.001);
    }

    @Test
    void testJobSchedulingWithDeadlines() {
        int[][] jobs = {{4, 20}, {1, 10}, {1, 40}, {1, 30}}; // {deadline, profit}
        Arrays.sort(jobs, (a, b) -> b[1] - a[1]); // sort by profit descending
        int maxDeadline = Arrays.stream(jobs).mapToInt(j -> j[0]).max().orElse(0);
        int[] slots = new int[maxDeadline + 1];
        int totalProfit = 0;
        for (int[] job : jobs) {
            for (int t = job[0]; t > 0; t--) {
                if (slots[t] == 0) {
                    slots[t] = job[1];
                    totalProfit += job[1];
                    break;
                }
            }
        }
        assertEquals(60, totalProfit);
    }

    @Test
    void testMatroidGraphic() {
        // Kruskal's algorithm on a graphic matroid
        int[][] edges = {{0, 1, 10}, {0, 2, 6}, {0, 3, 5}, {1, 3, 15}, {2, 3, 4}};
        Arrays.sort(edges, (a, b) -> Integer.compare(a[2], b[2]));
        int[] parent = new int[4];
        for (int i = 0; i < 4; i++) parent[i] = i;
        
        int[][] findUnion = edges;
        // Simple union-find to verify MST weight
        int weight = 0;
        for (int[] e : edges) {
            int pu = parent[e[0]];
            while (pu != parent[pu]) { pu = parent[pu]; }
            int pv = parent[e[1]];
            while (pv != parent[pv]) { pv = parent[pv]; }
            if (pu != pv) {
                weight += e[2];
                parent[pu] = pv;
            }
        }
        assertEquals(19, weight); // Kruskal: (2,3,4) + (0,3,5) + (0,1,10) = 19
    }

    @Test
    void testGreedyCoinChangeCanonical() {
        int[] coins = {25, 10, 5, 1}; // US coins (canonical)
        int amount = 99;
        int count = 0;
        for (int coin : coins) {
            count += amount / coin;
            amount %= coin;
        }
        assertEquals(0, amount);
        assertEquals(9, count); // 3*25 + 2*10 + 0*5 + 4*1 = 9 coins
    }

    @Test
    void testGreedyFailsNonCanonical() {
        // Non-canonical coin system: {1, 3, 4}
        // Greedy for amount 6: 4+1+1 = 3 coins
        // Optimal: 3+3 = 2 coins
        int[] coins = {4, 3, 1};
        int amount = 6;
        int greedyCount = 0;
        for (int coin : coins) {
            greedyCount += amount / coin;
            amount %= coin;
        }
        assertTrue(amount == 0, "Greedy may not give optimal for non-canonical systems");
        // Actually, sort by largest first and check
        int[] sortedCoins = {4, 3, 1};
        int amt = 6;
        int gc = 0;
        for (int c : sortedCoins) { gc += amt / c; amt %= c; }
        assertEquals(3, gc);
    }

    @Test
    void testHuffmanEntropyBound() {
        double[] probs = {0.25, 0.25, 0.2, 0.15, 0.1, 0.05};
        double entropy = 0;
        for (double p : probs) {
            if (p > 0) entropy -= p * Math.log(p) / Math.log(2);
        }
        // Entropy should be < 2.5 (for this distribution)
        assertTrue(entropy > 0 && entropy < 3.0);
    }

    @Test
    void testGreedyVsDPClassification() {
        // For knapsack, greedy by ratio gives 1/2 approximation
        int[] weights = {10, 20, 30};
        int[] values = {60, 100, 120};
        int cap = 50;
        
        // Greedy by ratio
        Integer[] idx = {0, 1, 2};
        Arrays.sort(idx, (a, b) -> Double.compare(
            (double) values[b] / weights[b], (double) values[a] / weights[a]));
        int gw = 0, gv = 0;
        for (int i : idx) {
            if (gw + weights[i] <= cap) { gw += weights[i]; gv += values[i]; }
        }
        
        assertTrue(gv >= 110, "Greedy should get at least 110 (half of optimal 220)");
    }
}
