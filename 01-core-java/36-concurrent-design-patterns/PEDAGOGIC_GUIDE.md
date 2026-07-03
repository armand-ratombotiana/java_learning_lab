# Pedagogic Guide: Concurrent Design Patterns

## 1. Module Overview
This module shifts the learner's focus from "how to make a variable thread-safe" to "how to build a thread-safe architecture." By teaching patterns, we provide learners with a vocabulary and a set of blueprints that solve 90% of the concurrency problems they will face in enterprise software.

## 2. Learning Paths

### Path A: The Backend Developer (Focus: Pipelines & Queues)
**Target Audience**: Developers building data pipelines, message queue consumers, or batch processors.
*   **Focus**: `DEEP_DIVE.md` (Producer-Consumer), `MINI_PROJECT.md`, and `EDGE_CASES.md` (Unbounded Buffers).
*   **Key Takeaway**: Understanding that the Producer-Consumer pattern with a *bounded* queue is the ultimate defense against system overload, and mastering the Poison Pill pattern for clean shutdowns.

### Path B: The Systems Architect (Focus: State Management)
**Target Audience**: Senior developers designing highly concurrent domain models or exploring reactive/actor frameworks.
*   **Focus**: `DEEP_DIVE.md` (Actor Pattern, Reader-Writer) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Understanding how the Actor model completely eliminates the need for explicit locks by isolating mutable state to a single thread, and understanding the trade-offs of Writer Starvation.

## 3. Teaching Strategies

### The "Assembly Line" Metaphor (Producer-Consumer)
Use a factory assembly line to explain the Producer-Consumer pattern.
*   **Producers**: Workers building car parts.
*   **Consumers**: Workers assembling the cars.
*   **Buffer**: The conveyor belt between them.
If the parts workers (Producers) are too fast, parts fall off the belt (OOM Error). Therefore, the belt must have a limit (Bounded Buffer). When the belt is full, the parts workers must stop and wait (Backpressure). This metaphor perfectly illustrates why queues must be bounded.

### The "Night Watchman" Metaphor (Balking)
To explain the Balking pattern, use a night watchman at a warehouse.
The watchman's job is to lock the front door at 10 PM. He walks to the door. If the door is *already* locked, he doesn't unlock it just to lock it again; he simply turns around and walks away (Balks). This saves time and effort. This is exactly how a background auto-save thread should work if a file hasn't been modified.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why not just use `Thread.interrupt()` to stop consumers?"
*   **Clarification**: This is a very common beginner mistake. Explain that `interrupt()` is like pulling the fire alarm. It causes chaos. If a consumer is in the middle of writing to a database, `interrupt()` might leave the database in an inconsistent state. The Poison Pill pattern is like a manager walking down the assembly line and handing the last worker a sign that says "Go home after this." It ensures all valid work is finished before shutdown.

### Block 2: "If Actors don't use locks, how are they thread-safe?"
*   **Clarification**: This requires shifting from a "shared memory" mindset to a "message passing" mindset. Draw a box representing the Actor. Inside the box is a variable `count`. Outside the box are 10 threads. Explain that the 10 threads *cannot touch* `count`. They can only drop notes in a mailbox outside the box. Inside the box, ONE single thread reads the notes one by one and updates `count`. Because only one thread ever touches `count`, race conditions are impossible.

### Block 3: "Why does Reader-Writer lock cause starvation?"
*   **Clarification**: Use a library metaphor. If 100 people are reading a book (Read Lock), the librarian (Writer) has to wait until everyone puts the book down to update a page. If new readers keep picking up the book before the old ones finish, the librarian waits forever. Explain that "Fairness" forces new readers to wait in line behind the librarian.

## 5. Assessment Strategy
*   **Formative**: Provide a snippet of a Producer-Consumer pipeline using an `ArrayList`. Ask the learner to identify the two catastrophic bugs in the code (not thread-safe, and unbounded memory growth).
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a Log Processing Pipeline. They must successfully combine a bounded `BlockingQueue`, a Poison Pill for shutdown, and a Balking save mechanism. This proves they can orchestrate multiple concurrent patterns into a single, cohesive application.