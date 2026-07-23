package com.algorithms.searching;

/**
 * LeetCode 33: Search in Rotated Sorted Array
 * https://leetcode.com/problems/search-in-rotated-sorted-array/
 *
 * Search for a target in a rotated sorted array in O(log n) time.
 *
 * Time Complexity: O(log n)
 * Space Complexity: O(1)
 */
public class SearchRotated {

    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) return mid;

            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) right = mid - 1;
                else left = mid + 1;
            } else {
                if (target > nums[mid] && target <= nums[right]) left = mid + 1;
                else right = mid - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SearchRotated sr = new SearchRotated();
        System.out.println("Test 1: " + sr.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 0) + " (expected: 4)");
        System.out.println("Test 2: " + sr.search(new int[] { 4, 5, 6, 7, 0, 1, 2 }, 3) + " (expected: -1)");
        System.out.println("Test 3: " + sr.search(new int[] { 1 }, 0) + " (expected: -1)");
        System.out.println("Test 4: " + sr.search(new int[] { 1, 3 }, 3) + " (expected: 1)");
        System.out.println("Test 5: " + sr.search(new int[] { 3, 1 }, 3) + " (expected: 0)");
    }
}
