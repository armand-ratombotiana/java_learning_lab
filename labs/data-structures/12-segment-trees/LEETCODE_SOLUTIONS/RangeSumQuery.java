package com.leetcode.segmenttree;

/**
 * LeetCode 307: Range Sum Query - Mutable
 * https://leetcode.com/problems/range-sum-query-mutable/
 *
 * Support updating an element and querying the sum of a range.
 *
 * Time Complexity: O(log n) for both update and sumRange
 * Space Complexity: O(n)
 */
public class RangeSumQuery {

    private final int[] tree;
    private final int n;

    /**
     * Approach: Segment Tree (array-based)
     * Leaf nodes at indices [n, 2n-1], internal nodes build up the tree.
     */
    public RangeSumQuery(int[] nums) {
        n = nums.length;
        tree = new int[2 * n];
        for (int i = 0; i < n; i++) tree[n + i] = nums[i];
        for (int i = n - 1; i > 0; i--) tree[i] = tree[2 * i] + tree[2 * i + 1];
    }

    public void update(int index, int val) {
        int pos = index + n;
        tree[pos] = val;
        while (pos > 1) {
            pos /= 2;
            tree[pos] = tree[2 * pos] + tree[2 * pos + 1];
        }
    }

    public int sumRange(int left, int right) {
        int l = left + n, r = right + n;
        int sum = 0;
        while (l <= r) {
            if ((l & 1) == 1) sum += tree[l++];
            if ((r & 1) == 0) sum += tree[r--];
            l /= 2;
            r /= 2;
        }
        return sum;
    }

    public static void main(String[] args) {
        RangeSumQuery rsq = new RangeSumQuery(new int[] { 1, 3, 5 });
        System.out.println("Sum [0,2]: " + rsq.sumRange(0, 2) + " (expected: 9)");
        rsq.update(1, 2);
        System.out.println("Sum [0,2] after update: " + rsq.sumRange(0, 2) + " (expected: 8)");

        // Single element
        RangeSumQuery rsq2 = new RangeSumQuery(new int[] { 5 });
        System.out.println("Sum [0,0]: " + rsq2.sumRange(0, 0) + " (expected: 5)");
        rsq2.update(0, 10);
        System.out.println("Sum [0,0] after update: " + rsq2.sumRange(0, 0) + " (expected: 10)");
    }
}
