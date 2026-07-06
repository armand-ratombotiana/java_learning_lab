# Flashcards: JIT Compilation

**Q: What are the five tiers of JIT compilation?**
A: 0 (Interpreter), 1 (Simple C1), 2 (Limited C1), 3 (Full C1 + profiling), 4 (C2 fully optimized).

**Q: What is the default compilation threshold?**
A: ~10,000 method invocations before C2 compilation begins.

**Q: What is method inlining?**
A: Replacing a method call site with the method body, eliminating call overhead and enabling cross-method optimization.

**Q: What is an intrinsic in the JVM?**
A: A method recognized by the JIT and replaced with hand-optimized machine code.

**Q: What is escape analysis?**
A: Analysis that determines whether an object's reference escapes the method/thread, enabling stack allocation, scalar replacement, and lock elision.

**Q: What is deoptimization?**
A: The JIT reverts from optimized compiled code to interpreter execution because its optimization assumptions were invalidated.

**Q: What does OSR stand for?**
A: On-Stack Replacement — compiling a long-running loop mid-execution and switching to the compiled version.

**Q: At how many receiver types does a call site become megamorphic?**
A: 3 or more types. Bimorphic means exactly 2 types. Monomorphic means 1 type.
