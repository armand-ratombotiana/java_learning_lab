package com.dsacademy.lab20.immutable;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class PersistentList<E> implements Iterable<E> {
    private static final PersistentList<?> EMPTY = new PersistentList<>(null, null);

    private final E head;
    private final PersistentList<E> tail;
    private final int size;

    private PersistentList(E head, PersistentList<E> tail) {
        this.head = head;
        this.tail = tail;
        this.size = (tail == null) ? 0 : tail.size + 1;
    }

    @SuppressWarnings("unchecked")
    public static <E> PersistentList<E> empty() { return (PersistentList<E>) EMPTY; }

    public PersistentList<E> add(E element) { return new PersistentList<>(element, this); }

    public PersistentList<E> remove() {
        if (isEmpty()) throw new IllegalStateException("Empty list");
        return tail;
    }

    public E head() {
        if (isEmpty()) throw new IllegalStateException("Empty list");
        return head;
    }

    public PersistentList<E> tail() {
        if (isEmpty()) throw new IllegalStateException("Empty list");
        return tail;
    }

    public boolean isEmpty() { return tail == null; }
    public int size() { return size; }

    public E get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        PersistentList<E> current = this;
        for (int i = 0; i < index; i++) current = current.tail;
        return current.head;
    }

    public PersistentList<E> set(int index, E value) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        if (index == 0) return new PersistentList<>(value, tail);
        return new PersistentList<>(head, tail.set(index - 1, value));
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            PersistentList<E> current = PersistentList.this;
            public boolean hasNext() { return !current.isEmpty(); }
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E val = current.head;
                current = current.tail;
                return val;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (E e : this) { sb.append(e).append(", "); }
        if (size > 0) sb.setLength(sb.length() - 2);
        return sb.append("]").toString();
    }
}
