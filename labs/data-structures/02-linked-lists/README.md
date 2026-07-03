# Lab 02: Linked Lists

<div align="center">

![Difficulty](https://img.shields.io/badge/Difficulty-Beginner-brightgreen?style=for-the-badge)
![Time](https://img.shields.io/badge/Time-3_4_hours-blue?style=for-the-badge)
![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)

**Dynamic node-based structures — singly, doubly, and circular linked lists**

</div>

---

## Learning Objectives

- Understand node-based memory allocation (non-contiguous)
- Implement singly linked, doubly linked, and circular linked lists
- Master insertion, deletion, reversal, and traversal operations
- Compare linked lists vs arrays for different use cases
- Implement sentinel/dummy nodes to simplify edge cases
- Understand iterator pattern for linked structures

## Prerequisites

- Basic Java classes and references
- Understanding of pointers/references in Java
- Lab 01: Arrays (for comparison)

## Topics Covered

- Singly linked list: `Node<T>` with next pointer
- Doubly linked list: `Node<T>` with prev and next
- Circular linked list: tail-to-head connection
- Sentinels (dummy head/tail) edge-case simplification
- Big O: access O(n), search O(n), insertion O(1), deletion O(1) at known position
- Comparison: ArrayList vs LinkedList use cases
- Memory overhead: per-node object + reference vs array
- `java.util.LinkedList` internals
- Common pitfalls: null pointer on tail operations, circular traversal termination

## Exercises

1. Implement a generic singly linked list with `addFirst`, `addLast`, `remove`, `get`
2. Reverse a linked list in-place (iterative and recursive)
3. Detect a cycle in a linked list (Floyd's tortoise and hare)
4. Find the middle node in one pass
5. Merge two sorted linked lists

## Estimated Time: 3-4 hours

--- 

*Start with* `THEORY.md` *for conceptual understanding, then* `CODE_DEEP_DIVE.md` *for implementations.*
