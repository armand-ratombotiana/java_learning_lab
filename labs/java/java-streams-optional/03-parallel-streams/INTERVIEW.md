# INTERVIEW — Parallel Streams

## Company-Specific Focus

### Google
- When should you NOT use parallel streams?
- ForkJoinPool common pool — what happens if one task blocks

### Amazon
- Parallel stream performance with large datasets (S3 logs, clickstreams)
- Spliterator characteristics — `SIZED`, `SUBSIZED`, `ORDERED`

### Oracle
- `ForkJoinPool` custom parallelism — `-Djava.util.concurrent.ForkJoinPool.common.parallelism=N`
- `Collector.Characteristics.CONCURRENT` with parallel streams

## Common Questions
1. How does `trySplit()` affect parallelism?
2. Why can parallel streams be slower than sequential for small datasets?
3. What is the ordering guarantee for `findAny()` in a parallel stream?
