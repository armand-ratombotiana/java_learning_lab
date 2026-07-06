package com.dsacademy.lab16.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack<T> {

    private static class Node<T> {
        final T value;
        AtomicReference<Node<T>> next;
        Node(T value) { this.value = value; this.next = new AtomicReference<>(null); }
    }

    private final AtomicReference<Node<T>> top = new AtomicReference<>(null);

    public void push(T value) {
        Node<T> newNode = new Node<>(value);
        while (true) {
            Node<T> currentTop = top.get();
            newNode.next.set(currentTop);
            if (top.compareAndSet(currentTop, newNode)) return;
        }
    }

    public T pop() {
        while (true) {
            Node<T> currentTop = top.get();
            if (currentTop == null) return null;
            Node<T> newTop = currentTop.next.get();
            if (top.compareAndSet(currentTop, newTop)) return currentTop.value;
        }
    }

    public T peek() {
        Node<T> currentTop = top.get();
        return currentTop == null ? null : currentTop.value;
    }

    public boolean isEmpty() { return top.get() == null; }
}
