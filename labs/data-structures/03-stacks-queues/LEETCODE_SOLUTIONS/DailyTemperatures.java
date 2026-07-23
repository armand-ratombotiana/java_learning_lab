package com.leetcode.stack_queue;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 739: Daily Temperatures
 * https://leetcode.com/problems/daily-temperatures/
 *
 * Given an array of temperatures, return an array answer where answer[i]
 * is the number of days until a warmer temperature.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 */
public class DailyTemperatures {

    /**
     * Approach 1 (Optimal): Monotonic Decreasing Stack
     * Store indices of temperatures waiting for a warmer day.
     * Time: O(n), Space: O(n)
     */
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int prevIndex = stack.pop();
                answer[prevIndex] = i - prevIndex;
            }
            stack.push(i);
        }
        return answer;
    }

    /**
     * Approach 2: Next warmer from right
     * Process from right to left.
     * Time: O(n), Space: O(n)
     */
    public int[] dailyTemperaturesFromRight(int[] temperatures) {
        int n = temperatures.length;
        int[] answer = new int[n];
        int hottest = 0;

        for (int i = n - 1; i >= 0; i--) {
            if (temperatures[i] >= hottest) {
                hottest = temperatures[i];
                answer[i] = 0;
            } else {
                int days = 1;
                while (temperatures[i + days] <= temperatures[i]) {
                    days += answer[i + days];
                }
                answer[i] = days;
            }
        }
        return answer;
    }

    public static void main(String[] args) {
        DailyTemperatures dt = new DailyTemperatures();

        int[] r1 = dt.dailyTemperatures(new int[] { 73, 72, 75, 71, 69, 72, 76, 73 });
        System.out.print("Test 1: ");
        printArray(r1);
        System.out.println(" (expected: [1, 1, 4, 2, 1, 1, 0, 0])");

        int[] r2 = dt.dailyTemperatures(new int[] { 30, 40, 50, 60 });
        System.out.print("Test 2: ");
        printArray(r2);
        System.out.println(" (expected: [1, 1, 1, 0])");

        int[] r3 = dt.dailyTemperatures(new int[] { 30, 30, 30 });
        System.out.print("Test 3: ");
        printArray(r3);
        System.out.println(" (expected: [0, 0, 0])");
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
