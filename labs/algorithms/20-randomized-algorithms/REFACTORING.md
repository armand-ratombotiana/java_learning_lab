# Refactoring — Randomized Algorithms

## Configurable Random Source

`java
interface RandomSource {
    int nextInt(int bound);
    double nextDouble();
}
`

Allow injecting Random, ThreadLocalRandom, or SecureRandom.

## Strategy Pattern for Quickselect

Make the pivot selection strategy pluggable:
- RandomPivotStrategy
- MedianOfThreeStrategy
- Deterministic3MedianStrategy

## Observable Reservoir

Create a reservoir stream that emits intermediate states for visualization and debugging.

## Parallel Reservoir Sampling

For large streams, partition the stream, sample each partition independently, then merge samples using a final reservoir pass.
