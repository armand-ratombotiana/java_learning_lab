# Problem Walkthrough: 02-Linked-Lists

## Problem 1: Reverse Linked List (LC 206) — Meta

### Interview Scenario
"Meta interviewer: 'Given the head of a singly linked list, reverse it and return the new head.'"

### The Problem
Reverse a singly linked list. Return the head of the reversed list.

### Step 1: Clarify (30 seconds)
- **Q:** Is this singly or doubly linked? **A:** Singly.
- **Q:** Can the list be empty? **A:** Yes, return null.
- **Q:** Single node? **A:** Return it as-is.
- **Edge cases:** Null head, single node, two nodes, long list.

### Step 2: Brute Force (2 min)
- Copy values to an array, then rebuild the list backwards.
- **Time:** O(n) — but uses O(n) extra space unnecessarily.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Iterative approach: use three pointers — prev, current, next. At each step, reverse the current node's pointer to prev, then advance all three pointers."
- O(n) time, O(1) space — optimal.
- **Why Meta likes it:** It's the foundation for 50% of linked list problems. They want to see you handle pointer manipulation cleanly.

### Step 4: Code (10 min)

```java
/**
 * Reverses a singly linked list in-place.
 * <p>
 * Time: O(n) | Space: O(1)
 */
public class Solution {
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
}
```

### Step 5: Test (3 min)
- **Edge:** head = null → null
- **Edge:** head = [1] → [1]
- **Example:** head = [1, 2, 3, 4, 5] → [5, 4, 3, 2, 1]
- Walk through pointer state at each iteration on the whiteboard.

### Step 6: Follow-ups
- "Now do it recursively." — Recursive version: base case null or single node, reverse rest, point head.next.next = head, head.next = null.
- "Reverse a doubly linked list." — Same idea, swap prev and next at each node.
- "Reverse between positions m and n." — Reverse Linked List II (LC 92).
- **What Meta looks for:** Can you write both iterative and recursive from memory? Clean pointer handling.

### Company Evaluation Criteria
- **Meta:** Code correctness and cleanliness. They watch for null pointer access.
- **Amazon:** Would ask about reversing in chunks of k.
- **Google:** Would explore recursive vs iterative trade-offs in depth.

---

## Problem 2: Merge Two Sorted Lists (LC 21) — Amazon

### Interview Scenario
"Amazon interviewer: 'You have two sorted linked lists. Merge them into one sorted list.'"

### The Problem
Merge two sorted linked lists and return the head of the merged list.

### Step 1: Clarify (30 seconds)
- **Q:** Can the lists have different lengths? **A:** Yes.
- **Q:** One or both empty? **A:** Return the non-null one.
- **Q:** Duplicates? **A:** Include all, standard merge.
- **Edge cases:** Both null, one null, same-length lists, one list much longer.

### Step 2: Brute Force (2 min)
- Extract all values into a list, sort it, rebuild a linked list.
- **Time:** O((n+m) log(n+m)) — sorting is wasteful.
- **Space:** O(n+m).

### Step 3: Optimize (5 min)
- "Use a dummy head and a two-pointer merge, like the merge step in merge sort. Compare current nodes and append the smaller one."
- O(n+m) time, O(1) space.
- **Why Amazon loves this:** It's a building block for merge sort, which they use at scale for distributed data processing.

### Step 4: Code (10 min)

```java
/**
 * Merges two sorted linked lists into one sorted list.
 * <p>
 * Time: O(n + m) | Space: O(1)
 */
public class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val < list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }

        tail.next = (list1 != null) ? list1 : list2;
        return dummy.next;
    }
}
```

### Step 5: Test (3 min)
- **Edge:** both null → null
- **Edge:** list1 = [], list2 = [0] → [0]
- **Example:** list1 = [1, 2, 4], list2 = [1, 3, 4] → [1, 1, 2, 3, 4, 4]
- Show the dummy head technique and why it simplifies code (no special case for head).

### Step 6: Follow-ups
- "Merge k sorted lists." — Use a min-heap of size k (LC 23).
- "What if the input is too large for memory?" — External merge sort approach.
- "Merge in place without dummy — can you?" — Yes, but edge cases increase complexity.
- **What Amazon looks for:** Can you build reusable components? The dummy head pattern shows engineering maturity.

### Company Evaluation Criteria
- **Amazon:** Correctness, then optimization. They value solutions that are easy to verify as correct.
- **Meta:** Would ask about recursive merge.
- **Google:** Would explore stability of the merge and how duplicates are handled.

---

## Problem 3: Linked List Cycle (LC 141) — Google

### Interview Scenario
"Google interviewer: 'Given head of a linked list, determine if it has a cycle.'"

### The Problem
Detect if a linked list has a cycle. Return true if there is a cycle, false otherwise.

### Step 1: Clarify (30 seconds)
- **Q:** Can I modify the list? **A:** No.
- **Q:** What's the node limit? **A:** Could be up to 10^4 nodes.
- **Q:** Singly linked? **A:** Yes.
- **Edge cases:** Empty list, single node pointing to itself, tail connecting to middle, full cycle.

### Step 2: Brute Force (2 min)
- Use a HashSet to track visited nodes. If a node is visited twice, there's a cycle.
- **Time:** O(n) — fine.
- **Space:** O(n) — could be better.

### Step 3: Optimize (5 min)
- "Floyd's Tortoise and Hare: two pointers — slow moves one step, fast moves two steps. If they meet, there's a cycle. If fast reaches null, no cycle."
- O(n) time, O(1) space — the interview gold standard.
- **Why Google values this:** It's a clever algorithm that shows you've studied classic techniques. They'll want to see you prove why it works mathematically.

### Step 4: Code (10 min)

```java
/**
 * Detects if a linked list has a cycle using Floyd's algorithm.
 * <p>
 * Time: O(n) | Space: O(1)
 */
public class Solution {
    public boolean hasCycle(ListNode head) {
        if (head == null) return false;

        ListNode slow = head;
        ListNode fast = head.next;

        while (slow != fast) {
            if (fast == null || fast.next == null) {
                return false;
            }
            slow = slow.next;
            fast = fast.next.next;
        }

        return true;
    }
}
```

### Step 5: Test (3 min)
- **Edge:** head = null → false
- **Edge:** head = [1] → false
- **Example:** head = [3, 2, 0, -4], pos = 1 → true (tail connects to index 1)
- **Example:** head = [1, 2], pos = 0 → true
- **Example:** head = [1], pos = -1 → false
- Walk through the meeting point proof on the whiteboard.

### Step 6: Follow-ups
- "Return the node where the cycle begins." — After meeting, reset one pointer to head, move both at same speed until they meet (LC 142).
- "Find the length of the cycle." — After meeting, continue counting until they meet again.
- "Why does Floyd's algorithm work?" — Discuss modular arithmetic: when slow has traveled k steps, fast has traveled 2k. If cycle length is L, they meet when k ≡ 2k (mod L), i.e., k ≡ 0 (mod L).
- **What Google looks for:** Deep understanding of the algorithm, not just rote memorization. Can you prove correctness?

### Company Evaluation Criteria
- **Google:** Proof of correctness and mathematical reasoning. They love Floyd's algorithm discussions.
- **Amazon:** Would ask about detecting cycles in a concurrent linked list.
- **Meta:** Would ask the "return cycle start" variant.

---

## Study Notes

### Key Patterns
- **Pointer manipulation:** Three-pointer reversal is the foundation
- **Dummy head technique:** Eliminates null checks for the head
- **Two-pointer (slow/fast):** Cycle detection, finding middle, finding kth from end
- **Runner technique:** One pointer ahead of the other by a fixed offset

### Common Mistakes
- Forgetting to advance both pointers in merge
- Null pointer access on .next of a null node (always check fast != null && fast.next != null)
- Infinite loops when pointer updates are skipped
- Using recursion when the list is long (stack overflow)

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Iterative reverse | O(n) | O(1) |
| Recursive reverse | O(n) | O(n) |
| Two-pointer merge | O(n+m) | O(1) |
| Floyd's cycle | O(n) | O(1) |
| HashMap cycle | O(n) | O(n) |
| Find middle (slow/fast) | O(n) | O(1) |
