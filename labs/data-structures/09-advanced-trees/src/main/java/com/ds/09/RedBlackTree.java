package com.ds09;

import java.util.*;

/*
 * RedBlackTree - Self-balancing BST with red-black properties.
 *
 * Properties:
 * 1. Every node is red or black
 * 2. Root is black
 * 3. Leaves (null) are black
 * 4. Red nodes have black children
 * 5. All paths from root to leaves have same black count
 *
 * Time Complexity: O(log n) for insert/search/delete
 * Space Complexity: O(n)
 */
public class RedBlackTree<T extends Comparable<T>> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    public static class Node<T> {
        T data;
        Node<T> left, right, parent;
        boolean color;

        Node(T data) {
            this.data = data;
            this.color = RED;
        }

        public T getData() { return data; }
        public boolean isRed() { return color == RED; }
    }

    private Node<T> root;

    public RedBlackTree() {}

    public Node<T> getRoot() { return root; }

    public void insert(T value) {
        Node<T> newNode = new Node<>(value);
        root = bstInsert(root, newNode);
        fixInsert(newNode);
        root.color = BLACK;
    }

    private Node<T> bstInsert(Node<T> node, Node<T> newNode) {
        if (node == null) return newNode;
        int cmp = newNode.data.compareTo(node.data);
        if (cmp < 0) {
            node.left = bstInsert(node.left, newNode);
            node.left.parent = node;
        } else if (cmp > 0) {
            node.right = bstInsert(node.right, newNode);
            node.right.parent = node;
        }
        return node;
    }

    private void fixInsert(Node<T> x) {
        while (x != root && x.parent.color == RED) {
            if (x.parent == x.parent.parent.left) {
                Node<T> uncle = x.parent.parent.right;
                if (uncle != null && uncle.color == RED) {
                    x.parent.color = BLACK;
                    uncle.color = BLACK;
                    x.parent.parent.color = RED;
                    x = x.parent.parent;
                } else {
                    if (x == x.parent.right) {
                        x = x.parent;
                        rotateLeft(x);
                    }
                    x.parent.color = BLACK;
                    x.parent.parent.color = RED;
                    rotateRight(x.parent.parent);
                }
            } else {
                Node<T> uncle = x.parent.parent.left;
                if (uncle != null && uncle.color == RED) {
                    x.parent.color = BLACK;
                    uncle.color = BLACK;
                    x.parent.parent.color = RED;
                    x = x.parent.parent;
                } else {
                    if (x == x.parent.left) {
                        x = x.parent;
                        rotateRight(x);
                    }
                    x.parent.color = BLACK;
                    x.parent.parent.color = RED;
                    rotateLeft(x.parent.parent);
                }
            }
        }
    }

    public boolean search(T value) {
        return search(root, value) != null;
    }

    private Node<T> search(Node<T> node, T value) {
        while (node != null) {
            int cmp = value.compareTo(node.data);
            if (cmp == 0) return node;
            node = cmp < 0 ? node.left : node.right;
        }
        return null;
    }

    public void delete(T value) {
        Node<T> node = search(root, value);
        if (node == null) return;
        deleteNode(node);
    }

    private void deleteNode(Node<T> node) {
        Node<T> y = node;
        Node<T> x;
        boolean originalColor = y.color;

        if (node.left == null) {
            x = node.right;
            transplant(node, node.right);
        } else if (node.right == null) {
            x = node.left;
            transplant(node, node.left);
        } else {
            y = minimum(node.right);
            originalColor = y.color;
            x = y.right;
            if (y.parent == node) {
                if (x != null) x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = node.right;
                if (y.right != null) y.right.parent = y;
            }
            transplant(node, y);
            y.left = node.left;
            if (y.left != null) y.left.parent = y;
            y.color = node.color;
        }
        if (originalColor == BLACK) {
            fixDelete(x);
        }
    }

    private void fixDelete(Node<T> x) {
        while (x != root && (x == null || x.color == BLACK)) {
            if (x == x.parent.left) {
                Node<T> w = x.parent.right;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateLeft(x.parent);
                    w = x.parent.right;
                }
                if ((w.left == null || w.left.color == BLACK) &&
                    (w.right == null || w.right.color == BLACK)) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.right == null || w.right.color == BLACK) {
                        if (w.left != null) w.left.color = BLACK;
                        w.color = RED;
                        rotateRight(w);
                        w = x.parent.right;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    if (w.right != null) w.right.color = BLACK;
                    rotateLeft(x.parent);
                    x = root;
                }
            } else {
                Node<T> w = x.parent.left;
                if (w.color == RED) {
                    w.color = BLACK;
                    x.parent.color = RED;
                    rotateRight(x.parent);
                    w = x.parent.left;
                }
                if ((w.right == null || w.right.color == BLACK) &&
                    (w.left == null || w.left.color == BLACK)) {
                    w.color = RED;
                    x = x.parent;
                } else {
                    if (w.left == null || w.left.color == BLACK) {
                        if (w.right != null) w.right.color = BLACK;
                        w.color = RED;
                        rotateLeft(w);
                        w = x.parent.left;
                    }
                    w.color = x.parent.color;
                    x.parent.color = BLACK;
                    if (w.left != null) w.left.color = BLACK;
                    rotateRight(x.parent);
                    x = root;
                }
            }
        }
        if (x != null) x.color = BLACK;
    }

    private Node<T> minimum(Node<T> node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private void transplant(Node<T> u, Node<T> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) v.parent = u.parent;
    }

    private void rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        x.right = y.left;
        if (y.left != null) y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rotateRight(Node<T> y) {
        Node<T> x = y.left;
        y.left = x.right;
        if (x.right != null) x.right.parent = y;
        x.parent = y.parent;
        if (y.parent == null) {
            root = x;
        } else if (y == y.parent.left) {
            y.parent.left = x;
        } else {
            y.parent.right = x;
        }
        x.right = y;
        y.parent = x;
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
