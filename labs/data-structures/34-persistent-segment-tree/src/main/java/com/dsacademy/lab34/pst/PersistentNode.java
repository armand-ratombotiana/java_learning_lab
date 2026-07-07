package com.dsacademy.lab34.pst;

public final class PersistentNode {

    final PersistentNode left;
    final PersistentNode right;
    final int value;

    public PersistentNode(PersistentNode left, PersistentNode right, int value) {
        this.left = left;
        this.right = right;
        this.value = value;
    }

    public PersistentNode getLeft() {
        return left;
    }

    public PersistentNode getRight() {
        return right;
    }

    public int getValue() {
        return value;
    }
}
