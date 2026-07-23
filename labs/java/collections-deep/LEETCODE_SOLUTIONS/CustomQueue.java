package collectionsdeep;

import java.util.*;

/**
 * Custom Queue implementation using a linked list internally.
 * 
 * Demonstrates: FIFO semantics, peek/poll/offer, dynamic sizing.
 * Alternative: Circular array-based queue (better cache locality).
 * 
 * Time: O(1) per operation
 * Space: O(n)
 */
public class CustomQueue<E> implements Iterable<E> {
    private Node<E> head, tail;
    private int size = 0;

    private static class Node<E> {
        final E value;
        Node<E> next;
        Node(E v) { value = v; }
    }

    public void offer(E element) {
        Node<E> node = new Node<>(element);
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public E poll() {
        if (head == null) return null;
        E val = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return val;
    }

    public E peek() {
        return head == null ? null : head.value;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public Iterator<E> iterator() {
        return new Iterator<>() {
            Node<E> cur = head;
            public boolean hasNext() { return cur != null; }
            public E next() {
                if (!hasNext()) throw new NoSuchElementException();
                E val = cur.value;
                cur = cur.next;
                return val;
            }
        };
    }

    public static void main(String[] args) {
        CustomQueue<Integer> q = new CustomQueue<>();
        assert q.isEmpty();
        assert q.poll() == null;

        q.offer(1);
        q.offer(2);
        q.offer(3);
        assert q.size() == 3;
        assert q.peek() == 1;
        assert q.poll() == 1;
        assert q.poll() == 2;
        assert q.peek() == 3;
        assert q.size() == 1;
        q.poll();
        assert q.isEmpty();

        q.offer(4);
        q.offer(5);
        ArrayList<Integer> list = new ArrayList<>();
        for (int v : q) list.add(v);
        assert list.equals(List.of(4, 5));

        System.out.println("All CustomQueue tests passed.");
    }
}