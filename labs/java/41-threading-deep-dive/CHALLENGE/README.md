# Challenge: Implement Work-Stealing Without ForkJoinPool

## Problem
Implement a work-stealing thread pool algorithm without using `ForkJoinPool` or `ExecutorService`. Each worker thread maintains its own deque of tasks. When a worker's deque is empty, it steals tasks from the tail of another worker's deque (LIFO for local, FIFO for stealing — the classic work-stealing design).

## Specifications
- `WorkStealingPool(int parallelism)` constructor
- `void submit(Runnable task)` — submits task to a random worker
- Workers run in daemon mode
- Use `ThreadLocalRandom` for victim selection
- Support `shutdown()` — complete all tasks, then stop

## Advanced Challenges
1. Implement `submit(Callable<T>)` with `Future<T>` support
2. Add work-stealing metrics (stolen task count, local run count)
3. Compare throughput against `ForkJoinPool.commonPool()` using the parallel sum benchmark
4. Implement the "victim selection" with a randomized strategy and compare to linear probing

## Edge Cases
- A worker trying to steal its own task
- Task submission during shutdown
- Very fine-grained tasks (sub-microsecond) causing stealing overhead to dominate
- Very coarse tasks (seconds) causing starvation

## Hints
- Use `ArrayDeque` (non-thread-safe, owned by each worker) with `synchronized` blocks for push/pop
- Use `Lock` and `Condition` for blocking when no tasks available
- `ThreadLocalRandom.current().nextInt(parallelism)` for victim selection

## Success Criteria
- Correct parallel sum of 100M integers
- No deadlocks or livelocks under high throughput
- Steal count > 0 demonstrating actual work-stealing behavior
