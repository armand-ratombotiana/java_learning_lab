package com.ds04;

import java.util.*;

/*
 * BinaryTree - Basic binary tree with traversal implementations.
 *
 * Time Complexity:
 * - Traversals (in/pre/post/level): O(n)
 * - height: O(n)
 *
 * Space Complexity: O(n) for recursion stack (worst-case O(n))
 */
public class BinaryTree<T> {

    public static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;

        public Node(T data) {
            this.data = data;
        }

        public T getData() { return data; }
        public Node<T> getLeft() { return left; }
        public Node<T> getRight() { return right; }
    }

    protected Node<T> root;

    public BinaryTree() {}

    public BinaryTree(T rootData) {
        this.root = new Node<>(rootData);
    }

    public Node<T> getRoot() { return root; }

    public int height() {
        return height(root);
    }

    protected int height(Node<T> node) {
        if (node == null) return -1;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public int size() {
        return size(root);
    }

    private int size(Node<T> node) {
        if (node == null) return 0;
        return 1 + size(node.left) + size(node.right);
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

    public List<T> preorder() {
        List<T> result = new ArrayList<>();
        preorder(root, result);
        return result;
    }

    private void preorder(Node<T> node, List<T> result) {
        if (node == null) return;
        result.add(node.data);
        preorder(node.left, result);
        preorder(node.right, result);
    }

    public List<T> postorder() {
        List<T> result = new ArrayList<>();
        postorder(root, result);
        return result;
    }

    private void postorder(Node<T> node, List<T> result) {
        if (node == null) return;
        postorder(node.left, result);
        postorder(node.right, result);
        result.add(node.data);
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

    public void printTree() {
        printTree(root, 0);
    }

    private void printTree(Node<T> node, int level) {
        if (node == null) return;
        printTree(node.right, level + 1);
        System.out.println("  ".repeat(level) + node.data);
        printTree(node.left, level + 1);
    }
}
