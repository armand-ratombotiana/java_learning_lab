# Exercises: JMH Benchmarks

## Basic Exercises

1. **Benchmark Setup**: Create first benchmark
   - Add JMH dependency
   - Create @Benchmark method

2. **Benchmark Modes**: Understand modes
   - Throughput mode (ops/time)
   - Average time mode
   - Sample time mode

3. **Execution**: Run benchmarks
   - Fork execution
   - Set warmup iterations

4. **Measurement**: Configure measurement
   - Set operations per iteration
   - Configure runtime

## Intermediate Exercises

5. **State Management**: Use benchmark state
   - Use @State for shared state
   - Configure scope (Benchmark, Thread, Group)

6. **Parameters**: Use benchmark parameters
   - Use @Param for varied inputs
   - Test multiple values

7. **Profiling**: Profile benchmark
   - Use -prof perfasm
   - Analyze hotspot

## Advanced Exercises

8. **Advanced Options**: Use advanced features
   - Configure GC cycles
   - Use -ff flag for detailed output