package com.dsacademy.lab16.concurrent;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> {

    private static class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next = new AtomicReference<>(null);
        Node(T value) { this.value = value; }
    }

    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;

    public LockFreeQueue() {
        Node<T> sentinel = new Node<>(null);
        head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    public void enqueue(T value) {
        Node<T> newNode = new Node<>(value);
        while (true) {
            Node<T> currentTail = tail.get();
            Node<T> next = currentTail.next.get();
            if (next != null) {
                tail.compareAndSet(currentTail, next);
                continue;
            }
            if (currentTail.next.compareAndSet(null, newNode)) {
                tail.compareAndSet(currentTail, newNode);
                return;
            }
        }
    }

    public T dequeue() {
        while (true) {
            Node<T> currentHead = head.get();
            Node<T> currentTail = tail.get();
            Node<T> first = currentHead.next.get();
            if (currentHead == currentTail) {
                if (first == null) return null;
                tail.compareAndSet(currentTail, first);
            } else {
                if (first == null) continue;
                T value = first.value;
                if (head.compareAndSet(currentHead, first)) return value;
            }
        }
    }

    public boolean isEmpty() { return head.get().next.get() == null; }
}
