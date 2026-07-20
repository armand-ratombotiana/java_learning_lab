package com.javalab.09;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

public class CopyOnWriteArrayList<E> implements Iterable<E> {

    private volatile Object[] array;
    private final transient ReentrantLock lock = new ReentrantLock();

    public CopyOnWriteArrayList() {
        this.array = new Object[0];
    }

    public CopyOnWriteArrayList(E[] initialArray) {
        this.array = Arrays.copyOf(initialArray, initialArray.length);
    }

    public boolean add(E element) {
        Objects.requireNonNull(element);
        lock.lock();
        try {
            Object[] current = array;
            int len = current.length;
            Object[] newArray = Arrays.copyOf(current, len + 1);
            newArray[len] = element;
            array = newArray;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public void add(int index, E element) {
        Objects.requireNonNull(element);
        lock.lock();
        try {
            Object[] current = array;
            int len = current.length;
            if (index < 0 || index > len) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
            }
            Object[] newArray = new Object[len + 1];
            System.arraycopy(current, 0, newArray, 0, index);
            newArray[index] = element;
            System.arraycopy(current, index, newArray, index + 1, len - index);
            array = newArray;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public E get(int index) {
        Object[] current = array;
        if (index < 0 || index >= current.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + current.length);
        }
        return (E) current[index];
    }

    @SuppressWarnings("unchecked")
    public E set(int index, E element) {
        Objects.requireNonNull(element);
        lock.lock();
        try {
            Object[] current = array;
            int len = current.length;
            if (index < 0 || index >= len) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
            }
            E old = (E) current[index];
            if (old != element) {
                Object[] newArray = Arrays.copyOf(current, len);
                newArray[index] = element;
                array = newArray;
            }
            return old;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public E remove(int index) {
        lock.lock();
        try {
            Object[] current = array;
            int len = current.length;
            if (index < 0 || index >= len) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + len);
            }
            E old = (E) current[index];
            int numMoved = len - index - 1;
            Object[] newArray = new Object[len - 1];
            System.arraycopy(current, 0, newArray, 0, index);
            if (numMoved > 0) {
                System.arraycopy(current, index + 1, newArray, index, numMoved);
            }
            array = newArray;
            return old;
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Object o) {
        lock.lock();
        try {
            Object[] current = array;
            int index = indexOf(o);
            if (index < 0) return false;
            remove(index);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public int indexOf(Object o) {
        Object[] current = array;
        for (int i = 0; i < current.length; i++) {
            if (Objects.equals(current[i], o)) return i;
        }
        return -1;
    }

    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    public int size() {
        return array.length;
    }

    public boolean isEmpty() {
        return array.length == 0;
    }

    public void clear() {
        lock.lock();
        try {
            array = new Object[0];
        } finally {
            lock.unlock();
        }
    }

    public Iterator<E> iterator() {
        return new SnapshotIterator(array);
    }

    private static class SnapshotIterator<E> implements Iterator<E> {
        private final Object[] snapshot;
        private int cursor;

        SnapshotIterator(Object[] snapshot) {
            this.snapshot = snapshot;
            this.cursor = 0;
        }

        public boolean hasNext() {
            return cursor < snapshot.length;
        }

        @SuppressWarnings("unchecked")
        public E next() {
            if (!hasNext()) throw new NoSuchElementException();
            return (E) snapshot[cursor++];
        }
    }

    public String toString() {
        return Arrays.toString(array);
    }
}
