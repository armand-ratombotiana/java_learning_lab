# Virtual Threads Theory & Intuition

## 💡 The Problem: OS Threads are Heavy
Historically, every `java.lang.Thread` was a thin wrapper around a native Operating System (OS) thread.
OS threads are expensive:
- **Memory**: Each OS thread typically requires a 1MB-2MB stack size. If you want to handle 10,000 concurrent requests using a thread-per-request model, you need 10GB-20GB of RAM just for the thread stacks.
- **Context Switching**: When the OS switches execution from one thread to another, it requires a costly context switch (saving registers, flushing caches) in kernel space.

Because of this, we had to use **Thread Pools** to reuse a small number of threads. But this led to asynchronous, non-blocking programming (like WebFlux or CompletableFuture), which is notoriously difficult to read, debug, and maintain.

## 🚀 The Solution: Virtual Threads (Project Loom)
Introduced in Java 21, Virtual Threads are lightweight threads managed by the JVM, not the OS.
- **Cheap**: They consume only a few bytes of memory. You can easily spawn millions of them.
- **Fast Switching**: Context switching happens in user space (within the JVM), making it incredibly fast.
- **Synchronous Code**: You can write simple, readable, synchronous, blocking code (the thread-per-request model) without worrying about exhausting system resources.

## ⚙️ How They Work (High Level)
When a Virtual Thread hits a blocking operation (like waiting for a database response or an HTTP call), the JVM automatically "unmounts" it. The underlying OS thread is freed up to run a different Virtual Thread. When the database responds, the JVM "mounts" the Virtual Thread back onto an available OS thread to resume execution.