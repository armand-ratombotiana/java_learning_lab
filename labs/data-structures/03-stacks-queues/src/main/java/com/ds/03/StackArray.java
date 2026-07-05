package com.ds03;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * StackArray - Stack implementation using a dynamic array.
 *
 * Time Complexity:
 * - push: O(1) amortized
 * - pop: O(1)
 * - peek: O(1)
 *
 * Space Complexity: O(n)
 */
public class StackArray<T> implements Iterable<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;
    private int top;

    public StackArray() {
        elements = new Object[DEFAULT_CAPACITY];
        top = -1;
    }

    public void push(T value) {
        if (top == elements.length - 1) resize();
        elements[++top] = value;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T value = (T) elements[top];
        elements[top--] = null;
        return value;
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return (T) elements[top];
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    private void resize() {
        Object[] newElements = new Object[elements.length * 2];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        elements = newElements;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int cursor = top;
            @Override
            public boolean hasNext() { return cursor >= 0; }
            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                return (T) elements[cursor--];
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = top; i >= 0; i--) {
            sb.append(elements[i]);
            if (i > 0) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
