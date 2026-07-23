package com.leetcode.spatial;

/**
 * LeetCode 223: Rectangle Area
 * https://leetcode.com/problems/rectangle-area/
 *
 * Find the total area covered by two rectilinear rectangles.
 *
 * Time Complexity: O(1)
 * Space Complexity: O(1)
 */
public class RectangleArea {

    public int computeArea(int ax1, int ay1, int ax2, int ay2, int bx1, int by1, int bx2, int by2) {
        int areaA = (ax2 - ax1) * (ay2 - ay1);
        int areaB = (bx2 - bx1) * (by2 - by1);

        int overlapX = Math.max(0, Math.min(ax2, bx2) - Math.max(ax1, bx1));
        int overlapY = Math.max(0, Math.min(ay2, by2) - Math.max(ay1, by1));
        int overlap = overlapX * overlapY;

        return areaA + areaB - overlap;
    }

    public static void main(String[] args) {
        RectangleArea ra = new RectangleArea();
        System.out.println("Test 1: " + ra.computeArea(-3, 0, 3, 4, 0, -1, 9, 2) + " (expected: 45)");
        System.out.println("Test 2: " + ra.computeArea(-2, -2, 2, 2, -2, -2, 2, 2) + " (expected: 16)");
        System.out.println("Test 3: " + ra.computeArea(0, 0, 0, 0, -1, -1, 1, 1) + " (expected: 4)");
    }
}
