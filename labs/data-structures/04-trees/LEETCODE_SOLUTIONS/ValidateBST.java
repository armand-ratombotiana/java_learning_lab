package com.leetcode.trees;

/**
 * LeetCode 98: Validate Binary Search Tree
 * https://leetcode.com/problems/validate-binary-search-tree/
 *
 * Given the root of a binary tree, determine if it is a valid BST.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 */
public class ValidateBST {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Range validation with recursion
     * Each node must be within (min, max) bounds.
     * Time: O(n), Space: O(h)
     */
    public boolean isValidBST(TreeNode root) {
        return validate(root, null, null);
    }

    private boolean validate(TreeNode node, Integer min, Integer max) {
        if (node == null) return true;
        if (min != null && node.val <= min) return false;
        if (max != null && node.val >= max) return false;
        return validate(node.left, min, node.val) && validate(node.right, node.val, max);
    }

    /**
     * Approach 2: Inorder traversal check
     * BST inorder is strictly increasing. Track the previous value.
     * Time: O(n), Space: O(h)
     */

    public static void main(String[] args) {
        ValidateBST vbst = new ValidateBST();

        // Valid: 2(1, 3)
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);
        System.out.println("Test 1: " + vbst.isValidBST(root1) + " (expected: true)");

        // Invalid: 5(1, 4) with 4(3, 6)
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.right.left = new TreeNode(3);
        root2.right.right = new TreeNode(6);
        System.out.println("Test 2: " + vbst.isValidBST(root2) + " (expected: false)");

        // Empty
        System.out.println("Test 3: " + vbst.isValidBST(null) + " (expected: true)");

        // Single
        System.out.println("Test 4: " + vbst.isValidBST(new TreeNode(1)) + " (expected: true)");

        // Duplicate (should fail)
        TreeNode root5 = new TreeNode(1);
        root5.left = new TreeNode(1);
        System.out.println("Test 5: " + vbst.isValidBST(root5) + " (expected: false)");
    }
}
