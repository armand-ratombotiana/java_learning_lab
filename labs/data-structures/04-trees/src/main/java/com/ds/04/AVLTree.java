package com.ds04;

import java.util.*;

/*
 * AVLTree - Self-balancing BST with balance factor maintenance.
 *
 * Time Complexity:
 * - insert: O(log n)
 * - delete: O(log n)
 * - search: O(log n)
 *
 * Space Complexity: O(n)
 */
public class AVLTree<T extends Comparable<T>> {

    public static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T data) {
            this.data = data;
            this.height = 1;
        }

        public T getData() { return data; }
        public Node<T> getLeft() { return left; }
        public Node<T> getRight() { return right; }
        public int getHeight() { return height; }
    }

    private Node<T> root;

    public AVLTree() {}

    public Node<T> getRoot() { return root; }

    public void insert(T value) {
        root = insert(root, value);
    }

    private Node<T> insert(Node<T> node, T value) {
        if (node == null) return new Node<>(value);
        int cmp = value.compareTo(node.data);
        if (cmp < 0) {
            node.left = insert(node.left, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, value);
        } else {
            return node;
        }
        node.height = 1 + Math.max(height(node.left), height(node.right));
        return balance(node, value);
    }

    public void delete(T value) {
        root = delete(root, value);
    }

    private Node<T> delete(Node<T> node, T value) {
        if (node == null) return null;
        int cmp = value.compareTo(node.data);
        if (cmp < 0) {
            node.left = delete(node.left, value);
        } else if (cmp > 0) {
            node.right = delete(node.right, value);
        } else {
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            Node<T> successor = findMin(node.right);
            node.data = successor.data;
            node.right = delete(node.right, successor.data);
        }
        node.height = 1 + Math.max(height(node.left), height(node.right));
        return rebalance(node);
    }

    public boolean search(T value) {
        return search(root, value) != null;
    }

    private Node<T> search(Node<T> node, T value) {
        if (node == null || value.compareTo(node.data) == 0) return node;
        if (value.compareTo(node.data) < 0) return search(node.left, value);
        return search(node.right, value);
    }

    private Node<T> findMin(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }

    private int balanceFactor(Node<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node<T> balance(Node<T> node, T value) {
        int bf = balanceFactor(node);
        if (bf > 1 && value.compareTo(node.left.data) < 0) {
            return rotateRight(node);
        }
        if (bf < -1 && value.compareTo(node.right.data) > 0) {
            return rotateLeft(node);
        }
        if (bf > 1 && value.compareTo(node.left.data) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (bf < -1 && value.compareTo(node.right.data) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private Node<T> rebalance(Node<T> node) {
        int bf = balanceFactor(node);
        if (bf > 1 && balanceFactor(node.left) >= 0) return rotateRight(node);
        if (bf > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (bf < -1 && balanceFactor(node.right) <= 0) return rotateLeft(node);
        if (bf < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        Node<T> T2 = x.right;
        x.right = y;
        y.left = T2;
        y.height = 1 + Math.max(height(y.left), height(y.right));
        x.height = 1 + Math.max(height(x.left), height(x.right));
        return x;
    }

    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        Node<T> T2 = y.left;
        y.left = x;
        x.right = T2;
        x.height = 1 + Math.max(height(x.left), height(x.right));
        y.height = 1 + Math.max(height(y.left), height(y.right));
        return y;
    }

    public boolean isAVL() {
        return isAVL(root);
    }

    private boolean isAVL(Node<T> node) {
        if (node == null) return true;
        int bf = balanceFactor(node);
        if (Math.abs(bf) > 1) return false;
        return isAVL(node.left) && isAVL(node.right);
    }

    public List<T> inorder() {
        List<T> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    private void inorder(Node<T> node, List<T> result) {
        if (node == null) return;
        inorder(node.left, result);
        result.add(node.data);
        inorder(node.right, result);
    }

    public List<T> levelOrder() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;
        Queue<Node<T>> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            Node<T> node = queue.poll();
            result.add(node.data);
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        return result;
    }
}
