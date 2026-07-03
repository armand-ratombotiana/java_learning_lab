# Interview Preparation: Functional Data Structures

This document covers advanced questions related to persistent data structures, structural sharing, and the limitations of functional programming in Java.

## Q1: Explain the concept of "Structural Sharing" in persistent data structures.
**Answer:**
In functional programming, collections are immutable. When you "modify" a collection (e.g., adding an element), you must return a *new* collection.
If you copied the entire collection every time, it would be extremely slow and consume too much memory ($O(N)$). Structural sharing solves this. The new collection shares as much of the underlying memory structure (nodes/pointers) with the old collection as possible.
For example, in a Singly Linked List, if you prepend an element, you simply create a new head node that points to the existing list. Both the old list and the new list exist simultaneously, sharing the tail nodes, achieving an $O(1)$ update time.

## Q2: Why is appending (adding to the end) to a functional Singly Linked List an $O(N)$ operation?
**Answer:**
Because the list is immutable, you cannot change the `next` pointer of the last node to point to a new element. To change the last node, you must create a copy of it. But to point to that new copy, you must create a copy of its parent, and so on, all the way back to the root. Therefore, appending to the tail requires copying every single node in the list, making it an $O(N)$ operation.

## Q3: What is a Hash Array Mapped Trie (HAMT), and why is it used for functional Maps/Vectors?
**Answer:**
A HAMT is a wide, shallow tree structure (often with a branching factor of 32).
Because functional linked lists are slow for random access ($O(N)$), functional languages use HAMTs to implement Maps and Vectors.
When you update an element in a HAMT, you use "Path Copying." You only copy the nodes from the root down to the specific leaf node being updated. Because the tree is very shallow (a depth of 6 can hold over 1 billion elements), the path copying takes a near-constant amount of time (effectively $O(1)$), providing the performance of an array with the immutability of a functional structure.

## Q4: What is Tail Call Optimization (TCO), and how does its absence affect Java?
**Answer:**
Functional data structures rely heavily on recursion to process data (e.g., using `fold` or `map`).
Normally, every recursive call adds a new frame to the call stack. If the recursion is deep (e.g., processing a list of 10,000 items), it will throw a `StackOverflowError`.
TCO is a compiler feature that recognizes when the recursive call is the very last operation in a function (a tail call). The compiler optimizes it by reusing the current stack frame, effectively turning the recursion into a `while` loop under the hood, preventing stack overflows.
**Java does not support TCO.** Therefore, writing pure recursive functional code in Java is dangerous for large datasets. Developers must manually convert recursive functions into iterative loops to ensure stability.

## Q5: How does "Lazy Evaluation" benefit functional data processing?
**Answer:**
Lazy evaluation means a computation is deferred until its result is actually needed.
In the context of streams or lazy lists, it allows for:
1.  **Short-Circuiting**: If you map 1,000,000 items but only need to `findFirst()` that matches a condition, lazy evaluation processes items one by one and stops immediately when the condition is met, saving massive CPU cycles.
2.  **Infinite Data Structures**: You can define a sequence that generates infinite numbers (e.g., the Fibonacci sequence). Because it's lazy, it only generates the numbers you explicitly ask for, preventing infinite loops and memory exhaustion.