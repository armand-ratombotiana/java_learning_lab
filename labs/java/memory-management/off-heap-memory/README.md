# Off-Heap Memory

Welcome to the atomic mastery lab for **Off-Heap Memory**. This lab is part of the Java Academy's Memory Management module.

## 🧠 What You Will Master
- The limitations of JVM Heap (GC overhead, latency).
- Direct ByteBuffers and `sun.misc.Unsafe`.
- The Foreign Function & Memory API (Project Panama).
- Manual memory management in Java.
- Zero-copy I/O and interaction with native libraries.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - Why bypass the Garbage Collector?
2. [INTERNALS.md](./INTERNALS.md) - Virtual memory, page alignment, and memory segments.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Pure Java implementation using `MemorySegment` and `Arena`.