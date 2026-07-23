package com.leetcode.linkedlist;

/**
 * LeetCode 141: Linked List Cycle
 * https://leetcode.com/problems/linked-list-cycle/
 *
 * Given head of a linked list, determine if the list has a cycle.
 *
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class LinkedListCycle {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Floyd's Cycle Detection (Hare-Tortoise)
     * Slow pointer moves 1 step, fast pointer moves 2 steps.
     * If they meet, there's a cycle.
     * Time: O(n), Space: O(1)
     */
    public boolean hasCycle(ListNode head) {
        if (head == null || head.next == null) return false;

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    /**
     * Approach 2: HashSet
     * Use a set to track visited nodes.
     * Time: O(n), Space: O(n)
     */

    public static void main(String[] args) {
        LinkedListCycle llc = new LinkedListCycle();

        // Create cycle: 1 -> 2 -> 3 -> 4 -> 2 (cycle)
        ListNode head = new ListNode(1);
        ListNode node2 = new ListNode(2);
        ListNode node3 = new ListNode(3);
        ListNode node4 = new ListNode(4);
        head.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = node2; // creates cycle

        System.out.println("Test 1 (has cycle): " + llc.hasCycle(head) + " (expected: true)");

        // No cycle: 1 -> 2 -> 3 -> 4
        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(2);
        head2.next.next = new ListNode(3);
        head2.next.next.next = new ListNode(4);

        System.out.println("Test 2 (no cycle): " + llc.hasCycle(head2) + " (expected: false)");

        // Single element, no cycle
        System.out.println("Test 3 (single): " + llc.hasCycle(new ListNode(1)) + " (expected: false)");

        // Null
        System.out.println("Test 4 (null): " + llc.hasCycle(null) + " (expected: false)");

        // Self-cycle
        ListNode self = new ListNode(1);
        self.next = self;
        System.out.println("Test 5 (self cycle): " + llc.hasCycle(self) + " (expected: true)");
    }
}
