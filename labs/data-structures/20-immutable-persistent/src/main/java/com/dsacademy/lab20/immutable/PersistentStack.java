package com.dsacademy.lab20.immutable;

public final class PersistentStack<E> {
    private static final PersistentStack<?> EMPTY = new PersistentStack<>(null, null, 0);

    private final E top;
    private final PersistentStack<E> rest;
    private final int size;

    private PersistentStack(E top, PersistentStack<E> rest, int size) {
        this.top = top;
        this.rest = rest;
        this.size = size;
    }

    @SuppressWarnings("unchecked")
    public static <E> PersistentStack<E> empty() { return (PersistentStack<E>) EMPTY; }

    public PersistentStack<E> push(E value) { return new PersistentStack<>(value, this, size + 1); }

    public PersistentStack<E> pop() {
        if (isEmpty()) throw new IllegalStateException("Empty stack");
        return rest;
    }

    public E peek() {
        if (isEmpty()) throw new IllegalStateException("Empty stack");
        return top;
    }

    public boolean isEmpty() { return size == 0; }
    public int size() { return size; }
}
