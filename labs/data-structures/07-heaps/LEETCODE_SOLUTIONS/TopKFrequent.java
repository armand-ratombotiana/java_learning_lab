package com.leetcode.heaps;

import java.util.*;

/**
 * LeetCode 347: Top K Frequent Elements
 * https://leetcode.com/problems/top-k-frequent-elements/
 *
 * Given an integer array nums and an integer k, return the k most frequent elements.
 *
 * Time Complexity: O(n log k)
 * Space Complexity: O(n)
 */
public class TopKFrequent {

    /**
     * Approach 1: Min-Heap
     * Count frequencies with HashMap, then use a min-heap of size k.
     * Time: O(n log k), Space: O(n)
     */
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) freq.put(num, freq.getOrDefault(num, 0) + 1);

        PriorityQueue<Map.Entry<Integer, Integer>> minHeap =
            new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) minHeap.poll();
        }

        int[] result = new int[k];
        for (int i = 0; i < k; i++) {
            result[i] = minHeap.poll().getKey();
        }
        return result;
    }

    /**
     * Approach 2: Bucket Sort (O(n))
     * Use array of lists where index = frequency.
     */

    public static void main(String[] args) {
        TopKFrequent tkf = new TopKFrequent();

        int[] r1 = tkf.topKFrequent(new int[] { 1, 1, 1, 2, 2, 3 }, 2);
        System.out.println("Test 1: " + Arrays.toString(r1) + " (expected: [1, 2])");

        int[] r2 = tkf.topKFrequent(new int[] { 1 }, 1);
        System.out.println("Test 2: " + Arrays.toString(r2) + " (expected: [1])");

        int[] r3 = tkf.topKFrequent(new int[] { 4, 4, 4, 4, 2, 2, 2, 3, 3, 1 }, 3);
        System.out.println("Test 3: " + Arrays.toString(r3) + " (expected: [4, 2, 3])");
    }
}
