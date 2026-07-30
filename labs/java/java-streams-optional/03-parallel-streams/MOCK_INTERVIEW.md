# MOCK_INTERVIEW — Parallel Streams

## Scenario
You have a stream of 10 million integers. Compute the sum of squares. Compare sequential vs parallel.

## Interviewer Notes
- Candidate should benchmark both approaches
- Discuss data splitting overhead
- Note that `sum()` is associative, safe for parallel

## Expected Solution Sketch
```java
long sumParallel = LongStream.range(1, 10_000_000)
    .parallel()
    .map(n -> n * n)
    .sum();
```

## Follow‑Up
- What if the operation is not associative?
- How to use a custom ForkJoinPool for parallel streams?
