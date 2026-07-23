package com.leetcode.fenwick;

import java.util.*;

/**
 * LeetCode 315: Count of Smaller Numbers After Self (Fenwick Tree version)
 * https://leetcode.com/problems/count-of-smaller-numbers-after-self/
 *
 * For each element, count how many elements to the right are smaller.
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class CountSmallerFenwick {

    private static class FenwickTree {
        int[] bit;
        FenwickTree(int n) { bit = new int[n + 1]; }
        void add(int i, int delta) {
            i++;
            while (i < bit.length) {
                bit[i] += delta;
                i += i & -i;
            }
        }
        int sum(int i) {
            i++;
            int s = 0;
            while (i > 0) {
                s += bit[i];
                i -= i & -i;
            }
            return s;
        }
    }

    public List<Integer> countSmaller(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        Map<Integer, Integer> rank = new HashMap<>();
        for (int i = 0; i < n; i++) rank.put(sorted[i], i);

        FenwickTree ft = new FenwickTree(n);
        List<Integer> result = new ArrayList<>();

        for (int i = n - 1; i >= 0; i--) {
            int r = rank.get(nums[i]);
            result.add(ft.sum(r - 1));
            ft.add(r, 1);
        }
        Collections.reverse(result);
        return result;
    }

    public static void main(String[] args) {
        CountSmallerFenwick csf = new CountSmallerFenwick();
        System.out.println("Test 1: " + csf.countSmaller(new int[] { 5, 2, 6, 1 }) + " (expected: [2, 1, 1, 0])");
        System.out.println("Test 2: " + csf.countSmaller(new int[] { -1 }) + " (expected: [0])");
        System.out.println("Test 3: " + csf.countSmaller(new int[] { 2, 0, 1 }) + " (expected: [2, 0, 0])");
    }
}
