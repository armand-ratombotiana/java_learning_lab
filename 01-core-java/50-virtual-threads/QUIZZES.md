# Module 50: Virtual Threads (Project Loom) - Quizzes

---

## Q1: Virtual Thread Concept
What is the primary difference between a Platform Thread and a Virtual Thread in Java 21?

A) Virtual threads run faster than platform threads when performing heavy CPU math calculations.
B) Virtual threads are mapped 1-to-1 with operating system threads, while platform threads are not.
C) Virtual threads are extremely lightweight, managed entirely by the JVM, and can be suspended and resumed without blocking the underlying OS thread during I/O operations.
D) Virtual threads do not support local variables.

**Answer**: C
**Explanation**: Virtual threads decouple the Java thread from the OS thread. By allowing the JVM to manage them, a small pool of OS threads (carrier threads) can seamlessly support millions of concurrent, blocking I/O operations.

---

## Q2: Virtual Thread Pitfalls
Which of the following is considered an anti-pattern when working with Virtual Threads?

A) Using `Thread.sleep()` inside a Virtual Thread.
B) Using `Executors.newVirtualThreadPerTaskExecutor()` to create a new thread for every task.
C) Creating a fixed-size thread pool (e.g., of 100 threads) to manage Virtual Threads.
D) Making blocking database calls.

**Answer**: C
**Explanation**: Because virtual threads consume negligible memory, pooling them is unnecessary and counterproductive. You should spawn a new virtual thread for every individual task and let it terminate upon completion.

---

## Q3: Carrier Thread Pinning
What happens if a Virtual Thread performs a blocking I/O operation while inside a `synchronized` block?

A) The Virtual Thread safely unmounts, and the carrier thread continues working.
B) The JVM throws a `VirtualMachineError`.
C) The Virtual Thread "pins" itself to the OS carrier thread, blocking the carrier thread from doing any other work until the synchronized block exits.
D) The synchronized block is automatically ignored by the JVM.

**Answer**: C
**Explanation**: Currently, the JVM cannot unmount a virtual thread if it is holding a monitor lock via a `synchronized` block or method. To avoid pinning, developers should refactor such blocks to use `java.util.concurrent.locks.ReentrantLock`.