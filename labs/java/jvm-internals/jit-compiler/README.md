# JIT Compiler Internals

Welcome to the atomic mastery lab for the **JIT (Just-In-Time) Compiler**. This lab is part of the Java Academy's JVM Internals module.

## 🧠 What You Will Master
- The transition from Bytecode to Native Machine Code.
- Tiered Compilation (C1 vs C2 compilers).
- Hotspot detection and method warming.
- Advanced optimizations: Inlining, Loop Unrolling, Escape Analysis.
- Code Cache management and Deoptimization.

## 📂 Lab Structure
1. [THEORY.md](./THEORY.md) - The architectural role of the JIT.
2. [INTERNALS.md](./INTERNALS.md) - Deep dive into C2 optimization techniques.
3. [CODE_DEEP_DIVE.md](./CODE_DEEP_DIVE.md) - Observing JIT activity with `-XX:+PrintCompilation`.