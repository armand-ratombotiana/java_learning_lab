# 02 Segment Tree

A segment tree is a binary tree for range query and range update operations on an array.

## Learning Objectives
- Build a segment tree from an array
- Perform range sum/min/max queries in O(log n)
- Implement lazy propagation for range updates
- Understand the 4n memory footprint

## Complexity Table

| Operation     | Time   | Space |
|---------------|--------|-------|
| Build         | O(n)   | O(n)  |
| Range Query   | O(log n)| O(1) |
| Point Update  | O(log n)| O(1) |
| Range Update  | O(log n)| O(1) |

n = array length