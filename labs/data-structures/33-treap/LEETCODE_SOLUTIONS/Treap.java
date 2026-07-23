package com.leetcode.treap;

import java.util.Random;

/**
 * Custom: Treap (Randomized BST)
 * A binary search tree with heap property based on random priorities.
 *
 * Time Complexity: O(log n) average per operation
 * Space Complexity: O(n)
 */
public class Treap {

    public static class Node {
        int key, priority;
        Node left, right;
        Node(int key) {
            this.key = key;
            this.priority = new Random().nextInt();
        }
    }

    private Node root;

    private Node rotateRight(Node p) {
        Node q = p.left;
        p.left = q.right;
        q.right = p;
        return q;
    }

    private Node rotateLeft(Node p) {
        Node q = p.right;
        p.right = q.left;
        q.left = p;
        return q;
    }

    public void insert(int key) {
        root = insert(root, key);
    }

    private Node insert(Node node, int key) {
        if (node == null) return new Node(key);
        if (key < node.key) {
            node.left = insert(node.left, key);
            if (node.left.priority < node.priority) node = rotateRight(node);
        } else if (key > node.key) {
            node.right = insert(node.right, key);
            if (node.right.priority < node.priority) node = rotateLeft(node);
        }
        return node;
    }

    public void delete(int key) {
        root = delete(root, key);
    }

    private Node delete(Node node, int key) {
        if (node == null) return null;
        if (key < node.key) node.left = delete(node.left, key);
        else if (key > node.key) node.right = delete(node.right, key);
        else {
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
        return node;
    }

    public boolean search(int key) {
        Node node = root;
        while (node != null) {
            if (key == node.key) return true;
            if (key < node.key) node = node.left;
            else node = node.right;
        }
        return false;
    }

    public static void main(String[] args) {
        Treap treap = new Treap();
        treap.insert(5);
        treap.insert(3);
        treap.insert(7);
        treap.insert(1);
        treap.insert(9);

        System.out.println("Search 5: " + treap.search(5) + " (expected: true)");
        System.out.println("Search 2: " + treap.search(2) + " (expected: false)");
        treap.delete(5);
        System.out.println("Search 5 after delete: " + treap.search(5) + " (expected: false)");
        System.out.println("Search 7: " + treap.search(7) + " (expected: true)");
        treap.delete(7);
        System.out.println("Search 7 after delete: " + treap.search(7) + " (expected: false)");
    }
}
