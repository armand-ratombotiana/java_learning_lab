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