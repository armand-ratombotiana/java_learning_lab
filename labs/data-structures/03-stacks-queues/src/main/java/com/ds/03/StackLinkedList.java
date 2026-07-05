package com.ds03;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * StackLinkedList - Stack implementation using a singly linked list.
 *
 * Time Complexity:
 * - push: O(1)
 * - pop: O(1)
 * - peek: O(1)
 *
 * Space Complexity: O(n)
 */
public class StackLinkedList<T> implements Iterable<T> {

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }

    private Node<T> head;
    private int size;

    public StackLinkedList() {}

    public void push(T value) {
        Node<T> newNode = new Node<>(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    public T pop() {
        if (isEmpty()) throw new EmptyStackException();
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }

    public T peek() {
        if (isEmpty()) throw new EmptyStackException();
        return head.data;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
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
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
