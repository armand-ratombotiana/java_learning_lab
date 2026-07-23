package com.algorithms.greedy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 56: Merge Intervals
 * https://leetcode.com/problems/merge-intervals/
 *
 * Merge all overlapping intervals.
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class MergeIntervals {

    public int[][] merge(int[][] intervals) {
        if (intervals.length <= 1) return intervals;
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];
        merged.add(current);

        for (int[] interval : intervals) {
            if (interval[0] <= current[1]) {
                current[1] = Math.max(current[1], interval[1]);
            } else {
                current = interval;
                merged.add(current);
            }
        }
        return merged.toArray(new int[merged.size()][]);
    }

    public static void main(String[] args) {
        MergeIntervals mi = new MergeIntervals();
        int[][] r1 = mi.merge(new int[][] { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } });
        System.out.println("Test 1: " + Arrays.deepToString(r1) + " (expected: [[1,6],[8,10],[15,18]])");

        int[][] r2 = mi.merge(new int[][] { { 1, 4 }, { 4, 5 } });
        System.out.println("Test 2: " + Arrays.deepToString(r2) + " (expected: [[1,5]])");

        int[][] r3 = mi.merge(new int[][] { { 1, 4 }, { 2, 3 } });
        System.out.println("Test 3: " + Arrays.deepToString(r3) + " (expected: [[1,4]])");

        int[][] r4 = mi.merge(new int[][] {});
        System.out.println("Test 4: " + Arrays.deepToString(r4) + " (expected: [])");
    }
}
