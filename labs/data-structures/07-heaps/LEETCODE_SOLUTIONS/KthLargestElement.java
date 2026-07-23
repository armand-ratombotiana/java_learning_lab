package com.leetcode.heaps;

import java.util.PriorityQueue;

/**
 * LeetCode 215: Kth Largest Element in an Array
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 *
 * Find the kth largest element in an unsorted array.
 *
 * Time Complexity: O(n log k) with heap, O(n) avg with QuickSelect
 * Space Complexity: O(k)
 */
public class KthLargestElement {

    /**
     * Approach 1: Min-Heap of size k
     * Keep k largest elements in a min-heap.
     * Time: O(n log k), Space: O(k)
     */
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }

    /**
     * Approach 2: QuickSelect (average O(n), worst O(n^2))
     * Partition around a pivot, recurse on the side containing kth largest.
     */

    public static void main(String[] args) {
        KthLargestElement kle = new KthLargestElement();

        System.out.println("Test 1: " + kle.findKthLargest(new int[] { 3, 2, 1, 5, 6, 4 }, 2) + " (expected: 5)");
        System.out.println("Test 2: " + kle.findKthLargest(new int[] { 3, 2, 3, 1, 2, 4, 5, 5, 6 }, 4) + " (expected: 4)");
        System.out.println("Test 3: " + kle.findKthLargest(new int[] { 1 }, 1) + " (expected: 1)");
        System.out.println("Test 4: " + kle.findKthLargest(new int[] { 99, 99 }, 1) + " (expected: 99)");
    }
}
