package com.leetcode.spatial;

import java.util.Arrays;
import java.util.PriorityQueue;

/**
 * LeetCode 973: K Closest Points to Origin
 * https://leetcode.com/problems/k-closest-points-to-origin/
 *
 * Given an array of points, return the k closest points to the origin (0, 0).
 *
 * Time Complexity: O(n log k)
 * Space Complexity: O(k)
 */
public class KthClosestPoints {

    /**
     * Approach 1: Max-Heap of size k
     * Time: O(n log k), Space: O(k)
     */
    public int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
            (a, b) -> (b[0] * b[0] + b[1] * b[1]) - (a[0] * a[0] + a[1] * a[1])
        );

        for (int[] point : points) {
            maxHeap.offer(point);
            if (maxHeap.size() > k) maxHeap.poll();
        }

        int[][] result = new int[k][2];
        for (int i = 0; i < k; i++) {
            result[i] = maxHeap.poll();
        }
        return result;
    }

    /**
     * Approach 2: QuickSelect (average O(n))
     */

    public static void main(String[] args) {
        KthClosestPoints kcp = new KthClosestPoints();

        int[][] r1 = kcp.kClosest(new int[][] { { 1, 3 }, { -2, 2 } }, 1);
        System.out.println("Test 1: " + Arrays.deepToString(r1) + " (expected: [[-2, 2]])");

        int[][] r2 = kcp.kClosest(new int[][] { { 3, 3 }, { 5, -1 }, { -2, 4 } }, 2);
        System.out.println("Test 2: " + Arrays.deepToString(r2));
    }
}
