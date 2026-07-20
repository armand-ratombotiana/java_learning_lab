package com.javalab.08;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;

public class MichaelScottQueue<E> implements Iterable<E> {

    private final AtomicReference<Node<E>> head;
    private final AtomicReference<Node<E>> tail;

    public MichaelScottQueue() {
        Node<E> sentinel = new Node<>(null, null);
        head = new AtomicReference<>(sentinel);
        tail = new AtomicReference<>(sentinel);
    }

    private static class Node<E> {
        final E value;
        final AtomicReference<Node<E>> next;

        Node(E value, Node<E> next) {
            this.value = value;
            this.next = new AtomicReference<>(next);
        }
    }

    public void add(E element) {
        if (element == null) throw new NullPointerException();
        Node<E> newNode = new Node<>(element, null);
        while (true) {
            Node<E> currentTail = tail.get();
            Node<E> tailNext = currentTail.next.get();
            if (currentTail == tail.get()) {
                if (tailNext == null) {
                    if (currentTail.next.compareAndSet(null, newNode)) {
                        tail.compareAndSet(currentTail, newNode);
                        return;
                    }
                } else {
                    tail.compareAndSet(currentTail, tailNext);
                }
            }
        }
    }

    public E poll() {
        while (true) {
            Node<E> currentHead = head.get();
            Node<E> currentTail = tail.get();
            Node<E> headNext = currentHead.next.get();
            if (currentHead == head.get()) {
                if (currentHead == currentTail) {
                    if (headNext == null) {
                        return null;
                    }
                    tail.compareAndSet(currentTail, headNext);
                } else {
                    E value = headNext.value;
                    if (head.compareAndSet(currentHead, headNext)) {
                        return value;
                    }
                }
            }
        }
    }

    public E peek() {
        Node<E> currentHead = head.get();
        Node<E> headNext = currentHead.next.get();
        return headNext != null ? headNext.value : null;
    }

    public boolean isEmpty() {
        return head.get().next.get() == null;
    }

    public Iterator<E> iterator() {
        return new QueueIterator();
    }

    private class QueueIterator implements Iterator<E> {
        private Node<E> current = head.get().next.get();

        public boolean hasNext() {
            return current != null;
        }

        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            E value = current.value;
            current = current.next.get();
            return value;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head.get().next.get();
        while (current != null) {
            sb.append(current.value);
            if (current.next.get() != null) sb.append(", ");
            current = current.next.get();
        }
        sb.append("]");
        return sb.toString();
    }
}
