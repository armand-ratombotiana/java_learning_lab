package com.dsacademy.lab33.treap;

import java.util.Random;

public class ImplicitTreap<V> {

    private static final Random RNG = new Random();

    private Node root;

    private static final class Node {
        V value;
        int priority;
        int size;
        Node left, right;
        boolean reversed;
        long sum;
        long lazyAdd;

        Node(V value) {
            this.value = value;
            this.priority = RNG.nextInt();
            this.size = 1;
            this.sum = value instanceof Number ? ((Number) value).longValue() : 0;
        }
    }

    public ImplicitTreap() {
        this.root = null;
    }

    public int size() {
        return size(root);
    }

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private long sum(Node node) {
        return node == null ? 0 : node.sum;
    }

    private void push(Node node) {
        if (node == null) return;
        if (node.reversed) {
            Node tmp = node.left;
            node.left = node.right;
            node.right = tmp;
            if (node.left != null) node.left.reversed ^= true;
            if (node.right != null) node.right.reversed ^= true;
            node.reversed = false;
        }
        if (node.lazyAdd != 0) {
            if (node.value instanceof Number) {
                node.value = (V) Long.valueOf(((Number) node.value).longValue() + node.lazyAdd);
            }
            if (node.left != null) node.left.lazyAdd += node.lazyAdd;
            if (node.right != null) node.right.lazyAdd += node.lazyAdd;
            node.lazyAdd = 0;
        }
    }

    private void update(Node node) {
        if (node == null) return;
        node.size = 1 + size(node.left) + size(node.right);
        node.sum = sum(node.left) + sum(node.right)
            + (node.value instanceof Number ? ((Number) node.value).longValue() : 0)
            + node.lazyAdd * (node.size);
    }

    public void insert(int index, V value) {
        Node[] parts = split(root, index);
        root = merge(merge(parts[0], new Node(value)), parts[1]);
    }

    public void append(V value) {
        root = merge(root, new Node(value));
    }

    public void delete(int index) {
        Node[] leftMid = split(root, index);
        Node[] midRight = split(leftMid[1], 1);
        root = merge(leftMid[0], midRight[1]);
    }

    public V get(int index) {
        return get(root, index);
    }

    private V get(Node node, int index) {
        push(node);
        int leftSize = size(node.left);
        if (index < leftSize) return get(node.left, index);
        if (index == leftSize) return node.value;
        return get(node.right, index - leftSize - 1);
    }

    public void set(int index, V value) {
        set(root, index, value);
    }

    private void set(Node node, int index, V value) {
        push(node);
        int leftSize = size(node.left);
        if (index < leftSize) set(node.left, index, value);
        else if (index > leftSize) set(node.right, index - leftSize - 1, value);
        else node.value = value;
        update(node);
    }

    public void reverse(int l, int r) {
        Node[] leftMid = split(root, l);
        Node[] midRight = split(leftMid[1], r - l);
        if (midRight[0] != null) midRight[0].reversed ^= true;
        root = merge(merge(leftMid[0], midRight[0]), midRight[1]);
    }

    public void rangeAdd(int l, int r, long add) {
        Node[] leftMid = split(root, l);
        Node[] midRight = split(leftMid[1], r - l);
        if (midRight[0] != null) midRight[0].lazyAdd += add;
        root = merge(merge(leftMid[0], midRight[0]), midRight[1]);
    }

    public long rangeSum(int l, int r) {
        Node[] leftMid = split(root, l);
        Node[] midRight = split(leftMid[1], r - l);
        long result = sum(midRight[0]);
        root = merge(merge(leftMid[0], midRight[0]), midRight[1]);
        return result;
    }

    private Node[] split(Node node, int k) {
        if (node == null) return new Node[]{null, null};
        push(node);
        if (k <= size(node.left)) {
            Node[] parts = split(node.left, k);
            node.left = parts[1];
            update(node);
            return new Node[]{parts[0], node};
        }
        Node[] parts = split(node.right, k - size(node.left) - 1);
        node.right = parts[0];
        update(node);
        return new Node[]{node, parts[1]};
    }

    private Node merge(Node a, Node b) {
        if (a == null) return b;
        if (b == null) return a;
        push(a);
        push(b);
        if (a.priority < b.priority) {
            a.right = merge(a.right, b);
            update(a);
            return a;
        }
        b.left = merge(a, b.left);
        update(b);
        return b;
    }

    public java.util.List<V> toList() {
        java.util.List<V> list = new java.util.ArrayList<>();
        toList(root, list);
        return list;
    }

    private void toList(Node node, java.util.List<V> list) {
        if (node == null) return;
        push(node);
        toList(node.left, list);
        list.add(node.value);
        toList(node.right, list);
    }
}
