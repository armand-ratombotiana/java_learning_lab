# Tests for JIT Compilation

## Test Categories

### Unit Tests (JitCompilationTest.java)
- `testJitCompilationResult()` — verifies compute() produces expected positive output
- `testInlining()` — validates small method called in a loop produces correct result
- `testEscapeAnalysisNoEscape()` — verifies noEscape() returns correct sum (300,000)
- `testIntrinsicArraycopy()` — validates System.arraycopy copies elements correctly
- `testDeoptimization()` — exercises polymorphic dispatch without error

### Running Tests
```bash
mvn test -pl labs/java/44-jit-compilation
```

### Test Design Notes
- Tests are designed to be JIT-friendly (≥10K iterations for warmup)
- Volatile sinks prevent dead code elimination
- Tests avoid environmental assumptions (actual JIT behavior is JVM-dependent)

### Additional Test Scenarios
- Verify that the performance ratio between escape/noEscape is measurable
- Verify that arraycopy intrinsic produces correct results under concurrent access
- Verify that deoptimization demo doesn't throw ClassCastException
- Test with `-Djava.compiler=NONE` to force interpreted mode comparison

### Coverage Targets
- JIT compilation: method behavior post-compilation
- Inlining: correctness of inlined arithmetic
- Escape analysis: correct results with and without allocation
- Intrinsics: API-level correctness of intrinsic methods
