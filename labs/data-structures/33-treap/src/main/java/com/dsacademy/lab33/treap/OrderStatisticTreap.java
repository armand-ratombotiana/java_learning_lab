package com.dsacademy.lab33.treap;

public class OrderStatisticTreap<K extends Comparable<K>> {

    private Node root;
    private final java.util.Random rng = new java.util.Random();

    private static final class Node {
        K key;
        int priority, size;
        Node left, right;

        Node(K key) {
            this.key = key;
            this.priority = new java.util.Random().nextInt();
            this.size = 1;
        }
    }

    private int size(Node n) {
        return n == null ? 0 : n.size;
    }

    private void update(Node n) {
        if (n != null) n.size = 1 + size(n.left) + size(n.right);
    }

    private Node rotateRight(Node p) {
        Node q = p.left;
        p.left = q.right;
        q.right = p;
        update(p);
        update(q);
        return q;
    }

    private Node rotateLeft(Node p) {
        Node q = p.right;
        p.right = q.left;
        q.left = p;
        update(p);
        update(q);
        return q;
    }

    public void insert(K key) {
        root = insert(root, key);
    }

    private Node insert(Node node, K key) {
        if (node == null) return new Node(key);
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key);
            if (node.left.priority < node.priority) node = rotateRight(node);
        } else if (cmp > 0) {
            node.right = insert(node.right, key);
            if (node.right.priority < node.priority) node = rotateLeft(node);
        }
        update(node);
        return node;
    }

    public void delete(K key) {
        root = delete(root, key);
    }

    private Node delete(Node node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = delete(node.left, key);
        } else if (cmp > 0) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            if (node.left.priority < node.right.priority) {
                node = rotateRight(node);
                node.right = delete(node.right, key);
            } else {
                node = rotateLeft(node);
                node.left = delete(node.left, key);
            }
        }
        update(node);
        return node;
    }

    public K kthSmallest(int k) {
        if (k < 1 || k > size(root)) throw new IllegalArgumentException("k out of range");
        return kthSmallest(root, k);
    }

    private K kthSmallest(Node node, int k) {
        int leftSize = size(node.left);
        if (k <= leftSize) return kthSmallest(node.left, k);
        if (k == leftSize + 1) return node.key;
        return kthSmallest(node.right, k - leftSize - 1);
    }

    public int rank(K key) {
        return rank(root, key);
    }

    private int rank(Node node, K key) {
        if (node == null) return 1;
        int cmp = key.compareTo(node.key);
        if (cmp <= 0) return rank(node.left, key);
        return 1 + size(node.left) + rank(node.right, key);
    }

    public boolean contains(K key) {
        Node node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp < 0) node = node.left;
            else if (cmp > 0) node = node.right;
            else return true;
        }
        return false;
    }

    public int size() {
        return size(root);
    }

    public boolean isEmpty() {
        return root == null;
    }
}
