# Quizzes: Concurrent Design Patterns

Test your knowledge of Producer-Consumer, Reader-Writer, and Actor patterns.

## Quiz 1: Producer-Consumer

**Q1: What is the primary benefit of using a bounded buffer in the Producer-Consumer pattern?**
- A) It makes the data processing faster.
- B) It prevents the Producers from generating data faster than Consumers can process it, creating backpressure and preventing OutOfMemory errors.
- C) It automatically sorts the data.
- D) It eliminates the need for thread synchronization.
*Answer: B*

**Q2: What is the "Poison Pill" pattern used for?**
- A) To kill a thread that is consuming too much CPU.
- B) To safely and cleanly shut down a Producer-Consumer pipeline by placing a special token on the queue that tells the Consumer to terminate itself after finishing its current work.
- C) To deliberately crash the application during testing.
- D) To clear all items from a queue instantly.
*Answer: B*

## Quiz 2: Reader-Writer and Balking

**Q1: Under what circumstances does the Reader-Writer pattern perform better than a standard exclusive lock?**
- A) When there are many writers and very few readers.
- B) When reads and writes are perfectly balanced (50/50).
- C) When the shared data is read very frequently but modified rarely, allowing multiple readers to access the data concurrently without blocking each other.
- D) When the data is immutable.
*Answer: C*

**Q2: A thread calls a method to save a file, but the method immediately returns because the file hasn't been modified since the last save. Which pattern is this?**
- A) Producer-Consumer
- B) Actor Pattern
- C) Balking Pattern
- D) Double-Checked Locking
*Answer: C*

## Quiz 3: The Actor Pattern

**Q1: How does the Actor model prevent race conditions without using explicit locks?**
- A) Actors do not use memory; they only use disk storage.
- B) Actors encapsulate their own state. They communicate exclusively via asynchronous messages placed in a mailbox queue. A single thread processes the mailbox sequentially, ensuring the Actor's state is only ever modified by one thread at a time.
- C) Actors use the `synchronized` keyword on every method.
- D) Actors use `AtomicInteger` for all state variables.
*Answer: B*