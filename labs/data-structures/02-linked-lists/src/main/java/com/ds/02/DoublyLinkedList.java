package com.ds02;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

/*
 * DoublyLinkedList - Doubly linked list implementation.
 *
 * Time Complexity:
 * - addFirst: O(1)
 * - addLast: O(1)
 * - removeFirst: O(1)
 * - removeLast: O(1)
 * - add(index): O(n)
 * - remove(index): O(n)
 * - reverse: O(n)
 *
 * Space Complexity: O(n)
 */
public class DoublyLinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        Node<T> prev;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public DoublyLinkedList() {}

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    public void addLast(T value) {
        Node<T> newNode = new Node<>(value);
        if (tail == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public T getFirst() {
        if (head == null) throw new NoSuchElementException();
        return head.data;
    }

    public T getLast() {
        if (tail == null) throw new NoSuchElementException();
        return tail.data;
    }

    public T removeFirst() {
        if (head == null) throw new NoSuchElementException();
        T data = head.data;
        if (head == tail) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
        return data;
    }

    public T removeLast() {
        if (tail == null) throw new NoSuchElementException();
        T data = tail.data;
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return data;
    }

    public void add(int index, T value) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == 0) {
            addFirst(value);
        } else if (index == size) {
            addLast(value);
        } else {
            Node<T> current = head;
            for (int i = 0; i < index; i++) current = current.next;
            Node<T> prev = current.prev;
            Node<T> newNode = new Node<>(value);
            prev.next = newNode;
            newNode.prev = prev;
            newNode.next = current;
            current.prev = newNode;
            size++;
        }
    }

    public T remove(int index) {
        checkIndex(index);
        if (index == 0) return removeFirst();
        if (index == size - 1) return removeLast();
        Node<T> current = head;
        for (int i = 0; i < index; i++) current = current.next;
        current.prev.next = current.next;
        current.next.prev = current.prev;
        size--;
        return current.data;
    }

    public boolean contains(T value) {
        Node<T> current = head;
        while (current != null) {
            if (Objects.equals(current.data, value)) return true;
            current = current.next;
        }
        return false;
    }

    public void reverse() {
        if (head == null) return;
        Node<T> current = head;
        Node<T> temp = null;
        tail = head;
        while (current != null) {
            temp = current.prev;
            current.prev = current.next;
            current.next = temp;
            current = current.prev;
        }
        if (temp != null) {
            head = temp.prev;
        }
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;
            @Override
            public boolean hasNext() { return current != null; }
            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    public Iterator<T> descendingIterator() {
        return new Iterator<T>() {
            private Node<T> current = tail;
            @Override
            public boolean hasNext() { return current != null; }
            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T data = current.data;
                current = current.prev;
                return data;
            }
        };
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" <-> ", "[", "]");
        Node<T> current = head;
        while (current != null) {
            sj.add(String.valueOf(current.data));
            current = current.next;
        }
        return sj.toString();
    }
}
