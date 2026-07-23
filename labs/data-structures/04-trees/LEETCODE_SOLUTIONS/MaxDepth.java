package com.leetcode.trees;

import java.util.LinkedList;
import java.util.Queue;

/**
 * LeetCode 104: Maximum Depth of Binary Tree
 * https://leetcode.com/problems/maximum-depth-of-binary-tree/
 *
 * Given the root of a binary tree, return its maximum depth.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(h) for recursion, O(w) for BFS
 */
public class MaxDepth {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Recursive DFS
     * Time: O(n), Space: O(h)
     */
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }

    /**
     * Approach 2: Iterative BFS (Level Order)
     * Count levels using a queue.
     * Time: O(n), Space: O(w) where w is max width
     */
    public int maxDepthBFS(TreeNode root) {
        if (root == null) return 0;
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
            depth++;
        }
        return depth;
    }

    public static void main(String[] args) {
        MaxDepth md = new MaxDepth();

        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);

        System.out.println("DFS: " + md.maxDepth(root) + " (expected: 3)");
        System.out.println("BFS: " + md.maxDepthBFS(root) + " (expected: 3)");

        // Empty
        System.out.println("Empty: " + md.maxDepth(null) + " (expected: 0)");

        // Single
        System.out.println("Single: " + md.maxDepth(new TreeNode(1)) + " (expected: 1)");
    }
}
