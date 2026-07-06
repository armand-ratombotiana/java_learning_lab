# Step-by-Step: JIT Compilation

## Step 1: Run JitCompilationDemo.java
Execute `JitCompilationDemo` and observe the output. Run with `-XX:+PrintCompilation`:
```bash
java -XX:+PrintCompilation com.javaacademy.lab44.jit.JitCompilationDemo
```
Look for `compute()` appearing in the compilation log. The `%` marker indicates OSR compilation if the loop triggers it.

## Step 2: Run InliningDemo.java
Execute with `-XX:+PrintInlining`:
```bash
java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining com.javaacademy.lab44.jit.InliningDemo
```
Look for `add()` being inlined into `compute()`. The output shows "Inlining" with the inlining depth.

## Step 3: Run IntrinsicExample.java
Execute and note the performance. Run with `-XX:+PrintIntrinsics` (diagnostic flag) to see intrinsic replacement. Compare against a manual loop-based array copy.

## Step 4: Run EscapeAnalysisDemo.java
Execute and note the timing differences between noEscape, escapes, and escapesViaGlobal. The time difference is due to heap allocation overhead and GC pressure. Run with `-XX:+PrintEscapeAnalysis` to see escape analysis decisions.

## Step 5: Run DeoptimizationTrigger.java
Execute and observe the timing differences between monomorphic, bimorphic, and megamorphic phases. The megamorphic phase is significantly slower due to deoptimization and virtual dispatch.

## Step 6: Run JitCompilationTest.java
Execute all JUnit tests. Each test verifies a specific JIT-related behavior without requiring specific JVM flags.

## Step 7: Experiment with JVM Flags
Try different JVM flags to observe their impact:
```bash
java -XX:+PrintCompilation -XX:CompileThreshold=5000 JitCompilationDemo
java -XX:+PrintCompilation -XX:CompileThreshold=50000 JitCompilationDemo
java -XX:MaxInlineSize=5 InliningDemo  # prevent inlining of add()
```
