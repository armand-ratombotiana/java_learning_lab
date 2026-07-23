package com.leetcode.trees;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * LeetCode 297: Serialize and Deserialize Binary Tree
 * https://leetcode.com/problems/serialize-and-deserialize-binary-tree/
 *
 * Design an algorithm to serialize and deserialize a binary tree.
 *
 * Time Complexity: O(n) for both operations
 * Space Complexity: O(n)
 */
public class SerializeDeserializeTree {

    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode(int val) { this.val = val; }
    }

    private static final String NULL = "null";
    private static final String SEP = ",";

    /**
     * Approach 1: Preorder traversal (DFS)
     * Serialize: root, left, right. Use "null" for null nodes.
     * Deserialize: Reconstruct preorder using a queue.
     */

    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append(NULL).append(SEP);
            return;
        }
        sb.append(node.val).append(SEP);
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    public TreeNode deserialize(String data) {
        Deque<String> queue = new ArrayDeque<>(Arrays.asList(data.split(SEP)));
        return deserializeHelper(queue);
    }

    private TreeNode deserializeHelper(Deque<String> queue) {
        String val = queue.poll();
        if (val.equals(NULL)) return null;
        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }

    public static void main(String[] args) {
        SerializeDeserializeTree sd = new SerializeDeserializeTree();

        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.right.left = new TreeNode(4);
        root.right.right = new TreeNode(5);

        String serialized = sd.serialize(root);
        System.out.println("Serialized: " + serialized + " (expected: 1,2,null,null,3,4,null,null,5,null,null,)");

        TreeNode deserialized = sd.deserialize(serialized);
        System.out.println("Deserialized root: " + deserialized.val + " (expected: 1)");
        System.out.println("Deserialized left: " + deserialized.left.val + " (expected: 2)");
        System.out.println("Deserialized right: " + deserialized.right.val + " (expected: 3)");

        // Empty tree
        String emptySer = sd.serialize(null);
        System.out.println("Empty serialized: " + emptySer);
        TreeNode emptyDeser = sd.deserialize(emptySer);
        System.out.println("Empty deserialized: " + (emptyDeser == null ? "null" : "not null"));
    }
}
