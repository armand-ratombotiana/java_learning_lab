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