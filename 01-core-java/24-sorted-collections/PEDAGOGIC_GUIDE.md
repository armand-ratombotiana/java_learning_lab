# Pedagogic Guide: Sorted Collections

## 1. Module Overview
This module transitions learners from hash-based, unordered data structures to tree-based, ordered data structures. It introduces algorithmic complexity (Big O) in a practical context and highlights the power of the `Navigable` interfaces for solving complex business problems (like scheduling and range queries).

## 2. Learning Paths

### Path A: The Application Developer (Focus: Usage & APIs)
**Target Audience**: Developers who need to build features like leaderboards, scheduling, or time-series data processing.
*   **Focus**: `DEEP_DIVE.md` (NavigableMap, SubMaps) and `MINI_PROJECT.md`.
*   **Key Takeaway**: Mastering the `NavigableMap` API (`ceilingKey`, `lowerKey`, `subMap`) to solve complex range query problems without writing manual loops.

### Path B: The Computer Scientist (Focus: Algorithms & Internals)
**Target Audience**: Senior developers preparing for algorithmic interviews or those writing high-performance data processing pipelines.
*   **Focus**: `DEEP_DIVE.md` (Red-Black Trees), `EDGE_CASES.md` (compareTo consistency), and `INTERVIEW_PREP.md`.
*   **Key Takeaway**: Understanding *why* a Red-Black tree is necessary (self-balancing) and understanding the disastrous consequences of mutating keys after insertion.

## 3. Teaching Strategies

### The "Unbalanced Tree" Demonstration
To teach why Red-Black trees exist, draw a simple Binary Search Tree on a whiteboard.
Ask the learner to insert the numbers 5, 3, and 7. The tree looks balanced.
Then, ask them to insert the numbers 1, 2, 3, 4, 5 in order. Draw the resulting tree. It will look exactly like a linked list leaning to the right. 
Explain that searching this "tree" now takes $O(N)$ time. This visual perfectly justifies the complex rotation and coloring rules of the Red-Black tree to maintain $O(\log N)$ performance.

### The "SubMap View" Metaphor
When explaining `subMap`, use the metaphor of a window frame looking at a landscape. The `subMap` isn't a new landscape (a copy of the data); it's just a window frame restricting what you can see. If someone paints a tree in the landscape (adds an element to the original map), you will see it through the window (the sub-map updates). If you throw a rock through the window (add an element to the sub-map), it lands in the real landscape (updates the original map).

## 4. Common Mental Blocks & Clarifications

### Block 1: "Why did my object disappear from the TreeSet?"
*   **Clarification**: This is almost always the `compareTo` consistency bug. Explain that `HashSet` uses `hashCode()` and `equals()`, but `TreeSet` is completely blind to those methods. It *only* uses `compareTo()`. If `compareTo()` returns `0`, the `TreeSet` assumes it's a duplicate, even if `equals()` would return `false`.

### Block 2: "Why can't I add null to a TreeSet?"
*   **Clarification**: A `TreeSet` must sort elements by comparing them to each other. How do you compare `null` to a `String`? You can't call `null.compareTo("A")`, and `"A".compareTo(null)` throws a `NullPointerException`. Therefore, `TreeSet` strictly rejects `null` values.

### Block 3: "Is a TreeMap faster than a HashMap?"
*   **Clarification**: Emphasize that for pure lookups, `HashMap` ($O(1)$) is significantly faster than `TreeMap` ($O(\log N)$). You only pay the performance cost of a `TreeMap` when you specifically need the data to be sorted or when you need to perform range queries.

## 5. Assessment Strategy
*   **Formative**: Provide a `compareTo` method that only compares `lastName` on a `User` object. Ask the learner what happens when `User("John", "Doe")` and `User("Jane", "Doe")` are added to the same `TreeSet`.
*   **Summative**: The `MINI_PROJECT.md` requires the learner to build a Scheduler. They must use `ceilingEntry` to find the next event and `subMap` to get a daily itinerary, proving they can apply the `NavigableMap` API to solve real-world problems.