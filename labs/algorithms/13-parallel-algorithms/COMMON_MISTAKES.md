# Common Mistakes

- Forking all subtasks instead of computing one directly — doubles thread usage
- Threshold too small → overhead from task creation dominates
- Threshold too large → insufficient parallelism
- Shared mutable state without synchronization
- Using blocking I/O inside ForkJoinTask
- Not using ForkJoinPool.invoke() — tasks not submitted correctly
- deadlock from improper join ordering
