package com.algorithms.greedy;

import java.util.*;

/**
 * LeetCode 621: Task Scheduler
 * https://leetcode.com/problems/task-scheduler/
 *
 * Given tasks and a cooldown n, find the minimum time to complete all tasks.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class TaskScheduler {

    /**
     * Approach 1 (Optimal): Greedy
     * The idle time is determined by the most frequent task.
     * idle = (maxFreq - 1) * n - count of other tasks filling slots.
     */
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char c : tasks) freq[c - 'A']++;
        Arrays.sort(freq);

        int maxFreq = freq[25];
        int idle = (maxFreq - 1) * n;

        for (int i = 24; i >= 0 && freq[i] > 0; i--) {
            idle -= Math.min(maxFreq - 1, freq[i]);
        }

        return idle > 0 ? tasks.length + idle : tasks.length;
    }

    public static void main(String[] args) {
        TaskScheduler ts = new TaskScheduler();
        System.out.println("Test 1: " + ts.leastInterval(new char[] { 'A', 'A', 'A', 'B', 'B', 'B' }, 2) + " (expected: 8)");
        System.out.println("Test 2: " + ts.leastInterval(new char[] { 'A', 'A', 'A', 'B', 'B', 'B' }, 0) + " (expected: 6)");
        System.out.println("Test 3: " + ts.leastInterval(new char[] { 'A', 'A', 'A', 'A', 'A', 'A', 'B', 'C', 'D', 'E', 'F', 'G' }, 2) + " (expected: 16)");
        System.out.println("Test 4: " + ts.leastInterval(new char[] { 'A' }, 3) + " (expected: 1)");
    }
}
