# Mini Project: JIT Behavior Profiler

## Objective
Build a tool that detects when a method has been JIT-compiled by monitoring execution time drops and reporting compiler events using `-XX:+PrintCompilation` log parsing.

## Requirements
1. Write a `JitProfiler` class that takes a `Runnable` and runs it in a loop
2. Measure per-iteration execution time using `System.nanoTime()`
3. Detect the "JIT kick-in" point where time drops significantly (>50% reduction sustained)
4. Report the iteration number where compilation likely occurred
5. Optionally parse `PrintCompilation` output if available

## Extended Features
- Classify methods as C1-compiled, C2-compiled, or interpreted
- Detect deoptimization events (sudden time increases after JIT)
- Generate a flame graph visualization of method timings
- Compare compilation thresholds with `-XX:CompileThreshold` values

## Example Output
```
Method: com.example.HotMethod.compute()
  Threshold: compiled at iteration #10,342
  C1 time: 1,234 ns (iter 0-10,342)
  C2 time: 234 ns (iter 10,343+)
  Speedup: 5.3x
  Deoptimization events: 2 (at iter #25,001, #37,221)
```

## Test Scenarios
- Simple arithmetic method (triggers C1 then C2)
- Polymorphic method (triggers deoptimization)
- Recursive method (OSR compilation)
- Method with try-catch (prevents some optimizations)

## Deliverables
- `JitProfiler.java` with core detection logic
- `JitProfilerTest.java` with test scenarios
- Analysis report for 5 different method patterns
