# Interview Preparation: Deadlock & Livelock

This document covers advanced questions related to the Coffman conditions, deadlock prevention strategies, and thread starvation.

## Q1: What are the four Coffman conditions for a deadlock? How do you break them?
**Answer:**
1.  **Mutual Exclusion**: Resources cannot be shared. (Hard to break, usually required by business logic).
2.  **Hold and Wait**: A thread holds one resource while waiting for another. (Break by requiring a thread to acquire all locks simultaneously, which is inefficient).
3.  **No Preemption**: Resources cannot be taken away; they must be released voluntarily. (Break by using `tryLock(timeout)`. If a thread can't get the second lock, it releases the first one).
4.  **Circular Wait**: A cycle of threads waiting for each other's resources. (Break by using **Lock Ordering**—enforcing a strict global sequence for acquiring locks).

## Q2: Explain the "Lock Ordering" strategy. What happens if the objects being locked have the same `hashCode()`?
**Answer:**
Lock Ordering breaks the Circular Wait condition. If you need to lock Object A and Object B, you must always lock them in the same order, regardless of the context. Typically, this is done by comparing their `System.identityHashCode()`.
If `hash(A) < hash(B)`, lock A then B.
**The Hash Collision Problem**: `identityHashCode` is not guaranteed to be unique. If `hash(A) == hash(B)`, the ordering logic breaks down. To fix this, you must introduce a static, global "Tie-Breaker" lock. If the hashes are equal, the thread must first acquire the Tie-Breaker lock, then lock A, then lock B. This ensures mutual exclusion during the ambiguous ordering phase.

## Q3: What is Livelock, and how does `tryLock()` cause it?
**Answer:**
Livelock occurs when threads are actively executing and changing state, but they are stuck in a loop reacting to each other and making no actual progress.
If Thread 1 and Thread 2 both use `tryLock()` to acquire Lock A and Lock B:
1. Thread 1 gets Lock A. Thread 2 gets Lock B.
2. Thread 1 tries to get Lock B, fails, and releases Lock A.
3. Thread 2 tries to get Lock A, fails, and releases Lock B.
4. They both immediately loop and grab their first locks again.
They are burning 100% CPU but achieving nothing.
**Solution**: Introduce **Random Backoff**. When a thread fails to acquire the second lock, it should release the first lock and sleep for a *random* duration before retrying. This breaks the symmetry and allows one thread to grab both locks while the other is sleeping.

## Q4: What is Thread Starvation, and how does a Fair Lock solve it?
**Answer:**
Starvation occurs when a thread is perpetually denied access to a resource it needs. It is not deadlocked, it just keeps losing the race to acquire the lock. This often happens with the `synchronized` keyword, which makes no guarantees about which waiting thread gets the lock next. A high-priority thread might constantly jump the queue, starving a low-priority thread.
**Solution**: Use `new ReentrantLock(true)` to create a "fair" lock. A fair lock guarantees FIFO (First-In, First-Out) ordering. The longest-waiting thread is guaranteed to get the lock next.
**Trade-off**: Fair locks suffer from massive performance degradation due to the overhead of maintaining the queue and forcing context switches, preventing "barging" (where an active thread just grabs an available lock).

## Q5: How do you detect a deadlock in a running Java application?
**Answer:**
1.  **Command Line**: Use `jstack <pid>` to generate a thread dump. The JVM will automatically analyze the dump and print a section explicitly titled "Found one Java-level deadlock", listing the involved threads and the locks they are waiting on.
2.  **Visual Tools**: Connect JConsole or VisualVM to the running process and click the "Detect Deadlock" button.
3.  **Programmatically**: Use JMX (Java Management Extensions). Call `ManagementFactory.getThreadMXBean().findDeadlockedThreads()`. This returns an array of thread IDs that are deadlocked, allowing the application to log the event or trigger an alert.