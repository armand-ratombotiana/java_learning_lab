package com.dsacademy.lab24.xorunrolled;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class XorLinkedList<T> implements Iterable<T> {

    private XorListNode<T> head;
    private XorListNode<T> tail;
    private int size;

    public XorLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void add(T value) {
        XorListNode<T> node = new XorListNode<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.xorPointer = System.identityHashCode(tail);
            tail.xorPointer ^= System.identityHashCode(node);
            tail = node;
        }
        size++;
    }

    public void addFirst(T value) {
        XorListNode<T> node = new XorListNode<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.xorPointer = System.identityHashCode(head);
            head.xorPointer ^= System.identityHashCode(node);
            head = node;
        }
        size++;
    }

    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        XorListNode<T> cur = head;
        long prevAddr = 0;
        for (int i = 0; i < index; i++) {
            long nextAddr = cur.xorPointer ^ prevAddr;
            if (nextAddr == 0) throw new NoSuchElementException();
            prevAddr = System.identityHashCode(cur);
            cur = addrToNode(nextAddr);
        }
        return cur.value;
    }

    public T removeFirst() {
        if (head == null) throw new NoSuchElementException();
        T val = head.value;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            long nextAddr = head.xorPointer;
            XorListNode<T> next = addrToNode(nextAddr);
            next.xorPointer ^= System.identityHashCode(head);
            head = next;
        }
        size--;
        return val;
    }

    public T removeLast() {
        if (tail == null) throw new NoSuchElementException();
        T val = tail.value;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            long prevAddr = tail.xorPointer;
            XorListNode<T> prev = addrToNode(prevAddr);
            prev.xorPointer ^= System.identityHashCode(tail);
            tail = prev;
        }
        size--;
        return val;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    @SuppressWarnings("unchecked")
    private XorListNode<T> addrToNode(long addr) {
        return (XorListNode<T>) NodeRef.getNode(addr);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            XorListNode<T> cur = head;
            long prevAddr = 0;
            @Override
            public boolean hasNext() { return cur != null; }
            @Override
            public T next() {
                if (cur == null) throw new NoSuchElementException();
                T val = cur.value;
                long nextAddr = cur.xorPointer ^ prevAddr;
                prevAddr = System.identityHashCode(cur);
                cur = nextAddr != 0 ? addrToNode(nextAddr) : null;
                return val;
            }
        };
    }
}

class NodeRef {
    static Object getNode(long addr) {
        return null;
    }
}
