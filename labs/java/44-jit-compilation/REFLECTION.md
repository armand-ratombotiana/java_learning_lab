# Reflection: JIT Compilation

## Key Takeaways
- JIT compilation is what makes Java competitive with native languages
- Tiered compilation balances startup time and peak performance
- Inlining, intrinsics, and escape analysis are the most impactful optimizations
- Deoptimization explains sudden performance cliffs
- Profiling (via JFR, PrintCompilation) is essential for JIT performance analysis

## Connections to Other Concepts
JIT compilation connects to garbage collection (allocation patterns affect escape analysis), virtual threads (pinning from synchronized blocks affects compilation), and bytecode engineering (invokedynamic affects JIT optimization). The compiler uses GC safepoints to install compiled code; deoptimization must interact with GC to find object references in compiled frames.

## Challenges Encountered
- Benchmarking correctly (warmup, dead code elimination)
- Interpreting PrintCompilation and PrintInlining output
- Understanding the tradeoff between C1 and C2 compilation time
- Predicting when deoptimization will occur

## Questions to Explore Further
1. How does the Graal compiler compare to C2 in terms of optimization quality and compilation speed?
2. How will Project Leyden (ahead-of-time optimizations) change the JIT landscape?
3. How do different CPU architectures (x86 vs ARM vs RISC-V) affect JIT code generation?
4. What is the role of profile-guided optimization (PGO) in future Java versions?

## Practical Application
- Always warm up before measuring performance
- Use intrinsified methods (arraycopy, Math functions) for best performance
- Keep hot methods small for inlining
- Avoid megamorphic dispatch in hot paths
- Use escape analysis friendly patterns (avoid returning short-lived objects)
- Monitor compilation with -XX:+PrintCompilation in production (low overhead)

## Next Steps
- Study the OpenJDK source code for C2's Ideal Graph
- Experiment with GraalVM's JIT compiler
- Learn JMH for serious microbenchmarking
- Explore how reactive frameworks (RxJava, Reactor) optimize for JIT
