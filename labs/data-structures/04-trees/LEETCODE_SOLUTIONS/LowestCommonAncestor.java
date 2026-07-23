package com.leetcode.trees;

/**
 * LeetCode 236: Lowest Common Ancestor of a Binary Tree
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/
 *
 * Find the lowest common ancestor of two nodes in a binary tree.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(h)
 */
public class LowestCommonAncestor {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Recursive DFS
     * If root is p or q, return root. Search left and right.
     * If both sides return non-null, root is LCA.
     * Time: O(n), Space: O(h)
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) return root;

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null) return root;
        return left != null ? left : right;
    }

    public static void main(String[] args) {
        LowestCommonAncestor lca = new LowestCommonAncestor();

        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);

        TreeNode p = root.left; // 5
        TreeNode q = root.right; // 1
        System.out.println("LCA of 5 and 1: " + lca.lowestCommonAncestor(root, p, q).val + " (expected: 3)");

        p = root.left; // 5
        q = root.left.right.right; // 4
        System.out.println("LCA of 5 and 4: " + lca.lowestCommonAncestor(root, p, q).val + " (expected: 5)");

        // Same node
        System.out.println("LCA of same node: " + lca.lowestCommonAncestor(root, root.left.left, root.left.left).val + " (expected: 6)");
    }
}
