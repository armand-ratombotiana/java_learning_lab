package com.dsacademy.lab31.rope;

public final class RopeNode {

    private final String data;
    private final int weight;
    private final int length;
    private final RopeNode left;
    private final RopeNode right;

    public RopeNode(String data) {
        if (data == null) {
            throw new IllegalArgumentException("Leaf data cannot be null");
        }
        this.data = data;
        this.weight = data.length();
        this.length = data.length();
        this.left = null;
        this.right = null;
    }

    public RopeNode(RopeNode left, RopeNode right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Internal node children cannot be null");
        }
        this.data = null;
        this.left = left;
        this.right = right;
        this.weight = left.length;
        this.length = left.length + right.length;
    }

    public boolean isLeaf() {
        return data != null;
    }

    public String getData() {
        return data;
    }

    public int getWeight() {
        return weight;
    }

    public int getLength() {
        return length;
    }

    public RopeNode getLeft() {
        return left;
    }

    public RopeNode getRight() {
        return right;
    }
}
