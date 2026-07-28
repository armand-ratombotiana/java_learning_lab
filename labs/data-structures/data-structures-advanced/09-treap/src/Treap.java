package com.ds.advanced.lab09;

import java.util.Random;

public class Treap {
    private static class Node {
        int val, prio, size;
        Node left, right;
        Node(int val) { this.val = val; this.prio = new Random().nextInt(); this.size = 1; }
    }

    private Node root;
    private final Random rand = new Random();

    private int size(Node n) { return n == null ? 0 : n.size; }
    private void upd(Node n) { if (n != null) n.size = 1 + size(n.left) + size(n.right); }

    private Node rotateRight(Node p) {
        Node q = p.left;
        p.left = q.right;
        q.right = p;
        upd(p); upd(q);
        return q;
    }

    private Node rotateLeft(Node p) {
        Node q = p.right;
        p.right = q.left;
        q.left = p;
        upd(p); upd(q);
        return q;
    }

    public void insert(int val) { root = insert(root, val); }

    private Node insert(Node node, int val) {
        if (node == null) return new Node(val);
        if (val < node.val) {
            node.left = insert(node.left, val);
            if (node.left.prio < node.prio) node = rotateRight(node);
        } else {
            node.right = insert(node.right, val);
            if (node.right.prio < node.prio) node = rotateLeft(node);
        }
        upd(node);
        return node;
    }

    public boolean search(int val) {
        Node cur = root;
        while (cur != null) {
            if (val == cur.val) return true;
            cur = val < cur.val ? cur.left : cur.right;
        }
        return false;
    }

    public void delete(int val) { root = delete(root, val); }

    private Node delete(Node node, int val) {
        if (node == null) return null;
        if (val == node.val) return merge(node.left, node.right);
        if (val < node.val) node.left = delete(node.left, val);
        else node.right = delete(node.right, val);
        upd(node);
        return node;
    }

    private Node merge(Node a, Node b) {
        if (a == null || b == null) return a == null ? b : a;
        if (a.prio < b.prio) { a.right = merge(a.right, b); upd(a); return a; }
        else { b.left = merge(a, b.left); upd(b); return b; }
    }

    public void split(int key, Treap leftOut, Treap rightOut) {
        Node[] pair = split(root, key);
        leftOut.root = pair[0];
        rightOut.root = pair[1];
    }

    private Node[] split(Node node, int key) {
        if (node == null) return new Node[]{null, null};
        if (node.val <= key) {
            Node[] pair = split(node.right, key);
            node.right = pair[0];
            upd(node);
            return new Node[]{node, pair[1]};
        } else {
            Node[] pair = split(node.left, key);
            node.left = pair[1];
            upd(node);
            return new Node[]{pair[0], node};
        }
    }
}