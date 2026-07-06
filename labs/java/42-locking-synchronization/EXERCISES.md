# Exercises: Locking & Synchronization

## Exercise 1: Reentrant AQS Lock
Modify AqsLock to support reentrancy. The lock should track how many times the owning thread has acquired it, and only release when the count reaches 0.

## Exercise 2: Fair vs Unfair Benchmark
Write a benchmark that compares fair and unfair ReentrantLock throughput with 1, 2, 4, 8, 16 threads. Also measure P99 wait time. Determine the cross-over point where fair lock performance degrades below unfair.

## Exercise 3: StampedLock Cache
Build a thread-safe key-value cache using StampedLock. Use optimistic reads for get(), pessimistic writes for put(). Measure throughput with varying read/write ratios (99/1, 90/10, 50/50).

## Exercise 4: LockSupport-based Latch
Implement a countdown latch using only LockSupport (no CountDownLatch, no synchronized). The latch should block threads until the count reaches 0.

## Exercise 5: CAS Stack
Implement a lock-free stack using CAS (Treiber stack). Use AtomicReference for the top pointer. Compare performance against synchronized Stack for concurrent push/pop.

## Exercise 6: Condition Queue
Implement a bounded buffer using ReentrantLock and two Conditions (notFull, notEmpty). The buffer should support blocking put and take with timeout.

## Exercise 7: Lock Profiler
Write a tool that attaches to a running JVM and reports which locks are most contended. Use `ThreadMXBean.getThreadInfo()` to sample thread states.
