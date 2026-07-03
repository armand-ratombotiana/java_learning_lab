# Advanced Sorting — Theoretical Foundation

## Merge Sort
Divide and conquer: split array in half, sort each half recursively, merge.
- All Cases: O(n log n)
- Space: O(n) auxiliary array

## Quick Sort
Pick pivot, partition around pivot, recurse on partitions.
- Best/Average: O(n log n)
- Worst: O(n²) with bad pivot choice
- Space: O(log n) recursion stack

## Heap Sort
Build max-heap, repeatedly extract maximum.
- All Cases: O(n log n)
- Space: O(1) in-place
