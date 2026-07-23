package com.leetcode.linkedlist;

/**
 * LeetCode 138: Copy List with Random Pointer
 * https://leetcode.com/problems/copy-list-with-random-pointer/
 *
 * A linked list where each node has a random pointer to any node or null.
 * Create a deep copy.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1) (excluding output)
 */
public class CopyListWithRandom {

    public static class Node {
        int val;
        Node next;
        Node random;
        Node(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Interweaving nodes
     * 1. Insert copy nodes after each original node
     * 2. Set random pointers for copy nodes
     * 3. Separate the lists
     * Time: O(n), Space: O(1)
     */
    public Node copyRandomList(Node head) {
        if (head == null) return null;

        Node current = head;
        while (current != null) {
            Node copy = new Node(current.val);
            copy.next = current.next;
            current.next = copy;
            current = copy.next;
        }

        current = head;
        while (current != null) {
            if (current.random != null) {
                current.next.random = current.random.next;
            }
            current = current.next.next;
        }

        Node dummy = new Node(0);
        Node copyCurrent = dummy;
        current = head;
        while (current != null) {
            copyCurrent.next = current.next;
            copyCurrent = copyCurrent.next;
            current.next = current.next.next;
            current = current.next;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        CopyListWithRandom clwr = new CopyListWithRandom();

        Node node1 = new Node(7);
        Node node2 = new Node(13);
        Node node3 = new Node(11);
        Node node4 = new Node(10);
        Node node5 = new Node(1);

        node1.next = node2; node2.next = node3;
        node3.next = node4; node4.next = node5;

        node2.random = node1;
        node3.random = node5;
        node4.random = node3;
        node5.random = node1;

        Node copied = clwr.copyRandomList(node1);
        System.out.print("Test 1 copied list: ");
        Node cur = copied;
        while (cur != null) {
            System.out.print("[" + cur.val + "," + (cur.random != null ? cur.random.val : "null") + "] ");
            cur = cur.next;
        }
        System.out.println(" (expected: [7,null] [13,7] [11,1] [10,11] [1,7])");

        System.out.println("Test 2 (null): " + (clwr.copyRandomList(null) == null ? "null" : "fail"));

        Node single = new Node(42);
        Node copied2 = clwr.copyRandomList(single);
        System.out.println("Test 3 (single): val=" + copied2.val + ", random=" + (copied2.random == null ? "null" : "not null"));
    }
}
