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