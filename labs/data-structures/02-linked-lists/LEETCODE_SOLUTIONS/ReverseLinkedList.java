package com.leetcode.linkedlist;

/**
 * LeetCode 206: Reverse Linked List
 * https://leetcode.com/problems/reverse-linked-list/
 *
 * Given the head of a singly linked list, reverse the list and return its head.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class ReverseLinkedList {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Iterative
     * Use three pointers (prev, current, next) to reverse links.
     * Time: O(n), Space: O(1)
     */
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        return prev;
    }

    /**
     * Approach 2: Recursive
     * Reverse the rest, then fix the current node.
     * Time: O(n), Space: O(n) for recursion stack
     */
    public ListNode reverseListRecursive(ListNode head) {
        if (head == null || head.next == null) return head;
        ListNode newHead = reverseListRecursive(head.next);
        head.next.next = head;
        head.next = null;
        return newHead;
    }

    public static void main(String[] args) {
        ReverseLinkedList rll = new ReverseLinkedList();

        // Create list: 1 -> 2 -> 3 -> 4 -> 5
        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        ListNode reversed = rll.reverseList(head);
        System.out.print("Test 1 (iterative): ");
        printList(reversed);
        System.out.println(" (expected: 5 4 3 2 1)");

        // Recursive
        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(2);
        head2.next.next = new ListNode(3);

        ListNode reversed2 = rll.reverseListRecursive(head2);
        System.out.print("Test 2 (recursive): ");
        printList(reversed2);
        System.out.println(" (expected: 3 2 1)");

        // Single element
        ListNode head3 = new ListNode(42);
        ListNode reversed3 = rll.reverseList(head3);
        System.out.print("Test 3 (single): ");
        printList(reversed3);
        System.out.println(" (expected: 42)");

        // Null
        System.out.println("Test 4 (null): " + (rll.reverseList(null) == null ? "null" : "fail"));
    }

    private static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
    }
}
