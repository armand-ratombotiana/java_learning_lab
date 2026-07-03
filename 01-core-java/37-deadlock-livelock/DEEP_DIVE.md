# Deep Dive: Deadlock, Livelock, & Starvation

## 1. The Coffman Conditions for Deadlock
A deadlock occurs when two or more threads are blocked forever, waiting for each other to release resources. For a deadlock to occur, **four conditions must hold true simultaneously** (known as the Coffman conditions):

1.  **Mutual Exclusion**: At least one resource must be held in a non-shareable mode (only one thread can use it at a time).
2.  **Hold and Wait**: A thread must be holding at least one resource and waiting to acquire additional resources held by other threads.
3.  **No Preemption**: Resources cannot be forcibly taken away from a thread; they must be released voluntarily by the thread holding them.
4.  **Circular Wait**: There must exist a set of threads $\{T_1, T_2, ..., T_n\}$ such that $T_1$ is waiting for a resource held by $T_2$, $T_2$ is waiting for $T_3$, and $T_n$ is waiting for $T_1$.

*To prevent a deadlock, you only need to break ONE of these four conditions.*

## 2. Breaking the Coffman Conditions (Prevention)

### Breaking "Hold and Wait"
Require a thread to acquire *all* the locks it needs at the exact same time, before it begins execution. If it can't get all of them, it gets none of them.
*   *Drawback*: Highly inefficient and reduces concurrency.

### Breaking "No Preemption"
Use `ReentrantLock.tryLock(timeout)`. If a thread holds Lock A and tries to acquire Lock B but times out, it voluntarily releases Lock A, waits a random amount of time, and tries the whole process again.
*   *Drawback*: Can lead to Livelock (see below).

### Breaking "Circular Wait" (The Best Approach)
**Lock Ordering**: Impose a strict, global order in which locks must be acquired by all threads. If Lock A always precedes Lock B, a circular wait is mathematically impossible.

```java
// Preventing Deadlock by ordering locks based on an intrinsic property (like an ID or HashCode)
public void transferFunds(Account from, Account to, int amount) {
    Account firstLock = from.getId() < to.getId() ? from : to;
    Account secondLock = from.getId() < to.getId() ? to : from;

    synchronized(firstLock) {
        synchronized(secondLock) {
            from.withdraw(amount);
            to.deposit(amount);
        }
    }
}
```

## 3. Livelock
A livelock is similar to a deadlock, except the states of the threads involved constantly change with regard to one another, but none progress.
*   **Analogy**: Two people meet in a narrow hallway. Person A steps to the left to let B pass. Person B steps to the right to let A pass. They are now blocking each other again. They both step to the other side. They continue this dance forever.
*   **Code Scenario**: Two threads use `tryLock()`. Thread 1 grabs Lock A. Thread 2 grabs Lock B. Thread 1 tries to grab Lock B, fails, and releases Lock A. Thread 2 tries to grab Lock A, fails, and releases Lock B. They both immediately try again, repeating the cycle infinitely.
*   **Prevention**: Introduce randomness. When a `tryLock()` fails, the thread should sleep for a random duration before retrying, breaking the symmetry of the clash.

## 4. Starvation
Starvation occurs when a thread is perpetually denied access to resources it needs to make progress. The thread is not deadlocked (it is not waiting for a specific thread in a cycle), but it keeps losing the race to acquire the lock.
*   **Scenario**: A system uses `synchronized` blocks. Thread A has a low priority. Threads B, C, D, E, and F have high priority and are constantly acquiring and releasing the lock. Because `synchronized` makes no guarantees about fairness, Thread A might never be chosen by the JVM to acquire the lock.
*   **Prevention**: Use `ReentrantLock(true)` to create a "fair" lock. A fair lock guarantees that the longest-waiting thread gets the lock next (FIFO). *Note: This comes with a severe performance penalty.*

## 5. Deadlock Detection
In Java, deadlocks can be detected at runtime using tools like `jstack`, VisualVM, or JConsole. The JVM automatically analyzes the thread dump and prints a specific "Found one Java-level deadlock" section, detailing exactly which threads are blocked and which locks they hold.
Programmatically, you can use `ThreadMXBean.findDeadlockedThreads()` from the `java.lang.management` package to detect deadlocks from within the application itself.