package com.ds04;

import java.util.*;

/*
 * BinarySearchTree - BST with insert, search, delete, and traversals.
 *
 * Time Complexity:
 * - insert: O(log n) average, O(n) worst
 * - search: O(log n) average, O(n) worst
 * - delete: O(log n) average, O(n) worst
 *
 * Space Complexity: O(n)
 */
public class BinarySearchTree<T extends Comparable<T>> extends BinaryTree<T> {

    public BinarySearchTree() {}

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
        }
        return node;
    }

    public boolean search(T value) {
        return search(root, value) != null;
    }

    private Node<T> search(Node<T> node, T value) {
        if (node == null || value.compareTo(node.data) == 0) return node;
        if (value.compareTo(node.data) < 0) return search(node.left, value);
        return search(node.right, value);
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
        return node;
    }

    public T findMin() {
        if (root == null) throw new NoSuchElementException("Tree is empty");
        return findMin(root).data;
    }

    private Node<T> findMin(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public T findMax() {
        if (root == null) throw new NoSuchElementException("Tree is empty");
        Node<T> node = root;
        while (node.right != null) node = node.right;
        return node.data;
    }

    public boolean isBST() {
        return isBST(root, null, null);
    }

    private boolean isBST(Node<T> node, T min, T max) {
        if (node == null) return true;
        if ((min != null && node.data.compareTo(min) <= 0) ||
            (max != null && node.data.compareTo(max) >= 0)) return false;
        return isBST(node.left, min, node.data) && isBST(node.right, node.data, max);
    }
}
