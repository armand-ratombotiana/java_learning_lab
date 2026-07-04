# Interview Questions: Advanced Trees

## Easy

1. **Validate BST** — Given a binary tree, check if it is a valid BST (range check, not just left/right comparison).

2. **Kth smallest in BST** — Inorder traversal, stop at k.

3. **Inorder successor in BST** — Find next node in inorder traversal.

4. **BST search, insert, delete** — Implement all three.

## Medium

5. **Serialize BST** — Encode/decode a BST (can use preorder + null markers).

6. **Balanced BST from sorted array** — Build a height-balanced BST (recursive, pick middle).

7. **Convert BST to doubly linked list** — Flatten BST to sorted linked list (in-place).

8. **Two sum in BST** — Find if two nodes sum to target (HashSet or two-pointer).

9. **Ceiling in BST** — Find smallest key ≥ target.

10. **Range sum BST** — Sum of all keys between low and high.

## Hard

11. **AVL tree implementation** — Implement insert with rotations and balance checks.

12. **B-tree implementation** — Implement insert with node splitting.

13. **Segment tree with lazy propagation** — Implement range update and range query.

14. **Count of smaller numbers after self** — Fenwick tree or segment tree with coordinate compression.

15. **Design a leaderboard** — Add score, get top K, get rank (TreeMap + HashMap).

## Key Patterns

- **BST property traversal**: validate, find, insert, delete using the left < root < right property
- **Inorder traversal**: sorted order, kth smallest, successor
- **Divide and conquer**: build balanced BST from sorted array
- **Rotation patterns**: LL, RR, LR, RL for balancing
- **Fenwick/Segment**: prefix sums, range queries, range updates

## Java-Specific Topics

- `TreeMap` and `TreeSet` methods (NavigableMap/SortedMap)
- `Comparator` vs `Comparable` for TreeMap ordering
- `TreeMap` time complexity guarantees (O(log n))
- SubMap views (backed by original map)
- ConcurrentSkipListMap as thread-safe alternative
