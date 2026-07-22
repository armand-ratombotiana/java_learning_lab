# Interview Questions: JMH Benchmarks

## Company-Specific Focus

### Google
- JMH (Java Microbenchmark Harness): framework for accurate microbenchmarking
- @Benchmark: marking benchmarking methods
- @BenchmarkMode: throughput, average time, sample time, single shot

### Microsoft
- JMH vs BenchmarkDotNet (.NET): similar microbenchmarking approaches
- Warm-up: iterations to warm up JVM before measurement

### Amazon
- Blackhole: consuming values without optimization
- Compiler control: preventing JIT optimizations from skewing results
- Fork: forked JVM to isolate benchmark from previous benchmarks

### Meta
- State objects: @State(Scope.Thread/Group/Benchmark)
- Setup/Teardown: @Setup, @TearDown for benchmark lifecycle
- Parameterized benchmarks: @Param for testing different parameters

### Apple
- Annotation-driven: @Benchmark, @State, @OutputTimeUnit
- Profilers: built-in profilers (async-profiler integration)

### Oracle
- JMH is part of OpenJDK, authored by Aleksey Shipilëv
- JVM optimizations: how JMH avoids common benchmarking pitfalls
- Dead code elimination: how JMH prevents JIT from eliminating benchmark code

## LeetCode-Related Questions
| LC Problem | Difficulty | Companies | Notes |
|------------|------------|-----------|-------|
| (No direct LC problems — JMH is a performance testing tool) |

## Real Production Scenarios
- **Netflix**: JMH benchmarks for comparing serialization libraries (Kryo vs Protobuf)
- **LinkedIn**: JMH benchmark revealed ArrayList vs LinkedList performance for specific access pattern

## Interview Patterns & Tips
- **Blackhole**: consume values to prevent dead code elimination
- **Fork**: use at least 1 fork to get clean JVM state
- **Warmup**: at least 5 warmup iterations for stable results
- **BenchmarkMode**: Throughput for ops/time, AverageTime for time/op

## Deep Dive Questions
- **Dead code elimination**: How does JMH prevent JIT from eliminating benchmark code?
- **Blackhole**: How does Blackhole.consume() work?
- **Fork**: Why is forking important for accurate results?
- **JIT warmup**: How does JMH handle JIT compilation during warmup?
- **Loop optimization**: How does JMH prevent loop optimization from hiding benchmark cost?