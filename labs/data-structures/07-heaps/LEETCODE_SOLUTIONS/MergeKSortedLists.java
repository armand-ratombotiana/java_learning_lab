package com.leetcode.heaps;

import java.util.PriorityQueue;

/**
 * LeetCode 23: Merge k Sorted Lists
 * https://leetcode.com/problems/merge-k-sorted-lists/
 *
 * Merge k sorted linked lists into one sorted list.
 *
 * Time Complexity: O(n log k) where n is total nodes, k is number of lists
 * Space Complexity: O(k)
 */
public class MergeKSortedLists {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Min-Heap
     * Put the head of each list in a min-heap. Extract min and add the next node.
     * Time: O(n log k), Space: O(k)
     */
    public ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> a.val - b.val);

        for (ListNode list : lists) {
            if (list != null) minHeap.offer(list);
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (!minHeap.isEmpty()) {
            ListNode node = minHeap.poll();
            current.next = node;
            current = current.next;
            if (node.next != null) minHeap.offer(node.next);
        }

        return dummy.next;
    }

    /**
     * Approach 2: Divide and Conquer
     * Pair up lists and merge them recursively.
     * Time: O(n log k), Space: O(log k)
     */

    public static void main(String[] args) {
        MergeKSortedLists mksl = new MergeKSortedLists();

        ListNode l1 = new ListNode(1);
        l1.next = new ListNode(4);
        l1.next.next = new ListNode(5);

        ListNode l2 = new ListNode(1);
        l2.next = new ListNode(3);
        l2.next.next = new ListNode(4);

        ListNode l3 = new ListNode(2);
        l3.next = new ListNode(6);

        ListNode merged = mksl.mergeKLists(new ListNode[] { l1, l2, l3 });
        System.out.print("Test 1: ");
        printList(merged);
        System.out.println(" (expected: 1 1 2 3 4 4 5 6)");

        // Null lists
        System.out.println("Test 2: " + (mksl.mergeKLists(new ListNode[] {}) == null ? "null" : "fail"));

        // All null
        System.out.println("Test 3: " + (mksl.mergeKLists(new ListNode[] { null, null }) == null ? "null" : "fail"));
    }

    private static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
    }
}
