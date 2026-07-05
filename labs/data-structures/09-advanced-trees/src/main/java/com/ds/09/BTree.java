package com.ds09;

import java.util.*;

/*
 * BTree - B-Tree of order m (minimum degree t).
 * Every node can have up to 2t-1 keys and 2t children.
 *
 * Time Complexity: O(log n) for insert/search/delete
 * Space Complexity: O(n)
 */
public class BTree<T extends Comparable<T>> {

    private static final int DEFAULT_DEGREE = 3;

    public static class BTreeNode<T> {
        List<T> keys;
        List<BTreeNode<T>> children;
        boolean leaf;
        int degree;

        BTreeNode(int degree, boolean leaf) {
            this.degree = degree;
            this.leaf = leaf;
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }

        public List<T> getKeys() { return Collections.unmodifiableList(keys); }
        public boolean isLeaf() { return leaf; }
    }

    private BTreeNode<T> root;
    private final int degree;

    public BTree() {
        this(DEFAULT_DEGREE);
    }

    public BTree(int degree) {
        this.degree = degree;
        this.root = new BTreeNode<>(degree, true);
    }

    public BTreeNode<T> getRoot() { return root; }

    public void insert(T key) {
        BTreeNode<T> r = root;
        if (r.keys.size() == 2 * degree - 1) {
            BTreeNode<T> s = new BTreeNode<>(degree, false);
            s.children.add(r);
            root = s;
            splitChild(s, 0);
            insertNonFull(s, key);
        } else {
            insertNonFull(r, key);
        }
    }

    private void insertNonFull(BTreeNode<T> node, T key) {
        int i = node.keys.size() - 1;
        if (node.leaf) {
            int pos = 0;
            while (pos < node.keys.size() && key.compareTo(node.keys.get(pos)) > 0) pos++;
            node.keys.add(pos, key);
        } else {
            while (i >= 0 && key.compareTo(node.keys.get(i)) < 0) i--;
            i++;
            if (node.children.get(i).keys.size() == 2 * degree - 1) {
                splitChild(node, i);
                if (key.compareTo(node.keys.get(i)) > 0) i++;
            }
            insertNonFull(node.children.get(i), key);
        }
    }

    private void splitChild(BTreeNode<T> parent, int index) {
        BTreeNode<T> child = parent.children.get(index);
        BTreeNode<T> newNode = new BTreeNode<>(degree, child.leaf);

        for (int j = degree; j < 2 * degree - 1; j++) {
            newNode.keys.add(child.keys.get(j));
        }
        for (int j = degree; j < child.keys.size(); j++) {
            child.keys.remove(j);
            j--;
        }

        if (!child.leaf) {
            for (int j = degree; j < 2 * degree; j++) {
                newNode.children.add(child.children.get(j));
            }
            for (int j = degree; j < child.children.size(); j++) {
                child.children.remove(j);
                j--;
            }
        }

        T midKey = child.keys.remove(degree - 1);
        parent.keys.add(index, midKey);
        parent.children.add(index + 1, newNode);
    }

    public boolean search(T key) {
        return search(root, key) != null;
    }

    private BTreeNode<T> search(BTreeNode<T> node, T key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) i++;
        if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) return node;
        if (node.leaf) return null;
        return search(node.children.get(i), key);
    }

    public List<T> inorder() {
        List<T> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    private void inorder(BTreeNode<T> node, List<T> result) {
        if (node == null) return;
        int i;
        for (i = 0; i < node.keys.size(); i++) {
            if (!node.leaf) inorder(node.children.get(i), result);
            result.add(node.keys.get(i));
        }
        if (!node.leaf) inorder(node.children.get(i), result);
    }

    public List<T> levelOrder() {
        List<T> result = new ArrayList<>();
        if (root == null) return result;
        Queue<BTreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            BTreeNode<T> node = queue.poll();
            result.addAll(node.keys);
            if (!node.leaf) {
                queue.addAll(node.children);
            }
        }
        return result;
    }
}
