package com.leetcode.advancedtrees;

/**
 * LeetCode 114: Flatten Binary Tree to Linked List
 * https://leetcode.com/problems/flatten-binary-tree-to-linked-list/
 *
 * Given the root of a binary tree, flatten it into a linked list in-place
 * following the preorder traversal order.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 */
public class FlattenTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    private TreeNode prev = null;

    /**
     * Approach 1 (Optimal): Reverse Postorder (right, left, root)
     * Process right first, then left. Connect current node's right to prev.
     * Time: O(n), Space: O(h)
     */
    public void flatten(TreeNode root) {
        if (root == null) return;
        flatten(root.right);
        flatten(root.left);
        root.right = prev;
        root.left = null;
        prev = root;
    }

    /**
     * Approach 2: Iterative (Morris-like)
     * For each node, find the rightmost node in the left subtree,
     * connect it to the right subtree, then move left to right.
     */

    public static void main(String[] args) {
        FlattenTree ft = new FlattenTree();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(4);
        root.right.right = new TreeNode(6);

        ft.flatten(root);

        System.out.print("Flattened: ");
        TreeNode cur = root;
        while (cur != null) {
            System.out.print(cur.val + " ");
            System.out.print("(left=" + (cur.left == null ? "null" : cur.left.val) + ") ");
            cur = cur.right;
        }
        System.out.println("(expected: 1 2 3 4 5 6 with all left=null)");

        // Single node
        ft.prev = null;
        TreeNode single = new TreeNode(0);
        ft.flatten(single);
        System.out.println("Single: " + single.val + " left=" + (single.left == null ? "null" : "not null"));
    }
}
