package com.dsacademy.lab33.treap;

import java.util.Random;

public class Treap<K extends Comparable<K>, V> {

    private static final Random RNG = new Random();

    private TreapNode<K, V> root;

    private static final class TreapNode<K, V> {
        K key;
        V value;
        int priority;
        TreapNode<K, V> left, right;

        TreapNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.priority = RNG.nextInt();
        }
    }

    public Treap() {
        this.root = null;
    }

    public V search(K key) {
        TreapNode<K, V> node = search(root, key);
        return node == null ? null : node.value;
    }

    private TreapNode<K, V> search(TreapNode<K, V> node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) return search(node.left, key);
        if (cmp > 0) return search(node.right, key);
        return node;
    }

    public void insert(K key, V value) {
        root = insert(root, key, value);
    }

    private TreapNode<K, V> insert(TreapNode<K, V> node, K key, V value) {
        if (node == null) return new TreapNode<>(key, value);
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
            if (node.left.priority < node.priority) {
                node = rotateRight(node);
            }
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
            if (node.right.priority < node.priority) {
                node = rotateLeft(node);
            }
        } else {
            node.value = value;
        }
        return node;
    }

    public void delete(K key) {
        root = delete(root, key);
    }

    private TreapNode<K, V> delete(TreapNode<K, V> node, K key) {
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
        return node;
    }

    private TreapNode<K, V> rotateRight(TreapNode<K, V> p) {
        TreapNode<K, V> q = p.left;
        p.left = q.right;
        q.right = p;
        return q;
    }

    private TreapNode<K, V> rotateLeft(TreapNode<K, V> p) {
        TreapNode<K, V> q = p.right;
        p.right = q.left;
        q.left = p;
        return q;
    }

    public void inorder(java.util.List<K> result) {
        inorder(root, result);
    }

    private void inorder(TreapNode<K, V> node, java.util.List<K> result) {
        if (node == null) return;
        inorder(node.left, result);
        result.add(node.key);
        inorder(node.right, result);
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
    }

    public Treap<K, V> split(K key) {
        Treap<K, V> right = new Treap<>();
        TreapNode<K, V>[] parts = split(root, key);
        this.root = parts[0];
        right.root = parts[1];
        return right;
    }

    @SuppressWarnings("unchecked")
    private TreapNode<K, V>[] split(TreapNode<K, V> node, K key) {
        if (node == null) return new TreapNode[]{null, null};
        if (key.compareTo(node.key) <= 0) {
            TreapNode<K, V>[] parts = split(node.left, key);
            node.left = parts[1];
            return new TreapNode[]{parts[0], node};
        }
        TreapNode<K, V>[] parts = split(node.right, key);
        node.right = parts[0];
        return new TreapNode[]{node, parts[1]};
    }

    public void merge(Treap<K, V> other) {
        this.root = merge(this.root, other.root);
    }

    private TreapNode<K, V> merge(TreapNode<K, V> a, TreapNode<K, V> b) {
        if (a == null) return b;
        if (b == null) return a;
        if (a.priority < b.priority) {
            a.right = merge(a.right, b);
            return a;
        }
        b.left = merge(a, b.left);
        return b;
    }
}
