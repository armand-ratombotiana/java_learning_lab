# ConcurrentHashMap Deep Dive

Welcome to the atomic mastery lab for `ConcurrentHashMap`. This lab builds upon the `HashMap` lab and introduces the complexities of highly concurrent data structures in Java.

## 🧠 What You Will Master
- Why `HashMap` and `Hashtable` fail in concurrent environments.
- Lock Striping (Java 7) vs. CAS Operations (Java 8+).
- The `volatile` keyword and Memory Visibility in concurrent maps.
- The `ForwardingNode` and concurrent resizing.
- Thread-safe compound operations (`computeIfAbsent`, `putIfAbsent`).

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - The concurrency problem and the evolution of solutions.
2. [INTERNALS.md](./INTERNALS.md) - Deep dive into CAS and memory visibility.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Practical usage and avoiding common concurrency traps.