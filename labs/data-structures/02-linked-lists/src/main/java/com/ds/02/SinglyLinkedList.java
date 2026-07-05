package com.ds02;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringJoiner;

/*
 * SinglyLinkedList - Singly linked list implementation.
 *
 * Time Complexity:
 * - addFirst: O(1)
 * - addLast: O(n)
 * - removeFirst: O(1)
 * - removeLast: O(n)
 * - get(index): O(n)
 * - remove(value): O(n)
 * - reverse: O(n)
 *
 * Space Complexity: O(n)
 */
public class SinglyLinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private int size;

    public SinglyLinkedList() {}

    public int size() { return size; }

    public boolean isEmpty() { return size == 0; }

    public void addFirst(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public void addLast(T value) {
        Node<T> newNode = new Node<>(value);
        if (head == null) {
            head = newNode;
        } else {
            Node<T> current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    public T getFirst() {
        if (head == null) throw new NoSuchElementException();
        return head.data;
    }

    public T getLast() {
        if (head == null) throw new NoSuchElementException();
        Node<T> current = head;
        while (current.next != null) {
            current = current.next;
        }
        return current.data;
    }

    public T removeFirst() {
        if (head == null) throw new NoSuchElementException();
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }

    public T removeLast() {
        if (head == null) throw new NoSuchElementException();
        if (head.next == null) {
            T data = head.data;
            head = null;
            size--;
            return data;
        }
        Node<T> current = head;
        while (current.next.next != null) {
            current = current.next;
        }
        T data = current.next.data;
        current.next = null;
        size--;
        return data;
    }

    public T get(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
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

    public boolean remove(T value) {
        if (head == null) return false;
        if (Objects.equals(head.data, value)) {
            head = head.next;
            size--;
            return true;
        }
        Node<T> current = head;
        while (current.next != null) {
            if (Objects.equals(current.next.data, value)) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void reverse() {
        Node<T> prev = null;
        Node<T> current = head;
        while (current != null) {
            Node<T> next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        head = prev;
    }

    public T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked")
            T[] result = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
            a = result;
        }
        Node<T> current = head;
        for (int i = 0; i < size; i++) {
            a[i] = current.data;
            current = current.next;
        }
        if (a.length > size) a[size] = null;
        return a;
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

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" -> ", "[", "]");
        Node<T> current = head;
        while (current != null) {
            sj.add(String.valueOf(current.data));
            current = current.next;
        }
        return sj.toString();
    }
}
