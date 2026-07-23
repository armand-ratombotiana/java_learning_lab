package com.leetcode.advancedtrees;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 173: Binary Search Tree Iterator
 * https://leetcode.com/problems/binary-search-tree-iterator/
 *
 * Implement an iterator for a BST that returns elements in order.
 * next() and hasNext() must run in O(1) average time and O(h) space.
 *
 * Time Complexity: O(1) average for next() and hasNext()
 * Space Complexity: O(h)
 */
public class BSTIterator {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    private final Deque<TreeNode> stack;

    /**
     * Approach: Controlled recursion with stack
     * Push all left children, pop and process right subtrees.
     */
    public BSTIterator(TreeNode root) {
        stack = new ArrayDeque<>();
        pushLeft(root);
    }

    private void pushLeft(TreeNode node) {
        while (node != null) {
            stack.push(node);
            node = node.left;
        }
    }

    public int next() {
        TreeNode node = stack.pop();
        pushLeft(node.right);
        return node.val;
    }

    public boolean hasNext() {
        return !stack.isEmpty();
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(7);
        root.left = new TreeNode(3);
        root.right = new TreeNode(15);
        root.right.left = new TreeNode(9);
        root.right.right = new TreeNode(20);

        BSTIterator it = new BSTIterator(root);
        System.out.print("BST Iterator: ");
        while (it.hasNext()) {
            System.out.print(it.next() + " ");
        }
        System.out.println("(expected: 3 7 9 15 20)");

        // Single node
        BSTIterator it2 = new BSTIterator(new TreeNode(1));
        System.out.println("Single next: " + it2.next() + " (expected: 1)");
        System.out.println("Single hasNext: " + it2.hasNext() + " (expected: false)");
    }
}
