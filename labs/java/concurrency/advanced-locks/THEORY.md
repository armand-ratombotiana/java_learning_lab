# Advanced Locks Theory & Intuition

## 💡 The Problem with `synchronized`
Java's built-in `synchronized` keyword (intrinsic locking) is incredibly easy to use and prevents basic race conditions. However, it has severe limitations for high-performance applications:
1. **Uninterruptible**: If Thread A holds a lock and Thread B tries to acquire it, Thread B blocks forever. You cannot interrupt Thread B or tell it to give up after 5 seconds.
2. **No Fairness**: When the lock is released, any waiting thread might grab it. A thread could be starved indefinitely while newer threads keep grabbing the lock.
3. **Coarse-Grained**: It locks the entire block of code for both reading and writing, even if 99% of threads only want to read the data without mutating it.

## 🚀 The Solution: Explicit Locks (`java.util.concurrent.locks`)
Java 5 introduced explicit lock objects that implement the `Lock` interface.

### `ReentrantLock`
A drop-in replacement for `synchronized` but with superpowers:
- **Try-Lock**: You can call `lock.tryLock(5, TimeUnit.SECONDS)`. If the lock isn't available after 5 seconds, the thread can walk away and do something else instead of deadlocking.
- **Fairness**: `new ReentrantLock(true)` guarantees that the longest-waiting thread gets the lock next (FIFO queue).

### `ReentrantReadWriteLock`
Many data structures (like caches) are read thousands of times per second but only written to occasionally.
A `ReadWriteLock` maintains a pair of associated locks:
- **Read Lock**: Multiple threads can hold the read lock simultaneously.
- **Write Lock**: Only one thread can hold the write lock, and it requires all readers to finish before it can be acquired.
This dramatically increases throughput for read-heavy workloads.

### `StampedLock` (Java 8+)
Even a `ReadWriteLock` has a flaw: readers block writers. If you have a constant stream of readers, a writer might starve forever waiting for the read lock to be completely free.
`StampedLock` introduces **Optimistic Reading**. A thread can read the data *without* acquiring any lock at all. It just gets a "stamp" (a version number). After reading, it checks if a writer modified the data while it was reading. If so, it throws away the read data and tries again (or upgrades to a real read lock).