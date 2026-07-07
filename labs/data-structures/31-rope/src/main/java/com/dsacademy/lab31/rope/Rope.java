package com.dsacademy.lab31.rope;

import java.util.ArrayDeque;
import java.util.Deque;

public class Rope {

    private RopeNode root;

    public Rope() {
        this.root = null;
    }

    public Rope(String initial) {
        if (initial == null || initial.isEmpty()) {
            this.root = null;
        } else {
            this.root = new RopeNode(initial);
        }
    }

    private Rope(RopeNode root) {
        this.root = root;
    }

    public int length() {
        return root == null ? 0 : root.getLength();
    }

    public char charAt(int index) {
        if (root == null || index < 0 || index >= root.getLength()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length());
        }
        return charAt(root, index);
    }

    private char charAt(RopeNode node, int index) {
        if (node.isLeaf()) {
            return node.getData().charAt(index);
        }
        if (index < node.getWeight()) {
            return charAt(node.getLeft(), index);
        }
        return charAt(node.getRight(), index - node.getWeight());
    }

    public String substring(int start, int end) {
        if (root == null || start < 0 || end > root.getLength() || start >= end) {
            if (start == end) return "";
            throw new IndexOutOfBoundsException("Start: " + start + ", End: " + end + ", Length: " + length());
        }
        StringBuilder sb = new StringBuilder(end - start);
        substring(root, start, end, sb);
        return sb.toString();
    }

    private void substring(RopeNode node, int start, int end, StringBuilder sb) {
        if (start >= end) return;
        if (node.isLeaf()) {
            sb.append(node.getData(), start, end);
            return;
        }
        int leftLen = node.getWeight();
        if (start < leftLen) {
            int split = Math.min(end, leftLen);
            substring(node.getLeft(), start, split, sb);
        }
        if (end > leftLen) {
            int split = Math.max(start, leftLen);
            substring(node.getRight(), split - leftLen, end - leftLen, sb);
        }
    }

    public Rope concat(Rope other) {
        if (this.root == null) return other;
        if (other.root == null) return this;
        return new Rope(new RopeNode(this.root, other.root));
    }

    public Rope split(int index) {
        if (root == null || index < 0 || index > root.getLength()) {
            throw new IndexOutOfBoundsException("Split index: " + index + ", Length: " + length());
        }
        RopeNode[] parts = split(root, index);
        Rope left = new Rope(parts[0]);
        this.root = parts[1];
        return left;
    }

    private RopeNode[] split(RopeNode node, int index) {
        if (node.isLeaf()) {
            String leftStr = node.getData().substring(0, index);
            String rightStr = node.getData().substring(index);
            return new RopeNode[] {
                leftStr.isEmpty() ? null : new RopeNode(leftStr),
                rightStr.isEmpty() ? null : new RopeNode(rightStr)
            };
        }
        int leftLen = node.getWeight();
        if (index < leftLen) {
            RopeNode[] leftParts = split(node.getLeft(), index);
            RopeNode rightPart = node.getRight();
            RopeNode merged = merge(leftParts[1], rightPart);
            return new RopeNode[] { leftParts[0], merged };
        } else if (index > leftLen) {
            RopeNode[] rightParts = split(node.getRight(), index - leftLen);
            RopeNode merged = merge(node.getLeft(), rightParts[0]);
            return new RopeNode[] { merged, rightParts[1] };
        }
        return new RopeNode[] { node.getLeft(), node.getRight() };
    }

    private RopeNode merge(RopeNode left, RopeNode right) {
        if (left == null) return right;
        if (right == null) return left;
        return new RopeNode(left, right);
    }

    public void insert(int index, String s) {
        if (s == null || s.isEmpty()) return;
        Rope left = split(index);
        Rope mid = new Rope(s);
        Rope right = new Rope(this.root);
        this.root = left.concat(mid).concat(right).root;
    }

    public void delete(int start, int end) {
        if (start >= end) return;
        Rope left = split(start);
        if (this.root == null) {
            this.root = left.root;
            return;
        }
        split(end - start);
        Rope right = new Rope(this.root);
        this.root = left.concat(right).root;
    }

    public RopeNode getRoot() {
        return root;
    }

    public String toString() {
        if (root == null) return "";
        StringBuilder sb = new StringBuilder(root.getLength());
        toString(root, sb);
        return sb.toString();
    }

    private void toString(RopeNode node, StringBuilder sb) {
        if (node.isLeaf()) {
            sb.append(node.getData());
            return;
        }
        toString(node.getLeft(), sb);
        toString(node.getRight(), sb);
    }

    public int indexOf(String s) {
        if (s == null || s.isEmpty()) return 0;
        String full = toString();
        return full.indexOf(s);
    }

    public int balanceFactor() {
        if (root == null) return 0;
        return balanceFactor(root);
    }

    private int balanceFactor(RopeNode node) {
        if (node.isLeaf()) return 0;
        int leftHeight = height(node.getLeft());
        int rightHeight = height(node.getRight());
        return leftHeight - rightHeight;
    }

    private int height(RopeNode node) {
        if (node == null) return 0;
        if (node.isLeaf()) return 1;
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }
}
