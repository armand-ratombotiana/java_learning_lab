package com.ds03;

import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * DequeArray - Double-ended queue using a circular array.
 *
 * Time Complexity:
 * - addFirst: O(1) amortized
 * - addLast: O(1) amortized
 * - removeFirst: O(1)
 * - removeLast: O(1)
 *
 * Space Complexity: O(n)
 */
public class DequeArray<T> implements Iterable<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int front;
    private int rear;
    private int size;

    public DequeArray() {
        elements = new Object[DEFAULT_CAPACITY];
        front = 0;
        rear = -1;
        size = 0;
    }

    public void addFirst(T value) {
        if (size == elements.length) resize();
        front = (front - 1 + elements.length) % elements.length;
        elements[front] = value;
        if (size == 0) rear = front;
        size++;
    }

    public void addLast(T value) {
        if (size == elements.length) resize();
        rear = (rear + 1) % elements.length;
        elements[rear] = value;
        if (size == 0) front = rear;
        size++;
    }

    @SuppressWarnings("unchecked")
    public T removeFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        T value = (T) elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;
        size--;
        if (size == 0) { front = 0; rear = -1; }
        return value;
    }

    @SuppressWarnings("unchecked")
    public T removeLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        T value = (T) elements[rear];
        elements[rear] = null;
        rear = (rear - 1 + elements.length) % elements.length;
        size--;
        if (size == 0) { front = 0; rear = -1; }
        return value;
    }

    @SuppressWarnings("unchecked")
    public T getFirst() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        return (T) elements[front];
    }

    @SuppressWarnings("unchecked")
    public T getLast() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        return (T) elements[rear];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void resize() {
        Object[] newElements = new Object[elements.length * 2];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(front + i) % elements.length];
        }
        elements = newElements;
        front = 0;
        rear = size - 1;
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
                return (T) elements[(front + cursor++) % elements.length];
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[(front + i) % elements.length]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
