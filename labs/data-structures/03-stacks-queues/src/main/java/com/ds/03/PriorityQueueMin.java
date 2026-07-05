package com.ds03;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * PriorityQueueMin - Min-heap based priority queue.
 *
 * Time Complexity:
 * - offer: O(log n)
 * - poll: O(log n)
 * - peek: O(1)
 *
 * Space Complexity: O(n)
 */
public class PriorityQueueMin<T extends Comparable<T>> implements Iterable<T> {

    private static final int DEFAULT_CAPACITY = 11;
    private Object[] heap;
    private int size;

    public PriorityQueueMin() {
        heap = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public void offer(T value) {
        if (size == heap.length) resize();
        heap[size] = value;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue is empty");
        T result = (T) heap[0];
        size--;
        heap[0] = heap[size];
        heap[size] = null;
        if (size > 0) siftDown(0);
        return result;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue is empty");
        return (T) heap[0];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
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
