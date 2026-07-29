# LeetCode 23: Merge k Sorted Lists

> **Difficulty**: Hard | **Company**: Amazon, Google, Microsoft, Meta, Apple | **Category**: Collections Deep (PriorityQueue)

## Problem

You are given an array of `k` linked-lists `lists`, each linked-list is sorted in ascending order. Merge all the linked-lists into one sorted linked-list and return it.

## Solution

Uses a `PriorityQueue` (min-heap) to always extract the smallest current node among all k lists. This is the classic "merge k sorted lists using a heap" approach.

```java
/**
 * LeetCode 23: Merge k Sorted Lists
 *
 * Approach: PriorityQueue (min-heap) tracks the smallest head across all lists.
 *
 * Time: O(N log k) where N = total nodes, k = number of lists
 * Space: O(k) for the heap
 */
public class MergeKSortedLists {

    public static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }

    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        java.util.PriorityQueue<ListNode> pq = new java.util.PriorityQueue<>(
            lists.length, (a, b) -> Integer.compare(a.val, b.val));

        for (ListNode head : lists) {
            if (head != null) pq.offer(head);
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (!pq.isEmpty()) {
            ListNode smallest = pq.poll();
            tail.next = smallest;
            tail = tail.next;
            if (smallest.next != null) pq.offer(smallest.next);
        }

        return dummy.next;
    }

    // ─────────────────────
    // Verification
    // ─────────────────────
    public static void main(String[] args) {
        var s = new MergeKSortedLists();

        // Example 1: lists = [[1,4,5],[1,3,4],[2,6]]
        ListNode l1 = list(1, 4, 5);
        ListNode l2 = list(1, 3, 4);
        ListNode l3 = list(2, 6);
        ListNode merged = s.mergeKLists(new ListNode[]{l1, l2, l3});
        System.out.print("Merged: ");
        print(merged);  // 1 → 1 → 2 → 3 → 4 → 4 → 5 → 6
        assert checkSorted(merged) : "List should be sorted";

        // Empty input
        assert s.mergeKLists(new ListNode[]{}) == null;
        assert s.mergeKLists(null) == null;

        // Single list
        ListNode single = list(5);
        ListNode res = s.mergeKLists(new ListNode[]{single});
        assert res.val == 5 && res.next == null;

        System.out.println("All tests passed.");
    }

    private static ListNode list(int... vals) {
        ListNode dummy = new ListNode(0), tail = dummy;
        for (int v : vals) { tail.next = new ListNode(v); tail = tail.next; }
        return dummy.next;
    }

    private static void print(ListNode head) {
        while (head != null) { System.out.print(head.val + " → "); head = head.next; }
        System.out.println("null");
    }

    private static boolean checkSorted(ListNode head) {
        while (head != null && head.next != null) {
            if (head.val > head.next.val) return false;
            head = head.next;
        }
        return true;
    }
}
```

## Complexity

| Metric          | Value       |
|-----------------|-------------|
| Time            | O(N log k)  |
| Space           | O(k)        |

## Key Insights

1. **Min-heap selection**: The heap always contains at most k nodes (one per list). Polling gives the smallest.
2. **No full sort**: We never sort all N nodes; we only compare k elements at a time.
3. **Alternative approaches**: 
   - Divide & conquer (merge pairs recursively) — O(N log k) time, O(1) extra space (ignoring recursion stack).
   - Brute force — collect all values, sort, rebuild — O(N log N).
