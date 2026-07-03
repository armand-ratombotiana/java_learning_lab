# Module 23: Data Structures & Algorithms - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-22  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Introduction to Time & Space Complexity (Big O)](#bigo)
2. [Linear Data Structures](#linear)
3. [Hierarchical Data Structures (Trees)](#trees)
4. [Graphs & Hash Tables](#graphs-hash)
5. [Basic Sorting & Searching Algorithms](#sorting-searching)

---

## 1. Introduction to Time & Space Complexity (Big O) <a name="bigo"></a>
Big O notation describes the performance or complexity of an algorithm relative to the input size $n$.
- **O(1)**: Constant time (e.g., accessing an array element).
- **O(log n)**: Logarithmic time (e.g., binary search).
- **O(n)**: Linear time (e.g., iterating through a list).
- **O(n log n)**: Linearithmic time (e.g., merge sort, quicksort).
- **O(n^2)**: Quadratic time (e.g., bubble sort).

---

## 2. Linear Data Structures <a name="linear"></a>
- **Arrays**: Fixed-size contiguous memory allocation. O(1) access, O(n) insertion/deletion.
- **Linked Lists**: Elements (nodes) point to the next (and optionally previous). O(n) access, O(1) insertion/deletion (if pointer is known).
- **Stacks**: LIFO (Last-In-First-Out) principle. O(1) push/pop operations.
- **Queues**: FIFO (First-In-First-Out) principle. O(1) enqueue/dequeue operations.

---

## 3. Hierarchical Data Structures (Trees) <a name="trees"></a>
- **Binary Tree**: Each node has at most two children.
- **Binary Search Tree (BST)**: Left child is smaller, right child is larger. Average O(log n) for search/insert/delete, worst case O(n) if unbalanced.
- **Balanced Trees (AVL, Red-Black)**: Automatically balance themselves to guarantee O(log n) operations.

---

## 4. Graphs & Hash Tables <a name="graphs-hash"></a>
- **Graphs**: Consist of vertices (nodes) and edges (connections). Can be directed or undirected, weighted or unweighted. Common representations: Adjacency Matrix, Adjacency List.
- **Hash Tables**: Map keys to values using a hash function. Average O(1) for search/insert/delete. Collisions are handled via chaining (LinkedLists) or open addressing.

---

## 5. Basic Sorting & Searching Algorithms <a name="sorting-searching"></a>
- **Linear Search**: Checks each element. O(n) time.
- **Binary Search**: Fast search on sorted arrays. O(log n) time.
- **Bubble Sort**: Swaps adjacent elements if out of order. O(n^2) time.
- **Merge Sort**: Divide and conquer. O(n log n) time, O(n) space.
- **Quick Sort**: Partitioning around a pivot. Average O(n log n) time, worst case O(n^2) time, O(log n) space.