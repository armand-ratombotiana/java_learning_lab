# Code Deep Dive: Heap Algorithms

## Find K Largest Elements

```java
public int[] findKLargest(int[] nums, int k) {
    // Min-heap of size k
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();  // remove smallest
        }
    }
    return minHeap.stream().mapToInt(i -> i).toArray();
}
```

Time: O(n log k), Space: O(k)

## Median of Stream

```java
class MedianFinder {
    private PriorityQueue<Integer> maxHeap;  // smaller half
    private PriorityQueue<Integer> minHeap;  // larger half

    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        minHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        if (maxHeap.isEmpty() || num <= maxHeap.peek()) {
            maxHeap.offer(num);
        } else {
            minHeap.offer(num);
        }
        // Balance: maxHeap.size() ≥ minHeap.size() and differ by ≤ 1
        if (maxHeap.size() > minHeap.size() + 1) {
            minHeap.offer(maxHeap.poll());
        } else if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }

    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}
```

## Merge K Sorted Lists

```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> pq = new PriorityQueue<>(
        (a, b) -> a.val - b.val
    );
    for (ListNode head : lists) {
        if (head != null) pq.offer(head);
    }
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;
    while (!pq.isEmpty()) {
        ListNode node = pq.poll();
        tail.next = node;
        tail = tail.next;
        if (node.next != null) pq.offer(node.next);
    }
    return dummy.next;
}
```

Time: O(n log k) where n = total nodes, k = number of lists.

## Heapify Analysis Proof

```java
// Building heap bottom-up:
for (int i = (size / 2) - 1; i >= 0; i--) {
    siftDown(i);
}
// Each siftDown at height h takes O(h)
// Sum over all nodes: Σ_{h=0}^{log n} n/2^{h+1} × O(h)
// = O(n × Σ h/2^h) = O(n)
```

The number of nodes at height h is at most n/2^{h+1}. The sum Σ h/2^h converges to 2.
