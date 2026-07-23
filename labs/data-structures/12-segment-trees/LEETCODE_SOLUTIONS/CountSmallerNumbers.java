package com.leetcode.segmenttree;

import java.util.*;

/**
 * LeetCode 315: Count of Smaller Numbers After Self
 * https://leetcode.com/problems/count-of-smaller-numbers-after-self/
 *
 * For each element, count how many elements to the right are smaller.
 *
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class CountSmallerNumbers {

    public static class SegmentTree {
        int[] tree;
        int n;
        SegmentTree(int n) {
            this.n = n;
            tree = new int[4 * n];
        }
        void update(int idx, int l, int r, int pos) {
            if (l == r) { tree[idx]++; return; }
            int mid = l + (r - l) / 2;
            if (pos <= mid) update(2 * idx, l, mid, pos);
            else update(2 * idx + 1, mid + 1, r, pos);
            tree[idx] = tree[2 * idx] + tree[2 * idx + 1];
        }
        int query(int idx, int l, int r, int ql, int qr) {
            if (ql > r || qr < l) return 0;
            if (ql <= l && r <= qr) return tree[idx];
            int mid = l + (r - l) / 2;
            return query(2 * idx, l, mid, ql, qr) + query(2 * idx + 1, mid + 1, r, ql, qr);
        }
    }

    public List<Integer> countSmaller(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        Arrays.sort(sorted);
        Map<Integer, Integer> rank = new HashMap<>();
        for (int i = 0; i < n; i++) rank.put(sorted[i], i);

        List<Integer> result = new ArrayList<>();
        SegmentTree st = new SegmentTree(n);
        for (int i = n - 1; i >= 0; i--) {
            int r = rank.get(nums[i]);
            result.add(st.query(1, 0, n - 1, 0, r - 1));
            st.update(1, 0, n - 1, r);
        }
        Collections.reverse(result);
        return result;
    }

    public static void main(String[] args) {
        CountSmallerNumbers csn = new CountSmallerNumbers();
        System.out.println("Test 1: " + csn.countSmaller(new int[] { 5, 2, 6, 1 }) + " (expected: [2, 1, 1, 0])");
        System.out.println("Test 2: " + csn.countSmaller(new int[] { -1, -1 }) + " (expected: [0, 0])");
        System.out.println("Test 3: " + csn.countSmaller(new int[] { 1 }) + " (expected: [0])");
    }
}
