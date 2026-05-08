# Pedagogic Guide: JMH Benchmarks

## Learning Path

### Phase 1: Foundation (Day 1)
- Understand microbenchmarking
- Learn JMH fundamentals
- Create first benchmark

### Phase 2: Core Skills (Day 2-3)
- Configure benchmark modes
- Set up proper warmup
- Analyze results

### Phase 3: Advanced (Day 4-5)
- Use state and parameters
- Profile with perfasm
- Optimize based on results

## Key Concepts

| Concept | Description |
|---------|-------------|
| @Benchmark | Marks benchmark method |
| @State | Shared state between calls |
| Fork | Separate JVM for execution |
| Warmup | Iterations before measurement |

## Prerequisites
- Java fundamentals
- Performance awareness

## Common Pitfalls
- Dead code elimination
- Hotspot optimization
- Missing warmup