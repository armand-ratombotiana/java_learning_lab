package com.algo.lab11;

public class BranchAndBoundExample {
    public static void main(String[] args) {
        System.out.println("=== Branch and Bound Demo ===\n");

        System.out.println("--- TSP (Branch & Bound) ---");
        int[][] tspGraph = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        int tspCost = BranchAndBound.tspBranchAndBound(tspGraph);
        System.out.println("Minimum TSP cost: " + tspCost);

        System.out.println("\n--- 0/1 Knapsack (Branch & Bound) ---");
        int[] weights = {10, 20, 30, 40, 50};
        int[] values = {60, 100, 120, 200, 240};
        int capacity = 100;
        int maxVal = BranchAndBound.knapSackBranchBound(weights, values, capacity);
        System.out.println("Max value (capacity " + capacity + "): " + maxVal);
    }
}