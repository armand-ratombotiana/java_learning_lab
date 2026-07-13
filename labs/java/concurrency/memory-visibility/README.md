# Memory Visibility & The Volatile Keyword

Welcome to the atomic mastery lab for **Memory Visibility**. This lab is part of the Java Academy's Concurrency module.

## 🧠 What You Will Master
- The difference between Race Conditions and Visibility Problems.
- CPU Caches (L1, L2, L3) and cache coherence.
- The Java Memory Model (JMM) and the "Happens-Before" guarantee.
- The `volatile` keyword and memory barriers.
- Implementing a visibility failure and fixing it in Java.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - CPU caching and the visibility problem.
2. [INTERNALS.md](./INTERNALS.md) - The JMM, Happens-Before, and Memory Barriers.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Simulating an infinite loop caused by CPU caching.