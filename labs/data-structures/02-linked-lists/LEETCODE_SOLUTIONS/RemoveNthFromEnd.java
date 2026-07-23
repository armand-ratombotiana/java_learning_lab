package com.leetcode.linkedlist;

/**
 * LeetCode 19: Remove Nth Node From End of List
 * https://leetcode.com/problems/remove-nth-node-from-end-of-list/
 *
 * Remove the nth node from the end of a linked list.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class RemoveNthFromEnd {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Two pointers
     * Move first pointer n steps ahead, then move both.
     * When first reaches the end, second is at the node before the target.
     * Time: O(n), Space: O(1)
     */
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode first = dummy;
        ListNode second = dummy;

        for (int i = 0; i <= n; i++) {
            first = first.next;
        }

        while (first != null) {
            first = first.next;
            second = second.next;
        }

        second.next = second.next.next;
        return dummy.next;
    }

    /**
     * Approach 2: Two pass
     * First pass: find length. Second pass: remove (len - n)th node.
     * Time: O(n), Space: O(1)
     */
    public ListNode removeNthFromEndTwoPass(ListNode head, int n) {
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }

        length -= n;
        current = dummy;
        while (length > 0) {
            length--;
            current = current.next;
        }
        current.next = current.next.next;
        return dummy.next;
    }

    public static void main(String[] args) {
        RemoveNthFromEnd rnfe = new RemoveNthFromEnd();

        ListNode head = new ListNode(1);
        head.next = new ListNode(2);
        head.next.next = new ListNode(3);
        head.next.next.next = new ListNode(4);
        head.next.next.next.next = new ListNode(5);

        ListNode result = rnfe.removeNthFromEnd(head, 2);
        System.out.print("Test 1: ");
        printList(result);
        System.out.println(" (expected: 1 2 3 5)");

        // Single element
        ListNode head2 = new ListNode(1);
        ListNode result2 = rnfe.removeNthFromEnd(head2, 1);
        System.out.print("Test 2: ");
        printList(result2);
        System.out.println(" (expected: )");

        // Two elements
        ListNode head3 = new ListNode(1);
        head3.next = new ListNode(2);
        ListNode result3 = rnfe.removeNthFromEnd(head3, 1);
        System.out.print("Test 3: ");
        printList(result3);
        System.out.println(" (expected: 1)");
    }

    private static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
    }
}
