package com.ds07;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * MinHeap - Binary min-heap implementation.
 *
 * Time Complexity:
 * - insert: O(log n)
 * - extractMin: O(log n)
 * - getMin: O(1)
 * - heapify: O(n)
 *
 * Space Complexity: O(n)
 */
public class MinHeap<T extends Comparable<T>> implements Iterable<T> {

    private static final int DEFAULT_CAPACITY = 11;
    private Object[] heap;
    private int size;

    public MinHeap() {
        heap = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public MinHeap(T[] array) {
        heap = Arrays.copyOf(array, array.length);
        size = array.length;
        heapify();
    }

    public void insert(T value) {
        if (size == heap.length) resize();
        heap[size] = value;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        T min = (T) heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) siftDown(0);
        return min;
    }

    @SuppressWarnings("unchecked")
    public T getMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return (T) heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void heapify() {
        for (int i = (size / 2) - 1; i >= 0; i--) {
            siftDown(i);
        }
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int index) {
        T value = (T) heap[index];
        while (index > 0) {
            int parent = (index - 1) / 2;
            T parentVal = (T) heap[parent];
            if (value.compareTo(parentVal) >= 0) break;
            heap[index] = parentVal;
            index = parent;
        }
        heap[index] = value;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int index) {
        T value = (T) heap[index];
        int half = size / 2;
        while (index < half) {
            int child = index * 2 + 1;
            int right = child + 1;
            if (right < size && ((T) heap[right]).compareTo((T) heap[child]) < 0) {
                child = right;
            }
            if (((T) heap[child]).compareTo(value) >= 0) break;
            heap[index] = heap[child];
            index = child;
        }
        heap[index] = value;
    }

    private void resize() {
        heap = Arrays.copyOf(heap, heap.length * 2);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = 0;
            @Override
            public boolean hasNext() { return cursor < size; }
            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) heap[cursor++];
            }
        };
    }

    @Override
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
