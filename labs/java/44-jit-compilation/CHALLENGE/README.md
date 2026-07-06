# Challenge: Predict and Control JIT Behavior

## Problem
Design a set of Java programs that reliably demonstrate specific JIT compilation behaviors. For each behavior, identify the exact JVM flags needed to observe it and explain why the behavior occurs.

## Behaviors to Demonstrate

### 1. Tiered Compilation (C1 → C2)
Write a method that shows three performance plateaus: interpreted, C1-compiled (with profiling), and C2-compiled (fully optimized). Measure and plot iteration time.

### 2. OSR (On-Stack Replacement)
Create a long-running loop that gets compiled mid-execution. Show with `-XX:+PrintCompilation` the `%` marker indicating OSR.

### 3. Lock Coarsening and Elision
Demonstrate scenarios where the JIT eliminates unnecessary locking (elision) and merges adjacent synchronized blocks (coarsening).

### 4. Loop Unrolling
Write a loop that is unrolled by C2. Use `-XX:+PrintAssembly` to identify unrolled loop bodies and measure the performance improvement.

### 5. Intrinsic Detection
Force the JIT to replace `Arrays.copyOf`, `String.length`, `System.arraycopy`, and `Math.min/max` with hand-coded intrinsics. Verify with `-XX:+PrintIntrinsics`.

### 6. Deoptimization
Create a bimorphic call site that becomes megamorphic at runtime, forcing deoptimization. Measure the performance cliff.

## Deliverables
- One Java file per behavior (e.g., `TieredCompilationDemo.java`, `OsrDemo.java`, etc.)
- Shell script with JVM flags to observe each behavior
- Explanation of observed output for each case
- Screenshots or verified output logs
