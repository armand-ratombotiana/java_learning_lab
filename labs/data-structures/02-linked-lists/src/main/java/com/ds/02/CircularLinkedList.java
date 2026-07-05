package com.ds02;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/*
 * CircularLinkedList - Circular singly linked list implementation.
 *
 * Time Complexity:
 * - addFirst: O(1)
 * - addLast: O(n)
 * - removeFirst: O(1)
 * - removeLast: O(n)
 * - contains: O(n)
 *
 * Space Complexity: O(n)
 */
public class CircularLinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> tail;
    private int size;

    public CircularLinkedList() {}

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        if (tail == null) {
            tail = newNode;
            tail.next = tail;
        } else {
            newNode.next = tail.next;
            tail.next = newNode;
        }
        size++;
    }

    public void addLast(T value) {
        addFirst(value);
        tail = tail.next;
    }

    public T getFirst() {
        if (tail == null) throw new NoSuchElementException();
        return tail.next.data;
    }

    public T getLast() {
        if (tail == null) throw new NoSuchElementException();
        return tail.data;
    }

    public T removeFirst() {
        if (tail == null) throw new NoSuchElementException();
        Node<T> head = tail.next;
        T data = head.data;
        if (head == tail) {
            tail = null;
        } else {
            tail.next = head.next;
        }
        size--;
        return data;
    }

    public T removeLast() {
        if (tail == null) throw new NoSuchElementException();
        if (tail.next == tail) {
            T data = tail.data;
            tail = null;
            size--;
            return data;
        }
        Node<T> current = tail.next;
        while (current.next != tail) {
            current = current.next;
        }
        T data = tail.data;
        current.next = tail.next;
        tail = current;
        size--;
        return data;
    }

    public boolean contains(T value) {
        if (tail == null) return false;
        Node<T> current = tail.next;
        do {
            if (Objects.equals(current.data, value)) return true;
            current = current.next;
        } while (current != tail.next);
        return false;
    }

    public boolean isCircular() {
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = tail;
            private boolean started = false;
            @Override
            public boolean hasNext() {
                if (tail == null) return false;
                if (!started) return true;
                return current.next != tail.next;
            }
            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                if (!started) {
                    current = tail.next;
                    started = true;
                } else {
                    current = current.next;
                }
                return current.data;
            }
        };
    }

    @Override
    public String toString() {
        if (tail == null) return "[]";
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = tail.next;
        do {
            sb.append(current.data);
            current = current.next;
            if (current != tail.next) sb.append(" -> ");
        } while (current != tail.next);
        sb.append(" -> (circular)");
        return sb.toString();
    }
}
