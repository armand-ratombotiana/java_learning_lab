# Virtual Threads Internals

## 🔬 Carrier Threads and Continuations
To understand Virtual Threads, we must look at how the JVM executes them.

### 1. Carrier Threads
A Virtual Thread does not run directly on the CPU. It must be mounted onto an OS thread to execute. The OS thread that executes a Virtual Thread is called its **Carrier Thread**.
By default, the JVM uses a `ForkJoinPool` to manage a small pool of Carrier Threads (usually equal to the number of CPU cores).

### 2. Continuations
A **Continuation** is a programming construct representing the rest of the execution of a program.
When a Virtual Thread performs a blocking I/O operation (e.g., `Socket.read()`), the JVM intercepts this call.
Instead of blocking the Carrier Thread, the JVM:
1. Captures the current execution state (the stack frames) of the Virtual Thread.
2. Moves this state (the Continuation) from the Carrier Thread's stack into the Java Heap.
3. Unmounts the Virtual Thread.
4. The Carrier Thread is now free to execute another Virtual Thread.

When the I/O operation completes (signaled by the OS via epoll/kqueue), the JVM takes the Continuation from the Heap, mounts it back onto any available Carrier Thread, and resumes execution exactly where it left off.

## ⚠️ The Pinning Problem
In some scenarios, a Virtual Thread cannot be unmounted. This is called **Pinning**.
If a Virtual Thread is pinned and performs a blocking operation, it *will* block the underlying Carrier Thread, reducing throughput.

Pinning usually occurs when:
1. The Virtual Thread executes a `synchronized` block or method.
2. The Virtual Thread executes a native method or a foreign function.

**Solution**: Replace `synchronized` blocks with `ReentrantLock`. `ReentrantLock` has been updated in Java 21 to be Virtual Thread friendly (it does not cause pinning).