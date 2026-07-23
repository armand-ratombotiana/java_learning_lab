package com.algorithms.branchbound;

import java.util.Arrays;

/**
 * Custom: 0/1 Knapsack using Branch and Bound
 * Given weights and values of items, find max value with capacity constraint.
 *
 * Time Complexity: O(2^n) worst case
 * Space Complexity: O(n)
 */
public class KnapsackBB {

    private static class Item {
        int weight, value;
        double ratio;
        Item(int weight, int value) {
            this.weight = weight;
            this.value = value;
            this.ratio = (double) value / weight;
        }
    }

    private int maxValue = 0;

    public int knapsack(int[] weights, int[] values, int capacity) {
        int n = weights.length;
        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) items[i] = new Item(weights[i], values[i]);
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio, a.ratio));
        backtrack(items, 0, 0, 0, capacity);
        return maxValue;
    }

    private void backtrack(Item[] items, int idx, int currentWeight, int currentValue, int capacity) {
        if (idx == items.length) {
            maxValue = Math.max(maxValue, currentValue);
            return;
        }
        if (currentWeight + items[idx].weight <= capacity) {
            backtrack(items, idx + 1, currentWeight + items[idx].weight, currentValue + items[idx].value, capacity);
        }
        double bound = currentValue + (capacity - currentWeight) * items[idx].ratio;
        if (bound > maxValue) {
            backtrack(items, idx + 1, currentWeight, currentValue, capacity);
        }
    }

    public static void main(String[] args) {
        KnapsackBB kbb = new KnapsackBB();
        int r1 = kbb.knapsack(new int[] { 10, 20, 30 }, new int[] { 60, 100, 120 }, 50);
        System.out.println("Test 1: " + r1 + " (expected: 220)");

        int r2 = kbb.knapsack(new int[] { 2, 3, 4, 5 }, new int[] { 3, 4, 5, 6 }, 5);
        System.out.println("Test 2: " + r2 + " (expected: 7)");
    }
}
