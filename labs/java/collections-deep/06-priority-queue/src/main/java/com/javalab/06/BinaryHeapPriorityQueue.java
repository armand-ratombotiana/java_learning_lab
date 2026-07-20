package com.javalab.06;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinaryHeapPriorityQueue<E> implements Iterable<E> {

    private static final int DEFAULT_CAPACITY = 11;

    private Object[] heap;
    private int size;
    private final Comparator<? super E> comparator;

    public BinaryHeapPriorityQueue() {
        this(DEFAULT_CAPACITY, null);
    }

    public BinaryHeapPriorityQueue(Comparator<? super E> comparator) {
        this(DEFAULT_CAPACITY, comparator);
    }

    public BinaryHeapPriorityQueue(int initialCapacity, Comparator<? super E> comparator) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Illegal capacity: " + initialCapacity);
        }
        this.heap = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }

    public void add(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        ensureCapacity(size + 1);
        heap[size] = element;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        if (size == 0) return null;
        return (E) heap[0];
    }

    @SuppressWarnings("unchecked")
    public E poll() {
        if (size == 0) return null;
        E result = (E) heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) {
            siftDown(0);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        E value = (E) heap[index];
        while (index > 0) {
            int parent = (index - 1) >>> 1;
            E parentVal = (E) heap[parent];
            if (compare(value, parentVal) >= 0) break;
            heap[index] = parentVal;
            index = parent;
        }
        heap[index] = value;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        E value = (E) heap[index];
        int half = size >>> 1;
        while (index < half) {
            int child = (index << 1) + 1;
            E childVal = (E) heap[child];
            int right = child + 1;
            if (right < size && compare((E) heap[right], childVal) < 0) {
                child = right;
                childVal = (E) heap[child];
            }
            if (compare(value, childVal) <= 0) break;
            heap[index] = childVal;
            index = child;
        }
        heap[index] = value;
    }

    @SuppressWarnings("unchecked")
    private int compare(E a, E b) {
        if (comparator != null) {
            return comparator.compare(a, b);
        }
        return ((Comparable<? super E>) a).compareTo(b);
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity > heap.length) {
            int newCapacity = heap.length < 64 ? heap.length + 2 : heap.length + (heap.length >> 1);
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            heap = Arrays.copyOf(heap, newCapacity);
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    public Iterator<E> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<E> {
        private int cursor = 0;

        public boolean hasNext() {
            return cursor < size;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return (E) heap[cursor++];
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
