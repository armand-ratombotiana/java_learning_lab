# Math Foundation: Heaps

## Heap Index Arithmetic

For a binary heap stored in a 0-indexed array:
- Root: index 0
- Left child of i: 2i + 1
- Right child of i: 2i + 2
- Parent of i: (i - 1) / 2
- Last non-leaf node: n/2 - 1

## Heapify Complexity

Building a heap from n elements (bottom-up siftDown):

```
Number of nodes at height h: ≤ n / 2^{h+1}
Time per siftDown at height h: O(h)
Total time: Σ_{h=0}^{⌊log₂n⌋} (n / 2^{h+1}) × O(h)
           = O(n × Σ_{h=0}^{∞} h / 2^{h+1})
           = O(n × 1) = O(n)
```

The sum converges: Σ h/2^{h} = 2.

## Insert Complexity

Each insert appends to the end (O(1)) and sifts up.
- Max siftUp distance: ⌊log₂n⌋
- Worst-case: O(log n)
- n inserts: O(n log n)

## Heap Sort Complexity

```
1. Build max-heap: O(n)
2. For i = n-1 down to 1:
     swap root with heap[i]
     siftDown on reduced heap: O(log n)
3. Total: O(n log n)
```

Space: O(1) extra (in-place).

## Complete Binary Tree Properties

For n elements:
- Depth d = ⌊log₂n⌋
- Powers of 2: 2^d ≤ n < 2^{d+1}
- Leaves: n - 2^d + 1 leaves at depth d (approximately n/2)

## Median Maintenance with Two Heaps

With a max-heap for lower half and min-heap for upper half:
- Max-heap contains ⌈n/2⌉ elements
- Min-heap contains ⌊n/2⌋ elements
- Each insert: O(log n) — one heap insert + possible rebalance
- Median: O(1) — peek at max-heap root (or average of both roots)

## Kth Largest with Heap

Using a min-heap of size k to track k largest elements:
- For each of n elements: O(log k) if element > heap root
- Total: O(n log k)
- Space: O(k)
