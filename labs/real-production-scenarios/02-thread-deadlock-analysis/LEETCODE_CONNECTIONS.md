# Lab 02 — Thread Deadlock Analysis: LeetCode Connections

## Core Algorithmic Concepts in Deadlock Debugging

| Algorithmic Concept | Deadlock Application |
|--------------------|---------------------|
| Cycle detection (graph) | Detecting circular wait in lock graph |
| Topological sorting | Ensuring consistent lock ordering |
| Graph coloring | Lock level assignment for hierarchical ordering |
| Dijkstra's algorithm | Resource allocation prevention |
| Banker's algorithm | Safe state detection for resource allocation |
| Wait-for graph | Modeling which thread waits for which lock |
| Dependency resolution | Maven/Gradle dependency cycles = lock cycles |

## LeetCode Problems Related to Deadlocks

### Easy

**Q1: Linked List Cycle (LeetCode 141)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Detect if a linked list has a cycle |
| **Deadlock Connection** | A deadlock cycle is identical to a cycle in a linked list. Thread A points to Lock A, Lock A is held by Thread B, Thread B points to Lock B, Lock B held by Thread A — cycle! The Floyd's cycle detection (tortoise and hare) is conceptually how ThreadMXBean detects deadlock cycles. |
| **Algorithmic Lesson** | Cycle detection is fundamental to deadlock detection. Both use the same principle: if you follow the references long enough, you return to where you started. |

**Q2: Valid Parentheses (LeetCode 20)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Check if brackets are balanced using a stack |
| **Deadlock Connection** | The LIFO nature of stack is exactly what consistent lock ordering requires. If you always push (acquire) in a fixed order and pop (release) in reverse order, you can't deadlock. The balanced parentheses problem is a simple version of lock ordering — each open bracket must be closed before the next outer one, just like nested locks. |
| **Algorithmic Lesson** | Consistent LIFO ordering prevents deadlocks. The same principle applies to lock acquisition and release — acquire in order A, B, C; release in order C, B, A. |

**Q3: Find the Difference (LeetCode 389)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find the extra character in one string vs another |
| **Deadlock Connection** | Finding the "extra" thing (out-of-order lock acquisition) is like finding which code path acquires locks in the wrong order. You have two code paths — one acquires A→B, the other B→A. The second path is the "difference" that causes the deadlock. |
| **Algorithmic Lesson** | Small differences in ordering between code paths can cause catastrophic failures. Always check that all code paths acquire locks in the same order. |

### Medium

**Q4: Course Schedule II (LeetCode 210) — Topological Sort**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find a valid order to take courses given prerequisites |
| **Deadlock Connection** | This is exactly consistent lock ordering! If each lock is a "course" and "lock A must be acquired before lock B" is a prerequisite, a topological sort of the lock dependency graph gives you the order in which locks must be acquired. If the graph has a cycle (no topological order possible), you have a potential deadlock. |
| **Algorithmic Lesson** | Topological sorting on lock dependencies produces a safe lock acquisition order. If the lock graph has no cycles, consistent ordering is always possible. |

**Q5: Graph Valid Tree (LeetCode 261)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Determine if an undirected graph is a tree (no cycles) |
| **Deadlock Connection** | A valid lock graph is a tree — no cycles. A cycle in the lock graph means a potential deadlock. The union-find algorithm used in this problem is applicable to detecting whether adding a new lock edge would create a cycle. |
| **Algorithmic Lesson** | Lock hierarchies should be trees, not graphs with cycles. Use union-find to validate that new lock acquisition patterns don't introduce cycles. |

**Q6: Minimum Height Trees (LeetCode 310)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find roots that minimize tree height |
| **Deadlock Connection** | In a lock hierarchy, the "root" lock is the one acquired first. The height of the tree corresponds to the depth of nested lock acquisition. Deeply nested locks increase deadlock risk. Minimizing nesting depth (tree height) reduces deadlock probability. |
| **Algorithmic Lesson** | Shorter lock chains reduce deadlock risk. Design for shallow lock hierarchies. |

**Q7: Redundant Connection (LeetCode 684)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find the edge that creates a cycle in a graph |
| **Deadlock Connection** | This is exactly the problem of finding which code path introduces the inconsistent lock ordering that creates a deadlock cycle. The redundant connection is the "out-of-order" lock acquisition that creates the first cycle in the lock graph. |
| **Algorithmic Lesson** | When adding a new lock acquisition pattern, check if it creates a cycle with existing patterns. Union-find can detect this before it reaches production. |

### Hard

**Q8: Alien Dictionary (LeetCode 269)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Given sorted words, determine the character order (topological sort) |
| **Deadlock Connection** | Deriving a consistent ordering from observed patterns is exactly what you do when establishing lock ordering. You have multiple code paths that acquire locks in different orders. By analyzing all paths, you derive the global lock order that satisfies all constraints. |
| **Algorithmic Lesson** | Given all code paths, derive a global lock order through topological sort. If the graph has a cycle (contradictory orders from different paths), you must redesign one of the paths. |

**Q9: Word Ladder II (LeetCode 126)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find shortest transformation path (BFS) |
| **Deadlock Connection** | The BFS explores paths just like the lock graph exploration. The "shortest path" is the smallest number of threads/locks in a deadlock cycle. The deadlock detection algorithm is essentially BFS on the wait-for graph looking for cycles. |
| **Algorithmic Lesson** | BFS on wait-for graph = deadlock detection. Understanding BFS helps you understand how quickly deadlocks can form and how many threads are typically involved. |

**Q10: Count of Smaller Numbers After Self (LeetCode 315)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Count elements smaller than current to the right |
| **Deadlock Connection** | This problem requires a data structure (Fenwick tree / BIT) for ordered statistics. In concurrent programming, you need similar ordered data structures for lock hierarchies. A properly ordered lock tree allows you to efficiently determine if a new lock acquisition would violate global ordering. |
| **Algorithmic Lesson** | Data structures for ordered statistics (BST, BIT) are useful for managing lock hierarchies — they can efficiently answer "what locks should I acquire before this one?" |

## How Algorithmic Thinking Helps

### Cycle Detection = Deadlock Detection

The core algorithmic skill for deadlock debugging is cycle detection. Whether you use Floyd's algorithm, DFS with coloring, or union-find, detecting cycles in a directed graph is essential.

```
Wait-for graph construction:
- Node = Thread or Lock
- Edge Thread → Lock: Thread is waiting for lock
- Edge Lock → Thread: Lock is held by thread
- Cycle = Deadlock
```

### Topological Sort = Lock Ordering

Topological sort of the lock dependency graph produces a global lock acquisition order. If a topological sort is not possible (graph has a cycle), deadlock is possible under the right timing conditions.

### Union-Find = Safe Lock Addition

When adding a new code path with lock acquisitions, use union-find to check if the new edges would create a cycle in the lock graph. This can be automated in code review.

## Practice Problems for Interview Preparation

1. **Implement a deadlock detector** (medium): Use ThreadMXBean to find deadlocked threads, then extract the lock graph and print it as a cycle.

2. **Design a lock ordering validator** (medium): Create an annotation processor that checks at compile time that locks are always acquired in a consistent order.

3. **Simulate the Banker's Algorithm** (hard): Implement the Banker's Algorithm for resource allocation that avoids unsafe states.

4. **Lock hierarchy visualizer** (medium): Given a list of lock acquisitions from thread dumps, reconstruct the lock graph and detect cycles.

5. **Wait-for graph cycle detection** (easy): Given a wait-for graph as an adjacency list, implement DFS-based cycle detection.
