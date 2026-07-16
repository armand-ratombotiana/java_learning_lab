# Lock-Free Queues

Welcome to the atomic mastery lab for **Lock-Free Queues**. This lab is part of the Java Academy's Concurrency module.

## 🧠 What You Will Master
- The cost of lock contention in high-throughput systems.
- Compare-And-Swap (CAS) as a synchronization primitive.
- The Michael-Scott Lock-Free Queue algorithm.
- ABA Problem and how to mitigate it.
- Implementing a basic Lock-Free Queue using `AtomicReference`.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - Why locks don't scale.
2. [INTERNALS.md](./INTERNALS.md) - The Michael-Scott algorithm and CAS loops.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Pure Java implementation of a lock-free linked queue.