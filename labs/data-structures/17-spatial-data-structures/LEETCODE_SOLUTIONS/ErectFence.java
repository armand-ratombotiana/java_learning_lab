package com.leetcode.spatial;

import java.util.*;

/**
 * LeetCode 587: Erect the Fence
 * https://leetcode.com/problems/erect-the-fence/
 *
 * Find the convex hull of a set of points (Monotone Chain algorithm).
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class ErectFence {

    public int[][] outerTrees(int[][] trees) {
        int n = trees.length;
        if (n <= 1) return trees;

        Arrays.sort(trees, (a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);

        List<int[]> hull = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            while (hull.size() >= 2 && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), trees[i]) > 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(trees[i]);
        }

        if (n == hull.size()) return trees;

        int lowerSize = hull.size();
        for (int i = n - 2; i >= 0; i--) {
            while (hull.size() > lowerSize && cross(hull.get(hull.size() - 2), hull.get(hull.size() - 1), trees[i]) > 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(trees[i]);
        }

        hull.remove(hull.size() - 1);
        int[][] result = new int[hull.size()][2];
        for (int i = 0; i < hull.size(); i++) result[i] = hull.get(i);
        return result;
    }

    private int cross(int[] o, int[] a, int[] b) {
        return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0]);
    }

    public static void main(String[] args) {
        ErectFence ef = new ErectFence();
        int[][] points = { { 1, 1 }, { 2, 2 }, { 2, 0 }, { 2, 4 }, { 3, 3 }, { 4, 2 } };
        int[][] hull = ef.outerTrees(points);
        System.out.println("Convex hull points: " + hull.length + " points");
        for (int[] p : hull) System.out.print("(" + p[0] + "," + p[1] + ") ");
        System.out.println();
    }
}
