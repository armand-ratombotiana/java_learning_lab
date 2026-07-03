# Debugging Complexity

## Benchmark to Verify Complexity
`java
long start = System.nanoTime();
// run algorithm with n=100, 1000, 10000
long end = System.nanoTime();
System.out.printf("n=%d: %d ns%n", n, end - start);
`

## Use Big O to Find Bugs
If an algorithm should be O(n) but the benchmark shows O(n²), there's a hidden loop or inefficient operation.
