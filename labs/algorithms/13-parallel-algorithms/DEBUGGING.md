# Debugging — Parallel Algorithms

## Log Thread Information
`java
System.out.println(Thread.currentThread() + " processing [" + lo + "," + hi + ")");
`

## Verify Correctness
`java
int[] sequential = arr.clone();
Arrays.sort(sequential);

int[] parallel = arr.clone();
ForkJoinPool.commonPool().invoke(new ParallelSortTask(parallel, 0, parallel.length));

assert Arrays.equals(sequential, parallel) : "Sort results differ!";
`

## Monitor Pool Activity
`java
ForkJoinPool pool = ForkJoinPool.commonPool();
System.out.printf("Active: %d, Steals: %d%n", 
    pool.getActiveThreadCount(), pool.getStealCount());
`
"@

wf "REFACTORING.md" @"
# Refactoring — Parallel Algorithms

## Sequential → Parallel
`java
// Sequential
int sum = arr.stream().mapToInt(Integer::intValue).sum();

// Parallel (one line change)
int sum = arr.parallelStream().mapToInt(Integer::intValue).sum();
`

## Custom threshold tuning
`java
// Test different thresholds
for (int t : new int[]{100, 500, 1000, 5000, 10000}) {
    threshold = t;
    // benchmark
}
`
"@

wf "PERFORMANCE.md" @"
# Performance — Parallel Algorithms

## Merge Sort Benchmarks (n=10⁷)

| Cores | Sequential | Fork/Join | Speedup |
|-------|-----------|-----------|---------|
| 1 | 1.2s | 1.3s | 0.92x |
| 2 | 1.2s | 0.7s | 1.7x |
| 4 | 1.2s | 0.38s | 3.2x |
| 8 | 1.2s | 0.22s | 5.5x |

## When to Parallelize
- Large datasets (n > 100,000)
- CPU-bound computations
- Independent subproblems
- Low synchronization overhead
- Avoid for I/O-bound tasks (use async I/O instead)
