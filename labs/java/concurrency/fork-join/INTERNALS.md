# Fork/Join Internals: Work-Stealing

## ⚙️ The ForkJoinPool Architecture
Unlike a standard `ThreadPoolExecutor` where all threads pull tasks from a single shared queue, the `ForkJoinPool` uses a **Work-Stealing** algorithm.

### 1. Per-Thread Deques
Each worker thread in the `ForkJoinPool` maintains its own private **Double-Ended Queue (Deque)** of tasks.
- When a thread forks a new subtask, it pushes it onto the **head** (top) of its own deque.
- When a thread is ready to execute a task, it pops a task from the **head** of its own deque.
- **LIFO (Last-In-First-Out)**: This ensures that a thread works on the most recently created subtasks first, which are usually the smallest and most likely to be in the CPU cache.

### 2. Work-Stealing
What happens if Thread A finishes all the tasks in its own deque, but Thread B still has 100 tasks waiting?
Instead of Thread A sitting idle:
1. Thread A picks a random victim (Thread B).
2. Thread A "steals" a task from the **tail** (bottom) of Thread B's deque.
3. **FIFO (First-In-First-Out)**: Stealing from the tail ensures that the thief takes the "oldest" task. In a recursive problem, the oldest tasks are the "largest" chunks of work, providing the thief with a significant amount of work to do.

## 🔬 RecursiveTask vs RecursiveAction
- **`RecursiveTask<V>`**: Used when the subtasks return a result. You call `fork()` to start them and `join()` to retrieve the result.
- **`RecursiveAction`**: Used for "fire and forget" tasks that don't return anything (like an in-place sort).

## 🛑 The Threshold
The most critical part of using Fork/Join is the **Sequential Threshold**.
If you split tasks too small (e.g., splitting until you have only 1 element), the overhead of creating task objects and managing the deques will be much larger than the actual work, making the parallel version slower than the serial version.
A good rule of thumb is that a task should perform at least 1,000 to 10,000 basic operations before being considered "small enough" to run sequentially.