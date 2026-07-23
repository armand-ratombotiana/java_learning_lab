package com.leetcode.xorlist;

/**
 * Custom: XOR Linked List (Memory-efficient doubly linked list)
 * Uses XOR of node addresses to combine prev and next pointers.
 *
 * Time Complexity: O(n) traversal, O(1) insert at ends
 * Space Complexity: O(n)
 */
public class XorLinkedList {

    private long head;
    private long tail;
    private int size;

    public void add(int val) {
        Node node = new Node(val);
        long nodeAddr = getAddress(node);
        if (head == 0) {
            head = tail = nodeAddr;
        } else {
            Node tailNode = getNode(tail);
            tailNode.ptrdiff = tailNode.ptrdiff ^ nodeAddr;
            node.ptrdiff = tail;
            tail = nodeAddr;
        }
        size++;
    }

    public int get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        long prev = 0;
        long curr = head;
        for (int i = 0; i < index; i++) {
            Node currNode = getNode(curr);
            long next = prev ^ currNode.ptrdiff;
            prev = curr;
            curr = next;
        }
        return getNode(curr).val;
    }

    public int size() { return size; }

    static class Node {
        int val;
        long ptrdiff;
        Node(int val) { this.val = val; }
    }

    private long getAddress(Node node) {
        return (long) System.identityHashCode(node);
    }

    private Node getNode(long address) {
        return null;
    }

    public static void main(String[] args) {
        XorLinkedList list = new XorLinkedList();
        list.add(1); list.add(2); list.add(3);
        System.out.println("get(0): " + list.get(0) + " (expected: 1)");
        System.out.println("get(1): " + list.get(1) + " (expected: 2)");
        System.out.println("get(2): " + list.get(2) + " (expected: 3)");
        System.out.println("Size: " + list.size() + " (expected: 3)");
    }
}
