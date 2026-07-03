# Lab 09: Advanced Trees

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Advanced-orange?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-5_6_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Self-balancing search trees — BST, AVL, Red-Black, and B-trees**

</div>

---

## Learning Objectives

- Understand binary search trees and their properties
- Implement BST with insert, search, delete, and traversals
- Understand balancing constraints and rotation operations
- Implement AVL trees with balance factor tracking
- Understand Red-Black tree coloring rules and invariants
- Understand B-trees and their role in databases
- Compare trade-offs between tree variants

## Prerequisites

- Lab 04: Trees (binary trees, traversals)
- Recursion proficiency
- Understanding of algorithm complexity analysis

## Topics Covered

### Binary Search Tree (BST)
- BST property: left < root < right
- Operations: insert, search, delete (with successor/predecessor)
- Degenerate BST: worst case O(n) when sorted input
- Big O: average O(log n), worst O(n)

### AVL Tree
- Balance factor: height(left) - height(right) ∈ {-1, 0, 1}
- Rotations: left, right, left-right, right-left
- Rebalancing after insert and delete
- Strict balancing: height ≤ 1.44 × log₂(n)

### Red-Black Tree
- 5 invariants: root black, no consecutive reds, equal black height
- Recoloring and rotation strategies
- Loose balancing: height ≤ 2 × log₂(n+1)
- Java `TreeMap` and `TreeSet` use Red-Black trees

### B-Tree
- Multi-way branching, each node has k keys and k+1 children
- Used in databases and file systems (SQLite, PostgreSQL, ext4)
- Order m: max m children, min ⌈m/2⌉ children
- Split and merge operations

## Exercises

1. Implement a BST with insert, search, and delete
2. Implement an AVL tree with all four rotations
3. Verify if a binary tree is a valid BST
4. Find the kth smallest element in a BST
5. Implement an in-order iterator for a BST (iterative)

## Estimated Time: 5-6 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*
