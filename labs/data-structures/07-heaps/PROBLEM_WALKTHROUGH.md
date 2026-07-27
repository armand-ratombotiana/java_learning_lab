# Problem Walkthrough: 07-Heaps

## Problem 1: Kth Largest Element in an Array (LC 215) — Amazon

### Interview Scenario
"Amazon interviewer: 'Given an unsorted array of integers nums and an integer k, return the kth largest element.'"

### The Problem
Find the kth largest element in a given array. It's the kth largest in sorted order, not the kth distinct element.

### Step 1: Clarify (30 seconds)
- **Q:** Can k be larger than the array length? **A:** No, 1 ≤ k ≤ n.
- **Q:** Are there duplicates? **A:** Yes — duplicates are counted separately.
- **Q:** Can I sort the array? **A:** Yes, but there's a better way.
- **Q:** Negative numbers? **A:** Yes.
- **Edge cases:** k = 1 (largest), k = n (smallest), all same values, negative numbers, large k with small n.

### Step 2: Brute Force (2 min)
- Sort the array descending, return element at index k-1.
- **Time:** O(n log n) — sorting is more work than needed.
- **Space:** O(1) (in-place sort) or O(n) depending on sort.

### Step 3: Optimize (5 min)
- "Use a min-heap of size k. Iterate through elements: add to heap. If heap size > k, remove the smallest (poll). At the end, the top of the heap is the kth largest."
- O(n log k) time — heap operations are O(log k), and we do n of them.
- Can also use QuickSelect (average O(n), worst O(n²)).
- **Why Amazon values this:** The min-heap pattern handles streaming data and large datasets well. Amazon uses this for top-k product rankings and real-time analytics.

### Step 4: Code (10 min)

```java
import java.util.PriorityQueue;

/**
 * Finds the kth largest element in an array using a min-heap.
 * <p>
 * Time: O(n log k) | Space: O(k)
 */
public class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();

        for (int num : nums) {
            heap.offer(num);
            if (heap.size() > k) {
                heap.poll();
            }
        }

        return heap.peek();
    }
}
```

### Step 5: Test (3 min)
- **Example 1:** nums = [3, 2, 1, 5, 6, 4], k = 2 → 5
- **Example 2:** nums = [3, 2, 3, 1, 2, 4, 5, 5, 6], k = 4 → 4
- **Edge:** nums = [1], k = 1 → 1
- **Edge:** nums = [2, 2, 2], k = 1 → 2
- Walk through heap state after each insertion.

### Step 6: Follow-ups
- "What if k is much smaller than n?" — Min-heap wins (O(n log k) ≈ O(n) for small k).
- "What if n is very large (streaming, 10⁹ elements)?" — Min-heap still works in O(n log k). QuickSelect is not usable for streaming.
- "Kth smallest?" — Use max-heap of size k.
- **What Amazon looks for:** Can you handle the streaming/data pipeline version? Min-heap scales horizontally.

### Company Evaluation Criteria
- **Amazon:** Practicality — they love the heap approach because it's real-time and streaming-friendly.
- **Google:** Would ask about QuickSelect and median of medians.
- **Meta:** Would ask about kth largest in a sorted matrix (heap + binary search).

---

## Problem 2: Merge k Sorted Lists (LC 23) — Google

### Interview Scenario
"Google interviewer: 'You are given an array of k linked-lists, each sorted in ascending order. Merge all into one sorted list.'"

### The Problem
Merge k sorted linked lists into a single sorted linked list.

### Step 1: Clarify (30 seconds)
- **Q:** Can the lists be empty? **A:** Yes, some or all may be null.
- **Q:** k value? **A:** Up to 10^4. Total nodes up to 10^4.
- **Q:** Duplicates? **A:** Yes, include all.
- **Q:** Can I modify the original lists? **A:** Yes, reuse nodes.
- **Edge cases:** All lists empty, single list, lists of varying lengths, all lists with one element.

### Step 2: Brute Force (2 min)
- Collect all values into a list, sort, rebuild a linked list.
- **Time:** O(N log N) where N = total nodes — wasteful sort.
- **Space:** O(N).

### Step 3: Optimize (5 min)
- "Use a min-heap to store the head of each list. Always extract the smallest node, add it to the result, and push the next node from that list back into the heap."
- O(N log k) time — each of N nodes is pushed/popped once, heap operations are O(log k).
- O(k) space for the heap.
- **Why Google values this:** Heap-based merging is a building block for external sort, MapReduce reduce step, and log merging in Google's infrastructure.

### Step 4: Code (10 min)

```java
import java.util.PriorityQueue;

/**
 * Merges k sorted linked lists using a min-heap.
 * <p>
 * Time: O(N log k) | Space: O(k)
 */
public class Solution {
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        PriorityQueue<ListNode> heap = new PriorityQueue<>(
            (a, b) -> a.val - b.val
        );

        for (ListNode list : lists) {
            if (list != null) {
                heap.offer(list);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (!heap.isEmpty()) {
            ListNode node = heap.poll();
            tail.next = node;
            tail = tail.next;
            if (node.next != null) {
                heap.offer(node.next);
            }
        }

        return dummy.next;
    }
}
```

### Step 5: Test (3 min)
- **Edge:** lists = [] → null
- **Edge:** lists = [null, null] → null
- **Example:** lists = [[1,4,5],[1,3,4],[2,6]] → [1,1,2,3,4,4,5,6]
- **Edge:** lists = [[1], [2], [3]] → [1, 2, 3]
- Walk through heap state: initially 3 nodes, extract min, push its next.

### Step 6: Follow-ups
- "Without a heap — divide and conquer merge (pairwise merge)?" — O(N log k) with O(1) extra space. Good as a follow-up.
- "What about merging arrays instead of lists?" — Same heap approach, track index per array.
- "What if k is huge (10⁶ empty lists, 10 full)?" — Only push non-null heads into heap.
- **What Google looks for:** Understanding of when heap is the right tool vs. divide-and-conquer. Trade-off analysis.

### Company Evaluation Criteria
- **Google:** Algorithm selection — why heap over divide-and-conquer? They want a thorough comparison.
- **Amazon:** Would ask about streaming merge (limited memory).
- **Meta:** Would ask about merging k sorted iterators.

---

## Problem 3: Find Median from Data Stream (LC 295) — Meta

### Interview Scenario
"Meta interviewer: 'Design a data structure that supports adding numbers and finding the median in O(1) average time.'"

### The Problem
Implement a class with `addNum(int num)` and `findMedian() -> double`. The median is the middle value — average of two middle values if the count is even.

### Step 1: Clarify (30 seconds)
- **Q:** Data range? **A:** -10⁵ to 10⁵.
- **Q:** Number of operations? **A:** Up to 5 * 10⁴.
- **Q:** Empty state? **A:** findMedian() will not be called on empty structure.
- **Q:** Are the numbers mostly sorted? **A:** No assumption.
- **Edge cases:** Single element, two elements (median is average), all same numbers, large numbers, interleaved ascending/descending stream.

### Step 2: Brute Force (2 min)
- Maintain a sorted list. Insert in O(n) position. Find median in O(1) by index.
- **Time:** O(n) per insert — too slow.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Use two heaps: a max-heap for the smaller half (left) and a min-heap for the larger half (right). Keep sizes balanced — left has either same size as right or one more."
- addNum: O(log n). findMedian: O(1).
- **Why Meta values this:** Real-time data analysis. Meta uses median and percentile tracking for engagement metrics, latency monitoring, and A/B test analysis.

### Step 4: Code (10 min)

```java
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Maintains a data stream and returns the median efficiently.
 * <p>
 * addNum: O(log n) | findMedian: O(1) | Space: O(n)
 */
public class MedianFinder {
    private PriorityQueue<Integer> left;  // max-heap
    private PriorityQueue<Integer> right; // min-heap

    public MedianFinder() {
        left = new PriorityQueue<>(Collections.reverseOrder());
        right = new PriorityQueue<>();
    }

    public void addNum(int num) {
        left.offer(num);
        right.offer(left.poll());

        if (left.size() < right.size()) {
            left.offer(right.poll());
        }
    }

    public double findMedian() {
        if (left.size() == right.size()) {
            return (left.peek() + right.peek()) / 2.0;
        }
        return left.peek();
    }
}
```

### Step 5: Test (3 min)
- addNum(1), addNum(2), findMedian() → 1.5
- addNum(3), findMedian() → 2.0
- addNum all increasing: median climbs smoothly
- addNum all same: median is that value
- **Edge:** addNum(1), findMedian() → 1.0
- Walk through heap balancing at each step.

### Step 6: Follow-ups
- "What about finding the median of a sliding window?" — Use two multisets (TreeSet) or two heaps + lazy removal (LC 480).
- "What about percentiles instead of median?" — Use a different heap size ratio.
- "What about thread safety?" — Use ReentrantLock or synchronized.
- **What Meta looks for:** Balancing logic is the trick. Many candidates mis-handle the rebalancing after each addNum.

### Company Evaluation Criteria
- **Meta:** Balancing logic and correctness on both even/odd counts. They'll test edge cases heavily.
- **Google:** Would ask about the multi-set (TreeSet) version and removing arbitrary elements.
- **Amazon:** Would ask about distributed median computation (approximate algorithms like t-digest).

---

## Study Notes

### Key Patterns
- **Min-heap for Kth Largest:** Maintain k smallest elements (min-heap discards smallest when full)
- **Max-heap for Kth Smallest:** Maintain k largest elements
- **Two heaps for median:** Left is max-heap (smaller half), right is min-heap (larger half)
- **Heap for merging:** Push heads, extract min, push next
- **Top-K / Most frequent:** Heap of size k (often paired with frequency map)

### Common Mistakes
- Using max-heap when min-heap is needed (and vice versa)
- Forgetting to rebalance the two heaps after each insertion
- Integer division instead of double division for median
- Not handling null lists in merge k sorted
- Using `PriorityQueue` without a comparator (natural ordering = min-heap)

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| Min-heap kth largest | O(n log k) | O(k) |
| QuickSelect (avg) | O(n) | O(1) |
| Merge k lists (heap) | O(N log k) | O(k) |
| Two-heap median | O(log n) add | O(n) |
| Heapify | O(n) | O(n) |
| Heap sort | O(n log n) | O(1) |
