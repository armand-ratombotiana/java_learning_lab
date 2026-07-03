# Lab 07: Heaps

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Intermediate-yellow?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-3_4_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Priority-based tree structures — min-heaps, max-heaps, and heap sort**

</div>

---

## Learning Objectives

- Understand the heap property (min-heap, max-heap)
- Implement a binary heap using an array
- Master `siftUp` and `siftDown` operations
- Implement heap sort in-place
- Understand priority queue internals
- Use heaps for real-world problems (stream median, task scheduling)
- Analyze heap operations and heapify complexity

## Prerequisites

- Lab 01: Arrays (heap is array-backed)
- Lab 04: Trees (heap is a complete binary tree)

## Topics Covered

- Complete binary tree property
- Binary heap array representation: index math (2i+1, 2i+2, (i-1)/2)
- Insert: append + siftUp, O(log n)
- Extract min/max: swap with last + siftDown, O(log n)
- Build heap (heapify): bottom-up O(n)
- Min-heap vs max-heap
- Heap sort: build heap + extract all, O(n log n)
- Priority queue: `java.util.PriorityQueue` internals
- Big O: insert O(log n), extract O(log n), peek O(1), heapify O(n)
- Common pitfalls: off-by-one in index math, heap property violation

## Exercises

1. Implement a min-heap from scratch using an array
2. Implement heap sort
3. Find the k largest elements in an array
4. Find the median of a stream of integers (two heaps)
5. Merge k sorted lists using a heap

## Estimated Time: 3-4 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*
