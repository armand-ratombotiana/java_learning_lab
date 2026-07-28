# 03 Fenwick Tree (Binary Indexed Tree)

A Fenwick tree (BIT) supports prefix sum queries and point updates in O(log n), with minimal memory overhead.

## Learning Objectives
- Understand the i & -i indexing trick
- Implement prefix sum and point update
- Adapt for range updates (difference BIT)
- Compare with segment trees

## Complexity Table

| Operation    | Time   | Space |
|--------------|--------|-------|
| Build        | O(n)   | O(n)  |
| Prefix Sum   | O(log n)| O(1) |
| Point Update | O(log n)| O(1) |
| Range Sum    | O(log n)| O(1) |

n = array size