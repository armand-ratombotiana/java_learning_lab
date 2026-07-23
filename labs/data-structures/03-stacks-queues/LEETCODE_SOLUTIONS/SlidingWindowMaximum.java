package com.leetcode.stack_queue;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 239: Sliding Window Maximum
 * https://leetcode.com/problems/sliding-window-maximum/
 *
 * You are given an array of integers nums and a sliding window of size k.
 * Return the max element in each window.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(k)
 */
public class SlidingWindowMaximum {

    /**
     * Approach 1 (Optimal): Deque (Monotonic Queue)
     * Maintain decreasing order of values in deque.
     * Time: O(n), Space: O(k)
     */
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k <= 0) return new int[0];

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i);

            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }
        return result;
    }

    public static void main(String[] args) {
        SlidingWindowMaximum swm = new SlidingWindowMaximum();

        int[] r1 = swm.maxSlidingWindow(new int[] { 1, 3, -1, -3, 5, 3, 6, 7 }, 3);
        System.out.print("Test 1: ");
        printArray(r1);
        System.out.println(" (expected: [3, 3, 5, 5, 6, 7])");

        int[] r2 = swm.maxSlidingWindow(new int[] { 1 }, 1);
        System.out.print("Test 2: ");
        printArray(r2);
        System.out.println(" (expected: [1])");

        int[] r3 = swm.maxSlidingWindow(new int[] { 9, 11 }, 2);
        System.out.print("Test 3: ");
        printArray(r3);
        System.out.println(" (expected: [11])");

        int[] r4 = swm.maxSlidingWindow(new int[] { 4, 3, 2, 1 }, 2);
        System.out.print("Test 4: ");
        printArray(r4);
        System.out.println(" (expected: [4, 3, 2])");
    }

    private static void printArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        System.out.print(sb.toString());
    }
}
