# Performance — Control Flow

## Branch Prediction

Modern CPUs use branch prediction. Predictable patterns (e.g., all true, alternating) are faster than random conditions. The JIT compiler may profile branches and reorder.

## Switch Performance

- `tableswitch` (dense int cases): O(1) jump table
- `lookupswitch` (sparse int cases): O(log n) binary search
- String switch: hash + switch + equals — similar to lookupswitch

## Loop Overhead

- Enhanced for-loop on arrays: same as indexed loop (compiler generates same bytecode)
- Enhanced for-loop on Iterables: creates iterator, overhead for small collections
- Empty loops: JIT eliminates them entirely

## Avoid in Hot Paths

- `String.format()` — uses reflection, expensive
- Exception creation — stack trace capture is costly
- Boxing/unboxing — object allocation
- Deep recursion — stack overflow risk, function call overhead

## Loop Invariant Code Motion

Move computations that don't change per iteration outside the loop:
```java
// Bad:
for (int i = 0; i < list.size(); i++) { ... }

// Good:
int size = list.size();
for (int i = 0; i < size; i++) { ... }
```
