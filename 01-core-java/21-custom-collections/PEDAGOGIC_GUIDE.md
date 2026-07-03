# Pedagogic Guide: Custom Collections

## 1. Module Overview
This module bridges the gap between *using* data structures and *engineering* them. It forces learners to confront low-level details like array resizing, memory management (lingering references), and thread-safety mechanics (fail-fast iterators) that the JDK usually hides from them.

## 2. Learning Paths

### Path A: The Systems Engineer (Focus: Mechanics & Memory)
**Target Audience**: Developers working on high-performance applications, game engines, or low-latency systems.
*   **Focus**: `EDGE_CASES.md` (Memory Leaks, Generic Arrays) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Understanding how to build primitive-backed collections to avoid autoboxing overhead, and how to prevent memory leaks when manipulating internal arrays.

### Path B: The API Designer (Focus: Contracts & Abstractions)
**Target Audience**: Senior developers building libraries or complex domain models.
*   **Focus**: `DEEP_DIVE.md` (Abstract Classes) and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Mastering the `AbstractCollection` hierarchy to minimize boilerplate code, and strictly adhering to the `equals()`, `hashCode()`, and `Iterator` contracts.

## 3. Teaching Strategies

### The "Under the Hood" Code Review
Don't just teach the theory; open the JDK source code. Have the learner look at the source code for `java.util.ArrayList`.
1.  Show them the `Object[] elementData` array.
2.  Show them the `remove(int index)` method and specifically point out the line `elementData[--size] = null; // clear to let GC do its work`.
3.  Show them the `modCount++` lines and how the `Itr` inner class uses `expectedModCount`.
Seeing this in professional, production code makes the concepts concrete.

### The "Broken Iterator" Exercise
Before teaching `modCount`, give the learner a simple custom list and ask them to iterate over it while simultaneously adding elements to it. Let them experience the `ArrayIndexOutOfBoundsException` or silent data corruption. Then, introduce `modCount` as the solution to this exact problem.

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why can't I just `new E[10]`?"
*   **Clarification**: This is a classic generics stumbling block. Remind them of Type Erasure. At runtime, `E` doesn't exist. The JVM needs to know exactly how much memory to allocate and what type of array to create. Since it only sees `Object`, it can't create an `E[]`. The workaround is creating an `Object[]` and casting elements upon retrieval.

### Block 2: "Why extend `AbstractList` instead of implementing `List` directly?"
*   **Clarification**: Show them the `List` interface. It has over 25 methods. Ask them if they want to write 25 methods for a simple custom list. Then show them that `AbstractList` implements almost all of them (like `iterator()`, `indexOf()`, `contains()`) using just the `get(int)` and `size()` methods that the developer provides.

### Block 3: "Isn't `modCount` for thread safety?"
*   **Clarification**: No! This is a very common misconception. `modCount` does *not* make a collection thread-safe. It is a "best-effort" mechanism to detect concurrent modifications and fail quickly (Fail-Fast) rather than corrupting data silently. True thread safety requires synchronization or concurrent collections (like `ConcurrentHashMap`).

## 5. Assessment Strategy
*   **Formative**: Ask the learner to explain *why* `ArrayList` sets removed elements to `null`.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a Ring Buffer and implement a custom iterator with `modCount` checks. This proves they understand both the algorithmic logic of the collection and the strict contracts of the Java Iterator API.