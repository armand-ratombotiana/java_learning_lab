package com.algorithms.searching;

import java.util.Arrays;

/**
 * LeetCode 34: Find First and Last Position of Element in Sorted Array
 * https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
 *
 * Given a sorted array, find the first and last position of a target.
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */
public class FirstLastPosition {

    public int[] searchRange(int[] nums, int target) {
        int first = findBound(nums, target, true);
        int last = findBound(nums, target, false);
        return new int[] { first, last };
    }

    private int findBound(int[] nums, int target, boolean isFirst) {
        int left = 0, right = nums.length - 1;
        int bound = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                bound = mid;
                if (isFirst) right = mid - 1;
                else left = mid + 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return bound;
    }

    public static void main(String[] args) {
        FirstLastPosition flp = new FirstLastPosition();
        System.out.println("Test 1: " + Arrays.toString(flp.searchRange(new int[] { 5, 7, 7, 8, 8, 10 }, 8)) + " (expected: [3, 4])");
        System.out.println("Test 2: " + Arrays.toString(flp.searchRange(new int[] { 5, 7, 7, 8, 8, 10 }, 6)) + " (expected: [-1, -1])");
        System.out.println("Test 3: " + Arrays.toString(flp.searchRange(new int[] {}, 0)) + " (expected: [-1, -1])");
        System.out.println("Test 4: " + Arrays.toString(flp.searchRange(new int[] { 1, 1, 1, 1, 1 }, 1)) + " (expected: [0, 4])");
        System.out.println("Test 5: " + Arrays.toString(flp.searchRange(new int[] { 2, 2 }, 2)) + " (expected: [0, 1])");
    }
}
