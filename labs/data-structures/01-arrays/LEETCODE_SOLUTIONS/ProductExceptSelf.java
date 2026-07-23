package com.leetcode.arrays;

/**
 * LeetCode 238: Product of Array Except Self
 * https://leetcode.com/problems/product-of-array-except-self/
 *
 * Given an integer array nums, return an array answer such that
 * answer[i] is equal to the product of all the elements of nums except nums[i].
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1) (excluding output array)
 */
public class ProductExceptSelf {

    /**
     * Approach 1 (Optimal): Prefix and Suffix Products
     * Compute prefix product, then multiply by suffix product in a single pass.
     * Time: O(n), Space: O(1) extra
     */
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }

        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i];
        }

        return result;
    }

    /**
     * Approach 2: Prefix and Suffix arrays
     * Two separate arrays for prefix and suffix products.
     * Time: O(n), Space: O(n)
     */
    public int[] productExceptSelfWithExtraSpace(int[] nums) {
        int n = nums.length;
        int[] prefix = new int[n];
        int[] suffix = new int[n];
        int[] result = new int[n];

        prefix[0] = 1;
        for (int i = 1; i < n; i++) {
            prefix[i] = prefix[i - 1] * nums[i - 1];
        }

        suffix[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            suffix[i] = suffix[i + 1] * nums[i + 1];
        }

        for (int i = 0; i < n; i++) {
            result[i] = prefix[i] * suffix[i];
        }

        return result;
    }

    public static void main(String[] args) {
        ProductExceptSelf pes = new ProductExceptSelf();

        int[] r1 = pes.productExceptSelf(new int[] { 1, 2, 3, 4 });
        System.out.print("Test 1: ");
        printArray(r1);
        System.out.println(" (expected: [24, 12, 8, 6])");

        int[] r2 = pes.productExceptSelf(new int[] { -1, 1, 0, -3, 3 });
        System.out.print("Test 2: ");
        printArray(r2);
        System.out.println(" (expected: [0, 0, 9, 0, 0])");
    }

    private static void printArray(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        sb.append("]");
        System.out.print(sb.toString());
    }
}
