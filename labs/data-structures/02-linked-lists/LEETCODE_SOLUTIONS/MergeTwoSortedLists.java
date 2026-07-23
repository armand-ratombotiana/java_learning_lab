package com.leetcode.linkedlist;

/**
 * LeetCode 21: Merge Two Sorted Lists
 * https://leetcode.com/problems/merge-two-sorted-lists/
 *
 * Merge two sorted linked lists into one sorted list.
 *
 * Time Complexity: O(n + m)
 * Space Complexity: O(1)
 */
public class MergeTwoSortedLists {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode(int val) { this.val = val; }
    }

    /**
     * Approach 1 (Optimal): Iterative with dummy head
     * Compare nodes and attach the smaller one.
     * Time: O(n + m), Space: O(1)
     */
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }

        current.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }

    /**
     * Approach 2: Recursive
     * Time: O(n + m), Space: O(n + m) for recursion stack
     */
    public ListNode mergeTwoListsRecursive(ListNode list1, ListNode list2) {
        if (list1 == null) return list2;
        if (list2 == null) return list1;

        if (list1.val <= list2.val) {
            list1.next = mergeTwoListsRecursive(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoListsRecursive(list1, list2.next);
            return list2;
        }
    }

    public static void main(String[] args) {
        MergeTwoSortedLists mtsl = new MergeTwoSortedLists();

        ListNode l1 = new ListNode(1);
        l1.next = new ListNode(2);
        l1.next.next = new ListNode(4);

        ListNode l2 = new ListNode(1);
        l2.next = new ListNode(3);
        l2.next.next = new ListNode(4);

        ListNode merged = mtsl.mergeTwoLists(l1, l2);
        System.out.print("Test 1: ");
        printList(merged);
        System.out.println(" (expected: 1 1 2 3 4 4)");

        // Empty list
        ListNode empty = null;
        ListNode single = new ListNode(0);
        ListNode merged2 = mtsl.mergeTwoLists(empty, single);
        System.out.print("Test 2 (empty + one): ");
        printList(merged2);
        System.out.println(" (expected: 0)");

        // Both empty
        System.out.println("Test 3 (both empty): " + (mtsl.mergeTwoLists(null, null) == null ? "null" : "fail"));
    }

    private static void printList(ListNode head) {
        while (head != null) {
            System.out.print(head.val + " ");
            head = head.next;
        }
    }
}
