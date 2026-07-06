package com.dsacademy.lab20.immutable;

public final class PersistentBinaryTree<E extends Comparable<E>> {
    private final E value;
    private final PersistentBinaryTree<E> left;
    private final PersistentBinaryTree<E> right;
    private final int size;

    private static final PersistentBinaryTree<?> EMPTY = new PersistentBinaryTree<>();

    private PersistentBinaryTree() { this.value = null; this.left = null; this.right = null; this.size = 0; }

    private PersistentBinaryTree(E value, PersistentBinaryTree<E> left, PersistentBinaryTree<E> right) {
        this.value = value;
        this.left = left;
        this.right = right;
        this.size = 1 + left.size + right.size;
    }

    @SuppressWarnings("unchecked")
    public static <E extends Comparable<E>> PersistentBinaryTree<E> empty() { return (PersistentBinaryTree<E>) EMPTY; }

    public PersistentBinaryTree<E> insert(E val) {
        if (isEmpty()) return new PersistentBinaryTree<>(val, empty(), empty());
        int cmp = val.compareTo(value);
        if (cmp < 0) return new PersistentBinaryTree<>(value, left.insert(val), right);
        if (cmp > 0) return new PersistentBinaryTree<>(value, left, right.insert(val));
        return this;
    }

    public boolean contains(E val) {
        if (isEmpty()) return false;
        int cmp = val.compareTo(value);
        if (cmp == 0) return true;
        return cmp < 0 ? left.contains(val) : right.contains(val);
    }

    public E findMin() {
        if (isEmpty()) throw new IllegalStateException("Empty tree");
        return left.isEmpty() ? value : left.findMin();
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
    public E value() { return value; }
    public PersistentBinaryTree<E> left() { return left; }
    public PersistentBinaryTree<E> right() { return right; }
}
