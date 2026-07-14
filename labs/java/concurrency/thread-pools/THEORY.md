# Thread Pools Theory & Intuition

## 💡 The Problem: Creating Threads is Expensive
In Java, a `new Thread()` is not just a small object. It is a request to the Operating System to allocate a heavy kernel-level thread.
1. **Memory**: Each thread typically reserves ~1MB of RAM for its stack.
2. **CPU**: Spawning a thread requires expensive syscalls and context switches.
3. **Stability**: If your web server spawns a new thread for every incoming request, a traffic spike will cause your server to crash with an `OutOfMemoryError`.

## 🚀 The Solution: The Worker Pool
Instead of "one-and-done" threads, we maintain a persistent pool of **Worker Threads**.
- Tasks (runnables) are submitted to a **Work Queue**.
- The Worker Threads continuously pull tasks from the queue and execute them.
- When a task is finished, the thread doesn't die; it goes back to the queue to wait for the next task.

This provides **Resource Throttling**: You can limit your application to exactly 200 threads, ensuring you never exceed the physical limits of your server.

## 🏗️ The `ExecutorService` Hierarchy
Java provides a high-level API to manage pools:
- **`Executor`**: The simplest interface (`execute(Runnable)`).
- **`ExecutorService`**: Adds lifecycle management (`shutdown()`) and task tracking (`submit(Callable)`).
- **`ThreadPoolExecutor`**: The workhorse implementation that allows for fine-tuned configuration.