# Interview Preparation: Atomic Variables & Hardware Concurrency

This document covers advanced questions related to CAS, the ABA problem, memory visibility, and lock-free algorithms.

## Q1: Explain the Compare-And-Swap (CAS) mechanism. How does it enable lock-free programming?
**Answer:**
CAS is an atomic hardware instruction provided by modern CPUs. It takes three arguments: a memory location, an expected value, and a new value.
The CPU atomically checks if the memory location currently holds the expected value. If it does, it updates the memory location with the new value and returns true. If it doesn't (because another thread modified it), it does nothing and returns false.
This enables "Optimistic Concurrency." A thread can read a value, calculate a new value, and attempt a CAS. If it fails, the thread simply loops (spins) and tries again. No OS-level locks, context switches, or thread suspensions are required, making it extremely fast for simple operations.

## Q2: What is the ABA problem, and how do you solve it in Java?
**Answer:**
The ABA problem occurs in lock-free algorithms. Thread 1 reads value 'A'. Before Thread 1 can perform its CAS, Thread 2 changes the value to 'B', and then Thread 3 changes it back to 'A'. Thread 1 executes its CAS, sees 'A', and succeeds.
In simple counters, this doesn't matter. But if 'A' is a pointer to a node in a lock-free linked list, the fact that it changed to 'B' and back to 'A' means the entire structure of the list might have changed. Thread 1's successful CAS will now corrupt the list.
**Solution**: Use `AtomicStampedReference`. It pairs the object reference with an integer stamp (version number). Every update increments the stamp. Even if the reference goes A -> B -> A, the stamp goes 1 -> 2 -> 3. Thread 1's CAS will fail because it expects stamp 1, but the current stamp is 3.

## Q3: Explain "False Sharing" and how to prevent it.
**Answer:**
CPUs read data from main memory into L1/L2 caches in chunks called "Cache Lines" (typically 64 bytes).
If Thread 1 frequently updates `variableX` and Thread 2 frequently updates `variableY`, and both variables happen to reside on the *same cache line*, the CPU hardware will force the threads to constantly invalidate and reload the entire cache line, even though they are modifying completely independent variables. This destroys multi-core performance.
**Prevention**: Pad the variables with dummy data so they occupy different cache lines, or use the `@Contended` annotation (Java 8+) to instruct the JVM to automatically pad the fields.

## Q4: Why was `LongAdder` introduced in Java 8 if we already had `AtomicLong`?
**Answer:**
Under extreme contention (dozens of threads constantly incrementing the same counter), `AtomicLong` suffers from severe performance degradation. Because CAS is optimistic, if 50 threads try to CAS simultaneously, 1 succeeds and 49 fail and spin-loop. This burns massive CPU cycles.
`LongAdder` solves this by maintaining an array of variables (cells). Different threads are hashed to different cells, so they can increment their own cells concurrently without contention. When you call `sum()`, it adds up all the cells. It trades a slightly slower read operation and higher memory footprint for massive write-throughput under contention.

## Q5: What is a `VarHandle`, and why is it preferred over `AtomicFieldUpdater`?
**Answer:**
Introduced in Java 9, `VarHandle` is a typed reference to a variable that allows for atomic operations and fine-grained memory fencing.
It is preferred over `AtomicFieldUpdater` because:
1.  It compiles down to tighter, faster JVM bytecode (similar to the internal, unsafe `sun.misc.Unsafe` class).
2.  It supports a much wider range of operations, including different memory ordering modes (e.g., `getOpaque`, `setRelease`, `getAcquire`), allowing developers to write highly optimized, low-latency concurrent algorithms without paying the full cost of `volatile` memory barriers on every operation.