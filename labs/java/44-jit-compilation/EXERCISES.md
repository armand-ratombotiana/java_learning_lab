# Exercises: JIT Compilation

## Exercise 1: Compilation Threshold Discovery
Write a method that takes an iteration count and measures execution time. Run it with 100, 1,000, 10,000, 100,000 iterations and plot the time-per-iteration curve. Identify the compilation threshold where performance improves.

## Exercise 2: Intrinsic Detection
Write a program that uses `System.arraycopy`, `Math.sqrt`, `Math.min`, `Math.max`, `String.length`, and `Integer.bitCount`. Run with `-XX:+PrintIntrinsics` to see which are recognized as intrinsics.

## Exercise 3: Inlining Budget
Create a method chain of depth 15 (A calls B calls C ...). Make each method 10 bytes. Use `-XX:+PrintInlining` to see which methods are inlined and which are not (beyond MaxInlineLevel=9).

## Exercise 4: Escape Analysis Verification
Write a method that creates an object, reads its fields, and returns the sum. Make the object escape (return it) and not escape. Compare GC activity with `-XX:+PrintGC`.

## Exercise 5: Deoptimization Trigger
Create a polymorphic call site with 1, 2, 3, and 5 implementations. Use `-XX:+PrintCompilation` to identify deoptimization events as the type count increases.

## Exercise 6: OSR Detection
Write a method with a long-running loop (10+ seconds). Use `-XX:+PrintCompilation` to see the `%` OSR compilation marker. Compare the loop's execution speed before and after OSR.

## Exercise 7: C1 vs C2 Comparison
Write a benchmark that measures elapsed time for the same workload with:
- `-XX:-TieredCompilation -XX:+UseC1` (C1 only)
- `-XX:-TieredCompilation -XX:+UseC2` (C2 only)
- Default (tiered)
Compare startup time and steady-state throughput.
