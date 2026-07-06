# Interview Questions: JIT Compilation

## Beginner
1. What is JIT compilation and why does Java need it?
2. Explain the difference between C1 and C2 compilers.
3. What is method inlining and why is it important?
4. How can you see which methods are being compiled?

## Intermediate
5. Explain tiered compilation. What are the tiers and when does each trigger?
6. What are JVM intrinsics? Name three examples.
7. What is escape analysis and what optimizations does it enable?
8. What causes deoptimization? How does the JVM recover?

## Advanced
9. How does the JIT determine whether to inline a method?
10. Explain how inline caches work for monomorphic, bimorphic, and megamorphic call sites.
11. What is OSR (On-Stack Replacement) and when is it used?
12. How does scalar replacement differ from stack allocation?
13. What is the impact of `-XX:CompileThreshold` on application warmup?

## Expert
14. How does the C2 compiler's Ideal Graph differ from C1's HIR?
15. Explain how the JIT ensures correct execution when assumptions change (class hierarchy, type profiles).
16. How would you design a benchmark that correctly measures JIT performance?
17. What are the tradeoffs between AOT compilation (jaotc) and JIT compilation?
18. How does the Graal compiler differ from C2?
19. Explain the memory ordering guarantees of JIT-compiled code vs the Java Memory Model.

## Answers
Available in the SOLUTION directory.
