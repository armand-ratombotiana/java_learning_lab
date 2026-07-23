package com.leetcode.fenwick;

/**
 * LeetCode 307: Range Sum Query - Mutable (Fenwick Tree version)
 * https://leetcode.com/problems/range-sum-query-mutable/
 *
 * Support updating an element and querying the sum of a range using a BIT.
 *
 * Time Complexity: O(log n) for both update and sumRange
 * Space Complexity: O(n)
 */
public class RangeSumQueryFenwick {

    private final int[] bit;
    private final int[] nums;
    private final int n;

    public RangeSumQueryFenwick(int[] nums) {
        this.n = nums.length;
        this.nums = new int[n];
        this.bit = new int[n + 1];
        for (int i = 0; i < n; i++) {
            update(i, nums[i]);
            this.nums[i] = nums[i];
        }
    }

    private void add(int index, int delta) {
        int i = index + 1;
        while (i <= n) {
            bit[i] += delta;
            i += i & -i;
        }
    }

    private int prefixSum(int index) {
        int i = index + 1;
        int sum = 0;
        while (i > 0) {
            sum += bit[i];
            i -= i & -i;
        }
        return sum;
    }

    public void update(int index, int val) {
        int delta = val - nums[index];
        nums[index] = val;
        add(index, delta);
    }

    public int sumRange(int left, int right) {
        return prefixSum(right) - prefixSum(left - 1);
    }

    public static void main(String[] args) {
        RangeSumQueryFenwick rsq = new RangeSumQueryFenwick(new int[] { 1, 3, 5 });
        System.out.println("Sum [0,2]: " + rsq.sumRange(0, 2) + " (expected: 9)");
        rsq.update(1, 2);
        System.out.println("Sum [0,2] after update: " + rsq.sumRange(0, 2) + " (expected: 8)");

        // Single element
        RangeSumQueryFenwick rsq2 = new RangeSumQueryFenwick(new int[] { 7 });
        System.out.println("Sum [0,0]: " + rsq2.sumRange(0, 0) + " (expected: 7)");
    }
}
