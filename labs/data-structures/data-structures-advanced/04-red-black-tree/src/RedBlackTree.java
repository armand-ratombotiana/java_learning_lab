package com.ds.advanced.lab04;

public class RedBlackTree {
    private static final boolean RED = true, BLACK = false;

    private static class Node {
        int val; Node left, right, parent; boolean color;
        Node(int val) { this.val = val; this.color = RED; }
    }

    private Node root;

    public void insert(int val) {
        Node node = new Node(val);
        root = bstInsert(root, node);
        fixInsert(node);
    }

    private Node bstInsert(Node root, Node node) {
        if (root == null) return node;
        if (node.val < root.val) { root.left = bstInsert(root.left, node); root.left.parent = root; }
        else { root.right = bstInsert(root.right, node); root.right.parent = root; }
        return root;
    }

    private void fixInsert(Node node) {
        while (node != root && node.parent.color == RED) {
            Node parent = node.parent;
            Node grand = parent.parent;
            if (parent == grand.left) {
                Node uncle = grand.right;
                if (uncle != null && uncle.color == RED) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    grand.color = RED;
                    node = grand;
                } else {
                    if (node == parent.right) { node = parent; leftRotate(node); }
                    parent.color = BLACK;
                    grand.color = RED;
                    rightRotate(grand);
                }
            } else {
                Node uncle = grand.left;
                if (uncle != null && uncle.color == RED) {
                    parent.color = BLACK;
                    uncle.color = BLACK;
                    grand.color = RED;
                    node = grand;
                } else {
                    if (node == parent.left) { node = parent; rightRotate(node); }
                    parent.color = BLACK;
                    grand.color = RED;
                    leftRotate(grand);
                }
            }
        }
        root.color = BLACK;
    }

    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.left) x.parent.left = y;
        else x.parent.right = y;
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null) y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null) root = y;
        else if (x == x.parent.right) x.parent.right = y;
        else x.parent.left = y;
        y.right = x;
        x.parent = y;
    }

    public boolean search(int val) {
        Node cur = root;
        while (cur != null) {
            if (val == cur.val) return true;
            cur = val < cur.val ? cur.left : cur.right;
        }
        return false;
    }

    public void delete(int val) {
        Node node = searchNode(root, val);
        if (node == null) return;
        deleteNode(node);
    }

    private Node searchNode(Node root, int val) {
        while (root != null) {
            if (val == root.val) return root;
            root = val < root.val ? root.left : root.right;
        }
        return null;
    }

    private void deleteNode(Node node) {
        Node u = node, v;
        boolean uOrigColor = u.color;
        if (node.left == null) { v = node.right; transplant(node, node.right); }
        else if (node.right == null) { v = node.left; transplant(node, node.left); }
        else {
            u = minimum(node.right);
            uOrigColor = u.color;
            v = u.right;
            if (u.parent == node) { if (v != null) v.parent = u; }
            else { transplant(u, u.right); u.right = node.right; u.right.parent = u; }
            transplant(node, u);
            u.left = node.left;
            u.left.parent = u;
            u.color = node.color;
        }
        if (uOrigColor == BLACK) fixDelete(v);
    }

    private void fixDelete(Node node) {
        while (node != root && (node == null || node.color == BLACK)) {
            if (node == null) break;
            Node parent = node.parent;
            if (node == parent.left) {
                Node sib = parent.right;
                if (sib.color == RED) { sib.color = BLACK; parent.color = RED; leftRotate(parent); sib = parent.right; }
                if ((sib.left == null || sib.left.color == BLACK) && (sib.right == null || sib.right.color == BLACK)) {
                    sib.color = RED; node = parent;
                } else {
                    if (sib.right == null || sib.right.color == BLACK) { if (sib.left != null) sib.left.color = BLACK; sib.color = RED; rightRotate(sib); sib = parent.right; }
                    sib.color = parent.color; parent.color = BLACK;
                    if (sib.right != null) sib.right.color = BLACK;
                    leftRotate(parent); node = root;
                }
            } else {
                Node sib = parent.left;
                if (sib.color == RED) { sib.color = BLACK; parent.color = RED; rightRotate(parent); sib = parent.left; }
                if ((sib.left == null || sib.left.color == BLACK) && (sib.right == null || sib.right.color == BLACK)) {
                    sib.color = RED; node = parent;
                } else {
                    if (sib.left == null || sib.left.color == BLACK) { if (sib.right != null) sib.right.color = BLACK; sib.color = RED; leftRotate(sib); sib = parent.left; }
                    sib.color = parent.color; parent.color = BLACK;
                    if (sib.left != null) sib.left.color = BLACK;
                    rightRotate(parent); node = root;
                }
            }
        }
        if (node != null) node.color = BLACK;
    }

    private Node minimum(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private void transplant(Node u, Node v) {
        if (u.parent == null) root = v;
        else if (u == u.parent.left) u.parent.left = v;
        else u.parent.right = v;
        if (v != null) v.parent = u.parent;
    }
}