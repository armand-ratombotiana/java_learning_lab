package com.leetcode.trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * LeetCode 94: Binary Tree Inorder Traversal
 * https://leetcode.com/problems/binary-tree-inorder-traversal/
 *
 * Given the root of a binary tree, return the inorder traversal (left, root, right).
 *
 * Time Complexity: O(n)
 * Space Complexity: O(h) where h is tree height
 */
public class InorderTraversal {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Approach 1: Recursive
     * Time: O(n), Space: O(h)
     */
    public List<Integer> inorderTraversalRecursive(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        helper(root, result);
        return result;
    }

    private void helper(TreeNode node, List<Integer> result) {
        if (node == null) return;
        helper(node.left, result);
        result.add(node.val);
        helper(node.right, result);
    }

    /**
     * Approach 2 (Optimal): Iterative using Stack
     * Time: O(n), Space: O(h)
     */
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            result.add(current.val);
            current = current.right;
        }
        return result;
    }

    public static void main(String[] args) {
        InorderTraversal it = new InorderTraversal();

        TreeNode root = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.left = new TreeNode(3);

        System.out.println("Recursive: " + it.inorderTraversalRecursive(root) + " (expected: [1, 3, 2])");
        System.out.println("Iterative: " + it.inorderTraversal(root) + " (expected: [1, 3, 2])");

        // Empty tree
        System.out.println("Empty: " + it.inorderTraversal(null) + " (expected: [])");

        // Single node
        System.out.println("Single: " + it.inorderTraversal(new TreeNode(42)) + " (expected: [42])");
    }
}
