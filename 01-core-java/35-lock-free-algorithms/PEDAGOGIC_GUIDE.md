# Pedagogic Guide: Lock-Free Algorithms

## 1. Module Overview
This module is the capstone of the concurrency track. It requires learners to combine their knowledge of hardware instructions (CAS), memory models, and data structure internals. It is intellectually demanding and primarily targeted at advanced developers preparing for senior/staff level roles or those building core infrastructure.

## 2. Learning Paths

### Path A: The Application Developer (Focus: Usage & Limitations)
**Target Audience**: Developers who use `ConcurrentLinkedQueue` or `ConcurrentHashMap` but don't need to write their own lock-free structures.
*   **Focus**: `EDGE_CASES.md` (Lost Updates, Livelock) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Understanding *why* `ConcurrentLinkedQueue.size()` is $O(N)$ and recognizing that "lock-free" does not automatically mean "faster," especially under high contention.

### Path B: The Systems Programmer (Focus: Mechanics & Algorithms)
**Target Audience**: Senior engineers, performance tuners, and those preparing for elite technical interviews.
*   **Focus**: `DEEP_DIVE.md` (Treiber Stack, Michael-Scott Queue) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Mastering the CAS spin-loop pattern and understanding the profound difficulty of maintaining multiple atomic pointers (like `head` and `tail` in a queue) simultaneously.

## 3. Teaching Strategies

### The "Sleight of Hand" Metaphor (CAS Loop)
To explain the CAS spin-loop, use a magic trick metaphor.
You have a card on the table (the current state). You look at it, turn around, do some complex math to figure out the next card, and then turn back.
Before you put the new card down, you must check: *Did someone swap the card on the table while I was turned around?*
If yes (CAS fails), you throw away your math, look at the new card, and start over.
If no (CAS succeeds), you quickly swap the cards. This emphasizes the optimistic, retry-based nature of the algorithm.

### The "ABA Problem" Visual
Draw a stack with nodes `[A] -> [B] -> [C]`.
Walk through the exact sequence of events in the `EDGE_CASES.md` document step-by-step on a whiteboard. Show Thread 1 holding a pointer to `A` and `B`. Show Thread 2 popping `A` and `B`, and then pushing `A` back.
Visually demonstrate that when Thread 1 wakes up, `A` is at the top, so it thinks nothing changed. But when it sets the head to `B`, it sets the head to a node that is floating in the void, completely disconnected from `[C]`. This visual is usually the "aha!" moment for understanding ABA.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why is a Queue harder than a Stack?"
*   **Clarification**: A stack only has one entry/exit point (`head`). A single CAS operation can update the `head`. A queue has two points (`head` and `tail`). When a queue is empty, or has one item, adding an item requires updating *both* the `head` and the `tail`. Because you can only do one CAS at a time, there is a microsecond where the queue is in an inconsistent state. The Michael-Scott algorithm solves this by having threads "help" each other finish the operation if they detect an inconsistent state.

### Block 2: "If lock-free is so great, why doesn't everyone use it for everything?"
*   **Clarification**: Reiterate the concept of Livelock and Contention. If 100 people try to walk through a door at once (Lock), 99 wait in line and 1 goes through. It's orderly. If 100 people try to run through a door at once using CAS, they all bump into each other, fall down, get back up, and try again. They burn massive amounts of energy (CPU) and nobody gets through quickly. Locks are better for high contention.

### Block 3: "Why doesn't Java have the ABA problem as much as C++?"
*   **Clarification**: In C++, you manually allocate memory. If you delete Node A, the OS might reuse that exact memory address for a brand new node a millisecond later. Thread 1's pointer now points to a completely different object that happens to have the same memory address (ABA). In Java, the GC doesn't reuse memory addresses like that while a thread holds a reference, significantly reducing (though not eliminating) the risk of ABA in node-based structures.

## 5. Assessment Strategy
*   **Formative**: Ask the learner to explain why `ConcurrentLinkedQueue` does not have a `size` variable that is updated via `AtomicInteger.incrementAndGet()` during the `offer()` method.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to implement a Treiber Stack from scratch and write a multi-threaded contention test to prove it works. This is the ultimate test of their understanding of the CAS spin-loop.