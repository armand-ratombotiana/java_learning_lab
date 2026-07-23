package com.algorithms.searching;

/**
 * LeetCode 704: Binary Search
 * https://leetcode.com/problems/binary-search/
 *
 * Given a sorted array of integers, return the index of the target value.
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */
public class BinarySearch {

    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;
            if (nums[mid] < target) left = mid + 1;
            else right = mid - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        BinarySearch bs = new BinarySearch();
        System.out.println("Test 1: " + bs.search(new int[] { -1, 0, 3, 5, 9, 12 }, 9) + " (expected: 4)");
        System.out.println("Test 2: " + bs.search(new int[] { -1, 0, 3, 5, 9, 12 }, 2) + " (expected: -1)");
        System.out.println("Test 3: " + bs.search(new int[] { 5 }, 5) + " (expected: 0)");
        System.out.println("Test 4: " + bs.search(new int[] { 1, 2, 3, 4, 5 }, 1) + " (expected: 0)");
        System.out.println("Test 5: " + bs.search(new int[] {}, 1) + " (expected: -1)");
    }
}
