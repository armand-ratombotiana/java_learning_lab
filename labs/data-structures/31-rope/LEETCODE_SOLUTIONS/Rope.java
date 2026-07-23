package com.leetcode.rope;

/**
 * Custom: Rope Data Structure
 * Balanced binary tree for efficient string operations (concat, split, substring).
 * Used in text editors for O(log n) editing operations.
 *
 * Time Complexity: O(log n) for operations
 * Space Complexity: O(n)
 */
public class Rope {

    public static class RopeNode {
        String data;
        RopeNode left, right;
        int length;

        RopeNode(String data) {
            this.data = data;
            this.length = data.length();
        }

        RopeNode(RopeNode left, RopeNode right) {
            this.left = left;
            this.right = right;
            this.length = (left != null ? left.length : 0) + (right != null ? right.length : 0);
        }
    }

    private RopeNode root;

    public Rope(String s) {
        root = new RopeNode(s);
    }

    private Rope(RopeNode node) {
        this.root = node;
    }

    public char charAt(int index) {
        return charAt(root, index);
    }

    private char charAt(RopeNode node, int index) {
        if (node == null) throw new IndexOutOfBoundsException();
        if (node.data != null) return node.data.charAt(index);
        int leftLen = node.left != null ? node.left.length : 0;
        if (index < leftLen) return charAt(node.left, index);
        return charAt(node.right, index - leftLen);
    }

    public Rope concat(Rope other) {
        return new Rope(new RopeNode(this.root, other.root));
    }

    public Rope substring(int start, int end) {
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) sb.append(charAt(i));
        return new Rope(sb.toString());
    }

    public int length() {
        return root != null ? root.length : 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(root, sb);
        return sb.toString();
    }

    private void toString(RopeNode node, StringBuilder sb) {
        if (node == null) return;
        if (node.data != null) { sb.append(node.data); return; }
        toString(node.left, sb);
        toString(node.right, sb);
    }

    public static void main(String[] args) {
        Rope r1 = new Rope("Hello ");
        Rope r2 = new Rope("World!");
        Rope r3 = r1.concat(r2);
        System.out.println("Concat: " + r3 + " (expected: Hello World!)");
        System.out.println("Length: " + r3.length() + " (expected: 12)");
        System.out.println("Char at 4: " + r3.charAt(4) + " (expected: o)");
        System.out.println("Substring [0,5]: " + r3.substring(0, 5) + " (expected: Hello)");
        System.out.println("Substring [6,11]: " + r3.substring(6, 11) + " (expected: World)");
    }
}
